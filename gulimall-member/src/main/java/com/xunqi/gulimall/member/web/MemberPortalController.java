package com.xunqi.gulimall.member.web;

import com.xunqi.common.utils.R;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * 前台门户(gulimall-portal)会员相关 JSON 接口。
 * 网关 /api/member/** -> /member/**
 */
@RestController
@RequestMapping("/member")
public class MemberPortalController {

    /**
     * 获取当前登录会员（基于 Spring Session 共享，未登录返回 401）
     * 网关 /api/member/currentMember -> /member/currentMember
     */
    @GetMapping("/currentMember")
    public R currentMember(HttpServletRequest request) {
        Object user = request.getSession().getAttribute("loginUser");
        if (user != null) {
            return R.ok().put("data", user);
        }
        return R.error(401, "未登录");
    }
}
