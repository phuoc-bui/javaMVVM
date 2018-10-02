package com.redhelmet.alert2me.ui.home.event;

import android.databinding.ObservableField;

import com.redhelmet.alert2me.R;
import com.redhelmet.alert2me.data.DataManager;
import com.redhelmet.alert2me.data.model.Event;
import com.redhelmet.alert2me.global.RxProperty;
import com.redhelmet.alert2me.ui.base.BaseViewModel;
import com.redhelmet.alert2me.ui.base.NavigationType;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;

public class EventViewModel extends BaseViewModel {
    private DataManager dataManager;

    public RxProperty<List<Event>> events = new RxProperty<>();

    public ObservableField<List<EventItemViewModel>> eventItemViewModelList;

    public Runnable onRefresh = this::getEvents;

    private boolean isStateWide = false;

    public EventViewModel(DataManager dataManager) {
        this.dataManager = dataManager;
        getEvents();
        disposeBag.add(events.asObservable()
                .flatMap(Observable::fromIterable)
                .map(event -> new EventItemViewModel(event, isStateWide))
                .toList()
                .subscribe(viewModels -> eventItemViewModelList.set(viewModels)));
    }

    private void getEvents() {
        isLoading = true;
        disposeBag.add(dataManager.getEventsWithFilter(true)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(data -> {
                    isLoading = false;
                    events.set(data);
                }, error -> {
                    isLoading = false;
                    navigationEvent.setValue(new com.redhelmet.alert2me.global.Event<>(NavigationType.SHOW_TOAST.setData(R.string.msgUnableToGetEvent)));
                }));
    }

    public boolean isDefaultFilter() {
        return dataManager.isDefaultFilter();
    }


}
