package com.redhelmet.alert2me.ui.help;

import android.databinding.ObservableField;
import android.net.Uri;

import com.redhelmet.alert2me.BuildConfig;
import com.redhelmet.alert2me.data.PreferenceStorage;
import com.redhelmet.alert2me.data.model.User;
import com.redhelmet.alert2me.ui.base.BaseViewModel;
import com.redhelmet.alert2me.ui.base.NavigationItem;

import javax.inject.Inject;

public class HelpViewModel extends BaseViewModel {
    public ObservableField<String> profileString = new ObservableField<>();
    public ObservableField<String> versionString = new ObservableField<>();

    @Inject
    public HelpViewModel(PreferenceStorage pref) {
        super(pref);
        User currentUser = pref.getCurrentUser();
        if (currentUser != null) {
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

    }

    public void onSupportClick() {
        String url = "https://a2m.cloud/";
        navigateTo(new NavigationItem(NavigationItem.START_WEB_VIEW, Uri.parse(url)));
    }
}
