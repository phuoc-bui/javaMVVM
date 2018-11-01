package com.redhelmet.alert2me.ui.signin;

import android.os.Bundle;
import android.view.View;

import com.redhelmet.alert2me.R;
import com.redhelmet.alert2me.databinding.FragmentLoginBinding;
import com.redhelmet.alert2me.ui.base.BaseFragment;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;

public class LoginFragment extends BaseFragment<LoginViewModel, FragmentLoginBinding> {

    private static final String EXTRA_EMAIL = "email";
    private static final String EXTRA_PASS = "pass";

    @Inject
    ViewModelProvider.Factory factory;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_login;
    }

    public static LoginFragment newInstance() {
        return new LoginFragment();
    }

    public static LoginFragment newInstance(String email, String pass) {
        LoginFragment fragment = new LoginFragment();
        Bundle bundle = new Bundle();
        bundle.putString(EXTRA_EMAIL, email);
        bundle.putString(EXTRA_PASS, pass);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        obtainViewModel(factory, LoginViewModel.class);
        Bundle bundle = getArguments();
        if (bundle != null) {
            viewModel.userEmail.set(bundle.getString(EXTRA_EMAIL, ""));
            viewModel.password.set(bundle.getString(EXTRA_PASS, ""));
        }
    }
}
