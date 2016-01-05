package com.store59.box.viewmodel;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.List;

/**
 * Created by zhangwangyong on 15/7/29.
 */
public class ViewOrder implements Serializable {

    @JsonProperty(value = "box_id")
    private Integer boxId;              //盒子id

    @JsonProperty(value = "box_code")
    private String boxCode;             //盒子code

    @JsonProperty(value = "type")
    private Integer type;               //订单类型

    @JsonProperty(value = "pay_type")
    private Integer payType;            //支付类型

    @JsonProperty(value = "source")
    private Integer source;             //订单来源

    @JsonProperty(value = "status")
    private Integer status;             //订单状态

    @JsonProperty(value = "dorm_id")
    private Integer dormId;             //楼主id

    @JsonProperty(value = "room")
    private String room;                //宿舍

    @JsonProperty(value = "uid")
    private Long uid;                   //用户id

    @JsonProperty(value = "food_amount")
    private Double foodAmount;          //商品金额

    @JsonProperty(value = "food_num")
    private Integer foodNum;            //商品数量

    @JsonProperty(value = "order_amount")
    private Double orderAmount;         //订单金额

    @JsonProperty(value = "order_sn")
    private String orderSn;             //订单sn

    @JsonProperty(value = "create_time")
    private Long createTime;         //创建时间

//    @JsonProperty(value = "order_sn")
//    private Long orderId;               //订单id

    @JsonProperty(value = "ip")
    private String ip;                  //订单ip

    @JsonProperty(value = "total_discount")
    private double totalDiscount;       //总折扣

    @JsonProperty(value = "cancel_time")
    private Long cancelTime;            //取消时间

    @JsonProperty(value = "confirm_time")
    private Long payTime;               //支付时间

    @JsonProperty(value = "share_info")
    private ShareInfo shareInfo;        //分享信息

    @JsonProperty(value = "share_infos")
    private List<ShareInfo> shareInfoList;        //分享信息

    @JsonProperty(value = "activities")
    private List<Activity> activities;

    @JsonProperty(value = "discount_info")
    private List<DiscountInfo> discountInfos;       //折扣列表

    @JsonProperty(value = "remark")
    private String remark;              //订单说明

    @JsonProperty(value = "refund_status_code")
    private Integer refundStatusCode;   //退款状态

    @JsonProperty(value = "refund_status_msg")
    private String refundStatusMsg;    //退款说明

    @JsonProperty(value = "tips")
    private String tips;

    private List<ViewOrderItem> items ;

    public Integer getBoxId() {
        return boxId;
    }

    public void setBoxId(Integer boxId) {
        this.boxId = boxId;
    }

    public String getBoxCode() {
        return boxCode;
    }

    public void setBoxCode(String boxCode) {
        this.boxCode = boxCode;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Integer getPayType() {
        return payType;
    }

    public void setPayType(Integer payType) {
        this.payType = payType;
    }

    public Integer getSource() {
        return source;
    }

    public void setSource(Integer source) {
        this.source = source;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getDormId() {
        return dormId;
    }

    public void setDormId(Integer dormId) {
        this.dormId = dormId;
    }

    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
    }

    public Long getUid() {
        return uid;
    }

    public void setUid(Long uid) {
        this.uid = uid;
    }

    public Double getFoodAmount() {
        return foodAmount;
    }

    public void setFoodAmount(Double foodAmount) {
        this.foodAmount = foodAmount;
    }

    public Integer getFoodNum() {
        return foodNum;
    }

    public void setFoodNum(Integer foodNum) {
        this.foodNum = foodNum;
    }

    public Double getOrderAmount() {
        return orderAmount;
    }

    public void setOrderAmount(Double orderAmount) {
        this.orderAmount = orderAmount;
    }

    public String getOrderSn() {
        return orderSn;
    }

    public void setOrderSn(String orderSn) {
        this.orderSn = orderSn;
    }

    public Long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Long createTime) {
        this.createTime = createTime;
    }

//    public Long getOrderId() {
//        return orderId;
//    }
//
//    public void setOrderId(Long orderId) {
//        this.orderId = orderId;
//    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Long getCancelTime() {
        return cancelTime;
    }

    public void setCancelTime(Long cancelTime) {
        this.cancelTime = cancelTime;
    }

    public Long getPayTime() {
        return payTime;
    }

    public void setPayTime(Long payTime) {
        this.payTime = payTime;
    }

    public List<ViewOrderItem> getItems() {
        return items;
    }

    public void setItems(List<ViewOrderItem> items) {
        this.items = items;
    }

    public List<DiscountInfo> getDiscountInfos() {
        return discountInfos;
    }

    public void setDiscountInfos(List<DiscountInfo> discountInfos) {
        this.discountInfos = discountInfos;
    }

    public double getTotalDiscount() {
        return totalDiscount;
    }

    public void setTotalDiscount(double totalDiscount) {
        this.totalDiscount = totalDiscount;
    }

    public ShareInfo getShareInfo() {
        return shareInfo;
    }

    public void setShareInfo(ShareInfo shareInfo) {
        this.shareInfo = shareInfo;
    }

    public Integer getRefundStatusCode() {
        return refundStatusCode;
    }

    public void setRefundStatusCode(Integer refundStatusCode) {
        this.refundStatusCode = refundStatusCode;
    }

    public String getRefundStatusMsg() {
        return refundStatusMsg;
    }

    public void setRefundStatusMsg(String refundStatusMsg) {
        this.refundStatusMsg = refundStatusMsg;
    }

    public List<ShareInfo> getShareInfoList() {
        return shareInfoList;
    }

    public void setShareInfoList(List<ShareInfo> shareInfoList) {
        this.shareInfoList = shareInfoList;
    }

    public String getTips() {
        return tips;
    }

    public void setTips(String tips) {
        this.tips = tips;
    }

    public List<Activity> getActivities() {
        return activities;
    }

    public void setActivities(List<Activity> activities) {
        this.activities = activities;
    }
}
