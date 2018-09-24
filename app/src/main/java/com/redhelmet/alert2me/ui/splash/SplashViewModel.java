package com.redhelmet.alert2me.ui.splash;

import com.redhelmet.alert2me.data.DataManager;
import com.redhelmet.alert2me.global.Constant;
import com.redhelmet.alert2me.global.Event;
import com.redhelmet.alert2me.ui.activity.HomeActivity;
import com.redhelmet.alert2me.ui.base.BaseViewModel;
import com.redhelmet.alert2me.ui.hint.HintsActivity;
import com.redhelmet.alert2me.ui.termsandcondition.TermConditionActivity;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;

public class SplashViewModel extends BaseViewModel {

    enum LaunchDestination {
        HOME(HomeActivity.class),
        TERMS_CONDITION(TermConditionActivity.class),
        HINTS(HintsActivity.class);

        Class clazz;

        LaunchDestination(Class clazz) {
            this.clazz = clazz;
        }
    }

    public SplashViewModel(DataManager dataManager) {
        super(dataManager);

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
                    if (dataManager.getInitialLaunch()) {
                        if (dataManager.getAccepted()) {
                            dest = LaunchDestination.HOME;
                        } else {
                            dest = LaunchDestination.TERMS_CONDITION;
                        }
                    } else {
                        dest = LaunchDestination.HINTS;
                    }

                    navigationEvent.setValue(new Event<>(dest.clazz));
                }));
    }
}
