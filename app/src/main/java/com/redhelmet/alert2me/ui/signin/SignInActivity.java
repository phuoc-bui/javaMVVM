package com.redhelmet.alert2me.ui.signin;

import android.os.Bundle;

import com.redhelmet.alert2me.R;
import com.redhelmet.alert2me.databinding.ActivitySignInBinding;
import com.redhelmet.alert2me.ui.base.BaseActivity;

public class SignInActivity extends BaseActivity<SignInViewModel, ActivitySignInBinding> {
    @Override
    protected int getLayoutId() {
        return R.layout.activity_sign_in;
    }

    @Override
    protected Class<SignInViewModel> obtainViewModel() {
        return SignInViewModel.class;
    }

    @Override
    protected int getFragmentContainer() {
        return R.id.container;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
}
