package com.phuocbui.mvvm.data;

import com.phuocbui.mvvm.data.model.Category;
import com.phuocbui.mvvm.data.model.EditWatchZones;
import com.phuocbui.mvvm.data.model.Event;
import com.phuocbui.mvvm.data.model.EventGroup;
import com.phuocbui.mvvm.data.remote.response.ConfigResponse;

import java.util.Comparator;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Single;

public interface DataManager {
    void saveConfig(ConfigResponse config);

    Observable<ConfigResponse> loadConfig();

    Observable<List<Event>> getAllEvents();

    Single<Category> getEventCategory(Event event);

    Observable<List<EventGroup>> getFilterOnDefaultFilters();

    Observable<Event> getEventsWithFilterOneByOne(Comparator<Event> sort);

    void saveWatchZones(List<EditWatchZones> watchZones);
}
