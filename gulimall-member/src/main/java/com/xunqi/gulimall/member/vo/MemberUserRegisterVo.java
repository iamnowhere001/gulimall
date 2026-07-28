package com.xunqi.gulimall.member.vo;

import lombok.Data;

/**
 * 会员注册请求 VO
 */
@Data
public class MemberUserRegisterVo {

    /** 用户名 */
    private String userName;

    /** 密码 */
    private String password;

    /** 手机号 */
    private String phone;

}
