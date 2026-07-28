package com.xunqi.gulimall.ware.vo;

import lombok.Data;

import java.util.List;

/**
 * 合并采购vo
 */

@Data
public class MergeVo {

    private Long purchaseId;

    private List<Long> items;

}
