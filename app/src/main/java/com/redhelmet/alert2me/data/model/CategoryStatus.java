package com.redhelmet.alert2me.data.model;

public class CategoryStatus implements Model {
    private String code;
    private String name;
    private String primaryColor;
    private String secondaryColor;
    private String textColor;
    private String textShade;
    private boolean highNotificationPriority;
    private boolean canFilter;
    private String description;
    private boolean defaultOn;
    private boolean notificationCanFilter;
    private boolean notificationDefaultOn;


    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrimaryColor() {
        return primaryColor;
    }

    public void setPrimaryColor(String primaryColor) {
        this.primaryColor = primaryColor;
    }

    public String getSecondaryColor() {
        return secondaryColor;
    }

    public void setSecondaryColor(String secondaryColor) {
        this.secondaryColor = secondaryColor;
    }

    public String getTextColor() {
        return textColor;
    }

    public void setTextColor(String textColor) {
        this.textColor = textColor;
    }

    public String getTextShade() {
        return textShade;
    }

    public void setTextShade(String textShade) {
        this.textShade = textShade;
    }

    public boolean isHighNotificationPriority() {
        return highNotificationPriority;
    }

    public void setHighNotificationPriority(boolean highNotificationPriority) {
        this.highNotificationPriority = highNotificationPriority;
    }

    public boolean isCanFilter() {
        return canFilter;
    }

    public void setCanFilter(boolean canFilter) {
        this.canFilter = canFilter;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isDefaultOn() {
        return defaultOn;
    }

    public void setDefaultOn(boolean defaultOn) {
        this.defaultOn = defaultOn;
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
}
