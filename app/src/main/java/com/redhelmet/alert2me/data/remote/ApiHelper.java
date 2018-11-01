package com.redhelmet.alert2me.data.remote;

import com.redhelmet.alert2me.data.model.DeviceInfo;
import com.redhelmet.alert2me.data.model.EditWatchZones;
import com.redhelmet.alert2me.data.model.Event;
import com.redhelmet.alert2me.data.model.User;
import com.redhelmet.alert2me.data.remote.request.ProximityLocationRequest;
import com.redhelmet.alert2me.data.remote.response.ConfigResponse;
import com.redhelmet.alert2me.data.remote.response.ForgotPasswordResponse;
import com.redhelmet.alert2me.data.remote.response.ProximityLocationResponse;
import com.redhelmet.alert2me.data.remote.response.RegisterAccountResponse;
import com.redhelmet.alert2me.data.remote.response.WatchZoneResponse;

import java.util.List;

import io.reactivex.Observable;

public interface ApiHelper {
    Observable<ConfigResponse> getConfig();

    Observable<DeviceInfo> registerDevice(String firebaseToken);

    Observable<ProximityLocationResponse> putProximityLocation(String userId, ProximityLocationRequest request);

    Observable<List<Event>> getAllEvents();

    Observable<RegisterAccountResponse> registerAccount(User user);

    Observable<User> login(String email, String password);

    Observable<ForgotPasswordResponse> forgotPassword(String email);

    Observable<User> updateUserProfile(User user);

    Observable<WatchZoneResponse> getWatchZones(String userId);

    Observable<EditWatchZones> createWatchZone(String userId, EditWatchZones watchZones);
}
