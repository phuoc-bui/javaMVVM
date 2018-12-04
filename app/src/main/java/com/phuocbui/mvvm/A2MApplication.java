package com.phuocbui.mvvm;

import com.jakewharton.threetenabp.AndroidThreeTen;
import com.phuocbui.mvvm.di.DaggerAppComponent;
import com.phuocbui.basemodule.util.ReleaseTree;

import dagger.android.AndroidInjector;
import dagger.android.support.DaggerApplication;
import timber.log.Timber;

public class A2MApplication extends DaggerApplication {

    @Override
    public void onCreate() {
        super.onCreate();

        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        } else {
            Timber.plant(new ReleaseTree());
        }

        // ThreeTenBP for times and dates
        AndroidThreeTen.init(this);
    }

    @Override
    protected AndroidInjector<? extends DaggerApplication> applicationInjector() {
        return DaggerAppComponent.builder().create(this);
    }
}
