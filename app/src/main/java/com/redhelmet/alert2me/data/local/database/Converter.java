package com.redhelmet.alert2me.data.local.database;

import android.arch.persistence.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;

public class Converter {

    @TypeConverter
    public static <T> ArrayList<T> fromString(String value) {
        Type listType = new TypeToken<ArrayList<T>>() {}.getType();
        return new Gson().fromJson(value, listType);
    }

    @TypeConverter
    public static <T> String fromArrayList(ArrayList<T> list) {
        return new Gson().toJson(list);
    }

    @TypeConverter
    public static Date fromTimestamp(Long value) {
        return value == null ? null : new Date(value);
    }

    @TypeConverter
    public static Long dateToTimestamp(Date date) {
        return date == null ? null : date.getTime();
    }
}
