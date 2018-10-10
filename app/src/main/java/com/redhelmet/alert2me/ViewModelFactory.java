package com.redhelmet.alert2me;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import com.redhelmet.alert2me.data.DataManager;
import com.redhelmet.alert2me.data.PreferenceHelper;
import com.redhelmet.alert2me.ui.eventdetail.EventDetailViewModel;
import com.redhelmet.alert2me.ui.eventfilter.custom.CustomFilterViewModel;
import com.redhelmet.alert2me.ui.eventfilter.defaultfilter.DefaultFilterViewModel;
import com.redhelmet.alert2me.ui.hint.HintViewModel;
import com.redhelmet.alert2me.ui.home.HomeViewModel;
import com.redhelmet.alert2me.ui.home.event.ClusterEventsViewModel;
import com.redhelmet.alert2me.ui.home.event.EventViewModel;
import com.redhelmet.alert2me.ui.signin.ForgotPasswordViewModel;
import com.redhelmet.alert2me.ui.signin.LoginViewModel;
import com.redhelmet.alert2me.ui.signin.RegisterViewModel;
import com.redhelmet.alert2me.ui.signin.SignInViewModel;
import com.redhelmet.alert2me.ui.splash.SplashViewModel;
import com.redhelmet.alert2me.ui.termsandcondition.TermsConditionViewModel;
import com.redhelmet.alert2me.ui.eventfilter.EventFilterViewModel;

public class ViewModelFactory implements ViewModelProvider.Factory {
    private static ViewModelFactory INSTANCE;
    private DataManager dataManager;
    private PreferenceHelper pref;

    private ViewModelFactory() {
        AppModule appModule = AppModule.getInstance();
        dataManager = appModule.provideDataManager();
        pref = appModule.providePreferenceHelper();
    }

    public static ViewModelFactory getInstance() {
        if (INSTANCE == null) {
            synchronized (ViewModelFactory.class) {
                if (INSTANCE == null) {
                    INSTANCE = new ViewModelFactory();
                }
            }
        }
        return INSTANCE;
    }

    @NonNull
    @SuppressWarnings("unchecked")
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (SplashViewModel.class.equals(modelClass)) {
            return (T) new SplashViewModel(dataManager, pref);
        } else if (HintViewModel.class.equals(modelClass)) {
            return (T) new HintViewModel(dataManager);
        } else if (TermsConditionViewModel.class.equals(modelClass)) {
            return (T) new TermsConditionViewModel(dataManager);
        } else if (HomeViewModel.class.equals(modelClass)) {
            return (T) new HomeViewModel(dataManager);
        } else if (EventViewModel.class.equals(modelClass)) {
            return (T) new EventViewModel(dataManager);
        } else if (ClusterEventsViewModel.class.equals(modelClass)) {
            return (T) new ClusterEventsViewModel();
        } else if (EventDetailViewModel.class.equals(modelClass)) {
            return (T) new EventDetailViewModel();
        } else if (EventFilterViewModel.class.equals(modelClass)) {
            return (T) new EventFilterViewModel(dataManager);
        } else if (DefaultFilterViewModel.class.equals(modelClass)) {
            return (T) new DefaultFilterViewModel(dataManager);
        } else if (CustomFilterViewModel.class.equals(modelClass)) {
            return (T) new CustomFilterViewModel(dataManager);
        } else if (SignInViewModel.class.equals(modelClass)) {
            return (T) new SignInViewModel(pref);
        } else if (LoginViewModel.class.equals(modelClass)) {
            return (T) new LoginViewModel(dataManager);
        } else if (RegisterViewModel.class.equals(modelClass)) {
            return (T) new RegisterViewModel(dataManager);
        } else if (ForgotPasswordViewModel.class.equals(modelClass)) {
            return (T) new ForgotPasswordViewModel(dataManager);
        } else {
            throw new Error("Invalid parameter");
        }
    }
}
