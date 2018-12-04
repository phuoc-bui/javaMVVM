package com.phuocbui.basemodule.data.preference;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;

import androidx.annotation.NonNull;

public class BasePreferenceStorage {

    private Gson gson;
    private SharedPreferences pref;

    public BasePreferenceStorage(@NonNull Context context, @NonNull String name, @NonNull Gson gson) {
        this.gson = gson;
        pref = context.getApplicationContext().getSharedPreferences(name, Context.MODE_PRIVATE);
    }

    public void putString(@NonNull String name, @NonNull String data) {
        pref.edit().putString(name, data).apply();
    }

    public void putInt(@NonNull String name, int data) {
        pref.edit().putInt(name, data).apply();
    }

    public void putBoolean(@NonNull String name, boolean data) {
        pref.edit().putBoolean(name, data).apply();
    }

    public void putFloat(@NonNull String name, float data) {
        pref.edit().putFloat(name, data).apply();
    }

    public void putLong(@NonNull String name, long data) {
        pref.edit().putLong(name, data).apply();
    }

    public <T> void put(@NonNull String name, T data) {
        String json = gson.toJson(data);
        putString(name, json);
    }

    public String getString(@NonNull String name, String defValue) {
        return pref.getString(name, defValue);
    }

    public int getInt(@NonNull String name, int defValue) {
        return pref.getInt(name, defValue);
    }

    public boolean getBoolean(@NonNull String name, boolean defValue) {
        return pref.getBoolean(name, defValue);
    }

    public float getFloat(@NonNull String name, float defValue) {
        return pref.getFloat(name, defValue);
    }

    public long getLong(@NonNull String name, long defValue) {
        return pref.getLong(name, defValue);
    }

    public <T> T get(@NonNull String name, Class<T> clazz) {
        String json = getString(name, "");
        return gson.fromJson(json, clazz);
    }

    public <T> T get(@NonNull String name, @NonNull T defValue) {
        String json = getString(name, "");
        T value = gson.fromJson(json, (Class<T>) defValue.getClass());
        if (value == null) value = defValue;
        return value;
    }
}
