package com.redhelmet.alert2me.data.database;

import android.util.Log;

import com.redhelmet.alert2me.data.Mapper;
import com.redhelmet.alert2me.data.model.Category;
import com.redhelmet.alert2me.data.model.EditWatchZones;
import com.redhelmet.alert2me.data.model.Event;
import com.redhelmet.alert2me.data.model.EventGroup;

import java.util.List;

import javax.inject.Inject;

import androidx.annotation.WorkerThread;
import io.reactivex.Completable;
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
    public Completable saveCategories(List<Category> categories) {
        return database.categoryDao().saveCategories(categories)
                .subscribeOn(Schedulers.computation());
    }

    @Override
    public Single<List<Category>> getCategories() {
        return database.categoryDao().getCategories()
                .subscribeOn(Schedulers.computation())
                .doOnSuccess(list -> Log.e("Database", "getCategories: " + list.size()));
    }

    @Override
    public Single<Category> getEventCategory(Event event) {
        return database.categoryDao().getEventCategory(event.getCategory())
                .subscribeOn(Schedulers.computation());
    }

    @Override
    public Completable saveEventGroups(List<EventGroup> eventGroups) {
        return database.eventGroupDao().saveEventGroups(eventGroups)
                .subscribeOn(Schedulers.computation());
    }

    @Override
    public Single<List<EventGroup>> getEventGroups() {
        return database.eventGroupDao().getEventGroups()
                .subscribeOn(Schedulers.computation())
                .doOnSuccess(list -> Log.e("Database", "getEventGroups: " + list.size()));
    }

    @Override
    public Completable saveEditedCategories(List<Category> categories) {
        return database.categoryDao().updateCategories(categories)
                .subscribeOn(Schedulers.computation());
    }

    @Override
    public Completable saveEditedEventGroups(List<EventGroup> eventGroups) {
        return database.eventGroupDao().updateEventGroups(eventGroups)
                .subscribeOn(Schedulers.computation());
    }

    @Override
    public Single<List<Category>> getEditedCategories() {
        return database.categoryDao().getFilterOnCategories()
                .subscribeOn(Schedulers.computation());
    }

    @Override
    public Single<List<EventGroup>> getFilterOnEventGroups() {
        return database.eventGroupDao().getFilterOnEventGroups()
                .subscribeOn(Schedulers.computation());
    }

    @Override
    public Completable saveWatchZones(List<EditWatchZones> watchZones) {
        return database.watchZoneDao().saveWatchZones(mapper.mapWzToWzEntities(watchZones))
                .subscribeOn(Schedulers.computation());
    }

    @Override
    public Single<Integer> clearWatchZones() {
        return Single.just(database.watchZoneDao().nukeTable())
                .subscribeOn(Schedulers.computation());
    }

    @Override
    public Single<List<EditWatchZones>> getWatchZones() {
        return database.watchZoneDao().getWatchZones()
                .subscribeOn(Schedulers.computation())
                .map(entities -> mapper.mapWzEntitiesToWz(entities));
    }

    @WorkerThread
    @Override
    public Completable addWatchZone(EditWatchZones watchZone) {
        return database.watchZoneDao().saveWatchZone(mapper.map(watchZone))
                .subscribeOn(Schedulers.computation());
    }

    @Override
    public Completable editWatchZone(EditWatchZones watchZone) {
        return database.watchZoneDao().updateWatchZone(mapper.map(watchZone))
                .subscribeOn(Schedulers.computation());
    }

    @Override
    public Completable enableWatchZone(long watchZoneId, boolean enable) {
        return Completable.fromAction(() -> database.watchZoneDao().enableWatchZone(watchZoneId, enable))
                .subscribeOn(Schedulers.computation());
    }

    @Override
    public Completable deleteWatchZone(long watchZoneId) {
        return Completable.fromAction(() -> database.watchZoneDao().deleteWatchZone(watchZoneId))
                .subscribeOn(Schedulers.computation());
    }
}
