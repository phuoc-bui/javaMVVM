package com.redhelmet.alert2me.ui.eventfilter.custom;

import androidx.lifecycle.ViewModel;

import com.redhelmet.alert2me.di.FragmentScoped;
import com.redhelmet.alert2me.di.ViewModelKey;

import dagger.Binds;
import dagger.Module;
import dagger.android.ContributesAndroidInjector;
import dagger.multibindings.IntoMap;

@Module
public abstract class CustomFilterModule {
    @FragmentScoped
    @ContributesAndroidInjector
    abstract CustomFilterFragment contributeCustomFilterFragment();

    @Binds
    @IntoMap
    @ViewModelKey(CustomFilterViewModel.class)
    abstract ViewModel bindCustomFilterViewModel(CustomFilterViewModel viewModel);
}
