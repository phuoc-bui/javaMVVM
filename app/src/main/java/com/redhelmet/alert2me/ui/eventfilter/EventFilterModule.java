package com.redhelmet.alert2me.ui.eventfilter;

import android.arch.lifecycle.ViewModel;

import com.redhelmet.alert2me.di.ViewModelKey;

import dagger.Binds;
import dagger.Module;
import dagger.multibindings.IntoMap;

@Module
public abstract class EventFilterModule {
    @Binds
    @IntoMap
    @ViewModelKey(EventFilterViewModel.class)
    abstract ViewModel bindEventFilterViewModel(EventFilterViewModel viewModel);
}
