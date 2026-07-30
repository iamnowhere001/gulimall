package com.xunqi.gulimall.gateway.config;

import com.alibaba.csp.sentinel.adapter.gateway.sc.callback.BlockRequestHandler;
import com.alibaba.csp.sentinel.adapter.gateway.sc.callback.GatewayCallbackManager;
import com.alibaba.fastjson.JSON;
import com.xunqi.common.exception.BizCodeEnum;
import com.xunqi.common.utils.R;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.ServerResponse;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

// @Configuration // 已禁用：项目移除了 Sentinel 网关限流依赖，此处不再注册为配置类
/**
 * Sentinel 网关限流回调配置（当前已停用）。
 *
 * 原本用于在网关被 Sentinel 限流（流控/降级/热点参数等规则触发）时，
 * 通过 GatewayCallbackManager.setBlockHandler 设置统一的阻塞处理回调，
 * 返回统一的 JSON 错误提示而非默认提示。
 * 由于项目已移除 Sentinel，该类不再生效，仅保留作历史参考，逻辑无需改动。
 */
public class SentinelGatewayConfig {

    public SentinelGatewayConfig() {
        GatewayCallbackManager.setBlockHandler(new BlockRequestHandler() {
            // 当网关请求被 Sentinel 限流/拦截时，会调用此回调方法
            @Override
            public Mono<ServerResponse> handleRequest(ServerWebExchange exchange, Throwable t) {

                // 构造统一的限流错误响应：使用业务码 TO_MANY_REQUEST（请求过多）
                R error = R.error(BizCodeEnum.TO_MANY_REQUEST.getCode(), BizCodeEnum.TO_MANY_REQUEST.getMessage());
                // 序列化为 JSON 字符串返回给前端
                String errorJson = JSON.toJSONString(error);

                // 以 200 状态码返回 JSON 错误体，避免浏览器将限流响应当作异常网络错误
                Mono<ServerResponse> body = ServerResponse.ok().body(Mono.just(errorJson), String.class);
                return body;
            }
        });
    }

}
