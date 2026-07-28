package com.xunqi.gulimall.auth.feign;

import com.xunqi.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 第三方服务 Feign 客户端
 * 远程调用 gulimall-third-party 服务发送短信验证码
 */
@FeignClient("gulimall-third-party")
public interface ThirdPartFeignService {

    /**
     * 发送短信验证码
     */
    @GetMapping(value = "/sms/sendCode")
    R sendCode(@RequestParam("phone") String phone, @RequestParam("code") String code);

}
