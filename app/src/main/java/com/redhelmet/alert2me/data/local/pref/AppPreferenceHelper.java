package com.redhelmet.alert2me.data.local.pref;

import android.content.Context;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.redhelmet.alert2me.R;
import com.redhelmet.alert2me.data.model.ApiInfo;
import com.redhelmet.alert2me.data.model.AppConfig;
import com.redhelmet.alert2me.data.model.Category;
import com.redhelmet.alert2me.data.model.EventGroup;
import com.redhelmet.alert2me.domain.util.PreferenceUtils;

import java.util.ArrayList;
import java.util.List;

public class AppPreferenceHelper implements PreferenceHelper {
    private final String CONFIG_KEY = "CONFIG_KEY";
    private final String DEVICE_KEY = "DEVICE_KEY";
    private final String DEFAULT_FILTER_KEY = "DEFAULT_FILTER_KEY";
    private final String CUSTOM_FILTER_KEY = "CUSTOM_FILTER_KEY";
    private final String IS_DEFAULT_FILTER_KEY = "IS_DEFAULT_FILTER_KEY";
    private Context context;
    private Gson gson;

    public AppPreferenceHelper(Context context, Gson gson) {
        this.context = context.getApplicationContext();
        this.gson = gson;
    }

    @Override
    public boolean isInitialLaunch() {
        return (boolean) PreferenceUtils.getFromPrefs(context, context.getString(R.string.pref_initialLaunch), false);
    }

    @Override
    public void setInitialLaunch(boolean isInitial) {
        PreferenceUtils.saveToPrefs(context, context.getString(R.string.pref_initialLaunch), true);
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
        return gson.fromJson(json, AppConfig.class);
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
        StringBuilder builder = new StringBuilder(String.valueOf(eventGroups.get(0).getId()));
        for (EventGroup eventGroup : eventGroups) {
            builder.append(",");
            builder.append(eventGroup.getId());
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
                result.add(Long.getLong(number));
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
        return (boolean) PreferenceUtils.getFromPrefs(context, IS_DEFAULT_FILTER_KEY, false);
    }
}
