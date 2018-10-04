package com.redhelmet.alert2me.ui.home.event;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.location.Location;

import com.google.common.collect.ComparisonChain;
import com.redhelmet.alert2me.R;
import com.redhelmet.alert2me.data.DataManager;
import com.redhelmet.alert2me.data.model.Area;
import com.redhelmet.alert2me.data.model.Event;
import com.redhelmet.alert2me.ui.eventdetail.EventDetailsActivity;
import com.redhelmet.alert2me.ui.base.BaseViewModel;
import com.redhelmet.alert2me.ui.base.NavigationItem;

import java.util.Collections;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;

public class EventViewModel extends BaseViewModel {
    public MutableLiveData<List<Event>> events = new MutableLiveData<>();

    public ObservableField<List<EventItemViewModel>> eventItemViewModelList = new ObservableField<>();

    public ObservableBoolean isEmpty = new ObservableBoolean();


    public MutableLiveData<Boolean> isRefreshing = new MutableLiveData<>();

    private boolean isStateWide = true;

    private int currentSortType = 0;

    private Observer<List<Event>> eventsObserver;

    public Runnable onRefresh = () -> {
        isRefreshing.setValue(true);
        getEvents();
    };

    public EventViewModel(DataManager dataManager) {
        super(dataManager);
        getEvents();
        eventsObserver = events -> {
            if (events != null) {
                disposeBag.add(Observable.fromIterable(events)
                        .map(event -> new EventItemViewModel(setDistanceForEvents(event), isStateWide))
                        .toList()
                        .subscribe(viewModels -> eventItemViewModelList.set(viewModels)));
            }
        };
        events.observeForever(eventsObserver);
    }

    private void getEvents() {
        isLoading.set(true);
        disposeBag.add(dataManager.getEventsWithFilter(isDefaultFilter())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(data -> {
                    isLoading.set(false);
                    isRefreshing.setValue(false);
                    events.setValue(data);
                    if (data == null || data.size() == 0) isEmpty.set(true);
                    else isEmpty.set(false);
                }, error -> {
                    isLoading.set(false);
                    isRefreshing.setValue(false);
                    navigationEvent.setValue(new com.redhelmet.alert2me.global.Event<>(new NavigationItem(NavigationItem.SHOW_TOAST, R.string.msgUnableToGetEvent)));
                }));
    }

    public void onEventClick(int position) {
        Event event = events.getValue().get(position);
        navigationEvent.setValue(new com.redhelmet.alert2me.global.Event<>(new NavigationItem(NavigationItem.START_ACTIVITY, EventDetailsActivity.class, EventDetailsActivity.createDataBundle(event))));
    }

    public void saveUserLocation(Location location) {
        dataManager.saveUserLocation(location);
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
        switch (currentSortType) {
            case 0:
                SortByDistance();
                break;
            case 1:
                SortByTime();
                break;
            case 2:
                SortByStatus();
                break;
            default:
                SortByStatus();
                break;
        }
    }

    private void SortByTime() {
        Collections.sort(events.getValue(), (event, event2) -> Long.compare(event2.getUpdated(), event.getUpdated()));
    }

    private void SortByDistance() {

        Collections.sort(events.getValue(), (event, event2) -> event.getDistanceTo() > event2.getDistanceTo() ? 1 : (event.getDistanceTo() < event2.getDistanceTo() ? -1 : 0));
    }

    private void SortByStatus() {
        Collections.sort(events.getValue(), (event, event2) -> ComparisonChain.start().compare(event2.getSeverity(), event.getSeverity()).compare(event2.getUpdated(), event.getUpdated()).result());
    }

    @Override
    protected void onCleared() {
        events.removeObserver(eventsObserver);
        super.onCleared();
    }
}
