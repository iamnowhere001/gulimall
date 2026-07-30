package com.xunqi.gulimall.auth;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

/**
 * 谷粒商城认证服务（auth-server）启动类。
 *
 * 职责：统一处理用户登录/注册、社交登录（微博/微信）、短信验证码等认证流程，
 * 并作为 SSO 登录入口；登录成功后把用户信息存入 HttpSession，
 * 借助 Spring Session + Redis（@EnableRedisHttpSession）实现 Session 跨服务、跨子域共享。
 * 通过 Feign 远程调用会员服务（gulimall-member）与第三方服务（gulimall-third-party）。
 *
 * 核心原理：
 *  - @EnableRedisHttpSession 导入 RedisHttpSessionConfiguration，向容器注册
 *    RedisOperationsSessionRepository，由它负责 Session 在 Redis 中的增删改查。
 */
@EnableRedisHttpSession     //整合Redis作为session存储
@EnableFeignClients
@EnableDiscoveryClient
@SpringBootApplication
public class GulimallAuthServerApplication {

    /**
     * 认证服务启动入口。
     * @param args 命令行参数
     */
    public static void main(String[] args) {
        SpringApplication.run(GulimallAuthServerApplication.class, args);
    }

}
