package com.redhelmet.alert2me.ui.eventfilter;

import com.redhelmet.alert2me.data.DataManager;
import com.redhelmet.alert2me.ui.base.BaseViewModel;

public class EventFilterViewModel extends BaseViewModel {

    public EventFilterViewModel(DataManager dataManager) {
        super(dataManager);
    }

    public boolean isDefaultFilter() {
        return dataManager.isDefaultFilter();
    }
}
