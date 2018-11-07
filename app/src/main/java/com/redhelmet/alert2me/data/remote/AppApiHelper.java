package com.redhelmet.alert2me.data.remote;

import com.redhelmet.alert2me.data.model.DeviceInfo;
import com.redhelmet.alert2me.data.model.EditWatchZones;
import com.redhelmet.alert2me.data.model.Event;
import com.redhelmet.alert2me.data.model.User;
import com.redhelmet.alert2me.data.remote.request.ProximityLocationRequest;
import com.redhelmet.alert2me.data.remote.request.RegisterDeviceRequest;
import com.redhelmet.alert2me.data.remote.response.ConfigResponse;
import com.redhelmet.alert2me.data.remote.response.ForgotPasswordResponse;
import com.redhelmet.alert2me.data.remote.response.ProximityLocationResponse;
import com.redhelmet.alert2me.data.remote.response.RegisterAccountResponse;
import com.redhelmet.alert2me.data.remote.response.RegisterDeviceResponse;
import com.redhelmet.alert2me.data.remote.response.Response;
import com.redhelmet.alert2me.data.remote.response.WatchZoneResponse;
import com.redhelmet.alert2me.global.RetrofitException;

import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.Notification;
import io.reactivex.Observable;
import retrofit2.Retrofit;

public class AppApiHelper implements ApiHelper {
    private ApiService apiService;

    @Inject
    public AppApiHelper(Retrofit retrofit) {
        this.apiService = retrofit.create(ApiService.class);
    }

    @Override
    public Observable<ConfigResponse> getConfig() {
        return filterSuccessResponse(apiService.getConfig());
    }

    @Override
    public Observable<DeviceInfo> registerDevice(String firebaseToken) {
        RegisterDeviceRequest request = new RegisterDeviceRequest(firebaseToken);
        return apiService.registerDevice(request)
                .onErrorResumeNext(throwable -> {
                    if (throwable instanceof RetrofitException) {
                        try {
                            RegisterDeviceResponse deviceInfo = ((RetrofitException) throwable).getErrorBodyAs(RegisterDeviceResponse.class);
                            return Observable.just(deviceInfo);
                        } catch (Exception exception) {
                            return Observable.error(throwable);
                        }
                    } else return Observable.error(throwable);
                }).map(response -> response.apiInfo);
    }

    @Override
    public Observable<ProximityLocationResponse> putProximityLocation(String userId, ProximityLocationRequest request) {
        return filterSuccessResponse(apiService.putLocation(userId, request));
    }

    @Override
    public Observable<List<Event>> getAllEvents() {
        return filterSuccessResponse(apiService.getAllEvents()).map(response -> response.events);
    }

    @Override
    public Observable<RegisterAccountResponse> registerAccount(String deviceId, User user) {
        return filterSuccessResponse(apiService.registerAccount(user));
    }

    @Override
    public Observable<User> login(String deviceId, String email, String password) {
        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put("email", email);
        requestMap.put("password", password);
        requestMap.put("deviceId", deviceId);
        return filterSuccessResponse(apiService.login(requestMap)).map(response -> response.account);
    }

    @Override
    public Observable<ForgotPasswordResponse> forgotPassword(String deviceId, String email) {
        HashMap<String, String> request = new HashMap<>();
        request.put("email", email);
        request.put("deviceId", deviceId);
        return filterSuccessResponse(apiService.forgotPassword(request));
    }

    @Override
    public Observable<User> updateUserProfile(User user) {
        // TODO: call API to update profile
        return Observable.just(user);
    }

    private <T extends Response> Observable<T> filterSuccessResponse(Observable<T> response) {
        return response.materialize().map(notification -> {
            if (notification.isOnNext() && !notification.getValue().success) {
                return Notification.createOnError(new Throwable(notification.getValue().errorMessage));
            } else return notification;
        }).dematerialize();
    }

    @Override
    public Observable<WatchZoneResponse> getWatchZones(String userId) {
        return filterSuccessResponse(apiService.getWatchZones(userId));
    }

    @Override
    public Observable<EditWatchZones> createWatchZone(String userId, EditWatchZones watchZones) {
        return apiService.createWatchZone(userId, watchZones);
    }

    @Override
    public Observable<Object> editWatchZone(String userId, long watchZoneId, EditWatchZones watchZones) {
        return apiService.updateWatchZone(userId, String.valueOf(watchZoneId), watchZones);
    }

    @Override
    public Observable<Object> enableWatchZone(String userId, long watchZoneId, boolean enable) {
        return apiService.enableWatchZone(userId, String.valueOf(watchZoneId), enable ? "enable" : "disable");
    }

    @Override
    public Observable<Object> deleteWatchZone(String userId, long watchZoneId) {
        return apiService.deleteWatchZone(userId, String.valueOf(watchZoneId));
    }
}
