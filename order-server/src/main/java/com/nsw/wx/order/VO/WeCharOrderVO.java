package com.nsw.wx.order.VO;

import com.nsw.wx.order.pojo.WeCharOrdeDetail;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Data
public class WeCharOrderVO implements Serializable {
    private Integer id; //ID
    private String orderno; //订单编号
    private BigDecimal total; //总计
    private Integer orderstate; //订单状态
    private  String openid;
    List<WeCharOrdeDetail> weCharOrdeDetailVOS;
}
