package com.redhelmet.alert2me.data;

import android.location.Location;

import com.redhelmet.alert2me.data.model.ApiInfo;
import com.redhelmet.alert2me.data.model.AppConfig;
import com.redhelmet.alert2me.data.model.Category;
import com.redhelmet.alert2me.data.model.Event;
import com.redhelmet.alert2me.data.model.EventGroup;
import com.redhelmet.alert2me.data.model.Hint;
import com.redhelmet.alert2me.data.model.User;
import com.redhelmet.alert2me.data.remote.response.ConfigResponse;
import com.redhelmet.alert2me.data.remote.response.ForgotPasswordResponse;
import com.redhelmet.alert2me.data.remote.response.LoginResponse;
import com.redhelmet.alert2me.data.remote.response.ProximityLocationResponse;
import com.redhelmet.alert2me.data.remote.response.RegisterAccountResponse;

import java.util.List;

import io.reactivex.Observable;

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
    List<Category> getCategoriesSync();
    Observable<List<EventGroup>> getEventGroups();
    List<EventGroup> getEventGroupsSync();
    Observable<List<Category>> getUserCustomFilters();
    Observable<List<EventGroup>> getUserDefaultFilters();
    void saveUserCustomFilters(List<Category> categories);
    void saveUserDefaultFilters(List<EventGroup> eventGroups);
    Observable<List<Event>> getEventsWithFilter(boolean isDefault);
    Observable<Event> getEventsWithFilterOneByOne(boolean isDefault);
    boolean isDefaultFilter();
    void setDefaultFilter(boolean isDefault);
    Observable<RegisterAccountResponse> registerAccount(User user);
    Observable<LoginResponse> login(String email, String password);
    Observable<ForgotPasswordResponse> forgotPassword(String email);
}
