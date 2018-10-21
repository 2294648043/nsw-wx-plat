package com.nsw.wx.order.controller;

import common.DecreaseStockInput;
import common.WeChatProductOutput;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * 商品服务客户端
 */
@FeignClient(name = "PRODUCT-SERVICE")
public interface ProductClient {

    @PostMapping("/api/product/listForOrder")
    List<WeChatProductOutput> listForOrder(@RequestBody List<String> productIdList);

    @PostMapping("/api/product/decreaseStock")
    void decreaseStock(@RequestBody List<DecreaseStockInput> decreaseStockInputList);

}