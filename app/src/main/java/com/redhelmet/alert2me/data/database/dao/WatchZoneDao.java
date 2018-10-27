package com.redhelmet.alert2me.data.database.dao;

import com.redhelmet.alert2me.data.model.EditWatchZones;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

import io.reactivex.Single;

@Dao
public interface WatchZoneDao {

    @Query("SELECT * FROM WatchZone")
    Single<List<EditWatchZones>> getWatchZones();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void saveWatchZones(List<EditWatchZones> watchZones);

    @Query("DELETE FROM WatchZone")
    void nukeTable();
}
