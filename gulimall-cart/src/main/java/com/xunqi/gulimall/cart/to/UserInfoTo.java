package com.xunqi.gulimall.cart.to;

import lombok.Data;

/**
 * 购物车用户身份信息（在线程内传递）。
 * 由 CartInterceptor 解析登录用户与临时 Cookie 后存入 ThreadLocal，
 * 包含：userId（登录用户 id）、userKey（临时用户标识）、tempUser（是否临时用户）。
 */
@Data
public class UserInfoTo {

    /** 登录用户 id（未登录为 null） */
    private Long userId;

    /** 临时用户标识（来自 user-key Cookie 或新生成） */
    private String userKey;

    /**
     * 是否已是临时用户（Cookie 中已存在 user-key）
     */
    private Boolean tempUser = false;

}
