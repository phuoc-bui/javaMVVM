package com.redhelmet.alert2me.ui.eventfilter.defaultfilter;

import android.arch.lifecycle.MutableLiveData;
import android.content.Intent;

import com.redhelmet.alert2me.data.DataManager;
import com.redhelmet.alert2me.data.model.EventGroup;
import com.redhelmet.alert2me.ui.base.BaseViewModel;
import com.redhelmet.alert2me.ui.base.NavigationItem;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.Completable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class DefaultFilterViewModel extends BaseViewModel {
    public MutableLiveData<List<EventGroup>> allEventGroup = new MutableLiveData<>();

    @Inject
    public DefaultFilterViewModel(DataManager dataManager) {
        super(dataManager);
        disposeBag.add(dataManager.getEventGroups()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(list -> allEventGroup.setValue(list)));
    }

    public void saveData() {
        saveDataToServer();
    }

    private void saveDataToServer() {
        if (allEventGroup.getValue() == null) return;
        List<EventGroup> filterGroup = new ArrayList<>();
        for (EventGroup group : allEventGroup.getValue()) {
            if (group.isUserEdited()) {
                filterGroup.add(group);
            }
        }

        disposeBag.add(Completable.fromAction(() -> {
            dataManager.saveUserDefaultFilters(filterGroup);
            dataManager.setDefaultFilter(true);
        }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(() -> {
                    Intent resultIntent = new Intent();

//        resultIntent.putExtra("default", true);
//        resultIntent.putExtra("filterGroup", defValues);
                    navigateTo(new NavigationItem(NavigationItem.FINISH_AND_RETURN, resultIntent));
                }));


    }
}
