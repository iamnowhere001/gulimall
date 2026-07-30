package com.xunqi.gulimall.search;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

/**
 * 谷粒商城商品检索服务（search）启动类。
 *
 * 基于 Elasticsearch 提供商品全文检索与聚合筛选；不连接数据库（exclude DataSourceAutoConfiguration）。
 * 通过 Feign 调用商品服务获取属性信息；与商品服务共用 Spring Session（Redis）以便跨域共享登录态。
 */
@EnableRedisHttpSession
@EnableFeignClients
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
@EnableDiscoveryClient
public class GulimallSearchApplication {

  public static void main(String[] args) {
    SpringApplication.run(GulimallSearchApplication.class, args);
  }
}
