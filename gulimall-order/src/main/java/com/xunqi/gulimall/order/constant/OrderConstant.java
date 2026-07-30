package com.xunqi.gulimall.order.constant;

/**
 * 订单服务常量。
 */
public class OrderConstant {

    /** 下单防重令牌（Token）在 Redis 中的 key 前缀：order:token:{用户id} */
    public static final String USER_ORDER_TOKEN_PREFIX = "order:token";

}
