package com.redhelmet.alert2me.data.local.pref;

import android.content.Context;

import com.google.gson.Gson;
import com.redhelmet.alert2me.R;
import com.redhelmet.alert2me.data.model.ApiInfo;
import com.redhelmet.alert2me.data.model.AppConfig;
import com.redhelmet.alert2me.domain.util.PreferenceUtils;

public class AppPreferenceHelper implements PreferenceHelper {
    private final String CONFIG_KEY = "config";
    private final String DEVICE_KEY = "apiInfo";
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
}
