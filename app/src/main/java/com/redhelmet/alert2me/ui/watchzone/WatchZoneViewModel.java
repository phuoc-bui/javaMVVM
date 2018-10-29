package com.redhelmet.alert2me.ui.watchzone;


import com.redhelmet.alert2me.data.DataManager;
import com.redhelmet.alert2me.data.PreferenceStorage;
import com.redhelmet.alert2me.ui.base.BaseViewModel;

import javax.inject.Inject;

import androidx.lifecycle.MutableLiveData;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;

public class WatchZoneViewModel extends BaseViewModel {
    public MutableLiveData<Boolean> proximityEnable = new MutableLiveData<>();
    public MutableLiveData<Boolean> isRefreshing = new MutableLiveData<>();
    public StaticWZAdapter staticWZAdapter = new StaticWZAdapter();

    @Inject
    public WatchZoneViewModel(DataManager dataManager, PreferenceStorage pref) {
        super(dataManager, pref);
        proximityEnable.setValue(pref.isProximityEnabled());
        getData();
    }

    private void getData() {
        isRefreshing.setValue(false);
        disposeBag.add(dataManager.getWatchZones()
                .flatMap(Observable::fromIterable)
                .map(ItemStaticWZViewModel::new)
                .toList()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe( list -> {
                    isRefreshing.setValue(false);
                    staticWZAdapter.itemsSource.clear();
                    staticWZAdapter.itemsSource.addAll(list);
                }, err -> {
                    isRefreshing.setValue(false);
                }));
    }

    public void onRefresh() {
        getData();
    }
}
