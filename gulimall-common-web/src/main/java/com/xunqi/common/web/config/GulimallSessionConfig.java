package com.xunqi.common.web.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.session.data.redis.config.ConfigureRedisAction;
import org.springframework.session.web.http.HeaderHttpSessionIdResolver;
import org.springframework.session.web.http.HttpSessionIdResolver;

/**
 * Spring Session Redis 配置。
 * <p>
 * 前后端分离架构下使用 Header 方式传递 Session ID（请求头 X-Auth-Token），
 * 彻底摆脱 Cookie 域名限制，便于 localhost 开发与跨域部署。
 * 各业务模块通过引入 common-web 自动装配，无需重复定义。
 */
@Configuration
public class GulimallSessionConfig {

    /**
     * 使用 X-Auth-Token 请求/响应头传递 Session ID，替代 Cookie。
     * 前端登录后将 token 存入 localStorage，每次请求携带 X-Auth-Token 头。
     */
    @Bean
    public HttpSessionIdResolver httpSessionIdResolver() {
        return HeaderHttpSessionIdResolver.xAuthToken();
    }

    @Bean
    public RedisSerializer<Object> springSessionDefaultRedisSerializer() {
        return new GenericJackson2JsonRedisSerializer();
    }

    /**
     * 禁用 Spring Session 启动时自动执行 CONFIG SET notify-keyspace-events。
     * 该命令在 Redis 连接繁忙或无权限时容易超时（Lettuce 默认 60s），阻塞应用启动。
     * 如需键空间通知，请在 Redis 中手动执行：CONFIG SET notify-keyspace-events "Egx"
     */
    @Bean
    public ConfigureRedisAction configureRedisAction() {
        return ConfigureRedisAction.NO_OP;
    }
}
