package com.store59.box.viewmodel;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by zhangwangyong on 15/8/21.
 */
public class ShareInfo {
    @JsonProperty(value = "title")
    private String title;

    @JsonProperty(value = "text")
    private String text;

    @JsonProperty(value = "url")
    private String url;

    @JsonProperty(value = "image_url")
    private String imageUrl;

    @JsonProperty(value = "share_btn")
    private String shareBtn;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getShareBtn() {
        return shareBtn;
    }

    public void setShareBtn(String shareBtn) {
        this.shareBtn = shareBtn;
    }
}
