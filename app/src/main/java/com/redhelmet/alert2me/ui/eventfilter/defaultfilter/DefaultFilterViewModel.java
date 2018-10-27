package com.redhelmet.alert2me.ui.eventfilter.defaultfilter;

import androidx.lifecycle.MutableLiveData;
import android.content.Intent;
import androidx.appcompat.widget.SwitchCompat;

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
    public MutableLiveData<List<EventGroup>> allEventGroup = new MutableLiveData<>();

    private OnSwitchChangedListener listener = (view, data) -> allEventGroup.getValue();

    @Inject
    public DefaultFilterViewModel(DataManager dataManager) {
        super(dataManager);
        disposeBag.add(dataManager.getEventGroups()
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(list -> allEventGroup.setValue(list))
                .flatMap(Observable::fromIterable)
                .map(eventGroup -> new EventGroupItemViewModel(eventGroup, listener))
                .subscribe(vm -> adapter.itemsSource.add(vm)));
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

    public interface OnSwitchChangedListener {
        void onSwitchChanged(SwitchCompat view, EventGroup data);
    }
}
