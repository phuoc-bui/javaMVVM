package com.redhelmet.alert2me.ui.signin;

import android.arch.lifecycle.ViewModel;

import com.redhelmet.alert2me.di.ViewModelKey;

import dagger.Binds;
import dagger.Module;
import dagger.multibindings.IntoMap;

@Module
public abstract class ForgotPasswordModule {
    @Binds
    @IntoMap
    @ViewModelKey(ForgotPasswordViewModel.class)
    abstract ViewModel bindForgotPasswordViewModel(ForgotPasswordViewModel viewModel);
}
