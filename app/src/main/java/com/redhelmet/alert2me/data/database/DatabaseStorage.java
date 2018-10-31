package com.redhelmet.alert2me.data.database;

import com.redhelmet.alert2me.data.model.Category;
import com.redhelmet.alert2me.data.model.EditWatchZones;
import com.redhelmet.alert2me.data.model.Event;
import com.redhelmet.alert2me.data.model.EventGroup;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Observable;
import io.reactivex.Single;

public interface DatabaseStorage {
    Completable saveCategories(List<Category> categories);

    Single<List<Category>> getCategories();

    Single<Category> getEventCategory(Event event);

    Completable saveEventGroups(List<EventGroup> eventGroups);

    Single<List<EventGroup>> getEventGroups();

    Completable saveEditedCategories(List<Category> categories);

    Completable saveEditedEventGroups(List<EventGroup> eventGroups);

    Single<List<Category>> getEditedCategories();

    Single<List<EventGroup>> getEditedEventGroups();

    Completable saveWatchZones(List<EditWatchZones> watchZones);

    Completable clearWatchZones();

    Single<List<EditWatchZones>> getWatchZones();

    Completable addWatchZone(EditWatchZones watchZone);
}
