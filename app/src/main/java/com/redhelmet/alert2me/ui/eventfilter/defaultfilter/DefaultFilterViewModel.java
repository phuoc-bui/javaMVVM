package com.redhelmet.alert2me.ui.eventfilter.defaultfilter;

import android.arch.lifecycle.MutableLiveData;

import com.redhelmet.alert2me.data.DataManager;
import com.redhelmet.alert2me.data.model.EventGroup;
import com.redhelmet.alert2me.ui.base.BaseViewModel;

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
}
