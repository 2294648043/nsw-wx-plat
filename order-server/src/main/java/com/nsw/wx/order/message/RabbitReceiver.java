package com.nsw.wx.order.message;


import com.nsw.wx.order.enums.OrderStatusEnum;
import com.nsw.wx.order.mapper.WeCharOrderMapper;
import com.nsw.wx.order.pojo.WeCharOrder;
import com.nsw.wx.order.server.impl.BuyerOrderServiceImpl;
import com.nsw.wx.order.util.FastJsonConvertUtil;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 消费端
 */
@Component
public class RabbitReceiver {
	@Autowired
	private  RabbitOrderSender rabbitOrderSender;
	@Autowired
	private WeCharOrderMapper weCharOrderMapper;

	@RabbitListener(bindings = @QueueBinding(
			value = @Queue(value = "queue1",
					durable="false"),
			exchange = @Exchange(value = "order-exchange",
					durable="false",
					type= "topic",
					ignoreDeclarationExceptions = "true"),
			key = "order.#"
	)
	)
	@RabbitHandler
	public void onOrderMessage(String takeStock,
							   Channel channel,
							   @Headers Map<String, Object> headers) throws Exception {
		TakeStock takeStock1 =FastJsonConvertUtil.convertJSONToObject(takeStock,TakeStock.class);
		if (takeStock1.getIsTake().equals(true)){
			System.out.println(takeStock1.getOrderId());
			WeCharOrder weCharOrder = new WeCharOrder();
			weCharOrder.setOrderno(takeStock1.getOrderId());
			weCharOrder.setOrderstate(OrderStatusEnum.NEW.getCode());

			String OrderNo=takeStock1.getOrderId();
            int count  = weCharOrderMapper.updateOrderSta(weCharOrder);
			System.out.println("秀给订单");
			Long deliveryTag = (Long)headers.get(AmqpHeaders.DELIVERY_TAG);
			channel.basicAck(deliveryTag, false);
		}else{
			System.out.println("错误");
			WeCharOrder weCharOrder = new WeCharOrder();
			weCharOrder.setOrderno(takeStock1.getOrderId());
			weCharOrder.setOrderstate(OrderStatusEnum.CANCEL.getCode());
			int count  = weCharOrderMapper.updateOrderSta(weCharOrder);
		}
		Long deliveryTag = (Long)headers.get(AmqpHeaders.DELIVERY_TAG);
		channel.basicAck(deliveryTag, false);
	}

}
