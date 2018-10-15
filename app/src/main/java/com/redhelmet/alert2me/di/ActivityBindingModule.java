package com.redhelmet.alert2me.di;

import com.redhelmet.alert2me.ui.hint.HintsActivity;
import com.redhelmet.alert2me.ui.home.HomeActivity;
import com.redhelmet.alert2me.ui.home.HomeModule;
import com.redhelmet.alert2me.ui.home.event.EventModule;
import com.redhelmet.alert2me.ui.home.help.HelpModule;
import com.redhelmet.alert2me.ui.home.watchzone.WatchZoneModule;
import com.redhelmet.alert2me.ui.signin.ForgotPasswordModule;
import com.redhelmet.alert2me.ui.signin.LoginModule;
import com.redhelmet.alert2me.ui.signin.RegisterModule;
import com.redhelmet.alert2me.ui.signin.SignInActivity;
import com.redhelmet.alert2me.ui.signin.SignInModule;
import com.redhelmet.alert2me.ui.splash.SplashScreen;
import com.redhelmet.alert2me.ui.termsandcondition.TermConditionActivity;
import com.redhelmet.alert2me.ui.termsandcondition.TermConditionModule;

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
    @ContributesAndroidInjector(modules = {SplashScreen.class})
    abstract SplashScreen splashScreen();

    @ActivityScoped
    @ContributesAndroidInjector(modules = {HintsActivity.class})
    abstract HintsActivity hintsActivity();

    @ActivityScoped
    @ContributesAndroidInjector(modules = {TermConditionModule.class})
    abstract TermConditionActivity termConditionActivity();

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
}
