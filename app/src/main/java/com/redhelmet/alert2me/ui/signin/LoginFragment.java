package com.redhelmet.alert2me.ui.signin;

import com.redhelmet.alert2me.R;
import com.redhelmet.alert2me.databinding.FragmentLoginBinding;
import com.redhelmet.alert2me.ui.base.BaseFragment;

public class LoginFragment extends BaseFragment<LoginViewModel, FragmentLoginBinding> {
    @Override
    protected int getLayoutId() {
        return R.layout.fragment_login;
    }

    @Override
    protected Class<LoginViewModel> getViewModelClass() {
        return LoginViewModel.class;
    }

    public static LoginFragment newInstance() {
        return new LoginFragment();
    }
}
