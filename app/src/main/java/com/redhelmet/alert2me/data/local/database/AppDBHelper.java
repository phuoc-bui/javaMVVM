package com.redhelmet.alert2me.data.local.database;

import com.redhelmet.alert2me.data.model.Category;
import com.redhelmet.alert2me.data.model.EventGroup;
import com.redhelmet.alert2me.data.remote.response.ConfigResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
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
        return database.categoryDao().getCategories();
    }

    @Override
    public void saveEventGroups(List<EventGroup> eventGroups) {
        database.eventGroupDao().saveEventGroups(eventGroups);
    }

    @Override
    public Observable<List<EventGroup>> getEventGroups() {
        return database.eventGroupDao().getEventGroups();
    }
}
