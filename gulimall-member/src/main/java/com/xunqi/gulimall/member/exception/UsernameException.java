package com.xunqi.gulimall.member.exception;

/**
 * 用户名已存在异常
 */
public class UsernameException extends RuntimeException {

    public UsernameException() {
        super("存在相同的用户名");
    }
}
