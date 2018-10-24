package com.redhelmet.alert2me.data.remote;

import com.redhelmet.alert2me.data.model.ApiInfo;
import com.redhelmet.alert2me.data.model.Event;
import com.redhelmet.alert2me.data.model.User;
import com.redhelmet.alert2me.data.remote.request.ProximityLocationRequest;
import com.redhelmet.alert2me.data.remote.request.RegisterDeviceRequest;
import com.redhelmet.alert2me.data.remote.response.ConfigResponse;
import com.redhelmet.alert2me.data.remote.response.ForgotPasswordResponse;
import com.redhelmet.alert2me.data.remote.response.ProximityLocationResponse;
import com.redhelmet.alert2me.data.remote.response.RegisterAccountResponse;
import com.redhelmet.alert2me.data.remote.response.Response;

import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.Notification;
import io.reactivex.Observable;
import retrofit2.Retrofit;

public class AppApiHelper implements ApiHelper {
    private ApiService apiService;

    @Inject
    public AppApiHelper(Retrofit retrofit) {
        this.apiService = retrofit.create(ApiService.class);
    }

    @Override
    public Observable<ConfigResponse> getConfig() {
        return filterSuccessResponse(apiService.getConfig());
    }

    @Override
    public Observable<ApiInfo> registerDevice(String firebaseToken) {
        RegisterDeviceRequest request = new RegisterDeviceRequest(firebaseToken);
        return filterSuccessResponse(apiService.registerDevice(request)).map(response -> response.apiInfo);
    }

    @Override
    public Observable<ProximityLocationResponse> putProximityLocation(String userId, ProximityLocationRequest request) {
        return filterSuccessResponse(apiService.putLocation(userId, request));
    }

    @Override
    public Observable<List<Event>> getAllEvents() {
        return filterSuccessResponse(apiService.getAllEvents()).map(response -> response.events);
    }

    @Override
    public Observable<RegisterAccountResponse> registerAccount(User user) {
        return filterSuccessResponse(apiService.registerAccount(user));
    }

    @Override
    public Observable<User> login(String email, String password) {
        HashMap<String, String> requestMap = new HashMap<>();
        requestMap.put("email", email);
        requestMap.put("password", password);
        return filterSuccessResponse(apiService.login(requestMap)).map(response -> response.account);
    }

    @Override
    public Observable<ForgotPasswordResponse> forgotPassword(String email) {
        HashMap<String, String> request = new HashMap<>();
        request.put("email", email);
        return filterSuccessResponse(apiService.forgotPassword(request));
    }

    @Override
    public Observable<User> updateUserProfile(User user) {
        // TODO: call API to get response
        return Observable.just(user);
    }

    private <T extends Response> Observable<T> filterSuccessResponse(Observable<T> response) {
        return response.materialize().map(notification -> {
            if (notification.isOnNext() && !notification.getValue().success) {
                return Notification.createOnError(new Throwable(notification.getValue().errorMessage));
            } else return notification;
        }).dematerialize();
    }
}
