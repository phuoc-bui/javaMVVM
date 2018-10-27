package com.redhelmet.alert2me.di;

import android.app.ActivityManager;
import android.content.Context;
import androidx.annotation.FloatRange;

import com.redhelmet.alert2me.BuildConfig;
import com.redhelmet.alert2me.data.PreferenceStorage;
import com.redhelmet.alert2me.data.model.ApiInfo;
import com.redhelmet.alert2me.data.remote.ApiHelper;
import com.redhelmet.alert2me.data.remote.AppApiHelper;
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

@Module
public class NetworkModule {

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
    public ApiHelper provideApiHelper(Retrofit retrofit) {
        return new AppApiHelper(retrofit);
    }

    private long calcCacheSize(Context context, @FloatRange(from = 0.01, to = 1.0) float size) {
        ActivityManager am = (ActivityManager) context.getSystemService(ACTIVITY_SERVICE);
        boolean largeHeap = (context.getApplicationInfo().flags | FLAG_LARGE_HEAP) != 0;
        long memoryClass = largeHeap ? am.getLargeMemoryClass() : am.getMemoryClass();
        return (long) (memoryClass * 1024L * 1024L * size);
    }
}
