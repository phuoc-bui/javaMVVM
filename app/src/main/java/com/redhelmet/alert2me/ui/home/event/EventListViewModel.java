package com.redhelmet.alert2me.ui.home.event;

import com.redhelmet.alert2me.data.DataManager;
import com.redhelmet.alert2me.ui.base.BaseViewModel;

public class EventListViewModel extends BaseViewModel {
    public EventViewModel parent;
    public EventListViewModel(DataManager dataManager) {
        super(dataManager);
    }
}
