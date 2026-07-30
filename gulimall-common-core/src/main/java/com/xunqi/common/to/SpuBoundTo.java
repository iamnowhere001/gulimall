package com.xunqi.common.to;

import lombok.Data;

import java.math.BigDecimal;

/**
 * SPU 积分奖励（购物积分 / 成长积分）传输对象。
 * 商品上架时由 product 服务发送给 coupon 服务，保存 SPU 的积分规则。
 */
@Data
public class SpuBoundTo {

    /** SPU 编号 */
    private Long spuId;

    /** 购物积分（buy bounds） */
    private BigDecimal buyBounds;

    /** 成长积分（grow bounds） */
    private BigDecimal growBounds;

}
