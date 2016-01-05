package com.store59.box.controller;

import com.store59.box.logic.UserToken;
import com.store59.box.viewmodel.Result;
import com.store59.kylin.common.Util;
import com.store59.kylin.common.cache.ICache;
import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by zhangwangyong on 15/7/25.
 */
@Controller
@RequestMapping("/token")
public class TokenController {

    Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private ICache iCacheClient;
    private static final int REDIS_EXPIRE = 172800;

    @RequestMapping(value = "/new", method = RequestMethod.POST)
    @ResponseBody
    public Object create(@RequestParam(value = "device_id")String deviceId,
                         @RequestParam(value = "device_type")Integer deviceType,@RequestParam(value = "site_id")String siteId,
                         @RequestParam(value = "uid")Long uid,HttpServletRequest request) throws ParseException {
        logger.info("-----------------box getToken start-----------");
        long beginTime = System.currentTimeMillis()/1000;
        UserToken userToken = new UserToken();
        userToken.setDevice_id(deviceId);
        userToken.setDevice_type(deviceType);
        userToken.setSite_id(siteId);
        userToken.setUid(uid);
        userToken.setTime(System.currentTimeMillis() / 1000);
        String tokenStr = DigestUtils.md5Hex(deviceId+new Date());
        iCacheClient.setex("token_" + tokenStr,REDIS_EXPIRE, Util.getJsonFromObject(userToken));
        Map<String, Object> data = new HashMap<>();
        data.put("token", tokenStr);
        Result result = new Result();
        result.setData(data);
        logger.info("token:"+tokenStr);
        long tasktime = System.currentTimeMillis()/1000 - beginTime;
        logger.info("-----------------box getToken end-----------"+tasktime);
        return result;
    }
}
