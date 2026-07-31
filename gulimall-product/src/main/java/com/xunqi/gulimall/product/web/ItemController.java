package com.xunqi.gulimall.product.web;

import com.xunqi.gulimall.product.service.SkuInfoService;
import com.xunqi.gulimall.product.vo.SkuItemVo;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.concurrent.ExecutionException;

@Controller
public class ItemController {

    @Resource
    private SkuInfoService skuInfoService;

    /**
     * 展示当前sku的详情（Thymeleaf 页）
     */
    @GetMapping("/{skuId}.html")
    public String skuItem(@PathVariable("skuId") Long skuId, Model model) throws ExecutionException, InterruptedException {

        SkuItemVo vos = skuInfoService.item(skuId);

        model.addAttribute("item",vos);

        return "item";
    }

    /**
     * 供前台门户(gulimall-portal)使用的 SKU 详情 JSON 接口
     * 网关 /api/product/sku/{skuId}/detail -> /product/sku/{skuId}/detail
     */
    @GetMapping("/product/sku/{skuId}/detail")
    @ResponseBody
    public SkuItemVo skuDetail(@PathVariable("skuId") Long skuId) throws ExecutionException, InterruptedException {
        return skuInfoService.item(skuId);
    }
}
