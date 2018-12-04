package com.phuocbui.mvvm.ui.home;

import com.phuocbui.mvvm.data.DataManager;
import com.phuocbui.basemodule.ui.base.BaseViewModel;

import javax.inject.Inject;

public class HomeViewModel extends BaseViewModel {


    @Inject
    public HomeViewModel(DataManager dataManager) {
        super(dataManager);
    }

}
