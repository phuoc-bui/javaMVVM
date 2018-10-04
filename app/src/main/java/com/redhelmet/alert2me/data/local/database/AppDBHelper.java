package com.redhelmet.alert2me.data.local.database;

import com.redhelmet.alert2me.data.model.Category;
import com.redhelmet.alert2me.data.model.EventGroup;

import java.util.List;

import io.reactivex.Observable;

public class AppDBHelper implements DBHelper {
    private AppDatabase database;

    public AppDBHelper(AppDatabase database) {
        this.database = database;
    }

    @Override
    public void saveCategories(List<Category> categories) {
        database.categoryDao().saveCategories(categories);
    }

    @Override
    public Observable<List<Category>> getCategories() {
        return database.categoryDao().getCategories().toObservable();
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
        return database.eventGroupDao().getEventGroups().toObservable();
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
}
