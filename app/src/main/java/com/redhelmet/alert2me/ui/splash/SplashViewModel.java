package com.redhelmet.alert2me.ui.splash;

import com.redhelmet.alert2me.R;
import com.redhelmet.alert2me.data.DataManager;
import com.redhelmet.alert2me.data.PreferenceHelper;
import com.redhelmet.alert2me.global.Constant;
import com.redhelmet.alert2me.global.Event;
import com.redhelmet.alert2me.ui.base.BaseViewModel;
import com.redhelmet.alert2me.ui.base.NavigationItem;
import com.redhelmet.alert2me.ui.hint.HintsActivity;
import com.redhelmet.alert2me.ui.home.HomeActivity;
import com.redhelmet.alert2me.ui.signin.SignInActivity;
import com.redhelmet.alert2me.ui.termsandcondition.TermConditionActivity;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;

public class SplashViewModel extends BaseViewModel {
    private PreferenceHelper pref;

    public SplashViewModel(DataManager dataManager, PreferenceHelper pref) {
        super(dataManager);
        this.pref = pref;
        isLoading.set(true);
        disposeBag.add(dataManager.loadConfig()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> startTimer(), error -> {
                    isLoading.set(false);
                    startTimer();
                    navigateTo(new NavigationItem(NavigationItem.SHOW_TOAST, error.getMessage()));
                }));
    }

    private void startTimer() {
        disposeBag.add(Observable.timer(Constant.SPLASH_DISPLAY_LENGTH, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(number -> {
                    isLoading.set(false);
                    Class dest;
                    if (pref.isInitialLaunch()) {
                        if (pref.isAccepted()) {
                            dest = pref.isLoggedIn() ? HomeActivity.class : SignInActivity.class;
                        } else {
                            dest = TermConditionActivity.class;
                        }
                    } else {
                        dest = HintsActivity.class;
                    }

                    navigateTo(new NavigationItem(NavigationItem.START_ACTIVITY_AND_FINISH, dest));
                }));
    }
}
