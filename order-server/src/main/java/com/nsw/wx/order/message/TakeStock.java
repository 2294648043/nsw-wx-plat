package com.nsw.wx.order.message;

import lombok.Data;

/**
 * Created by IntelliJ IDEA.
 *
 * @author 张维维
 * date: 2018/10/30/030 16:18
 */
@Data
public class TakeStock {
    private Boolean  isTake;
    private  String orderId;

    public TakeStock(Boolean isTake, String orderId) {
        this.isTake = isTake;
        this.orderId = orderId;
    }

    public TakeStock() {

    }
}
