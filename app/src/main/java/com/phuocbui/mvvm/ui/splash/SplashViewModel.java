package com.phuocbui.mvvm.ui.splash;

import com.phuocbui.mvvm.data.DataManager;
import com.phuocbui.mvvm.data.PreferenceStorage;
import com.phuocbui.mvvm.global.Constant;
import com.phuocbui.basemodule.ui.base.BaseViewModel;
import com.phuocbui.mvvm.ui.home.HomeActivity;
import com.phuocbui.mvvm.ui.signin.SignInActivity;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;

public class SplashViewModel extends BaseViewModel {

    @Inject
    SplashViewModel(DataManager dataManager, PreferenceStorage pref) {
        super(dataManager, pref);
        isLoading.set(true);

        disposeBag.add(dataManager.loadConfig()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> startTimer(), error -> startTimer()));
    }

    private void startTimer() {
        disposeBag.add(Observable.timer(Constant.SPLASH_DISPLAY_LENGTH, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(number -> {
                    isLoading.set(false);
                    startActivity(preferenceStorage.isLoggedIn() ? HomeActivity.class : SignInActivity.class, true);
                }));
    }
}
