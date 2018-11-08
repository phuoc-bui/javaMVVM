package com.redhelmet.alert2me.ui.watchzone;

import com.redhelmet.alert2me.data.model.EditWatchZones;
import com.redhelmet.alert2me.ui.base.BaseViewModel;

import androidx.databinding.ObservableBoolean;
import androidx.databinding.ObservableField;

public class ItemStaticWZViewModel extends BaseViewModel {
    public ObservableField<String> wzName = new ObservableField<>();
    public ObservableBoolean wzEnable = new ObservableBoolean();
    private EditWatchZones watchZone;
    private StaticWZAdapter.OnSwitchCompatCheckChangedListener listener;

    public ItemStaticWZViewModel(EditWatchZones watchZone, StaticWZAdapter.OnSwitchCompatCheckChangedListener listener) {
        this.watchZone = watchZone;
        wzName.set(watchZone.getName());
        wzEnable.set(watchZone.isEnable());
        this.listener = listener;
    }

    public EditWatchZones getWatchZone() {
        return watchZone;
    }

    public void onCheckChanged(boolean checked) {
        if (listener != null) listener.onCheckChanged(watchZone, checked);
    }
}
