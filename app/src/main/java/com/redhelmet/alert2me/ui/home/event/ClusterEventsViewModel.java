package com.redhelmet.alert2me.ui.home.event;

import android.databinding.ObservableField;

import com.redhelmet.alert2me.data.model.Event;
import com.redhelmet.alert2me.ui.activity.EventDetailsActivity;
import com.redhelmet.alert2me.ui.base.BaseViewModel;
import com.redhelmet.alert2me.ui.base.NavigationItem;

import java.util.ArrayList;
import java.util.List;

public class ClusterEventsViewModel extends BaseViewModel {
    public ObservableField<List<Event>> events = new ObservableField<>(new ArrayList<>());

    public void setEvents(List<Event> events) {
        this.events.set(events);
    }

    public void onEventClick(int position) {
        if (position < events.get().size()) {
            Event event = events.get().get(position);
            if (event != null)
                navigationEvent.setValue(new com.redhelmet.alert2me.global.Event<>(new NavigationItem(NavigationItem.START_ACTIVITY, EventDetailsActivity.class, EventDetailsActivity.createDataBundle(event))));
        }
    }
}
