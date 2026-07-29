package com.xunqi.gulimall.gateway;

import com.alibaba.cloud.sentinel.gateway.SentinelGatewayAutoConfiguration;
import com.alibaba.cloud.sentinel.gateway.scg.SentinelSCGAutoConfiguration;
import com.alibaba.cloud.sentinel.gateway.zuul.SentinelZuulAutoConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * 1、开启服务注册发现
 *  (配置nacos的注册中心地址)
 * 2、编写网关配置文件
 */

@EnableDiscoveryClient
@SpringBootApplication(exclude = {
        DataSourceAutoConfiguration.class,
        SentinelSCGAutoConfiguration.class,
        SentinelGatewayAutoConfiguration.class,
        SentinelZuulAutoConfiguration.class
})
public class GulimallGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(GulimallGatewayApplication.class, args);
    }

}
