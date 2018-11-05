package com.redhelmet.alert2me.ui.addwatchzone;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polygon;
import com.redhelmet.alert2me.R;
import com.redhelmet.alert2me.data.DataManager;
import com.redhelmet.alert2me.data.model.EditWatchZones;
import com.redhelmet.alert2me.data.model.Geometry;
import com.redhelmet.alert2me.ui.base.BaseViewModel;
import com.redhelmet.alert2me.ui.base.NavigationItem;

import java.util.List;

import javax.inject.Inject;

import androidx.databinding.ObservableField;
import androidx.databinding.ObservableInt;
import androidx.lifecycle.MutableLiveData;
import io.reactivex.android.schedulers.AndroidSchedulers;

public class AddStaticZoneViewModel extends BaseViewModel {

    public enum Step {
        EDIT_NAME,
        EDIT_LOCATION,
        EDIT_NOTIFICATION
    }

    public enum Mode {
        EDIT, ADD
    }

    public MutableLiveData<Step> currentStep = new MutableLiveData<>();
    public Mode mode = Mode.ADD;

    public WatchZoneModel watchZoneModel = new WatchZoneModel();

    @Inject
    public AddStaticZoneViewModel(DataManager dataManager) {
        super(dataManager);
        currentStep.setValue(Step.EDIT_NAME);
    }

    public void setWatchZone(EditWatchZones watchZone) {
        watchZoneModel = new WatchZoneModel(watchZone);
        mode = Mode.EDIT;
    }

    public void onNextClick() {
        NavigationItem destination = null;
        switch (currentStep.getValue()) {
            case EDIT_NAME:
                if (watchZoneModel.validateName()) {
                    currentStep.setValue(Step.EDIT_LOCATION);
                    destination = new NavigationItem(NavigationItem.CHANGE_FRAGMENT_AND_ADD_TO_BACK_STACK, new EditStaticZoneLocationFragment());
                } else {
                    destination = new NavigationItem(NavigationItem.SHOW_TOAST, R.string.msg_wz_name_not_valid);
                }
                break;
            case EDIT_LOCATION:
                currentStep.setValue(Step.EDIT_NOTIFICATION);
                destination = new NavigationItem(NavigationItem.CHANGE_FRAGMENT_AND_ADD_TO_BACK_STACK, new EditStaticZoneNotificationFragment());
                break;
        }
        if (destination != null) navigateTo(destination);
    }

    public void onBackClick() {
        switch (currentStep.getValue()) {
            case EDIT_NOTIFICATION:
                currentStep.setValue(Step.EDIT_LOCATION);
                break;
            case EDIT_LOCATION:
                currentStep.setValue(Step.EDIT_NAME);
                break;
        }
        navigateTo(new NavigationItem(NavigationItem.POP_FRAGMENT_BACK));
    }

    public void onSaveClick() {
        if (mode == Mode.ADD) {
            disposeBag.add(dataManager.addWatchZone(watchZoneModel.getWatchZones(true))
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(o -> {
                        navigateTo(new NavigationItem(NavigationItem.SHOW_TOAST, R.string.msg_createdWZ));
                        navigateTo(new NavigationItem(NavigationItem.FINISH));
                    }, e -> {
                        navigateTo(new NavigationItem(NavigationItem.SHOW_TOAST, e.getMessage()));
                        navigateTo(new NavigationItem(NavigationItem.FINISH));
                    }));
        } else {
            disposeBag.add(dataManager.editWatchZone(watchZoneModel.getWatchZones(true))
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe());
        }
    }

    public void setRingSound(String ringSoundUri) {
        watchZoneModel.sound.set(ringSoundUri);
    }

    public boolean isDefaultFilter() {
        return dataManager.isDefaultFilter();
    }

    public void savePolygon(Polygon polygon) {
        if (polygon != null) {
            List<LatLng> points = polygon.getPoints();
            double[][][] coordinates = new double[1][points.size()][2];
            for (int i = 0; i < points.size(); i++) {
                coordinates[0][i][0] = points.get(i).latitude;
                coordinates[0][i][1] = points.get(i).longitude;
            }

            Geometry geometry = new Geometry();
            geometry.setType("POLYGON");
            geometry.setCoordinates(coordinates);
            watchZoneModel.geom.setValue(geometry);
            watchZoneModel.type = "VARIABLE";
            watchZoneModel.radius.set(0);
        }
    }

    public void clearGeometry() {
        watchZoneModel.geom.setValue(null);
        watchZoneModel.type = "";
        watchZoneModel.radius.set(0);
    }

    public void saveCircle(double lat, double lng, int radius) {
        double[][][] coordinates = new double[1][1][2];
        coordinates[0][0][0] = lat;
        coordinates[0][0][1] = lng;

        Geometry geometry = new Geometry();
        geometry.setType("POINT");
        geometry.setCoordinates(coordinates);
        watchZoneModel.geom.setValue(geometry);
        watchZoneModel.type = "STANDARD";
        watchZoneModel.radius.set(radius);
    }

    public static class WatchZoneModel {
        public ObservableField<String> name = new ObservableField<>("");
        public ObservableField<String> sound = new ObservableField<>("Default");
        public ObservableInt radius = new ObservableInt(0);
        public MutableLiveData<Geometry> geom = new MutableLiveData<>();
        public String type;

        private EditWatchZones watchZones;

        public WatchZoneModel() {
            watchZones = new EditWatchZones();
        }

        public WatchZoneModel(EditWatchZones init) {
            watchZones = init;
            name.set(init.getName());
            sound.set(init.getSound());
            radius.set(init.getRadius());
            geom.setValue(init.getGeom());
        }

        public EditWatchZones getWatchZones(boolean updated) {
            if (updated) {
                watchZones.setName(name.get());
                watchZones.setSound(sound.get());
                watchZones.setRadius(radius.get());
                watchZones.setGeom(geom.getValue());
                watchZones.setWzType(type);
            }
            return watchZones;
        }

        public boolean validateName() {
            return !name.get().matches("^\\s*$") && name.get().trim().length() > 0;
        }

        public boolean isCircle() {
            return type.equals("STANDARD");
        }
    }
}
