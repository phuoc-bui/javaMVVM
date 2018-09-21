package com.redhelmet.alert2me.data.model;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

import java.util.List;

public class CustomMarker implements ClusterItem {
    private final LatLng mPosition;
    private Event event;
    private  List<Event> objects;

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public CustomMarker(Event event, Area area) {
        this.event = event;
        mPosition = new LatLng(area.getLatitude(), area.getLongitude());
    }

    public  void setObjects(List<Event> events) {
       this.objects = events;
    }

    public List<Event> getObjects()
        {
            return objects;
    }
    @Override
    public LatLng getPosition() {
        return mPosition;
    }
}
