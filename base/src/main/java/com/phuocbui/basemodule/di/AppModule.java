package com.phuocbui.basemodule.di;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.phuocbui.basemodule.MVVMApplication;
import com.phuocbui.basemodule.data.preference.PreferenceStorage;
import com.phuocbui.basemodule.data.preference.SharedPreferenceStorage;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Defines all the classes that need to be provided in the scope of the app.
 * <p>
 * Define here all objects that are shared throughout the app, like SharedPreferences, navigators or
 * others. If some of those objects are singletons, they should be annotated with `@Singleton`.
 */
@Module
public class AppModule {

    @Singleton
    @Provides
    public Context provideContext(MVVMApplication application) {
        return application.getApplicationContext();
    }


    @Singleton
    @Provides
    public PreferenceStorage providePreferenceStorage(Context context, Gson gson) {
        return new SharedPreferenceStorage(context, gson);
    }

    @Singleton
    @Provides
    public Gson provideGson() {
        return new GsonBuilder()
                .setLenient()
                .create();
    }
}
