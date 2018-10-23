package com.redhelmet.alert2me.ui.help;

import android.arch.lifecycle.ViewModelProvider;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;

import com.redhelmet.alert2me.R;
import com.redhelmet.alert2me.databinding.FragmentProfileBinding;
import com.redhelmet.alert2me.ui.base.BaseFragment;

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
    }
}
