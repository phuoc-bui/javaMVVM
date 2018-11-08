package com.redhelmet.alert2me.data.model;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

import java.util.List;

public class ClusterMarker implements ClusterItem {
    private final LatLng mPosition;
    private Event event;
    private  List<Event> objects;
    private String title;
    private String snippet;

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public ClusterMarker(Event event, Area area) {
        this.event = event;
        this.title = "events";
        mPosition = new LatLng(area.getLatitude(), area.getLongitude());
    }

    public  void setObjects(List<Event> events) {
       this.objects = events;
       this.snippet = String.valueOf(events.size());
    }

    public List<Event> getObjects()
        {
            return objects;
    }
    @Override
    public LatLng getPosition() {
        return mPosition;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public String getSnippet() {
        return snippet;
    }
}
