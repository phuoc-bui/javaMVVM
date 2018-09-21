package com.redhelmet.alert2me.data.model;

import java.io.Serializable;

public class Entry implements Serializable
{
    private String title ;
     private Object value ;
    private String link ;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }
}
