package com.phuocbui.mvvm.data.database.dao;

import com.phuocbui.mvvm.data.database.entity.WatchZoneEntity;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;
import io.reactivex.Completable;
import io.reactivex.Single;

@Dao
public interface WatchZoneDao {

    @Query("SELECT * FROM WatchZone")
    Single<List<WatchZoneEntity>> getWatchZones();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable saveWatchZones(List<WatchZoneEntity> watchZones);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable saveWatchZone(WatchZoneEntity watchZone);

    @Update
    Completable updateWatchZone(WatchZoneEntity watchZone);

    @Query("UPDATE WatchZone SET enable = :enabled WHERE id = :id")
    int enableWatchZone(long id, boolean enabled);

    @Query("DELETE FROM WatchZone WHERE id = :id")
    int deleteWatchZone(long id);

    @Query("DELETE FROM WatchZone")
    int nukeTable();
}
