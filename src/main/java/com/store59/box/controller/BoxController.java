package com.store59.box.controller;

import com.store59.box.logic.UserToken;
import com.store59.box.service.BoxService;
import com.store59.box.service.TokenService;
import com.store59.box.viewmodel.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * Created by shanren on 7/8/15.
 */
@RestController
public class BoxController {

    @Autowired
    private BoxService boxService;
    @Autowired
    private TokenService tokenService;

    @RequestMapping(value = "/box/info",method = RequestMethod.GET)
    @ResponseBody
    public Object getBoxInfo(HttpServletRequest request,@RequestParam(value = "box_id") Integer boxId){
        return boxService.findBoxById(boxId);
    }


    @RequestMapping(value = "/box/list",method = RequestMethod.GET)
    @ResponseBody
    public Object findBoxList(HttpServletRequest request,@RequestParam(value = "dormentry_id") Integer dormentryId,
                              @RequestParam(required = false)String room){
        return boxService.findBoxList(dormentryId, room);
    }

    @RequestMapping(value = "/box/listByDormentryId",method = RequestMethod.GET)
    @ResponseBody
    public Object findBoxListByDormentryId(HttpServletRequest request,@RequestParam(value = "dormentry_id") Integer dormentryId){
        return boxService.findBoxList(dormentryId);
    }

    @RequestMapping(value = "/my_box",method = RequestMethod.GET)
    @ResponseBody
    public Object getLoginUserBox(HttpServletRequest request){
        UserToken userToken = tokenService.getUserToken(request);
        return boxService.getLoginUserBox(userToken.getUid());
    }

    @RequestMapping(value = "/box/exclude_items",method = RequestMethod.GET)
    @ResponseBody
    public Object getExcludeItems(HttpServletRequest request,@RequestParam("box_id")Integer boxId){
        UserToken userToken = tokenService.getUserToken(request);
        return boxService.getExcludeItems(boxId,userToken.getUid());
    }

    @RequestMapping(value = "/box/item/add",method = RequestMethod.POST)
    @ResponseBody
    public Object itemAdd(HttpServletRequest request,@RequestParam("box_id")Integer boxId,
                          @RequestParam("rids")List<Integer> rids){
        UserToken userToken = tokenService.getUserToken(request);
        return boxService.itemAdd(boxId,rids,userToken.getUid());
    }

    @RequestMapping(value = "/box/item/remove",method = RequestMethod.POST)
    @ResponseBody
    public Object itemRemove(HttpServletRequest request,@RequestParam("box_id")Integer boxId,
                             @RequestParam("rid")Integer rid){
        UserToken userToken = tokenService.getUserToken(request);
        return boxService.itemRemove(boxId,rid,userToken.getUid());
    }

    @RequestMapping(value = "/box/item/replenish",method = RequestMethod.POST)
    @ResponseBody
    public Object itemReplenish(HttpServletRequest request,@RequestParam("box_id")Integer boxId,
                                @RequestParam("rid")Integer rid){
        UserToken userToken = tokenService.getUserToken(request);
        return boxService.itemReplenish(boxId,rid,userToken.getUid());
    }
}
