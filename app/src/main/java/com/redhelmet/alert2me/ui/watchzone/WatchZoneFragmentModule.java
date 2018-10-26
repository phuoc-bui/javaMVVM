package com.redhelmet.alert2me.ui.watchzone;

import com.redhelmet.alert2me.di.ChildFragmentScoped;

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
