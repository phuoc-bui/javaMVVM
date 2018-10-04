package com.redhelmet.alert2me.data;

import android.location.Location;
import android.util.Log;

import com.redhelmet.alert2me.R;
import com.redhelmet.alert2me.data.local.database.DBHelper;
import com.redhelmet.alert2me.data.local.pref.PreferenceHelper;
import com.redhelmet.alert2me.data.model.ApiInfo;
import com.redhelmet.alert2me.data.model.AppConfig;
import com.redhelmet.alert2me.data.model.Category;
import com.redhelmet.alert2me.data.model.Event;
import com.redhelmet.alert2me.data.model.EventGroup;
import com.redhelmet.alert2me.data.model.Hint;
import com.redhelmet.alert2me.data.remote.ApiHelper;
import com.redhelmet.alert2me.data.remote.request.ProximityLocationRequest;
import com.redhelmet.alert2me.data.remote.response.ConfigResponse;
import com.redhelmet.alert2me.data.remote.response.ProximityLocationResponse;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Single;
import io.reactivex.schedulers.Schedulers;

public class AppDataManager implements DataManager {
    private final String TAG = AppDataManager.class.getSimpleName();

    private PreferenceHelper pref;
    private ApiHelper api;
    private DBHelper database;

    public AppDataManager(PreferenceHelper pref, DBHelper db, ApiHelper apiHelper) {
        this.pref = pref;
        this.database = db;
        this.api = apiHelper;
    }

    @Override
    public void saveConfig(ConfigResponse config) {
        pref.saveAppConfig(config.appConfig);
        database.saveCategories(config.categories);
        database.saveEventGroups(config.eventGroups);
    }

    @Override
    public Observable<ConfigResponse> loadConfig() {
        return api.getConfig()
                .doOnNext(this::saveConfig)
                .doOnError(this::handleError)
                .subscribeOn(Schedulers.io());
    }

    @Override
    public AppConfig getAppConfig() {
        return pref.getAppConfig();
    }

    @Override
    public void setInitialLaunch(boolean isInitial) {
        pref.setInitialLaunch(isInitial);
    }

    @Override
    public boolean getInitialLaunch() {
        return pref.isInitialLaunch();
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
                .subscribeOn(Schedulers.io())
                .doOnNext(apiInfo -> pref.saveDeviceInfo(apiInfo))
                .doOnError(error -> {
                    ApiInfo apiInfo = new ApiInfo();
                    apiInfo.setUserId("0");
                    apiInfo.setApiToken("");
                    pref.saveDeviceInfo(apiInfo);
                    handleError(error);
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
        return api.getAllEvents().subscribeOn(Schedulers.io())
                .doOnError(this::handleError);
    }

    @Override
    public Observable<List<Category>> getCategories() {
        return database.getCategories().subscribeOn(Schedulers.computation());
    }

    @Override
    public List<Category> getCategoriesSync() {
        return database.getCategoriesSync();
    }

    @Override
    public Observable<List<EventGroup>> getEventGroups() {
        return database.getEventGroups().subscribeOn(Schedulers.computation());
    }

    @Override
    public List<EventGroup> getEventGroupsSync() {
        return database.getEventGroupsSync();
    }

    @Override
    public Observable<List<Category>> getUserCustomFilters() {
        List<Long> ids = pref.getUserCustomFilters();
        return database.getCategoriesWithIds(ids);
    }

    @Override
    public Observable<List<EventGroup>> getUserDefaultFilters() {
        List<Long> ids = pref.getUserDefaultFilters();
        return database.getEventGroupsWithIds(ids);
    }

    @Override
    public void saveUserCustomFilters(List<Category> categories) {
        pref.saveUserCustomFilters(categories);
    }

    @Override
    public void saveUserDefaultFilters(List<EventGroup> eventGroups) {
        pref.saveUserDefaultFilters(eventGroups);
    }

    @Override
    public Observable<List<Event>> getEventsWithFilter(boolean isDefault) {
        Log.e(TAG, "Start filter event");
        return getAllEvents()
                .flatMap(Observable::fromIterable)
                .filter(event -> {
                    if (event.isAlwaysOn()) {
                        return updateEventByCategory(event)
                                .doOnSuccess(b -> Log.e(TAG, "updated is always event with category"))
                                .blockingGet();
                    }

                    return isDefault ? filterEventWithDefaultFilter(event)
                            .map(b -> {
                                if (b) return updateEventByCategory(event)
                                        .doOnSuccess(a -> Log.e(TAG, "updated event with category"))
                                        .blockingGet();
                                return false;
                            }).doOnSuccess(b -> Log.e(TAG, "default filter done"))
                            .blockingGet()
                            : filterEventWithCustomFilter(event)
                            .doOnSuccess(a -> Log.e(TAG, "custom filter done"))
                            .blockingGet();
                })
                .doOnNext(event -> Log.e(TAG, "filter success: " + event.getId()))
                .toList()
                .doOnSuccess(list -> Log.e(TAG, "Complete filter, return list " + list.size()))
                .toObservable();
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
        return getCategories()
                .flatMap(categories -> {
                    Log.e(TAG, "start flat map");
                    return Observable.fromIterable(categories);
                })
                .any(category -> {
                    Log.e(TAG, "start find category of event");
                    if (event.getCategory().equalsIgnoreCase(category.getCategory())) {
                        // update event
                        return Observable.fromIterable(category.getStatuses())
                                .any(status -> {
                                    Log.e(TAG, "start find status of event");
                                    if (event.getStatusCode().equals(status.getCode())) {
                                        event.setPrimaryColor(status.getPrimaryColor());
                                        event.setSecondaryColor(status.getSecondaryColor());
                                        event.setTextColor(status.getTextColor());
                                        event.setName(status.getName());
                                        return true;
                                    }
                                    return false;
                                }).blockingGet();
                    }
                    return false;
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
    public List<Hint> getHintData() {
        List<Hint> hints = new ArrayList<>();

        Hint hint1 = new Hint();
        hint1.setTitle("");
        hint1.setDesc("EmergencyAUS keeps you<br>updated with current<br>emergency information and<br>alerts in Australia.");
        hint1.setResId(R.drawable.hint1);
        hint1.setLast(true);

        Hint hint2 = new Hint();
        hint2.setTitle("Observation");
        hint2.setDesc("Share what you know<br>share what you see, hear and feel.");
        hint2.setResId(R.drawable.hint1);

        Hint hint3 = new Hint();
        hint3.setTitle("Watch Zones");
        hint3.setDesc("Monitor the risk all day<br>all night, all year");
        hint3.setResId(R.drawable.hint2);

        Hint hint4 = new Hint();
        hint4.setTitle("Warning & Incidents");
        hint4.setDesc("Be aware of your environment<br>your risk, your safety");
        hint4.setResId(R.drawable.hint3);

        hints.add(hint1);
        hints.add(hint2);
        hints.add(hint3);
        hints.add(hint4);
        return hints;
    }

    private void handleError(Throwable error) {
        Log.e(TAG, error.getMessage());
    }
}
