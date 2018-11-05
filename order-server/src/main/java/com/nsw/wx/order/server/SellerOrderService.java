package com.nsw.wx.order.server;

import com.github.pagehelper.PageInfo;
import com.nsw.wx.order.dto.OrderDTO;
import com.nsw.wx.order.pojo.WeCharOrdeDetail;
import com.nsw.wx.order.pojo.WeCharOrder;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created by 张维维
 *
 */
public interface SellerOrderService {

    /**
     * 完结订单(只能卖家操作)
     * @param orderId

     * @return
     */
    OrderDTO finish (String orderId);

    /** 卖家查询订单详情 */
    List<WeCharOrdeDetail> findOne(String orderId);

    /** 查询订单列表. */
    PageInfo <WeCharOrder> findList (Integer page, Integer limit,Integer enterpriseid );

    /** 取消订单. */
    Object cancel(String orderId);

    /**
     * 根据订单编号查询订单详情
     * @param oid
     * @return
     */
    List<WeCharOrdeDetail> selectoid(List<String> oid);

}
