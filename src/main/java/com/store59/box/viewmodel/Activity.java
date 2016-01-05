/**
 * Copyright (c) 2015, 59store. All rights reserved.
 */
package com.store59.box.viewmodel;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 *
 * @author <a href="mailto:zhangwangy@59store.com">阿勇</a>
 * @version 1.0 15/11/19
 * @since 1.0
 */
public class Activity extends ShareInfo{

    @JsonProperty(value = "action")
    private String action;

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

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

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
