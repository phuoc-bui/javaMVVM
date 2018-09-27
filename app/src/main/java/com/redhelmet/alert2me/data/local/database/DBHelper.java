package com.redhelmet.alert2me.data.local.database;

import com.redhelmet.alert2me.data.model.Category;
import com.redhelmet.alert2me.data.model.EventGroup;

import java.util.List;

import io.reactivex.Observable;

public interface DBHelper {
    void saveCategories(List<Category> categories);

    Observable<List<Category>> getCategories();

    void saveEventGroups(List<EventGroup> eventGroups);

    Observable<List<EventGroup>> getEventGroups();
}
