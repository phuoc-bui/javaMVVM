package com.redhelmet.alert2me.ui.signin;

import com.redhelmet.alert2me.data.PreferenceHelper;
import com.redhelmet.alert2me.global.Event;
import com.redhelmet.alert2me.ui.base.BaseViewModel;
import com.redhelmet.alert2me.ui.base.NavigationItem;

public class SignInViewModel extends BaseViewModel {

    public SignInViewModel(PreferenceHelper preferenceHelper) {
        Event<NavigationItem> event;
        if (preferenceHelper.haveAccount()) {
            event = new Event<>(new NavigationItem(NavigationItem.CHANGE_FRAGMENT, LoginFragment.newInstance()));
        } else {
            event = new Event<>(new NavigationItem(NavigationItem.CHANGE_FRAGMENT, RegisterFragment.newInstance()));
        }
        navigationEvent.setValue(event);
    }
}
