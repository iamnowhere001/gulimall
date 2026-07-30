package com.xunqi.gulimall.order.feign;

import com.xunqi.common.utils.R;
import com.xunqi.gulimall.order.vo.WareSkuLockVo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * 仓储服务 Feign 客户端。
 * 订单创建时远程调用 gulimall-ware：批量查库存（hasStock）、查运费/收货地址（fare）、
 * 以及锁定库存（orderLockStock，配合 MQ 延迟队列实现库存释放）。
 */
@FeignClient("gulimall-ware")
public interface WmsFeignService {

    /**
     * 查询sku是否有库存
     * @return
     */
    @PostMapping(value = "/ware/waresku/hasStock")
    R getSkuHasStock(@RequestBody List<Long> skuIds);

    /**
     * 查询运费和收货地址信息
     * @param addrId
     * @return
     */
    @GetMapping(value = "/ware/wareinfo/fare")
    R getFare(@RequestParam("addrId") Long addrId);

    /**
     * 锁定库存
     * @param vo
     * @return
     */
    @PostMapping(value = "/ware/waresku/lock/order")
    R orderLockStock(@RequestBody WareSkuLockVo vo);
}
