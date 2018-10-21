package com.nsw.wx.order.converter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nsw.wx.order.com.nsw.wx.order.dto.OrderDTO;
import com.nsw.wx.order.enums.ResultEnum;
import com.nsw.wx.order.exception.OrderException;
import com.nsw.wx.order.form.OrderForm;
import com.nsw.wx.order.pojo.WeCharOrdeDetail;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 廖师兄
 * 2017-12-10 17:38
 */
@Slf4j
public class OrderForm2OrderDTOConverter {

    public static OrderDTO convert(OrderForm orderForm) {
        Gson gson = new Gson();
        OrderDTO orderDTO = new OrderDTO();
        orderDTO.setBuyerName(orderForm.getName());
        orderDTO.setBuyerPhone(orderForm.getPhone());
        orderDTO.setBuyerAddress(orderForm.getAddress());
        orderDTO.setBuyerOpenid(orderForm.getOpenid());
        System.out.println("orderDTO"+orderDTO);


        List<WeCharOrdeDetail> orderDetailList = new ArrayList<>();
        try {
            orderDetailList = gson.fromJson(orderForm.getItems(),
                    new TypeToken<List<WeCharOrdeDetail>>() {
                    }.getType());
        } catch (Exception e) {
            log.error("【json转换】错误, string={}", orderForm.getItems());
            throw new OrderException(ResultEnum.PARAM_ERROR);
        }
        orderDTO.setOrderDetailList(orderDetailList);

        return orderDTO;
    }
}