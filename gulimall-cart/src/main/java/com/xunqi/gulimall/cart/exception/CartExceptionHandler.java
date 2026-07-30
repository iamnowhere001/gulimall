package com.xunqi.gulimall.cart.exception;

/**
 * 购物车业务自定义异常。
 * 用于表示“购物车无此商品”等购物车专属异常，由全局异常处理器
 * {@link RuntimeExceptionHandler} 捕获并统一返回提示信息。
 */
public class CartExceptionHandler extends RuntimeException {
}
