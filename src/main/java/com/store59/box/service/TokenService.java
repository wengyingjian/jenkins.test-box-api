package com.store59.box.service;

import com.store59.box.logic.UserToken;
import com.store59.kylin.common.Util;
import com.store59.kylin.common.cache.ICache;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by zhangwangyong on 15/7/29.
 */
@Service
public class TokenService{

    @Autowired
    private ICache iCacheClient;

    public UserToken getUserToken(HttpServletRequest request) {
        String tokenStr = request.getParameter("token");
        String userTokenStr = iCacheClient.get("token_"+tokenStr);
        if(!StringUtils.isEmpty(userTokenStr)){
            return Util.getObjectFromJson(userTokenStr, UserToken.class);
        }
        return null;
    }
    
    public UserToken getUserToken(String token) {
        String userTokenStr = iCacheClient.get("token_" + token);
        if(!StringUtils.isEmpty(userTokenStr)){
            return Util.getObjectFromJson(userTokenStr, UserToken.class);
        }
        return null;
    }
}
