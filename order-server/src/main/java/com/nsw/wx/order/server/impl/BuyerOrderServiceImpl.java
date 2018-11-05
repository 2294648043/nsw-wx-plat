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
import com.nsw.wx.order.pojo.WeCharOrdeDetail;
import com.nsw.wx.order.pojo.WeCharOrder;
import com.nsw.wx.order.server.BuyerOrderService;
import com.nsw.wx.order.server.WebSocket;
import com.nsw.wx.order.util.KeyUtil;
import common.DecreaseStockInput;
import common.WeChatProductOutput;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.math.BigInteger;
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
    private WebSocket webSocket;
    @Autowired
    private WeCharOrdeDetailMapper weCharOrdeDetailMapper;
    @Autowired
    private WeCharOrderMapper weCharOrderMapper;
    @Autowired
    private ProductClient productClient;
    @Transactional
    public OrderDTO create(OrderDTO orderDTO) {

        String orderId = KeyUtil.genUniqueKey();
        //查询商品信息(调用商品服务) 获取商品信息
        List<String> productIdList = orderDTO.getOrderDetailList().stream()
                .map(WeCharOrdeDetail::getProductid)
                .collect(Collectors.toList());
        //得到商品信息
        System.out.println();
        List<WeChatProductOutput> productInfoList = productClient.listForOrder(productIdList);
        System.out.println("---------------"+productInfoList);
        //计算总价
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
                    System.out.println("productInfo"+productInfo.getOrderid());
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
                    weCharOrdeDetail.setStatus(1212);
                    weCharOrdeDetail.setUserid(12);
                    orderAmoutSum =  orderAmoutSum.add(orderAmout);
                    //订单详情入库
                    weCharOrdeDetailMapper.insert(weCharOrdeDetail);
                }
            }
        }
        //扣库存(调用商品服务)
        List<DecreaseStockInput> decreaseStockInputList = orderDTO.getOrderDetailList().stream()
                .map(e -> new DecreaseStockInput(e.getProductid(), e.getNum()))
                .collect(Collectors.toList());
        productClient.decreaseStock(decreaseStockInputList);

        //订单入库
        WeCharOrder orderMaster = new WeCharOrder();
        orderDTO.setOrderno(orderId);
        BeanUtils.copyProperties(orderDTO, orderMaster);
        System.out.println(orderDTO.getOpenid()+"=========="+orderMaster.getOpenid());
        orderMaster.setInvoicetime(new Date());
        orderMaster.setOrderno(orderId);
        orderMaster.setTotal(orderAmoutSum);
        weCharOrderMapper.insert(orderMaster);
        webSocket.sendMessage(orderDTO.getOrderno());
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
        if (weCharOrder ==null){
            throw  new OrderException(ResultEnum.CART_EMPTY.ORDER_NOT_EXIST);
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
        System.out.println(weCharOrder.getOrderstate());
        OrderDTO orderDTO = OrderMaster2OrderDTOConverter.convert(weCharOrder);
        return orderDTO;
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
