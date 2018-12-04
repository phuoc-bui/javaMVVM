package com.phuocbui.basemodule.data.preference;

import android.content.Context;

import com.google.gson.Gson;
import com.phuocbui.basemodule.BuildConfig;

import javax.inject.Inject;

public class SharedPreferenceStorage extends BasePreferenceStorage implements PreferenceStorage {
    private static final String PREFS_NAME = BuildConfig.PREFERENCE_NAME;

    @Inject
    public SharedPreferenceStorage(Context context, Gson gson) {
        super(context, PREFS_NAME, gson);
    }
}
