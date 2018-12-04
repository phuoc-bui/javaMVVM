package com.phuocbui.mvvm.data.database;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.phuocbui.mvvm.data.model.CategoryStatus;
import com.phuocbui.mvvm.data.model.CategoryType;
import com.phuocbui.mvvm.data.model.EventGroupDisplayFilter;
import com.phuocbui.mvvm.data.model.WatchZoneFilterType;

import java.lang.reflect.Type;
import java.util.Date;
import java.util.List;

import androidx.room.TypeConverter;

public class Converter {

    @TypeConverter
    public static List<String> fromJsonToStrings(String value) {
        Type listType = new TypeToken<List<String>>() {
        }.getType();
        return new Gson().fromJson(value, listType);
    }

    @TypeConverter
    public static String fromStrings(List<String> list) {
        return new Gson().toJson(list);
    }

    @TypeConverter
    public static double[][][] fromJsonToArray(String value) {
        Type listType = new TypeToken<double[][][]>() {
        }.getType();
        return new Gson().fromJson(value, listType);
    }

    @TypeConverter
    public static String fromArray(double[][][] list) {
        return new Gson().toJson(list);
    }

    @TypeConverter
    public static List<Integer> fromJsonToIntegers(String value) {
        Type listType = new TypeToken<List<Integer>>() {
        }.getType();
        return new Gson().fromJson(value, listType);
    }

    @TypeConverter
    public static String fromIntegers(List<Integer> list) {
        return new Gson().toJson(list);
    }

    @TypeConverter
    public static List<WatchZoneFilterType> fromJsonToWZTypes(String value) {
        Type listType = new TypeToken<List<WatchZoneFilterType>>() {
        }.getType();
        return new Gson().fromJson(value, listType);
    }

    @TypeConverter
    public static String fromWZTypes(List<WatchZoneFilterType> list) {
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
