package com.store59.box.viewmodel;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.List;

/**
 * Created by zhangwangyong on 15/10/8.
 */
public class ExcludeRepoItems implements Serializable{
    @JsonProperty("box_id")
    private Integer boxId;
    @JsonProperty("owner")
    private String owner;
    @JsonProperty("status")
    private Integer status;
    @JsonProperty("room")
    private String room;
    @JsonProperty("code")
    private String code;
    @JsonProperty("capacity_left")
    private Integer capacityLeft;
    @JsonProperty("items")
    private List<ExcludeRepoItem> items;

    public Integer getBoxId() {
        return boxId;
    }

    public void setBoxId(Integer boxId) {
        this.boxId = boxId;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public List<ExcludeRepoItem> getItems() {
        return items;
    }

    public void setItems(List<ExcludeRepoItem> items) {
        this.items = items;
    }

    public Integer getCapacityLeft() {
        return capacityLeft;
    }

    public void setCapacityLeft(Integer capacityLeft) {
        this.capacityLeft = capacityLeft;
    }
}
