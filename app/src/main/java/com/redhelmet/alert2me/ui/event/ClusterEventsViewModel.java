package com.redhelmet.alert2me.ui.event;

import android.databinding.ObservableField;

import com.redhelmet.alert2me.data.model.Event;
import com.redhelmet.alert2me.ui.base.BaseViewModel;
import com.redhelmet.alert2me.ui.base.NavigationItem;
import com.redhelmet.alert2me.ui.eventdetail.EventDetailsActivity;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class ClusterEventsViewModel extends BaseViewModel {

    @Inject
    public ClusterEventsViewModel() {
    }

    public ObservableField<List<Event>> events = new ObservableField<>(new ArrayList<>());

    public void setEvents(List<Event> events) {
        this.events.set(events);
    }

    public void onEventClick(int position) {
        if (position < events.get().size()) {
            Event event = events.get().get(position);
            if (event != null)
                navigateTo(new NavigationItem(NavigationItem.START_ACTIVITY, EventDetailsActivity.class, EventDetailsActivity.createDataBundle(event)));
        }
    }
}
