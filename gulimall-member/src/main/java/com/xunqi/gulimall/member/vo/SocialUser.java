package com.xunqi.gulimall.member.vo;

import lombok.Data;

/**
 * 社交用户信息
 * 用于接收微博等社交平台返回的 access_token 及用户标识
 */
@Data
public class SocialUser {

    /** 访问令牌 */
    private String access_token;

    /** 提醒时间 */
    private String remind_in;

    /** 过期时间（秒） */
    private long expires_in;

    /** 社交用户唯一标识 */
    private String uid;

    /** 是否实名 */
    private String isRealName;

}
