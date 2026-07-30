package com.xunqi.gulimall.auth.vo;

import lombok.Data;

/**
 * 社交登录用户对象（微博授权返回）。
 * 由微博“用 code 换取 access_token”接口返回的 JSON 反序列化得到，
 * 保存第三方登录凭证与用户标识，用于后续会员服务的登录/注册。
 */
@Data
public class SocialUser {

    /** 第三方访问令牌 */
    private String access_token;

    /** 令牌过期提醒信息 */
    private String remind_in;

    /** 令牌有效期（秒） */
    private long expires_in;

    /** 第三方平台用户唯一标识（微博 uid） */
    private String uid;

    /** 是否实名认证 */
    private String isRealName;

}
