package com.xunqi.gulimall.auth.vo;

import lombok.Data;

/**
 * 用户登录请求 VO
 */
@Data
public class UserLoginVo {

    /** 登录账号（用户名/手机号/邮箱） */
    private String loginacct;

    /** 密码 */
    private String password;
}
