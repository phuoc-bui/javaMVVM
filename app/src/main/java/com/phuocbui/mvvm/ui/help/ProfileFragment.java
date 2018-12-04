package com.phuocbui.mvvm.ui.help;

import androidx.lifecycle.ViewModelProvider;
import android.os.Bundle;
import androidx.annotation.NonNull;
import android.view.View;

import com.phuocbui.mvvm.R;
import com.phuocbui.mvvm.databinding.FragmentProfileBinding;
import com.phuocbui.basemodule.ui.base.BaseFragment;

import javax.inject.Inject;

public class ProfileFragment extends BaseFragment<HelpViewModel, FragmentProfileBinding> {

    @Inject
    ViewModelProvider.Factory factory;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_profile;
    }

    public static ProfileFragment newInstance() {
        return new ProfileFragment();
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        obtainViewModel(factory, HelpViewModel.class);
        getBaseActivity().hideToolbar(false);
        getBaseActivity().updateToolbarTitle(getString(R.string.back));
        getBaseActivity().showHomeButton(true);
    }
}
