package com.redhelmet.alert2me.ui.home.event;

import android.arch.lifecycle.MutableLiveData;

import com.redhelmet.alert2me.R;
import com.redhelmet.alert2me.data.DataManager;
import com.redhelmet.alert2me.data.model.Event;
import com.redhelmet.alert2me.ui.base.BaseViewModel;
import com.redhelmet.alert2me.ui.base.NavigationType;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;

public class EventViewModel extends BaseViewModel {

    public boolean selectedMapType = false;
    public MutableLiveData<List<Event>> events = new MutableLiveData<>();

    public EventViewModel(DataManager dataManager) {
        super(dataManager);
        getEvents();
    }

    public MapViewModel getMapViewModel() {
        MapViewModel viewModel = new MapViewModel(dataManager);
        viewModel.parent = this;
        return viewModel;
    }

    public EventListViewModel getEventListViewModel() {
        EventListViewModel viewModel = new EventListViewModel(dataManager);
        viewModel.parent = this;
        return viewModel;
    }


    public void getEvents() {
        isLoading = true;
        disposeBag.add(dataManager.getAllEvents()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(data -> {
                    isLoading = false;
                    events.setValue(data);
                }, error -> {
                    isLoading = false;
                    navigationEvent.setValue(new com.redhelmet.alert2me.global.Event<>(NavigationType.SHOW_TOAST.setData(R.string.msgUnableToGetEvent)));
                }));
    }

    public void filterEvent()
}
