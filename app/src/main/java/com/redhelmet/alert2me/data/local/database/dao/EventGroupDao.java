package com.redhelmet.alert2me.data.local.database.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.redhelmet.alert2me.data.model.Category;
import com.redhelmet.alert2me.data.model.EventGroup;

import java.util.List;

import io.reactivex.Observable;

@Dao
public interface EventGroupDao {

    @Query("SELECT * FROM EventGroup")
    Observable<List<EventGroup>> getEventGroups();

    @Query("SELECT * FROM EventGroup")
    List<EventGroup> getEventGroupsSync();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void saveEventGroups(List<EventGroup> eventGroups);

    @Query("SELECT * FROM EventGroup WHERE id IN (:ids)")
    Observable<List<EventGroup>> getEventGroupWithIds(List<Long> ids);
}
