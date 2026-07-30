package com.xunqi.gulimall.auth.controller;

import com.alibaba.fastjson.TypeReference;
import com.xunqi.common.constant.AuthServerConstant;
import com.xunqi.common.exception.BizCodeEnum;
import com.xunqi.common.utils.R;
import com.xunqi.common.vo.MemberResponseVo;
import com.xunqi.gulimall.auth.feign.MemberFeignService;
import com.xunqi.gulimall.auth.feign.ThirdPartFeignService;
import com.xunqi.gulimall.auth.vo.UserLoginVo;
import com.xunqi.gulimall.auth.vo.UserRegisterVo;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.xunqi.common.constant.AuthServerConstant.LOGIN_USER;

/**
 * 登录注册控制器
 * 负责短信验证码发送、用户注册、账号登录、退出登录等核心认证流程
 */
@Controller
public class LoginController {

    @Autowired
    private ThirdPartFeignService thirdPartFeignService;

    @Autowired
    private MemberFeignService memberFeignService;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 发送短信验证码
     * 通过 Redis 实现 60 秒接口防刷，验证码有效期 10 分钟
     *
     * @param phone 手机号
     * @return 发送结果
     */
    @ResponseBody
    @GetMapping(value = "/sms/sendCode")
    public R sendCode(@RequestParam("phone") String phone) {

        // 1、接口防刷：校验同一手机号是否在 60s 内已发送过验证码
        String redisCode = stringRedisTemplate.opsForValue().get(AuthServerConstant.SMS_CODE_CACHE_PREFIX + phone);
        if (!StringUtils.isEmpty(redisCode)) {
            // redis 中存储格式为 code_timestamp，取出时间戳判断是否在 60s 内
            String[] parts = redisCode.split("_");
            if (parts.length == 2) {
                long lastSendTime = Long.parseLong(parts[1]);
                if (System.currentTimeMillis() - lastSendTime < 60000) {
                    return R.error(BizCodeEnum.SMS_CODE_EXCEPTION.getCode(), BizCodeEnum.SMS_CODE_EXCEPTION.getMessage());
                }
            }
        }

        // 2、生成 6 位随机验证码，拼接时间戳后存入 Redis（key=phone, value=code_timestamp）
        int code = (int) ((Math.random() * 9 + 1) * 100000);
        String codeNum = String.valueOf(code);
        String redisStorage = codeNum + "_" + System.currentTimeMillis();

        stringRedisTemplate.opsForValue().set(AuthServerConstant.SMS_CODE_CACHE_PREFIX + phone,
                redisStorage, 10, TimeUnit.MINUTES);

        // 3、调用第三方服务发送短信
        thirdPartFeignService.sendCode(phone, codeNum);

        return R.ok();
    }

    /**
     * 用户注册
     * 校验表单数据与短信验证码，校验通过后调用会员服务完成注册
     * 使用 RedirectAttributes 携带错误信息重定向回注册页（Flash attribute 仅在一次请求中有效）
     *
     * @param vos       注册表单 VO
     * @param result    校验结果
     * @param attributes 重定向属性
     * @return 注册成功跳转登录页，失败重定向回注册页
     */
    @PostMapping(value = "/register")
    public String register(@Valid UserRegisterVo vos, BindingResult result,
                           RedirectAttributes attributes) {

        // 表单校验失败，回到注册页面
        if (result.hasErrors()) {
            Map<String, String> errors = result.getFieldErrors().stream()
                    .collect(Collectors.toMap(FieldError::getField, FieldError::getDefaultMessage));
            attributes.addFlashAttribute("errors", errors);
            return "redirect:http://auth.gulimall.com/reg.html";
        }

        // 1、校验短信验证码
        String code = vos.getCode();
        String redisCode = stringRedisTemplate.opsForValue().get(AuthServerConstant.SMS_CODE_CACHE_PREFIX + vos.getPhone());

        if (!StringUtils.isEmpty(redisCode)) {
            if (code.equals(redisCode.split("_")[0])) {
                // 2、调用远程服务完成注册
                R register = memberFeignService.register(vos);
                if (register.getCode() == 0) {
                    // 注册成功后删除验证码，避免重复使用
                    stringRedisTemplate.delete(AuthServerConstant.SMS_CODE_CACHE_PREFIX + vos.getPhone());
                    return "redirect:http://auth.gulimall.com/login.html";
                } else {
                    Map<String, String> errors = new HashMap<>();
                    errors.put("msg", register.getData("msg", new TypeReference<String>() {}));
                    attributes.addFlashAttribute("errors", errors);
                    return "redirect:http://auth.gulimall.com/reg.html";
                }
            }
        }

        // 验证码错误
        Map<String, String> errors = new HashMap<>();
        errors.put("code", "验证码错误");
        attributes.addFlashAttribute("errors", errors);
        return "redirect:http://auth.gulimall.com/reg.html";
    }

    /**
     * 登录页面
     * 已登录用户访问登录页时直接跳转首页
     *
     * @param session 当前会话
     * @return 未登录返回登录页，已登录重定向到首页
     */
    @GetMapping(value = "/login.html")
    public String loginPage(HttpSession session,
                            @RequestParam(value = "return_url", required = false) String returnUrl) {

        Object attribute = session.getAttribute(LOGIN_USER);
        if (attribute == null) {
            return "login";
        } else {
            String redirect = (returnUrl != null && returnUrl.startsWith("http")) ? returnUrl : "http://gulimall.com";
            return "redirect:" + redirect;
        }
    }

    /**
     * 账号密码登录
     * 调用会员服务校验账号密码，校验通过后将用户信息存入 Session（借助 Spring Session 同步到 Redis）
     *
     * @param vo        登录表单 VO
     * @param attributes 重定向属性
     * @param session   当前会话
     * @return 登录成功跳转首页，失败重定向回登录页
     */
    @PostMapping(value = "/login")
    public String login(UserLoginVo vo, RedirectAttributes attributes, HttpSession session,
                        @RequestParam(value = "return_url", required = false) String returnUrl) {

        R login = memberFeignService.login(vo);

        if (login.getCode() == 0) {
            MemberResponseVo data = login.getData("data", new TypeReference<MemberResponseVo>() {});
            session.setAttribute(LOGIN_USER, data);
            String redirect = (returnUrl != null && returnUrl.startsWith("http")) ? returnUrl : "http://gulimall.com";
            return "redirect:" + redirect;
        } else {
            Map<String, String> errors = new HashMap<>();
            errors.put("msg", login.getData("msg", new TypeReference<String>() {}));
            attributes.addFlashAttribute("errors", errors);
            // 登录失败时携带 return_url 回登录页，以便二次登录成功后能回跳
            String redirectUrl = "http://auth.gulimall.com/login.html";
            if (returnUrl != null && returnUrl.startsWith("http")) {
                redirectUrl += "?return_url=" + returnUrl;
            }
            return "redirect:" + redirectUrl;
        }
    }

    /**
     * 退出登录
     * 清除 Session 中的用户信息并使 Session 失效
     *
     * @param request 当前请求
     * @return 重定向到首页
     */
    @GetMapping(value = "/logout.html")
    public String logout(HttpServletRequest request) {
        request.getSession().removeAttribute(LOGIN_USER);
        request.getSession().invalidate();
        return "redirect:http://gulimall.com";
    }

}
