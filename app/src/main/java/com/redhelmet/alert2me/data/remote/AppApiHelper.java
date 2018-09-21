package com.redhelmet.alert2me.data.remote;

import com.redhelmet.alert2me.data.model.response.ConfigResponse;

import io.reactivex.Observable;

public class AppApiHelper implements ApiHelper {
    private ApiService apiService;

    public AppApiHelper(ApiService apiService) {
        this.apiService = apiService;
    }

    @Override
    public Observable<ConfigResponse> getConfig() {
        return apiService.getConfig();
    }
}
