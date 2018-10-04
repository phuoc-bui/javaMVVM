package com.redhelmet.alert2me.ui.termsandcondition;

import android.net.Uri;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.redhelmet.alert2me.data.DataManager;
import com.redhelmet.alert2me.global.Event;
import com.redhelmet.alert2me.ui.base.BaseViewModel;
import com.redhelmet.alert2me.ui.base.NavigationItem;
import com.redhelmet.alert2me.ui.hint.HintsActivity;
import com.redhelmet.alert2me.ui.home.HomeActivity;

import io.reactivex.android.schedulers.AndroidSchedulers;

public class TermsConditionViewModel extends BaseViewModel {

    public TermsConditionViewModel(DataManager dataManager) {
        super(dataManager);
    }

    public void onTermsAccept() {
        isLoading.set(true);
        FirebaseInstanceId.getInstance()
                .getInstanceId()
                .addOnSuccessListener(instanceIdResult -> disposeBag.add(dataManager.getUserId(instanceIdResult.getToken())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(registerResponse -> onLoadFinish(), error -> onLoadFinish())));
        FirebaseInstanceId.getInstance().getInstanceId().addOnFailureListener(e -> {
            isLoading.set(false);
            Log.e("FirebaseInstanceId", "Fail to get firebase token: " + e.getMessage());
        });
    }

    private void onLoadFinish() {
        dataManager.setAccepted(true);
        disposeBag.add(dataManager.getEventGroups()
                .subscribe(eventGroups -> {
                    isLoading.set(false);
                    dataManager.saveUserDefaultFilters(eventGroups);
                    navigationEvent.postValue(new Event<>(new NavigationItem(NavigationItem.START_ACTIVITY_AND_FINISH, HomeActivity.class)));
                }));
    }

    public void onShowTermsCondition() {
        String url = dataManager.getAppConfig().getTermsAndConditionUrl();
        navigationEvent.setValue(new Event<>(new NavigationItem(NavigationItem.START_WEB_VIEW, Uri.parse(url))));
    }

    public void onReplayHint() {
        dataManager.setInitialLaunch(false);
        navigationEvent.setValue(new Event<>(new NavigationItem(NavigationItem.START_ACTIVITY_AND_FINISH, HintsActivity.class)));
    }
}
