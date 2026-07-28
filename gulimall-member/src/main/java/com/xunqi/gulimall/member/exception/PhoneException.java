package com.xunqi.gulimall.member.exception;

/**
 * 手机号已存在异常
 */
public class PhoneException extends RuntimeException {

    public PhoneException() {
        super("存在相同的手机号");
    }
}
