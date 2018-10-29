package com.redhelmet.alert2me.di;

import androidx.room.Room;
import android.content.Context;

import com.redhelmet.alert2me.BuildConfig;
import com.redhelmet.alert2me.data.AppDataManager;
import com.redhelmet.alert2me.data.DataManager;
import com.redhelmet.alert2me.data.PreferenceStorage;
import com.redhelmet.alert2me.data.database.DatabaseStorage;
import com.redhelmet.alert2me.data.database.AppRoomDatabase;
import com.redhelmet.alert2me.data.database.RoomDatabaseStorage;
import com.redhelmet.alert2me.data.remote.ApiHelper;

import javax.inject.Singleton;

import androidx.room.migration.Migration;
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
    public DatabaseStorage provideDatabaseStorage(AppRoomDatabase database) {
        return new RoomDatabaseStorage(database);
    }

    @Singleton
    @Provides
    public AppRoomDatabase provideRoomDatabase(Context context) {
        return Room.databaseBuilder(context, AppRoomDatabase.class, BuildConfig.DB_FILE_NAME + "room")
                .fallbackToDestructiveMigration()
                .build();
    }
}
