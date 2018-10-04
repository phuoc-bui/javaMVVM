package com.redhelmet.alert2me.ui.home;

import com.redhelmet.alert2me.R;
import com.redhelmet.alert2me.data.DataManager;
import com.redhelmet.alert2me.global.Event;
import com.redhelmet.alert2me.ui.base.BaseViewModel;
import com.redhelmet.alert2me.ui.base.NavigationItem;

import io.reactivex.android.schedulers.AndroidSchedulers;

public class HomeViewModel extends BaseViewModel {

    public HomeViewModel(DataManager dataManager) {
        super(dataManager);
    }

    public void proximityLocationCheckin() {
        disposeBag.add(dataManager.putProximityLocation(0, 0)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> {
//                    PreferenceUtils.saveToPrefs(getApplicationContext(), Constants.KEY_USERLATITUDE,String.valueOf(lastKnownLocation.getLatitude()));
//                    PreferenceUtils.saveToPrefs(getApplicationContext(),Constants.KEY_USERLONGITUDE,String.valueOf(lastKnownLocation.getLongitude()));
                }, error -> navigationEvent.setValue(new Event<>(new NavigationItem(NavigationItem.SHOW_TOAST, R.string.timeOut)))));
    }
}
