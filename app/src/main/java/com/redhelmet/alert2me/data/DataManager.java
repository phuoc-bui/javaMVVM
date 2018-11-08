package com.redhelmet.alert2me.data;

import android.location.Location;

import com.redhelmet.alert2me.data.model.DeviceInfo;
import com.redhelmet.alert2me.data.model.AppConfig;
import com.redhelmet.alert2me.data.model.Category;
import com.redhelmet.alert2me.data.model.EditWatchZones;
import com.redhelmet.alert2me.data.model.Event;
import com.redhelmet.alert2me.data.model.EventGroup;
import com.redhelmet.alert2me.data.model.User;
import com.redhelmet.alert2me.data.remote.response.ConfigResponse;
import com.redhelmet.alert2me.data.remote.response.ForgotPasswordResponse;
import com.redhelmet.alert2me.data.remote.response.ProximityLocationResponse;
import com.redhelmet.alert2me.data.remote.response.RegisterAccountResponse;

import java.util.Comparator;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Single;

public interface DataManager {
    void saveConfig(ConfigResponse config);
    Observable<ConfigResponse> loadConfig();
    void setAccepted(boolean accepted);
    Location getLastUserLocation();
    void saveUserLocation(Location location);
    Observable<DeviceInfo> registerDeviceToken(String firebaseToken);
    Observable<ProximityLocationResponse> putProximityLocation(double lat, double lng);
    Observable<List<Event>> getAllEvents();
    Observable<List<Category>> getCategories();
    Single<Category> getEventCategory(Event event);
    Observable<List<EventGroup>> getEventGroups();
    Observable<List<Category>> getUserCustomFilters();
    Observable<List<EventGroup>> getFilterOnDefaultFilters();
    void saveUserCustomFilters(List<Category> categories);
    void saveUserDefaultFilters(List<EventGroup> eventGroups);
    Observable<List<Event>> getEventsWithFilter(boolean isDefault, Comparator<Event> sort);
    Observable<List<Event>> getEventsWithFilter(boolean isDefault);
    Observable<Event> getEventsWithFilterOneByOne(boolean isDefault, Comparator<Event> sort);
    Observable<Event> getEventsWithFilterOneByOne(boolean isDefault);
    boolean isDefaultFilter();
    void setDefaultFilter(boolean isDefault);
    Observable<RegisterAccountResponse> registerAccount(User user);
    Observable<User> login(String email, String password);
    Observable<ForgotPasswordResponse> forgotPassword(String email);
    Observable<User> updateUserProfile(User user);
    Observable<List<EditWatchZones>> getWatchZones();
    void saveWatchZones(List<EditWatchZones> watchZones);
    Observable<EditWatchZones> addWatchZone(EditWatchZones watchZone);
    Observable<Object> editWatchZone(EditWatchZones watchZone);
    Observable<Object> enableWatchZone(long watchZoneId, boolean enabled);
    Observable<Object> deleteWatchZone(long watchZoneId);
}
