package com.redhelmet.alert2me.di;

import android.arch.persistence.room.Room;
import android.content.Context;

import com.google.gson.Gson;
import com.redhelmet.alert2me.BuildConfig;
import com.redhelmet.alert2me.data.AppDataManager;
import com.redhelmet.alert2me.data.DataManager;
import com.redhelmet.alert2me.data.PreferenceStorage;
import com.redhelmet.alert2me.data.SharedPreferenceStorage;
import com.redhelmet.alert2me.data.database.DatabaseStorage;
import com.redhelmet.alert2me.data.database.RoomDatabase;
import com.redhelmet.alert2me.data.database.RoomDatabaseStorage;
import com.redhelmet.alert2me.data.remote.ApiHelper;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class RepositoryModule {

    @Singleton
    @Provides
    public DataManager providesDataManager(PreferenceStorage pref, DatabaseStorage db, ApiHelper api) {
        return new AppDataManager(pref, db, api);
    }

    @Singleton
    @Provides
    public DatabaseStorage provideDatabaseStorage(RoomDatabase database) {
        return new RoomDatabaseStorage(database);
    }

    @Singleton
    @Provides
    public RoomDatabase provideRoomDatabase(Context context) {
        return Room.databaseBuilder(context, RoomDatabase.class, BuildConfig.DB_FILE_NAME + "room")
                .build();
    }
}
