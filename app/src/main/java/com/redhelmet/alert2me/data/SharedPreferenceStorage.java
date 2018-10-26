package com.redhelmet.alert2me.data;

import android.content.Context;
import android.location.Location;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.redhelmet.alert2me.R;
import com.redhelmet.alert2me.data.model.ApiInfo;
import com.redhelmet.alert2me.data.model.AppConfig;
import com.redhelmet.alert2me.data.model.Category;
import com.redhelmet.alert2me.data.model.EventGroup;
import com.redhelmet.alert2me.data.model.User;
import com.redhelmet.alert2me.domain.util.PreferenceUtils;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class SharedPreferenceStorage implements PreferenceStorage {
    private final String CONFIG_KEY = "CONFIG_KEY";
    private final String DEVICE_KEY = "DEVICE_KEY";
    private final String USER_LOCATION_KEY = "USER_LOCATION_KEY";
    private final String DEFAULT_FILTER_KEY = "DEFAULT_FILTER_KEY";
    private final String CUSTOM_FILTER_KEY = "CUSTOM_FILTER_KEY";
    private final String IS_DEFAULT_FILTER_KEY = "IS_DEFAULT_FILTER_KEY";
    private final String HAVE_ACCOUNT_KEY = "HAVE_ACCOUNT_KEY";
    private final String LOGGED_IN_KEY = "LOGGED_IN_KEY";
    private final String TOKEN_KEY = "TOKEN_KEY";
    private final String USER_KEY = "USER_KEY";
    private final String ENABLE_PROXIMITY_KEY = "ENABLE_PROXIMITY_KEY";
    private Context context;
    private Gson gson;

    @Inject
    public SharedPreferenceStorage(Context context, Gson gson) {
        this.context = context.getApplicationContext();
        this.gson = gson;
    }

    @Override
    public boolean isAccepted() {
        return (boolean) PreferenceUtils.getFromPrefs(context, context.getString(R.string.pref_accepted), false);
    }

    @Override
    public void setAccepted(boolean accepted) {
        PreferenceUtils.saveToPrefs(context, context.getString(R.string.pref_accepted), true);
    }

    @Override
    public void saveAppConfig(AppConfig appConfig) {
        String json = gson.toJson(appConfig);
        PreferenceUtils.saveToPrefs(context, CONFIG_KEY, json);
    }

    @Override
    public AppConfig getAppConfig() {
        String json = (String) PreferenceUtils.getFromPrefs(context, CONFIG_KEY, "");
        AppConfig config = gson.fromJson(json, AppConfig.class);
        if (config == null) config = new AppConfig();
        return config;
    }

    @Override
    public void saveDeviceInfo(ApiInfo apiInfo) {
        String json = gson.toJson(apiInfo);
        PreferenceUtils.saveToPrefs(context, DEVICE_KEY, json);
    }

    @Override
    public ApiInfo getDeviceInfo() {
        String json = (String) PreferenceUtils.getFromPrefs(context, DEVICE_KEY, "");
        return gson.fromJson(json, ApiInfo.class);
    }

    @Override
    public void saveUserCustomFilters(List<Category> categories) {
        StringBuilder builder = new StringBuilder(String.valueOf(categories.get(0).getId()));
        for (Category category : categories) {
            builder.append(",");
            builder.append(category.getId());
        }

        PreferenceUtils.saveToPrefs(context, CUSTOM_FILTER_KEY, builder.toString());
    }

    @Override
    public List<Long> getUserCustomFilters() {
        List<Long> result = new ArrayList<>();
        String list = (String) PreferenceUtils.getFromPrefs(context, CUSTOM_FILTER_KEY, "");
        if (!TextUtils.isEmpty(list)) {
            String[] arr = list.split(",");
            for (String number : arr) {
                result.add(Long.getLong(number));
            }
        }
        return result;
    }

    @Override
    public void saveUserDefaultFilters(List<EventGroup> eventGroups) {
        if (eventGroups == null || eventGroups.size() == 0) return;
        StringBuilder builder = new StringBuilder(String.valueOf(eventGroups.get(0).getId()));
        if (eventGroups.size() > 1) {
            for (int i = 1; i < eventGroups.size(); i++) {
                builder.append(",");
                builder.append(eventGroups.get(i).getId());
            }
        }

        PreferenceUtils.saveToPrefs(context, DEFAULT_FILTER_KEY, builder.toString());
    }

    @Override
    public List<Long> getUserDefaultFilters() {
        List<Long> result = new ArrayList<>();
        String list = (String) PreferenceUtils.getFromPrefs(context, DEFAULT_FILTER_KEY, "");
        if (!TextUtils.isEmpty(list)) {
            String[] arr = list.split(",");
            for (String number : arr) {
                result.add(Long.parseLong(number));
            }
        }
        return result;
    }

    @Override
    public void setDefaultFilter(boolean isDefault) {
        PreferenceUtils.saveToPrefs(context, IS_DEFAULT_FILTER_KEY, isDefault);
    }

    @Override
    public boolean isDefaultFilter() {
        return (boolean) PreferenceUtils.getFromPrefs(context, IS_DEFAULT_FILTER_KEY, true);
    }

    @Override
    public Location getLastUserLocation() {
        String str = (String) PreferenceUtils.getFromPrefs(context, USER_LOCATION_KEY, "User Location,0,0");
        String[] arr = str.split(",");
        if (arr.length < 3) return null;
        Location location = new Location(arr[0]);
        location.setLatitude(Double.parseDouble(arr[1]));
        location.setLongitude(Double.parseDouble(arr[2]));
        return location;
    }

    @Override
    public void saveCurrentUserLocation(Location location) {
        if (location == null) return;
        double lat = location.getLatitude();
        double lng = location.getLongitude();
        String provider = location.getProvider();
        String str = provider + "," + lat + "," + lng;
        PreferenceUtils.saveToPrefs(context, USER_LOCATION_KEY, str);
    }

    @Override
    public boolean haveAccount() {
        return (boolean) PreferenceUtils.getFromPrefs(context, HAVE_ACCOUNT_KEY, false);
    }

    @Override
    public void setHaveAccount(boolean haveAccount) {
        PreferenceUtils.saveToPrefs(context, HAVE_ACCOUNT_KEY, haveAccount);
    }

    @Override
    public boolean isLoggedIn() {
        return (boolean) PreferenceUtils.getFromPrefs(context, LOGGED_IN_KEY, false);
    }

    @Override
    public void setLoggedIn(boolean isLoggedIn) {
        PreferenceUtils.saveToPrefs(context, LOGGED_IN_KEY, isLoggedIn);
    }

    @Override
    public void saveToken(String token) {
        PreferenceUtils.saveToPrefs(context, TOKEN_KEY, token);
    }

    @Override
    public String getToken() {
        return (String) PreferenceUtils.getFromPrefs(context, TOKEN_KEY, "");
    }

    @Override
    public void saveUserInfo(User user) {
        String json = gson.toJson(user);
        PreferenceUtils.saveToPrefs(context, USER_KEY, json);
    }

    @Override
    public User getCurrentUser() {
        String json = (String) PreferenceUtils.getFromPrefs(context, USER_KEY, "");
        return gson.fromJson(json, User.class);
    }

    @Override
    public boolean isProximityEnabled() {
        return (boolean) PreferenceUtils.getFromPrefs(context, ENABLE_PROXIMITY_KEY, false);
    }

    @Override
    public void setProximityEnabled(boolean enabled) {
        PreferenceUtils.saveToPrefs(context, ENABLE_PROXIMITY_KEY, enabled);
    }
}
