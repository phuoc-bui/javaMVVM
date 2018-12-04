package com.phuocbui.mvvm.ui.signin;

import com.phuocbui.mvvm.R;
import com.phuocbui.mvvm.data.DataManager;
import com.phuocbui.mvvm.data.model.User;
import com.phuocbui.basemodule.global.RxProperty;
import com.phuocbui.basemodule.ui.base.BaseViewModel;

import javax.inject.Inject;

import androidx.databinding.ObservableBoolean;
import androidx.databinding.ObservableInt;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;

public class RegisterViewModel extends BaseViewModel {

    public UserModel userModel = new UserModel();
    public ObservableBoolean enableButton = new ObservableBoolean(false);
    public ObservableInt emailNotValidError = new ObservableInt();
    public ObservableInt passwordNotMatchesError = new ObservableInt();

    @Inject
    RegisterViewModel(DataManager dataManager) {
        super(dataManager);
        disposeBag.add(userModel.isValid().subscribe(b -> enableButton.set(b)));
        disposeBag.add(userModel.isEmailValid().subscribe(b -> {
            if (!b) emailNotValidError.set(R.string.register_email_not_valid_error);
            else emailNotValidError.set(0);
        }));
        disposeBag.add(userModel.isPasswordMatches().subscribe(b -> {
            if (!b) passwordNotMatchesError.set(R.string.register_password_not_matches_error);
            else passwordNotMatchesError.set(0);
        }));
    }

    public void onRegisterClick() {
        showLoadingDialog(true);
        disposeBag.add(dataManager.registerAccount(userModel.getUser())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> {
                    showLoadingDialog(false);
                    showToast(response.message);
                    changeFragment(LoginFragment.newInstance(userModel.userEmail.get(), userModel.password.get()), true);
                }, error -> {
                    showLoadingDialog(false);
                    handleError(error);
                }));
    }

    public void onSignInClick() {
        userModel.resetFields();
        changeFragment(LoginFragment.newInstance(), true);
    }

    public static class UserModel {
        private User user;
        public RxProperty<String> code = new RxProperty<>("");
        public RxProperty<String> userEmail = new RxProperty<>("");
        public RxProperty<String> firstName = new RxProperty<>("");
        public RxProperty<String> surname = new RxProperty<>("");
        public RxProperty<String> postcode = new RxProperty<>("");
        public RxProperty<String> password = new RxProperty<>("");
        public RxProperty<String> repeatPassword = new RxProperty<>("");

        public UserModel() {
            user = new User();
        }

        Observable<Boolean> isEmailValid() {
            return userEmail.asObservable().map(email -> {
                if (email == null) return false;
                return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
            });
        }

        Observable<Boolean> isPasswordMatches() {
            return Observable.combineLatest(password.asObservable(), repeatPassword.asObservable(),
                    (pass, rePass) -> {
                        if ((pass == null || pass.isEmpty()) &&
                                (rePass == null || rePass.isEmpty()))
                            return true;
                        return pass != null && pass.equals(rePass);
                    });
        }

        public User getUser() {
            user.setEmail(userEmail.get());
            user.setFirstName(firstName.get());
            user.setSurname(surname.get());
            user.setPostcode(postcode.get());
            user.setPassword(password.get());
            return user;
        }

        Observable<Boolean> isValid() {
            return Observable.combineLatest(isEmailValid(),
                    firstName.asObservable(),
                    surname.asObservable(),
                    postcode.asObservable(),
                    password.asObservable(),
                    repeatPassword.asObservable(),
                    (emailValid, first, last, post, pass, rePass) -> emailValid
                            && first != null && first.length() > 0
                            && last != null && last.length() > 0
                            && post != null && post.length() > 0
                            && pass != null && pass.length() > 0
                            && rePass != null && rePass.equals(pass));
        }

        void resetFields() {
            code.set("");
            userEmail.set("");
            firstName.set("");
            surname.set("");
            postcode.set("");
            password.set("");
            repeatPassword.set("");
        }
    }
}
