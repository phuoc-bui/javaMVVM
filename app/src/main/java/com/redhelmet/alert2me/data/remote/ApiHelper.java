package com.redhelmet.alert2me.data.remote;

import com.redhelmet.alert2me.data.remote.request.ProximityLocationRequest;
import com.redhelmet.alert2me.data.remote.response.ConfigResponse;
import com.redhelmet.alert2me.data.remote.response.ProximityLocationResponse;
import com.redhelmet.alert2me.data.remote.response.RegisterResponse;

import io.reactivex.Observable;

public interface ApiHelper {
    Observable<ConfigResponse> getConfig();
    Observable<RegisterResponse> registerDevice(String firebaseToken);
    Observable<ProximityLocationResponse> putProximityLocation(String userId, ProximityLocationRequest request);
}
