package com.store59.box.controller;

import com.store59.box.canstants.PayType;
import com.store59.box.canstants.RetCode;
import com.store59.box.exception.ServiceException;
import com.store59.box.logic.UserToken;
import com.store59.box.service.OrderService;
import com.store59.box.service.TokenService;
import com.store59.box.util.ResultUtil;
import com.store59.box.util.SignCheckUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by zhangwangyong on 15/7/19.
 */
@RestController
public class OrderController {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private OrderService orderService;
    @Autowired
    private TokenService tokenService;

    @Value("${pay.secretKey}")
    private String secretKey;
    private final int IOS_DEVICE_TYPE = 0;
    private final int ANDROID_DEVICE_TYPE = 1;
    private final int IOS_SOURCE_TYPE = 3;
    private final int ANDROID_SOURCE_TYPE = 4;

    @RequestMapping(value = "/order/add",method = RequestMethod.POST)
    @ResponseBody
    public Object addOrder(HttpServletRequest request,@RequestParam(value = "box_id") Integer boxId,
                           @RequestParam(value = "source")Integer source,
                           @RequestParam(required = false,value = "coupon_code")String couponCode,@RequestParam("app_version")String appVersion){
        String tokenStr = request.getParameter("token");
        String ip = request.getRemoteAddr();
        UserToken userToken = tokenService.getUserToken(request);
        if(userToken.getDevice_type() == IOS_DEVICE_TYPE){
            source = IOS_SOURCE_TYPE;
        }else if(userToken.getDevice_type() == ANDROID_DEVICE_TYPE){
            source = ANDROID_SOURCE_TYPE;
        }
        return orderService.addOrder(tokenStr,boxId,couponCode,userToken.getUid(),source,ip,appVersion);
    }

    @RequestMapping(value = "/order/list",method = RequestMethod.GET)
    @ResponseBody
    public Object getOrderList(HttpServletRequest request,@RequestParam("app_version")String appVersion){
        UserToken userToken = tokenService.getUserToken(request);
        if(null == userToken){
            return ResultUtil.genResult(RetCode.TOKEN_EMPTY,"empty token");
        }
        String tokenStr = request.getParameter("token");
        return orderService.getOrderListByUid(userToken.getUid(),tokenStr,appVersion);
    }

    @RequestMapping(value = "/order/info",method = RequestMethod.GET)
    @ResponseBody
    public Object getOrderInfo(HttpServletRequest request,@RequestParam(value = "order_sn") String orderSn,@RequestParam("app_version")String appVersion){
        return orderService.getOrderInfoBySn(orderSn,appVersion);
    }

    @RequestMapping(value = "/order/cancel",method = RequestMethod.POST)
    @ResponseBody
    public Object cancelOrder(HttpServletRequest request,@RequestParam(value = "order_sn") String orderSn){
        logger.info("------------------盒子取消orderSn:"+orderSn);
        return orderService.cancelOrder(orderSn);
    }

    @RequestMapping(value = "/order/pay",method = RequestMethod.POST)
    @ResponseBody
    public Object payOrder(Integer status,String payType,String sign,
                           @RequestParam(value = "orderId")String orderSn,double money,
                           String tradeNo,Long payTime,HttpServletRequest request){
        //支付校验
        if(status != 0){
            return ResultUtil.genResult(RetCode.NORMAL_ERROR, "支付状态不正确");
        }
        //令牌校验
        if (!SignCheckUtils.checkSign(request,secretKey)) {
            throw new ServiceException(RetCode.SIGN_ERROR, "check sign error");
        }
        return orderService.payOrder(orderSn,PayType.getPayTypeByValue(payType).getType(),tradeNo,money,payTime);
    }
}
