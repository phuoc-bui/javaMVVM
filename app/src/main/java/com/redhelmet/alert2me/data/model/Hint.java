package com.redhelmet.alert2me.data.model;

import android.support.annotation.DrawableRes;

public class Hint implements Model {
    @DrawableRes
    private int url;
    private String title;
    private String desc;
    private boolean isLast = false;

    public int getUrl() {
        return url;
    }

    public void setUrl(int url) {
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public boolean isLast() {
        return isLast;
    }

    public void setLast(boolean last) {
        isLast = last;
    }
}
