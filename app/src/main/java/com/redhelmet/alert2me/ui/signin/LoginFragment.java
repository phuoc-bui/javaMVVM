package com.redhelmet.alert2me.ui.signin;

import androidx.lifecycle.ViewModelProvider;
import android.os.Bundle;
import androidx.annotation.NonNull;
import android.view.View;

import com.redhelmet.alert2me.R;
import com.redhelmet.alert2me.databinding.FragmentLoginBinding;
import com.redhelmet.alert2me.ui.base.BaseFragment;

import javax.inject.Inject;

public class LoginFragment extends BaseFragment<LoginViewModel, FragmentLoginBinding> {

    @Inject
    ViewModelProvider.Factory factory;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_login;
    }

    public static LoginFragment newInstance() {
        return new LoginFragment();
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        obtainViewModel(factory, LoginViewModel.class);
    }
}
