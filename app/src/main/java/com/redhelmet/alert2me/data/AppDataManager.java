package com.redhelmet.alert2me.data;

import android.location.Location;
import android.util.Log;

import com.redhelmet.alert2me.data.database.DatabaseStorage;
import com.redhelmet.alert2me.data.model.ApiInfo;
import com.redhelmet.alert2me.data.model.AppConfig;
import com.redhelmet.alert2me.data.model.Category;
import com.redhelmet.alert2me.data.model.CategoryStatus;
import com.redhelmet.alert2me.data.model.CategoryType;
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
        database.saveCategories(categories);
        database.saveEventGroups(config.eventGroups);
        eventGroups = config.eventGroups;
    }

    @Override
    public Observable<ConfigResponse> loadConfig() {
        return api.getConfig().subscribeOn(Schedulers.io())
                .doOnNext(this::saveConfig);
    }

    @Override
    public AppConfig getAppConfig() {
        return pref.getAppConfig();
    }

    @Override
    public void setAccepted(boolean accepted) {
        pref.setAccepted(accepted);
    }

    @Override
    public boolean getAccepted() {
        return pref.isAccepted();
    }

    @Override
    public Observable<ApiInfo> getUserId(String firebaseToken) {
        return api.registerDevice(firebaseToken)
                .doOnNext(apiInfo -> pref.saveDeviceInfo(apiInfo))
                .doOnError(error -> {
                    ApiInfo apiInfo = new ApiInfo();
                    apiInfo.setUserId("0");
                    apiInfo.setApiToken("");
                    pref.saveDeviceInfo(apiInfo);
                });
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
        String userId = pref.getDeviceInfo().getUserId();
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
        return database.getCategories()
                .doOnNext(list -> categories = list);
    }

    @Override
    public Single<Category> getEventCategory(Event event) {
        return database.getEventCategory(event);
    }

    @Override
    public List<Category> getCategoriesSync() {
        return categories;
    }

    @Override
    public Observable<List<EventGroup>> getEventGroups() {
        return database.getEventGroups()
                .doOnNext(list -> eventGroups = list);
    }

    @Override
    public List<EventGroup> getEventGroupsSync() {
        return eventGroups;
    }

    @Override
    public Observable<List<Category>> getUserCustomFilters() {
        return database.getEditedCategories();
    }

    @Override
    public Observable<List<EventGroup>> getUserDefaultFilters() {
        return database.getEditedEventGroups();
    }

    @Override
    public void saveUserCustomFilters(List<Category> categories) {
        database.saveEditedCategories(categories);
    }

    @Override
    public void saveUserDefaultFilters(List<EventGroup> eventGroups) {
        database.saveEditedEventGroups(eventGroups);
    }

    @Override
    public Observable<List<Event>> getEventsWithFilter(boolean isDefault, Comparator<Event> sort) {
        return getEventsWithFilterOneByOne(isDefault, sort)
                .toList()
                .doOnSuccess(list -> Log.e(TAG, "Complete filter, return list " + list.size()))
                .toObservable();
    }

    @Override
    public Observable<Event> getEventsWithFilterOneByOne(boolean isDefault, Comparator<Event> sort) {
        Log.e(TAG, "Start filter event --------");
        return getAllEvents()
                .flatMap(Observable::fromIterable)
                .sorted(sort)
                .filter(event -> {
                    if (event.isAlwaysOn()) {
                        return updateEventByCategory(event)
                                .blockingGet();
                    }

                    return isDefault ? filterEventWithDefaultFilter(event)
                            .map(b -> {
                                if (b) return updateEventByCategory(event)
                                        .blockingGet();
                                return false;
                            }).blockingGet()
                            : filterEventWithCustomFilter(event)
                            .blockingGet();
                })
                .doOnNext(event -> Log.e(TAG, "filter success: " + event.getId()))
                .doOnComplete(() -> Log.e(TAG, "Complete filter event ---------"));
    }

    private Single<Boolean> filterEventWithDefaultFilter(Event event) {
        return getUserDefaultFilters()
                .flatMap(Observable::fromIterable)
                .flatMap(group -> Observable.fromIterable(group.getDisplayFilter()))
                .flatMap(display -> Observable.fromArray(display.getLayers()))
                .any(layer -> layer.equalsIgnoreCase(event.getGroup()));
    }

    private Single<Boolean> filterEventWithCustomFilter(Event event) {
        return getUserCustomFilters()
                .flatMap(Observable::fromIterable)
                .any(category -> event.getCategory().equalsIgnoreCase(category.getCategory())
                        && filterAndUpdateEventWithCategory(event, category).blockingGet());
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

    private Single<Boolean> updateEventByCategory(Event event) {
        return getEventCategory(event)
                .map(category -> {
                    // update event
                    return Observable.fromIterable(category.getStatuses())
                            .any(status -> {
                                if (event.getStatusCode().equals(status.getCode())) {
                                    event.setPrimaryColor(status.getPrimaryColor());
                                    event.setSecondaryColor(status.getSecondaryColor());
                                    event.setTextColor(status.getTextColor());
                                    event.setName(status.getName());
                                    return true;
                                }
                                return false;
                            }).blockingGet();
                });
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
        return api.registerAccount(user)
                .doOnNext(response -> pref.saveToken(response.account.token));
    }

    @Override
    public Observable<User> login(String email, String password) {
        return api.login(email, password)
                .doOnNext(user -> pref.saveUserInfo(user));
    }

    @Override
    public Observable<ForgotPasswordResponse> forgotPassword(String email) {
        return api.forgotPassword(email);
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
        String userId = pref.getDeviceInfo().getUserId();
        return Observable.concatArrayEager(database.getWatchZones().doOnError(err -> Log.e("AppDataManager", "Fail to get Watch Zones from DB")),
                api.getWatchZones(userId)
                        .doOnNext(watchZoneResponse -> saveWatchZones(watchZoneResponse.watchzones))
                        .doOnError(err -> Log.e("AppDataManager", "Fail to get Watch Zones from API"))
                        .map(response -> response.watchzones))
                .debounce(400L, TimeUnit.MILLISECONDS);
    }

    @Override
    public void saveWatchZones(List<EditWatchZones> watchZones) {
        Single.just(1)
                .doOnSuccess(i -> database.saveWatchZones(watchZones))
                .subscribe();
    }

    @Override
    public Observable<Object> addWatchZone(EditWatchZones watchZone) {
        String userId = pref.getDeviceInfo().getUserId();
        return api.createWatchZone(userId, watchZone)
        .doOnNext(o -> database.addWatchZone(watchZone));
    }

    @Override
    public Observable<Object> editWatchZone(EditWatchZones watchZone) {
        return null;
    }
}
