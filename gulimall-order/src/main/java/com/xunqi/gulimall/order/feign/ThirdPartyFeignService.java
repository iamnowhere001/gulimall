package com.xunqi.gulimall.order.feign;

import com.alipay.api.AlipayApiException;
import com.xunqi.gulimall.order.vo.PayVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * 第三方服务 Feign 客户端。
 * 调用 gulimall-third-party 的支付宝支付接口（/pay），传入 PayVo 发起支付。
 */
@FeignClient("gulimall-third-party")
public interface ThirdPartyFeignService {

    @GetMapping(value = "/pay", consumes = "application/json")
    String pay(@RequestBody PayVo vo) throws AlipayApiException;

}
