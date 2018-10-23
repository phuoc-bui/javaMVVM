package com.redhelmet.alert2me.ui.signin;

import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;

import com.redhelmet.alert2me.R;
import com.redhelmet.alert2me.data.DataManager;
import com.redhelmet.alert2me.data.model.User;
import com.redhelmet.alert2me.global.RxProperty;
import com.redhelmet.alert2me.ui.base.BaseViewModel;
import com.redhelmet.alert2me.ui.base.NavigationItem;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;

public class RegisterViewModel extends BaseViewModel {

    public UserModel userModel = new UserModel();
    public ObservableBoolean enableButton = new ObservableBoolean(false);

    @Inject
    public RegisterViewModel(DataManager dataManager) {
        super(dataManager);
        disposeBag.add(userModel.isValid().subscribe(b -> enableButton.set(b)));
    }

    public void onRegisterClick() {
        showLoadingDialog(true);
        disposeBag.add(dataManager.registerAccount(userModel.getUser())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> {
                    showLoadingDialog(false);
                    navigateTo(new NavigationItem(NavigationItem.SHOW_TOAST, R.string.register_successful_message));
                    navigateTo(new NavigationItem(NavigationItem.CHANGE_FRAGMENT, LoginFragment.newInstance()));
                }, error -> {
                    showLoadingDialog(false);
                    handleError(error);
                }));
    }

    public void onSignInClick() {
        navigateTo(new NavigationItem(NavigationItem.CHANGE_FRAGMENT, LoginFragment.newInstance()));
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

        public UserModel(User user) {
            this.user = user;
            code.set(String.valueOf(user.getId()));
            userEmail.set(user.getEmail());
            firstName.set(user.getFirstName());
            password.set(user.getPassword());
            postcode.set(user.getPostcode());
            surname.set(user.getSurname());
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
            return Observable.combineLatest(userEmail.asObservable(),
                    firstName.asObservable(),
                    surname.asObservable(),
                    postcode.asObservable(),
                    password.asObservable(),
                    repeatPassword.asObservable(),
                    (mail, first, last, post, pass, rePass) -> {
                        return mail != null && mail.length() > 0
                                && first != null && first.length() > 0
                                && last != null && last.length() > 0
                                && post != null && post.length() > 0
                                && pass != null && pass.length() > 0
                                && rePass != null && rePass.equals(pass);
                    });
        }
    }
}
