package com.phuocbui.mvvm.ui.eventdetail;

import androidx.lifecycle.ViewModel;

import com.phuocbui.basemodule.di.ViewModelKey;

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
