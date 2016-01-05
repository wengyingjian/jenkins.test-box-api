package com.store59.box.viewmodel;

import java.io.Serializable;

/**
 * Created by zhangwangyong on 15/7/30.
 */
public class ViewDormentryBoxNum implements Serializable {
    private Integer dormentryId;
    private String address;
    private String address1;
    private String address2;
    private Integer boxNum;

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

    public String getAddress1() {
        return address1;
    }

    public void setAddress1(String address1) {
        this.address1 = address1;
    }

    public String getAddress2() {
        return address2;
    }

    public void setAddress2(String address2) {
        this.address2 = address2;
    }

    public Integer getBoxNum() {
        return boxNum;
    }

    public void setBoxNum(Integer boxNum) {
        this.boxNum = boxNum;
    }
}
