package com.redhelmet.alert2me.ui.home.event;

import com.redhelmet.alert2me.di.ChildFragmentScoped;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class EventFragmentModule {
    @ChildFragmentScoped
    @ContributesAndroidInjector
    abstract MapFragment contributeEventFragment();

    @ChildFragmentScoped
    @ContributesAndroidInjector
    abstract EventListFragment contributeEventListFragment();
}
