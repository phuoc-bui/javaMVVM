package com.redhelmet.alert2me.model;

import java.util.List;

public class EventGroup {

    private static EventGroup instance;
    private String description;
    private List<EventGroupDisplayFilter> displayFilter;
    private boolean displayOn;
    private boolean displayToggle;
    private boolean displayOnly;
    private List<String> eventCodes;
    private boolean filterOn;
    private boolean filterToggle;
    private int id;
    private String name;
    private int order;

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
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

    public EventGroup() {
    }

    public static EventGroup getInstance() {

        if (instance == null)
            instance = new EventGroup();
        return instance;
    }


}