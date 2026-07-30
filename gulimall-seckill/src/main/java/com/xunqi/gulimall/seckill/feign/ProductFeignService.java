package com.xunqi.gulimall.seckill.feign;

import com.xunqi.common.utils.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 商品服务远程调用客户端（Feign）
 * 用于在上架秒杀商品时，远程查询参与秒杀的 SKU 基本信息（名称、图片、价格等），
 * 以便把这些展示信息一起缓存到 Redis，前端展示无需再次远程调用。
 */
@FeignClient("gulimall-product")
public interface ProductFeignService {

    /**
     * 根据 skuId 远程查询 SKU 的详细信息。
     * @param skuId 商品 SKU 编号
     * @return 包含 skuInfo 的通用返回结果 R
     */
    @RequestMapping("/product/skuinfo/info/{skuId}")
    R getSkuInfo(@PathVariable("skuId") Long skuId);

}
