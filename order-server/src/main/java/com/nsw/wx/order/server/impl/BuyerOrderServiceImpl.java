package com.nsw.wx.order.server.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.nsw.wx.order.controller.ProductClient;
import com.nsw.wx.order.converter.OrderMaster2OrderDTOConverter;
import com.nsw.wx.order.dto.OrderDTO;
import com.nsw.wx.order.enums.OrderStatusEnum;
import com.nsw.wx.order.enums.ResultEnum;
import com.nsw.wx.order.exception.OrderException;
import com.nsw.wx.order.mapper.WeCharOrdeDetailMapper;
import com.nsw.wx.order.mapper.WeCharOrderMapper;
import com.nsw.wx.order.message.*;
import com.nsw.wx.order.pojo.WeCharOrdeDetail;
import com.nsw.wx.order.pojo.WeCharOrder;
import com.nsw.wx.order.redis.RedisLock;
import com.nsw.wx.order.redis.RedisService;
import com.nsw.wx.order.redis.WeChatProductOutputKey;
import com.nsw.wx.order.server.BuyerOrderService;
import com.nsw.wx.order.server.WebSocket;
import com.nsw.wx.order.util.KeyUtil;
import com.nsw.wx.order.common.DecreaseStockInput;
import com.nsw.wx.order.common.WeChatProductOutput;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by IntelliJ IDEA.
 *
 * @author 张维维
 * date: 2018/10/23/023 15:26
 */
@Service
public class BuyerOrderServiceImpl implements BuyerOrderService {
    @Autowired
    private  RabbitOrderSender rabbitOrderSender;
    @Autowired
    private OrderSender orderSender;
    @Autowired
    RedisService redisService;
    @Autowired
    private WebSocket webSocket;
    @Autowired
    private WeCharOrdeDetailMapper weCharOrdeDetailMapper;
    @Autowired
    private WeCharOrderMapper weCharOrderMapper;
    @Autowired
    private ProductClient productClient;
    private AmqpTemplate amqpTemplate;
    @Transactional
    public  OrderDTO create(OrderDTO orderDTO) {

        String  orderId = KeyUtil.genUniqueKey();
        List<WeChatProductOutput> productInfoList = new ArrayList<>();
        //得到商品的ID
        for (WeCharOrdeDetail weCharOrdeDetail: orderDTO.getOrderDetailList()) {
            WeChatProductOutput weChatProductOutput = redisService.get(WeChatProductOutputKey.getById, "" + weCharOrdeDetail.getProductid(), WeChatProductOutput.class);
            if (weChatProductOutput.getStock() < 0) {
                throw new OrderException(ResultEnum.PEODUCT_STOCK_EMPTY);
            }
            weChatProductOutput.setStock(weChatProductOutput.getStock() - weCharOrdeDetail.getNum());
            redisService.set(WeChatProductOutputKey.getById, "" + weChatProductOutput.getId(), weChatProductOutput);
            productInfoList.add(weChatProductOutput);
        }
        BigDecimal orderAmout = new BigDecimal(BigInteger.ZERO);
        BigDecimal orderAmoutSum = new BigDecimal(BigInteger.ZERO);
         //订单商品入库
        for (WeCharOrdeDetail weCharOrdeDetail: orderDTO.getOrderDetailList()) {
            for (WeChatProductOutput productInfo: productInfoList) {
                if (productInfo.getId().equals(weCharOrdeDetail.getProductid())) {
                    //单价*数量
                    orderAmout = productInfo.getPrice()
                            .multiply(new BigDecimal(weCharOrdeDetail.getNum()));
                    weCharOrdeDetail.setUserprice(orderAmout);

                    BeanUtils.copyProperties(productInfo, weCharOrdeDetail);
                    weCharOrdeDetail.setOid(orderId);
                    weCharOrdeDetail.setProductname(productInfo.getTitle());
                    weCharOrdeDetail.setProductprice(productInfo.getPrice());
                    weCharOrdeDetail.setPricesum(orderAmout);
                    weCharOrdeDetail.setEnable(true);
                    weCharOrdeDetail.setInputtime(new Date());
                    weCharOrdeDetail.setCarttype(null);
                    weCharOrdeDetail.setSkuid(12);
                    BigDecimal aDouble =new BigDecimal(198);
                    weCharOrdeDetail.setGroupbuyprice(aDouble);
                    weCharOrdeDetail.setOffertype(1);
                    weCharOrdeDetail.setDeposit(aDouble);
                    weCharOrdeDetail.setRent(aDouble);
                    weCharOrdeDetail.setDay(320);
                    weCharOrdeDetail.setStatus(OrderStatusEnum.DFINISHED.getCode());
                    weCharOrdeDetail.setUserid(12);
                    orderAmoutSum =  orderAmoutSum.add(orderAmout);
                    //订单详情入库
                    try {
                        int count =  weCharOrdeDetailMapper.insert(weCharOrdeDetail);
                    }catch (Exception ex){

                    }
                }
            }
        }

        //订单入库
        WeCharOrder orderMaster = new WeCharOrder();
        orderDTO.setOrderno(orderId);
        BeanUtils.copyProperties(orderDTO, orderMaster);
        System.out.println(orderDTO.getOpenid()+"=========="+orderMaster.getOpenid());
        orderMaster.setInvoicetime(new Date());
        orderMaster.setOrderstate(OrderStatusEnum.DFINISHED.getCode());
        orderMaster.setOrderno(orderId);
        orderMaster.setTotal(orderAmoutSum);
        weCharOrderMapper.insert(orderMaster);
        DecreaseStockInputReceiver decreaseStockInputReceiver = new DecreaseStockInputReceiver();
        //扣库存(调用商品服务)
        List<DecreaseStockInput> decreaseStockInputList = orderDTO.getOrderDetailList().stream()
                .map(e -> new DecreaseStockInput(e.getProductid(), e.getNum()))
                .collect(Collectors.toList());
        decreaseStockInputReceiver.setDecreaseStockInput(decreaseStockInputList);
        decreaseStockInputReceiver.setOrderId(orderId);
        try {
            rabbitOrderSender.sendOrder(decreaseStockInputReceiver);
        }catch (Exception ex){
            ex.printStackTrace();
            throw new OrderException(ResultEnum.PEODUCT_STOCK_EMPTY);
        }

        return orderDTO;
    }

    /**
     * 买家查询订单列表
     * @param buyerOpenid
     * @param page
     * @param limit
     * @return
     */
    @Override
    public PageInfo<WeCharOrder> buyerfindList(String buyerOpenid, Integer page, Integer limit) {
        PageHelper.startPage(page,limit);
        List<WeCharOrder> findlist = weCharOrderMapper.BuyerfindList(buyerOpenid);
        PageInfo<WeCharOrder> pageInfoUserList =  new PageInfo<WeCharOrder>(findlist);
        return pageInfoUserList;
    }
    /**
     * 详情
     * @param buyeropenid
     * @param orderId
     * @return
     */
    @Override
    public OrderDTO findOne(String buyeropenid, String orderId) {
        WeCharOrder weCharOrder = weCharOrderMapper.BuyerFinaAllByid(Integer.parseInt(orderId),buyeropenid);
        if (weCharOrder ==null || weCharOrder.getOrderstate()==OrderStatusEnum.CANCEL.getCode()){
            throw  new OrderException(ResultEnum.ORDER_NOT_EXIST);
        }
        //查看订单详情
        List<WeCharOrdeDetail> weCharOrdeDetails = weCharOrdeDetailMapper.findByOrderno(weCharOrder.getOrderno());
        if(CollectionUtils.isEmpty(weCharOrdeDetails)){
            throw new OrderException(ResultEnum.ORDER_DETAIL_NOT_EXIST);
        }
        OrderDTO orderDTO =  new OrderDTO();
        orderDTO.setOrderDetailList(weCharOrdeDetails);
        return orderDTO;
    }

    /**
     * 取消订单
     * @param orderId
     * @return
     */
    @Override
    public OrderDTO cancel(String orderId,String openid) {
        System.out.println(orderId+"-------"+openid);
        WeCharOrder weCharOrder = weCharOrderMapper.BuyerFinaAllByid(Integer.parseInt(orderId),openid);
        if(weCharOrder==null){
            throw  new OrderException(ResultEnum.CART_EMPTY.ORDER_NOT_EXIST);
        }

        weCharOrder.setOrderstate(OrderStatusEnum.DCANCEL.getCode());
        weCharOrderMapper.updateByPrimary(weCharOrder);
        WeCharOrdeDetail weCharOrdeDetail=new WeCharOrdeDetail();
        weCharOrdeDetail.setStatus(OrderStatusEnum.DCANCEL.getCode());//修改订单详情状态
        weCharOrdeDetail.setOid(weCharOrder.getOrderno());//订单编号
        weCharOrdeDetailMapper.updateByPrimaryOid(weCharOrdeDetail);
        OrderDTO orderDTO = OrderMaster2OrderDTOConverter.convert(weCharOrder);
        return orderDTO;
    }
    @Override
    public List<WeCharOrder> orderdetailuserid(int openid) {
        return weCharOrderMapper.orderdetailuserid(openid);
    }


    /**
     * 新增方法 查询订单详情
     * @param oid
     * @return
     */
    @Override
    public List<WeCharOrdeDetail> list(String oid) {
        return weCharOrdeDetailMapper.findByDetailOid(oid);
    }
}
