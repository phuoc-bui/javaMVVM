package com.redhelmet.alert2me.ui.watchzone;

import android.arch.lifecycle.MutableLiveData;

import com.redhelmet.alert2me.data.DataManager;
import com.redhelmet.alert2me.data.PreferenceStorage;
import com.redhelmet.alert2me.ui.base.BaseViewModel;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;

public class WatchZoneViewModel extends BaseViewModel {
    public MutableLiveData<Boolean> proximityEnable = new MutableLiveData<>();

    public StaticWZAdapter staticWZAdapter = new StaticWZAdapter();

    @Inject
    public WatchZoneViewModel(DataManager dataManager, PreferenceStorage pref) {
        super(dataManager, pref);
        proximityEnable.setValue(pref.isProximityEnabled());
        getData();
    }

    private void getData() {
        showLoadingDialog(true);
        disposeBag.add(dataManager.getWatchZones()
                .flatMap(Observable::fromIterable)
                .map(ItemStaticWZViewModel::new)
                .toList()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe( list -> {
                    showLoadingDialog(false);
                    staticWZAdapter.itemsSource.clear();
                    staticWZAdapter.itemsSource.addAll(list);
                }, err -> showLoadingDialog(false)));
    }

    public void onRefresh() {
        getData();
    }
}
