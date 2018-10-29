package com.redhelmet.alert2me.data.database;

import android.util.Log;

import com.redhelmet.alert2me.data.Mapper;
import com.redhelmet.alert2me.data.model.Category;
import com.redhelmet.alert2me.data.model.EditWatchZones;
import com.redhelmet.alert2me.data.model.Event;
import com.redhelmet.alert2me.data.model.EventGroup;

import java.util.List;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;

public class RoomDatabaseStorage implements DatabaseStorage {
    private AppRoomDatabase database;
    private Mapper mapper;

    @Inject
    public RoomDatabaseStorage(AppRoomDatabase database, Mapper mapper) {
        this.database = database;
        this.mapper = mapper;
    }

    @Override
    public void saveCategories(List<Category> categories) {
        database.categoryDao().nukeTable();
        database.categoryDao().saveCategories(categories);
    }

    @Override
    public Observable<List<Category>> getCategories() {
        return database.categoryDao().getCategories()
                .doOnSuccess(list -> Log.e("Database", "getCategories: " + list.size()))
                .toObservable().subscribeOn(Schedulers.computation());
    }

    @Override
    public Single<Category> getEventCategory(Event event) {
        return database.categoryDao().getEventCategory(event.getCategory());
    }

    @Override
    public List<Category> getCategoriesSync() {
        return database.categoryDao().getCategoriesSync();
    }

    @Override
    public void saveEventGroups(List<EventGroup> eventGroups) {
        database.eventGroupDao().saveEventGroups(eventGroups);
    }

    @Override
    public Observable<List<EventGroup>> getEventGroups() {
        return database.eventGroupDao().getEventGroups()
                .doOnSuccess(list -> Log.e("Database", "getEventGroups: " + list.size()))
                .toObservable().subscribeOn(Schedulers.computation());
    }

    @Override
    public List<EventGroup> getEventGroupsSync() {
        return database.eventGroupDao().getEventGroupsSync();
    }

    @Override
    public Observable<List<Category>> getCategoriesWithIds(List<Long> ids) {
        return database.categoryDao().getCategoriesWithIds(ids).toObservable();
    }

    @Override
    public Observable<List<EventGroup>> getEventGroupsWithIds(List<Long> ids) {
        return database.eventGroupDao().getEventGroupWithIds(ids).toObservable();
    }

    @Override
    public void saveEditedCategories(List<Category> categories) {
        database.categoryDao().updateCategories(categories);
    }

    @Override
    public void saveEditedEventGroups(List<EventGroup> eventGroups) {
        database.eventGroupDao().updateEventGroups(eventGroups);
    }

    @Override
    public Observable<List<Category>> getEditedCategories() {
        return database.categoryDao().getFilterOnCategories().toObservable().subscribeOn(Schedulers.computation());
    }

    @Override
    public Observable<List<EventGroup>> getEditedEventGroups() {
        return database.eventGroupDao().getFilterOnEventGroups().toObservable().subscribeOn(Schedulers.computation());
    }

    @Override
    public void saveWatchZones(List<EditWatchZones> watchZones) {
        database.watchZoneDao().nukeTable();
        database.watchZoneDao().saveWatchZones(mapper.mapWzToWzEntities(watchZones));
    }

    @Override
    public Observable<List<EditWatchZones>> getWatchZones() {
        return database.watchZoneDao().getWatchZones()
                .map(entities -> mapper.mapWzEntitiesToWz(entities))
                .toObservable()
                .subscribeOn(Schedulers.computation());
    }
}
