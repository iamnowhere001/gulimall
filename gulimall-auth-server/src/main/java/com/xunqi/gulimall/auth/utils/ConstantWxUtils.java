package com.xunqi.gulimall.auth.utils;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * 微信开放平台配置常量工具类。
 *
 * 从配置文件（wx.open.*）读取 appId / appSecret / redirectUrl，
 * 并在 Bean 初始化完成（afterPropertiesSet）后将其赋给静态变量，
 * 这样在微信登录流程的任意位置都能直接以
 * ConstantWxUtils.WX_OPEN_APP_ID 等方式引用，无需注入 Bean。
 */
@Component
public class ConstantWxUtils implements InitializingBean {

    /** 配置文件中的微信 appId */
    @Value("${wx.open.app_id}")
    private String appId;

    /** 配置文件中的微信 appSecret */
    @Value("${wx.open.app_secret}")
    private String appSecret;

    /** 配置文件中的微信授权回调地址 */
    @Value("${wx.open.redirect_url}")
    private String redirectUrl;

    /** 微信开放平台 AppID（静态暴露，供全局引用） */
    public static String WX_OPEN_APP_ID;
    /** 微信开放平台 AppSecret（静态暴露，供全局引用） */
    public static String WX_OPEN_APP_SECRET;
    /** 微信授权回调地址（静态暴露，供全局引用） */
    public static String WX_OPEN_REDIRECT_URL;

    /**
     * Bean 初始化后，把配置值同步到静态变量。
     * @throws Exception 初始化异常
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        WX_OPEN_APP_ID = appId;
        WX_OPEN_APP_SECRET = appSecret;
        WX_OPEN_REDIRECT_URL = redirectUrl;
    }
}
