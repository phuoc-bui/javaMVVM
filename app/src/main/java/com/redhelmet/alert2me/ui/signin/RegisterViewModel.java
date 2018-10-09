package com.redhelmet.alert2me.ui.signin;

import com.redhelmet.alert2me.data.DataManager;
import com.redhelmet.alert2me.global.Event;
import com.redhelmet.alert2me.global.RxProperty;
import com.redhelmet.alert2me.ui.base.BaseViewModel;
import com.redhelmet.alert2me.ui.base.NavigationItem;

public class RegisterViewModel extends BaseViewModel {
    public RxProperty<String> userEmail = new RxProperty<>("");
    public RxProperty<String> firstName = new RxProperty<>("");
    public RxProperty<String> surname = new RxProperty<>("");
    public RxProperty<String> postcode = new RxProperty<>("");
    public RxProperty<String> password = new RxProperty<>("");
    public RxProperty<String> repeatPassword = new RxProperty<>("");

    public RegisterViewModel(DataManager dataManager) {
        super(dataManager);
    }

    public void onSignInClick() {
        navigationEvent.setValue(new Event<>(new NavigationItem(NavigationItem.CHANGE_FRAGMENT, LoginFragment.newInstance())));
    }
}
