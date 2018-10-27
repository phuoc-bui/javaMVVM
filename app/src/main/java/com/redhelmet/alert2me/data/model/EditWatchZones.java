package com.redhelmet.alert2me.data.model;

import com.redhelmet.alert2me.data.model.base.UserModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

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
    private String type;
//    private ArrayList<HashMap<String, CategoryFilter>> filter;
//    private List<Integer> filterGroupId;
    private boolean enable;
    private boolean proximity;
    private boolean isDefault;
    private boolean noEdit;
    private String shareCode;
//    private WatchZoneGeom watchZoneGeoms;

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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

//    public ArrayList<HashMap<String, CategoryFilter>> getFilter() {
//        return filter;
//    }
//
//    public void setFilter(ArrayList<HashMap<String, CategoryFilter>> filter) {
//        this.filter = filter;
//    }
//
//    public List<Integer> getFilterGroupId() {
//        return filterGroupId;
//    }
//
//    public void setFilterGroupId(List<Integer> filterGroupId) {
//        this.filterGroupId = filterGroupId;
//    }

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

//    public WatchZoneGeom getWatchZoneGeoms() {
//        return watchZoneGeoms;
//    }
//
//    public void setWatchZoneGeoms(WatchZoneGeom watchZoneGeoms) {
//        this.watchZoneGeoms = watchZoneGeoms;
//    }
}
