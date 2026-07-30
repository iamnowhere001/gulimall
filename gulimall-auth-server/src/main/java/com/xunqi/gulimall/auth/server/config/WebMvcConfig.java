package com.xunqi.gulimall.auth.server.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web MVC 配置（auth-server 子模块）。
 * 配置静态资源映射：将 /static/** 的请求指向 classpath 下的 static 目录，
 * 便于直接访问页面所需的 JS/CSS/图片等静态资源。
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/static/**")
                .addResourceLocations("classpath:/static/");
    }
}
