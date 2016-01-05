package com.store59.box.util;

import org.apache.commons.codec.digest.DigestUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Created by zhangwangyong on 15/8/15.
 */
public class SignCheckUtils {

    public static boolean checkSign(HttpServletRequest request,String secretKey){
        List<String> keys = new ArrayList<>();
        keys.addAll(request.getParameterMap().keySet());
        Collections.sort(keys);
        StringBuilder sb = new StringBuilder();
        String signValue = null;
        for (String key : keys) {
            String value = request.getParameter(key);
            if (key.equals("sign")) {
                signValue = value;
                continue;
            }
            sb.append(key).append("=").append(value).append("&");
        }
        sb.append(secretKey);
        String md5Value = DigestUtils.md5Hex(sb.toString());
        if (signValue != null && signValue.equals(md5Value)) {
            return true;
        }
        return false;
    }

    public static String checkSign(Map<String,String> paramMap,String secretKey){
        List<String> keys = new ArrayList<>();
        keys.addAll(paramMap.keySet());
        Collections.sort(keys);
        StringBuilder sb = new StringBuilder();
        for (String key : keys) {
            String value = paramMap.get(key);
            sb.append(key).append("=").append(value).append("&");
        }
        sb.append("secretKey="+secretKey);
        return DigestUtils.md5Hex(sb.toString());
    }
}
