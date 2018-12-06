package com.phuocbui.basemodule.di;

import androidx.lifecycle.ViewModelProvider;

import dagger.Binds;
import dagger.Module;

/**
 * Module used to define the connection between the framework's {@link androidx.lifecycle.ViewModelProvider.Factory} and
 * our own implementation: {@link AppViewModelFactory}.
 */
@Module
abstract class ViewModelModule {

    @Binds
    abstract ViewModelProvider.Factory bindViewModelFactory(AppViewModelFactory factory);
}
