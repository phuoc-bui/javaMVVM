package com.redhelmet.alert2me.ui.splash;

import com.redhelmet.alert2me.R;
import com.redhelmet.alert2me.data.DataManager;
import com.redhelmet.alert2me.global.Constant;
import com.redhelmet.alert2me.global.Event;
import com.redhelmet.alert2me.ui.base.BaseViewModel;
import com.redhelmet.alert2me.ui.base.NavigationItem;
import com.redhelmet.alert2me.ui.hint.HintsActivity;
import com.redhelmet.alert2me.ui.home.HomeActivity;
import com.redhelmet.alert2me.ui.termsandcondition.TermConditionActivity;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;

public class SplashViewModel extends BaseViewModel {
    public SplashViewModel(DataManager dataManager) {
        super(dataManager);
        isLoading.set(true);
        disposeBag.add(dataManager.loadConfig()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> {
                    startTimer();
                }, error -> {
                    isLoading.set(false);
                    startTimer();
                    navigationEvent.setValue(
                            new Event<>(new NavigationItem(NavigationItem.SHOW_TOAST, R.string.timeOut)));
                }));
    }

    private void startTimer() {
        disposeBag.add(Observable.timer(Constant.SPLASH_DISPLAY_LENGTH, TimeUnit.MILLISECONDS)
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe(number -> {
                    isLoading.set(false);
                    Class dest;
                    if (dataManager.getInitialLaunch()) {
                        if (dataManager.getAccepted()) {
                            dest = HomeActivity.class;
                        } else {
                            dest = TermConditionActivity.class;
                        }
                    } else {
                        dest = HintsActivity.class;
                    }

                    navigationEvent.postValue(new Event<>(new NavigationItem(NavigationItem.START_ACTIVITY_AND_FINISH,dest)));
                }));
    }
}
