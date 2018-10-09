package com.redhelmet.alert2me.ui.termsandcondition;

import android.net.Uri;

import com.redhelmet.alert2me.data.DataManager;
import com.redhelmet.alert2me.global.Event;
import com.redhelmet.alert2me.ui.base.BaseViewModel;
import com.redhelmet.alert2me.ui.base.NavigationItem;
import com.redhelmet.alert2me.ui.hint.HintsActivity;
import com.redhelmet.alert2me.ui.signin.SignInActivity;

public class TermsConditionViewModel extends BaseViewModel {

    public TermsConditionViewModel(DataManager dataManager) {
        super(dataManager);
    }

    public void onTermsAccept() {
        dataManager.setAccepted(true);
        navigationEvent.setValue(new Event<>(new NavigationItem(NavigationItem.START_ACTIVITY, SignInActivity.class)));
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
