package com.redhelmet.alert2me.data.remote;

import com.redhelmet.alert2me.data.remote.request.RegisterDeviceRequest;
import com.redhelmet.alert2me.data.remote.response.ConfigResponse;
import com.redhelmet.alert2me.data.remote.response.RegisterResponse;

import io.reactivex.Observable;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface ApiService {
    @GET("appConfig/android")
    Observable<ConfigResponse> getConfig();

    @POST("device")
    Observable<RegisterResponse> registerDevice(@Body RegisterDeviceRequest request);
}
