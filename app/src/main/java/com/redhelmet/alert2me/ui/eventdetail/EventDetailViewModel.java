package com.redhelmet.alert2me.ui.eventdetail;

import android.databinding.ObservableField;

import com.redhelmet.alert2me.data.model.Area;
import com.redhelmet.alert2me.data.model.Event;
import com.redhelmet.alert2me.data.model.Section;
import com.redhelmet.alert2me.global.EventIcon;
import com.redhelmet.alert2me.ui.base.BaseViewModel;
import com.redhelmet.alert2me.util.EventUtils;

import java.util.Date;
import java.util.List;

import javax.inject.Inject;

public class EventDetailViewModel extends BaseViewModel {

    public ObservableField<Event> event = new ObservableField<>();
    public ObservableField<EventIcon> eventIcon = new ObservableField<>();
    public ObservableField<String> eventLocation = new ObservableField<>();
    public ObservableField<String> eventColor = new ObservableField<>();
    public ObservableField<String> eventTimeAgo = new ObservableField<>();

    public EventSectionAdapter sectionAdapter = new EventSectionAdapter();

    @Inject
    public EventDetailViewModel() {
        super();
    }

    public void setEvent(Event event) {
        this.event.set(event);

        eventIcon.set(EventIcon.DETAIL_ICON.setEvent(event));
        // location
        List<Area> areas = event.getArea();
        Area area = areas.get(0);
        String location = (area.getLocation() == null) ? "" : area.getLocation();
        String eventState = (area.getState() == null) ? "" : area.getState();
        eventLocation.set(String.format("%s %s", location, eventState));

        eventColor.set(event.getPrimaryColor());

        Date updatedTime = new Date(event.getUpdated());
        eventTimeAgo.set(EventUtils.getDetailTimeAgo(updatedTime));

        for (Section section : event.getSection()) {
            sectionAdapter.itemsSource.add(new ItemSectionViewModel(section, event.getPrimaryColor(), event.getTextColor()));
        }
    }
}
