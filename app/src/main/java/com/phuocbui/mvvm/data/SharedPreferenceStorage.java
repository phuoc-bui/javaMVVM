package com.phuocbui.mvvm.data;

import android.content.Context;

import com.google.gson.Gson;
import com.phuocbui.basemodule.data.BasePreferenceStorage;
import com.phuocbui.mvvm.data.model.DeviceInfo;
import com.phuocbui.mvvm.data.model.User;

import javax.inject.Inject;

public class SharedPreferenceStorage extends BasePreferenceStorage implements PreferenceStorage {
    private static final String PREFS_NAME = "MVVMDemo";
    private final String DEVICE_KEY = "DEVICE_KEY";
    private final String LOGGED_IN_KEY = "LOGGED_IN_KEY";
    private final String USER_KEY = "USER_KEY";
    private final String ENABLE_PROXIMITY_KEY = "ENABLE_PROXIMITY_KEY";

    @Inject
    public SharedPreferenceStorage(Context context, Gson gson) {
        super(context, PREFS_NAME, gson);
    }

    @Override
    public DeviceInfo getDeviceInfo() {
        return get(DEVICE_KEY, new DeviceInfo());
    }

    @Override
    public void setLoggedIn(boolean isLoggedIn) {
        putBoolean(LOGGED_IN_KEY, isLoggedIn);
    }

    @Override
    public User getCurrentUser() {
        return get(USER_KEY, User.class);
    }

    @Override
    public boolean isProximityEnabled() {
        return getBoolean(ENABLE_PROXIMITY_KEY, false);
    }

    @Override
    public void setProximityEnabled(boolean enabled) {
        putBoolean(ENABLE_PROXIMITY_KEY, enabled);
    }
}
