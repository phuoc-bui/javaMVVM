package com.redhelmet.alert2me.ui.watchzone;


import android.util.Log;

import com.redhelmet.alert2me.data.DataManager;
import com.redhelmet.alert2me.data.PreferenceStorage;
import com.redhelmet.alert2me.ui.addwatchzone.AddStaticZoneActivity;
import com.redhelmet.alert2me.ui.base.BaseViewModel;
import com.redhelmet.alert2me.ui.base.NavigationItem;

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
        Log.e("WatchZoneViewModel", "Constructor");
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

    public void onAddWatchZoneClick() {
        navigateTo(new NavigationItem(NavigationItem.START_ACTIVITY, AddStaticZoneActivity.class));
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        Log.e("WatchZoneViewModel", "onCleared");
    }
}
