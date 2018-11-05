package com.nsw.wx.order.exception;

import com.nsw.wx.order.enums.ResultEnum;

/**
 * Created by zww
 * 2018-10-25
 */
public class OrderException extends RuntimeException {

    private Integer code;

    public OrderException(Integer code, String message) {
        super(message);
        this.code = code;
    }

    public OrderException(ResultEnum resultEnum) {
        super(resultEnum.getMessage());
        this.code = resultEnum.getCode();
    }
}
