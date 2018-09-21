package com.redhelmet.alert2me.data;

import android.util.Log;

import com.redhelmet.alert2me.data.local.database.DBHelper;
import com.redhelmet.alert2me.data.local.pref.PreferenceHelper;
import com.redhelmet.alert2me.data.model.response.ConfigResponse;
import com.redhelmet.alert2me.data.remote.ApiHelper;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;

public class AppDataManager implements DataManager {
    private final String TAG = AppDataManager.class.getSimpleName();
    private PreferenceHelper pref;
    private ApiHelper api;
    private DBHelper database;
    private ConfigResponse config;

    public AppDataManager(PreferenceHelper pref, DBHelper db, ApiHelper apiHelper) {
        this.pref = pref;
        this.database = db;
        this.api = apiHelper;
    }

    @Override
    public void saveConfig(ConfigResponse config) {
        this.config = config;
        pref.saveConfig(config);
        database.saveConfig(config);
    }

    @Override
    public Observable<ConfigResponse> loadConfig() {
        return Observable.concatArrayEager(
                Observable.just(pref.getConfig()),
                api.getConfig()
                        .doOnNext(this::saveConfig)
                        .doOnError(this::handleError))
                .debounce(400L, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io());
    }

    @Override
    public ConfigResponse getConfig() {
        return config;
    }

    private void handleError(Throwable error) {
        Log.e(TAG, error.getMessage());
    }
}
