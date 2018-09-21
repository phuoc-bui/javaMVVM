package com.redhelmet.alert2me.data;

import com.redhelmet.alert2me.data.model.response.ConfigResponse;

import io.reactivex.Observable;

public interface DataManager {
    void saveConfig(ConfigResponse config);
    Observable<ConfigResponse> loadConfig();
    ConfigResponse getConfig();
}
