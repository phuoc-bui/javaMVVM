package com.redhelmet.alert2me.ui.watchzone;

import com.redhelmet.alert2me.di.FragmentScoped;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class WatchZoneModule {
    @FragmentScoped
    @ContributesAndroidInjector
    abstract WatchZoneFragment provideWatchZoneFragment();
}
