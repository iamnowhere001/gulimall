package com.xunqi.gulimall.order.config;

import com.xunqi.common.web.interceptor.LoginUserInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Collections;

/**
 * Web MVC 配置（order 服务）。
 *
 * 注册登录拦截器：仅放行 mall 用户订单端点（如 /order/order/listWithItem）做登录校验，
 * 其余 /order/** 为后台（renren-fast）管理接口直接放行（避免后台调用被误判未登录）。
 * 同时映射 /static/** 静态资源。
 */
@Configuration
public class OrderWebConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 只有 mall 用户订单端点（如 listWithItem）需要登录校验；
        // 其余 /order/** 均为后台(renren-fast)管理接口，直接放行，否则后台调用会被误判为未登录。
        // status / payed/notify 不在白名单内，按"白名单外放行"规则同样不被拦截。
        registry.addInterceptor(new LoginUserInterceptor(null, Collections.singletonList("/order/order/listWithItem")))
                .addPathPatterns("/**")
                .excludePathPatterns("/static/**");
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/static/**")
                .addResourceLocations("classpath:/static/");
    }
}
