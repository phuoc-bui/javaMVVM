package com.redhelmet.alert2me.data;

import com.redhelmet.alert2me.data.model.ApiInfo;
import com.redhelmet.alert2me.data.model.Category;
import com.redhelmet.alert2me.data.model.Event;
import com.redhelmet.alert2me.data.model.EventGroup;
import com.redhelmet.alert2me.data.model.Hint;
import com.redhelmet.alert2me.data.remote.response.ConfigResponse;
import com.redhelmet.alert2me.data.remote.response.ProximityLocationResponse;
import com.redhelmet.alert2me.data.remote.response.RegisterResponse;

import java.util.List;

import io.reactivex.Observable;

public interface DataManager {
    void saveConfig(ConfigResponse config);
    Observable<ConfigResponse> loadConfig();
    List<Hint> getHintData();
    void setInitialLaunch(boolean isInitial);
    boolean getInitialLaunch();
    void setAccepted(boolean accepted);
    boolean getAccepted();
    Observable<ApiInfo> getUserId(String firebaseToken);
    Observable<ProximityLocationResponse> putProximityLocation(double lat, double lng);
    Observable<List<Event>> getAllEvents();
    Observable<List<Category>> getCategories();
    Observable<List<EventGroup>> getEventGroups();
    Observable<List<Category>> getUserCustomFilters();
    Observable<List<EventGroup>> getUserDefaultFilters();
    void saveUserCustomFilters(List<Category> categories);
    void saveUserDefaultFilters(List<EventGroup> eventGroups);
    Observable<List<Event>> getEventsWithFilter(boolean isDefault);
    boolean isDefaultFilter();
    void setDefaultFilter(boolean isDefault);
}
