package com.phuocbui.mvvm.data.database;

import com.phuocbui.mvvm.BuildConfig;
import com.phuocbui.mvvm.data.database.dao.CategoryDao;
import com.phuocbui.mvvm.data.database.dao.EventGroupDao;
import com.phuocbui.mvvm.data.database.dao.WatchZoneDao;
import com.phuocbui.mvvm.data.database.entity.WatchZoneEntity;
import com.phuocbui.mvvm.data.model.Category;
import com.phuocbui.mvvm.data.model.EventGroup;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

/**
 * The Room Database that contains the User table.
 */
@Database(entities = {Category.class, EventGroup.class, WatchZoneEntity.class},
        version = BuildConfig.DB_SCHEMA_VERSION,
        exportSchema = false)
@TypeConverters(value = {Converter.class})
public abstract class AppRoomDatabase extends RoomDatabase {
    public abstract CategoryDao categoryDao();

    public abstract EventGroupDao eventGroupDao();

    public abstract WatchZoneDao watchZoneDao();
}
