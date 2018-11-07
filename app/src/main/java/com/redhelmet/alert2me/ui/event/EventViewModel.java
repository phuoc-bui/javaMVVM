package com.redhelmet.alert2me.ui.event;

import android.location.Location;

import com.google.common.collect.ComparisonChain;
import com.redhelmet.alert2me.adapters.EventListRecyclerAdapter;
import com.redhelmet.alert2me.data.DataManager;
import com.redhelmet.alert2me.data.model.Area;
import com.redhelmet.alert2me.data.model.Event;
import com.redhelmet.alert2me.ui.base.BaseViewModel;
import com.redhelmet.alert2me.ui.base.NavigationItem;
import com.redhelmet.alert2me.ui.eventdetail.EventDetailsActivity;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import javax.inject.Inject;

import androidx.databinding.ObservableBoolean;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;

public class EventViewModel extends BaseViewModel {

    public enum SortType {
        DISTANCE, TIME, STATUS;

        public static SortType fromInt(int index) {
            for (SortType type : values()) {
                if (type.ordinal() == index) return type;
            }
            return STATUS;
        }
    }

    public EventListRecyclerAdapter adapter = new EventListRecyclerAdapter();
    // flag to show/hide empty text view in event list fragment
    public ObservableBoolean isEmpty = new ObservableBoolean(true);
    // flag to start/stop refresh menu animation
    public MutableLiveData<Boolean> isRefreshing = new MutableLiveData<>();
    private boolean isStateWide = true;
    // sort mode in even list fragment
    private SortType currentSortType = SortType.STATUS;

    // for map fragment clear map
    public MutableLiveData<Boolean> onClearEvents = new MutableLiveData<>();

    public MutableLiveData<List<Event>> events = new MutableLiveData<>();
    private Observer<List<Event>> eventsObserver;

    public Runnable onRefresh = () -> {
        isRefreshing.setValue(true);
        getEvents();
    };

    @Inject
    public EventViewModel(DataManager dataManager) {
        super(dataManager);
        events.setValue(new ArrayList<>());
        getEvents();
        // update event list fragment when event list change
        eventsObserver = events -> {
            if (events != null) {
                if (events.isEmpty()) isEmpty.set(true);
                else isEmpty.set(false);
                updateEventList();
            } else {
                isEmpty.set(true);
            }
        };
        events.observeForever(eventsObserver);
    }

    private void getEvents() {
        isLoading.set(true);
//        isEmpty.set(true);
        onClearEvents.setValue(true);
        disposeBag.add(dataManager.getEventsWithFilter(isDefaultFilter(), getSortComparator())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(data -> events.setValue(data),
                        this::handleError,
                        () -> {
                            isLoading.set(false);
                            isRefreshing.setValue(false);
                        }));
    }

    public void onEventClick(int position) {
        Event event = events.getValue().get(position);
        navigateTo(new NavigationItem(NavigationItem.START_ACTIVITY, EventDetailsActivity.class, EventDetailsActivity.createDataBundle(event)));
    }

    public void saveUserLocation(Location location) {
        if (location != null) {
            Location oldLocation = dataManager.getLastUserLocation();
            dataManager.saveUserLocation(location);
            // update event list if user location is changed
            if (oldLocation == null || oldLocation.getLatitude() != location.getLatitude() || oldLocation.getLongitude() != location.getLongitude())
                updateEventList();
        }
    }

    private void updateEventList() {
        if (events.getValue() != null) {
            disposeBag.add(Observable.fromIterable(events.getValue())
                    .map(event -> new EventItemViewModel(setDistanceForEvents(event), isStateWide))
                    .toList()
                    .subscribe(viewModels -> {
                        adapter.itemsSource.clear();
                        adapter.itemsSource.addAll(viewModels);
                    }));
        }
    }

    private Event setDistanceForEvents(Event event) {
        List<Area> areas = event.getArea();

        Area area = areas.get(0);
        Location userLocation = dataManager.getLastUserLocation();
        Location eventLocation = new Location("EventLocation");
        eventLocation.setLatitude(area.getLatitude());
        eventLocation.setLongitude(area.getLongitude());

        if (userLocation.getLatitude() == 0 && userLocation.getLongitude() == 0) {

            event.setDistanceTo((double) 0.0f);
        } else {
            double distance = userLocation.distanceTo(eventLocation);
            event.setDistanceTo(distance);
        }
        return event;
    }

    public boolean isDefaultFilter() {
        return dataManager.isDefaultFilter();
    }

    public void sortList() {
        adapter.sortItemSource(getSortComparator());
    }

    private Comparator<Event> getSortComparator() {
        switch (currentSortType) {
            case DISTANCE:
                return (o1, o2) -> o1.getDistanceTo() > o2.getDistanceTo() ? 1 : (o1.getDistanceTo() < o2.getDistanceTo() ? -1 : 0);
            case TIME:
                return (o1, o2) -> Long.compare(o2.getUpdated(), o1.getUpdated());
            case STATUS:
                return (o1, o2) -> ComparisonChain.start().compare(o2.getSeverity(), o1.getSeverity()).compare(o2.getUpdated(), o1.getUpdated()).result();
            default:
                return (o1, o2) -> ComparisonChain.start().compare(o2.getSeverity(), o1.getSeverity()).compare(o2.getUpdated(), o1.getUpdated()).result();
        }
    }

    @Override
    protected void onCleared() {
        events.removeObserver(eventsObserver);
        super.onCleared();
    }

    public void setCurrentSortType(int index) {
        this.currentSortType = SortType.fromInt(index);
    }

    public SortType getCurrentSortType() {
        return currentSortType;
    }
}
