package com.phuocbui.mvvm.data.remote;

import com.phuocbui.mvvm.data.model.DeviceInfo;
import com.phuocbui.mvvm.data.model.EditWatchZones;
import com.phuocbui.mvvm.data.model.Event;
import com.phuocbui.mvvm.data.model.User;
import com.phuocbui.mvvm.data.remote.request.ProximityLocationRequest;
import com.phuocbui.mvvm.data.remote.response.ConfigResponse;
import com.phuocbui.mvvm.data.remote.response.ForgotPasswordResponse;
import com.phuocbui.mvvm.data.remote.response.ProximityLocationResponse;
import com.phuocbui.mvvm.data.remote.response.RegisterAccountResponse;
import com.phuocbui.mvvm.data.remote.response.WatchZoneResponse;

import java.util.List;

import io.reactivex.Observable;

public interface ApiHelper {
    Observable<ConfigResponse> getConfig();

    Observable<DeviceInfo> registerDevice(String firebaseToken);

    Observable<ProximityLocationResponse> putProximityLocation(String userId, ProximityLocationRequest request);

    Observable<List<Event>> getAllEvents();

    Observable<RegisterAccountResponse> registerAccount(String deviceId, User user);

    Observable<User> login(String deviceId, String email, String password);

    Observable<ForgotPasswordResponse> forgotPassword(String deviceId, String email);

    Observable<User> updateUserProfile(User user);

    Observable<WatchZoneResponse> getWatchZones(String userId);

    Observable<EditWatchZones> createWatchZone(String userId, EditWatchZones watchZones);

    Observable<Object> editWatchZone(String userId, long watchZoneId, EditWatchZones watchZones);

    Observable<Object> enableWatchZone(String userId, long watchZoneId, boolean enable);

    Observable<Object> deleteWatchZone(String userId, long watchZoneId);
}
