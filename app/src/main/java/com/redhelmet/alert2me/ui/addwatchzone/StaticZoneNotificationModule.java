package com.redhelmet.alert2me.ui.addwatchzone;

import com.redhelmet.alert2me.di.ChildFragmentScoped;
import com.redhelmet.alert2me.ui.event.EventListFragment;
import com.redhelmet.alert2me.ui.event.MapFragment;
import com.redhelmet.alert2me.ui.eventfilter.custom.CustomFilterFragment;
import com.redhelmet.alert2me.ui.eventfilter.defaultfilter.DefaultFilterFragment;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class StaticZoneNotificationModule {
    @ChildFragmentScoped
    @ContributesAndroidInjector
    abstract DefaultFilterFragment contributeDefaultFilterFragment();

    @ChildFragmentScoped
    @ContributesAndroidInjector
    abstract CustomFilterFragment contributeCustomFilterFragment();
}
