package com.redhelmet.alert2me.ui.signin;

import com.redhelmet.alert2me.R;
import com.redhelmet.alert2me.databinding.FragmentRegisterBinding;
import com.redhelmet.alert2me.ui.base.BaseFragment;

public class RegisterFragment extends BaseFragment<RegisterViewModel, FragmentRegisterBinding> {
    @Override
    protected int getLayoutId() {
        return R.layout.fragment_register;
    }

    @Override
    protected Class<RegisterViewModel> getViewModelClass() {
        return RegisterViewModel.class;
    }

    public static RegisterFragment newInstance() {
        return new RegisterFragment();
    }
}
