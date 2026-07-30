package com.xunqi.gulimall.thirdparty.controller;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.common.utils.BinaryUtil;
import com.aliyun.oss.model.MatchMode;
import com.aliyun.oss.model.PolicyConditions;
import com.xunqi.common.utils.R;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 阿里云 OSS 文件上传控制器。
 *
 * 提供 OSS 服务端签名直传能力：前端先请求本接口获取上传策略（policy）、签名（signature）
 * 及上传路径前缀（dir），随后携带这些参数直接向 OSS 服务 POST 上传文件，
 * 避免将 AccessKey 暴露给前端，也无需经过应用服务器中转大文件。
 */
@RestController
public class OssController {

    /**
     * 生成 OSS 直传所需的服务端签名与上传策略。
     * 返回 accessid、policy、signature、host、dir（按日期分目录）、expire（签名过期时间）。
     * 前端拿到后构造表单直接上传到 OSS。
     * @return 包含签名信息的统一返回对象 R
     */
    @RequestMapping("/oss/policy")
    public R policy() {

        // 示例文件访问地址：https://gulimall-clouds.oss-cn-beijing.aliyuncs.com/iqiyi.png

        // OSS 访问域名（Endpoint），此处使用北京地域，实际按桶所在地域填写
        String endpoint = "oss-cn-beijing.aliyuncs.com";
        // 阿里云账号 AccessKey（注意：生产环境应使用 RAM 子账号并妥善保管，此处为占位示例）
        String accessKeyId = "YOUR_ACCESS_KEY_ID";
        String accessKeySecret = "YOUR_ACCESS_KEY_SECRET";

        // 存储桶名称与访问主机地址
        String bucket = "gulimall-clouds";
        String host = "https://" + bucket + "." + endpoint;

        // 按当前日期（yyyy-MM-dd）作为上传目录前缀，便于文件归档
        String format = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        String dir = format + "/"; // 用户上传文件时指定的前缀。

        // 创建 OSS 客户端实例
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);
        Map<String, String> respMap = null;
        try {
            // 签名有效期 30 秒
            long expireTime = 30;
            long expireEndTime = System.currentTimeMillis() + expireTime * 1000;
            Date expiration = new Date(expireEndTime);
            // 构造上传策略条件：限制文件大小 0~1GB，且上传路径必须以 dir 前缀开头
            PolicyConditions policyConds = new PolicyConditions();
            policyConds.addConditionItem(PolicyConditions.COND_CONTENT_LENGTH_RANGE, 0, 1048576000);
            policyConds.addConditionItem(MatchMode.StartWith, PolicyConditions.COND_KEY, dir);

            // 生成 Base64 编码的上传策略与签名
            String postPolicy = ossClient.generatePostPolicy(expiration, policyConds);
            byte[] binaryData = postPolicy.getBytes("utf-8");
            String encodedPolicy = BinaryUtil.toBase64String(binaryData);
            String postSignature = ossClient.calculatePostSignature(postPolicy);

            // 组装返回给前端的参数
            respMap = new LinkedHashMap<String, String>();
            respMap.put("accessid", accessKeyId);
            respMap.put("policy", encodedPolicy);
            respMap.put("signature", postSignature);
            respMap.put("dir", dir);
            respMap.put("host", host);
            respMap.put("expire", String.valueOf(expireEndTime / 1000));

        } catch (Exception e) {
        } finally {
            // 使用完毕关闭客户端，释放连接资源
            ossClient.shutdown();
        }
        return R.ok().put("data",respMap);
    }

}
