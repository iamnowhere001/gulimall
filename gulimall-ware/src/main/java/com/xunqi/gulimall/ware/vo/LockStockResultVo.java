package com.xunqi.gulimall.ware.vo;

import lombok.Data;

/**
 * 锁定库存结果vo
 */

@Data
public class LockStockResultVo {

    private Long skuId;

    private Integer num;

    /** 是否锁定成功 **/
    private Boolean locked;

}
