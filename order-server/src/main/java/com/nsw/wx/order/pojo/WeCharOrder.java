package com.nsw.wx.order.pojo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.nsw.wx.order.enums.OrderStatusEnum;
import com.nsw.wx.order.enums.PayStatusEnum;
import com.nsw.wx.order.util.EnumUtil;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

@Getter
@Setter
/**
 * 订单
 * 张维维
 */
@ToString
public class WeCharOrder {


    private Integer id; //ID

    private Integer enterpriseid; //企业ID

    private Integer userid;  //用户ID

    private String orderno; //订单编号

    private BigDecimal total; //总计

    private BigDecimal actualprice; //实际价格

    private String coupons; //优惠券
    /**(0, "新订单"),
   (1, "完结"),
    (2, "取消"),
   */
    private Integer orderstate; //订单状态

    private Integer paystate;   //支付状态

    private Integer deliverystate;  //交货状态

    private String fullname;    //全名

    private String phone;   //电话

    private String mphone;  //手机

    private String zipcode; //邮政编码

    private Integer deductpoint;    //扣点

    private Integer returnpoint;    //折扣点

    private BigDecimal discountamount;  //折扣金额

    private String country; //国家

    private String province;    //省

    private String city;    //城市

    private String address; //地址

    private String userremark;  //用户评论

    private String adminremark; //管理评论

    private Boolean islock; //是否锁

    private Boolean enable; //是够启用

    private Date inputtime; //输入时间

    private Integer orderid;    //订单ID

    private String payments;    //支付

    private String region;  //地区

    private Boolean isdelete;   //是否删除

    private String discountremarks; //优惠备注

    private String invoice; //发票

    private String invoiceno; //发票编号

    private String otherfield01;    //其他字段01

    private String otherfield02;    //其他字段02

    private Boolean ischeck;    //检查

    private String checkdesc;   //检查描述

    private BigDecimal freight;     //运费

    private String companyname; //公司名称

    private String logisticsno;    //物流编号

    private Integer logisticsid;    //物流ID

    private String logisticsjosn;   //物流数据

    private Date logisticstime; //物流数据

    private Integer sourceuserid;   //源标识

    private Integer invoicetype;    //发票类型

    private String invoicetel;  //发票电话

    private String invoiceaddress;  //发票地址

    private String invoicecontact;  // 发票联系

    private String invoiceregistertel;  //发票登记电话号码

    private String invoiceregisteraddress;  //发票登记地址

    private String invoicebankname; //发票银行名称

    private String invoicebankaccount;  //发票的银行账户

    private String invoicenumber;   //发票号码

    private String invoiceremark;   //发票备注

    private Integer result; //结果

    private String resultusers; //结果用户

    private Integer childenterpriseid;  //子企业标识

    private Date deliverytime;  //交货时间

    private Date invoicetime;   //发票时间

    private String paymentproofs;   //付款证明

    private String paymentmethod;   //支付方式

    private String notedescription; //注意描述

    private Date paymenttime;   //付款时间

    private Integer ordertype;  //订单类型

    private String interfacedata;   //接口数据

    private Integer groupid;    //团体ID
    private  String openid;

    private Integer initiator;  //发起者

    private BigDecimal amount;  //量

    private Integer ispayment;  //是否付款

    private BigDecimal deductibleamount;    //可扣除的金额

    private Integer groupbuyingrecordid;    //团购记录编号

    private Integer groupbuyingtype;    //团购类型

    private BigDecimal groupbuyingdiscount; //团购折扣

    private Integer groupbuystatus; //集团购买状态

    private Integer isgroupbuying;  //是否团购

    private Integer receipttype;    //收据类型

    private BigDecimal deposit; //存款

    private String returnnumber;    //返回数

    private BigDecimal membershipconsumption;   //会员消费

    private BigDecimal couponpreferential;  //优惠券优惠
    private BigDecimal defaultfreight;  //默认的运费

    @JsonIgnore
    public OrderStatusEnum getOrderStatusEnum() {
        return EnumUtil.getByCode(orderstate, OrderStatusEnum.class);
    }

    @JsonIgnore
    public PayStatusEnum getPayStatusEnum() {
        return EnumUtil.getByCode(paystate, PayStatusEnum.class);
    }

    public  WeCharOrder(String orderno,String fullname,String mphone){
        orderno = this.orderno;
        fullname = this.fullname;
        mphone = this.mphone;

    }

    public  WeCharOrder(){}
}