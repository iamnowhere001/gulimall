package com.xunqi.gulimall.seckill.web;

import com.xunqi.common.utils.R;
import com.xunqi.common.vo.MemberResponseVo;
import com.xunqi.common.web.interceptor.LoginUserInterceptor;
import com.xunqi.gulimall.seckill.service.SeckillService;
import com.xunqi.gulimall.seckill.to.SeckillSkuRedisTo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
public class SeckillController {

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(SeckillController.class);

    @Autowired
    private SeckillService seckillService;

    /**
     * 【测试用】模拟登录用户并秒杀，绕过session登录校验。仅限开发测试。
     * @param memberId 模拟用户ID
     * @param killId 场次-skuId
     * @param key 随机码
     * @param num 数量
     * @return R.ok(orderSn) 或 错误
     */
    @GetMapping(value = "/test/kill")
    @ResponseBody
    public R testSeckill(@RequestParam("memberId") Long memberId,
                         @RequestParam("killId") String killId,
                         @RequestParam("key") String key,
                         @RequestParam("num") Integer num) {
        try {
            //模拟登录用户
            MemberResponseVo mockUser = new MemberResponseVo();
            mockUser.setId(memberId);
            mockUser.setUsername("testuser_" + memberId);
            mockUser.setNickname("测试用户" + memberId);
            LoginUserInterceptor.loginUser.set(mockUser);
            String orderSn = seckillService.kill(killId, key, num);
            if (orderSn != null) {
                return R.ok().put("orderSn", orderSn).put("msg", "秒杀成功");
            } else {
                return R.error("秒杀失败：库存不足、非法参数或已参与过秒杀");
            }
        } catch (Exception e) {
            log.error("测试秒杀异常", e);
            return R.error("秒杀异常: " + e.getMessage());
        } finally {
            LoginUserInterceptor.loginUser.remove();
        }
    }

    /**
     * 手动触发上架未来3天的秒杀商品（调试用，避免等待定时任务）
     * @return
     */
    @GetMapping(value = "/uploadSeckillSkusLatest3Day")
    @ResponseBody
    public R uploadSeckillSkusLatest3Day() {
        try {
            seckillService.uploadSeckillSkuLatest3Days();
            return R.ok("秒杀商品上架成功");
        } catch (Exception e) {
            log.error("手动上架秒杀商品失败", e);
            return R.error("上架失败: " + e.getMessage());
        }
    }

    /**
     * 当前时间可以参与秒杀的商品信息
     * @return
     */
    @GetMapping(value = "/getCurrentSeckillSkus")
    @ResponseBody
    public R getCurrentSeckillSkus() {

        //获取到当前可以参加秒杀商品的信息
        List<SeckillSkuRedisTo> vos = seckillService.getCurrentSeckillSkus();

        return R.ok().setData(vos);
    }


    /**
     * 根据skuId查询商品是否参加秒杀活动
     * @param skuId
     * @return
     */
    @GetMapping(value = "/sku/seckill/{skuId}")
    @ResponseBody
    public R getSkuSeckilInfo(@PathVariable("skuId") Long skuId) {

        SeckillSkuRedisTo to = seckillService.getSkuSeckilInfo(skuId);

        return R.ok().setData(to);
    }


    /**
     * 商品进行秒杀(秒杀开始)
     * @param killId
     * @param key
     * @param num
     * @return
     */
    @GetMapping(value = "/kill")
    public String seckill(@RequestParam("killId") String killId,
                          @RequestParam("key") String key,
                          @RequestParam("num") Integer num,
                          Model model) {

        String orderSn = null;
        try {
            //1、判断是否登录
            orderSn = seckillService.kill(killId,key,num);
            model.addAttribute("orderSn",orderSn);
        } catch (Exception e) {
            log.error("秒杀异常", e);
        }
        return "success";
    }

}
