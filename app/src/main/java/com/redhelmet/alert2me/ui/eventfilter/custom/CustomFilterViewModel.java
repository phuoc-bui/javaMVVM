package com.redhelmet.alert2me.ui.eventfilter.custom;

import android.arch.lifecycle.MutableLiveData;

import com.redhelmet.alert2me.data.DataManager;
import com.redhelmet.alert2me.data.model.Category;
import com.redhelmet.alert2me.ui.base.BaseViewModel;

import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;

public class CustomFilterViewModel extends BaseViewModel {
    public MutableLiveData<List<Category>> allCategories = new MutableLiveData<>();

    public CustomFilterViewModel(DataManager dataManager) {
        super(dataManager);
        disposeBag.add(dataManager.getCategories()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(list -> allCategories.setValue(list)));
    }
}
