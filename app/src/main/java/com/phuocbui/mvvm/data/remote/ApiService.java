package com.phuocbui.mvvm.data.remote;

import com.phuocbui.mvvm.data.model.EditWatchZones;
import com.phuocbui.mvvm.data.model.User;
import com.phuocbui.mvvm.data.remote.request.ProximityLocationRequest;
import com.phuocbui.mvvm.data.remote.request.RegisterDeviceRequest;
import com.phuocbui.mvvm.data.remote.response.ConfigResponse;
import com.phuocbui.mvvm.data.remote.response.EventListResponse;
import com.phuocbui.mvvm.data.remote.response.ForgotPasswordResponse;
import com.phuocbui.mvvm.data.remote.response.LoginResponse;
import com.phuocbui.mvvm.data.remote.response.ProximityLocationResponse;
import com.phuocbui.mvvm.data.remote.response.RegisterAccountResponse;
import com.phuocbui.mvvm.data.remote.response.RegisterDeviceResponse;
import com.phuocbui.mvvm.data.remote.response.WatchZoneResponse;

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

    @PUT("account/users/{userId}")
    Observable<LoginResponse> updateProfile(@Path("userId") String userId, @Body User user);

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
