package com.redhelmet.alert2me.ui.signin;

import com.redhelmet.alert2me.R;
import com.redhelmet.alert2me.data.DataManager;
import com.redhelmet.alert2me.data.model.User;
import com.redhelmet.alert2me.global.RxProperty;
import com.redhelmet.alert2me.ui.base.BaseViewModel;
import com.redhelmet.alert2me.ui.base.NavigationItem;

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
    public RegisterViewModel(DataManager dataManager) {
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
                    navigateTo(new NavigationItem(NavigationItem.SHOW_TOAST, response.message));
                    navigateTo(new NavigationItem(NavigationItem.CHANGE_FRAGMENT_AND_ADD_TO_BACK_STACK,
                            LoginFragment.newInstance(userModel.userEmail.get(), userModel.password.get())));
                }, error -> {
                    showLoadingDialog(false);
                    handleError(error);
                }));
    }

    public void onSignInClick() {
        userModel.resetFields();
        navigateTo(new NavigationItem(NavigationItem.CHANGE_FRAGMENT_AND_ADD_TO_BACK_STACK, LoginFragment.newInstance()));
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

        public Observable<Boolean> isEmailValid() {
            return userEmail.asObservable().map(email -> {
                if (email == null) return false;
                return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
            });
        }

        public Observable<Boolean> isPasswordMatches() {
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

        public Observable<Boolean> isValid() {
            return Observable.combineLatest(isEmailValid(),
                    firstName.asObservable(),
                    surname.asObservable(),
                    postcode.asObservable(),
                    password.asObservable(),
                    repeatPassword.asObservable(),
                    (emailValid, first, last, post, pass, rePass) -> {
                        return emailValid
                                && first != null && first.length() > 0
                                && last != null && last.length() > 0
                                && post != null && post.length() > 0
                                && pass != null && pass.length() > 0
                                && rePass != null && rePass.equals(pass);
                    });
        }

        public void resetFields() {
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
