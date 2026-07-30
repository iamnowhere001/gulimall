package com.xunqi.common.to;

import lombok.Data;

/**
 * SKU 库存查询结果传输对象。
 * 由仓储服务返回某 SKU 是否有库存，供商品详情、购物车等判断可售状态。
 */
@Data
public class SkuHasStockVo {

    /** SKU 编号 */
    private Long skuId;

    /** 是否有库存 */
    private Boolean hasStock;

}
