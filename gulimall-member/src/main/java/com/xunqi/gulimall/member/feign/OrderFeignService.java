package com.xunqi.gulimall.member.feign;

import com.xunqi.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

/**
 * 订单服务 Feign 客户端
 * 远程调用 gulimall-order 服务查询订单数据
 */
@FeignClient("gulimall-order")
public interface OrderFeignService {

    /**
     * 分页查询当前登录用户的所有订单信息（含订单项）
     */
    @PostMapping("/order/order/listWithItem")
    R listWithItem(@RequestBody Map<String, Object> params);

}
