package com.nsw.wx.order.pojo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.sun.xml.internal.ws.developer.Serialization;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;


/**
 * 订单详情
 */
@Data
public class WeCharOrdeDetail implements Serializable {
    private Integer id; //ID

    //此字段表中无
    private String orderstatus;//筛选（状态）显示到前端页面

    private Integer userid; //用户ID

    private String oid; //OID

    private String productid; //产品ID

    private String productname; //产品名称

    private BigDecimal productprice; //产品价格

    private BigDecimal userprice; //用户价格

    private Integer num; //数量

    private BigDecimal pricesum; //价格总和

    private String shortdesc; //短描述

    private String attribute; //属性

    private String attributekeyvalue; //键值属性

    private Boolean enable; //是否启用
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date inputtime; //输入时间

    private Integer orderid; //订单ID

    private Integer integral; //积分

    private String carttype; //车类型

    private Integer skuid; //SkuID

    private BigDecimal groupbuyprice; //团购价格

    private Integer offertype; //提供类型

    private BigDecimal deposit; //存款

    private BigDecimal rent; //租金

    private Integer day; //天数

    private Integer status; //状态

}