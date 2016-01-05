package com.store59.box.controller;

import com.store59.box.service.BoxCartService;
import com.store59.box.viewmodel.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by zhangwangyong on 15/7/21.
 */
@RestController
@RequestMapping("/cart")
public class BoxCartController {

    @Autowired
    private BoxCartService boxCartService;

    @RequestMapping(value = "/info",method = RequestMethod.GET)
    @ResponseBody
    public Object findBoxCartByBoxId(HttpServletRequest request,@RequestParam(value = "box_id") Integer boxId){
        Result result = new Result();
        String tokenStr = request.getParameter("token");
        result.setData(boxCartService.findBoxCartByBoxId(tokenStr,boxId));
        result.setMsg(result.getMsg());
        return result;
    }

    @RequestMapping(value = "/update",method = RequestMethod.POST)
    @ResponseBody
    public Object updateBoxCart(HttpServletRequest request,@RequestParam(value = "box_id") Integer boxId,
                                 @RequestParam(value = "rid") int rid,
                                 @RequestParam(value = "quantity") int quantity){
        String tokenStr = request.getParameter("token");
        return boxCartService.updateBoxCart(tokenStr,boxId, rid, quantity);
    }

    @RequestMapping(value = "/clear",method = RequestMethod.POST)
    @ResponseBody
    public Object clearBoxCart(HttpServletRequest request,@RequestParam(value = "box_id",required = false) Integer boxId){
        Result result = new Result();
        String tokenStr = request.getParameter("token");
        result.setData(boxCartService.clearBoxCart(tokenStr,boxId));
        result.setMsg(result.getMsg());
        return result;
    }

    @RequestMapping(value = "/use_coupon",method = RequestMethod.GET)
    @ResponseBody
    public Object useCoupon(HttpServletRequest request,@RequestParam(value = "box_id")Integer boxId,
                            @RequestParam(value = "coupon_code",required = false)String couponCode){
        String tokenStr = request.getParameter("token");
        return boxCartService.useCoupon(tokenStr,boxId,couponCode);
    }
}
