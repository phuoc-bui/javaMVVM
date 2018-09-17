package com.redhelmet.alert2me.model;

import java.util.List;

/**
 * Created by inbox on 6/2/18.
 */

public class ObservationGroups {

    String icon;
    String name;
    List<ObservationTypes> types;

    public String getName() {
        return this.name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getIcon() {
        return this.icon;
    }
    public void setIcon(String icon) {
        this.icon = icon;
    }

      public List<ObservationTypes> getTypes() {
        return this.types;
    };
    public void setTypes(List<ObservationTypes> types) {
        this.types = types;
    }

}
