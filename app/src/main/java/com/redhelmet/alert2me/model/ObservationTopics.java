package com.redhelmet.alert2me.model;

import java.util.List;

/**
 * Created by inbox on 6/2/18.
 */

public class ObservationTopics {


    String name;
    List<ObservationGroups> groups;




    public String getName() {
        return this.name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public List<ObservationGroups> getGroups() {
        return this.groups;
    }
    public void setGroups(List<ObservationGroups> groups) {
        this.groups = groups;
    }

}
