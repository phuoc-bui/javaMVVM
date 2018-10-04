package com.redhelmet.alert2me.data.local.database;

import android.arch.persistence.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.redhelmet.alert2me.data.model.CategoryStatus;
import com.redhelmet.alert2me.data.model.CategoryType;
import com.redhelmet.alert2me.data.model.EventGroupDisplayFilter;

import java.lang.reflect.Type;
import java.util.Date;
import java.util.List;

public class Converter {

    @TypeConverter
    public static List<String> fromString(String value) {
        Type listType = new TypeToken<List<String>>() {
        }.getType();
        return new Gson().fromJson(value, listType);
    }

    @TypeConverter
    public static String fromList(List<String> list) {
        return new Gson().toJson(list);
    }

    @TypeConverter
    public static List<EventGroupDisplayFilter> fromDisplayFilterString(String value) {
        Type listType = new TypeToken<List<EventGroupDisplayFilter>>() {
        }.getType();
        return new Gson().fromJson(value, listType);
    }

    @TypeConverter
    public static String fromDisplayFilterList(List<EventGroupDisplayFilter> list) {
        return new Gson().toJson(list);
    }

    @TypeConverter
    public static List<CategoryType> fromCategoryTypeString(String value) {
        Type listType = new TypeToken<List<CategoryType>>() {
        }.getType();
        return new Gson().fromJson(value, listType);
    }

    @TypeConverter
    public static String fromCategoryTypeList(List<CategoryType> list) {
        return new Gson().toJson(list);
    }

    @TypeConverter
    public static List<CategoryStatus> fromCategoryStatusString(String value) {
        Type listType = new TypeToken<List<CategoryStatus>>() {
        }.getType();
        return new Gson().fromJson(value, listType);
    }

    @TypeConverter
    public static String fromCategoryStatusList(List<CategoryStatus> list) {
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
