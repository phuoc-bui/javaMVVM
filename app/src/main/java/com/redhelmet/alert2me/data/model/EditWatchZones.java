package com.redhelmet.alert2me.data.model;

import com.google.gson.annotations.SerializedName;
import com.redhelmet.alert2me.data.model.base.Model;

import java.util.List;

public class EditWatchZones implements Model {

    public static final String CIRCLE_TYPE = "STANDARD";
    public static final String POLYGON_TYPE = "VARIABLE";

    private long id;
    private String deviceId;
    private String sound;
    private String address;
    private String name;
    private int radius;
    @SerializedName("type")
    private String wzType;
    private WatchZoneFilter filter;
    private List<Integer> filterGroupId;
    private boolean enable;
    private boolean proximity;
    private boolean isDefault;
    private boolean noEdit;
    private String shareCode;
    private Geometry geom;

    public EditWatchZones() {
        radius = 5;
        wzType = CIRCLE_TYPE;
        geom = new Geometry();
        geom.setType(Geometry.POINT_TYPE);

    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public String getSound() {
        return sound;
    }

    public void setSound(String sound) {
        this.sound = sound;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getRadius() {
        return radius;
    }

    public void setRadius(int radius) {
        this.radius = radius;
    }

    public String getWzType() {
        return wzType;
    }

    public void setWzType(String type) {
        this.wzType = type;
    }

    public WatchZoneFilter getFilter() {
        return filter;
    }

    public void setFilter(WatchZoneFilter filter) {
        this.filter = filter;
    }

    public List<Integer> getFilterGroupId() {
        return filterGroupId;
    }

    public void setFilterGroupId(List<Integer> filterGroupId) {
        this.filterGroupId = filterGroupId;
    }

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public boolean isProximity() {
        return proximity;
    }

    public void setProximity(boolean proximity) {
        this.proximity = proximity;
    }

    public boolean isDefault() {
        return isDefault;
    }

    public void setDefault(boolean aDefault) {
        isDefault = aDefault;
    }

    public boolean isNoEdit() {
        return noEdit;
    }

    public void setNoEdit(boolean noEdit) {
        this.noEdit = noEdit;
    }

    public String getShareCode() {
        return shareCode;
    }

    public void setShareCode(String shareCode) {
        this.shareCode = shareCode;
    }

    public Geometry getGeom() {
        return geom;
    }

    public void setGeom(Geometry geom) {
        this.geom = geom;
    }

    public static class WatchZoneFilter implements Model {

        private Filter warning;
        private Filter incident;
        private Filter restriction;
        private Filter support_service;

        public Filter getWarning() {
            return warning;
        }

        public void setWarning(Filter warning) {
            this.warning = warning;
        }

        public Filter getIncident() {
            return incident;
        }

        public void setIncident(Filter incident) {
            this.incident = incident;
        }

        public Filter getRestriction() {
            return restriction;
        }

        public void setRestriction(Filter restriction) {
            this.restriction = restriction;
        }

        public Filter getSupport_service() {
            return support_service;
        }

        public void setSupport_service(Filter support_service) {
            this.support_service = support_service;
        }
    }

    public static class Filter {
        private List<WatchZoneFilterType> types;

        public List<WatchZoneFilterType> getTypes() {
            return types;
        }

        public void setTypes(List<WatchZoneFilterType> types) {
            this.types = types;
        }
    }
}
