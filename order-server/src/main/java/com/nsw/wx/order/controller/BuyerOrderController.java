package com.nsw.wx.order.controller;

import com.github.pagehelper.PageInfo;
import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;
import com.nsw.wx.order.VO.ResultVO;
import com.nsw.wx.order.converter.OrderForm2OrderDTOConverter;
import com.nsw.wx.order.converter.OrderMaster2OrderDTOConverter;
import com.nsw.wx.order.dto.OrderDTO;
import com.nsw.wx.order.enums.ResultEnum;
import com.nsw.wx.order.exception.OrderException;
import com.nsw.wx.order.form.OrderForm;
import com.nsw.wx.order.pojo.WeCharOrder;
import com.nsw.wx.order.server.BuyerOrderService;
import com.nsw.wx.order.VO.ResultVOUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.CollectionUtils;

import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

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
    private StringRedisTemplate redisTemplate;
    @Autowired
    private BuyerOrderService buyerOrderService;
    /**
     * 1. 参数检验
     * 2. 查询商品信息(调用商品服务)
     * 3. 计算总价
     * 4. 扣库存(调用商品服务)
     * 5. 订单入库
     */
    @PostMapping("/create")
     @HystrixCommand(fallbackMethod = "saveOrderFail")
    public ResultVO<Map<String, String>> create(@Valid OrderForm orderForm,
                                                BindingResult bindingResult,
                                                HttpServletRequest request) {
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
     * 下单降级
     * @param orderForm
     * @param bindingResult
     * @param request
     * @return
     */
   private ResultVO <Map<String, String>>saveOrderFail (@Valid OrderForm orderForm,
                                 BindingResult bindingResult, HttpServletRequest request){
        //监控报警
        String saveOrderKye = "save-order";
        String sendValue = redisTemplate.opsForValue().get(saveOrderKye);
        final String ip = request.getRemoteAddr();
        new Thread( ()->{
            if (StringUtils.isBlank(sendValue)) {
                System.out.println("紧急短信，用户下单失败，请离开查找原因,ip地址是="+ip);
                //发送一个http请求，调用短信服务 TODO
                redisTemplate.opsForValue().set(saveOrderKye, "save-order-fail", 20, TimeUnit.SECONDS);

            }else{
                System.out.println("已经发送过短信，20秒内不重复发送");
            }
        }).start();


        Map<String, Object> msg = new HashMap<>();
        msg.put("code", -1);
        msg.put("msg", "抢购人数太多，您被挤出来了，稍等重试");
        return ResultVOUtil.success(msg);
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
    @PostMapping("/detail")
    public ResultVO<OrderDTO> detail(@RequestParam("openid") String openid,
                                     @RequestParam("orderId") String orderId) {
       return ResultVOUtil.success(buyerOrderService.findOne(openid, orderId));
    }
    //取消订单
    @PostMapping("/cancel")
    public ResultVO cancel(@RequestParam("openid") String openid,
                           @RequestParam("orderId") String orderId) {
       buyerOrderService.cancel(orderId,openid);
        return ResultVOUtil.success();
    }
}
