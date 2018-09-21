package com.redhelmet.alert2me.data.local.pref;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.gson.Gson;
import com.redhelmet.alert2me.R;
import com.redhelmet.alert2me.data.model.response.ConfigResponse;
import com.redhelmet.alert2me.domain.util.PreferenceUtils;
import com.redhelmet.alert2me.ui.splash.SplashScreen;

public class AppPreferenceHelper implements PreferenceHelper {
    private final String CONFIG_KEY = "config";
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

    }

    @Override
    public boolean isAccepted() {
        return (boolean) PreferenceUtils.getFromPrefs(context, context.getString(R.string.pref_accepted), false);
    }

    @Override
    public void setAccepted(boolean accepted) {

    }

    @Override
    public void saveConfig(ConfigResponse configResponse) {
        String json = gson.toJson(configResponse);
        PreferenceUtils.saveToPrefs(context, CONFIG_KEY, json);
    }

    @Override
    public ConfigResponse getConfig() {
        String json = (String) PreferenceUtils.getFromPrefs(context, CONFIG_KEY, "");
        return gson.fromJson(json, ConfigResponse.class);
    }
}
