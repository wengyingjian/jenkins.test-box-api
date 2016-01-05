package com.store59.box.viewmodel;

import java.io.Serializable;

/**
 * Created by zhangwangyong on 15/9/22.
 */
public class QuestionNaire implements Serializable{
    private String image;   //图片地址
    private String url;     //问卷地址

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
