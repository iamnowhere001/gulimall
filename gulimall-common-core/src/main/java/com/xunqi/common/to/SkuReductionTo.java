package com.xunqi.common.to;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * SKU 优惠规则（满减 / 折扣 / 会员价）传输对象。
 * 商品上架时由 product 服务发送给 coupon 服务，用于保存 SKU 的优惠促销规则。
 */
@Data
public class SkuReductionTo {

    /** SKU 编号 */
    private Long skuId;
    /** 满件打折的件数门槛 */
    private int fullCount;
    /** 折扣值（如 0.8 表示打八折） */
    private BigDecimal discount;
    /** 满件打折是否启用（1 启用 / 0 不启用） */
    private int countStatus;
    /** 满额减价的金额门槛 */
    private BigDecimal fullPrice;
    /** 满减金额 */
    private BigDecimal reducePrice;
    /** 满额减价是否启用（1 启用 / 0 不启用） */
    private int priceStatus;
    /** 各会员等级对应的专属价格列表 */
    private List<MemberPrice> memberPrice;

}
