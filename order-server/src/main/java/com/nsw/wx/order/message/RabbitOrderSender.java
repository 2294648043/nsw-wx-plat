package com.nsw.wx.order.message;

import com.nsw.wx.order.enums.ResultEnum;
import com.nsw.wx.order.exception.OrderException;
import com.nsw.wx.order.mapper.WeCharOrderMapper;
import com.nsw.wx.order.util.FastJsonConvertUtil;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.core.RabbitTemplate.ConfirmCallback;
import org.springframework.amqp.rabbit.support.CorrelationData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 生产者
 */
@Component
public class RabbitOrderSender {

	//自动注入RabbitTemplate模板类
	@Autowired
	private RabbitTemplate rabbitTemplate;  
	
	@Autowired
	private WeCharOrderMapper weCharOrderMapper;
	
	final ConfirmCallback confirmCallback = new ConfirmCallback() {
		@Override
		public void confirm(CorrelationData correlationData, boolean ack, String cause) {

			if(ack){


			} else {

				throw  new OrderException(ResultEnum.ORDER_NOT_OPENID);
				//失败则进行具体的后续操作:重试 或者补偿等手段

			}
		}
	};
	
	//发送消息方法调用: 构建自定义对象消息
	public void sendOrder(DecreaseStockInputReceiver decreaseStockInputReceiver) throws Exception {
		rabbitTemplate.setConfirmCallback(confirmCallback);
		System.out.println(decreaseStockInputReceiver.getOrderId()+"*****");
		rabbitTemplate.convertAndSend("test-order20", "test.ABC", FastJsonConvertUtil.convertObjectToJSON(decreaseStockInputReceiver));
	}
	
}
