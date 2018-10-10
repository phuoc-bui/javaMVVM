package com.redhelmet.alert2me.ui.signin;

import com.redhelmet.alert2me.R;
import com.redhelmet.alert2me.databinding.FragmentForgotPasswordBinding;
import com.redhelmet.alert2me.ui.base.BaseFragment;

public class ForgotPasswordFragment extends BaseFragment<ForgotPasswordViewModel, FragmentForgotPasswordBinding> {
    @Override
    protected int getLayoutId() {
        return R.layout.fragment_forgot_password;
    }

    @Override
    protected Class<ForgotPasswordViewModel> getViewModelClass() {
        return ForgotPasswordViewModel.class;
    }

    public static ForgotPasswordFragment newInstance() {
        return new ForgotPasswordFragment();
    }
}
