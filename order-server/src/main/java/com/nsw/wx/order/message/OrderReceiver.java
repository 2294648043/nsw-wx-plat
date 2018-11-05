//package com.nsw.wx.order.message;
//
//import com.nsw.wx.order.dto.OrderDTO;
//import com.nsw.wx.order.enums.OrderStatusEnum;
//import com.nsw.wx.order.enums.ResultEnum;
//import com.nsw.wx.order.exception.OrderException;
//import com.nsw.wx.order.mapper.WeCharOrdeDetailMapper;
//import com.nsw.wx.order.mapper.WeCharOrderMapper;
//import com.nsw.wx.order.pojo.WeCharOrdeDetail;
//import com.nsw.wx.order.pojo.WeCharOrder;
//import com.nsw.wx.order.redis.RedisService;
//import com.nsw.wx.order.util.FastJsonConvertUtil;
//import com.nsw.wx.order.util.KeyUtil;
//import com.rabbitmq.client.Channel;
//import com.nsw.wx.order.common.DecreaseStockInput;
//import com.nsw.wx.order.common.WeChatProductOutput;
//import org.apache.commons.lang.SerializationUtils;
//import org.springframework.amqp.rabbit.annotation.*;
//import org.springframework.amqp.support.AmqpHeaders;
//import org.springframework.beans.BeanUtils;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.core.annotation.Order;
//import org.springframework.messaging.handler.annotation.Headers;
//import org.springframework.messaging.handler.annotation.Payload;
//import org.springframework.stereotype.Component;
//
//import java.math.BigDecimal;
//import java.math.BigInteger;
//import java.util.Date;
//import java.util.List;
//import java.util.Map;
//import java.util.stream.Collectors;
//
///**
// * Created by IntelliJ IDEA.
// *  创建订单消费者
// * @author 张维维
// * date: 2018/10/29/029 20:58
// */
//@Component
//public class OrderReceiver {
//    @Autowired
//    private WeCharOrdeDetailMapper weCharOrdeDetailMapper;
//    @Autowired
//    private  RabbitOrderSender rabbitOrderSender;
//    @Autowired
//    private WeCharOrderMapper weCharOrderMapper;
//
//    @RabbitListener(bindings = @QueueBinding(
//            value = @Queue(value = "queue10",
//                    durable="false"),
//            exchange = @Exchange(value = "order-exchange10",
//                    durable="false",
//                    type= "topic",
//                    ignoreDeclarationExceptions = "true"),
//            key = "order.#"
//    )
//    )
//    //@Payload com.nsw.wx.order.message.OrderMessage orderMessage
//    @RabbitHandler
//    public void onOrderMessage( String orderMessage,
//                               Channel channel,
//                               @Headers Map<String, Object> headers) throws Exception {
//        String orderId = KeyUtil.genUniqueKey();
//        OrderMessage orderMessage1 = FastJsonConvertUtil.convertJSONToObject(orderMessage, OrderMessage.class);
//        List<WeChatProductOutput> productInfoList = orderMessage1.getWeChatProductOutputList();
//        OrderDTO orderDTO = orderMessage1.getOrderDTO();
//
//        BigDecimal orderAmout = new BigDecimal(BigInteger.ZERO);
//        BigDecimal orderAmoutSum = new BigDecimal(BigInteger.ZERO);
//        //订单商品入库
//        for (WeCharOrdeDetail weCharOrdeDetail: orderDTO.getOrderDetailList()) {
//            for (WeChatProductOutput productInfo: productInfoList) {
//                if (productInfo.getId().equals(weCharOrdeDetail.getProductid())) {
//                    //单价*数量
//                    orderAmout = productInfo.getPrice()
//                            .multiply(new BigDecimal(weCharOrdeDetail.getNum()));
//                    weCharOrdeDetail.setUserprice(orderAmout);
//
//                    BeanUtils.copyProperties(productInfo, weCharOrdeDetail);
//                    weCharOrdeDetail.setOid(orderId);
//                    weCharOrdeDetail.setProductname(productInfo.getTitle());
//                    weCharOrdeDetail.setProductprice(productInfo.getPrice());
//                    weCharOrdeDetail.setPricesum(orderAmout);
//                    weCharOrdeDetail.setEnable(true);
//                    weCharOrdeDetail.setInputtime(new Date());
//                    weCharOrdeDetail.setCarttype(null);
//                    weCharOrdeDetail.setSkuid(12);
//                    BigDecimal aDouble =new BigDecimal(198);
//                    weCharOrdeDetail.setGroupbuyprice(aDouble);
//                    weCharOrdeDetail.setOffertype(1);
//                    weCharOrdeDetail.setDeposit(aDouble);
//                    weCharOrdeDetail.setRent(aDouble);
//                    weCharOrdeDetail.setDay(320);
//                    weCharOrdeDetail.setStatus(OrderStatusEnum.DFINISHED.getCode());
//                    weCharOrdeDetail.setUserid(12);
//                    orderAmoutSum =  orderAmoutSum.add(orderAmout);
//                    //订单详情入库
//                  int count =  weCharOrdeDetailMapper.insert(weCharOrdeDetail);
//                    System.out.println(weCharOrdeDetail.getStatus()+"------"+count);
//                }
//            }
//        }
//        //订单入库
//        WeCharOrder orderMaster = new WeCharOrder();
//        orderDTO.setOrderno(orderId);
//        BeanUtils.copyProperties(orderDTO, orderMaster);
//        orderMaster.setInvoicetime(new Date());
//        orderMaster.setOrderstate(OrderStatusEnum.DFINISHED.getCode());
//        orderMaster.setOrderno(orderId);
//        orderMaster.setTotal(orderAmoutSum);
//        weCharOrderMapper.insert(orderMaster);
//        Long deliveryTag = (Long) headers.get(AmqpHeaders.DELIVERY_TAG);
//        channel.basicAck(deliveryTag, false);
//    }
//
//}
