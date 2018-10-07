package com.redhelmet.alert2me.ui.eventfilter.defaultfilter;

import android.arch.lifecycle.MutableLiveData;
import android.content.Intent;

import com.redhelmet.alert2me.data.DataManager;
import com.redhelmet.alert2me.data.model.EventGroup;
import com.redhelmet.alert2me.global.Event;
import com.redhelmet.alert2me.ui.base.BaseViewModel;
import com.redhelmet.alert2me.ui.base.NavigationItem;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;

public class DefaultFilterViewModel extends BaseViewModel {
    public MutableLiveData<List<EventGroup>> allEventGroup = new MutableLiveData<>();

    public DefaultFilterViewModel(DataManager dataManager) {
        super(dataManager);
        disposeBag.add(dataManager.getEventGroups()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(list -> allEventGroup.setValue(list)));
    }

    public void saveData(boolean editMode) {
        saveDataToServer();
//        if (editMode) {
//            editModeNotificationSave();
//        } else {
//            saveDataToServer();
//        }
    }

    private void saveDataToServer() {
        if (allEventGroup.getValue() == null) return;
        List<EventGroup> filterGroup = new ArrayList<>();
        for (EventGroup group : allEventGroup.getValue()) {
            if (group.isFilterOn()) {
                filterGroup.add(group);
            }
        }
        dataManager.saveUserDefaultFilters(filterGroup);
        dataManager.setDefaultFilter(true);

        Intent resultIntent = new Intent();

//        resultIntent.putExtra("default", true);
//        resultIntent.putExtra("filterGroup", defValues);
        navigationEvent.setValue(new Event<>(new NavigationItem(NavigationItem.FINISH_AND_RETURN, resultIntent)));
    }

//    private void editModeNotificationSave() {
//        ArrayList<String> defValues = new ArrayList<String>();
//        ArrayList<HashMap<String, CategoryFilter>> categoryFilters = new ArrayList<HashMap<String, CategoryFilter>>();
//
//        for (int i = 0; i < default_data.size(); i++) {
//            EventGroup ev = new EventGroup();
//            ev = default_data.get(i);
//            if (ev.isFilterOn()) {
//                defValues.add(String.valueOf(ev.getId()));
//            }
//        }
//
//        Intent resultIntent = new Intent();
//
////        resultIntent.putExtra("default", true);
////        resultIntent.putExtra("filter", categoryFilters);
////        resultIntent.putExtra("filterGroup", defValues);
//        setResult(Activity.RESULT_OK, resultIntent);
//        finish();
//    }
}
