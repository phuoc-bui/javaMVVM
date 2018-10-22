package com.redhelmet.alert2me.ui.event;

import android.arch.lifecycle.ViewModel;

import com.redhelmet.alert2me.di.FragmentScoped;
import com.redhelmet.alert2me.di.ViewModelKey;

import dagger.Binds;
import dagger.Module;
import dagger.android.ContributesAndroidInjector;
import dagger.multibindings.IntoMap;

@Module
public abstract class EventModule {

    @FragmentScoped
    @ContributesAndroidInjector(modules = {EventFragmentModule.class})
    abstract EventFragment provideEventFragment();

    @Binds
    @IntoMap
    @ViewModelKey(EventViewModel.class)
    abstract ViewModel bindEventViewModel(EventViewModel viewModel);
}
