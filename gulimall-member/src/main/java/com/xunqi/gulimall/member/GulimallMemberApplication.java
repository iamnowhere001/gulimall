package com.xunqi.gulimall.member;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

/**
 * 谷粒商城会员服务（member）启动类。
 *
 * 职责：管理会员信息、登录注册、社交登录、收货地址、会员等级、积分/成长值变动、收藏等。
 * 要点：
 *  - 使用 Spring Session + Redis（@EnableRedisHttpSession）共享 Session，登录态跨服务可用；
 *  - 通过 Feign 远程调用优惠券服务（gulimall-coupon）与订单服务（gulimall-order）；
 *  - 配置 Feign 请求拦截器（GuliFeignConfig）在远程调用时透传 Cookie，避免 Session 丢失。
 */
@EnableRedisHttpSession
@EnableFeignClients(basePackages = "com.xunqi.gulimall.member.feign")
@EnableDiscoveryClient
@SpringBootApplication
public class GulimallMemberApplication {

    public static void main(String[] args) {
        SpringApplication.run(GulimallMemberApplication.class, args);
    }

}
