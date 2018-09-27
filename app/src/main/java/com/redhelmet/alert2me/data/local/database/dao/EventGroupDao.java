package com.redhelmet.alert2me.data.local.database.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import com.redhelmet.alert2me.data.model.EventGroup;

import java.util.List;

import io.reactivex.Observable;

@Dao
public interface EventGroupDao {

    @Query("SELECT * FROM Category")
    Observable<List<EventGroup>> getEventGroups();

    @Insert
    void saveEventGroups(List<EventGroup> eventGroups);
}
