package com.phuocbui.mvvm.ui.event;

import androidx.lifecycle.ViewModel;

import com.phuocbui.basemodule.di.FragmentScoped;
import com.phuocbui.basemodule.di.ViewModelKey;

import dagger.Binds;
import dagger.Module;
import dagger.android.ContributesAndroidInjector;
import dagger.multibindings.IntoMap;

@Module
public abstract class EventModule {

    @Binds
    @IntoMap
    @ViewModelKey(EventViewModel.class)
    abstract ViewModel bindEventViewModel(EventViewModel viewModel);

    @FragmentScoped
    @ContributesAndroidInjector
    abstract EventListFragment provideEventListFragment();
}
