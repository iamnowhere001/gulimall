package com.xunqi.gulimall.auth.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.xunqi.common.utils.HttpUtils;
import com.xunqi.common.utils.R;
import com.xunqi.common.vo.MemberResponseVo;
import com.xunqi.gulimall.auth.feign.MemberFeignService;
import com.xunqi.gulimall.auth.vo.SocialUser;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

import static com.xunqi.common.constant.AuthServerConstant.LOGIN_USER;

/**
 * 社交登录控制器
 * 处理微博 OAuth2.0 授权登录流程
 */
@Slf4j
@Controller
public class OAuth2Controller {

    @Autowired
    private MemberFeignService memberFeignService;


    /**
     * 微博授权登录回调
     * 流程：用户授权后微博回调本接口携带 code -> 用 code 换取 access_token -> 调用会员服务登录或注册
     *
     * @param code    微博授权返回的授权码
     * @param session 当前会话
     * @return 登录成功跳转首页，失败重定向回登录页
     */
    @GetMapping(value = "/oauth2.0/weibo/success")
    public String weibo(@RequestParam("code") String code, HttpSession session) throws Exception {

        // 1、构建换取 access_token 的请求参数
        Map<String, String> map = new HashMap<>();
        map.put("client_id", "2077705774");
        map.put("client_secret", "40af02bd1c7e435ba6a6e9cd3bf799fd");
        map.put("grant_type", "authorization_code");
        map.put("redirect_uri", "http://auth.gulimall.com/oauth2.0/weibo/success");
        map.put("code", code);

        // 2、使用 code 换取 access_token
        HttpResponse response = HttpUtils.doPost("https://api.weibo.com", "/oauth2/access_token", "post", new HashMap<>(), map, new HashMap<>());

        if (response.getStatusLine().getStatusCode() == 200) {
            // 3、解析 access_token，转为通用社交用户对象
            String json = EntityUtils.toString(response.getEntity());
            SocialUser socialUser = JSON.parseObject(json, SocialUser.class);

            // 4、调用会员服务：已注册则登录，未注册则自动注册后登录
            R oauthLogin = memberFeignService.oauthLogin(socialUser);
            if (oauthLogin.getCode() == 0) {
                MemberResponseVo data = oauthLogin.getData("data", new TypeReference<MemberResponseVo>() {});
                log.info("微博登录成功：用户信息：{}", data.toString());

                // 5、用户信息存入 Session（Spring Session 会同步至 Redis，实现子域共享）
                session.setAttribute(LOGIN_USER, data);
                return "redirect:http://gulimall.com";
            } else {
                return "redirect:http://auth.gulimall.com/login.html";
            }
        } else {
            return "redirect:http://auth.gulimall.com/login.html";
        }

    }

}
