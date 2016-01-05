package com.store59.box.viewmodel;

import java.io.Serializable;
import java.util.List;

/**
 * Created by zhangwangyong on 15/7/30.
 */
public class ViewRoomBox implements Serializable {
    private String room;
    private List<ViewBox> items;

    public String getRoom() {
        return room;
    }

    public void setRoom(String room) {
        this.room = room;
    }

    public List<ViewBox> getItems() {
        return items;
    }

    public void setItems(List<ViewBox> items) {
        this.items = items;
    }
}
