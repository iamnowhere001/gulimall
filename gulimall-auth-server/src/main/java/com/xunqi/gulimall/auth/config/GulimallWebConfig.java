package com.xunqi.gulimall.auth.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web MVC 配置类
 * 配置 URL 到视图名的直接映射，无需编写 Controller
 */
@Configuration
public class GulimallWebConfig implements WebMvcConfigurer {

    /**
     * 视图映射：将指定 URL 直接映射到视图名，跳过 Controller 层
     */
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {

        registry.addViewController("/reg.html").setViewName("reg");
    }
}
