package com.phuocbui.mvvm.data.remote;

import com.phuocbui.mvvm.data.model.DeviceInfo;
import com.phuocbui.mvvm.data.model.EditWatchZones;
import com.phuocbui.mvvm.data.model.Event;
import com.phuocbui.mvvm.data.model.User;
import com.phuocbui.mvvm.data.remote.request.ProximityLocationRequest;
import com.phuocbui.mvvm.data.remote.request.RegisterDeviceRequest;
import com.phuocbui.mvvm.data.remote.response.ConfigResponse;
import com.phuocbui.mvvm.data.remote.response.ForgotPasswordResponse;
import com.phuocbui.mvvm.data.remote.response.ProximityLocationResponse;
import com.phuocbui.mvvm.data.remote.response.RegisterAccountResponse;
import com.phuocbui.mvvm.data.remote.response.RegisterDeviceResponse;
import com.phuocbui.mvvm.data.remote.response.Response;
import com.phuocbui.mvvm.data.remote.response.WatchZoneResponse;
import com.phuocbui.basemodule.global.RetrofitException;

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
        return apiService.updateProfile(String.valueOf(user.getId()), user)
                .map(response -> response.account);
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
