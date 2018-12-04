package com.phuocbui.mvvm.data.model;

import androidx.annotation.DrawableRes;

import com.phuocbui.mvvm.data.model.base.Model;

public class Hint implements Model {
    private String url;
    @DrawableRes
    private int resId;
    private String title;
    private String desc;
    private boolean isLast = false;

    public int getResId() {
        return resId;
    }

    public void setResId(int resId) {
        this.resId = resId;
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

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
