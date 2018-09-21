package com.redhelmet.alert2me.data.remote;

import com.redhelmet.alert2me.data.model.response.ConfigResponse;

import io.reactivex.Observable;
import retrofit2.http.GET;

public interface ApiService {
    @GET("appConfig/android")
    Observable<ConfigResponse> getConfig();
}
