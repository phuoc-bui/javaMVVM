package com.redhelmet.alert2me.di;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;

import com.redhelmet.alert2me.ui.eventdetail.EventDetailViewModel;
import com.redhelmet.alert2me.ui.eventfilter.EventFilterViewModel;
import com.redhelmet.alert2me.ui.eventfilter.custom.CustomFilterViewModel;
import com.redhelmet.alert2me.ui.eventfilter.defaultfilter.DefaultFilterViewModel;
import com.redhelmet.alert2me.ui.home.HomeViewModel;
import com.redhelmet.alert2me.ui.home.event.ClusterEventsViewModel;
import com.redhelmet.alert2me.ui.home.event.EventViewModel;

import dagger.Binds;
import dagger.Module;
import dagger.multibindings.IntoMap;

/**
 * Module used to define the connection between the framework's {@link android.arch.lifecycle.ViewModelProvider.Factory} and
 * our own implementation: {@link A2MViewModelFactory}.
 */
@Module
abstract class ViewModelModule {

    @Binds
    abstract ViewModelProvider.Factory bindViewModelFactory(A2MViewModelFactory factory);

    @Binds
    @IntoMap
    @ViewModelKey(HomeViewModel.class)
    abstract ViewModel bindHomeViewModel(HomeViewModel viewModel);

    @Binds
    @IntoMap
    @ViewModelKey(EventViewModel.class)
    abstract ViewModel bindEventViewModel(EventViewModel viewModel);

    @Binds
    @IntoMap
    @ViewModelKey(ClusterEventsViewModel.class)
    abstract ViewModel bindClusterEventsViewModel(ClusterEventsViewModel viewModel);

    @Binds
    @IntoMap
    @ViewModelKey(EventDetailViewModel.class)
    abstract ViewModel bindEventDetailViewModel(EventDetailViewModel viewModel);

    @Binds
    @IntoMap
    @ViewModelKey(EventFilterViewModel.class)
    abstract ViewModel bindEventFilterViewModel(EventFilterViewModel viewModel);

    @Binds
    @IntoMap
    @ViewModelKey(DefaultFilterViewModel.class)
    abstract ViewModel bindDefaultFilterViewModel(DefaultFilterViewModel viewModel);

    @Binds
    @IntoMap
    @ViewModelKey(CustomFilterViewModel.class)
    abstract ViewModel bindCustomFilterViewModel(CustomFilterViewModel viewModel);
}
