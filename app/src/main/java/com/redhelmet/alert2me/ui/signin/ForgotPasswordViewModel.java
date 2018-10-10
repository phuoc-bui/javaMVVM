package com.redhelmet.alert2me.ui.signin;

import android.databinding.ObservableBoolean;

import com.redhelmet.alert2me.R;
import com.redhelmet.alert2me.data.DataManager;
import com.redhelmet.alert2me.global.RxProperty;
import com.redhelmet.alert2me.ui.base.BaseViewModel;
import com.redhelmet.alert2me.ui.base.NavigationItem;

import io.reactivex.android.schedulers.AndroidSchedulers;

public class ForgotPasswordViewModel extends BaseViewModel {
    public RxProperty<String> userEmail = new RxProperty<>();
    public ObservableBoolean isValid = new ObservableBoolean(false);

    public ForgotPasswordViewModel(DataManager dataManager) {
        super(dataManager);
        disposeBag.add(userEmail.asObservable()
                .map(s -> s != null && s.length() > 0)
                .subscribe(b -> isValid.set(b)));
    }

    public void onSendClick() {
        showLoadingDialog(true);
        disposeBag.add(dataManager.forgotPassword(userEmail.get())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> {
                    showLoadingDialog(false);
                    navigateTo(new NavigationItem(NavigationItem.SHOW_TOAST, R.string.forgot_send_successful));
                }, error -> {
                    showLoadingDialog(false);
                    navigateTo(new NavigationItem(NavigationItem.SHOW_TOAST, error.getMessage()));
                }));
    }

    public void onBackClick() {
        navigateTo(new NavigationItem(NavigationItem.POP_FRAGMENT_BACK));
    }
}
