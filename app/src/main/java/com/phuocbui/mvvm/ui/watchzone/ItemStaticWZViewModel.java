package com.phuocbui.mvvm.ui.watchzone;

import com.phuocbui.mvvm.data.DataManager;
import com.phuocbui.mvvm.data.model.EditWatchZones;
import com.phuocbui.mvvm.ui.base.BaseViewModel;

import androidx.databinding.ObservableBoolean;
import androidx.databinding.ObservableField;
import io.reactivex.android.schedulers.AndroidSchedulers;

public class ItemStaticWZViewModel extends BaseViewModel {
    public ObservableField<String> wzName = new ObservableField<>();
    public ObservableBoolean wzEnable = new ObservableBoolean();
    private EditWatchZones watchZone;
    private StaticWZAdapter.OnItemClickListener listener;

    public ItemStaticWZViewModel(DataManager dataManager, EditWatchZones watchZone, StaticWZAdapter.OnItemClickListener listener) {
        super(dataManager);
        this.watchZone = watchZone;
        wzName.set(watchZone.getName());
        wzEnable.set(watchZone.isEnable());
        this.listener = listener;
    }

    public EditWatchZones getWatchZone() {
        return watchZone;
    }

    public void onCheckChanged(boolean checked) {
        isLoading.set(true);
        disposeBag.add(dataManager.enableWatchZone(watchZone.getId(), checked)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(o -> isLoading.set(false), e -> {
                    isLoading.set(false);
                    handleError(e);
                }));
    }

    public void onItemClick() {
        if (listener != null) listener.onItemClick(watchZone);
    }
}
