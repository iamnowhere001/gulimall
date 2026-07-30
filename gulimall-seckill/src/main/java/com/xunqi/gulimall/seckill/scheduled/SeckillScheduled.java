package com.xunqi.gulimall.seckill.scheduled;

import com.xunqi.gulimall.seckill.service.SeckillService;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * 秒杀商品定时上架
 *  每天晚上3点，上架最近三天需要三天秒杀的商品
 *  当天00:00:00 - 23:59:59
 *  明天00:00:00 - 23:59:59
 *  后天00:00:00 - 23:59:59
 */

@Slf4j
@Service
public class SeckillScheduled {

    @Autowired
    private SeckillService seckillService;

    @Autowired
    private RedissonClient redissonClient;

    //秒杀商品上架功能的分布式锁 key，保证集群环境下同一时刻只有一个节点执行上架
    private final String upload_lock = "seckill:upload:lock";

    //TODO 保证幂等性问题（上架前已做 hasKey 判断，重复执行不会覆盖已有数据）
    //cron = "0 0 1/1 * * ?" 表示每小时的第 0 分 0 秒执行一次（即每个整点触发一次上架扫描）
    //如需调整为每天凌晨 3 点执行，可改为 "0 0 3 * * ?"
    @Scheduled(cron = "0 0 1/1 * * ? ")
    public void uploadSeckillSkuLatest3Days() {
        //1、重复上架无需处理
        log.info("上架秒杀的商品...");

        //分布式锁
        RLock lock = redissonClient.getLock(upload_lock);
        try {
            //加锁
            lock.lock(10, TimeUnit.SECONDS);
            seckillService.uploadSeckillSkuLatest3Days();
        } catch (Exception e) {
            log.error("上架秒杀商品异常", e);
        } finally {
            lock.unlock();
        }
    }
}
