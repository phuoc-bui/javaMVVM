package com.phuocbui.mvvm.ui.help;

import androidx.lifecycle.ViewModel;

import com.phuocbui.basemodule.di.FragmentScoped;
import com.phuocbui.basemodule.di.ViewModelKey;

import dagger.Binds;
import dagger.Module;
import dagger.android.ContributesAndroidInjector;
import dagger.multibindings.IntoMap;

@Module
public abstract class HelpModule {

    @FragmentScoped
    @ContributesAndroidInjector
    abstract ProfileFragment provideProfileFragment();

    @FragmentScoped
    @ContributesAndroidInjector
    abstract HelpFragment provideHelpFragment();

    @FragmentScoped
    @ContributesAndroidInjector
    abstract EditProfileFieldFragment provideEditProfileFieldFragment();

    @FragmentScoped
    @ContributesAndroidInjector
    abstract EditPasswordFragment provideEditPasswordFragment();

    @Binds
    @IntoMap
    @ViewModelKey(HelpViewModel.class)
    abstract ViewModel bindHelpViewModel(HelpViewModel viewModel);
}
