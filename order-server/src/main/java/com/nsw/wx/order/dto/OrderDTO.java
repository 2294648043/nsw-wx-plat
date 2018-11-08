package com.nsw.wx.order.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.nsw.wx.order.enums.DeliverystateEnum;
import com.nsw.wx.order.enums.OrderStatusEnum;
import com.nsw.wx.order.enums.PayStatusEnum;
import com.nsw.wx.order.pojo.WeCharOrdeDetail;
import com.nsw.wx.order.util.Date2LongSerializer;
import com.nsw.wx.order.util.EnumUtil;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 张维维
 */
@Data
public class OrderDTO  implements  Serializable  {
    private String token;//(token，查询出redis中的openid)

    private Integer id; //ID

    private Integer userid;  //用户ID

    private String orderno; //订单编号

    private BigDecimal total; //总计

    private String coupons; //优惠券
    /**(0, "新订单"),
     (1, "完结"),
     (2, "取消"),
     */
    private Integer orderstate = OrderStatusEnum.NEW.getCode(); //订单状态默认新订单

    private Integer paystate = PayStatusEnum.WAIT.getCode();   //支付状态默认待支付

    private Integer deliverystate = DeliverystateEnum.NEW.getCode();  //交货状态

    private String fullname;//全名

    private String openid = "test";

    private String phone = "10086";   //电话

    private String mphone;  //手机

    private String zipcode = "888"; //邮政编码

    private Integer deductpoint;    //扣点

    private Integer returnpoint;    //折扣点

    private BigDecimal discountamount;  //折扣金额

    private String country = "中国"; //国家

    private String province;    //省

    private String city;    //城市

    private String address; //地址

    private String userremark;  //用户评论

    private Date inputtime; //输入时间

    private Integer orderid;    //订单ID

    private String payments;    //支付

    private String region;  //地区


    private String invoice; //发票

    private String invoiceno; //发票编号


    private BigDecimal freight;     //运费


    private String logisticsno;    //物流编号

    private Integer logisticsid;    //物流ID

    private Integer invoicetype;    //发票类型

    private String invoicetel;  //发票电话

    private String invoiceaddress;  //发票地址

    private String invoicecontact;  // 发票联系

    private String invoiceregistertel;  //发票登记电话号码

    private String invoiceregisteraddress;  //发票登记地址

    private String invoicenumber;   //发票号码

    private String invoiceremark;   //发票备注

    private Integer result; //结果

    private String resultusers; //结果用户

    private Date deliverytime;  //交货时间

    private Date invoicetime;   //发票时间

    private String paymentproofs;   //付款证明

    private String paymentmethod;   //支付方式

    private String notedescription; //注意描述

    private Date paymenttime;   //付款时间

    private Integer ordertype;  //订单类型

    private String interfacedata;   //接口数据

    private Integer ispayment;  //是否付款

    private Integer groupbuyingrecordid;    //团购记录编号

    private Integer groupbuyingtype;    //团购类型

    private BigDecimal groupbuyingdiscount; //团购折扣

    private Integer isgroupbuying;  //是否团购

    private Integer receipttype;    //收据类型

    private BigDecimal deposit; //存款

    private String returnnumber;    //返回数

    private BigDecimal membershipconsumption;   //会员消费

    private BigDecimal couponpreferential;  //优惠券优惠

    private BigDecimal defaultfreight;  //默认的运费

    /** 支付状态, 默认为0未支付. */
//    private Integer paystate;
    /** 创建时间. */
//    @JsonSerialize(using = Date2LongSerializer.class)
//    private Date createTime;
//
//    /** 更新时间. */
//    @JsonSerialize(using = Date2LongSerializer.class)
    private Date updateTime;

    private List<WeCharOrdeDetail> orderDetailList;

//    @JsonIgnore
//    public OrderStatusEnum getOrderStatusEnum() {
//        return EnumUtil.getByCode(orderid, OrderStatusEnum.class);
//    }
//
//    @JsonIgnore
//    public PayStatusEnum getPayStatusEnum() {
//        return EnumUtil.getByCode( paystate, PayStatusEnum.class);
//    }
}
