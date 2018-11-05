package com.nsw.wx.order.message;

import com.nsw.wx.order.common.DecreaseStockInput;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 *
 * @author 张维维
 * date: 2018/10/26/026 16:50
 */
@Data
public class DecreaseStockInputReceiver implements Serializable {
    private List<DecreaseStockInput> decreaseStockInput;
    private String orderId;
}
