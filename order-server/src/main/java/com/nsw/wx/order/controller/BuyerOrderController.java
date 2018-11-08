package com.nsw.wx.order.controller;

import com.github.pagehelper.PageInfo;
import com.nsw.wx.order.VO.ResultVO;
import com.nsw.wx.order.VO.WeCharOrderVO;
import com.nsw.wx.order.converter.OrderForm2OrderDTOConverter;
import com.nsw.wx.order.converter.OrderMaster2OrderDTOConverter;
import com.nsw.wx.order.dto.OrderDTO;
import com.nsw.wx.order.enums.ResultEnum;
import com.nsw.wx.order.exception.OrderException;
import com.nsw.wx.order.form.OrderForm;
import com.nsw.wx.order.pojo.WeCharOrdeDetail;
import com.nsw.wx.order.pojo.WeCharOrder;
import com.nsw.wx.order.redis.RedisService;
import com.nsw.wx.order.server.BuyerOrderService;
import com.nsw.wx.order.VO.ResultVOUtil;
import com.nsw.wx.order.server.SellerOrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 买家订单
 *
 * @author 张维维
 * date: 2018/10/23/023 15:13
 */
@RestController
@RequestMapping("/order/buyer")
@Slf4j
public class BuyerOrderController {
    @Autowired
    private BuyerOrderService buyerOrderService;

    @Autowired
    private SellerOrderService orderService;

    @Autowired
    private RedisService redisService;
    /**
     * 1. 参数检验
     * 2. 查询商品信息(调用商品服务)
     * 3. 计算总价
     * 4. 扣库存(调用商品服务)
     * 5. 订单入库
     */
    /**
     * 1. 参数检验
     * 2. 查询商品信息(调用商品服务)
     * 3. 计算总价
     * 4. 扣库存(调用商品服务)
     * 5. 订单入库
     */
    @PostMapping("/create")
    public ResultVO<Map<String, String>> create(@Valid OrderForm orderForm,
                                                BindingResult bindingResult,
                                                HttpServletResponse response, HttpServletRequest request){
        //response.setHeader("Access-Control-Allow-Origin", "*");
        System.out.println("orderForm"+orderForm+request.getParameter("name"));
        if (bindingResult.hasErrors()) {
            log.error("【创建订单】参数不正确, orderForm={}", orderForm);
            throw new OrderException(ResultEnum.PARAM_ERROR.getCode(),
                    bindingResult.getFieldError().getDefaultMessage());
        }
        OrderDTO orderDTO = OrderForm2OrderDTOConverter.convert(orderForm);
        if (CollectionUtils.isEmpty(orderDTO.getOrderDetailList())) {
            log.error("【创建订单】购物车信息为空");
            throw new OrderException(ResultEnum.CART_EMPTY);
        }

        OrderDTO result = buyerOrderService.create(orderDTO);

        Map<String, String> map = new HashMap<>();
        map.put("orderId", result.getOrderno());
        return ResultVOUtil.success(map);
    }
    /**
     * 查询订单列表
     * @param response
     * @param page
     * @param limit
     * @return
     */
    @GetMapping("/list")
    public ResultVO list(HttpServletResponse response, @RequestParam(value = "page") Integer page,
                       @RequestParam(value = "limit") Integer limit,
                       @RequestParam(value = "openid") String openid) {
        response.setHeader("Access-Control-Allow-Origin", "*");     if (StringUtils.isEmpty(openid)) {
            log.error("【查询订单列表】openid为空");
            throw new OrderException(ResultEnum.ORDER_NOT_OPENID);
        }
        PageInfo<WeCharOrder> pageInfoList = buyerOrderService.buyerfindList(openid,page, limit);
        long count=pageInfoList.getTotal();
        List<OrderDTO> orderDTOList = OrderMaster2OrderDTOConverter.convert(pageInfoList.getList());
        return ResultVOUtil.success(orderDTOList,count);
    }
    //订单详情
    @RequestMapping("/detail")
    public ResultVO<OrderDTO> detail( @RequestParam(value ="openid") String openid,
                                     @RequestParam("orderId") String orderId) {
       return ResultVOUtil.success(buyerOrderService.findOne(openid, orderId));
    }
    //取消订单
    @PostMapping("/cancel")
    public ResultVO cancel(@RequestParam("token") String token,
                           @RequestParam("orderId") String orderId,HttpServletResponse response) {
        //response.setHeader("Access-Control-Allow-Origin", "*");
        String openid=redisService.get(token);
        buyerOrderService.cancel(orderId,openid);
        return ResultVOUtil.success();
    }

    /**
     * 根据用户id和订单状态查询订
     * 单编号再根据编号查询订单详情
     * @param response
     * @param
     * @param
     * @return
     */
    @RequestMapping("orderdetailuserid")
    public Object orderdetailuserid(HttpServletResponse response,
                                    @RequestParam("token") String token) {
        //response.setHeader("Access-Control-Allow-Origin", "*");
        String openid=redisService.get(token);
        System.out.println(token);
        List<WeCharOrder> weCharOrder = buyerOrderService.orderdetailuserid(openid);
        //2. 获取weCharOrder的订单编号
        List<String> categoryOIDList = weCharOrder.stream()
                .map(WeCharOrder::getOrderno)
                .collect(Collectors.toList());
        System.out.println(categoryOIDList);
        List<WeCharOrdeDetail> weCharOrdeDetail = (List<WeCharOrdeDetail>) orderService.selectoid(categoryOIDList);
        System.out.println(weCharOrdeDetail);
        List<WeCharOrderVO> weCharOrderList =new ArrayList<>();
        for (WeCharOrder wecharorder : weCharOrder) {
            WeCharOrderVO weCharOrder1=new WeCharOrderVO();
            weCharOrder1.setId(wecharorder.getId());
            weCharOrder1.setOrderno(wecharorder.getOrderno());
            weCharOrder1.setTotal(wecharorder.getTotal());
            weCharOrder1.setOpenid(wecharorder.getOpenid());
            weCharOrder1.setOrderstate(wecharorder.getOrderstate());
            List<WeCharOrdeDetail> weCharOrdeDetailX = new ArrayList<>();
            for (WeCharOrdeDetail productInfo : weCharOrdeDetail) {
                if (productInfo.getStatus() == 0) {
                    productInfo.setOrderstatus("新订单");
                } else if (productInfo.getStatus() == 1) {
                    productInfo.setOrderstatus("排队");
                } else if (productInfo.getStatus() == 2) {
                    productInfo.setOrderstatus("完结");
                } else if (productInfo.getStatus() == 3) {
                    productInfo.setOrderstatus("待取消");
                } else if (productInfo.getStatus() == 4) {
                    productInfo.setOrderstatus("已取消");
                }
                WeCharOrdeDetail weCharOrdeDetail1 = new WeCharOrdeDetail();
                BeanUtils.copyProperties(productInfo, weCharOrdeDetail1);
                weCharOrdeDetailX.add(weCharOrdeDetail1);
            }
            weCharOrder1.setWeCharOrdeDetailVOS(weCharOrdeDetailX);
            weCharOrderList.add(weCharOrder1);
        }
        return weCharOrderList;
    }


    @RequestMapping("/detailoid")
    public List<WeCharOrdeDetail> detail(HttpServletResponse response,String oid) {
        response.setHeader("Access-Control-Allow-Origin", "*");
        List<WeCharOrdeDetail> list  = buyerOrderService.list(oid);
        return list;
    }
}
