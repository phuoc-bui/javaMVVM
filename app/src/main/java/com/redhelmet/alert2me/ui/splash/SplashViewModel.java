package com.redhelmet.alert2me.ui.splash;

import android.arch.lifecycle.MutableLiveData;

import com.redhelmet.alert2me.data.DataManager;
import com.redhelmet.alert2me.data.local.pref.PreferenceHelper;
import com.redhelmet.alert2me.global.Constant;
import com.redhelmet.alert2me.global.Event;
import com.redhelmet.alert2me.ui.base.BaseViewModel;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;

public class SplashViewModel extends BaseViewModel {

    enum LaunchDestination {
        HOME,
        MAIN,
        HINTS
    }

    MutableLiveData<Event<LaunchDestination>> launchDestination = new MutableLiveData<>();

    private PreferenceHelper preferenceHelper;

    public SplashViewModel(DataManager dataManager, PreferenceHelper pref) {
        super(dataManager);
        this.preferenceHelper = pref;

        isLoading = true;
        disposeBag.add(dataManager.loadConfig()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(configResponse -> {
                    isLoading = false;
                    startTimer();
                }, error -> {
                    isLoading = false;
                    startTimer();
                    // TODO: show snack bar or Toast
//                    SnackbarUtils.showSnackbar();
                }));
    }

    private void startTimer() {
        disposeBag.add(Observable.timer(Constant.SPLASH_DISPLAY_LENGTH, TimeUnit.MILLISECONDS)
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe(number -> {
                    LaunchDestination dest;
                    if (preferenceHelper.isInitialLaunch()) {
                        if (preferenceHelper.isAccepted()) {
                            dest = LaunchDestination.HOME;
                        } else {
                            dest = LaunchDestination.MAIN;
                        }
                    } else {
                        dest = LaunchDestination.HINTS;
                    }

                    launchDestination.setValue(new Event<>(dest));
                }));
    }
}
