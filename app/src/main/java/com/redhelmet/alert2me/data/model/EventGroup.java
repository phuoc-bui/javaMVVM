package com.redhelmet.alert2me.data.model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import com.redhelmet.alert2me.data.model.base.UserModel;

import java.util.List;

@Entity(tableName = "EventGroup")
public class EventGroup extends UserModel {
    private String description;
    private List<EventGroupDisplayFilter> displayFilter;
    private boolean displayOn;
    private boolean displayToggle;
    private boolean displayOnly;
    private List<String> eventCodes;
    private boolean filterOn;
    private boolean filterToggle;
    @PrimaryKey
    private long id;
    private String name;
    private int order;

    public long getId() {
        return this.id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getOrder() {
        return this.order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isDisplayOn() {
        return this.displayOn;
    }

    public void setDisplayOn(boolean displayOn) {
        this.displayOn = displayOn;
    }

    public boolean isDisplayToggle() {
        return this.displayToggle;
    }

    public void setDisplayToggle(boolean displayToggle) {
        this.displayToggle = displayToggle;
    }

    public boolean isDisplayOnly() {
        return displayOnly;
    }

    public void setDisplayOnly(boolean displayOnly) {
        this.displayOnly = displayOnly;
    }

    public boolean isFilterOn() {
        return this.filterOn;
    }

    public void setFilterOn(boolean filterOn) {
        this.filterOn = filterOn;
    }

    public boolean isFilterToggle() {
        return this.filterToggle;
    }

    public void setFilterToggle(boolean filterToggle) {
        this.filterToggle = filterToggle;
    }

    public List<EventGroupDisplayFilter> getDisplayFilter() {
        return this.displayFilter;
    }

    public void setDisplayFilter(List<EventGroupDisplayFilter> displayFilter) {
        this.displayFilter = displayFilter;
    }

    public List<String> getEventCodes() {
        return this.eventCodes;
    }

    public void setEventCodes(List<String> eventCodes) {
        this.eventCodes = eventCodes;
    }
}