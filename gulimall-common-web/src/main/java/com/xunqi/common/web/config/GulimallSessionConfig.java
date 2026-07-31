package com.xunqi.common.web.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.session.data.redis.config.ConfigureRedisAction;
import org.springframework.session.web.http.CookieSerializer;
import org.springframework.session.web.http.DefaultCookieSerializer;

/**
 * Spring Session Redis 配置。
 * <p>
 * 统一配置 Session Cookie 序列化与 Redis 序列化，
 * 各业务模块通过引入 common-web 自动装配，无需重复定义。
 */
@Configuration
public class GulimallSessionConfig {

    @Bean
    public CookieSerializer cookieSerializer() {
        DefaultCookieSerializer cookieSerializer = new DefaultCookieSerializer();
        // 放大作用域至父域，实现子域间 Session 共享
        cookieSerializer.setDomainName("gulimall.com");
        cookieSerializer.setCookieName("GULISESSION");
        return cookieSerializer;
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
