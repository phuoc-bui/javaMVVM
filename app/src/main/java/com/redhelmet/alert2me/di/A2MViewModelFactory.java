package com.redhelmet.alert2me.di;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.annotation.NonNull;

import java.util.Map;

import javax.inject.Inject;
import javax.inject.Provider;

/**
 * ViewModelFactory which uses Dagger to create the instances.
 */
public class A2MViewModelFactory implements ViewModelProvider.Factory {
    private Map<Class<? extends ViewModel>, Provider<ViewModel>> creators;

    @Inject
    public A2MViewModelFactory(Map<Class<? extends ViewModel>, Provider<ViewModel>> creators) {
        this.creators = creators;
    }

    @NonNull
    @Override
    @SuppressWarnings("unchecked")
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        for (Map.Entry<Class<? extends ViewModel>, Provider<ViewModel>> entry : creators.entrySet()) {
            if (modelClass.isAssignableFrom(entry.getKey())) {
                return (T) entry.getValue().get();
            }
        }
        throw new IllegalArgumentException("unknown model class " + modelClass);
    }
}
