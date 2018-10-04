package com.redhelmet.alert2me;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import com.redhelmet.alert2me.data.DataManager;
import com.redhelmet.alert2me.ui.eventdetail.EventDetailViewModel;
import com.redhelmet.alert2me.ui.hint.HintViewModel;
import com.redhelmet.alert2me.ui.home.HomeViewModel;
import com.redhelmet.alert2me.ui.home.event.ClusterEventsViewModel;
import com.redhelmet.alert2me.ui.home.event.EventViewModel;
import com.redhelmet.alert2me.ui.splash.SplashViewModel;
import com.redhelmet.alert2me.ui.termsandcondition.TermsConditionViewModel;

public class ViewModelFactory implements ViewModelProvider.Factory {
    private static ViewModelFactory INSTANCE;
    private DataManager dataManager;
    private AppModule appModule;

    private ViewModelFactory() {
        appModule = AppModule.getInstance();
        dataManager = appModule.provideDataManager();
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
            return (T) new SplashViewModel(dataManager);
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
        } else {
            throw new Error("Invalid parameter");
        }
    }
}
