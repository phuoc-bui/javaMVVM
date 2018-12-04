package com.phuocbui.mvvm.ui.watchzone;

import androidx.lifecycle.ViewModel;

import com.phuocbui.basemodule.di.FragmentScoped;
import com.phuocbui.basemodule.di.ViewModelKey;

import dagger.Binds;
import dagger.Module;
import dagger.android.ContributesAndroidInjector;
import dagger.multibindings.IntoMap;

@Module
public abstract class WatchZoneModule {
    @FragmentScoped
    @ContributesAndroidInjector (modules = {WatchZoneFragmentModule.class})
    abstract WatchZoneFragment provideWatchZoneFragment();


    @Binds
    @IntoMap
    @ViewModelKey(WatchZoneViewModel.class)
    abstract ViewModel bindWatchZoneViewModel(WatchZoneViewModel viewModel);
}
