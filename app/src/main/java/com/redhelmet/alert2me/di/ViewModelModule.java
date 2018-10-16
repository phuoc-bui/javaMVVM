package com.redhelmet.alert2me.di;

import android.arch.lifecycle.ViewModelProvider;

import dagger.Binds;
import dagger.Module;

/**
 * Module used to define the connection between the framework's {@link android.arch.lifecycle.ViewModelProvider.Factory} and
 * our own implementation: {@link A2MViewModelFactory}.
 */
@Module
abstract class ViewModelModule {

    @Binds
    abstract ViewModelProvider.Factory bindViewModelFactory(A2MViewModelFactory factory);
}
