package com.xunqi.gulimall.order.feign;

import com.xunqi.gulimall.order.vo.OrderItemVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

/**
 * 购物车服务 Feign 客户端。
 * 订单在结算确认页远程查询当前用户购物车中已勾选的商品项（gulimall-cart）。
 */
@FeignClient("gulimall-cart")
public interface CartFeignService {

    /**
     * 查询当前用户购物车选中的商品项
     * @return
     */
    @GetMapping(value = "/currentUserCartItems")
    List<OrderItemVo> getCurrentCartItems();

}
