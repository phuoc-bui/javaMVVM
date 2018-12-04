package com.phuocbui.mvvm.data.database;

import com.phuocbui.mvvm.data.model.Category;
import com.phuocbui.mvvm.data.model.EditWatchZones;
import com.phuocbui.mvvm.data.model.Event;
import com.phuocbui.mvvm.data.model.EventGroup;

import java.util.List;

import io.reactivex.Completable;
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

    Single<List<EventGroup>> getFilterOnEventGroups();

    Completable saveWatchZones(List<EditWatchZones> watchZones);

    Single<Integer> clearWatchZones();

    Single<List<EditWatchZones>> getWatchZones();

    Completable addWatchZone(EditWatchZones watchZone);

    Completable editWatchZone(EditWatchZones watchZone);

    Completable enableWatchZone(long watchZoneId, boolean enable);

    Completable deleteWatchZone(long watchZoneId);
}
