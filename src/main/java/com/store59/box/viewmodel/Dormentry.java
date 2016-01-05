package com.store59.box.viewmodel;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

/**
 * Created by zhangwangyong on 15/10/9.
 */
public class Dormentry implements Serializable{
    @JsonProperty("dormentry_id")
    private Integer dormentryId;
    @JsonProperty("address")
    private String address;
    @JsonProperty("floor_address")
    private String floorAddress;
    @JsonProperty("status")
    private byte status;

    public Integer getDormentryId() {
        return dormentryId;
    }

    public void setDormentryId(Integer dormentryId) {
        this.dormentryId = dormentryId;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getFloorAddress() {
        return floorAddress;
    }

    public void setFloorAddress(String floorAddress) {
        this.floorAddress = floorAddress;
    }

    public byte getStatus() {
        return status;
    }

    public void setStatus(byte status) {
        this.status = status;
    }
}
