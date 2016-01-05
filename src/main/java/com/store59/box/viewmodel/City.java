package com.store59.box.viewmodel;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

/**
 * Created by zhangwangyong on 15/10/9.
 */
public class City implements Serializable{
    @JsonProperty("city_id")
    private Integer cityId;
    @JsonProperty("city")
    private String city;

    public Integer getCityId() {
        return cityId;
    }

    public void setCityId(Integer cityId) {
        this.cityId = cityId;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }
}
