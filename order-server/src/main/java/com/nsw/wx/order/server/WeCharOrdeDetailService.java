package com.nsw.wx.order.server;


import com.github.pagehelper.PageInfo;
import com.nsw.wx.order.pojo.WeCharOrdeDetail;


import java.util.List;

public interface WeCharOrdeDetailService {
    /**
     * author Wu_kong
     * @param page
     * @param pageSize
     * @return List
     */
    PageInfo<WeCharOrdeDetail> pageSelect(int page, int pageSize);

    /**
     * author Wu_kong
     * @return List
     */
    List<WeCharOrdeDetail> finaAll();

    /**
     * author Wu_kong
     * 根据订单编号删除订单信息
     * @param OID
     * @return int
     */
    int deleteByPrimaryKey(String OID);

    /**
     * author Wu_kong
     * 根据订单编号修改订单细节信息
     * @param order
     * @return int
     */
    int updateByPrimaryOid(WeCharOrdeDetail order);

    int insert(WeCharOrdeDetail record);

}