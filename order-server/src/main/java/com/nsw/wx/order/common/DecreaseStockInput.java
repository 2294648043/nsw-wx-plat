package com.nsw.wx.order.common;

import lombok.Data;

import java.io.Serializable;

/**
 * 购物车
 *
 * @author 张维维
 * description:
 * path: nsw-wx-product-com.nsw.wx.product.com.nsw.wx.order.common-DecreaseStockInput
 * date: 2018/10/18/018 17:40
 * version: 02.06
 * To change this template use File | Settings | File Templates.
 */
    @Data
    public class DecreaseStockInput  implements Serializable {

        private String productId;

        private Integer num;

        public DecreaseStockInput() {
        }

        public DecreaseStockInput(String productId, Integer num) {
            this.productId = productId;
            this.num = num;
        }

}
