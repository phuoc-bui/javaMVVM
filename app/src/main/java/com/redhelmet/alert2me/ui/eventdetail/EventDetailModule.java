package com.redhelmet.alert2me.ui.eventdetail;

import android.arch.lifecycle.ViewModel;

import com.redhelmet.alert2me.di.ViewModelKey;

import dagger.Binds;
import dagger.Module;
import dagger.multibindings.IntoMap;

@Module
public abstract class EventDetailModule {
    @Binds
    @IntoMap
    @ViewModelKey(EventDetailViewModel.class)
    abstract ViewModel bindEventDetailViewModel(EventDetailViewModel viewModel);
}
