package com.phuocbui.mvvm.di;

import com.phuocbui.basemodule.di.ActivityScoped;
import com.phuocbui.mvvm.ui.event.EventModule;
import com.phuocbui.mvvm.ui.eventdetail.EventDetailModule;
import com.phuocbui.mvvm.ui.eventdetail.EventDetailsActivity;
import com.phuocbui.mvvm.ui.help.HelpModule;
import com.phuocbui.mvvm.ui.home.HomeActivity;
import com.phuocbui.mvvm.ui.home.HomeModule;
import com.phuocbui.mvvm.ui.signin.ForgotPasswordModule;
import com.phuocbui.mvvm.ui.signin.LoginModule;
import com.phuocbui.mvvm.ui.signin.RegisterModule;
import com.phuocbui.mvvm.ui.signin.SignInActivity;
import com.phuocbui.mvvm.ui.signin.SignInModule;
import com.phuocbui.mvvm.ui.splash.SplashActivity;
import com.phuocbui.mvvm.ui.splash.SplashModule;
import com.phuocbui.mvvm.ui.watchzone.WatchZoneModule;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

/**
 * We want Dagger.Android to create a Subcomponent which has a parent Component of whichever module
 * ActivityBindingModule is on, in our case that will be [AppComponent]. You never
 * need to tell [AppComponent] that it is going to have all these subcomponents
 * nor do you need to tell these subcomponents that [AppComponent] exists.
 * We are also telling Dagger.Android that this generated SubComponent needs to include the
 * specified modules and be aware of a scope annotation [@ActivityScoped].
 * When Dagger.Android annotation processor runs it will create 2 subcomponents for us.
 */
@Module
abstract class ActivityBindingModule {

    @ActivityScoped
    @ContributesAndroidInjector(modules = {SplashModule.class})
    abstract SplashActivity splashActivity();

    @ActivityScoped
    @ContributesAndroidInjector(
            modules = {
                    SignInModule.class,
                    ForgotPasswordModule.class,
                    LoginModule.class,
                    RegisterModule.class
            }
    )
    abstract SignInActivity signInActivity();

    @ActivityScoped
    @ContributesAndroidInjector(
            modules = {
                    HomeModule.class,
                    EventModule.class,
                    HelpModule.class,
                    WatchZoneModule.class
            }
    )
    abstract HomeActivity homeActivity();

    @ActivityScoped
    @ContributesAndroidInjector(modules = {EventDetailModule.class})
    abstract EventDetailsActivity eventDetailsActivity();
}
