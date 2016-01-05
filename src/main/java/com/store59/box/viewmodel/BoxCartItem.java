package com.store59.box.viewmodel;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;

/**
 * Created by zhangwangyong on 15/7/21.
 */
public class BoxCartItem {

    @JsonProperty(value = "rid")
    private Integer rid;
    @JsonProperty(value = "name")
    private String name;
    @JsonProperty(value = "img")
    private String img;
    @JsonProperty(value = "quantity")
    private Integer quantity;
    @JsonProperty(value = "price")
    private BigDecimal price;
    @JsonProperty(value = "amount")
    private BigDecimal amount;

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

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}
