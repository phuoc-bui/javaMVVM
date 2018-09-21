package com.redhelmet.alert2me.data.model;

import java.util.List;

/**
 * Created by inbox on 6/2/18.
 */

public class Observations {

    private static Observations instance;

    List<ObservationTopics> topics;


    public Observations() {
    }

    public static Observations getInstance() {

        if (instance == null)
            instance = new Observations();
        return instance;
    }

    public List<ObservationTopics> getTopics() {
        return topics;
    }

    public void setTopics(List<ObservationTopics> topics) {
        this.topics = topics;
    }
}
