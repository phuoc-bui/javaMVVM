package com.phuocbui.mvvm.ui.signin;

import androidx.lifecycle.ViewModelProvider;
import android.os.Bundle;
import androidx.annotation.NonNull;
import android.view.View;

import com.phuocbui.mvvm.R;
import com.phuocbui.mvvm.databinding.FragmentRegisterBinding;
import com.phuocbui.basemodule.ui.base.BaseFragment;

import javax.inject.Inject;

public class RegisterFragment extends BaseFragment<RegisterViewModel, FragmentRegisterBinding> {

    @Inject
    ViewModelProvider.Factory factory;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_register;
    }

    public static RegisterFragment newInstance() {
        return new RegisterFragment();
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        obtainViewModel(factory, RegisterViewModel.class);
    }
}
