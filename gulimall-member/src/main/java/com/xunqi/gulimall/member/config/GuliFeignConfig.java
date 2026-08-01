package com.xunqi.gulimall.member.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * Feign 配置类
 * 配置 Feign 请求拦截器，在远程调用时同步携带当前请求的 X-Auth-Token 头（前后端分离场景的 Session ID）
 * 用于解决 Feign 调用时 Session 丢失问题
 */
@Configuration
public class GuliFeignConfig {

    @Bean("requestInterceptor")
    public RequestInterceptor requestInterceptor() {

        RequestInterceptor requestInterceptor = new RequestInterceptor() {
            @Override
            public void apply(RequestTemplate template) {
                // 1、使用 RequestContextHolder 获取当前请求上下文
                ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

                if (requestAttributes != null) {
                    HttpServletRequest request = requestAttributes.getRequest();

                    if (request != null) {
                        // 2、同步 X-Auth-Token 头（前后端分离场景的 Session ID 传递方式）
                        String authToken = request.getHeader("X-Auth-Token");
                        if (authToken != null && !authToken.isEmpty()) {
                            template.header("X-Auth-Token", authToken);
                        }
                        // 兼容旧的 Cookie 方式（Thymeleaf 页面流程仍可能使用）
                        String cookie = request.getHeader("Cookie");
                        if (cookie != null && !cookie.isEmpty()) {
                            template.header("Cookie", cookie);
                        }
                    }
                }
            }
        };
        return requestInterceptor;
    }

}
