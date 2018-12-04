package com.phuocbui.mvvm.ui.watchzone;


import com.phuocbui.mvvm.R;
import com.phuocbui.mvvm.data.DataManager;
import com.phuocbui.mvvm.data.PreferenceStorage;
import com.phuocbui.mvvm.data.model.EditWatchZones;
import com.phuocbui.basemodule.global.RxProperty;
import com.phuocbui.basemodule.ui.base.BaseViewModel;

import javax.inject.Inject;

import androidx.databinding.ObservableField;
import androidx.databinding.ObservableInt;
import androidx.lifecycle.MutableLiveData;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;

public class WatchZoneViewModel extends AppViewModel {
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
        getStaticWZData();
        disposeBag.add(proximityEnable.asObservable()
                .subscribe(pref::setProximityEnabled));

        listener = this::onStaticWZClick;
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
        EditWatchZones wz = new EditWatchZones();
        wz.setName("test");
        ItemStaticWZViewModel item = new ItemStaticWZViewModel(dataManager, wz, listener);
        staticWZAdapter.itemsSource.add(item);
    }

    public void onStaticWZClick(EditWatchZones watchZone) {
        showToast("Click on watch zone item");
    }

    public void onNotificationOptionClick() {
        showToast("Click on Notification Option");
    }

    public void removeWatchZone(ItemStaticWZViewModel watchZone) {
        disposeBag.add(dataManager.deleteWatchZone(watchZone.getWatchZone().getId())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(o -> {
                            showToast(R.string.msg_deleted);
                            staticWZAdapter.itemsSource.remove(watchZone);
                        }, this::handleError
                ));
    }
}
