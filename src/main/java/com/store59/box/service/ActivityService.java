/**
 * Copyright (c) 2015, 59store. All rights reserved.
 */
package com.store59.box.service;

import com.store59.box.util.SignCheckUtils;
import com.store59.box.viewmodel.Activity;
import com.store59.box.viewmodel.ShareInfo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author <a href="mailto:zhangwangy@59store.com">阿勇</a>
 * @version 1.0 15/11/23
 * @since 1.0
 */
@Service
public class ActivityService {

    @Value("${dzp.url}")
    private String dzpUrl;
    @Value("${dzp.imageurl}")
    private String dzpImageurl;
    @Value("${dzp.sharebtn}")
    private String dzpSharebtn;
    @Value("${hongbao.url}")
    private String hongbaoUrl;
    @Value("${hongbao.imageurl}")
    private String hongbaoImageurl;
    @Value("${hongbao.sharebtn}")
    private String hongbaoSharebtn;
    @Value("${hongbao.secretKey}")
    private String hbSecretKey;
    @Value("${hongbao.starttime}")
    private long hongbaoStartTime;
    @Value("${hongbao.endtime}")
    private long hongbaoEndTime;
    @Value("${survey.url}")
    private String surveyUrl;
    @Value("${survey.imageurl}")
    private String surveyImageurl;

    /**
     * 增加大转盘对分享信息
     * */
    public ShareInfo createShareInfo() {
        ShareInfo shareInfo = new ShareInfo();
        shareInfo.setTitle("开学照样玩，59送大奖");
        shareInfo.setText("找抽有礼，任性无罪。100%中奖，还不来抽我呀！");
        shareInfo.setUrl(dzpUrl);
        shareInfo.setImageUrl(dzpImageurl);
        shareInfo.setShareBtn(dzpSharebtn);
        return shareInfo;
    }

    /**
     * 方法的功能描述：返回红包活动
     *
     * @param   sn,type     订单sn，订单类型
     * @return Activity
     * @author zhangwangyong
     * */
    public Activity createHongBaoActivity(String sn,String type){
        Activity activity = new Activity();
        activity.setAction("share_url");
        activity.setTitle("59零食狂欢节，小伙伴们都抢疯啦！");
        activity.setText("爆款零食无底线，满5元最高减3元，2000多所学校同步开抢！");
        long now = System.currentTimeMillis()/1000;
        String url = hongbaoUrl +("?orderId="+sn+"&orderType="+type+"&time="+now);
        Map<String,String> paramMap = new HashMap<>();
        paramMap.put("orderId", sn);
        paramMap.put("orderType",type);
        paramMap.put("time", String.valueOf(now));
        String sign = SignCheckUtils.checkSign(paramMap, hbSecretKey);
        url += ("&sign="+sign);
        activity.setUrl(url);
        activity.setImageUrl(hongbaoImageurl);
        activity.setShareBtn(hongbaoSharebtn);
        return activity;
    }

    /**
     * 方法的功能描述：问卷调查活动
     *
     * @param
     * @return  Activity
     * @throuws
     * @author  zhangwangyong
     * */
    public Activity createQuestionActivity() {
        Activity questionActivity = new Activity();
        questionActivity.setAction("open_url");
        questionActivity.setTitle("问卷调查");
        questionActivity.setText("");
//        questionActivity.setUrl("http://gala.59shangcheng.com/survey-finance");
        questionActivity.setUrl(surveyUrl);
        questionActivity.setShareBtn(surveyImageurl);
        return questionActivity;
    }

    /**
     * 增加红包分享信息
     * */
    public ShareInfo createHongBaoShareInfo(String sn,String type){
        ShareInfo shareInfo = new ShareInfo();
        shareInfo.setTitle("糖果萌萌消联合59store发红包啦～");
        shareInfo.setText("59夜猫店，开在寝室里的便利店，5分钟送货上床。红包可以在下单时直接抵扣现金。");
        long now = System.currentTimeMillis()/1000;
        String url = hongbaoUrl +("?orderId="+sn+"&orderType="+type+"&time="+now);
        Map<String,String> paramMap = new HashMap<>();
        paramMap.put("orderId", sn);
        paramMap.put("orderType",type);
        paramMap.put("time", String.valueOf(now));
        String sign = SignCheckUtils.checkSign(paramMap, hbSecretKey);
        url += ("&sign="+sign);
        shareInfo.setUrl(url);
        shareInfo.setImageUrl(hongbaoImageurl);
        shareInfo.setShareBtn(hongbaoSharebtn);
        return shareInfo;
    }

}
