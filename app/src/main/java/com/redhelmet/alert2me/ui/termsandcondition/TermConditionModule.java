package com.redhelmet.alert2me.ui.termsandcondition;

import android.arch.lifecycle.ViewModel;

import com.redhelmet.alert2me.di.ViewModelKey;

import dagger.Binds;
import dagger.Module;
import dagger.multibindings.IntoMap;

@Module
public abstract class TermConditionModule {
    @Binds
    @IntoMap
    @ViewModelKey(TermsConditionViewModel.class)
    abstract ViewModel bindTermsConditionViewModel(TermsConditionViewModel viewModel);
}
