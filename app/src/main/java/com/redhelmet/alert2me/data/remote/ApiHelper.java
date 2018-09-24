package com.redhelmet.alert2me.data.remote;

import com.redhelmet.alert2me.data.remote.response.ConfigResponse;
import com.redhelmet.alert2me.data.remote.response.RegisterResponse;

import io.reactivex.Observable;

public interface ApiHelper {
    Observable<ConfigResponse> getConfig();
    Observable<RegisterResponse> registerDevice(String firebaseToken);
}
