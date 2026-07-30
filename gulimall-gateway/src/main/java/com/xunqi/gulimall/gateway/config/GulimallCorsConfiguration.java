package com.xunqi.gulimall.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

/**
 * 网关全局跨域（CORS）配置。
 *
 * 浏览器同源策略会拦截前端（域名/端口不同）对网关的跨域请求，
 * 因此在网关层统一配置 CORS 过滤器，对所有路径（/**）放行跨域，
 * 使前端可无障碍访问各微服务。使用响应式的 {@link CorsWebFilter} 适配 WebFlux 网关环境。
 */
@Configuration
public class GulimallCorsConfiguration {

    /**
     * 注册全局 CORS 过滤器 Bean。
     * 基于 URL 匹配所有请求路径，定义允许的跨域行为并返回 {@link CorsWebFilter}。
     * @return 作用于全路径的跨域过滤器
     */
    @Bean
    public CorsWebFilter corsWebFilter() {
        // 基于 URL 的跨域配置源，可对不同路径设置不同规则
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();

        CorsConfiguration corsConfiguration = new CorsConfiguration();

        // 允许所有请求头（如 Authorization、Content-Type 等）
        corsConfiguration.addAllowedHeader("*");
        // 允许所有 HTTP 方法（GET、POST、PUT、DELETE 等）
        corsConfiguration.addAllowedMethod("*");
        // 允许所有来源域名跨域访问
        corsConfiguration.addAllowedOrigin("*");
        // 允许跨域请求携带凭证（Cookie、Authorization），需配合前端 withCredentials
        corsConfiguration.setAllowCredentials(true);

        // 将上述规则注册到所有路径
        source.registerCorsConfiguration("/**",corsConfiguration);
        return new CorsWebFilter(source);
    }
}
