package com.xunqi.common.web.interceptor;

import com.alibaba.fastjson.JSON;
import com.xunqi.common.vo.MemberResponseVo;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.xunqi.common.constant.AuthServerConstant.LOGIN_USER;

/**
 * 通用登录拦截器。
 * <p>
 * 支持两种匹配模式：
 * <ul>
 *   <li><b>includePaths</b>：仅对匹配的路径执行登录校验，其他请求直接放行</li>
 *   <li><b>excludePaths</b>：对匹配的路径直接放行，其他请求执行登录校验</li>
 * </ul>
 * 若同时配置，优先以 <code>includePaths</code> 为准。
 * <p>
 * 登录成功后将用户信息写入 ThreadLocal，供同线程内 Service 层获取；
 * 请求结束后在 {@link #afterCompletion} 中清理，防止线程池复用导致内存泄漏。
 * 未登录时返回 JSON 格式的 401 错误（前后端分离适配）。
 */
public class LoginUserInterceptor implements HandlerInterceptor {

    /** 使用 ThreadLocal 保存当前线程的登录用户，便于 service 层获取 */
    public static ThreadLocal<MemberResponseVo> loginUser = new ThreadLocal<>();

    private final List<String> excludePaths;
    private final List<String> includePaths;

    public LoginUserInterceptor(List<String> excludePaths, List<String> includePaths) {
        this.excludePaths = excludePaths;
        this.includePaths = includePaths;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        String uri = request.getRequestURI();
        AntPathMatcher matcher = new AntPathMatcher();

        // 若配置了 includePaths，仅对这些路径做登录校验，其余直接放行
        if (includePaths != null && !includePaths.isEmpty()) {
            boolean shouldCheck = includePaths.stream().anyMatch(p -> matcher.match(p, uri));
            if (!shouldCheck) {
                return true;
            }
        } else if (excludePaths != null && !excludePaths.isEmpty()) {
            // 若配置了 excludePaths，对这些路径直接放行
            boolean excluded = excludePaths.stream().anyMatch(p -> matcher.match(p, uri));
            if (excluded) {
                return true;
            }
        }

        // 获取登录的用户信息
        MemberResponseVo attribute = (MemberResponseVo) request.getSession().getAttribute(LOGIN_USER);

        if (attribute != null) {
            // 把登录后用户的信息放在 ThreadLocal 里面进行保存
            loginUser.set(attribute);
            return true;
        } else {
            // 未登录，返回 JSON 401 错误（前后端分离适配）
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json;charset=UTF-8");
            PrintWriter out = response.getWriter();
            Map<String, Object> body = new HashMap<>();
            body.put("code", 401);
            body.put("msg", "请先登录");
            out.write(JSON.toJSONString(body));
            out.flush();
            return false;
        }
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        // 清理 ThreadLocal，防止线程池复用导致内存泄漏和数据串号
        loginUser.remove();
    }
}
