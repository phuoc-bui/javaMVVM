package com.redhelmet.alert2me.ui.clusterevents;

import com.redhelmet.alert2me.adapters.EventListRecyclerAdapter;
import com.redhelmet.alert2me.data.model.Event;
import com.redhelmet.alert2me.ui.base.BaseViewModel;
import com.redhelmet.alert2me.ui.base.NavigationItem;
import com.redhelmet.alert2me.ui.eventdetail.EventDetailsActivity;

import java.util.List;

import javax.inject.Inject;

public class ClusterEventsViewModel extends BaseViewModel {

    public EventListRecyclerAdapter adapter;

    @Inject
    public ClusterEventsViewModel() {
        adapter = new EventListRecyclerAdapter();
    }

    public void setEvents(List<Event> events) {
        adapter.setData(events);
    }

    public void onEventClick(int position) {
        if (position < adapter.itemsSource.size()) {
            Event event = adapter.itemsSource.get(position).event.get();
            if (event != null)
                navigateTo(new NavigationItem(NavigationItem.START_ACTIVITY, EventDetailsActivity.class, EventDetailsActivity.createDataBundle(event)));
        }
    }
}
