package com.redhelmet.alert2me.data.remote;

import com.redhelmet.alert2me.data.model.ApiInfo;
import com.redhelmet.alert2me.data.model.Event;
import com.redhelmet.alert2me.data.remote.request.ProximityLocationRequest;
import com.redhelmet.alert2me.data.remote.request.RegisterDeviceRequest;
import com.redhelmet.alert2me.data.remote.response.ConfigResponse;
import com.redhelmet.alert2me.data.remote.response.ProximityLocationResponse;
import com.redhelmet.alert2me.data.remote.response.Response;

import java.util.List;

import io.reactivex.Notification;
import io.reactivex.Observable;

public class AppApiHelper implements ApiHelper {
    private ApiService apiService;

    public AppApiHelper(ApiService apiService) {
        this.apiService = apiService;
    }

    @Override
    public Observable<ConfigResponse> getConfig() {
        return filterSuccessResponse(apiService.getConfig());
    }

    @Override
    public Observable<ApiInfo> registerDevice(String firebaseToken) {
        RegisterDeviceRequest request = new RegisterDeviceRequest(firebaseToken);
        return filterSuccessResponse(apiService.registerDevice(request)).map(response -> response.apiInfo);
    }

    @Override
    public Observable<ProximityLocationResponse> putProximityLocation(String userId, ProximityLocationRequest request) {
        return filterSuccessResponse(apiService.putLocation(userId, request));
    }

    @Override
    public Observable<List<Event>> getAllEvents() {
        return filterSuccessResponse(apiService.getAllEvents()).map(response -> response.events);
    }

    private <T extends Response> Observable<T> filterSuccessResponse(Observable<T> response) {
        return response.materialize().map(notification -> {
            if(notification.isOnNext() && !notification.getValue().success) {
                return Notification.createOnError(new Throwable(notification.getValue().errorMessage));
            } else {
                return notification;
            }
        }).dematerialize();
    }
}
