package com.redhelmet.alert2me.di;

import com.redhelmet.alert2me.A2MApplication;

import javax.inject.Singleton;

import dagger.Component;
import dagger.android.AndroidInjector;
import dagger.android.support.AndroidSupportInjectionModule;

/**
 * Main component of the app, created and persisted in the Application class.
 *
 * Whenever a new module is created, it should be added to the list of modules.
 * [AndroidSupportInjectionModule] is the module from Dagger.Android that helps with the
 * generation and location of subcomponents.
 */
@Singleton
@Component(
        modules = {
        AndroidSupportInjectionModule.class,
        AppModule.class,
        ActivityBindingModule.class,
        ViewModelModule.class,
        ServiceBindingModule::class,
        SharedModule::class,
        SignInModule::class,
        SignInViewModelDelegateModule::class}
)
public interface AppComponent extends AndroidInjector<A2MApplication> {
    @Component.Builder
    abstract class Builder extends AndroidInjector.Builder<A2MApplication>{

    }
}
