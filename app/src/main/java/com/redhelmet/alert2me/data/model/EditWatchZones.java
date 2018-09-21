package com.redhelmet.alert2me.data.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class EditWatchZones implements Serializable {

    private static EditWatchZones instance;

    private  String watchzone_id;
    private  String watchzone_device_id;
    private  String watchzone_sound;
    private  String watchzone_address;
    private  String watchzone_name;
    private  String watchzone_radius;
    private  String watchzone_type;
    private ArrayList<HashMap<String, CategoryFilter>> watchzone_filter;
    private List<Integer> watchzone_filterGroupId;
    private  boolean watchzone_enable;
    private  boolean watchzone_proximity;
    private  boolean watchzone_isDefault;
    private  boolean watchzone_noEdit;
    private  String watchzone_shareCode;
    private WatchZoneGeom watchZoneGeoms;

    public static ArrayList<EditWatchZones> edit_Wz;



    public EditWatchZones() {
    }

    public static EditWatchZones getInstance() {

        if (instance == null)
            instance = new EditWatchZones();
        return instance;
    }
    public String getWatchzoneDeviceId(){
        return watchzone_device_id;
    }

    public void setWatchzoneDeviceId(String wzDeviceId ){
        this.watchzone_device_id=wzDeviceId;
    }

    public String getWatchzoneId(){
        return watchzone_id;
    }

    public void setWatchzoneId(String wzId ){
        this.watchzone_id=wzId;
    }

    public String getWatchzoneName(){
        return watchzone_name;
    }

    public void setWatchzoneName(String wzName ){
        this.watchzone_name=wzName;
    }

    public String getWatchzoneSound(){
        return watchzone_sound;
    }

    public void setWatchzoneSound(String wzSound ){
        this.watchzone_sound=wzSound;
    }

    public String getWatchzoneAddress(){
        return watchzone_address;
    }

    public void setWatchzoneAddress(String wzAddress ){
        this.watchzone_address=wzAddress;
    }

    public boolean getWatchzoneProximity(){
        return watchzone_proximity;
    }

    public void setWatchzoneProximity(boolean wzProximity ){
        this.watchzone_proximity=wzProximity;
    }

    public String getWatchzoneRadius(){
        return watchzone_radius;
    }

    public void setWatchzoneRadius(String wzRadius ){
        this.watchzone_radius=wzRadius;
    }

    public String getWatchzoneType(){
        return watchzone_type;
    }

    public void setWatchzoneType(String wzType ){
        this.watchzone_type=wzType;
    }

    public ArrayList<HashMap<String, CategoryFilter>> getWatchzoneFilter(){
        return watchzone_filter;
    }

    public void setWatchzoneFilter(ArrayList<HashMap<String, CategoryFilter>> wzFilter ){
        this.watchzone_filter=wzFilter;
    }

    public List<Integer> getWatchzoneFilterGroupId(){
        return watchzone_filterGroupId;
    }

    public void setWatchzoneFilterGroupId(List<Integer> wzFilterGroup ){
        this.watchzone_filterGroupId=wzFilterGroup;
    }

    public boolean isWzEnable(){
        return watchzone_enable;
    }

    public void setWzEnable(boolean wzEnable ){
        this.watchzone_enable=wzEnable;
    }

    public boolean isWzDefault(){
        return watchzone_isDefault;
    }

    public void setWzDefault(boolean wzDefault ){
        this.watchzone_isDefault=wzDefault;
    }
    public boolean isWzNoEdit(){
        return watchzone_noEdit;
    }

    public void setWzNoEdit(boolean wzEdit ){
        this.watchzone_noEdit=wzEdit;
    }

    public String getWatchZoneShareCode(){
        return watchzone_shareCode;
    }

    public void setWatchZoneShareCode(String shareCode ){
        this.watchzone_shareCode=shareCode;
    }

    public void setEditWz(ArrayList<EditWatchZones> wzData) {

        this.edit_Wz= wzData;
    }

    public ArrayList<EditWatchZones> getEditWz() {

        return this.edit_Wz;
    }

    public void setWatchZoneGeoms(WatchZoneGeom wzGeom) {

        this.watchZoneGeoms= wzGeom;
    }

    public WatchZoneGeom getWatchZoneGeoms() {

        return this.watchZoneGeoms;
    }
}
