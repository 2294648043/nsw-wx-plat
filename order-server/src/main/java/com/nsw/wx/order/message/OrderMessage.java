package com.nsw.wx.order.message;

import com.nsw.wx.order.dto.OrderDTO;
import com.nsw.wx.order.common.WeChatProductOutput;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;

/**
 *
 *
 * @author 张维维
 * date: 2018/10/26/026 14:53
 */
@Getter
@Setter
@ToString
public class OrderMessage  implements Serializable {
    private List<WeChatProductOutput> weChatProductOutputList;

      private OrderDTO orderDTO;

    public OrderMessage() {

    }
}
