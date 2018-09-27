package com.redhelmet.alert2me.ui.home.event;

import com.redhelmet.alert2me.data.DataManager;
import com.redhelmet.alert2me.ui.base.BaseViewModel;

public class MapViewModel extends BaseViewModel {
    public EventViewModel parent;

    public MapViewModel(DataManager dataManager) {
        super(dataManager);
    }
}
