package com.redhelmet.alert2me.data.local.pref;

import android.location.Location;
import android.support.v4.util.Pair;

import com.redhelmet.alert2me.data.model.ApiInfo;
import com.redhelmet.alert2me.data.model.AppConfig;
import com.redhelmet.alert2me.data.model.Category;
import com.redhelmet.alert2me.data.model.EventGroup;
import com.redhelmet.alert2me.data.remote.response.ConfigResponse;

import java.util.List;

public interface PreferenceHelper {
    void saveAppConfig(AppConfig appConfig);
    AppConfig getAppConfig();
    boolean isInitialLaunch();
    void setInitialLaunch(boolean isInitial);
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
}
