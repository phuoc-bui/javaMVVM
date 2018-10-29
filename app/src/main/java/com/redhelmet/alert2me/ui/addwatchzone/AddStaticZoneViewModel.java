package com.redhelmet.alert2me.ui.addwatchzone;

import com.redhelmet.alert2me.R;
import com.redhelmet.alert2me.data.DataManager;
import com.redhelmet.alert2me.ui.base.BaseViewModel;
import com.redhelmet.alert2me.ui.base.NavigationItem;

import javax.inject.Inject;

import androidx.databinding.ObservableField;
import androidx.databinding.ObservableInt;
import androidx.databinding.ObservableLong;

public class AddStaticZoneViewModel extends BaseViewModel {

    public enum Step {
        EDIT_NAME,
        EDIT_LOCATION,
        EDIT_NOTIFICATION
    }

    private Step currentStep = Step.EDIT_NAME;

    public WatchZoneModel watchZoneModel = new WatchZoneModel();

    @Inject
    public AddStaticZoneViewModel(DataManager dataManager) {
        super(dataManager);
    }

    public void onNextClick() {
        NavigationItem destination = null;
        switch (currentStep) {
            case EDIT_NAME:
                if (watchZoneModel.validateName()) {
                    currentStep = Step.EDIT_LOCATION;
                    destination = new NavigationItem(NavigationItem.CHANGE_FRAGMENT_AND_ADD_TO_BACK_STACK, new EditStaticZoneLocationFragment());
                } else {
                    destination = new NavigationItem(NavigationItem.SHOW_TOAST, R.string.msg_wz_name_not_valid);
                }
                break;
            case EDIT_LOCATION:
                currentStep = Step.EDIT_NOTIFICATION;
                destination = new NavigationItem(NavigationItem.CHANGE_FRAGMENT_AND_ADD_TO_BACK_STACK, new EditStaticZoneNotificationFragment());
                break;
        }
        if (destination != null) navigateTo(destination);
    }

    public void onBackClick() {
        switch (currentStep) {
            case EDIT_NOTIFICATION:
                currentStep = Step.EDIT_LOCATION;
                break;
            case EDIT_LOCATION:
                currentStep = Step.EDIT_NAME;
                break;
        }
        navigateTo(new NavigationItem(NavigationItem.POP_FRAGMENT_BACK));
    }

    public void onSaveClick() {

    }

    public static class WatchZoneModel {
        public ObservableField<String> name = new ObservableField<>("");
        public ObservableField<String> sound = new ObservableField<>("Default");
        public ObservableInt radius = new ObservableInt(0);
        public ObservableField<String> geomType = new ObservableField<>("");
        public ObservableLong lngLocation = new ObservableLong();
        public ObservableLong latLocation = new ObservableLong();

        public boolean validateName() {
            return !name.get().matches("^\\s*$") && name.get().trim().length() > 0;
        }
    }
}
