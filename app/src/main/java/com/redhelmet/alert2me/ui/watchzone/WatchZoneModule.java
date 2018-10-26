package com.redhelmet.alert2me.ui.watchzone;

import android.arch.lifecycle.ViewModel;

import com.redhelmet.alert2me.di.FragmentScoped;
import com.redhelmet.alert2me.di.ViewModelKey;

import dagger.Binds;
import dagger.Module;
import dagger.android.ContributesAndroidInjector;
import dagger.multibindings.IntoMap;

@Module
public abstract class WatchZoneModule {
    @FragmentScoped
    @ContributesAndroidInjector
    abstract WatchZoneFragment provideWatchZoneFragment();


    @Binds
    @IntoMap
    @ViewModelKey(WatchZoneViewModel.class)
    abstract ViewModel bindWatchZoneViewModel(WatchZoneViewModel viewModel);
}
