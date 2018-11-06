package com.nsw.wx.order.server.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.nsw.wx.order.controller.ProductClient;
import com.nsw.wx.order.dto.OrderDTO;
import com.nsw.wx.order.enums.OrderStatusEnum;
import com.nsw.wx.order.enums.ResultEnum;
import com.nsw.wx.order.exception.OrderException;
import com.nsw.wx.order.mapper.WeCharOrdeDetailMapper;
import com.nsw.wx.order.mapper.WeCharOrderMapper;
import com.nsw.wx.order.pojo.WeCharOrdeDetail;
import com.nsw.wx.order.pojo.WeCharOrder;
import com.nsw.wx.order.redis.RedisService;
import com.nsw.wx.order.server.SellerOrderService;
import com.nsw.wx.order.common.DecreaseStockInput;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by 张维维
 * 2018-10-19
 */
@Service
public class SellerOrderServiceImpl implements SellerOrderService {

    @Autowired
    private RedisService redisService;
    @Autowired
    private WeCharOrdeDetailMapper weCharOrdeDetailMapper;
    @Autowired
    private WeCharOrderMapper weCharOrderMapper;
    @Autowired
    private ProductClient productClient;

    @Override
    public OrderDTO finish(String orderId) {
        WeCharOrder weCharOrder = weCharOrderMapper.finaAllByid(Integer.parseInt(orderId));
        System.out.println(weCharOrder.getOrderstate());
        if (weCharOrder == null || !(weCharOrder.getOrderstate().equals(OrderStatusEnum.NEW.getCode()))) {

            throw new OrderException(ResultEnum.ORDER_STATUS_ERROR);
        }

        //查看订单详情
        List<WeCharOrdeDetail> weCharOrdeDetails = weCharOrdeDetailMapper.findByOrderno(weCharOrder.getOrderno());
        if (CollectionUtils.isEmpty(weCharOrdeDetails)) {
            throw new OrderException(ResultEnum.ORDER_DETAIL_NOT_EXIST);
        }
        //订单状态转换

        weCharOrder.setOrderstate(OrderStatusEnum.FINISHED.getCode());
        int count = weCharOrderMapper.updateOrderStatus(weCharOrder);
        System.out.println();
        OrderDTO orderDTO = new OrderDTO();
        BeanUtils.copyProperties(weCharOrder, orderDTO);
        orderDTO.setOrderDetailList(weCharOrdeDetails);

        return orderDTO;
    }

    @Override
    public List<WeCharOrdeDetail> findOne(String orderId) {
        WeCharOrder weCharOrder = weCharOrderMapper.finaAllByid(Integer.parseInt(orderId));
        if (weCharOrder == null) {
            throw new OrderException(ResultEnum.CART_EMPTY.ORDER_NOT_EXIST);
        }
        //查看订单详情
        List<WeCharOrdeDetail> weCharOrdeDetails = weCharOrdeDetailMapper.findByOrderno(weCharOrder.getOrderno());
        if (CollectionUtils.isEmpty(weCharOrdeDetails)) {
            throw new OrderException(ResultEnum.ORDER_DETAIL_NOT_EXIST);
        }

        return weCharOrdeDetails;
    }


    @Override
    public PageInfo<WeCharOrder> findList(Integer page, Integer limit) {
        PageHelper.startPage(page, limit);
        List<WeCharOrder> findlist = weCharOrderMapper.findList();
        System.out.println("==" + findlist);
        PageInfo<WeCharOrder> pageInfoUserList = new PageInfo<WeCharOrder>(findlist);

        return pageInfoUserList;
    }

    @Override
    @Transactional
    public Object cancel(String orderId) {
        //查询订单
        //判断订单状态
        //修改订单状态
        //加库存
        //退款
        //查询订单
        WeCharOrder weCharOrder = weCharOrderMapper.finaAllByid(Integer.parseInt(orderId));
        if (weCharOrder == null) {
            throw new OrderException(ResultEnum.CART_EMPTY.ORDER_NOT_EXIST);
        }
        //判断订单转态

        if (!weCharOrder.getOrderstate().equals(OrderStatusEnum.DCANCEL.getCode())) {
            throw new OrderException(ResultEnum.ORDER_STATUS_ERROR);
        }
        //修改订单状态
        weCharOrder.setOrderstate(OrderStatusEnum.CANCEL.getCode());
        int count = weCharOrderMapper.updateOrderStatus(weCharOrder);
        List<WeCharOrdeDetail> weCharOrdeDetails = weCharOrdeDetailMapper.findByOrderno(weCharOrder.getOrderno());

        List<DecreaseStockInput> decreaseStockInputList = weCharOrdeDetails.stream()
                .map(e -> new DecreaseStockInput(e.getProductid(), e.getNum()))
                .collect(Collectors.toList());
        System.out.println("===="+decreaseStockInputList+"-");
        try{
            productClient.addStock(decreaseStockInputList);
        }catch (Exception ex){
            weCharOrder.setOrderstate(OrderStatusEnum.DCANCEL.getCode());
            weCharOrderMapper.updateOrderStatus(weCharOrder);
            throw new OrderException(ResultEnum.ORDER_END);
        }

        return count;
    }






}
