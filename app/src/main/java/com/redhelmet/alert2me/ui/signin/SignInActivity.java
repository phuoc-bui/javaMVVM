package com.redhelmet.alert2me.ui.signin;

import android.arch.lifecycle.ViewModelProvider;
import android.os.Bundle;

import com.redhelmet.alert2me.R;
import com.redhelmet.alert2me.databinding.ActivitySignInBinding;
import com.redhelmet.alert2me.ui.base.BaseActivity;

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
