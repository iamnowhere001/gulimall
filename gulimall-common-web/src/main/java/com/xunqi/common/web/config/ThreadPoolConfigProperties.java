package com.xunqi.common.web.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 线程池配置属性。
 * <p>
 * 对应配置前缀 gulimall.thread，在 application.yml 中配置核心线程数、最大线程数、存活时间。
 */
@Data
@ConfigurationProperties(prefix = "gulimall.thread")
public class ThreadPoolConfigProperties {

    private Integer coreSize = 10;

    private Integer maxSize = 200;

    private Integer keepAliveTime = 10;
}
