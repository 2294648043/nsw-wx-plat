package com.nsw.wx.order.controller;

import com.nsw.wx.order.common.DecreaseStockInput;
import com.nsw.wx.order.common.WeChatProductOutput;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 商品服务客户端
 */
@FeignClient(name = "PRODUCT-SERVICE")
public interface ProductClient {
    /**
     * 查询商品信息
     * @param productIdList
     * @return
     */
    @PostMapping("/api/product/listForOrder")
    List<WeChatProductOutput> listForOrder(@RequestBody List<String> productIdList);

    /**
     * 减库存
     * @param decreaseStockInputList
     */
    @PostMapping("/api/product/decreaseStock")
    void decreaseStock(@RequestBody List<DecreaseStockInput> decreaseStockInputList);

    /**
     * 加库存
     * @param decreaseStockInputList
     */
    @PostMapping("/api/product/addStock")
    void addStock(@RequestBody List<DecreaseStockInput> decreaseStockInputList);

    @RequestMapping("/api/shoppingcart/cartproductid")
    Object cartproductid(@RequestParam("productid") Integer productid);

}
