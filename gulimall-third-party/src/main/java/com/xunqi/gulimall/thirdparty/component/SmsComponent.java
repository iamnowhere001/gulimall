package com.xunqi.gulimall.thirdparty.component;

import com.xunqi.common.utils.HttpUtils;
import lombok.Data;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * 短信发送组件。
 *
 * 通过阿里云市场短信服务 API 发送短信验证码。配置项从
 * spring.cloud.alicloud.sms.* 注入（host/path/skin/sign/appcode 等），
 * 由 Spring 容器管理为单例组件，供 {@link com.xunqi.gulimall.thirdparty.controller.SmsSendController} 调用。
 */
@ConfigurationProperties(prefix = "spring.cloud.alicloud.sms")
@Data
@Component
public class SmsComponent {

    /** 短信服务 API 主机地址 */
    private String host;
    /** 短信服务 API 路径 */
    private String path;
    /** 短信模板皮肤（skin）编号 */
    private String skin;
    /** 短信签名（sign） */
    private String sign;
    /** 阿里云市场 APPCODE，用于接口鉴权 */
    private String appcode;

    /**
     * 发送短信验证码。
     * @param phone 接收短信的手机号
     * @param code  要发送的验证码内容
     */
    public void sendCode(String phone,String code) {
        // 短信服务使用 GET 方式调用
        String method = "GET";
        Map<String, String> headers = new HashMap<String, String>();
        // 鉴权头：APPCODE + 阿里云市场分配的 appcode
        headers.put("Authorization", "APPCODE " + appcode);
        Map<String, String> querys = new HashMap<String, String>();
        // 业务参数：验证码、手机号、模板皮肤、签名
        querys.put("code", code);
        querys.put("phone", phone);
        querys.put("skin", skin);
        querys.put("sign", sign);
        try {
            // 调用阿里云市场短信接口发送验证码
            HttpResponse response = HttpUtils.doGet(host, path, method, headers, querys);
            EntityUtils.toString(response.getEntity());
        } catch (Exception e) {
            // 失败仅打印堆栈，调用方可根据返回结果判断是否成功
            e.printStackTrace();
        }
    }

}
