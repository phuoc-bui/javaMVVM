package com.phuocbui.mvvm.ui.watchzone;

import com.phuocbui.basemodule.di.ChildFragmentScoped;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class WatchZoneFragmentModule {

    @ChildFragmentScoped
    @ContributesAndroidInjector
    abstract StaticWatchZoneFragment contributeStaticWatchZoneFragment();

    @ChildFragmentScoped
    @ContributesAndroidInjector
    abstract MobileWatchZoneFragment contributeMobileWatchZoneFragment();
}
