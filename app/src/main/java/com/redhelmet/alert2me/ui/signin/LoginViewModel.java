package com.redhelmet.alert2me.ui.signin;

import androidx.databinding.ObservableBoolean;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.redhelmet.alert2me.R;
import com.redhelmet.alert2me.data.DataManager;
import com.redhelmet.alert2me.data.PreferenceStorage;
import com.redhelmet.alert2me.global.RxProperty;
import com.redhelmet.alert2me.ui.base.BaseViewModel;
import com.redhelmet.alert2me.ui.base.NavigationItem;
import com.redhelmet.alert2me.ui.home.HomeActivity;

import javax.inject.Inject;

import androidx.databinding.ObservableInt;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;

public class LoginViewModel extends BaseViewModel {

    public RxProperty<String> userEmail = new RxProperty<>("");
    public RxProperty<String> password = new RxProperty<>("");
    public ObservableInt emailNotValidError = new ObservableInt();
    public ObservableBoolean disableLoginButton = new ObservableBoolean(true);

    @Inject
    public LoginViewModel(DataManager dataManager, PreferenceStorage pref) {
        super(dataManager, pref);

        disposeBag.add(isEmailValid().subscribe(b -> {
            if (!b) emailNotValidError.set(R.string.register_email_not_valid_error);
            else emailNotValidError.set(0);
        }));

        disposeBag.add(Observable.combineLatest(isEmailValid(),
                password.asObservable(),
                (emailValid, pass) -> emailValid && pass != null && pass.length() > 0)
                .subscribe(enable -> disableLoginButton.set(enable)));
    }

    private Observable<Boolean> isEmailValid() {
        return userEmail.asObservable().map(email -> {
            if (email == null) return false;
            return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
        });
    }

    public void login() {
        showLoadingDialog(true);
        disposeBag.add(dataManager.login(userEmail.get(), password.get())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> {
                    onLoginSuccess();
                }, error -> {
                    showLoadingDialog(false);
                    handleError(error);
                }));
    }

    private void onLoginSuccess() {
        FirebaseInstanceId.getInstance()
                .getInstanceId()
                .addOnSuccessListener(instanceIdResult -> disposeBag.add(dataManager.registerDeviceToken(instanceIdResult.getToken())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(registerResponse -> onLoadFinish(), error -> onLoadFinish())));
        FirebaseInstanceId.getInstance()
                .getInstanceId()
                .addOnFailureListener(e -> {
                    showLoadingDialog(false);
                    onLoadFinish();
                    Log.e("FirebaseInstanceId", "Fail to get firebase token: " + e.getMessage());
                });
    }

    private void onLoadFinish() {
        disposeBag.add(dataManager.getEventGroups()
                .doOnNext(eventGroups -> dataManager.saveUserDefaultFilters(eventGroups))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(eventGroups -> {
                    showLoadingDialog(false);
                    preferenceStorage.setLoggedIn(true);
                    navigateTo(new NavigationItem(NavigationItem.START_ACTIVITY_AND_CLEAR_TASK, HomeActivity.class));
                }, error -> {
                    showLoadingDialog(false);
                    handleError(error);
                }));
    }

    public void forgotPassword() {
        navigateTo(new NavigationItem(NavigationItem.CHANGE_FRAGMENT_AND_ADD_TO_BACK_STACK, ForgotPasswordFragment.newInstance()));
    }

    public void back() {
        navigateTo(new NavigationItem(NavigationItem.POP_FRAGMENT_BACK));
    }
}
