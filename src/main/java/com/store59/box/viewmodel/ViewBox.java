package com.store59.box.viewmodel;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.List;

/**
 * Created by zhangwangyong on 15/7/29.
 */
public class ViewBox implements Serializable {
    @JsonProperty(value = "box_id")
    private Integer boxId;              //零食盒子ID

    @JsonProperty(value = "owner")
    private String owner;               //盒主姓名

    @JsonProperty(value = "status")
    private Integer status;             //盒子状态

    @JsonProperty(value = "room")
    private String room;                //宿舍

    @JsonProperty(value = "order")
    private Integer order;              //序号

    @JsonProperty(value = "code")
    private String code;                //盒子编号

    @JsonProperty(value = "capacity")
    private Integer capacity;

//    @JsonProperty(value = "questionnaire")
//    private QuestionNaire questionNaire;  //问卷调查

    private List<ViewBoxItem> items;

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

    public Integer getOrder() {
        return order;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public List<ViewBoxItem> getItems() {
        return items;
    }

    public void setItems(List<ViewBoxItem> items) {
        this.items = items;
    }

    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    //    public QuestionNaire getQuestionNaire() {
//        return questionNaire;
//    }
//
//    public void setQuestionNaire(QuestionNaire questionNaire) {
//        this.questionNaire = questionNaire;
//    }
}
