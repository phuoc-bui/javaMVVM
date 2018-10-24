package com.redhelmet.alert2me.ui.help;

import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.net.Uri;
import android.util.Log;

import com.redhelmet.alert2me.BuildConfig;
import com.redhelmet.alert2me.R;
import com.redhelmet.alert2me.data.DataManager;
import com.redhelmet.alert2me.data.PreferenceStorage;
import com.redhelmet.alert2me.data.model.User;
import com.redhelmet.alert2me.global.RxProperty;
import com.redhelmet.alert2me.ui.base.BaseViewModel;
import com.redhelmet.alert2me.ui.base.NavigationItem;
import com.redhelmet.alert2me.ui.signin.SignInActivity;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;

public class HelpViewModel extends BaseViewModel {
    public ObservableField<String> profileString = new ObservableField<>();
    public ObservableField<String> versionString = new ObservableField<>();
    public UserModel userModel;

    public RxProperty<String> oldPassword = new RxProperty<>();
    public RxProperty<String> newPassword = new RxProperty<>();
    public RxProperty<String> repeatPassword = new RxProperty<>();

    public ObservableBoolean enableUpdateButton = new ObservableBoolean(false);

    @Inject
    public HelpViewModel(DataManager dataManager, PreferenceStorage pref) {
        super(dataManager, pref);
        User currentUser = pref.getCurrentUser();
        if (currentUser != null) {
            userModel = new UserModel(currentUser);
            String name = currentUser.getFirstName();
            String email = currentUser.getEmail();
            profileString.set(name + "\n" + email);
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
        String url = "https://a2m.cloud/";
        navigateTo(new NavigationItem(NavigationItem.START_WEB_VIEW, Uri.parse(url)));
    }

    public void onUserProfileClick() {
        navigateTo(new NavigationItem(NavigationItem.CHANGE_FRAGMENT_AND_ADD_TO_BACK_STACK, ProfileFragment.newInstance()));
    }

    public void onSupportClick() {
        String url = "https://a2m.cloud/";
        navigateTo(new NavigationItem(NavigationItem.START_WEB_VIEW, Uri.parse(url)));
    }

    public void onLogoutClick() {
        navigateTo(new NavigationItem(NavigationItem.START_ACTIVITY_AND_CLEAR_TASK, SignInActivity.class));
    }

    public void onFirstNameClick() {
        navigateTo(new NavigationItem(NavigationItem.CHANGE_FRAGMENT_AND_ADD_TO_BACK_STACK, EditProfileFieldFragment.newInstance(R.string.register_first_name_hint)));
    }

    public void onSurnameClick() {
        navigateTo(new NavigationItem(NavigationItem.CHANGE_FRAGMENT_AND_ADD_TO_BACK_STACK, EditProfileFieldFragment.newInstance(R.string.register_surname_hint)));
    }

    public void onPostcodeClick() {
        navigateTo(new NavigationItem(NavigationItem.CHANGE_FRAGMENT_AND_ADD_TO_BACK_STACK, EditProfileFieldFragment.newInstance(R.string.register_postcode_hint)));
    }

    public void onPasswordClick() {
        navigateTo(new NavigationItem(NavigationItem.CHANGE_FRAGMENT_AND_ADD_TO_BACK_STACK, new EditPasswordFragment()));
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

                    navigateTo(new NavigationItem(NavigationItem.POP_FRAGMENT_BACK));
                }, error -> showLoadingDialog(false)));
    }

    @Override
    protected void onCleared() {
        Log.e("HelpViewModel", "onCleared");
        super.onCleared();
    }

    public static class UserModel {

        private User user;
        public ObservableField<String> code = new ObservableField<>();
        public ObservableField<String> userEmail = new ObservableField<>();
        public ObservableField<String> firstName = new ObservableField<>();
        public ObservableField<String> surname = new ObservableField<>();
        public ObservableField<String> postcode = new ObservableField<>();
        public ObservableField<String> password = new ObservableField<>();

        public UserModel(User user) {
            this.user = user;
            code.set(String.valueOf(user.getId()));
            userEmail.set(user.getEmail());
            firstName.set(user.getFirstName());
            password.set(user.getPassword());
            postcode.set(user.getPostcode());
            surname.set(user.getSurname());
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

        public User getUser() {
            user.setEmail(userEmail.get());
            user.setFirstName(firstName.get());
            user.setSurname(surname.get());
            user.setPostcode(postcode.get());
            user.setPassword(password.get());
            return user;
        }
    }
}
