package com.redhelmet.alert2me.data;

import android.location.Location;
import android.support.v4.util.Pair;

import com.redhelmet.alert2me.data.model.ApiInfo;
import com.redhelmet.alert2me.data.model.AppConfig;
import com.redhelmet.alert2me.data.model.Category;
import com.redhelmet.alert2me.data.model.EventGroup;
import com.redhelmet.alert2me.data.remote.response.ConfigResponse;

import java.util.List;

public interface PreferenceStorage {
    void saveAppConfig(AppConfig appConfig);
    AppConfig getAppConfig();
    boolean isAccepted();
    void setAccepted(boolean accepted);
    void saveDeviceInfo(ApiInfo apiInfo);
    ApiInfo getDeviceInfo();
    void saveUserCustomFilters(List<Category> categories);
    void saveUserDefaultFilters(List<EventGroup> eventGroups);
    List<Long> getUserCustomFilters();
    List<Long> getUserDefaultFilters();
    void setDefaultFilter(boolean isDefault);
    boolean isDefaultFilter();
    Location getLastUserLocation();
    void saveCurrentUserLocation(Location location);
    boolean haveAccount();
    void setHaveAccount(boolean haveAccount);
    boolean isLoggedIn();
    void setLoggedIn(boolean isLoggedIn);
    void saveToken(String token);
    String getToken();
}
