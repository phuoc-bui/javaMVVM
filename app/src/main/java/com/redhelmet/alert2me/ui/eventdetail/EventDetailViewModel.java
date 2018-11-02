package com.redhelmet.alert2me.ui.eventdetail;

import com.redhelmet.alert2me.data.model.Area;
import com.redhelmet.alert2me.data.model.Entry;
import com.redhelmet.alert2me.data.model.Event;
import com.redhelmet.alert2me.data.model.Section;
import com.redhelmet.alert2me.ui.base.BaseViewModel;
import com.redhelmet.alert2me.ui.base.NavigationItem;
import com.redhelmet.alert2me.ui.home.HomeActivity;
import com.redhelmet.alert2me.util.EventUtils;

import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

import androidx.databinding.ObservableField;

public class EventDetailViewModel extends BaseViewModel {

    public ObservableField<Event> event = new ObservableField<>();
    public ObservableField<String> eventLocation = new ObservableField<>();
    public ObservableField<String> eventColor = new ObservableField<>();
    public ObservableField<String> eventTimeAgo = new ObservableField<>();
    public ObservableField<String> eventDistance = new ObservableField<>();

    public EventSectionAdapter sectionAdapter = new EventSectionAdapter();

    @Inject
    public EventDetailViewModel() {
        super();
    }

    public void setEvent(Event event) {
        this.event.set(event);
        // location
        List<Area> areas = event.getArea();
        Area area = areas.get(0);
        String location = (area.getLocation() == null) ? "" : area.getLocation();
        String eventState = (area.getState() == null) ? "" : area.getState();
        eventLocation.set(String.format("%s %s", location, eventState));

        Double distance = (event.getDistanceTo() / 1000);
        String formattedDistance = String.format(Locale.getDefault(), "%.1f km", distance);
        eventDistance.set(formattedDistance);

        eventColor.set(event.getPrimaryColor());

        Date updatedTime = new Date(event.getUpdated());
        eventTimeAgo.set(EventUtils.getDetailTimeAgo(updatedTime));

        if (event.getSection() == null || event.getSection().isEmpty()) {
            navigateTo(new NavigationItem(NavigationItem.SHOW_TOAST, "Event Expired"));
            navigateTo(new NavigationItem(NavigationItem.START_ACTIVITY_AND_FINISH, HomeActivity.class));
        } else {
            for (Section section : event.getSection()) {
                // add title
                sectionAdapter.itemsSource.add(new ItemSectionViewModel(section.getName(), event.getPrimaryColor()));
                for (Entry entry : section.getEntries()) {
                    sectionAdapter.itemsSource.add(new ItemSectionViewModel(entry, event.getPrimaryColor()));
                }
            }
        }
    }
}
