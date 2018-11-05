package com.nsw.wx.order.mapper;



import com.nsw.wx.order.pojo.WeCharOrder;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

public interface WeCharOrderMapper {
    /**
     * @return list
     */
    List<WeCharOrder> findList(@Param("enterpriseid") Integer enterpriseid);

    /**
     * 买家订单列表
     * @param openid
     * @return
     */
    List<WeCharOrder> BuyerfindList(String openid);
    /**
     *
     * @param record
     * @return int
     */
    int insert(WeCharOrder record);

    /**
     * 根据订单编号删除订单信息
     * @param orderNo
     * @return int
     */
    int deleteByPrimaryKey(String orderNo);

    /**
     * 通过id查询订单信息
     * @param id
     * @return
     */
   WeCharOrder finaAllByid(@Param("id") int id);

   WeCharOrder BuyerFinaAllByid(@Param("orderId") int orderId,@Param("openid") String openid);

    /**
     * 通过订单编号修改订单的状态
     * @param
     * @return int
     */
    int updateByPrimary(WeCharOrder weCharOrder);

    /**
     * 取消取消订单
     * @param weCharOrder
     * @return
     */
    int updateOrderStatus(WeCharOrder weCharOrder);


}