package com.redhelmet.alert2me.data.model;

import com.redhelmet.alert2me.data.model.base.Model;

import java.util.ArrayList;
import java.util.List;

public class Event implements Model {

    private long id;
    private long created;
    private long updated;
    private String detailId;
    private String category;
    private String name;
    private String type;
    private String status;
    private String statusCode;
    private int severity;
    private String icon;
    private String group;
    private String textShade;
    private String textColor;
    private String primaryColor;
    private String secondaryColor;
    private String sequence;
    private boolean notificationSent;
    private boolean stateWide;
    private List<Area> area;
    private List<Section> section;
    private Double distanceTo = 0.0;
    private long timeAgo;
    private String eventType;
    private String eventTypeCode;
    private long notificationId;
    private String temporal;
    private boolean alwaysOn;
    private boolean showOn;
    private Geometry geometry = null;

    public Double getDistanceTo() {
        return distanceTo;
    }

    public void setDistanceTo(Double distanceTo) {
        this.distanceTo = distanceTo;
    }

    public long getTimeAgo() {
        return timeAgo;
    }

    public void setTimeAgo(long timeAgo) {
        this.timeAgo = timeAgo;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getCreated() {
        return created;
    }

    public void setCreated(long created) {
        this.created = created;
    }

    public long getUpdated() {
        return updated;
    }

    public void setUpdated(long updated) {
        this.updated = updated;
    }

    public String getDetailId() {
        return detailId;
    }

    public void setDetailId(String detailId) {
        this.detailId = detailId;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getStatus() {
        return status;
    }

    public String getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getSeverity() {
        return severity;
    }

    public void setSeverity(int severity) {
        this.severity = severity;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getTextShade() {
        return textShade;
    }

    public void setTextShade(String textShade) {
        this.textShade = textShade;
    }

    public String getTextColor() {
        return textColor;
    }

    public void setTextColor(String textColor) {
        this.textColor = textColor;
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

    public String getSequence() {
        return sequence;
    }

    public void setSequence(String sequence) {
        this.sequence = sequence;
    }

    public boolean isNotificationSent() {
        return notificationSent;
    }

    public void setNotificationSent(boolean notificationSent) {
        this.notificationSent = notificationSent;
    }

    public boolean isStateWide() {
        return stateWide;
    }

    public void setStateWide(boolean stateWide) {
        this.stateWide = stateWide;
    }

    public List<Area> getArea() {
        return area;
    }

    public void setArea(List<Area> area) {
        this.area = area;
    }

    public List<Section> getSection() {
        return section;
    }

    public void setSection(List<Section> section) {
        this.section = section;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public String getEventTypeCode() {
        return eventTypeCode;
    }

    public void setEventTypeCode(String eventTypeCode) {
        this.eventTypeCode = eventTypeCode;
    }

    public long getNotificationId() {
        return notificationId;
    }

    public void setNotificationId(long notificationId) {
        this.notificationId = notificationId;
    }

    public String getTemporal() {
        return temporal;
    }

    public void setTemporal(String temporal) {
        this.temporal = temporal;
    }

    public boolean isAlwaysOn() {
        return alwaysOn;
    }

    public void setAlwaysOn(boolean alwaysOn) {
        this.alwaysOn = alwaysOn;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public void setShowOn(boolean show) {
        this.showOn = show;
    }

    public boolean isShowOn() {
        return this.showOn;
    }

    public Geometry getGeometry() {
        return geometry;
    }

    public void setGeometry(Geometry geometry) {
        this.geometry = geometry;
    }

    public static class EventList extends ArrayList<Event> {}
}
