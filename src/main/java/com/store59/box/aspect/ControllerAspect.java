package com.store59.box.aspect;

import com.store59.box.canstants.RetCode;
import com.store59.box.exception.ServiceException;
import com.store59.box.logic.UserToken;
import com.store59.box.util.SignCheckUtils;
import com.store59.kylin.common.Util;
import com.store59.kylin.common.cache.ICache;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * Created by zhangwangyong on 15/7/23.
 */
@Aspect
@Component
@Resource
public class ControllerAspect {
    static Boolean DEBUG_TOKEN = false;

    @Autowired
    private ICache iCacheClient;

    @Value("${boxapi.secretKey}")
    private String secretKey;
    @Value("${box.token.lastTime}")
    private String lastTime;
    @Value("${box.token.overdueTime}")
    private String overdueTime;
    @Value("${boxapi.isCheckSum}")
    private Boolean isCheckSum;

    @Before("execution(* com.store59.box.controller.*.*(javax.servlet.http.HttpServletRequest, ..))")
    public void proController(JoinPoint point){
        Object[] args = point.getArgs();
        if(args.length > 0 && args[0] instanceof HttpServletRequest){
            HttpServletRequest request = (HttpServletRequest) args[0];

            if (this.isCheckSum) {
                if (!SignCheckUtils.checkSign(request,secretKey)) {
                    throw new ServiceException(RetCode.SIGN_ERROR, "check sign error");
                }
            }

            String token = request.getParameter("token");
            if (token != null) {
                //校验token的时间，如果小于一天，则延长时间
                String userTokenStr = iCacheClient.get("token_"+token);
                if(!StringUtils.isEmpty(userTokenStr)){
                    UserToken userToken = Util.getObjectFromJson(userTokenStr,UserToken.class);
                    long now = System.currentTimeMillis()/1000;
                    long tokenTime = userToken.getTime();
                    if(tokenTime+Integer.parseInt(overdueTime) < now){
                        throw new ServiceException(RetCode.TOKEN_EMPTY,"overdue token");
                    }else if(tokenTime+Integer.parseInt(overdueTime) >= now
                            && tokenTime+Integer.parseInt(overdueTime) < now + Integer.parseInt(lastTime)){
                        userToken.setTime(System.currentTimeMillis()/1000);
                        iCacheClient.setex("token_" + token, Integer.parseInt(overdueTime), Util.getJsonFromObject(userToken));
                    }
                }else {
                    throw new ServiceException(RetCode.TOKEN_EMPTY, "empty token");
                }
            } else if (!(DEBUG_TOKEN || request.getRequestURI().contains(
                    "/token/new"))) {
                throw new ServiceException(RetCode.TOKEN_EMPTY, "empty token");
            }
        }
    }

}
