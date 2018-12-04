package com.phuocbui.basemodule.data.remote;

import javax.inject.Inject;

import retrofit2.Retrofit;

public class AppRemoteStorage implements RemoteStorage {
    private ApiService apiService;

    @Inject
    public AppRemoteStorage(Retrofit retrofit) {
        this.apiService = retrofit.create(ApiService.class);
    }
}
