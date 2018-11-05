package com.nsw.wx.order.mapper;



import com.nsw.wx.order.pojo.WeCharOrdeDetail;
import jdk.nashorn.internal.runtime.options.Option;

import java.util.List;
import java.util.Optional;

public interface WeCharOrdeDetailMapper {
    /**
     * 根据订单编号删除订单信息
     * @param OID
     * @return int
     */
    int deleteByPrimaryKey(String OID);

    /**
     * author Wu_kong
     * 查询订单全部信息
     * @param record
     * @return 订单信息
     */
    int insert(WeCharOrdeDetail record);

    /**
     * author Wu_kong
     * 查询订单全部信息
     * @return  int
     */
    List<WeCharOrdeDetail> finaAll();

    /**
     * author Wu_kong
     * 根据订单编号修改订单细节信息
     * @param order
     * @return int
     */
    int updateByPrimaryOid(WeCharOrdeDetail order);

    /**
     * 查询订单详情
     * @param orderno
     * @return
     */
    List<WeCharOrdeDetail> findByOrderno(String orderno);

    /**
     * 根据订单编号查询订单详情
     * @param oid
     * @return
     */
    List<WeCharOrdeDetail> selectoid(List<String> oid);

    /**
     * 查询订单详情
     * @param oid
     * @return
     */
    List<WeCharOrdeDetail>  findByDetailOid(String oid);




}