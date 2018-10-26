package com.redhelmet.alert2me.ui.watchzone;

import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;

import com.redhelmet.alert2me.data.model.EditWatchZones;
import com.redhelmet.alert2me.ui.base.BaseViewModel;

public class ItemStaticWZViewModel extends BaseViewModel {
    public ObservableField<String> wzName = new ObservableField<>();
    public ObservableBoolean wzEnable = new ObservableBoolean();

    public ItemStaticWZViewModel(EditWatchZones watchZone) {
        wzName.set(watchZone.getWatchzoneName());
        wzEnable.set(watchZone.isWzEnable());
    }
}
