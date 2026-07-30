package com.xunqi.common.web.config;

import com.alibaba.csp.sentinel.adapter.servlet.callback.UrlBlockHandler;
import com.alibaba.csp.sentinel.adapter.servlet.callback.WebCallbackManager;
import com.alibaba.csp.sentinel.slots.block.BlockException;
import com.alibaba.fastjson.JSON;
import com.xunqi.common.exception.BizCodeEnum;
import com.xunqi.common.utils.R;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Sentinel 限流降级统一配置。
 * <p>
 * 注册全局 UrlBlockHandler，限流时返回统一 JSON 响应。
 * 各业务模块通过引入 common-web 自动装配，无需重复定义。
 */
public class GulimallSentinelConfig {

    /**
     * 注册 Sentinel 限流降级处理器。
     * 注意：Sentinel WebCallbackManager 是全局单例，只需初始化一次。
     */
    public static void init() {
        WebCallbackManager.setUrlBlockHandler(new UrlBlockHandler() {
            @Override
            public void blocked(HttpServletRequest request, HttpServletResponse response, BlockException ex)
                    throws IOException {
                R error = R.error(BizCodeEnum.TO_MANY_REQUEST.getCode(), BizCodeEnum.TO_MANY_REQUEST.getMessage());
                response.setCharacterEncoding("UTF-8");
                response.setContentType("application/json");
                response.getWriter().write(JSON.toJSONString(error));
            }
        });
    }
}
