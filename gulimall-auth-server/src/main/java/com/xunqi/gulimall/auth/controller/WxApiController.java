package com.xunqi.gulimall.auth.controller;

import com.alibaba.fastjson.TypeReference;
import com.xunqi.common.utils.R;
import com.xunqi.common.vo.MemberResponseVo;
import com.xunqi.gulimall.auth.feign.MemberFeignService;
import com.xunqi.gulimall.auth.utils.ConstantWxUtils;
import com.xunqi.gulimall.auth.utils.HttpClientUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpSession;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import static com.xunqi.common.constant.AuthServerConstant.LOGIN_USER;

/**
 * 微信扫码登录控制器
 * 处理微信 OAuth2.0 扫码授权登录流程
 */
@Slf4j
@Controller
@RequestMapping(value = "/api/ucenter/wx")
public class WxApiController {

    @Autowired
    private MemberFeignService memberFeignService;

    /**
     * 微信授权回调
     * 微信授权后回调本接口携带 code -> 用 code 换取 access_token 和 openid -> 调用会员服务登录或注册
     *
     * @param code    微信授权返回的授权码
     * @param state   状态码（用于防止 CSRF 攻击）
     * @param session 当前会话
     * @return 登录成功跳转首页，失败重定向回登录页
     */
    @GetMapping(value = "/callback")
    public String callback(String code, String state, HttpSession session) throws Exception {

        try {
            // 1、使用 code 换取微信 access_token 和 openid
            String baseAccessTokenUrl = "https://api.weixin.qq.com/sns/oauth2/access_token" +
                    "?appid=%s" +
                    "&secret=%s" +
                    "&code=%s" +
                    "&grant_type=authorization_code";

            String accessTokenUrl = String.format(
                    baseAccessTokenUrl,
                    ConstantWxUtils.WX_OPEN_APP_ID,
                    ConstantWxUtils.WX_OPEN_APP_SECRET,
                    code
            );

            // 2、请求微信接口换取 access_token
            String accessTokenInfo = HttpClientUtils.get(accessTokenUrl);

            // 3、调用会员服务：已注册则登录，未注册则自动注册后登录
            R r = memberFeignService.weixinLogin(accessTokenInfo);
            if (r.getCode() == 0) {
                MemberResponseVo data = r.getData("data", new TypeReference<MemberResponseVo>() {});
                log.info("微信登录成功：用户信息：{}", data.toString());

                // 4、用户信息存入 Session
                session.setAttribute(LOGIN_USER, data);
                return "redirect:http://gulimall.com";
            } else {
                return "redirect:http://auth.gulimall.com/login.html";
            }

        } catch (Exception e) {
            log.error("微信登录回调处理异常", e);
        }
        return "redirect:http://auth.gulimall.com/login.html";
    }

    /**
     * 生成微信扫码登录二维码
     * 构造微信授权 URL 并重定向，引导用户扫码授权
     *
     * @return 重定向到微信授权页面
     */
    @GetMapping(value = "/login")
    public String getWxCode() throws UnsupportedEncodingException {

        // 微信开发平台授权 baseUrl，%s 为占位符
        String baseUrl = "https://open.weixin.qq.com/connect/qrconnect" +
                "?appid=%s" +
                "&redirect_uri=%s" +
                "&response_type=code" +
                "&scope=snsapi_login" +
                "&state=%s" +
                "#wechat_redirect";

        // 对 redirect_url 进行 URLEncoder 编码
        String redirect_url = ConstantWxUtils.WX_OPEN_REDIRECT_URL;
        redirect_url = URLEncoder.encode(redirect_url, "UTF-8");

        // 拼接完整授权 URL
        String url = String.format(
                baseUrl,
                ConstantWxUtils.WX_OPEN_APP_ID,
                redirect_url,
                "xunqi"
        );

        return "redirect:" + url;
    }

}
