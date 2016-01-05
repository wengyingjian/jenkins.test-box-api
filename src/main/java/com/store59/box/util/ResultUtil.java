package com.store59.box.util;


import com.store59.box.canstants.RetCode;
import com.store59.box.viewmodel.PayResult;
import com.store59.box.viewmodel.Result;

/**
 * Created by zhangwangyong on 15/7/25.
 */
public class ResultUtil {

    public static Result resultService2View(com.store59.kylin.common.model.Result result){
        Result viewResult = new Result();
        viewResult.setData(result.getData());
        viewResult.setMsg(result.getMsg());
        viewResult.setStatus(0 == result.getStatus()?0:RetCode.NORMAL_ERROR);
        return viewResult;
    }

    public static Result resultBase2View(com.store59.kylin.common.model.Result result){
        Result viewResult = new Result();
        viewResult.setData(result.getData());
        viewResult.setMsg(result.getMsg());
        viewResult.setStatus(0 == result.getStatus()?0:RetCode.NORMAL_ERROR);
        return viewResult;
    }

    public static Result genResult(int status,String msg,Object o){
        Result result = new Result();
        result.setStatus(status);
        result.setMsg(msg);
        result.setData(o);
        return result;
    }

    public static Result genResult(int status, String msg){
        Result result = new Result();
        result.setStatus(status);
        result.setMsg(msg);
        return result;
    }

    public static Result genSuccessResult(Object o){
        Result result = new Result();
        result.setData(o);
        return result;
    }

    public static PayResult genSuccessPayResult(Object o){
        PayResult payResult = new PayResult();
        payResult.setData(o);
        return payResult;
    }

    public static PayResult genPayResult(int status,String msg){
        PayResult payResult = new PayResult();
        payResult.setStatus(status);
        payResult.setMsg(msg);
        return payResult;
    }
}
