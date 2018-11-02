package com.redhelmet.alert2me.ui.splash;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.redhelmet.alert2me.data.DataManager;
import com.redhelmet.alert2me.data.PreferenceStorage;
import com.redhelmet.alert2me.global.Constant;
import com.redhelmet.alert2me.ui.base.BaseViewModel;
import com.redhelmet.alert2me.ui.base.NavigationItem;
import com.redhelmet.alert2me.ui.home.HomeActivity;
import com.redhelmet.alert2me.ui.signin.SignInActivity;
import com.redhelmet.alert2me.ui.termsandcondition.TermConditionActivity;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;

public class SplashViewModel extends BaseViewModel {

    @Inject
    public SplashViewModel(DataManager dataManager, PreferenceStorage pref) {
        super(dataManager, pref);
        isLoading.set(true);

        disposeBag.add(dataManager.loadConfig()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> {
                    getFirebaseToken();
                    startTimer();
                }, error -> {
                    getFirebaseToken();
                    startTimer();
                    isLoading.set(false);
                    handleError(error);
                }));
    }

    private void getFirebaseToken() {
        FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(task -> {
            if (!task.isSuccessful()) {
                Log.w("Firebase", "getInstanceId failed", task.getException());
                startTimer();
            } else {
                // Get new Instance ID token
                String token = task.getResult().getToken();
                Log.d("Firebase", "Firebase token: " + token);
                registerDevice(token);
            }
        });
    }

    private void registerDevice(String token) {
        disposeBag.add(dataManager.registerDeviceToken(token)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(apiInfo -> {
                    Log.d("SplashViewModel", "register device successful");
                    startTimer();
                }, e -> startTimer()));
    }

    private void startTimer() {
        disposeBag.add(Observable.timer(Constant.SPLASH_DISPLAY_LENGTH, TimeUnit.MILLISECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(number -> {
                    isLoading.set(false);
                    Class dest;
                    if (preferenceStorage.isAccepted()) {
                        dest = preferenceStorage.isLoggedIn() ? HomeActivity.class : SignInActivity.class;
                    } else {
                        dest = TermConditionActivity.class;
                    }

                    navigateTo(new NavigationItem(NavigationItem.START_ACTIVITY_AND_FINISH, dest));
                }));
    }
}
