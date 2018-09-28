package com.redhelmet.alert2me.ui.home.event;

import android.arch.lifecycle.MutableLiveData;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.ClusterManager;
import com.redhelmet.alert2me.R;
import com.redhelmet.alert2me.data.DataManager;
import com.redhelmet.alert2me.data.model.Area;
import com.redhelmet.alert2me.data.model.ClusterMarker;
import com.redhelmet.alert2me.data.model.Event;
import com.redhelmet.alert2me.ui.base.BaseViewModel;
import com.redhelmet.alert2me.ui.base.NavigationType;
import com.redhelmet.alert2me.util.EventUtils;
import com.redhelmet.alert2me.util.IconUtils;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;

public class EventViewModel extends BaseViewModel {

    public MutableLiveData<List<Event>> events = new MutableLiveData<>();

    public EventViewModel(DataManager dataManager) {
        super(dataManager);
        getEvents();
    }

    private void getEvents() {
        isLoading = true;
        disposeBag.add(dataManager.getEventsWithFilter(true)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(data -> {
                    isLoading = false;
                    events.setValue(data);
                }, error -> {
                    isLoading = false;
                    navigationEvent.setValue(new com.redhelmet.alert2me.global.Event<>(NavigationType.SHOW_TOAST.setData(R.string.msgUnableToGetEvent)));
                }));
    }

    public boolean isDefaultFilter() {
        return dataManager.isDefaultFilter();
    }
}
