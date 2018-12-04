package com.phuocbui.mvvm.ui.base;

import com.phuocbui.mvvm.data.DataManager;
import com.phuocbui.mvvm.data.PreferenceStorage;

public class AppViewModel extends com.phuocbui.basemodule.ui.base.BaseViewModel {

    protected DataManager dataManager;

    protected PreferenceStorage preferenceStorage;

    public AppViewModel() {
        this(null, null);
    }

    public AppViewModel(DataManager dataManager) {
        this(dataManager, null);
    }

    public AppViewModel(PreferenceStorage pref) {
        this(null, pref);
    }

    public AppViewModel(DataManager dataManager, PreferenceStorage pref) {
        this.dataManager = dataManager;
        this.preferenceStorage = pref;
    }
}
