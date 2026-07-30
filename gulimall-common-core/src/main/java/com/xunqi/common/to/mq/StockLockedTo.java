package com.xunqi.common.to.mq;

import lombok.Data;

/**
 * 库存锁定消息对象（MQ）。
 * 下单后由订单服务发送给仓储服务，请求锁定库存；
 * 包含库存工作单 id 与全部锁定明细（{@link StockDetailTo}），用于后续扣减或回滚。
 */
@Data
public class StockLockedTo {

    /** 库存工作单的id **/
    private Long id;

    /** 工作单详情的所有信息 **/
    private StockDetailTo detailTo;
}
