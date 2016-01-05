package com.store59.box.viewmodel;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JacksonStdImpl;

import java.io.Serializable;

/**

 * Created by zhangwangyong on 15/10/9.
 */
public class Building implements Serializable{
    @JsonProperty("building_name")
    private String buildingName;


    public String getBuildingName() {
        return buildingName;
    }

    public void setBuildingName(String buildingName) {
        this.buildingName = buildingName;
    }
}
