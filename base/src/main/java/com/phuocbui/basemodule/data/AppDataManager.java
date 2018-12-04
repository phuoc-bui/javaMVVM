package com.phuocbui.basemodule.data;

import com.phuocbui.basemodule.data.database.DatabaseStorage;
import com.phuocbui.basemodule.data.preference.PreferenceStorage;
import com.phuocbui.basemodule.data.remote.RemoteStorage;

import javax.inject.Inject;

public class AppDataManager implements DataManager {
    private final String TAG = AppDataManager.class.getSimpleName();

    private PreferenceStorage pref;
    private RemoteStorage remote;
    private DatabaseStorage database;

    @Inject
    public AppDataManager(PreferenceStorage pref, DatabaseStorage db, RemoteStorage remote) {
        this.pref = pref;
        this.database = db;
        this.remote = remote;
    }
}
