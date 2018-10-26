package com.redhelmet.alert2me.data.database.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.redhelmet.alert2me.ui.activity.EditWatchZone;

import java.util.List;

import io.reactivex.Single;

@Dao
public interface WatchZoneDao {

    @Query("SELECT * FROM WatchZone")
    Single<List<EditWatchZone>> getWatchZones();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void saveWatchZones(List<EditWatchZone> watchZones);

    @Query("DELETE FROM WatchZone")
    void nukeTable();
}
