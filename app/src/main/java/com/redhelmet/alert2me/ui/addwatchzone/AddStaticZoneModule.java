package com.redhelmet.alert2me.ui.addwatchzone;

import com.redhelmet.alert2me.di.FragmentScoped;
import com.redhelmet.alert2me.di.ViewModelKey;
import com.redhelmet.alert2me.ui.eventfilter.custom.CustomFilterViewModel;
import com.redhelmet.alert2me.ui.eventfilter.defaultfilter.DefaultFilterViewModel;

import androidx.lifecycle.ViewModel;
import dagger.Binds;
import dagger.Module;
import dagger.android.ContributesAndroidInjector;
import dagger.multibindings.IntoMap;

@Module
public abstract class AddStaticZoneModule {

    @FragmentScoped
    @ContributesAndroidInjector
    abstract EditStaticZoneNameFragment provideEditStaticZoneNameFragment();

    @FragmentScoped
    @ContributesAndroidInjector
    abstract EditStaticZoneLocationFragment provideEditStaticZoneLocationFragment();

    @FragmentScoped
    @ContributesAndroidInjector (modules = {StaticZoneNotificationModule.class})
    abstract EditStaticZoneNotificationFragment provideEditStaticZoneNotificationFragment();

    @Binds
    @IntoMap
    @ViewModelKey(AddStaticZoneViewModel.class)
    abstract ViewModel bindAddStaticZoneViewModel(AddStaticZoneViewModel viewModel);

    @Binds
    @IntoMap
    @ViewModelKey(DefaultFilterViewModel.class)
    abstract ViewModel bindDefaultViewModel(DefaultFilterViewModel viewModel);

    @Binds
    @IntoMap
    @ViewModelKey(CustomFilterViewModel.class)
    abstract ViewModel bindCustomFilterViewModel(CustomFilterViewModel viewModel);
}
