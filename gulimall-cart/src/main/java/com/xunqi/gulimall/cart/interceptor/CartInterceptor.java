package com.xunqi.gulimall.cart.interceptor;

import com.xunqi.common.vo.MemberResponseVo;
import com.xunqi.gulimall.cart.to.UserInfoTo;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.UUID;

import static com.xunqi.common.constant.AuthServerConstant.LOGIN_USER;
import static com.xunqi.common.constant.CartConstant.TEMP_USER_SESSION_KEY;

/**
 * 购物车拦截器。
 *
 * 在请求进入业务前解析用户身份：
 *  - 登录用户：从 Session 中取 userId；
 *  - 未登录：从 Session 中取临时用户标识（前后端分离场景，Session 通过 X-Auth-Token 头共享），
 *    没有则分配一个临时用户标识并存入 Session；
 * 将解析结果放入 ThreadLocal（toThreadLocal）供 Controller/Service 使用。
 * 请求完成后清理 ThreadLocal，避免内存泄漏。
 */
public class CartInterceptor implements HandlerInterceptor {

    /** 当前请求的购物车用户信息在线程内传递 */
    public static ThreadLocal<UserInfoTo> toThreadLocal = new ThreadLocal<>();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        UserInfoTo userInfoTo = new UserInfoTo();

        HttpSession session = request.getSession();
        //获得当前登录用户的信息
        MemberResponseVo memberResponseVo = (MemberResponseVo) session.getAttribute(LOGIN_USER);

        if (memberResponseVo != null) {
            //用户登录了
            userInfoTo.setUserId(memberResponseVo.getId());
        }

        //前后端分离：从 Session 中读取临时用户标识
        String tempUserKey = (String) session.getAttribute(TEMP_USER_SESSION_KEY);
        if (tempUserKey != null) {
            userInfoTo.setUserKey(tempUserKey);
            userInfoTo.setTempUser(true);
        }

        //如果没有临时用户一定分配一个临时用户，并存入 Session
        if (tempUserKey == null) {
            String uuid = UUID.randomUUID().toString();
            userInfoTo.setUserKey(uuid);
            session.setAttribute(TEMP_USER_SESSION_KEY, uuid);
        }

        toThreadLocal.set(userInfoTo);
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        // 前后端分离场景下无需写 Cookie，临时用户标识已存入 Session
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        toThreadLocal.remove();
    }
}
