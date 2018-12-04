package com.phuocbui.mvvm.ui.watchzone;

import android.os.Bundle;
import android.view.View;

import com.phuocbui.mvvm.R;
import com.phuocbui.mvvm.databinding.FragmentMobileWatchZoneBinding;
import com.phuocbui.basemodule.ui.base.BaseFragment;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;

public class MobileWatchZoneFragment extends BaseFragment<WatchZoneViewModel, FragmentMobileWatchZoneBinding> {
    @Inject
    ViewModelProvider.Factory factory;


    @Override
    protected int getLayoutId() {
        return R.layout.fragment_mobile_watch_zone;
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        obtainViewModel(factory, WatchZoneViewModel.class);

        viewModel.setRingSound("Default");

    }
}
