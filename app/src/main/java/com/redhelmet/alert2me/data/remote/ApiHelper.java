package com.redhelmet.alert2me.data.remote;

import com.redhelmet.alert2me.data.model.ApiInfo;
import com.redhelmet.alert2me.data.model.Event;
import com.redhelmet.alert2me.data.remote.request.ProximityLocationRequest;
import com.redhelmet.alert2me.data.remote.response.ConfigResponse;
import com.redhelmet.alert2me.data.remote.response.ProximityLocationResponse;

import java.util.List;

import io.reactivex.Observable;

public interface ApiHelper {
    Observable<ConfigResponse> getConfig();

    Observable<ApiInfo> registerDevice(String firebaseToken);

    Observable<ProximityLocationResponse> putProximityLocation(String userId, ProximityLocationRequest request);

    Observable<List<Event>> getAllEvents();
}
