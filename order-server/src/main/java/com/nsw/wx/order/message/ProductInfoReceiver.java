//package com.nsw.wx.order.message;
//
//import com.fasterxml.jackson.core.type.TypeReference;
//import com.nsw.wx.order.rabbitmq.MQConfig;
//import com.nsw.wx.order.redis.RedisService;
//import com.nsw.wx.order.redis.WeChatProductOutputKey;
//import com.nsw.wx.order.util.JsonUtil;
//import com.rabbitmq.tools.json.JSONUtil;
//import com.nsw.wx.order.common.ProductInfoOutput;
//import com.nsw.wx.order.common.WeChatProductOutput;
//import lombok.extern.slf4j.Slf4j;
//import org.bouncycastle.cms.PasswordRecipientId;
//import org.springframework.amqp.rabbit.annotation.Queue;
//import org.springframework.amqp.rabbit.annotation.RabbitListener;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.data.redis.core.StringRedisTemplate;
//import org.springframework.stereotype.Component;
//import org.springframework.transaction.annotation.Transactional;
//
//import java.util.List;
//
///**
// * Mq接收商品信息
// *
// * @author 张维维
// * date: 2018/10/21/021 20:05
// */
//@Component
//@Slf4j
//@Transactional
//public class ProductInfoReceiver {
//    @Autowired
//    private RedisService redisService;
//private  static  final  String PRODUCT_STOCK_TEMPLATE = "product_stock_%s";
//    @Autowired
//    private StringRedisTemplate stringRedisTemplate;
//
//    @RabbitListener(queuesToDeclare = @Queue(MQConfig.PRODUCT_QUEUE))
//    public void  process(String message) {
//        System.out.println("你真棒");
//        List<WeChatProductOutput> weChatProductOutputList =
//                (List<WeChatProductOutput>) JsonUtil.fromJson(message,
//                        new TypeReference<List<WeChatProductOutput>>() {
//                        });
//        System.out.println("你真棒2" + weChatProductOutputList.size());
//        //储存到redis中
//        for (WeChatProductOutput weChatProductOutput:weChatProductOutputList){
//           redisService.set(WeChatProductOutputKey.getById,""+weChatProductOutput.getId(),weChatProductOutput);
//
//        }
//    }
//
//
//
//}
