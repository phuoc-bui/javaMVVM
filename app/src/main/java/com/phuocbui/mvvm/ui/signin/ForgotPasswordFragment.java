package com.phuocbui.mvvm.ui.signin;

import androidx.lifecycle.ViewModelProvider;
import android.os.Bundle;
import androidx.annotation.NonNull;
import android.view.View;

import com.phuocbui.mvvm.R;
import com.phuocbui.mvvm.databinding.FragmentForgotPasswordBinding;
import com.phuocbui.basemodule.ui.base.BaseFragment;

import javax.inject.Inject;

public class ForgotPasswordFragment extends BaseFragment<ForgotPasswordViewModel, FragmentForgotPasswordBinding> {

    @Inject
    ViewModelProvider.Factory factory;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_forgot_password;
    }

    public static ForgotPasswordFragment newInstance() {
        return new ForgotPasswordFragment();
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        obtainViewModel(factory, ForgotPasswordViewModel.class);
    }
}
