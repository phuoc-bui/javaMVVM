package com.redhelmet.alert2me.data.database;

import com.redhelmet.alert2me.data.model.Category;
import com.redhelmet.alert2me.data.model.EditWatchZones;
import com.redhelmet.alert2me.data.model.Event;
import com.redhelmet.alert2me.data.model.EventGroup;

import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Single;

public interface DatabaseStorage {
    void saveCategories(List<Category> categories);

    Observable<List<Category>> getCategories();

    Single<Category> getEventCategory(Event event);

    List<Category> getCategoriesSync();

    Observable<List<Category>> getCategoriesWithIds(List<Long> ids);

    void saveEventGroups(List<EventGroup> eventGroups);

    Observable<List<EventGroup>> getEventGroups();

    List<EventGroup> getEventGroupsSync();

    Observable<List<EventGroup>> getEventGroupsWithIds(List<Long> ids);

    void saveEditedCategories(List<Category> categories);

    void saveEditedEventGroups(List<EventGroup> eventGroups);

    Observable<List<Category>> getEditedCategories();

    Observable<List<EventGroup>> getEditedEventGroups();

    void saveWatchZones(List<EditWatchZones> watchZones);

    Observable<List<EditWatchZones>> getWatchZones();

    void addWatchZone(EditWatchZones watchZone);
}
