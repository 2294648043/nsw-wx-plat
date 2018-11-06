package com.nsw.wx.order.fallbcak;

import com.nsw.wx.order.common.DecreaseStockInput;
import com.nsw.wx.order.common.WeChatProductOutput;
import com.nsw.wx.order.controller.ProductClient;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 针对商品服务，错降级处理
 */
@Component
public class ProductClientFallback implements ProductClient {


    @Override
    public List<WeChatProductOutput> listForOrder(List<String> productIdList) {
        System.out.println("feign 调用product-service findbyid 异常");
        return null;
    }

    @Override
    public void decreaseStock(List<DecreaseStockInput> decreaseStockInputList) {
        System.out.println("feign 调用product-service findbyid 异常");
    }

    @Override
    public void addStock(List<DecreaseStockInput> decreaseStockInputList) {
        System.out.println("feign 调用product-service findbyid 异常");
    }
}
