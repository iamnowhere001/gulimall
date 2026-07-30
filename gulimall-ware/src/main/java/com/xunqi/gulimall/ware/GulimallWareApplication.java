package com.xunqi.gulimall.ware;

import com.alibaba.cloud.seata.GlobalTransactionAutoConfiguration;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * 谷粒商城仓储服务（ware）启动类。
 *
 * 负责库存、仓库、采购（采购单/采购需求）与库存的锁定/释放。
 * 通过 @EnableRabbit 开启 RabbitMQ 监听（库存释放监听器）；
 * 通过 Feign 调用订单/商品/会员服务；
 * 排除 Seata 自动配置（本项目以 MQ + 最终一致性保证下单与库存的一致，而非强一致分布式事务）。
 */
@EnableRabbit
@EnableFeignClients
@EnableDiscoveryClient
@SpringBootApplication(exclude = GlobalTransactionAutoConfiguration.class)
public class GulimallWareApplication {

    public static void main(String[] args) {
        SpringApplication.run(GulimallWareApplication.class, args);
    }

}
