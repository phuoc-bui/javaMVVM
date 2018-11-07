package com.redhelmet.alert2me.ui.addwatchzone;

import com.google.android.gms.maps.model.LatLng;
import com.redhelmet.alert2me.R;
import com.redhelmet.alert2me.data.DataManager;
import com.redhelmet.alert2me.data.model.EditWatchZones;
import com.redhelmet.alert2me.data.model.EventGroup;
import com.redhelmet.alert2me.data.model.Geometry;
import com.redhelmet.alert2me.global.RxProperty;
import com.redhelmet.alert2me.ui.base.BaseViewModel;
import com.redhelmet.alert2me.ui.base.NavigationItem;
import com.redhelmet.alert2me.ui.eventfilter.defaultfilter.DefaultFilterViewModel;
import com.redhelmet.alert2me.ui.eventfilter.defaultfilter.EventGroupItemViewModel;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;
import javax.inject.Inject;

import androidx.databinding.ObservableField;
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

    enum GeometryType {
        CIRCLE, POLYGON
    }

    public MutableLiveData<Step> currentStep = new MutableLiveData<>();

    public WatchZoneModel watchZoneModel = new WatchZoneModel();

    private DefaultFilterViewModel filterViewModel;

    @Inject
    public AddStaticZoneViewModel(DataManager dataManager) {
        super(dataManager);
        currentStep.setValue(Step.EDIT_NAME);
    }

    public void setWatchZone(EditWatchZones watchZone) {
        watchZoneModel = new WatchZoneModel(watchZone);
    }

    public void setFilterViewModel(DefaultFilterViewModel filterViewModel) {
        this.filterViewModel = filterViewModel;
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
                if (watchZoneModel.validateLocation()) {
                    currentStep.setValue(Step.EDIT_NOTIFICATION);
                    destination = new NavigationItem(NavigationItem.CHANGE_FRAGMENT_AND_ADD_TO_BACK_STACK, new EditStaticZoneNotificationFragment());
                } else {
                    destination = new NavigationItem(NavigationItem.SHOW_TOAST, R.string.msg_wz_location_not_valid);
                }
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
        if (filterViewModel != null)
            watchZoneModel.updateFilter(filterViewModel.adapter.itemsSource);
        if (watchZoneModel.mode == Mode.ADD) {
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
                    .subscribe(o -> {
                        navigateTo(new NavigationItem(NavigationItem.SHOW_TOAST, R.string.msg_updatedWZ));
                        navigateTo(new NavigationItem(NavigationItem.FINISH));
                    }, this::handleError));
        }
    }

    public void setRingSound(String ringSoundUri) {
        watchZoneModel.sound.set(ringSoundUri);
    }

    public boolean isDefaultFilter() {
        return dataManager.isDefaultFilter();
    }


    public static class WatchZoneModel {
        public ObservableField<String> name = new ObservableField<>("");
        public ObservableField<String> sound = new ObservableField<>("Default");
        public RxProperty<Integer> radius = new RxProperty<>(5);
        public List<LatLng> points = new ArrayList<>();
        public List<Integer> groupIds = new ArrayList<>();
        private Geometry geometry;
        public String type;
        public MutableLiveData<GeometryType> geometryType = new MutableLiveData<>();
        public Mode mode = Mode.ADD;

        private EditWatchZones watchZones;

        public WatchZoneModel() {
            this(null);
        }

        public WatchZoneModel(@Nullable EditWatchZones init) {
            if (init == null) {
                mode = Mode.ADD;
                init = new EditWatchZones();
            } else {
                mode = Mode.EDIT;
            }
            watchZones = init;
            name.set(init.getName());
            sound.set(init.getSound());
            radius.set(init.getRadius());
            geometry = init.getGeom();
            type = init.getWzType();
            groupIds = init.getFilterGroupId();
            points = convertPointsFromGeometry(init.getGeom());
            geometryType.setValue(isCircle() ? GeometryType.CIRCLE : GeometryType.POLYGON);
        }

        public EditWatchZones getWatchZones(boolean updated) {
            if (updated) {
                watchZones.setName(name.get());
                watchZones.setSound(sound.get());
                watchZones.setRadius(radius.get());
                geometry.setCoordinates(convertPointsCoordinates(points));
                watchZones.setGeom(geometry);
                watchZones.setWzType(type);
                watchZones.setFilterGroupId(groupIds);
            }
            return watchZones;
        }

        public boolean validateName() {
            return !name.get().matches("^\\s*$") && name.get().trim().length() > 0;
        }

        public boolean validateLocation() {
            return points.size() > 0;
        }

        public boolean isCircle() {
            return type.equals(EditWatchZones.CIRCLE_TYPE);
        }

        public void clearGeometry() {
            points.clear();
            type = "";
            radius.set(5);
        }

        public void changeGeometryType(GeometryType type) {
            if (geometry == null) geometry = new Geometry();
            clearGeometry();
            switch (type) {
                case CIRCLE:
                    geometry.setType(Geometry.POINT_TYPE);
                    this.type = EditWatchZones.CIRCLE_TYPE;
                    break;
                case POLYGON:
                    geometry.setType(Geometry.POLYGON_TYPE);
                    this.type = EditWatchZones.POLYGON_TYPE;
                    radius.set(0);
                    break;
            }

            geometryType.setValue(type);
        }

        private void updateFilter(List<EventGroupItemViewModel> itemList) {
            if (groupIds == null) groupIds = new ArrayList<>();
            else groupIds.clear();
            for (EventGroupItemViewModel item : itemList) {
                EventGroup eventGroup = item.eventGroup.getValue();
                if (eventGroup != null && eventGroup.isFilterOn())
                    groupIds.add((int) eventGroup.getId());
            }
        }

        private List<LatLng> convertPointsFromGeometry(Geometry geometry) {
            List<LatLng> points = new ArrayList<>();
            if (geometry != null && geometry.getCoordinates() != null) {
                double[][][] coordinates = geometry.getCoordinates();
                for (int i = 0; i < coordinates[0].length; i++) {
                    double lat = coordinates[0][i][1];
                    double lng = coordinates[0][i][0];
                    LatLng point = new LatLng(lat, lng);
                    points.add(point);
                }
            }
            return points;
        }

        private double[][][] convertPointsCoordinates(List<LatLng> points) {
            double[][][] coordinates = new double[1][points.size()][2];
            for (int i = 0; i < points.size(); i++) {
                coordinates[0][i][1] = points.get(i).latitude;
                coordinates[0][i][0] = points.get(i).longitude;
            }
            return coordinates;
        }
    }
}
