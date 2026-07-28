package com.xunqi.gulimall.member.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.session.web.http.CookieSerializer;
import org.springframework.session.web.http.DefaultCookieSerializer;

/**
 * Spring Session 配置类
 * 1. 放大 Cookie 作用域到 gulimall.com，实现子域 session 共享
 * 2. 使用 JSON 序列化方式存储 Session 数据到 Redis
 */
@Configuration
public class GulimallSessionConfig {

    /**
     * 自定义 Cookie 序列化器
     * 将 Cookie 作用域放大到父域 gulimall.com，使所有子域共享同一个 Session
     */
    @Bean
    public CookieSerializer cookieSerializer() {

        DefaultCookieSerializer cookieSerializer = new DefaultCookieSerializer();
        cookieSerializer.setDomainName("gulimall.com");
        cookieSerializer.setCookieName("GULISESSION");

        return cookieSerializer;
    }


    /**
     * 自定义 Session 在 Redis 中的序列化方式
     * 使用 JSON 序列化，便于跨服务读取 Session 中的对象
     */
    @Bean
    public RedisSerializer<Object> springSessionDefaultRedisSerializer() {
        return new GenericJackson2JsonRedisSerializer();
    }

}
