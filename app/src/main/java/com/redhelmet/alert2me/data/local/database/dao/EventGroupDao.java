package com.redhelmet.alert2me.data.local.database.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.redhelmet.alert2me.data.model.EventGroup;

import java.util.List;

import io.reactivex.Single;

@Dao
public interface EventGroupDao {

    @Query("SELECT * FROM EventGroup")
    Single<List<EventGroup>> getEventGroups();

    @Query("SELECT * FROM EventGroup")
    List<EventGroup> getEventGroupsSync();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void saveEventGroups(List<EventGroup> eventGroups);

    @Query("SELECT * FROM EventGroup WHERE id IN (:ids)")
    Single<List<EventGroup>> getEventGroupWithIds(List<Long> ids);

    @Update
    void updateEventGroups(List<EventGroup> eventGroups);

    @Query("SELECT * FROM EventGroup WHERE filterOn = 1")
    Single<List<EventGroup>> getFilterOnEventGroups();
}
