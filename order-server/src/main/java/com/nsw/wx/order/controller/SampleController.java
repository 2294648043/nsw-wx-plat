package com.nsw.wx.order.controller;

import com.nsw.wx.order.VO.ResultVO;
import com.nsw.wx.order.VO.ResultVOUtil;
import com.nsw.wx.order.pojo.WeCharOrder;
import com.nsw.wx.order.redis.RedisService;
import com.nsw.wx.order.redis.WeChatProductOutputKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

//import com.imooc.miaosha.domain.User;
//import com.imooc.miaosha.redis.RedisService;
//import com.imooc.miaosha.redis.UserKey;
//import com.imooc.miaosha.result.CodeMsg;
//import com.imooc.miaosha.result.Result;
//import com.imooc.miaosha.service.UserService;

@Controller
@RequestMapping("/demo")
public class SampleController {

//	@Autowired
//	UserService userService;
	
	@Autowired
    RedisService redisService;
	
    @RequestMapping("/hello")
    @ResponseBody
    public ResultVO<String> home() {
        return ResultVOUtil.success("Hello，world");
    }
    
//    @RequestMapping("/error")
//    @ResponseBody
//    public Result<String> error() {
//        return Result.error(CodeMsg.SESSION_ERROR);
//    }
//
//    @RequestMapping("/hello/themaleaf")
//    public String themaleaf(Model model) {
//        model.addAttribute("name", "Joshua");
//        return "hello";
//    }
//
//    @RequestMapping("/db/get")
//    @ResponseBody
//    public Result<User> dbGet() {
//    	User user = userService.getById(1);
//        return Result.success(user);
//    }
//
//
//    @RequestMapping("/db/tx")
//    @ResponseBody
//    public Result<Boolean> dbTx() {
//    	userService.tx();
//        return Result.success(true);
//    }
//
    @RequestMapping("/redis/get")
    @ResponseBody
    public ResultVO<WeCharOrder> redisGet() {
        WeCharOrder  weCharOrder  = redisService.get(WeChatProductOutputKey.getById, ""+1, WeCharOrder.class);
        return ResultVOUtil.success(weCharOrder);
    }
    
    @RequestMapping("/redis/set")
    @ResponseBody
    public ResultVO<Boolean> redisSet() {
        WeCharOrder weCharOrder  = new WeCharOrder();
        weCharOrder.setId(1);
        weCharOrder.setOrderstate(11);
    	redisService.set(WeChatProductOutputKey.getById, ""+1, weCharOrder);//UserKey:id1
        return ResultVOUtil.success(true);
    }
    
    
}
