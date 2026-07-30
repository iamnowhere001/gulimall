package com.xunqi.gulimall.member.config;

import com.xunqi.common.web.interceptor.LoginUserInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Collections;

/**
 * Web MVC 配置类
 * 注册通用登录拦截器，拦截所有请求校验登录状态
 */
@Configuration
public class MemberWebConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new LoginUserInterceptor(Collections.singletonList("/member/**"), null))
                .addPathPatterns("/**");
    }

}
