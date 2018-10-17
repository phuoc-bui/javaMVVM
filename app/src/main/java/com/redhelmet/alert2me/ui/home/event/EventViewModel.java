package com.redhelmet.alert2me.ui.home.event;

import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.databinding.ObservableBoolean;
import android.graphics.Color;
import android.location.Location;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.common.collect.ComparisonChain;
import com.redhelmet.alert2me.adapters.EventListRecyclerAdapter;
import com.redhelmet.alert2me.data.DataManager;
import com.redhelmet.alert2me.data.model.Area;
import com.redhelmet.alert2me.data.model.Event;
import com.redhelmet.alert2me.ui.base.BaseViewModel;
import com.redhelmet.alert2me.ui.base.NavigationItem;
import com.redhelmet.alert2me.ui.eventdetail.EventDetailsActivity;
import com.redhelmet.alert2me.util.IconUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.subjects.ReplaySubject;

public class EventViewModel extends BaseViewModel {

    public EventListRecyclerAdapter adapter = new EventListRecyclerAdapter();
    // flag to show/hide empty text view in event list fragment
    public ObservableBoolean isEmpty = new ObservableBoolean(true);
    // flag to start/stop refresh menu animation
    public MutableLiveData<Boolean> isRefreshing = new MutableLiveData<>();
    private boolean isStateWide = true;
    // sort mode in even list fragment
    private int currentSortType = 0;

    // Variables for one by one mode
    public boolean isLoadOneByOne = true;
    public ReplaySubject<Event> eventsOneByOne;
    // for map fragment clear map
    public MutableLiveData<Boolean> onClearEvents = new MutableLiveData<>();

    // Variables for normal mode
    public MutableLiveData<List<Event>> events = new MutableLiveData<>();
    private Observer<List<Event>> eventsObserver;

    public Runnable onRefresh = () -> {
        isRefreshing.setValue(true);
        if (isLoadOneByOne) getEventsOneByOne();
        else getEvents();
    };

    @Inject
    public EventViewModel(DataManager dataManager) {
        super(dataManager);
        events.setValue(new ArrayList<>());
        if (isLoadOneByOne) {
            eventsOneByOne = ReplaySubject.create();
            getEventsOneByOne();
            disposeBag.add(eventsOneByOne
                    .map(event -> new EventItemViewModel(setDistanceForEvents(event), isStateWide))
                    .subscribe(event -> {
                                adapter.itemsSource.add(event);
                                isEmpty.set(false);
                            },
                            this::handleError));
        } else {
            getEvents();
            // update event list fragment when event list change
            eventsObserver = events -> {
                if (events != null) {
                    if (events.isEmpty()) isEmpty.set(true);
                    else isEmpty.set(false);
                    disposeBag.add(Observable.fromIterable(events)
                            .map(event -> new EventItemViewModel(setDistanceForEvents(event), isStateWide))
                            .toList()
                            .subscribe(viewModels -> {
                                adapter.itemsSource.clear();
                                adapter.itemsSource.addAll(viewModels);
                            }));
                } else {
                    isEmpty.set(true);
                }
            };
            events.observeForever(eventsObserver);
        }
    }

    private void getEvents() {
        isLoading.set(true);
        isEmpty.set(true);
        onClearEvents.setValue(true);
        disposeBag.add(dataManager.getEventsWithFilter(isDefaultFilter())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(data -> {
                    isLoading.set(false);
                    isRefreshing.setValue(false);
                    events.setValue(data);
                }, error -> {
                    isLoading.set(false);
                    isRefreshing.setValue(false);
                    handleError(error);
                }));
    }

    private void getEventsOneByOne() {
        isLoading.set(true);
        // clear data
        onClearEvents.setValue(true);
        adapter.itemsSource.clear();
        isEmpty.set(true);

        List<Event> eventList = new ArrayList<>();
        disposeBag.add(dataManager.getEventsWithFilterOneByOne(isDefaultFilter())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(event -> {
                    eventsOneByOne.onNext(event);
                    eventList.add(event);
                    events.getValue().add(event);
                }, error -> {
                    eventsOneByOne.onError(error);
                    isLoading.set(false);
                    isRefreshing.setValue(false);
                    handleError(error);
                }, () -> {
                    isLoading.set(false);
                    isRefreshing.setValue(false);
                }));
    }

    public PolygonOptions createPolygonForEvent(Event event) {
        PolygonOptions polygon = new PolygonOptions();
        int strokeColor = Color.parseColor(event.getPrimaryColor());
        int fillColor = Color.parseColor(event.getPrimaryColor());
        polygon.strokeColor(strokeColor);
        polygon.fillColor(IconUtils.getColorWithAlpha(fillColor, 0.4f));
        for (double[] coordinate : event.getGeometry().getCoordinates()[0]) {
            polygon.add(new LatLng(coordinate[1], coordinate[0]));
        }
        return polygon;
    }

    public void onEventClick(int position) {
        Event event = events.getValue().get(position);
        navigateTo(new NavigationItem(NavigationItem.START_ACTIVITY, EventDetailsActivity.class, EventDetailsActivity.createDataBundle(event)));
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
        eventsOneByOne.onComplete();
        super.onCleared();
    }

    public void setCurrentSortType(int currentSortType) {
        this.currentSortType = currentSortType;
    }
}
