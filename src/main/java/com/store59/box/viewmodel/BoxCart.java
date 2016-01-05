package com.store59.box.viewmodel;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by zhangwangyong on 15/7/21.
 */
public class BoxCart {

    @JsonProperty(value = "amount")
    private BigDecimal amount;                  //订单金额
    @JsonProperty(value = "total_num")
    private Integer totalNum;                   //产品总数
    @JsonProperty(value = "box_id")
    private Integer boxId;                      //盒子id
    @JsonProperty(value = "coupon_code")
    private String couponCode;                  //优惠券code
    @JsonProperty(value = "coupon_discount")
    private Double couponDiscount;              //优惠券折扣
    @JsonProperty(value = "food_amount")
    private BigDecimal foodAmount;              //产品金额
    @JsonProperty(value = "items")
    private List<com.store59.box.viewmodel.BoxCartItem> itemList;

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Integer getTotalNum() {
        return totalNum;
    }

    public void setTotalNum(Integer totalNum) {
        this.totalNum = totalNum;
    }

    public Integer getBoxId() {
        return boxId;
    }

    public void setBoxId(Integer boxId) {
        this.boxId = boxId;
    }

    public String getCouponCode() {
        return couponCode;
    }

    public void setCouponCode(String couponCode) {
        this.couponCode = couponCode;
    }

    public Double getCouponDiscount() {
        return couponDiscount;
    }

    public void setCouponDiscount(Double couponDiscount) {
        this.couponDiscount = couponDiscount;
    }

    public BigDecimal getFoodAmount() {
        return foodAmount;
    }

    public void setFoodAmount(BigDecimal foodAmount) {
        this.foodAmount = foodAmount;
    }

    public List<com.store59.box.viewmodel.BoxCartItem> getItemList() {
        return itemList;
    }

    public void setItemList(List<com.store59.box.viewmodel.BoxCartItem> itemList) {
        this.itemList = itemList;
    }
}
