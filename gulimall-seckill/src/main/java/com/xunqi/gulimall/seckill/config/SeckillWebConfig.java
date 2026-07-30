package com.xunqi.gulimall.seckill.config;

import com.xunqi.common.web.interceptor.LoginUserInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Collections;

/**
 * Web MVC 配置类：注册登录拦截器。
 * 秒杀属于用户敏感操作，必须先登录才能参与，因此通过拦截器统一校验登录状态。
 */
@Configuration
public class SeckillWebConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 注册登录拦截器，拦截所有请求（/**），并放行 /kill 之外的路径
        // 即只对真正的秒杀下单接口 /kill 做登录校验，上架、查询等接口不受影响
        registry.addInterceptor(new LoginUserInterceptor(null, Collections.singletonList("/kill")))
                .addPathPatterns("/**");
    }
}
