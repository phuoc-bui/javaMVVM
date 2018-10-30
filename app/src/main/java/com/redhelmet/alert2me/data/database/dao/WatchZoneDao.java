package com.redhelmet.alert2me.data.database.dao;

import com.redhelmet.alert2me.data.database.entity.WatchZoneEntity;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import io.reactivex.Single;

@Dao
public interface WatchZoneDao {

    @Query("SELECT * FROM WatchZone")
    Single<List<WatchZoneEntity>> getWatchZones();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void saveWatchZones(List<WatchZoneEntity> watchZones);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void saveWatchZone(WatchZoneEntity watchZone);

    @Query("DELETE FROM WatchZone")
    void nukeTable();
}
