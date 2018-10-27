package com.redhelmet.alert2me.ui.eventfilter.defaultfilter;

import androidx.lifecycle.ViewModel;

import com.redhelmet.alert2me.di.FragmentScoped;
import com.redhelmet.alert2me.di.ViewModelKey;

import dagger.Binds;
import dagger.Module;
import dagger.android.ContributesAndroidInjector;
import dagger.multibindings.IntoMap;

@Module
public abstract class DefaultFilterModule {
    @FragmentScoped
    @ContributesAndroidInjector
    abstract DefaultFilterFragment contributeDefaultFilterFragment();

    @Binds
    @IntoMap
    @ViewModelKey(DefaultFilterViewModel.class)
    abstract ViewModel bindDefaultViewModel(DefaultFilterViewModel viewModel);
}
