package com.redhelmet.alert2me.ui.eventfilter.defaultfilter;

import android.content.Intent;

import com.redhelmet.alert2me.data.DataManager;
import com.redhelmet.alert2me.data.model.EventGroup;
import com.redhelmet.alert2me.ui.base.BaseViewModel;
import com.redhelmet.alert2me.ui.base.NavigationItem;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class DefaultFilterViewModel extends BaseViewModel {
    public DefaultFilterAdapter adapter = new DefaultFilterAdapter();
    private List<Integer> enabledEventGroup = new ArrayList<>();

    @Inject
    public DefaultFilterViewModel(DataManager dataManager) {
        super(dataManager);
        disposeBag.add(dataManager.getEventGroups()
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap(Observable::fromIterable)
                .map(eventGroup -> {
                    for (int id : enabledEventGroup) {
                        if (eventGroup.getId() == id) {
                            eventGroup.setFilterOn(true);
                            return eventGroup;
                        }
                    }
                    eventGroup.setFilterOn(false);
                    return eventGroup;
                })
                .map(EventGroupItemViewModel::new)
                .subscribe(vm -> adapter.itemsSource.add(vm), this::handleError));
    }

    public void setEnabledFilters(List<Integer> ids) {
        if (ids != null) enabledEventGroup = ids;
    }

    public void saveData() {
        List<EventGroup> filterGroup = new ArrayList<>();
        for (EventGroupItemViewModel item : adapter.itemsSource) {
            EventGroup group = item.eventGroup.getValue();
            filterGroup.add(group);
        }

        disposeBag.add(Completable.fromAction(() -> {
            dataManager.saveUserDefaultFilters(filterGroup);
            dataManager.setDefaultFilter(true);
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(() -> {
                    Intent resultIntent = new Intent();
                    navigateTo(new NavigationItem(NavigationItem.FINISH_AND_RETURN, resultIntent));
                }));
    }
}
