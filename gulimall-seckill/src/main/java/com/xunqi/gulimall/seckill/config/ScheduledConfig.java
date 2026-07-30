package com.xunqi.gulimall.seckill.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 定时任务与异步配置类
 * @EnableScheduling 开启 Spring 的定时任务支持，配合方法上的 @Scheduled 注解使用。
 * @EnableAsync      开启 Spring 的异步支持，配合方法上的 @Async 注解使用，
 *                   使定时任务或耗时逻辑可在独立线程池中异步执行，不阻塞主流程。
 */
@EnableAsync
@EnableScheduling
@Configuration
public class ScheduledConfig {

}
