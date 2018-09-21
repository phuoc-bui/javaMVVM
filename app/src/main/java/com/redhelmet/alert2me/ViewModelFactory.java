package com.redhelmet.alert2me;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import com.redhelmet.alert2me.data.DataManager;
import com.redhelmet.alert2me.ui.splash.SplashViewModel;

public class ViewModelFactory implements ViewModelProvider.Factory {
    private static ViewModelFactory INSTANCE;
    private DataManager dataManager;

    private ViewModelFactory() {
        dataManager = ServiceLocator.get(DataManager.class);
        
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
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if(SplashViewModel.class.equals(modelClass)) {
            return (T) new SplashViewModel(dataManager);
        }
        else {
            throw new Error("Invalid parameter");
        }
    }
}
