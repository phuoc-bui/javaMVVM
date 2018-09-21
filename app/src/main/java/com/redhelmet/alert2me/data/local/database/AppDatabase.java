package com.redhelmet.alert2me.data.local.database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;

import com.redhelmet.alert2me.BuildConfig;

/**
 * The Room Database that contains the User table.
 */
@Database(entities = {}, version = BuildConfig.DB_SCHEMA_VERSION)
@TypeConverters({Converter.class})
public abstract class AppDatabase extends RoomDatabase {
//    public abstract UserDao userDao();
//    public abstract PostDao postDao();
}
