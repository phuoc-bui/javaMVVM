package com.redhelmet.alert2me.data;

import android.location.Location;

import com.redhelmet.alert2me.data.model.ApiInfo;
import com.redhelmet.alert2me.data.model.AppConfig;
import com.redhelmet.alert2me.data.model.Category;
import com.redhelmet.alert2me.data.model.EditWatchZones;
import com.redhelmet.alert2me.data.model.Event;
import com.redhelmet.alert2me.data.model.EventGroup;
import com.redhelmet.alert2me.data.model.Hint;
import com.redhelmet.alert2me.data.model.User;
import com.redhelmet.alert2me.data.remote.response.ConfigResponse;
import com.redhelmet.alert2me.data.remote.response.ForgotPasswordResponse;
import com.redhelmet.alert2me.data.remote.response.LoginResponse;
import com.redhelmet.alert2me.data.remote.response.ProximityLocationResponse;
import com.redhelmet.alert2me.data.remote.response.RegisterAccountResponse;

import java.util.Comparator;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Single;

public interface DataManager {
    void saveConfig(ConfigResponse config);
    Observable<ConfigResponse> loadConfig();
    AppConfig getAppConfig();
    void setAccepted(boolean accepted);
    boolean getAccepted();
    Location getLastUserLocation();
    void saveUserLocation(Location location);
    Observable<ApiInfo> getUserId(String firebaseToken);
    Observable<ProximityLocationResponse> putProximityLocation(double lat, double lng);
    Observable<List<Event>> getAllEvents();
    Observable<List<Category>> getCategories();
    Single<Category> getEventCategory(Event event);
    List<Category> getCategoriesSync();
    Observable<List<EventGroup>> getEventGroups();
    List<EventGroup> getEventGroupsSync();
    Observable<List<Category>> getUserCustomFilters();
    Observable<List<EventGroup>> getUserDefaultFilters();
    void saveUserCustomFilters(List<Category> categories);
    void saveUserDefaultFilters(List<EventGroup> eventGroups);
    Observable<List<Event>> getEventsWithFilter(boolean isDefault, Comparator<Event> sort);
    Observable<Event> getEventsWithFilterOneByOne(boolean isDefault, Comparator<Event> sort);
    boolean isDefaultFilter();
    void setDefaultFilter(boolean isDefault);
    Observable<RegisterAccountResponse> registerAccount(User user);
    Observable<User> login(String email, String password);
    Observable<ForgotPasswordResponse> forgotPassword(String email);
    Observable<User> updateUserProfile(User user);
    Observable<List<EditWatchZones>> getWatchZones();
    void saveWatchZones(List<EditWatchZones> watchZones);
}
