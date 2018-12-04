package com.phuocbui.mvvm.data;

import android.util.Log;

import com.phuocbui.mvvm.data.database.DatabaseStorage;
import com.phuocbui.mvvm.data.model.Category;
import com.phuocbui.mvvm.data.model.CategoryStatus;
import com.phuocbui.mvvm.data.model.CategoryType;
import com.phuocbui.mvvm.data.model.EditWatchZones;
import com.phuocbui.mvvm.data.model.Event;
import com.phuocbui.mvvm.data.model.EventGroup;
import com.phuocbui.mvvm.data.remote.ApiHelper;
import com.phuocbui.mvvm.data.remote.response.ConfigResponse;

import java.util.Comparator;
import java.util.List;

import javax.inject.Inject;

import androidx.annotation.WorkerThread;
import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;

public class AppDataManager implements DataManager {
    private final String TAG = AppDataManager.class.getSimpleName();

    private PreferenceStorage pref;
    private ApiHelper api;
    private DatabaseStorage database;

    private List<Category> categories;
    private List<EventGroup> eventGroups;

    @Inject
    public AppDataManager(PreferenceStorage pref, DatabaseStorage db, ApiHelper apiHelper) {
        this.pref = pref;
        this.database = db;
        this.api = apiHelper;
    }

    @Override
    public void saveConfig(ConfigResponse config) {
        categories = copyStatusToCategoryType(config.categories);
        eventGroups = config.eventGroups;
        database.saveCategories(categories).subscribe();
        database.saveEventGroups(eventGroups).subscribe();
    }

    @Override
    public Observable<ConfigResponse> loadConfig() {
        return api.getConfig().doOnNext(this::saveConfig);
    }

    @Override
    public Observable<List<Event>> getAllEvents() {
        return api.getAllEvents();
    }

    @Override
    public Single<Category> getEventCategory(Event event) {
        return database.getEventCategory(event);
    }

    @Override
    public Observable<List<EventGroup>> getFilterOnDefaultFilters() {
        return database.getFilterOnEventGroups().toObservable()
                .subscribeOn(Schedulers.computation());
    }

    @Override
    public Observable<Event> getEventsWithFilterOneByOne(Comparator<Event> sort) {
        Log.e(TAG, "Start filter event --------");
        return getAllEvents()
                .flatMap(Observable::fromIterable)
                .sorted(sort)
                .filter(event -> {
                    if (event.isAlwaysOn()) {
                        updateEventByCategory(event);
                        return true;
                    }
                    updateEventByCategory(event);
                    return filterEventWithDefaultFilter(event);
                });
    }

    private boolean filterEventWithDefaultFilter(Event event) {
        return getFilterOnDefaultFilters()
                .flatMap(Observable::fromIterable)
                .flatMap(group -> Observable.fromIterable(group.getDisplayFilter()))
                .flatMap(display -> Observable.fromArray(display.getLayers()))
                .any(layer -> layer.equalsIgnoreCase(event.getGroup())).blockingGet();
    }

    private void updateEventByCategory(Event event) {
        getEventCategory(event)
                .toObservable()
                .flatMap(category -> Observable.fromIterable(category.getStatuses()))
                .any(status -> {
                    if (event.getStatusCode().equals(status.getCode())) {
                        event.setPrimaryColor(status.getPrimaryColor());
                        event.setSecondaryColor(status.getSecondaryColor());
                        event.setTextColor(status.getTextColor());
                        event.setName(status.getName());
                        return true;
                    }
                    return false;
                }).subscribe();
    }

    private List<Category> copyStatusToCategoryType(List<Category> categories) {
        if (categories == null) return null;
        for (Category category : categories) {
            List<CategoryType> typeList = category.getTypes();
            for (CategoryType type : typeList) {
                // update status
                for (CategoryStatus status : category.getStatuses()) {
                    status.setNotificationDefaultOn(type.isNotificationDefaultOn());
                }
                type.setStatuses(category.getStatuses());
            }
        }
        return categories;
    }

    @WorkerThread
    @Override
    public void saveWatchZones(List<EditWatchZones> watchZones) {
        database.clearWatchZones();
        database.saveWatchZones(watchZones);
    }
}
