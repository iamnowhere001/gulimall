package com.xunqi.gulimall.member.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.xunqi.common.utils.PageUtils;
import com.xunqi.gulimall.member.entity.MemberEntity;
import com.xunqi.gulimall.member.exception.PhoneException;
import com.xunqi.gulimall.member.exception.UsernameException;
import com.xunqi.gulimall.member.vo.MemberUserLoginVo;
import com.xunqi.gulimall.member.vo.MemberUserRegisterVo;
import com.xunqi.gulimall.member.vo.SocialUser;

import java.util.Map;

/**
 * 会员服务接口
 * 提供会员注册、登录（账号密码/社交/微信）、唯一性校验等业务方法
 */
public interface MemberService extends IService<MemberEntity> {

    PageUtils queryPage(Map<String, Object> params);

    /**
     * 用户注册
     * @param vo 注册信息
     */
    void register(MemberUserRegisterVo vo);

    /**
     * 校验手机号是否重复
     * @param phone 手机号
     */
    void checkPhoneUnique(String phone) throws PhoneException;

    /**
     * 校验用户名是否重复
     * @param userName 用户名
     */
    void checkUserNameUnique(String userName) throws UsernameException;

    /**
     * 账号密码登录
     * @param vo 登录信息
     * @return 登录成功返回会员实体，失败返回 null
     */
    MemberEntity login(MemberUserLoginVo vo);

    /**
     * 社交账号登录（微博等）
     * @param socialUser 社交用户信息
     * @return 登录成功返回会员实体，失败返回 null
     */
    MemberEntity login(SocialUser socialUser) throws Exception;

    /**
     * 微信扫码登录
     * @param accessTokenInfo 微信 access_token 信息
     * @return 登录成功返回会员实体，失败返回 null
     */
    MemberEntity login(String accessTokenInfo);
}
