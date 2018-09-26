package com.redhelmet.alert2me.data.remote.request;

public class ProximityLocationRequest {
    public String speed;
    public String movement;
    public double latitude;
    public double longitude;

    public ProximityLocationRequest(double latitude, double longitude) {
        speed = "0.0 km/hr";
        movement = "Not Moving";
        this.latitude = latitude;
        this.longitude = longitude;
    }
}
