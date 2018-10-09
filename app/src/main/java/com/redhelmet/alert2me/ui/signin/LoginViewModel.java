package com.redhelmet.alert2me.ui.signin;

import android.databinding.ObservableBoolean;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.redhelmet.alert2me.data.DataManager;
import com.redhelmet.alert2me.global.Event;
import com.redhelmet.alert2me.global.RxProperty;
import com.redhelmet.alert2me.ui.base.BaseViewModel;
import com.redhelmet.alert2me.ui.base.NavigationItem;
import com.redhelmet.alert2me.ui.home.HomeActivity;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;

public class LoginViewModel extends BaseViewModel {
    public RxProperty<String> userEmail = new RxProperty<>("");
    public RxProperty<String> password = new RxProperty<>("");
    public ObservableBoolean disableLoginButton = new ObservableBoolean(true);

    public LoginViewModel(DataManager dataManager) {
        super(dataManager);
        disposeBag.add(Observable.combineLatest(userEmail.asObservable(),
                password.asObservable(),
                (email, pass) -> email!=null && email.length() > 0 && pass != null && pass.length() > 0)
                .subscribe(enable -> disableLoginButton.set(enable)));
    }

    public void login() {
        isLoading.set(true);
        onLoginSuccess();
    }

    private void onLoginSuccess() {
        FirebaseInstanceId.getInstance()
                .getInstanceId()
                .addOnSuccessListener(instanceIdResult -> disposeBag.add(dataManager.getUserId(instanceIdResult.getToken())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(registerResponse -> onLoadFinish(), error -> onLoadFinish())));
        FirebaseInstanceId.getInstance()
                .getInstanceId()
                .addOnFailureListener(e -> {
            isLoading.set(false);
            onLoadFinish();
            Log.e("FirebaseInstanceId", "Fail to get firebase token: " + e.getMessage());
        });
    }

    private void onLoadFinish() {
        disposeBag.add(dataManager.getEventGroups()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(eventGroups -> {
                    isLoading.set(false);
                    dataManager.saveUserDefaultFilters(eventGroups);
                    navigationEvent.setValue(new Event<>(new NavigationItem(NavigationItem.START_ACTIVITY_AND_FINISH, HomeActivity.class)));
                }));
    }

    public void forgotPassword() {

    }

    public void back() {
        navigationEvent.setValue(new Event<>(new NavigationItem(NavigationItem.FINISH)));
    }
}
