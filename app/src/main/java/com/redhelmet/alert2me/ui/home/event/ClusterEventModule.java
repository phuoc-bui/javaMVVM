package com.redhelmet.alert2me.ui.home.event;

import android.arch.lifecycle.ViewModel;

import com.redhelmet.alert2me.di.ViewModelKey;

import dagger.Binds;
import dagger.Module;
import dagger.multibindings.IntoMap;

@Module
public abstract class ClusterEventModule {
    @Binds
    @IntoMap
    @ViewModelKey(ClusterEventsViewModel.class)
    abstract ViewModel bindClusterViewModel(ClusterEventsViewModel viewModel);
}
