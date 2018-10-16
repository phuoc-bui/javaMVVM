package com.redhelmet.alert2me.di;

import android.content.Context;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.redhelmet.alert2me.A2MApplication;
import com.redhelmet.alert2me.data.PreferenceStorage;
import com.redhelmet.alert2me.data.SharedPreferenceStorage;

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
    public Context provideContext(A2MApplication application) {
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
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES);
//        gsonBuilder.setDateFormat(DATE_FORMAT);
        return gsonBuilder.create();
    }

}
