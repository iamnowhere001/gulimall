package com.xunqi.common.web.config;

import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

/**
 * Sentinel 自动装配配置类。
 * <p>
 * 通过 @PostConstruct 触发 GulimallSentinelConfig 的静态初始化，
 * 确保 UrlBlockHandler 在容器启动时注册。
 */
@Configuration
public class GulimallSentinelAutoConfiguration {

    @PostConstruct
    public void init() {
        GulimallSentinelConfig.init();
    }
}
