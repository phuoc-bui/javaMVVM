package com.redhelmet.alert2me.data.model;

import com.redhelmet.alert2me.data.model.base.Model;

public class Geometry implements Model {
    public static final String POINT_TYPE = "Point";
    public static final String POLYGON_TYPE = "Polygon";
    private String type;
    private double[][][] coordinates;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public double[][][] getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(double[][][] coordinates) {
        this.coordinates = coordinates;
    }
}
