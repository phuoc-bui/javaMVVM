package com.phuocbui.mvvm.di;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.phuocbui.mvvm.A2MApplication;
import com.phuocbui.mvvm.data.PreferenceStorage;
import com.phuocbui.mvvm.data.SharedPreferenceStorage;
import com.phuocbui.mvvm.data.model.Event;
import com.phuocbui.mvvm.data.model.Geometry;
import com.phuocbui.mvvm.global.AppJsonDeserializer;

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
public
class AppModule {

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
        return new GsonBuilder()
                .setLenient()
                .registerTypeAdapter(Event.EventList.class, new AppJsonDeserializer.EventsDeserializer())
                .registerTypeAdapter(Geometry.class, new AppJsonDeserializer.GeometryDeserializer())
                .registerTypeAdapter(Geometry.class, new AppJsonDeserializer.GeometrySerializer())
                .create();
    }
}
