package com.redhelmet.alert2me.ui.termsandcondition;

import android.net.Uri;

import com.redhelmet.alert2me.data.DataManager;
import com.redhelmet.alert2me.ui.base.BaseViewModel;
import com.redhelmet.alert2me.ui.base.NavigationItem;
import com.redhelmet.alert2me.ui.signin.SignInActivity;

import javax.inject.Inject;

public class TermsConditionViewModel extends BaseViewModel {

    @Inject
    public TermsConditionViewModel(DataManager dataManager) {
        super(dataManager);
    }

    public void onTermsAccept() {
        dataManager.setAccepted(true);
        navigateTo(new NavigationItem(NavigationItem.START_ACTIVITY, SignInActivity.class));
    }

    public void onShowTermsCondition() {
//        String url = dataManager.getAppConfig().getTermsAndConditionUrl();
        String url = "https://a2m.cloud/";
        navigateTo(new NavigationItem(NavigationItem.START_WEB_VIEW, Uri.parse(url)));
    }
}
