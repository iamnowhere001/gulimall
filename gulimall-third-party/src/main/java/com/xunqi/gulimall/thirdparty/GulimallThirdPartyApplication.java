package com.xunqi.gulimall.thirdparty;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * 谷粒商城第三方服务（third-party）启动类。
 *
 * 该服务封装对外部第三方云能力的调用，供其他微服务通过 Feign 远程调用，目前主要提供：
 *  - 阿里云 OSS 文件上传（服务端签名直传）
 *  - 阿里云短信发送（验证码）
 * 注册到 Nacos 以被发现。
 */
@EnableDiscoveryClient
@SpringBootApplication
public class GulimallThirdPartyApplication {

    /**
     * 第三方服务启动入口。
     * @param args 命令行参数
     */
    public static void main(String[] args) {
        SpringApplication.run(GulimallThirdPartyApplication.class, args);
    }

}
