package com.xunqi.gulimall.cart;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

/**
 * 谷粒商城购物车服务（cart）启动类。
 *
 * 购物车数据存放于 Redis（key = gulimall:cart:{用户标识}），通过 Spring Session 获取用户身份；
 * 借助 Feign 调用商品服务获取 SKU 信息，并用自定义线程池（ThreadPoolExecutor）异步并发查询以提升性能。
 */
@EnableRedisHttpSession
@EnableFeignClients
@EnableDiscoveryClient
@SpringBootApplication
public class GulimallCartApplication {

    /**
     * 购物车服务启动入口。
     * @param args 命令行参数
     */
    public static void main(String[] args) {
        SpringApplication.run(GulimallCartApplication.class, args);
    }

}
