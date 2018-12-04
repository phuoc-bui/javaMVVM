package com.phuocbui.mvvm.di;

import android.content.Context;

import com.phuocbui.mvvm.BuildConfig;
import com.phuocbui.mvvm.data.AppDataManager;
import com.phuocbui.mvvm.data.DataManager;
import com.phuocbui.mvvm.data.Mapper;
import com.phuocbui.mvvm.data.PreferenceStorage;
import com.phuocbui.mvvm.data.database.AppRoomDatabase;
import com.phuocbui.mvvm.data.database.DatabaseStorage;
import com.phuocbui.mvvm.data.database.RoomDatabaseStorage;
import com.phuocbui.mvvm.data.remote.ApiHelper;

import javax.inject.Singleton;

import androidx.room.Room;
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
    public DatabaseStorage provideDatabaseStorage(AppRoomDatabase database, Mapper mapper) {
        return new RoomDatabaseStorage(database, mapper);
    }

    @Singleton
    @Provides
    public AppRoomDatabase provideRoomDatabase(Context context) {
        return Room.databaseBuilder(context, AppRoomDatabase.class, BuildConfig.DB_FILE_NAME + "room")
                .fallbackToDestructiveMigration()
                .build();
    }

    @Singleton
    @Provides
    public Mapper provideMapper() {
        return new Mapper();
    }
}
