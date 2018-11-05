package com.nsw.wx.order.redis;

/**
 * Created by IntelliJ IDEA.
 *
 * @author 张维维
 * date: 2018/10/25/025 19:07
 */

public class WeChatProductOutputKey extends BasePrefix {

    public WeChatProductOutputKey(String prefix) {
        super(prefix);
    }
    public static WeChatProductOutputKey getById = new WeChatProductOutputKey("id");
}
