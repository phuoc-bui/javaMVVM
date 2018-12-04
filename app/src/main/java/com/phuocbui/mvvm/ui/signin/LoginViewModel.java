package com.phuocbui.mvvm.ui.signin;

import com.phuocbui.mvvm.R;
import com.phuocbui.mvvm.data.DataManager;
import com.phuocbui.mvvm.data.PreferenceStorage;
import com.phuocbui.basemodule.global.RxProperty;
import com.phuocbui.basemodule.ui.base.BaseViewModel;
import com.phuocbui.mvvm.ui.home.HomeActivity;

import javax.inject.Inject;

import androidx.databinding.ObservableBoolean;
import androidx.databinding.ObservableInt;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;

public class LoginViewModel extends BaseViewModel {

    public RxProperty<String> userEmail = new RxProperty<>("");
    public RxProperty<String> password = new RxProperty<>("");
    public ObservableInt emailNotValidError = new ObservableInt();
    public ObservableBoolean disableLoginButton = new ObservableBoolean(true);

    @Inject
    LoginViewModel(DataManager dataManager, PreferenceStorage pref) {
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
                    showLoadingDialog(false);
                    preferenceStorage.setLoggedIn(true);
                    startActivity(HomeActivity.class, true);
                }, error -> {
                    showLoadingDialog(false);
                    handleError(error);
                }));
    }

    public void forgotPassword() {
        changeFragment(ForgotPasswordFragment.newInstance(), true);
    }

    public void back() {
        popFragmentBack();
    }
}
