package com.redhelmet.alert2me.ui.watchzone;


import android.util.Log;

import com.redhelmet.alert2me.data.DataManager;
import com.redhelmet.alert2me.data.PreferenceStorage;
import com.redhelmet.alert2me.global.RxProperty;
import com.redhelmet.alert2me.ui.addwatchzone.AddStaticZoneActivity;
import com.redhelmet.alert2me.ui.base.BaseViewModel;
import com.redhelmet.alert2me.ui.base.NavigationItem;
import com.redhelmet.alert2me.ui.eventfilter.EventFilterActivity;

import javax.inject.Inject;

import androidx.databinding.ObservableField;
import androidx.databinding.ObservableInt;
import androidx.lifecycle.MutableLiveData;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;

public class WatchZoneViewModel extends BaseViewModel {
    public RxProperty<Boolean> proximityEnable = new RxProperty<>();
    public MutableLiveData<Boolean> isRefreshing = new MutableLiveData<>();
    public StaticWZAdapter staticWZAdapter = new StaticWZAdapter();
    public ObservableInt mobileRadius = new ObservableInt(5);
    public ObservableField<String> mobileRingSound = new ObservableField<>();

    @Inject
    public WatchZoneViewModel(DataManager dataManager, PreferenceStorage pref) {
        super(dataManager, pref);
        proximityEnable.set(pref.isProximityEnabled());
        // don't need call getStaticWZData() because it is called on Resume of WatchZoneFragment
//        getStaticWZData();
        disposeBag.add(proximityEnable.asObservable()
                .subscribe(pref::setProximityEnabled));
    }

    public void setRingSound(String ringSoundUri) {
        mobileRingSound.set(ringSoundUri);
    }

    private void getStaticWZData() {
        isRefreshing.setValue(false);
        disposeBag.add(dataManager.getWatchZones()
                .flatMap(Observable::fromIterable)
                .map(ItemStaticWZViewModel::new)
                .toList()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(list -> {
                    isRefreshing.setValue(false);
                    staticWZAdapter.itemsSource.clear();
                    staticWZAdapter.itemsSource.addAll(list);
                }, err -> {
                    isRefreshing.setValue(false);
                    handleError(err);
                }));
    }

    public void onRefresh() {
        getStaticWZData();
    }

    public void onAddWatchZoneClick() {
        navigateTo(new NavigationItem(NavigationItem.START_ACTIVITY, AddStaticZoneActivity.class));
    }

    public void onStaticWZClick(int position) {
        if (position >= 0 && position < staticWZAdapter.itemsSource.size()) {
            ItemStaticWZViewModel item = staticWZAdapter.itemsSource.get(position);
            navigateTo(new NavigationItem(NavigationItem.START_ACTIVITY, AddStaticZoneActivity.class, AddStaticZoneActivity.createBundle(item.getWatchZone())));
        }
    }

    public void onNotificationOptionClick() {
        navigateTo(new NavigationItem(NavigationItem.START_ACTIVITY, EventFilterActivity.class));
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        Log.e("WatchZoneViewModel", "onCleared");
    }
}
