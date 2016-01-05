package com.store59.box.viewmodel;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

/**
 * Created by zhangwangyong on 15/7/29.
 */
public class ViewBoxMessage implements Serializable {

    @JsonProperty(value = "box_id")
    private Integer boxId;              //零食盒子id

    @JsonProperty(value = "message")
    private String message;             //留言信息

    public Integer getBoxId() {
        return boxId;
    }

    public void setBoxId(Integer boxId) {
        this.boxId = boxId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
