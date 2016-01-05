package com.store59.box.controller;

import com.store59.box.canstants.RetCode;
import com.store59.box.logic.UserToken;
import com.store59.box.service.BoxApplyService;
import com.store59.box.service.TokenService;
import com.store59.box.util.ResultUtil;
import com.store59.box.viewmodel.ViewBoxApply;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by zhangwangyong on 15/7/18.
 */
@RestController
@RequestMapping("/box")
public class BoxApplyController {

    @Autowired
    private BoxApplyService boxApplyService;
    @Autowired
    private TokenService tokenService;

    @RequestMapping(value = "/apply",method = RequestMethod.POST)
    @ResponseBody
    public Object addBoxApply(HttpServletRequest request,@RequestParam(value = "phone")String phone,
                              @RequestParam(value = "dormentry_id")Integer dormentryId,
                              @RequestParam(value = "name")String name,
                              @RequestParam(value = "room")String room){
        ViewBoxApply viewBoxApply = new ViewBoxApply();
        viewBoxApply.setPhone(phone);
        viewBoxApply.setDormentryId(dormentryId);
        viewBoxApply.setName(name);
        viewBoxApply.setRoom(room);
        //只有登录用户可申请盒子
        UserToken userToken = tokenService.getUserToken(request);
        if(userToken.getUid() == null || userToken.getUid().compareTo(0l)==0){
            return ResultUtil.genResult(RetCode.NORMAL_ERROR,"用户未登录");
        }
        viewBoxApply.setUid(userToken.getUid());
        return boxApplyService.addBoxApply(viewBoxApply);
    }
}
