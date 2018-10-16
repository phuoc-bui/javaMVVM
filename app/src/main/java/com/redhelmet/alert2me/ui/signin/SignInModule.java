package com.redhelmet.alert2me.ui.signin;

import android.arch.lifecycle.ViewModel;

import com.redhelmet.alert2me.di.FragmentScoped;
import com.redhelmet.alert2me.di.ViewModelKey;

import dagger.Binds;
import dagger.Module;
import dagger.android.ContributesAndroidInjector;
import dagger.multibindings.IntoMap;

@Module
public abstract class SignInModule {

    @FragmentScoped
    @ContributesAndroidInjector
    abstract RegisterFragment provideRegisterFragment();

    @FragmentScoped
    @ContributesAndroidInjector
    abstract LoginFragment provideLoginFragment();

    @FragmentScoped
    @ContributesAndroidInjector
    abstract ForgotPasswordFragment provideForgotPasswordFragment();

    @Binds
    @IntoMap
    @ViewModelKey(SignInViewModel.class)
    abstract ViewModel bindSignInViewModel(SignInViewModel viewModel);
}
