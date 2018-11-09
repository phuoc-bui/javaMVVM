package com.redhelmet.alert2me.ui.watchzone;


import android.util.Log;

import com.redhelmet.alert2me.R;
import com.redhelmet.alert2me.data.DataManager;
import com.redhelmet.alert2me.data.PreferenceStorage;
import com.redhelmet.alert2me.data.model.EditWatchZones;
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

    private StaticWZAdapter.OnItemClickListener listener;

    @Inject
    public WatchZoneViewModel(DataManager dataManager, PreferenceStorage pref) {
        super(dataManager, pref);
        proximityEnable.set(pref.isProximityEnabled());
        // don't need call getStaticWZData() because it is called on Resume of WatchZoneFragment
//        getStaticWZData();
        disposeBag.add(proximityEnable.asObservable()
                .subscribe(pref::setProximityEnabled));

        listener = (item) -> onStaticWZClick(item);
    }

    public void setRingSound(String ringSoundUri) {
        mobileRingSound.set(ringSoundUri);
    }

    private void getStaticWZData() {
        isRefreshing.setValue(false);
        disposeBag.add(dataManager.getWatchZones()
                .flatMap(Observable::fromIterable)
                .observeOn(AndroidSchedulers.mainThread())
                .map(item -> new ItemStaticWZViewModel(dataManager, item, listener))
                .toList()
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

    public void onStaticWZClick(EditWatchZones watchZone) {
        if (watchZone != null) {
            navigateTo(new NavigationItem(NavigationItem.START_ACTIVITY, AddStaticZoneActivity.class, AddStaticZoneActivity.createBundle(watchZone)));
        }
    }

    public void onNotificationOptionClick() {
        navigateTo(new NavigationItem(NavigationItem.START_ACTIVITY, EventFilterActivity.class));
    }

    public void removeWatchZone(ItemStaticWZViewModel watchZone) {
        disposeBag.add(dataManager.deleteWatchZone(watchZone.getWatchZone().getId())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(o -> {
                            navigateTo(new NavigationItem(NavigationItem.SHOW_TOAST, R.string.msg_deleted));
                            staticWZAdapter.itemsSource.remove(watchZone);
                        }, this::handleError
                ));
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        Log.e("WatchZoneViewModel", "onCleared");
    }
}
