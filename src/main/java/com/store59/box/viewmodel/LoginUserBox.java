package com.store59.box.viewmodel;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

/**
 * Created by zhangwangyong on 15/10/7.
 */
public class LoginUserBox implements Serializable {
    @JsonProperty(value = "city")
    private com.store59.box.viewmodel.City city;
    @JsonProperty(value = "site")
    private Site site;
    @JsonProperty(value = "dormentry")
    private Dormentry dormentry;
    @JsonProperty(value = "groups_name")
    private String groupsName;
    @JsonProperty(value = "building")
    private Building building;
    @JsonProperty(value = "box")
    private UserBox userBox;


    public com.store59.box.viewmodel.City getCity() {
        return city;
    }

    public void setCity(com.store59.box.viewmodel.City city) {
        this.city = city;
    }

    public Site getSite() {
        return site;
    }

    public void setSite(Site site) {
        this.site = site;
    }

    public Dormentry getDormentry() {
        return dormentry;
    }

    public void setDormentry(Dormentry dormentry) {
        this.dormentry = dormentry;
    }

    public Building getBuilding() {
        return building;
    }

    public void setBuilding(Building building) {
        this.building = building;
    }

    public UserBox getUserBox() {
        return userBox;
    }

    public void setUserBox(UserBox userBox) {
        this.userBox = userBox;
    }

    public String getGroupsName() {
        return groupsName;
    }

    public void setGroupsName(String groupsName) {
        this.groupsName = groupsName;
    }
}
