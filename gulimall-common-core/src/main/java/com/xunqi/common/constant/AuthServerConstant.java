package com.xunqi.common.constant;

/**
 * 认证服务（auth-server）相关常量定义。
 * 包括短信验证码 Redis 缓存 key 前缀、登录用户 Session 属性名等。
 */
public class AuthServerConstant {

    /** 短信验证码在 Redis 中的缓存 key 前缀：sms:code:{手机号} */
    public static final String SMS_CODE_CACHE_PREFIX = "sms:code:";

    /** 登录成功后存入 HttpSession 的用户属性名（LoginUserInterceptor 据此读取并放入 ThreadLocal） */
    public static final String LOGIN_USER = "loginUser";

}
