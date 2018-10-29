package com.redhelmet.alert2me.data.database;

import com.redhelmet.alert2me.BuildConfig;
import com.redhelmet.alert2me.data.database.dao.CategoryDao;
import com.redhelmet.alert2me.data.database.dao.EventGroupDao;
import com.redhelmet.alert2me.data.database.dao.WatchZoneDao;
import com.redhelmet.alert2me.data.model.Category;
import com.redhelmet.alert2me.data.model.EditWatchZones;
import com.redhelmet.alert2me.data.model.EventGroup;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

/**
 * The Room Database that contains the User table.
 */
@Database(entities = {Category.class, EventGroup.class, EditWatchZones.class},
        version = BuildConfig.DB_SCHEMA_VERSION,
        exportSchema = false)
@TypeConverters(value = {Converter.class})
public abstract class AppRoomDatabase extends RoomDatabase {
    public abstract CategoryDao categoryDao();

    public abstract EventGroupDao eventGroupDao();

    public abstract WatchZoneDao watchZoneDao();
}
