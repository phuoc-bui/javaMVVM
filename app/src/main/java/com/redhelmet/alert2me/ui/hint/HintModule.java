package com.redhelmet.alert2me.ui.hint;

import android.arch.lifecycle.ViewModel;

import com.redhelmet.alert2me.di.ViewModelKey;

import dagger.Binds;
import dagger.Module;
import dagger.multibindings.IntoMap;

@Module
public abstract class HintModule {
    @Binds
    @IntoMap
    @ViewModelKey(HintViewModel.class)
    abstract ViewModel bindHintViewModel(HintViewModel viewModel);
}
