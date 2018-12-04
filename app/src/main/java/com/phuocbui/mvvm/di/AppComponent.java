package com.phuocbui.mvvm.di;

import com.phuocbui.basemodule.di.ViewModelModule;
import com.phuocbui.mvvm.A2MApplication;

import javax.inject.Singleton;

import dagger.Component;
import dagger.android.AndroidInjector;
import dagger.android.support.AndroidSupportInjectionModule;

/**
 * Main component of the app, created and persisted in the Application class.
 * <p>
 * Whenever a new module is created, it should be added to the list of modules.
 * [AndroidSupportInjectionModule] is the module from Dagger.Android that helps with the
 * generation and location of subcomponents.
 */
@Singleton
@Component(
        modules = {
                AndroidSupportInjectionModule.class,
                AppModule.class,
                RepositoryModule.class,
                NetworkModule.class,
                ActivityBindingModule.class,
                ViewModelModule.class}
)
public interface AppComponent extends AndroidInjector<A2MApplication> {
    @Component.Builder
    abstract class Builder extends AndroidInjector.Builder<A2MApplication> {

    }
}
