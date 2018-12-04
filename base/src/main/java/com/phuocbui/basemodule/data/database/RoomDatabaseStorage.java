package com.phuocbui.basemodule.data.database;

import com.phuocbui.basemodule.data.Mapper;

import javax.inject.Inject;

public class RoomDatabaseStorage implements DatabaseStorage {
    //    private AppRoomDatabase database;
    private Mapper mapper;

    @Inject
    public RoomDatabaseStorage(Mapper mapper) {
//        this.database = database;
        this.mapper = mapper;
    }
}
