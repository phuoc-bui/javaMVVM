package com.phuocbui.mvvm.ui.signin;

import com.phuocbui.mvvm.R;
import com.phuocbui.mvvm.data.DataManager;
import com.phuocbui.basemodule.global.RxProperty;
import com.phuocbui.basemodule.ui.base.BaseViewModel;

import javax.inject.Inject;

import androidx.databinding.ObservableBoolean;
import androidx.databinding.ObservableInt;
import io.reactivex.android.schedulers.AndroidSchedulers;

public class ForgotPasswordViewModel extends BaseViewModel {
    public RxProperty<String> userEmail = new RxProperty<>();
    public ObservableBoolean isValid = new ObservableBoolean(false);
    public ObservableInt emailNotValidError = new ObservableInt();

    @Inject
    ForgotPasswordViewModel(DataManager dataManager) {
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
                    showToast(R.string.forgot_send_successful);
                    popFragmentBack();
                }, error -> {
                    showLoadingDialog(false);
                    handleError(error);
                }));
    }

    public void onBackClick() {
        popFragmentBack();
    }
}
