package com.redhelmet.alert2me.data.model;

import com.redhelmet.alert2me.data.model.base.Model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

public class WatchZoneGeom implements Model {
    private String type;
    private ArrayList<HashMap<String,Double>> cordinate;


    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public ArrayList<HashMap<String,Double>> getCordinate() {
        return cordinate;
    }

    public void setCordinate(ArrayList<HashMap<String,Double>> cordinate) {
        this.cordinate = cordinate;
    }



}
