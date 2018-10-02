package com.redhelmet.alert2me.ui.home.event;

import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.graphics.Bitmap;

import com.redhelmet.alert2me.R;
import com.redhelmet.alert2me.data.model.Area;
import com.redhelmet.alert2me.data.model.Event;
import com.redhelmet.alert2me.ui.base.BaseViewModel;
import com.redhelmet.alert2me.util.EventUtils;
import com.redhelmet.alert2me.util.IconUtils;

import java.util.Date;
import java.util.List;
import java.util.Locale;

public class EventItemViewModel extends BaseViewModel {
    public ObservableField<Event> event = new ObservableField<>();
    public ObservableField<Bitmap> eventIcon = new ObservableField<>();
    public ObservableBoolean isStateWide = new ObservableBoolean(false);
    public ObservableField<String> eventLocation = new ObservableField<>();
    public ObservableField<String> eventTimeAgo = new ObservableField<>();
    public ObservableField<String> eventDistance = new ObservableField<>();

    public EventItemViewModel(Event event, boolean isStateWide) {
        this.event.set(event);
        this.isStateWide.set(isStateWide);
        List<Area> areas = event.getArea();
        Area area = areas.get(0);
        String location = (area.getLocation() == null) ? "" : area.getLocation();
        String eventState = (area.getState() == null) ? "" : area.getState();
        eventLocation.set(String.format("%s %s", location, eventState));
        Date updated = new Date(event.getUpdated());
        eventTimeAgo.set(EventUtils.getTimeAgo(updated));

        Double distance = (event.getDistanceTo() / 1000);
        String formattedDistance = String.format(Locale.getDefault(), "%.1f km", distance);
        eventDistance.set(formattedDistance);

        String backgroundColor = event.getPrimaryColor();
        Bitmap icon = IconUtils.createEventIcon(R.layout.custom_list_layer_icon, event, backgroundColor, true, false, "");
        eventIcon.set(icon);
    }
}
