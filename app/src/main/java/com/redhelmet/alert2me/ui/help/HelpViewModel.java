package com.redhelmet.alert2me.ui.help;

import android.databinding.ObservableField;
import android.net.Uri;
import android.util.Log;

import com.redhelmet.alert2me.BuildConfig;
import com.redhelmet.alert2me.data.PreferenceStorage;
import com.redhelmet.alert2me.data.model.User;
import com.redhelmet.alert2me.ui.base.BaseViewModel;
import com.redhelmet.alert2me.ui.base.NavigationItem;

import javax.inject.Inject;

public class HelpViewModel extends BaseViewModel {
    public ObservableField<String> profileString = new ObservableField<>();
    public ObservableField<String> versionString = new ObservableField<>();
    public UserModel userModel;

    @Inject
    public HelpViewModel(PreferenceStorage pref) {
        super(pref);
        Log.e("HelpViewModel", "constructor");
        User currentUser = pref.getCurrentUser();
        if (currentUser != null) {
            userModel = new UserModel(currentUser);
            String name = currentUser.getFirstName();
            String email = currentUser.getEmail();
            profileString.set(name + "\n" + email);
        }
        versionString.set(BuildConfig.VERSION_NAME);
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

    @Override
    protected void onCleared() {
        Log.e("HelpViewModel", "onCleared");
        super.onCleared();
    }

    public static class UserModel {
        private User user;
        public ObservableField<String> code = new ObservableField<>("ssss");
        public ObservableField<String> userEmail = new ObservableField<>("sss");
        public ObservableField<String> firstName = new ObservableField<>("sss");
        public ObservableField<String> surname = new ObservableField<>("sss");
        public ObservableField<String> postcode = new ObservableField<>("fff");
        public ObservableField<String> password = new ObservableField<>("ffff");

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
    }
}
