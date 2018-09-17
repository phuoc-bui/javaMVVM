package com.redhelmet.alert2me.model;

import java.io.Serializable;
import java.util.List;

public class CategoryType implements Serializable{
    private String code;
    private String name;
    private String nameLabel;
    private boolean canFilter;
    private boolean defaultOn;
    private boolean notificationCanFilter;
    private boolean notificationDefaultOn;
    private String icon;
    private List<CategoryStatus> statuses;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getNameLabel() {
        return nameLabel;
    }

    public void setNameLabel(String nameLabel) {
        this.nameLabel = nameLabel;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isCanFilter() {
        return canFilter;
    }

    public void setCanFilter(boolean canFilter) {
        this.canFilter = canFilter;
    }

    public boolean isDefaultOn() {
        return defaultOn;
    }

    public void setDefaultOn(boolean defaultOn) {
        this.defaultOn = defaultOn;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public boolean isNotificationCanFilter() {
        return notificationCanFilter;
    }

    public void setNotificationCanFilter(boolean canFilter) {
        this.notificationCanFilter = canFilter;
    }

    public boolean isNotificationDefaultOn() {
        return notificationDefaultOn;
    }

    public void setNotificationDefaultOn(boolean defaultOn) {
        this.notificationDefaultOn = defaultOn;
    }

    public List<CategoryStatus> getStatuses() {
        return statuses;
    }

    public void setStatuses(List<CategoryStatus> statuses) {
        this.statuses = statuses;
    }


}
