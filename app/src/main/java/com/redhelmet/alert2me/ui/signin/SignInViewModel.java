package com.redhelmet.alert2me.ui.signin;

import com.redhelmet.alert2me.data.PreferenceStorage;
import com.redhelmet.alert2me.ui.base.BaseViewModel;
import com.redhelmet.alert2me.ui.base.NavigationItem;

import javax.inject.Inject;

public class SignInViewModel extends BaseViewModel {

    @Inject
    public SignInViewModel(PreferenceStorage preferenceStorage) {
        super(preferenceStorage);
        NavigationItem item = new NavigationItem(NavigationItem.CHANGE_FRAGMENT, RegisterFragment.newInstance());
        navigateTo(item);
    }
}
