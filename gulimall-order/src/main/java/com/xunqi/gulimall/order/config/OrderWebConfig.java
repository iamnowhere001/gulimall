package com.xunqi.gulimall.order.config;

import com.xunqi.common.web.interceptor.LoginUserInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Arrays;
import java.util.List;

/**
 * Web MVC 配置（order 服务）。
 *
 * 注册登录拦截器：仅对前台用户订单端点做登录校验，
 * 其余 /order/** 为后台（renren-fast）管理接口直接放行（避免后台调用被误判未登录）。
 * 同时映射 /static/** 静态资源。
 */
@Configuration
public class OrderWebConfig implements WebMvcConfigurer {

    /** 需要登录校验的前台用户订单端点 */
    private static final List<String> PORTAL_INCLUDE_PATHS = Arrays.asList(
            "/order/order/listWithItem",
            "/order/confirm",
            "/order/submit",
            "/order/myOrders"
    );

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 前台用户订单端点需要登录校验；
        // 其余 /order/** 均为后台(renren-fast)管理接口，直接放行，否则后台调用会被误判为未登录。
        registry.addInterceptor(new LoginUserInterceptor(null, PORTAL_INCLUDE_PATHS))
                .addPathPatterns("/**")
                .excludePathPatterns("/static/**");
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/static/**")
                .addResourceLocations("classpath:/static/");
    }
}
