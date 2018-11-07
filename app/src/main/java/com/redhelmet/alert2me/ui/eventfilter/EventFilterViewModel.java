package com.redhelmet.alert2me.ui.eventfilter;

import com.redhelmet.alert2me.data.DataManager;
import com.redhelmet.alert2me.data.model.EventGroup;
import com.redhelmet.alert2me.ui.base.BaseViewModel;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.Observable;

public class EventFilterViewModel extends BaseViewModel {

    public List<Integer> eventFilterOnIds = new ArrayList<>();

    @Inject
    public EventFilterViewModel(DataManager dataManager) {
        super(dataManager);
        disposeBag.add(dataManager.getFilterOnDefaultFilters()
                .flatMap(Observable::fromIterable)
                .map(EventGroup::getId)
                .subscribe(id -> eventFilterOnIds.add((int) id.longValue()), this::handleError));

    }

    public boolean isDefaultFilter() {
        return dataManager.isDefaultFilter();
    }
}
