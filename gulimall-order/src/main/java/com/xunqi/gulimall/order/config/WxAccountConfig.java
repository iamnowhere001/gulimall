package com.xunqi.gulimall.order.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 微信支付账号配置（由配置文件 wx.* 注入）。
 * 提供微信支付所需的公众号 appId、商户号、API 密钥、支付结果异步通知地址与支付成功跳转地址。
 */
@Component
@ConfigurationProperties(prefix = "wx")
@Data
public class WxAccountConfig {

    /** 微信公众平台/开放平台 AppID */
    private String appId;

    /** 微信支付商户号 */
    private String mchId;

    /** 微信支付 API 密钥 */
    private String mchKey;

    /** 支付结果异步通知地址 */
    private String notifyUrl;

    /** 支付成功跳转地址 */
    private String returnUrl;

}
