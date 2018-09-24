package com.redhelmet.alert2me.ui.termsandcondition;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.redhelmet.alert2me.data.DataManager;
import com.redhelmet.alert2me.global.Event;
import com.redhelmet.alert2me.ui.activity.HomeActivity;
import com.redhelmet.alert2me.ui.base.BaseViewModel;
import com.redhelmet.alert2me.ui.hint.HintsActivity;

import io.reactivex.android.schedulers.AndroidSchedulers;

public class TermsConditionViewModel extends BaseViewModel {

    protected TermsConditionViewModel(DataManager dataManager) {
        super(dataManager);
    }

    public void onTermsAccept() {
       isLoading = true;
        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(instanceIdResult -> {
            disposeBag.add(dataManager.getUserId(instanceIdResult.getToken())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(registerResponse -> {
                        isLoading = false;
                        dataManager.setAccepted(true);
                        navigationEvent.setValue(new Event<>(HomeActivity.class));
                    }, error -> {
                        isLoading = false;
                        dataManager.setAccepted(true);
                        navigationEvent.setValue(new Event<>(HomeActivity.class));
                    }));
        });
        FirebaseInstanceId.getInstance().getInstanceId().addOnFailureListener(e -> {
            isLoading = false;
            Log.e("FirebaseInstanceId", "Fail to get firebase token: " + e.getMessage());
        });
    }

    public void onShowTermsCondition() {
        navigationEvent.setValue(new Event<>(dataManager.getConfig().appConfig.getTermsAndConditionUrl()));
    }

    public void onReplayHint() {
        dataManager.setInitialLaunch(false);
        navigationEvent.setValue(new Event<>(HintsActivity.class));
    }
}
