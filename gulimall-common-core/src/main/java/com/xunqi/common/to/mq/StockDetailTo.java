package com.xunqi.common.to.mq;

import lombok.Data;

/**
 * 库存工作单明细传输对象（MQ）。
 * 描述一次库存锁定涉及的单条 SKU：在哪个仓库、锁定多少、锁定状态。
 * 作为 {@link StockLockedTo} 的明细被库存锁定消息携带。
 */
@Data
public class StockDetailTo {

    private Long id;
    /**
     * sku_id
     */
    private Long skuId;
    /**
     * sku_name
     */
    private String skuName;
    /**
     * 购买个数
     */
    private Integer skuNum;
    /**
     * 工作单id
     */
    private Long taskId;

    /**
     * 仓库id
     */
    private Long wareId;

    /**
     * 锁定状态
     */
    private Integer lockStatus;

}
