package com.redhelmet.alert2me.data.remote;

import com.redhelmet.alert2me.data.model.EditWatchZones;
import com.redhelmet.alert2me.data.model.User;
import com.redhelmet.alert2me.data.remote.request.ProximityLocationRequest;
import com.redhelmet.alert2me.data.remote.request.RegisterDeviceRequest;
import com.redhelmet.alert2me.data.remote.response.ConfigResponse;
import com.redhelmet.alert2me.data.remote.response.EventListResponse;
import com.redhelmet.alert2me.data.remote.response.ForgotPasswordResponse;
import com.redhelmet.alert2me.data.remote.response.LoginResponse;
import com.redhelmet.alert2me.data.remote.response.ProximityLocationResponse;
import com.redhelmet.alert2me.data.remote.response.RegisterAccountResponse;
import com.redhelmet.alert2me.data.remote.response.RegisterDeviceResponse;
import com.redhelmet.alert2me.data.remote.response.WatchZoneResponse;

import java.util.HashMap;

import io.reactivex.Observable;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface ApiService {
    @GET("config/android")
    Observable<ConfigResponse> getConfig();

    @POST("device")
    Observable<RegisterDeviceResponse> registerDevice(@Body RegisterDeviceRequest request);

    @PUT("apiInfo/{userId}/watchzones/proximity/location")
    Observable<ProximityLocationResponse> putLocation(@Path("userId") String userId, @Body ProximityLocationRequest request);

    @GET("events")
    Observable<EventListResponse> getAllEvents();

    @POST("account/register")
    Observable<RegisterAccountResponse> registerAccount(@Body User user);

    @POST("account/login")
    Observable<LoginResponse> login(@Body HashMap<String, String> loginRequest);

    @POST("account/forgot")
    Observable<ForgotPasswordResponse> forgotPassword(@Body HashMap<String, String> email);

    @PUT("account/user/{userId}")
    Observable<Object> updateProfile(@Path("userId") String userId);

    @GET("device/{userId}/watchzones")
    Observable<WatchZoneResponse> getWatchZones(@Path("userId") String userId);

    @POST("device/{userId}/watchzones")
    Observable<EditWatchZones> createWatchZone(@Path("userId") String userId, @Body EditWatchZones watchZones);

    @PUT("device/{userId}/watchzones/{watchZoneId}")
    Observable<Object> updateWatchZone(@Path("userId") String userId, @Path("watchZoneId") String watchZoneId, @Body EditWatchZones watchZones);

    @PUT("device/{userId}/watchzones/{watchZoneId}/{status}")
    Observable<Object> enableWatchZone(@Path("userId") String userId, @Path("watchZoneId") String watchZoneId, @Path("status") String status);

    @DELETE("device/{userId}/watchzones/{watchZoneId}")
    Observable<Object> deleteWatchZone(@Path("userId") String userId, @Path("watchZoneId") String watchZoneId);
}
