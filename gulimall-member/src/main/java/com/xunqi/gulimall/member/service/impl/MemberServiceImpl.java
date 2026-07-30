package com.xunqi.gulimall.member.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.google.gson.Gson;
import com.xunqi.common.utils.HttpUtils;
import com.xunqi.common.utils.PageUtils;
import com.xunqi.common.utils.Query;
import com.xunqi.gulimall.member.dao.MemberDao;
import com.xunqi.gulimall.member.dao.MemberLevelDao;
import com.xunqi.gulimall.member.entity.MemberEntity;
import com.xunqi.gulimall.member.entity.MemberLevelEntity;
import com.xunqi.gulimall.member.exception.PhoneException;
import com.xunqi.gulimall.member.exception.UsernameException;
import com.xunqi.gulimall.member.service.MemberService;
import com.xunqi.gulimall.member.utils.HttpClientUtils;
import com.xunqi.gulimall.member.vo.MemberUserLoginVo;
import com.xunqi.gulimall.member.vo.MemberUserRegisterVo;
import com.xunqi.gulimall.member.vo.SocialUser;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service("memberService")
public class MemberServiceImpl extends ServiceImpl<MemberDao, MemberEntity> implements MemberService {

    @Resource
    private MemberLevelDao memberLevelDao;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<MemberEntity> page = this.page(
                new Query<MemberEntity>().getPage(params),
                new QueryWrapper<MemberEntity>()
        );

        return new PageUtils(page);
    }

    /**
     * 用户注册流程：
     * 1. 校验手机号、用户名是否唯一（重复时抛出异常由 Controller 捕获）
     * 2. 设置默认会员等级
     * 3. 密码使用 BCrypt 加密后入库
     */
    @Override
    public void register(MemberUserRegisterVo vo) {

        MemberEntity memberEntity = new MemberEntity();

        // 设置默认等级
        MemberLevelEntity levelEntity = memberLevelDao.getDefaultLevel();
        memberEntity.setLevelId(levelEntity.getId());

        // 校验手机号和用户名是否唯一（通过异常机制感知）
        checkPhoneUnique(vo.getPhone());
        checkUserNameUnique(vo.getUserName());

        memberEntity.setNickname(vo.getUserName());
        memberEntity.setUsername(vo.getUserName());
        // 密码使用 BCrypt 加密
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        String encode = bCryptPasswordEncoder.encode(vo.getPassword());
        memberEntity.setPassword(encode);
        memberEntity.setMobile(vo.getPhone());
        memberEntity.setGender(0);
        memberEntity.setCreateTime(new Date());

        this.baseMapper.insert(memberEntity);
    }

    @Override
    public void checkPhoneUnique(String phone) throws PhoneException {

        Integer phoneCount = this.baseMapper.selectCount(new QueryWrapper<MemberEntity>().eq("mobile", phone));

        if (phoneCount > 0) {
            throw new PhoneException();
        }

    }

    @Override
    public void checkUserNameUnique(String userName) throws UsernameException {

        Integer usernameCount = this.baseMapper.selectCount(new QueryWrapper<MemberEntity>().eq("username", userName));

        if (usernameCount > 0) {
            throw new UsernameException();
        }
    }

    /**
     * 账号密码登录
     * 支持用户名或手机号登录，使用 BCrypt 校验密码
     */
    @Override
    public MemberEntity login(MemberUserLoginVo vo) {

        String loginacct = vo.getLoginacct();
        String password = vo.getPassword();

        // 查询条件：username = ? OR mobile = ?
        MemberEntity memberEntity = this.baseMapper.selectOne(new QueryWrapper<MemberEntity>()
                .eq("username", loginacct).or().eq("mobile", loginacct));

        if (memberEntity == null) {
            return null;
        }

        // BCrypt 密码匹配
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        boolean matches = passwordEncoder.matches(password, memberEntity.getPassword());
        return matches ? memberEntity : null;
    }

    /**
     * 社交账号登录（微博）
     * 老用户：更新 access_token；新用户：查询社交资料后自动注册
     */
    @Override
    public MemberEntity login(SocialUser socialUser) throws Exception {

        String uid = socialUser.getUid();

        // 1、判断当前社交用户是否已注册
        MemberEntity memberEntity = this.baseMapper.selectOne(new QueryWrapper<MemberEntity>().eq("social_uid", uid));

        if (memberEntity != null) {
            // 老用户：更新 access_token 和过期时间
            MemberEntity update = new MemberEntity();
            update.setId(memberEntity.getId());
            update.setAccessToken(socialUser.getAccess_token());
            update.setExpiresIn(socialUser.getExpires_in());
            this.baseMapper.updateById(update);

            memberEntity.setAccessToken(socialUser.getAccess_token());
            memberEntity.setExpiresIn(socialUser.getExpires_in());
            return memberEntity;
        }

        // 2、新用户：查询微博用户信息后自动注册
        MemberEntity register = new MemberEntity();
        Map<String,String> query = new HashMap<>();
        query.put("access_token", socialUser.getAccess_token());
        query.put("uid", socialUser.getUid());
        HttpResponse response = HttpUtils.doGet("https://api.weibo.com", "/2/users/show.json", "get", new HashMap<String, String>(), query);

        if (response.getStatusLine().getStatusCode() == 200) {
            String json = EntityUtils.toString(response.getEntity());
            JSONObject jsonObject = JSON.parseObject(json);
            String name = jsonObject.getString("name");
            String gender = jsonObject.getString("gender");
            String profileImageUrl = jsonObject.getString("profile_image_url");

            register.setNickname(name);
            register.setGender("m".equals(gender) ? 1 : 0);
            register.setHeader(profileImageUrl);
            register.setCreateTime(new Date());
            register.setSocialUid(socialUser.getUid());
            register.setAccessToken(socialUser.getAccess_token());
            register.setExpiresIn(socialUser.getExpires_in());

            this.baseMapper.insert(register);

        }
        return register;
    }

    /**
     * 微信扫码登录
     * 新用户：使用 access_token 和 openid 请求微信 API 获取用户信息后注册
     * 老用户：直接返回已存在的会员信息
     */
    @Override
    public MemberEntity login(String accessTokenInfo) {

        // 从 accessTokenInfo 中解析 access_token 和 openid
        Gson gson = new Gson();
        HashMap accessMap = gson.fromJson(accessTokenInfo, HashMap.class);
        String accessToken = (String) accessMap.get("access_token");
        String openid = (String) accessMap.get("openid");

        // 查询当前微信用户是否已注册
        MemberEntity memberEntity = this.baseMapper.selectOne(new QueryWrapper<MemberEntity>().eq("social_uid", openid));

        if (memberEntity != null) {
            return memberEntity;
        }

        // 新用户：请求微信 API 获取用户信息
        String baseUserInfoUrl = "https://api.weixin.qq.com/sns/userinfo" +
                "?access_token=%s" +
                "&openid=%s";
        String userInfoUrl = String.format(baseUserInfoUrl, accessToken, openid);

        String resultUserInfo;
        try {
            resultUserInfo = HttpClientUtils.get(userInfoUrl);
        } catch (Exception e) {
            throw new RuntimeException("请求微信用户信息接口失败", e);
        }

        // 解析微信返回的用户信息
        HashMap userInfoMap = gson.fromJson(resultUserInfo, HashMap.class);
        String nickName = (String) userInfoMap.get("nickname");
        Double sex = (Double) userInfoMap.get("sex");
        String headimgurl = (String) userInfoMap.get("headimgurl");

        // 注册新会员
        memberEntity = new MemberEntity();
        memberEntity.setNickname(nickName);
        memberEntity.setGender(Integer.valueOf(Double.valueOf(sex).intValue()));
        memberEntity.setHeader(headimgurl);
        memberEntity.setCreateTime(new Date());
        memberEntity.setSocialUid(openid);
        this.baseMapper.insert(memberEntity);

        return memberEntity;
    }

}
