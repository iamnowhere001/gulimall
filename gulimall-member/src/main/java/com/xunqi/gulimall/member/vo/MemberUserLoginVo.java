package com.xunqi.gulimall.member.vo;

import lombok.Data;

/**
 * 会员登录请求 VO
 */
@Data
public class MemberUserLoginVo {

    /** 登录账号（用户名/手机号/邮箱） */
    private String loginacct;

    /** 密码 */
    private String password;

}
