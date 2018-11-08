package com.redhelmet.alert2me.data;

import android.location.Location;
import android.util.Log;

import com.redhelmet.alert2me.data.database.DatabaseStorage;
import com.redhelmet.alert2me.data.model.Category;
import com.redhelmet.alert2me.data.model.CategoryStatus;
import com.redhelmet.alert2me.data.model.CategoryType;
import com.redhelmet.alert2me.data.model.DeviceInfo;
import com.redhelmet.alert2me.data.model.EditWatchZones;
import com.redhelmet.alert2me.data.model.Event;
import com.redhelmet.alert2me.data.model.EventGroup;
import com.redhelmet.alert2me.data.model.User;
import com.redhelmet.alert2me.data.remote.ApiHelper;
import com.redhelmet.alert2me.data.remote.request.ProximityLocationRequest;
import com.redhelmet.alert2me.data.remote.response.ConfigResponse;
import com.redhelmet.alert2me.data.remote.response.ForgotPasswordResponse;
import com.redhelmet.alert2me.data.remote.response.ProximityLocationResponse;
import com.redhelmet.alert2me.data.remote.response.RegisterAccountResponse;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.TimeUnit;

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
        pref.saveAppConfig(config.appConfig);
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
    public void setAccepted(boolean accepted) {
        pref.setAccepted(accepted);
    }

    @Override
    public Observable<DeviceInfo> registerDeviceToken(String firebaseToken) {
        return api.registerDevice(firebaseToken)
                .doOnNext(apiInfo -> pref.saveDeviceInfo(apiInfo))
                .doOnError(e -> Log.e(TAG, "register device error"));
    }

    @Override
    public Location getLastUserLocation() {
        return pref.getLastUserLocation();
    }

    @Override
    public void saveUserLocation(Location location) {
        pref.saveCurrentUserLocation(location);
    }

    @Override
    public Observable<ProximityLocationResponse> putProximityLocation(double lat, double lng) {
        ProximityLocationRequest request = new ProximityLocationRequest(lat, lng);
        String userId = String.valueOf(pref.getDeviceInfo().getId());
        return api.putProximityLocation(userId, request)
                .doOnNext(response -> {
//                    PreferenceUtils.saveToPrefs(getApplicationContext(),
//                            Constants.KEY_LASTUPDATEDUSERLATITUDE,
//                            String.valueOf(
//                                    Double.valueOf((String) PreferenceUtils.getFromPrefs(getApplicationContext(),
//                                            Constants.KEY_USERLATITUDE, "0"))));
//                    PreferenceUtils.saveToPrefs(getApplicationContext(),Constants.KEY_LASTUPDATEDUSERLONGITUDE,String.valueOf(Double.valueOf((String ) PreferenceUtils.getFromPrefs(getApplicationContext(), Constants.KEY_USERLONGITUDE, "0"))));
//                    appPreferences.put(Constants.PROXIMITY_MOVEMENT, 3);
                });
    }

    @Override
    public Observable<List<Event>> getAllEvents() {
        return api.getAllEvents();
    }

    @Override
    public Observable<List<Category>> getCategories() {
        return database.getCategories().toObservable()
                .doOnNext(list -> categories = list);
    }

    @Override
    public Single<Category> getEventCategory(Event event) {
        return database.getEventCategory(event);
    }

    @Override
    public Observable<List<EventGroup>> getEventGroups() {
        return database.getEventGroups().toObservable()
                .doOnNext(list -> eventGroups = list);
    }

    @Override
    public Observable<List<Category>> getUserCustomFilters() {
        return database.getEditedCategories().toObservable()
                .subscribeOn(Schedulers.computation());
    }

    @Override
    public Observable<List<EventGroup>> getFilterOnDefaultFilters() {
        return database.getFilterOnEventGroups().toObservable()
                .subscribeOn(Schedulers.computation());
    }

    @WorkerThread
    @Override
    public void saveUserCustomFilters(List<Category> categories) {
        database.saveEditedCategories(categories).subscribe();
    }

    @WorkerThread
    @Override
    public void saveUserDefaultFilters(List<EventGroup> eventGroups) {
        database.saveEditedEventGroups(eventGroups).subscribe();
    }

    @Override
    public Observable<List<Event>> getEventsWithFilter(boolean isDefault, Comparator<Event> sort) {
        return getEventsWithFilterOneByOne(isDefault, sort)
                .toList()
                .doOnSuccess(list -> Log.e(TAG, "Complete filter, return list " + list.size()))
                .toObservable();
    }

    @Override
    public Observable<List<Event>> getEventsWithFilter(boolean isDefault) {
        return getEventsWithFilterOneByOne(isDefault)
                .toList()
                .doOnSuccess(list -> Log.e(TAG, "Complete filter, return list " + list.size()))
                .toObservable();
    }

    @Override
    public Observable<Event> getEventsWithFilterOneByOne(boolean isDefault) {
        Log.e(TAG, "Start filter event --------");
        return getAllEvents()
                .flatMap(Observable::fromIterable)
                .filter(event -> {
                    if (event.isAlwaysOn()) {
                        updateEventByCategory(event);
                        return true;
                    }
                    if (isDefault) {
                        updateEventByCategory(event);
                        return filterEventWithDefaultFilter(event);
                    } else {
                        return filterEventWithCustomFilter(event);
                    }
                })
                .doOnError(e -> Log.e(TAG, "filter error: " + e.getMessage()))
                .doOnNext(event -> Log.e(TAG, "filter success: " + event.getId()))
                .doOnComplete(() -> Log.e(TAG, "Complete filter event ---------"));
    }

    @Override
    public Observable<Event> getEventsWithFilterOneByOne(boolean isDefault, Comparator<Event> sort) {
        Log.e(TAG, "Start filter event --------");
        return getAllEvents()
                .flatMap(Observable::fromIterable)
                .sorted(sort)
                .filter(event -> {
                    if (event.isAlwaysOn()) {
                        updateEventByCategory(event);
                        return true;
                    }
                    if (isDefault) {
                        updateEventByCategory(event);
                        return filterEventWithDefaultFilter(event);
                    } else {
                        return filterEventWithCustomFilter(event);
                    }
                })
                .doOnError(e -> Log.e(TAG, "filter error: " + e.getMessage()))
                .doOnNext(event -> Log.e(TAG, "filter success: " + event.getId()))
                .doOnComplete(() -> Log.e(TAG, "Complete filter event ---------"));
    }

    private boolean filterEventWithDefaultFilter(Event event) {
        return getFilterOnDefaultFilters()
                .flatMap(Observable::fromIterable)
                .flatMap(group -> Observable.fromIterable(group.getDisplayFilter()))
                .flatMap(display -> Observable.fromArray(display.getLayers()))
                .any(layer -> layer.equalsIgnoreCase(event.getGroup())).blockingGet();
    }

    private boolean filterEventWithCustomFilter(Event event) {
        return getUserCustomFilters()
                .flatMap(Observable::fromIterable)
                .any(category -> event.getCategory().equalsIgnoreCase(category.getCategory())
                        && filterAndUpdateEventWithCategory(event, category).blockingGet())
                .blockingGet();
    }

    private Single<Boolean> filterAndUpdateEventWithCategory(Event event, Category category) {
        return Observable.fromIterable(category.getTypes())
                .filter(type -> (event.getEventTypeCode().equals(type.getCode())))
                .flatMap(type -> Observable.fromIterable(category.getStatuses()))
                .any(status -> {
                    if (event.getStatusCode().equals(status.getCode())) {
                        // update event by category
                        event.setPrimaryColor(status.getPrimaryColor());
                        event.setSecondaryColor(status.getSecondaryColor());
                        event.setTextColor(status.getTextColor());
                        event.setName(status.getName());
                        return true;
                    }
                    return false;
                });
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

    @Override
    public boolean isDefaultFilter() {
        return pref.isDefaultFilter();
    }

    @Override
    public void setDefaultFilter(boolean isDefault) {
        pref.setDefaultFilter(isDefault);
    }

    @Override
    public Observable<RegisterAccountResponse> registerAccount(User user) {
        String deviceId = String.valueOf(pref.getDeviceInfo().getId());
        return api.registerAccount(deviceId, user);
    }

    @Override
    public Observable<User> login(String email, String password) {
        String deviceId = String.valueOf(pref.getDeviceInfo().getId());
        return api.login(deviceId, email, password)
                .doOnNext(user -> pref.saveUserInfo(user));
    }

    @Override
    public Observable<ForgotPasswordResponse> forgotPassword(String email) {
        String deviceId = String.valueOf(pref.getDeviceInfo().getId());
        return api.forgotPassword(deviceId, email);
    }

    @Override
    public Observable<User> updateUserProfile(User user) {
        return api.updateUserProfile(user)
                .doOnNext(u -> pref.saveUserInfo(u));
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

    @Override
    public Observable<List<EditWatchZones>> getWatchZones() {
        String deviceId = String.valueOf(pref.getDeviceInfo().getId());
        return Observable.concatArrayEager(database.getWatchZones().toObservable()
                        .subscribeOn(Schedulers.computation())
                        .doOnError(err -> Log.e("AppDataManager", "Fail to get Watch Zones from DB: " + err)),
                api.getWatchZones(deviceId)
                        .map(response -> response.watchzones)
                        .doOnNext(this::saveWatchZones)
                        .doOnError(err -> Log.e("AppDataManager", "Fail to get Watch Zones from API: " + err)))
                .debounce(400L, TimeUnit.MILLISECONDS);
    }

    @WorkerThread
    @Override
    public void saveWatchZones(List<EditWatchZones> watchZones) {
        database.clearWatchZones();
        database.saveWatchZones(watchZones);
    }

    @Override
    public Observable<EditWatchZones> addWatchZone(EditWatchZones watchZone) {
        String deviceId = String.valueOf(pref.getDeviceInfo().getId());
        return api.createWatchZone(deviceId, watchZone)
                .doOnNext(o -> database.addWatchZone(o));
    }

    @Override
    public Observable<Object> editWatchZone(EditWatchZones watchZone) {
        String deviceId = String.valueOf(pref.getDeviceInfo().getId());
        return api.editWatchZone(deviceId,watchZone.getId(), watchZone)
                .doOnNext(o -> database.editWatchZone(watchZone));
    }

    @Override
    public Observable<Object> enableWatchZone(long watchZoneId, boolean enabled) {
        String deviceId = String.valueOf(pref.getDeviceInfo().getId());
        return api.enableWatchZone(deviceId, watchZoneId, enabled)
                .doOnNext(o -> database.enableWatchZone(watchZoneId, enabled));
    }

    @Override
    public Observable<Object> deleteWatchZone(long watchZoneId) {
        String deviceId = String.valueOf(pref.getDeviceInfo().getId());
        return api.deleteWatchZone(deviceId, watchZoneId)
                .doOnNext(o -> database.deleteWatchZone(watchZoneId));
    }
}
