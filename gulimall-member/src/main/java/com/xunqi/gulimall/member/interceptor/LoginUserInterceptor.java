package com.xunqi.gulimall.member.interceptor;

import com.xunqi.common.vo.MemberResponseVo;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.PrintWriter;

import static com.xunqi.common.constant.AuthServerConstant.LOGIN_USER;

/**
 * 登录拦截器
 * 拦截所有请求校验登录状态，并将登录用户信息存入 ThreadLocal 供同线程内使用
 */
@Component
public class LoginUserInterceptor implements HandlerInterceptor {

    /** 使用 ThreadLocal 保存当前线程的登录用户，便于 service 层获取 */
    public static ThreadLocal<MemberResponseVo> loginUser = new ThreadLocal<>();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        String uri = request.getRequestURI();
        boolean match = new AntPathMatcher().match("/member/**", uri);
        if (match) {
            return true;
        }

        HttpSession session = request.getSession();
        MemberResponseVo attribute = (MemberResponseVo) session.getAttribute(LOGIN_USER);

        if (attribute != null) {
            loginUser.set(attribute);
            return true;
        } else {
            response.setContentType("text/html;charset=UTF-8");
            PrintWriter out = response.getWriter();
            out.println("<script>alert('请先进行登录，再进行后续操作！');location.href='http://auth.gulimall.com/login.html'</script>");
            return false;
        }
    }
}
