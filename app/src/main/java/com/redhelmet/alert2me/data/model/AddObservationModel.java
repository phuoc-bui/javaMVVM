package com.redhelmet.alert2me.data.model;

import com.google.android.gms.location.places.Place;
import com.google.android.gms.maps.model.LatLng;

/**
 * Created by inbox on 8/2/18.
 */

public class AddObservationModel {

    private static AddObservationModel instance;
    public LatLng latLng;
    public Place place;
    public String comments;
    public String whatText;
    public String group;
    public String type;
    public String icon;

    public AddObservationModel() {
    }

    public static AddObservationModel getInstance() {

        if (instance == null)
            instance = new AddObservationModel();
        return instance;
    }

    public LatLng getLatLng(){
        return  this.latLng;
    }
    public void setLatLng(LatLng latLng){
          this.latLng=latLng;
    }

    public Place getPlace( ){
        return this.place;
    }

    public void setPlace(Place place){
        this.place=place;
    }

    public void setComments(String comments){
        this.comments=comments;
    }

    public String getComments(){
        return this.comments;
    }

    public String getWhatText(){
        if(this.whatText==null)
            return "";
        else
        return this.whatText;
    }

    public void setWhatText(String whatText){
        this.whatText=whatText;
    }

    public String getGroup(){
        if(this.group==null)
            return "";
        else
            return this.group;
    }

    public void setGroup(String group){
        this.group=group;
    }

    public String getType(){
        if(this.type==null)
            return "";
        else
            return this.type;
    }

    public void setType(String type){
        this.type=type;
    }

    public String getIcon(){
        if(this.icon==null)
            return "";
        else
            return this.icon;
    }

    public void setIcon(String icon){
        this.icon=icon;
    }
}
