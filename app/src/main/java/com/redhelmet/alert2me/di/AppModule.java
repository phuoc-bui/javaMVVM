package com.redhelmet.alert2me.di;

import android.app.ActivityManager;
import android.arch.persistence.room.Room;
import android.content.Context;
import android.support.annotation.FloatRange;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.redhelmet.alert2me.A2MApplication;
import com.redhelmet.alert2me.BuildConfig;
import com.redhelmet.alert2me.data.PreferenceStorage;
import com.redhelmet.alert2me.data.SharedPreferenceStorage;
import com.redhelmet.alert2me.data.database.DatabaseStorage;
import com.redhelmet.alert2me.data.database.RoomDatabase;
import com.redhelmet.alert2me.data.database.RoomDatabaseStorage;
import com.redhelmet.alert2me.data.model.ApiInfo;
import com.redhelmet.alert2me.global.RxErrorHandlingCallAdapterFactory;

import java.io.File;
import java.util.concurrent.TimeUnit;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.Cache;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.content.Context.ACTIVITY_SERVICE;
import static android.content.pm.ApplicationInfo.FLAG_LARGE_HEAP;

/**
 * Defines all the classes that need to be provided in the scope of the app.
 * <p>
 * Define here all objects that are shared throughout the app, like SharedPreferences, navigators or
 * others. If some of those objects are singletons, they should be annotated with `@Singleton`.
 */
@Module
public class AppModule {

    @Provides
    public Context provideContext(A2MApplication application) {
        return application.getApplicationContext();
    }

    @Singleton
    @Provides
    public PreferenceStorage providesPreferenceStorage(Context context, Gson gson) {
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

    @Singleton
    @Provides
    public Interceptor provideInterceptor(PreferenceStorage pref) {
        return chain -> {
            Request originalRequest = chain.request();
            Request.Builder builder = originalRequest.newBuilder();
            builder.header("Content-Type", "application/json");
            ApiInfo info = pref.getDeviceInfo();
            if (info != null && info.getApiToken() != null && !info.getApiToken().isEmpty()) {
                builder.addHeader("Authorization", "Bearer " + info.getApiToken());
            }
            Request newRequest = builder.build();
            return chain.proceed(newRequest);
        };
    }

    @Singleton
    @Provides
    public Retrofit provideRetrofit(OkHttpClient client) {
        return new Retrofit.Builder()
                .baseUrl(BuildConfig.API_ENDPOINT)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxErrorHandlingCallAdapterFactory.create())
                .client(client)
                .build();
    }

    @Singleton
    @Provides
    public PreferenceStorage providePreferenceStorage(Context context, Gson gson) {
        return new SharedPreferenceStorage(context, gson);
    }

    @Singleton
    @Provides
    public DatabaseStorage provideStorage(RoomDatabase database) {
        return new RoomDatabaseStorage(database);
    }

    @Singleton
    @Provides
    public RoomDatabase provideRoomDatabase(Context context) {
        return Room.databaseBuilder(context, RoomDatabase.class, BuildConfig.DB_FILE_NAME + "room")
                .build();
    }

    @Singleton
    @Provides
    public OkHttpClient provideOkHttpClient(Context context, Interceptor interceptor) {
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        return new OkHttpClient.Builder()
                .addInterceptor(interceptor)
                .addInterceptor(loggingInterceptor)
                .connectTimeout(10L, TimeUnit.SECONDS)
                .writeTimeout(10L, TimeUnit.SECONDS)
                .readTimeout(30L, TimeUnit.SECONDS)
                .cache(new Cache(new File(context.getCacheDir(), "OkCache"),
                        calcCacheSize(context, .25f)))
                .build();
    }

    private long calcCacheSize(Context context, @FloatRange(from = 0.01, to = 1.0) float size) {
        ActivityManager am = (ActivityManager) context.getSystemService(ACTIVITY_SERVICE);
        boolean largeHeap = (context.getApplicationInfo().flags | FLAG_LARGE_HEAP) != 0;
        long memoryClass = largeHeap ? am.getLargeMemoryClass() : am.getMemoryClass();
        return (long) (memoryClass * 1024L * 1024L * size);
    }
}
