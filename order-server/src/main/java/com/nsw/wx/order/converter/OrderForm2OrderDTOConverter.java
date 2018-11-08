package com.nsw.wx.order.converter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nsw.wx.order.dto.OrderDTO;
import com.nsw.wx.order.enums.ResultEnum;
import com.nsw.wx.order.exception.OrderException;
import com.nsw.wx.order.form.OrderForm;
import com.nsw.wx.order.pojo.WeCharOrdeDetail;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

/**
 * 张维维
 * 2018-10-23 17:38
 */
@Slf4j
public class OrderForm2OrderDTOConverter {

    public static OrderDTO convert(OrderForm orderForm) {
        Gson gson = new Gson();
        OrderDTO orderDTO = new OrderDTO();
        orderDTO.setFullname(orderForm.getName());
        orderDTO.setMphone(orderForm.getPhone());
        orderDTO.setAddress(orderForm.getAddress());
        orderDTO.setToken(orderForm.getToken());
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
