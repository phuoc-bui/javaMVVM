package com.phuocbui.mvvm.data.model;

/**
 * Created by inbox on 6/2/18.
 */

public class ObservationTypes {


    String name;
    String text;
    String status;
    String obsKey;
    String icon;

    public String getName() {
        return this.name;
    }
    public void setName(String name) {
        this.name = name;
    }

     public String getText() {
        return this.text;
    }
    public void setText(String text) {
        this.text = text;
    }

     public String getStatus() {
        return this.status;
    }
    public void setStatus(String status) {
        this.status = status;
    }

     public String getObsKey() {
        return this.obsKey;
    }
    public void setObsKey(String obsKey) {
        this.obsKey = obsKey;
    }

    public String getIcon() {
        return this.icon;
    }
    public void setIcon(String icon) {
        this.icon = icon;
    }

}
