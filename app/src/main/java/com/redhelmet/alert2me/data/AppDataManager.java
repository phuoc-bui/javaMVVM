package com.redhelmet.alert2me.data;

import android.location.Location;
import android.util.Log;

import com.redhelmet.alert2me.R;
import com.redhelmet.alert2me.data.database.DBHelper;
import com.redhelmet.alert2me.data.model.ApiInfo;
import com.redhelmet.alert2me.data.model.AppConfig;
import com.redhelmet.alert2me.data.model.Category;
import com.redhelmet.alert2me.data.model.CategoryStatus;
import com.redhelmet.alert2me.data.model.CategoryType;
import com.redhelmet.alert2me.data.model.Event;
import com.redhelmet.alert2me.data.model.EventGroup;
import com.redhelmet.alert2me.data.model.Hint;
import com.redhelmet.alert2me.data.model.User;
import com.redhelmet.alert2me.data.remote.ApiHelper;
import com.redhelmet.alert2me.data.remote.request.ProximityLocationRequest;
import com.redhelmet.alert2me.data.remote.response.ConfigResponse;
import com.redhelmet.alert2me.data.remote.response.ForgotPasswordResponse;
import com.redhelmet.alert2me.data.remote.response.LoginResponse;
import com.redhelmet.alert2me.data.remote.response.ProximityLocationResponse;
import com.redhelmet.alert2me.data.remote.response.RegisterAccountResponse;

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

    private List<Category> categories;
    private List<EventGroup> eventGroups;

    public AppDataManager(PreferenceHelper pref, DBHelper db, ApiHelper apiHelper) {
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
        return database.getCategories().subscribeOn(Schedulers.computation())
                .doOnNext(list -> categories = list);
    }

    @Override
    public List<Category> getCategoriesSync() {
        return categories;
    }

    @Override
    public Observable<List<EventGroup>> getEventGroups() {
        return database.getEventGroups().subscribeOn(Schedulers.computation())
                .doOnNext(list -> eventGroups = list);
    }

    @Override
    public List<EventGroup> getEventGroupsSync() {
        return eventGroups;
    }

    @Override
    public Observable<List<Category>> getUserCustomFilters() {
        return database.getEditedCategories().subscribeOn(Schedulers.computation());
    }

    @Override
    public Observable<List<EventGroup>> getUserDefaultFilters() {
        return database.getEditedEventGroups().subscribeOn(Schedulers.computation());
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
        return getCategories()
                .flatMap(Observable::fromIterable)
                .any(category -> {
                    if (event.getCategory().equalsIgnoreCase(category.getCategory())) {
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

    @Override
    public Observable<RegisterAccountResponse> registerAccount(User user) {
        return api.registerAccount(user)
                .doOnNext(response -> pref.saveToken(response.account.token));
    }

    @Override
    public Observable<LoginResponse> login(String email, String password) {
        return api.login(email, password);
    }

    @Override
    public Observable<ForgotPasswordResponse> forgotPassword(String email) {
        return api.forgotPassword(email);
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
}
