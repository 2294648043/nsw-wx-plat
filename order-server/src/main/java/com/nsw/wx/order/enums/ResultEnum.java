package com.nsw.wx.order.enums;

import lombok.Getter;

/**
 * 张维维
 * 22018-10-20 16:18
 */
@Getter
public enum ResultEnum {
    PARAM_ERROR(1, "参数错误"),
    CART_EMPTY(2, "购物车为空"),
    ORDER_NOT_EXIST(3,"订单不存在"),
    ORDER_STATUS_ERROR(4,"订单状态错误"),
    ORDER_DETAIL_NOT_EXIST(5,"订单详情不存在"),
    ORDER_NOT_OPENID(6,"ipenid不存在"),
    ORDER_END(7,"完结订单失败"),
    PRODUCT_EMPTY(8,"商品信息信为空"),
    PEODUCT_STOCK_EMPTY(9,"存库不足"),

    TOO_MANY_PROPLE(10,"人太多了"),
    REDIS_STOCK_ERROR(11,"redis库存异常")

    ;

    private Integer code;

    private String message;

    ResultEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}
