package com.redhelmet.alert2me.data.model;

import com.redhelmet.alert2me.data.model.base.UserModel;

import java.util.List;

import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "WatchZone")
public class EditWatchZones extends UserModel {

    @PrimaryKey
    private long id;
    private String deviceId;
    private String sound;
    private String address;
    private String name;
    private String radius;
    private String wzType;
    @Embedded
    private WatchZoneFilter filter;
    private List<Integer> filterGroupId;
    private boolean enable;
    private boolean proximity;
    private boolean isDefault;
    private boolean noEdit;
    private String shareCode;
    @Embedded
    private Geometry geom;

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

    public String getRadius() {
        return radius;
    }

    public void setRadius(String radius) {
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
}
