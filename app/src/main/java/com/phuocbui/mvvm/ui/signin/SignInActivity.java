package com.phuocbui.mvvm.ui.signin;

import androidx.lifecycle.ViewModelProvider;
import android.os.Bundle;

import com.phuocbui.mvvm.R;
import com.phuocbui.mvvm.databinding.ActivitySignInBinding;
import com.phuocbui.basemodule.ui.base.BaseActivity;

import javax.inject.Inject;

public class SignInActivity extends BaseActivity<SignInViewModel, ActivitySignInBinding> {

    @Inject
    ViewModelProvider.Factory factory;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_sign_in;
    }

    @Override
    protected int getFragmentContainer() {
        return R.id.container;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        obtainViewModel(factory, SignInViewModel.class);
    }
}
