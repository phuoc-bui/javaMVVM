package com.phuocbui.mvvm.ui.help;

import android.util.Pair;

import com.phuocbui.mvvm.BuildConfig;
import com.phuocbui.mvvm.R;
import com.phuocbui.mvvm.data.DataManager;
import com.phuocbui.mvvm.data.PreferenceStorage;
import com.phuocbui.mvvm.data.model.User;
import com.phuocbui.basemodule.global.RxProperty;
import com.phuocbui.basemodule.ui.base.BaseViewModel;
import com.phuocbui.mvvm.ui.signin.SignInActivity;

import javax.inject.Inject;

import androidx.databinding.ObservableBoolean;
import androidx.databinding.ObservableField;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;

public class HelpViewModel extends BaseViewModel {

    public ObservableField<String> versionString = new ObservableField<>();
    public UserModel userModel;

    public RxProperty<String> oldPassword = new RxProperty<>();
    public RxProperty<String> newPassword = new RxProperty<>();
    public RxProperty<String> repeatPassword = new RxProperty<>();

    public ObservableBoolean enableUpdateButton = new ObservableBoolean(false);

    @Inject
    public HelpViewModel(DataManager dataManager, PreferenceStorage pref) {
        super(dataManager, pref);

        long deviceId = pref.getDeviceInfo().getId();

        User currentUser = pref.getCurrentUser();
        if (currentUser != null) {
            userModel = new UserModel(currentUser, deviceId);
        }
        versionString.set(BuildConfig.VERSION_NAME);

        disposeBag.add(Observable.combineLatest(oldPassword.asObservable(),
                newPassword.asObservable(),
                repeatPassword.asObservable(), (o, n, r) ->
                        (o != null && !o.isEmpty() &&
                                n != null && !n.isEmpty() &&
                                r != null && !r.isEmpty() &&
                                n.equals(r))).subscribe(b -> enableUpdateButton.set(b)));
    }

    public void onLegalClick() {
        String url = preferenceStorage.getAppConfig().getTermsAndConditionUrl();
        if (url == null) url = "https://google.com/";
        startWebview(url);
    }

    public void onUserProfileClick() {
        changeFragment(ProfileFragment.newInstance(), true);
    }

    public void onSupportClick() {
        String url = preferenceStorage.getAppConfig().getSupportUrl();
        if (url == null) url = "https://google.com/";
        startWebview(url);
    }

    public void onLogoutClick() {
        preferenceStorage.setLoggedIn(false);
        startActivity(SignInActivity.class, true);
    }

    public void onFirstNameClick() {
        changeFragment(EditProfileFieldFragment.newInstance(R.string.register_first_name_hint));
    }

    public void onSurnameClick() {
        changeFragment(EditProfileFieldFragment.newInstance(R.string.register_surname_hint));
    }

    public void onPostcodeClick() {
        changeFragment(EditProfileFieldFragment.newInstance(R.string.register_postcode_hint));
    }

    public void onPasswordClick() {
        changeFragment(new EditPasswordFragment());
    }

    public void onUpdateClick() {
        showLoadingDialog(true);
        disposeBag.add(dataManager.updateUserProfile(userModel.getUser())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(user -> {
                    showLoadingDialog(false);
                    // reset edit password fragment
                    oldPassword.set("");
                    newPassword.set("");
                    repeatPassword.set("");
                    userModel.updateUser(user);

                    popFragmentBack();
                }, error -> {
                    showLoadingDialog(false);
                    userModel.rollbackToOrigin();
                    handleError(error);
                }));
    }

    public static class UserModel {

        private User user;
        public ObservableField<Pair<String, String>> supportCode = new ObservableField<>();
        public ObservableField<String> userEmail = new ObservableField<>();
        public ObservableField<String> firstName = new ObservableField<>();
        public ObservableField<String> surname = new ObservableField<>();
        public ObservableField<String> postcode = new ObservableField<>();
        public ObservableField<String> password = new ObservableField<>();
        public ObservableField<String> profileString = new ObservableField<>();

        public UserModel(User user, long deviceId) {
            this.user = user;
            supportCode.set(new Pair<>( String.valueOf(deviceId), String.valueOf(user.getId())));
            userEmail.set(user.getEmail());
            firstName.set(user.getFirstName());
            password.set(user.getPassword());
            postcode.set(user.getPostcode());
            surname.set(user.getSurname());

            String name = user.getFirstName();
            String email = user.getEmail();
            profileString.set(name + "\n" + email);
        }

        public void updateUser(User user) {
            this.user = user;
            String name = user.getFirstName();
            String email = user.getEmail();
            profileString.set(name + "\n" + email);
        }

        public ObservableField<String> getObservable(int hintId) {
            switch (hintId) {
                case R.string.register_first_name_hint:
                    return firstName;
                case R.string.register_surname_hint:
                    return surname;
                case R.string.register_postcode_hint:
                    return postcode;
                case R.string.login_password_hint:
                    return password;
            }
            throw new Error("This field doesn't support");
        }

        public void rollbackToOrigin() {
            firstName.set(user.getFirstName());
            password.set(user.getPassword());
            postcode.set(user.getPostcode());
            surname.set(user.getSurname());
        }

        public User getUser() {
            User user = this.user.clone();
            user.setEmail(userEmail.get());
            user.setFirstName(firstName.get());
            user.setSurname(surname.get());
            user.setPostcode(postcode.get());
            user.setPassword(password.get());
            return user;
        }
    }
}
