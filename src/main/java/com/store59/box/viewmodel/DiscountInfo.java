package com.store59.box.viewmodel;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

/**
 * Created by zhangwangyong on 15/8/18.
 */
public class DiscountInfo implements Serializable{
    @JsonProperty(value = "discount_title")
    private String discountTitle;
    @JsonProperty(value = "discount")
    private double discount;

    public String getDiscountTitle() {
        return discountTitle;
    }

    public void setDiscountTitle(String discountTitle) {
        this.discountTitle = discountTitle;
    }

    public double getDiscount() {
        return discount;
    }

    public void setDiscount(double discount) {
        this.discount = discount;
    }
}
