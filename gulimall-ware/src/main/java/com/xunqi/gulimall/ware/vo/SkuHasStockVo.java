package com.xunqi.gulimall.ware.vo;

import lombok.Data;

/**
 * sku是否有库存vo
 */

@Data
public class SkuHasStockVo {

    private Long skuId;

    private Boolean hasStock;

}
