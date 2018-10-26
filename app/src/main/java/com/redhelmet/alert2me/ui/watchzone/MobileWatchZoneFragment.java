package com.redhelmet.alert2me.ui.watchzone;

import android.arch.lifecycle.ViewModelProvider;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;

import com.redhelmet.alert2me.R;
import com.redhelmet.alert2me.databinding.FragmentMobileWatchZoneBinding;
import com.redhelmet.alert2me.ui.base.BaseFragment;

import javax.inject.Inject;

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
    }
}
