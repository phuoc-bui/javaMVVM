package com.redhelmet.alert2me.data.model;

import com.redhelmet.alert2me.data.model.base.Model;

import java.util.List;

public class Section implements Model {

    private String name;
    private String icon;
    private List<Entry> entries;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public List<Entry> getEntries() {
        return entries;
    }

    public void setEntries(List<Entry> entries) {
        this.entries = entries;
    }
}
