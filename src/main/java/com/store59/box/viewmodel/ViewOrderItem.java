package com.store59.box.viewmodel;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

/**
 * Created by zhangwangyong on 15/7/29.
 */
public class ViewOrderItem implements Serializable {

    @JsonProperty(value = "rid")
    private Integer rid;                //商品id

    @JsonProperty(value = "name")
    private String name;                //商品名称

    @JsonProperty(value = "quantity")
    private Integer quantity;           //商品数量

    @JsonProperty(value = "price")
    private Double price;               //商品价格

    @JsonProperty(value = "amount")
    private Double amount;              //商品金额

    @JsonProperty(value = "img")
    private String img;                 //商品图片

    public Integer getRid() {
        return rid;
    }

    public void setRid(Integer rid) {
        this.rid = rid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }
}
