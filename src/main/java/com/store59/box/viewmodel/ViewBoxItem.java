package com.store59.box.viewmodel;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

/**
 * Created by zhangwangyong on 15/7/29.
 */
public class ViewBoxItem implements Serializable {

    @JsonProperty(value = "item_id")
    private Integer itemId;             //盒子明细id

    @JsonProperty(value = "img")
    private String img;                 //商品图片

    @JsonProperty(value = "name")
    private String name;                //商品名称

    @JsonProperty(value = "rid")
    private Integer rid;                //商品id

    @JsonProperty(value = "stock")
    private Integer stock;              //商品库存

    @JsonProperty(value = "price")
    private Double price;               //商品单价

    @JsonProperty(value = "order")
    private Integer order;            //产品排序

    @JsonProperty(value = "replenishing")
    private int replenishing;

    public Integer getItemId() {
        return itemId;
    }

    public void setItemId(Integer itemId) {
        this.itemId = itemId;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getRid() {
        return rid;
    }

    public void setRid(Integer rid) {
        this.rid = rid;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public Integer getOrder() {
        return order;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }

    public int getReplenishing() {
        return replenishing;
    }

    public void setReplenishing(int replenishing) {
        this.replenishing = replenishing;
    }
}
