package com.phuocbui.basemodule.di;

import com.phuocbui.basemodule.data.AppDataManager;
import com.phuocbui.basemodule.data.DataManager;
import com.phuocbui.basemodule.data.Mapper;
import com.phuocbui.basemodule.data.database.DatabaseStorage;
import com.phuocbui.basemodule.data.database.RoomDatabaseStorage;
import com.phuocbui.basemodule.data.preference.PreferenceStorage;
import com.phuocbui.basemodule.data.remote.RemoteStorage;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class RepositoryModule {

    @Singleton
    @Provides
    public DataManager providesDataManager(PreferenceStorage pref, DatabaseStorage db, RemoteStorage api) {
        return new AppDataManager(pref, db, api);
    }

    @Singleton
    @Provides
    public DatabaseStorage provideDatabaseStorage(Mapper mapper) {
        return new RoomDatabaseStorage(mapper);
    }

//    @Singleton
//    @Provides
//    public AppRoomDatabase provideRoomDatabase(Context context) {
//        return Room.databaseBuilder(context, AppRoomDatabase.class, BuildConfig.DB_FILE_NAME)
//                .fallbackToDestructiveMigration()
//                .build();
//    }

    @Singleton
    @Provides
    public Mapper provideMapper() {
        return new Mapper();
    }
}
