package com.xunqi.gulimall.auth.feign;

import com.xunqi.common.utils.R;
import com.xunqi.gulimall.auth.vo.SocialUser;
import com.xunqi.gulimall.auth.vo.UserLoginVo;
import com.xunqi.gulimall.auth.vo.UserRegisterVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * 会员服务 Feign 客户端
 * 远程调用 gulimall-member 服务完成用户注册、登录、社交登录等操作
 */
@FeignClient("gulimall-member")
public interface MemberFeignService {

    /**
     * 用户注册
     */
    @PostMapping(value = "/member/member/register")
    R register(@RequestBody UserRegisterVo vo);

    /**
     * 账号密码登录
     */
    @PostMapping(value = "/member/member/login")
    R login(@RequestBody UserLoginVo vo);

    /**
     * 社交账号登录（微博等）
     */
    @PostMapping(value = "/member/member/oauth2/login")
    R oauthLogin(@RequestBody SocialUser socialUser) throws Exception;

    /**
     * 微信扫码登录
     */
    @PostMapping(value = "/member/member/weixin/login")
    R weixinLogin(@RequestParam("accessTokenInfo") String accessTokenInfo);
}
