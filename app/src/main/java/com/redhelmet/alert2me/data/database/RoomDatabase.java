package com.redhelmet.alert2me.data.database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;

import com.redhelmet.alert2me.BuildConfig;
import com.redhelmet.alert2me.data.database.dao.CategoryDao;
import com.redhelmet.alert2me.data.database.dao.EventGroupDao;
import com.redhelmet.alert2me.data.model.Category;
import com.redhelmet.alert2me.data.model.EventGroup;

/**
 * The Room Database that contains the User table.
 */
@Database(entities = {Category.class, EventGroup.class},
        version = BuildConfig.DB_SCHEMA_VERSION,
        exportSchema = false)
@TypeConverters({Converter.class})
public abstract class RoomDatabase extends android.arch.persistence.room.RoomDatabase {
    public abstract CategoryDao categoryDao();

    public abstract EventGroupDao eventGroupDao();
}
