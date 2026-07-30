package com.xunqi.gulimall.seckill.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.xunqi.common.to.mq.SeckillOrderTo;
import com.xunqi.common.utils.R;
import com.xunqi.common.vo.MemberResponseVo;
import com.xunqi.gulimall.seckill.feign.CouponFeignService;
import com.xunqi.gulimall.seckill.feign.ProductFeignService;
import com.xunqi.common.web.interceptor.LoginUserInterceptor;
import com.xunqi.gulimall.seckill.service.SeckillService;
import com.xunqi.gulimall.seckill.to.SeckillSkuRedisTo;
import com.xunqi.gulimall.seckill.vo.SeckillSessionWithSkusVo;
import com.xunqi.gulimall.seckill.vo.SkuInfoVo;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RSemaphore;
import org.redisson.api.RedissonClient;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * 秒杀核心服务实现类。
 *
 * 整体设计：将秒杀的“读”与“写”都前置到 Redis，减轻数据库压力。
 * 上架时把秒杀活动与商品信息存入 Redis，并提供给前端展示；秒杀时直接基于 Redis 做
 * 校验、扣减库存（Redisson 信号量）与防超卖（用户占位 SETNX），下单成功后发送 MQ 异步落库。
 *
 * Redis 中使用的三类数据结构：
 *  1) 场次索引：  key = seckill:sessions:{startTime}_{endTime}，value 为 List，
 *                元素是 "{场次id}-{skuId}"，用于根据“当前时间”快速定位正在进行的场次及该场次的商品。
 *  2) 商品详情：  key = seckill:skus（Hash），field = "{场次id}-{skuId}"，
 *                value 为 SeckillSkuRedisTo 的 JSON，保存单个商品完整的秒杀信息（含随机码、价格、时间等）。
 *  3) 库存信号量：key = seckill:stock:{随机码}，使用 Redisson 信号量，permits = 秒杀总量，
 *                用于分布式限流与原子扣减库存。
 */
@Slf4j
@Service
public class SeckillServiceImpl implements SeckillService {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private CouponFeignService couponFeignService;

    @Autowired
    private ProductFeignService productFeignService;

    @Autowired
    private RedissonClient redissonClient;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    /** 秒杀场次在 Redis 中的 key 前缀：seckill:sessions:{startTime}_{endTime} */
    private final String SESSION__CACHE_PREFIX = "seckill:sessions:";

    /** 秒杀商品详情在 Redis 中的 Hash key 前缀：seckill:skus */
    private final String SECKILL_CHARE_PREFIX = "seckill:skus";

    /** 秒杀库存信号量在 Redis 中的 key 前缀：seckill:stock:{随机码} */
    private final String SKU_STOCK_SEMAPHORE = "seckill:stock:";    //+商品随机码

    @Override
    public void uploadSeckillSkuLatest3Days() {

        //1、扫描最近三天的商品需要参加秒杀的活动
        R lates3DaySession = couponFeignService.getLates3DaySession();
        if (lates3DaySession.getCode() == 0) {
            //上架商品
            List<SeckillSessionWithSkusVo> sessionData = lates3DaySession.getData("data", new TypeReference<List<SeckillSessionWithSkusVo>>() {
            });
            if (sessionData != null && !sessionData.isEmpty()) {
                //缓存到Redis
                //1、缓存活动信息
                saveSessionInfos(sessionData);

                //2、缓存活动的关联商品信息
                saveSessionSkuInfo(sessionData);
            } else {
                log.warn("未查询到未来3天需要上架的秒杀活动数据");
            }
        }

    }

    /**
     * 缓存秒杀活动信息
     * @param sessions
     */
    private void saveSessionInfos(List<SeckillSessionWithSkusVo> sessions) {

        sessions.stream().forEach(session -> {

            if (session.getStartTime() == null || session.getEndTime() == null) {
                log.warn("秒杀场次时间为空，跳过: sessionId={}", session.getId());
                return;
            }
            if (session.getRelationSkus() == null || session.getRelationSkus().isEmpty()) {
                log.warn("秒杀场次无关联商品，跳过: sessionId={}", session.getId());
                return;
            }

            //获取当前活动的开始和结束时间的时间戳
            long startTime = session.getStartTime().getTime();
            long endTime = session.getEndTime().getTime();

            //存入到Redis中的key
            String key = SESSION__CACHE_PREFIX + startTime + "_" + endTime;

            //判断Redis中是否有该信息，如果没有才进行添加
            Boolean hasKey = redisTemplate.hasKey(key);
            //缓存活动信息
            if (hasKey == null || !hasKey) {
                //获取到活动中所有商品的skuId
                List<String> skuIds = session.getRelationSkus().stream()
                        .filter(item -> item.getPromotionSessionId() != null && item.getSkuId() != null)
                        .map(item -> item.getPromotionSessionId() + "-" + item.getSkuId().toString())
                        .collect(Collectors.toList());
                if (!skuIds.isEmpty()) {
                    redisTemplate.opsForList().leftPushAll(key, skuIds);
                }
            }
        });

    }

    /**
     * 缓存秒杀活动所关联的商品信息
     * @param sessions
     */
    private void saveSessionSkuInfo(List<SeckillSessionWithSkusVo> sessions) {

        sessions.stream().forEach(session -> {
            if (session.getRelationSkus() == null || session.getRelationSkus().isEmpty()
                    || session.getStartTime() == null || session.getEndTime() == null) {
                return;
            }
            //准备hash操作，绑定hash
            BoundHashOperations<String, Object, Object> operations = redisTemplate.boundHashOps(SECKILL_CHARE_PREFIX);
            session.getRelationSkus().stream()
                    .filter(vo -> vo != null && vo.getPromotionSessionId() != null
                            && vo.getSkuId() != null && vo.getSeckillCount() != null)
                    .forEach(seckillSkuVo -> {
                //生成随机码
                String token = UUID.randomUUID().toString().replace("-", "");
                String redisKey = seckillSkuVo.getPromotionSessionId().toString() + "-" + seckillSkuVo.getSkuId().toString();
                if (!operations.hasKey(redisKey)) {

                    //缓存我们商品信息
                    SeckillSkuRedisTo redisTo = new SeckillSkuRedisTo();
                    Long skuId = seckillSkuVo.getSkuId();
                    //1、先查询sku的基本信息，调用远程服务
                    R info = productFeignService.getSkuInfo(skuId);
                    if (info != null && info.getCode() == 0) {
                        SkuInfoVo skuInfo = info.getData("skuInfo",new TypeReference<SkuInfoVo>(){});
                        redisTo.setSkuInfo(skuInfo);
                    }

                    //2、sku的秒杀信息
                    BeanUtils.copyProperties(seckillSkuVo,redisTo);

                    //3、设置当前商品的秒杀时间信息
                    redisTo.setStartTime(session.getStartTime().getTime());
                    redisTo.setEndTime(session.getEndTime().getTime());

                    //4、设置商品的随机码（防止恶意攻击）
                    redisTo.setRandomCode(token);

                    //序列化json格式存入Redis中
                    String seckillValue = JSON.toJSONString(redisTo);
                    operations.put(redisKey, seckillValue);

                    //如果当前这个场次的商品库存信息已经上架就不需要上架
                    //5、使用库存作为分布式Redisson信号量（限流）
                    // 使用库存作为分布式信号量
                    RSemaphore semaphore = redissonClient.getSemaphore(SKU_STOCK_SEMAPHORE + token);
                    // 商品可以秒杀的数量作为信号量，至少设置1个（如果数据库设置为0，也至少允许1个防止异常）
                    int permits = seckillSkuVo.getSeckillCount() != null && seckillSkuVo.getSeckillCount() > 0
                            ? seckillSkuVo.getSeckillCount() : 1;
                    semaphore.trySetPermits(permits);
                }
            });
        });
    }

    /**
     * 获取到当前可以参加秒杀商品的信息
     * @return
     */
    @Override
    public List<SeckillSkuRedisTo> getCurrentSeckillSkus() {

        try {
            //1、确定当前属于哪个秒杀场次
            long currentTime = System.currentTimeMillis();

            //从Redis中查询到所有key以seckill:sessions开头的所有数据
            Set<String> keys = redisTemplate.keys(SESSION__CACHE_PREFIX + "*");
            if (keys == null || keys.isEmpty()) {
                return null;
            }
            for (String key : keys) {
                //seckill:sessions:1594396764000_1594453242000
                String replace = key.replace(SESSION__CACHE_PREFIX, "");
                String[] s = replace.split("_");
                if (s.length < 2) {
                    continue;
                }
                //获取存入Redis商品的开始时间
                long startTime = Long.parseLong(s[0]);
                //获取存入Redis商品的结束时间
                long endTime = Long.parseLong(s[1]);

                //判断是否是当前秒杀场次
                if (currentTime >= startTime && currentTime <= endTime) {
                    //2、获取这个秒杀场次需要的所有商品信息
                    List<String> range = redisTemplate.opsForList().range(key, -100, 100);
                    BoundHashOperations<String, String, String> hasOps = redisTemplate.boundHashOps(SECKILL_CHARE_PREFIX);
                    if (range == null || range.isEmpty()) {
                        break;
                    }
                    List<String> listValue = hasOps.multiGet(range);
                    if (listValue != null && !listValue.isEmpty()) {

                        List<SeckillSkuRedisTo> collect = listValue.stream()
                                .filter(StringUtils::hasText)
                                .map(item -> {
                                    SeckillSkuRedisTo redisTo = JSON.parseObject(item, SeckillSkuRedisTo.class);
                                    return redisTo;
                                }).collect(Collectors.toList());
                        return collect;
                    }
                    break;
                }
            }
        } catch (Exception e) {
            log.error("获取当前秒杀商品异常", e);
        }

        return null;
    }

    /**
     * 根据skuId查询商品是否参加秒杀活动
     * @param skuId
     * @return
     */
    @Override
    public SeckillSkuRedisTo getSkuSeckilInfo(Long skuId) {

        //1、找到所有需要秒杀的商品的key信息---seckill:skus
        BoundHashOperations<String, String, String> hashOps = redisTemplate.boundHashOps(SECKILL_CHARE_PREFIX);

        //拿到所有的key
        Set<String> keys = hashOps.keys();
        if (keys != null && keys.size() > 0) {
            //正则表达式进行匹配，支持多位数字的sessionId
            String reg = "\\d+-" + skuId;
            for (String key : keys) {
                //如果匹配上了
                if (Pattern.matches(reg, key)) {
                    //从Redis中取出数据来
                    String redisValue = hashOps.get(key);
                    //进行序列化
                    SeckillSkuRedisTo redisTo = JSON.parseObject(redisValue, SeckillSkuRedisTo.class);

                    //随机码
                    Long currentTime = System.currentTimeMillis();
                    Long startTime = redisTo.getStartTime();
                    Long endTime = redisTo.getEndTime();
                    //如果当前时间大于等于秒杀活动开始时间并且要小于活动结束时间
                    if (currentTime >= startTime && currentTime <= endTime) {
                        return redisTo;
                    }
                    redisTo.setRandomCode(null);
                    return redisTo;
                }
            }
        }
        return null;
    }

    /**
     * 当前商品进行秒杀（秒杀开始）
     * @param killId
     * @param key
     * @param num
     * @return
     */
    @Override
    public String kill(String killId, String key, Integer num) throws InterruptedException {

        long s1 = System.currentTimeMillis();
        //获取当前登录用户的信息（由 LoginUserInterceptor 在请求前放入 ThreadLocal）
        MemberResponseVo user = LoginUserInterceptor.loginUser.get();

        //1、从 Redis 中获取当前秒杀商品的详细信息
        BoundHashOperations<String, String, String> hashOps = redisTemplate.boundHashOps(SECKILL_CHARE_PREFIX);
        String skuInfoValue = hashOps.get(killId);
        if (StringUtils.isEmpty(skuInfoValue)) {
            // 商品不存在或未上架，直接返回失败
            return null;
        }
        //(合法性效验) 反序列化为商品秒杀信息对象
        SeckillSkuRedisTo redisTo = JSON.parseObject(skuInfoValue, SeckillSkuRedisTo.class);
        Long startTime = redisTo.getStartTime();
        Long endTime = redisTo.getEndTime();
        long currentTime = System.currentTimeMillis();
        //判断当前这个秒杀请求是否在活动时间区间内(效验时间的合法性)
        if (currentTime >= startTime && currentTime <= endTime) {

            //2、效验随机码和商品 id（随机码防止他人通过枚举接口恶意刷单）
            String randomCode = redisTo.getRandomCode();
            String skuId = redisTo.getPromotionSessionId() + "-" +redisTo.getSkuId();
            if (randomCode.equals(key) && killId.equals(skuId)) {
                //3、验证购买数量是否合理、是否在限购范围内，以及库存是否充足
                Integer seckillLimit = redisTo.getSeckillLimit();

                //获取信号量当前剩余库存值
                String seckillCount = redisTemplate.opsForValue().get(SKU_STOCK_SEMAPHORE + randomCode);
                if (StringUtils.isEmpty(seckillCount)) {
                    return null;
                }
                Integer count = Integer.valueOf(seckillCount);
                //判断信号量是否大于0,并且买的数量不能超过库存，也不能超过限购数量
                if (count >= num && num <= seckillLimit) {
                    //4、验证这个人是否已经买过了（幂等性处理）：以 userId-sessionId-skuId 做占位
                    //使用 SETNX 原子性写入，已存在则说明该用户已秒杀过，直接失败
                    String redisKey = user.getId() + "-" + skuId;
                    //设置自动过期时间 = 活动结束时间 - 当前时间，活动结束后占位自动失效
                    Long ttl = endTime - currentTime;
                    Boolean aBoolean = redisTemplate.opsForValue().setIfAbsent(redisKey, num.toString(), ttl, TimeUnit.MILLISECONDS);
                    if (aBoolean) {
                        //占位成功说明该用户从未买过，尝试获取分布式信号量（库存 -num）
                        RSemaphore semaphore = redissonClient.getSemaphore(SKU_STOCK_SEMAPHORE + randomCode);
                        //TODO 秒杀成功，快速下单
                        boolean semaphoreCount = semaphore.tryAcquire(num, 100, TimeUnit.MILLISECONDS);
                        //保证 Redis 中还有商品库存
                        if (semaphoreCount) {
                            //创建订单号和订单信息发送给 MQ，由订单服务异步创建订单、扣减数据库库存
                            // 秒杀成功 快速下单 发送消息到 MQ 整个操作时间在 10ms 左右
                            String timeId = System.currentTimeMillis() + "" + UUID.randomUUID().toString().replace("-", "").substring(0, 8);
                            SeckillOrderTo orderTo = new SeckillOrderTo();
                            orderTo.setOrderSn(timeId);
                            orderTo.setMemberId(user.getId());
                            orderTo.setNum(num);
                            orderTo.setPromotionSessionId(redisTo.getPromotionSessionId());
                            orderTo.setSkuId(redisTo.getSkuId());
                            orderTo.setSeckillPrice(redisTo.getSeckillPrice());
                            // 路由到 order-event-exchange，routingKey=order.seckill.order
                            rabbitTemplate.convertAndSend("order-event-exchange","order.seckill.order",orderTo);
                            long s2 = System.currentTimeMillis();
                            log.info("耗时..." + (s2 - s1));
                            return timeId;
                        }
                    }
                }
            }
        }
        long s3 = System.currentTimeMillis();
        log.info("耗时..." + (s3 - s1));
        return null;
    }

}
