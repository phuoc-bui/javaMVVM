package com.redhelmet.alert2me.data.remote;

import com.redhelmet.alert2me.data.remote.request.ProximityLocationRequest;
import com.redhelmet.alert2me.data.remote.request.RegisterDeviceRequest;
import com.redhelmet.alert2me.data.remote.response.ConfigResponse;
import com.redhelmet.alert2me.data.remote.response.EventListResponse;
import com.redhelmet.alert2me.data.remote.response.ProximityLocationResponse;
import com.redhelmet.alert2me.data.remote.response.RegisterResponse;

import io.reactivex.Observable;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface ApiService {
    @GET("appConfig/android")
    Observable<ConfigResponse> getConfig();

    @POST("apiInfo")
    Observable<RegisterResponse> registerDevice(@Body RegisterDeviceRequest request);

    @PUT("apiInfo/{userId}/watchzones/proximity/location")
    Observable<ProximityLocationResponse> putLocation(@Path("userId") String userId, @Body ProximityLocationRequest request);

    @GET("events")
    Observable<EventListResponse> getAllEvents();
}
