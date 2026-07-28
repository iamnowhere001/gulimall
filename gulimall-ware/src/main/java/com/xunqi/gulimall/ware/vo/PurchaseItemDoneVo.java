package com.xunqi.gulimall.ware.vo;

import lombok.Data;

/**
 * 采购项完成vo
 */

@Data
public class PurchaseItemDoneVo {

    private Long itemId;

    private Integer status;

    private String reason;

}
