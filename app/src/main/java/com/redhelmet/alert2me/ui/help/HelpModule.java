package com.redhelmet.alert2me.ui.help;

import android.arch.lifecycle.ViewModel;

import com.redhelmet.alert2me.di.ChildFragmentScoped;
import com.redhelmet.alert2me.di.FragmentScoped;
import com.redhelmet.alert2me.di.ViewModelKey;

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

    @Binds
    @IntoMap
    @ViewModelKey(HelpViewModel.class)
    abstract ViewModel bindHelpViewModel(HelpViewModel viewModel);
}
