package com.phuocbui.mvvm.ui.signin;

import com.phuocbui.mvvm.data.PreferenceStorage;
import com.phuocbui.basemodule.ui.base.BaseViewModel;

import javax.inject.Inject;

public class SignInViewModel extends BaseViewModel {

    @Inject
    SignInViewModel(PreferenceStorage preferenceStorage) {
        super(preferenceStorage);
        preferenceStorage.setLoggedIn(false);
        changeFragment(RegisterFragment.newInstance());
    }
}
