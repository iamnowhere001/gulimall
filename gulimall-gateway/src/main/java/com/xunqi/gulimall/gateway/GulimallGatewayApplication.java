package com.xunqi.gulimall.gateway;

import com.alibaba.cloud.sentinel.gateway.SentinelGatewayAutoConfiguration;
import com.alibaba.cloud.sentinel.gateway.scg.SentinelSCGAutoConfiguration;
import com.alibaba.cloud.sentinel.gateway.zuul.SentinelZuulAutoConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * 谷粒商城 API 网关服务启动类（基于 Spring Cloud Gateway）。
 *
 * 职责与设计要点：
 * 1、开启服务注册发现（@EnableDiscoveryClient），将网关注册到 Nacos，
 *    从而能根据服务名动态路由到各微服务实例。
 * 2、网关本身不连接数据库，排除 DataSourceAutoConfiguration 以免无数据源启动报错。
 * 3、项目已移除 Sentinel 网关限流能力，故显式排除 Sentinel 相关的三个自动配置类
 *    （SCG / 通用 Gateway / Zuul 适配器），避免加载无关注释 Bean。
 * 4、具体的路由断言、过滤器、跨域等规则在 application.yml 中配置
 *    （如 /api/** 按服务名转发到对应微服务）。
 */

@EnableDiscoveryClient
@SpringBootApplication(exclude = {
        DataSourceAutoConfiguration.class,
        SentinelSCGAutoConfiguration.class,
        SentinelGatewayAutoConfiguration.class,
        SentinelZuulAutoConfiguration.class
})
public class GulimallGatewayApplication {

    /**
     * 网关服务启动入口。
     * @param args 命令行参数
     */
    public static void main(String[] args) {
        SpringApplication.run(GulimallGatewayApplication.class, args);
    }

}
