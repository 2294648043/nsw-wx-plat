package com.nsw.wx.order.controller;

import com.github.pagehelper.PageInfo;
import com.nsw.wx.order.VO.ResultVO;
import com.nsw.wx.order.converter.OrderMaster2OrderDTOConverter;
import com.nsw.wx.order.dto.OrderDTO;
import com.nsw.wx.order.enums.ResultEnum;
import com.nsw.wx.order.exception.OrderException;
import com.nsw.wx.order.pojo.WeCharOrdeDetail;
import com.nsw.wx.order.pojo.WeCharOrder;
import com.nsw.wx.order.server.SellerOrderService;
import com.nsw.wx.order.util.JsonData;
import com.nsw.wx.order.VO.ResultVOUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.List;

/**
 * 卖端家订单
 * Created by 张维维
 * 2018-10-19 16:36
 */
@RestController
@RequestMapping("/order/sell")
@Slf4j
public class SellerOrderController {

    @Autowired
    private SellerOrderService orderService;




//    private ResultVO<Map<String, String>> saveOrderFail(@Valid OrderForm orderForm,
//                                                BindingResult bindingResult) {
//        System.out.println("人数太多了");
//
//        return ResultVOUtil.success("人数太多了");
//    }

    /**
     * 完结订单
     * @param orderId
     * @return
     */
    @PostMapping("/finish")
    public ResultVO<OrderDTO> finish(
                @RequestParam("orderId") String orderId) {
       OrderDTO orderDTO = orderService.finish(orderId);
       if (orderDTO ==null){
           throw  new OrderException(ResultEnum.ORDER_END);
       }
        return ResultVOUtil.success();
    }

    /**
     * 查询所有订单 可根据
     * @param response
     * @param page
     * @param limit
     * @return
     */
    @RequestMapping("/list")
    public Object list(HttpServletResponse response
                ,@RequestParam(value = "page") Integer page
                ,@RequestParam(value = "limit") Integer limit
                ,@RequestParam(value = "enterpriseid",required = false) String enterpriseid) {
        response.setHeader("Access-Control-Allow-Origin", "*");
        PageInfo<WeCharOrder> pageInfoList = orderService.findList(page, limit,Integer.parseInt(enterpriseid));
        List<OrderDTO> orderDTOList = OrderMaster2OrderDTOConverter.convert(pageInfoList.getList());
        long count=pageInfoList.getTotal();
        System.out.println(count);
        return JsonData.buildSuccess(count,orderDTOList);
    }

    //订单详情
    @PostMapping("/detail")
    public Object detail(@RequestParam("orderId") String orderId,HttpServletResponse response) {
        response.setHeader("Access-Control-Allow-Origin", "*");
        List <WeCharOrdeDetail> weCharOrdeDetailList = orderService.findOne( orderId);
     return JsonData.buildSuccess(weCharOrdeDetailList);

    }
    //取消订单
    @RequestMapping("/cancel")
    public Object cancel(@RequestParam("orderId") String orderId,HttpServletResponse response) {
        response.setHeader("Access-Control-Allow-Origin", "*");
        int count = (int) orderService.cancel(orderId);
        System.out.println(count);
        return count;
    }
}


