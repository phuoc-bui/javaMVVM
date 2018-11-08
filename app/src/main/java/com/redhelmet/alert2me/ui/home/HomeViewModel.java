package com.redhelmet.alert2me.ui.home;

import com.google.android.gms.maps.model.LatLng;
import com.redhelmet.alert2me.data.DataManager;
import com.redhelmet.alert2me.ui.base.BaseViewModel;

import javax.inject.Inject;

import io.reactivex.android.schedulers.AndroidSchedulers;

public class HomeViewModel extends BaseViewModel {

    public LatLng initPoint;

    @Inject
    public HomeViewModel(DataManager dataManager) {
        super(dataManager);
    }

    public void proximityLocationCheckin() {
        disposeBag.add(dataManager.putProximityLocation(0, 0)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> {
//                    PreferenceUtils.saveToPrefs(getApplicationContext(), Constants.KEY_USERLATITUDE,String.valueOf(lastKnownLocation.getLatitude()));
//                    PreferenceUtils.saveToPrefs(getApplicationContext(),Constants.KEY_USERLONGITUDE,String.valueOf(lastKnownLocation.getLongitude()));
                }, this::handleError));
    }
}
