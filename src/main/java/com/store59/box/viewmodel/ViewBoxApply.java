package com.store59.box.viewmodel;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

/**
 * Created by zhangwangyong on 15/7/30.
 */
public class ViewBoxApply implements Serializable {

    @JsonProperty(value = "dormentry_id")
    private Integer dormentryId;           //楼栋id

    @JsonProperty(value = "room")
    private String room;                    //宿舍

    @JsonProperty(value = "name")
    private String name;                    //姓名

    @JsonProperty(value = "gender")
    private Integer gender;                 //性别

    @JsonProperty(value = "phone")
    private String phone;                   //电话

    private Long uid;                       //用户id

    public Integer getDormentryId() {
        return dormentryId;
    }

    public void setDormentryId(Integer dormentryId) {
        this.dormentryId = dormentryId;
    }

    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getGender() {
        return gender;
    }

    public void setGender(Integer gender) {
        this.gender = gender;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Long getUid() {
        return uid;
    }

    public void setUid(Long uid) {
        this.uid = uid;
    }
}
