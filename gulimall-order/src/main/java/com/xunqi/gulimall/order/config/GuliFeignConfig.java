package com.xunqi.gulimall.order.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * Feign 请求拦截器配置（订单服务）。
 * 注册名为 requestInterceptor 的 Bean，在 Feign 发起远程调用前把当前请求的
 * X-Auth-Token 头（前后端分离场景的 Session ID）同步到新请求头，
 * 解决"订单服务→会员/购物车等服务的远程调用丢失登录态（Session）"的问题。
 */
@Configuration
public class GuliFeignConfig {

    @Bean("requestInterceptor")
    public RequestInterceptor requestInterceptor() {

        RequestInterceptor requestInterceptor = new RequestInterceptor() {
            @Override
            public void apply(RequestTemplate template) {
                //1、使用RequestContextHolder拿到刚进来的请求数据
                ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

                if (requestAttributes != null) {
                    //老请求
                    HttpServletRequest request = requestAttributes.getRequest();

                    if (request != null) {
                        //2、同步 X-Auth-Token 头（前后端分离场景的 Session ID 传递方式）
                        String authToken = request.getHeader("X-Auth-Token");
                        if (authToken != null && !authToken.isEmpty()) {
                            template.header("X-Auth-Token", authToken);
                        }
                        //兼容旧的 Cookie 方式（Thymeleaf 页面流程仍可能使用）
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
