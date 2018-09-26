package com.redhelmet.alert2me.data.remote;

import com.redhelmet.alert2me.data.remote.request.ProximityLocationRequest;
import com.redhelmet.alert2me.data.remote.request.RegisterDeviceRequest;
import com.redhelmet.alert2me.data.remote.response.ConfigResponse;
import com.redhelmet.alert2me.data.remote.response.ProximityLocationResponse;
import com.redhelmet.alert2me.data.remote.response.RegisterResponse;

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

    @Override
    public Observable<RegisterResponse> registerDevice(String firebaseToken) {
        RegisterDeviceRequest request = new RegisterDeviceRequest(firebaseToken);
        return apiService.registerDevice(request);
    }

    @Override
    public Observable<ProximityLocationResponse> putProximityLocation(String userId, ProximityLocationRequest request) {
        return apiService.putLocation(userId, request);
    }


}
