package com.redhelmet.alert2me;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.Context;
import android.support.annotation.FloatRange;
import android.support.annotation.NonNull;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.redhelmet.alert2me.data.AppDataManager;
import com.redhelmet.alert2me.data.DataManager;
import com.redhelmet.alert2me.data.local.database.AppDBHelper;
import com.redhelmet.alert2me.data.local.database.DBController;
import com.redhelmet.alert2me.data.local.database.DBHelper;
import com.redhelmet.alert2me.data.local.pref.AppPreferenceHelper;
import com.redhelmet.alert2me.data.local.pref.PreferenceHelper;
import com.redhelmet.alert2me.data.remote.ApiHelper;
import com.redhelmet.alert2me.data.remote.ApiService;
import com.redhelmet.alert2me.data.remote.AppApiHelper;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

import static android.content.Context.ACTIVITY_SERVICE;
import static android.content.pm.ApplicationInfo.FLAG_LARGE_HEAP;

/**
 * This class provide app service for Application, so please init it in Application
 */
@SuppressWarnings({"unused", "WeakerAccess"})
public class AppModule {

    @SuppressLint("StaticFieldLeak")
    private static AppModule INSTANCE;

    private Context application;

//    private String DATE_FORMAT = "yyyy-MM-dd";

    public static AppModule getInstance() {
        if (INSTANCE != null) {
            return INSTANCE;
        }

        throw new Error("AppModel isn't init!");
    }

    public static void init(Context application) {
        INSTANCE = new AppModule(application);
    }

    private AppModule(Context application) {
        this.application = application;
    }

    public Context provideApplication() {
        return application;
    }

    public Gson provideGson() {
        Gson instance = ServiceLocator.get(Gson.class);
        if (instance == null) {
            GsonBuilder gsonBuilder = new GsonBuilder();
            gsonBuilder.setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES);
//        gsonBuilder.setDateFormat(DATE_FORMAT);
            instance = gsonBuilder.create();
            ServiceLocator.addService(instance);
        }
        return instance;
    }

    public Retrofit provideRetrofit() {
        Retrofit instance = ServiceLocator.get(Retrofit.class);
        if (instance == null) {
            instance = new Retrofit.Builder()
                    .baseUrl(BuildConfig.API_ENDPOINT)
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .client(buildOkHttpClient())
                    .build();
            ServiceLocator.addService(instance);
        }
        return instance;
    }

    public ApiService provideApiService() {
        ApiService instance = ServiceLocator.get(ApiService.class);
        if (instance == null) {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(BuildConfig.API_ENDPOINT)
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .client(buildOkHttpClient())
                    .build();
            instance = retrofit.create(ApiService.class);
            ServiceLocator.addService(instance);
        }
        return instance;
    }

    public PreferenceHelper providePreferenceHelper() {
        PreferenceHelper instance = ServiceLocator.get(PreferenceHelper.class);
        if (instance == null) {
            instance = new AppPreferenceHelper(provideApplication(), provideGson());
            ServiceLocator.addService(instance);
        }
        return instance;
    }

    public DBHelper provideDBHelper() {
        DBHelper instance = ServiceLocator.get(DBHelper.class);
        if (instance == null) {
            instance = new AppDBHelper(DBController.getInstance(provideApplication()));
            ServiceLocator.addService(instance);
        }
        return instance;
    }



    public ApiHelper provideApiHelper() {
        ApiHelper instance = ServiceLocator.get(ApiHelper.class);
        if (instance == null) {
            instance = new AppApiHelper(provideApiService());
            ServiceLocator.addService(instance);
        }
        return instance;
    }

    public DataManager provideDataManager() {
        DataManager instance = ServiceLocator.get(DataManager.class);
        if (instance == null) {
            instance = new AppDataManager(providePreferenceHelper(), provideDBHelper(), provideApiHelper());
            ServiceLocator.addService(instance);
        }
        return instance;
    }

    private long calcCacheSize(@FloatRange(from = 0.01, to = 1.0) float size) {
        ActivityManager am = (ActivityManager) application.getSystemService(ACTIVITY_SERVICE);
        boolean largeHeap = (application.getApplicationInfo().flags | FLAG_LARGE_HEAP) != 0;
        long memoryClass = largeHeap ? am.getLargeMemoryClass() : am.getMemoryClass();
        return (long) (memoryClass * 1024L * 1024L * size);
    }

    private OkHttpClient buildOkHttpClient() {
        return new OkHttpClient.Builder()
                .connectTimeout(10L, TimeUnit.SECONDS)
                .writeTimeout(10L, TimeUnit.SECONDS)
                .readTimeout(30L, TimeUnit.SECONDS)
                .cache(new Cache(new File(application.getCacheDir(), "OkCache"),
                        calcCacheSize(.25f)))
                .build();
    }

    private static class ServiceLocator {

        private static final Map<String, Object> servicesInstances = new HashMap<>();

        private static final Object sServicesInstancesLock = new Object();

        @SuppressWarnings({"unchecked"})
        public static <T> T get(@NonNull Class<T> clazz) {
            return (T) servicesInstances.get(clazz.getName());
        }

        public static <T> void addService(T service) {
            synchronized (sServicesInstancesLock) {
                servicesInstances.put(service.getClass().getName(), service);
            }
        }
    }
}
