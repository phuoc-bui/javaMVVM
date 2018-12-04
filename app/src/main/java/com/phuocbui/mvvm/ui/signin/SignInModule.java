package com.phuocbui.mvvm.ui.signin;

import androidx.lifecycle.ViewModel;

import com.phuocbui.basemodule.di.FragmentScoped;
import com.phuocbui.basemodule.di.ViewModelKey;

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
