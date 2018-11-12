package com.redhelmet.alert2me.ui.signin;

import com.redhelmet.alert2me.R;
import com.redhelmet.alert2me.data.DataManager;
import com.redhelmet.alert2me.global.RxProperty;
import com.redhelmet.alert2me.ui.base.BaseViewModel;
import com.redhelmet.alert2me.ui.base.NavigationItem;

import javax.inject.Inject;

import androidx.databinding.ObservableBoolean;
import androidx.databinding.ObservableInt;
import io.reactivex.android.schedulers.AndroidSchedulers;

public class ForgotPasswordViewModel extends BaseViewModel {
    public RxProperty<String> userEmail = new RxProperty<>();
    public ObservableBoolean isValid = new ObservableBoolean(false);
    public ObservableInt emailNotValidError = new ObservableInt();

    @Inject
    public ForgotPasswordViewModel(DataManager dataManager) {
        super(dataManager);
        disposeBag.add(userEmail.asObservable()
                .map(s -> s != null && s.length() > 0 && android.util.Patterns.EMAIL_ADDRESS.matcher(s).matches())
                .subscribe(b -> {
                    isValid.set(b);
                    if (!b) emailNotValidError.set(R.string.register_email_not_valid_error);
                    else emailNotValidError.set(0);
                }));
    }

    public void onSendClick() {
        showLoadingDialog(true);
        disposeBag.add(dataManager.forgotPassword(userEmail.get())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> {
                    showLoadingDialog(false);
                    navigateTo(new NavigationItem(NavigationItem.SHOW_TOAST, R.string.forgot_send_successful));
                    navigateTo(new NavigationItem(NavigationItem.POP_FRAGMENT_BACK));
                }, error -> {
                    showLoadingDialog(false);
                    handleError(error);
                }));
    }

    public void onBackClick() {
        navigateTo(new NavigationItem(NavigationItem.POP_FRAGMENT_BACK));
    }
}
