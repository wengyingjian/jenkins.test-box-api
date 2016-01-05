package com.store59.box.viewmodel;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

/**
 * Created by zhangwangyong on 15/10/8.
 */
public class ExcludeRepoItem implements Serializable{
    @JsonProperty("img")
    private String img;
    @JsonProperty("name")
    private String name;
    @JsonProperty("rid")
    private Integer rid;
    @JsonProperty("price")
    private Double price;
    @JsonProperty("order")
    private Integer order;
    @JsonProperty("is_new")
    private int newRepo;

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

    public Integer getOrder() {
        return order;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }

    public int getNewRepo() {
        return newRepo;
    }

    public void setNewRepo(int newRepo) {
        this.newRepo = newRepo;
    }
}
