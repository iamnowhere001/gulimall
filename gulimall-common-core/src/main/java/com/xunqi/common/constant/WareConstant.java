package com.xunqi.common.constant;

/**
 * 仓储服务常量与枚举。
 * 包含：
 *  - PurchaseStatusEnum：采购单状态（新建 / 已分配 / 已领取 / 已完成 / 有异常）
 *  - PurchaseDetailStatusEnum：采购需求（明细）状态（新建 / 已分配 / 采购中 / 已完成 / 失败）
 * 用于库存采购业务流程中的状态流转。
 */
public class WareConstant {

    public enum PurchaseStatusEnum {
        CREATED(0,"新建"),
        ASSIGNED(1,"已分配"),
        RECEIVE(2,"已领取"),
        FINISH(3,"已完成"),
        HASERROR(4,"有异常"),

        ;

        private int code;

        private String msg;

        public int getCode() {
            return code;
        }

        public String getMsg() {
            return msg;
        }

        PurchaseStatusEnum(int code, String msg) {
            this.code = code;
            this.msg = msg;
        }

    }

    public enum PurchaseDetailStatusEnum {
        CREATED(0,"新建"),
        ASSIGNED(1,"已分配"),
        BUYING(2,"正在采购"),
        FINISH(3,"已完成"),
        HASERROR(4,"采购失败"),

        ;

        private int code;

        private String msg;

        public int getCode() {
            return code;
        }

        public String getMsg() {
            return msg;
        }

        PurchaseDetailStatusEnum(int code, String msg) {
            this.code = code;
            this.msg = msg;
        }

    }

}
