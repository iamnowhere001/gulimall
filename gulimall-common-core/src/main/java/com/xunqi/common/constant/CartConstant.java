package com.xunqi.common.constant;

/**
 * 购物车常量。
 */
public class CartConstant {

    /** 未登录（临时）用户在 Cookie 中的标识名，用于标识游客购物车 */
    public final static String TEMP_USER_COOKIE_NAME = "user-key";

    /** 临时用户 Cookie 有效期（30 天，单位：秒） */
    public final static int TEMP_USER_COOKIE_TIMEOUT = 60*60*24*30;

    /** 未登录（临时）用户在 Session 中的属性名，用于前后端分离场景下标识游客购物车 */
    public final static String TEMP_USER_SESSION_KEY = "cartTempUserKey";

    /** 购物车在 Redis 中的 key 前缀：gulimall:cart:{用户标识} */
    public final static String CART_PREFIX = "gulimall:cart:";

}
