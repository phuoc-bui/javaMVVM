package com.redhelmet.alert2me.ui.signin;

import com.redhelmet.alert2me.data.PreferenceHelper;
import com.redhelmet.alert2me.ui.base.BaseViewModel;
import com.redhelmet.alert2me.ui.base.NavigationItem;

public class SignInViewModel extends BaseViewModel {

    public SignInViewModel(PreferenceHelper preferenceHelper) {
        NavigationItem item;
        if (preferenceHelper.haveAccount()) {
            item = new NavigationItem(NavigationItem.CHANGE_FRAGMENT, LoginFragment.newInstance());
        } else {
            item = new NavigationItem(NavigationItem.CHANGE_FRAGMENT, RegisterFragment.newInstance());
        }
        navigateTo(item);
    }
}
