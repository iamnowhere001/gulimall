package com.xunqi.gulimall.coupon;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * 谷粒商城优惠券/营销服务（coupon）启动类。
 *
 * 职责：管理优惠券、满减/折扣、会员价、积分、首页广告/专题、以及秒杀活动（场次与商品关联）。
 * 商品上架时由 product 服务通过 MQ/Feign 调用本服务保存 SKU 的优惠与积分规则；
 * 秒杀服务（gulimall-seckill）上架时拉取本服务的秒杀场次与关联商品。
 */
@EnableDiscoveryClient
@SpringBootApplication
public class GulimallCouponApplication {

    /**
     * 优惠券服务启动入口。
     * @param args 命令行参数
     */
    public static void main(String[] args) {
        SpringApplication.run(GulimallCouponApplication.class, args);
    }

}
