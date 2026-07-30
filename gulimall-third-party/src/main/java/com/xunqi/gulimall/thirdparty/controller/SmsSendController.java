package com.xunqi.gulimall.thirdparty.controller;

import com.xunqi.common.utils.R;
import com.xunqi.gulimall.thirdparty.component.SmsComponent;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.Resource;

/**
 * 短信发送 Feign 远程调用控制器。
 *
 * 供其他微服务（如会员服务 member）通过 Feign 远程调用本接口来发送短信验证码，
 * 从而将短信能力统一收敛到第三方服务，避免各服务自行集成短信 SDK。
 */
@Controller
@RequestMapping(value = "/sms")
public class SmsSendController {

    /** 注入短信发送组件 */
    @Resource
    private SmsComponent smsComponent;

    /**
     * 发送短信验证码（对外提供的远程调用入口）。
     * @param phone 接收短信的手机号
     * @param code  要发送的验证码
     * @return 统一返回对象 R，成功时 code=0
     */
    @GetMapping(value = "/sendCode")
    public R sendCode(@RequestParam("phone") String phone, @RequestParam("code") String code) {

        // 委托短信组件实际发送验证码
        smsComponent.sendCode(phone,code);

        return R.ok();
    }

}
