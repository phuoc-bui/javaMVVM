package com.redhelmet.alert2me.data.database.dao;

import com.redhelmet.alert2me.data.model.EventGroup;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;
import io.reactivex.Completable;
import io.reactivex.Single;

@Dao
public interface EventGroupDao {

    @Query("SELECT * FROM EventGroup")
    Single<List<EventGroup>> getEventGroups();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable saveEventGroups(List<EventGroup> eventGroups);

    @Update
    Completable updateEventGroups(List<EventGroup> eventGroups);

    @Query("SELECT * FROM EventGroup WHERE filterOn = 1")
    Single<List<EventGroup>> getFilterOnEventGroups();
}
