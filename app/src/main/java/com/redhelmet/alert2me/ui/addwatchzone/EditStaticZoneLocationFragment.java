package com.redhelmet.alert2me.ui.addwatchzone;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.places.Place;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.maps.android.PolyUtil;
import com.google.maps.android.SphericalUtil;
import com.google.maps.android.ui.IconGenerator;
import com.redhelmet.alert2me.BuildConfig;
import com.redhelmet.alert2me.R;
import com.redhelmet.alert2me.autocomplete.AutoCompleteLocation;
import com.redhelmet.alert2me.data.model.EditWatchZones;
import com.redhelmet.alert2me.data.model.Geometry;
import com.redhelmet.alert2me.data.model.WatchZoneGeom;
import com.redhelmet.alert2me.databinding.FragmentEditStaticZoneLocationBinding;
import com.redhelmet.alert2me.ui.activity.AddStaticZoneNotification;
import com.redhelmet.alert2me.ui.base.BaseFragment;
import com.redhelmet.alert2me.util.MapUtil;
import com.savvyapps.togglebuttonlayout.ToggleButtonLayout;

import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.lifecycle.ViewModelProvider;

public class EditStaticZoneLocationFragment extends BaseFragment<AddStaticZoneViewModel, FragmentEditStaticZoneLocationBinding> implements GoogleMap.OnMapLongClickListener, AutoCompleteLocation.AutoCompleteLocationListener, OnMapReadyCallback {

    @Inject
    ViewModelProvider.Factory factory;

    enum GeometryType {
        CIRCLE, POLYGON
    }

    String fill_wz_color = "#8Ce0701e";//"#8C8EB9E8";
    Intent i;
    GoogleMap googleMap;
    //    Button circleBtn, polygonBtn;
    private ToggleButtonLayout toggleGeometryType;
    private GeometryType geometryType = GeometryType.CIRCLE;
    LinearLayout radiusLinear;
    LinearLayout wz_loc_inst_layout;
    TextView loc_info;
    private Circle currentCircle = null;
    private Polygon _polygon;
    private ArrayList<LatLng> points;
    private ArrayList<Marker> markers;
    private DiscreteSeekBar _discreteSeekBar;
    Marker titleMarker;
    TextView txtRadius;
    int _radius = 5;
    private int seekRadius = 5;
    public String wz_name = null;
    ArrayList<EditWatchZones> wzData;
    int position = 0;
    boolean editMode;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_edit_static_zone_location;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        obtainViewModel(factory, AddStaticZoneViewModel.class);

        initializeControls();
        initializeMap();

    }

    public void initializeMap() {
        if (viewModel.mode == AddStaticZoneViewModel.Mode.EDIT) {
            if (wzData.get(position).getWzType().equalsIgnoreCase("STANDARD")) {
                viewSetting(GeometryType.CIRCLE);
            } else {
                viewSetting(GeometryType.POLYGON);
            }
        }

        points = new ArrayList<>();
        markers = new ArrayList<>();
        binder.autocompleteLocation.setAutoCompleteTextListener(this);

        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.locationMap);
        mapFragment.getMapAsync(this);

        getBaseActivity().hideKeyboard();
    }


    public void initializeControls() {
        radiusLinear = binder.radiusLayout;
        wz_loc_inst_layout = binder.wzLocInstLayout;
        loc_info = binder.wzLocInst;
        _discreteSeekBar = binder.radiusSeek;
        toggleGeometryType = binder.toggleGeometry;
        toggleGeometryType.setToggled(geometryType == GeometryType.CIRCLE ? R.id.toggle_circle : R.id.toggle_polygon, true);
        toggleGeometryType.setOnToggledListener((toggle, selected) -> {
            switch (toggle.getId()) {
                case R.id.toggle_circle:
                    geometryType = GeometryType.CIRCLE;
                    if (isPolygonAdded())
                        showWzChangeAlert(getString(R.string.wz_change_heading_circle), getString(R.string.wz_change_text_circle), false);
                    else {
                        viewSetting(GeometryType.CIRCLE);
                    }
                    break;
                case R.id.toggle_polygon:
                    geometryType = GeometryType.CIRCLE;
                    if (isCircleAdded())
                        showWzChangeAlert(getString(R.string.wz_change_heading_custom), getString(R.string.wz_change_text_custom), true);
                    else {
                        viewSetting(GeometryType.POLYGON);
                    }

                    break;
            }
            return null;
        });
//        circleBtn.setOnClickListener(this);
//        polygonBtn.setOnClickListener(this);
        txtRadius = binder.txtRadius;
        setupSliderValue();

        _discreteSeekBar.setOnProgressChangeListener(new DiscreteSeekBar.OnProgressChangeListener() {
            @Override
            public void onProgressChanged(DiscreteSeekBar seekBar, int radius, boolean fromUser) {
                if (currentCircle != null) {
                    _radius = radius * 100;
                    seekRadius = radius;

                    setupSliderValue();
                    currentCircle.setRadius(_radius);
                    AdjustZoom();
                }
            }

            @Override
            public void onStartTrackingTouch(DiscreteSeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(DiscreteSeekBar seekBar) {

            }
        });
    }

    void setupSliderValue() {

        txtRadius.setText(getString(R.string.txtRadiusValue) + " " + seekRadius + getString(R.string.txtRadiusKM));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.clear_btn:
                if (geometryType == GeometryType.CIRCLE) {
                    if (isCircleAdded()) {
                        //checking is circle is on map
                        deleteWz(getString(R.string.delete_circle_heading), getString(R.string.delete_circle_text), true);
                    } else {
                        Toast.makeText(getBaseActivity(), R.string.delete_circle_message, Toast.LENGTH_LONG).show();
                    }
                } else {
                    //for polygon
                    if (isPolygonAdded()) //checking polygon on map or not
                    {
                        deleteWz(getString(R.string.delete_poly_heading), getString(R.string.delete_poly_message), false);
                    } else {
                        Toast.makeText(getBaseActivity(), R.string.delete_polygon_message, Toast.LENGTH_LONG).show();
                    }
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onMapLongClick(LatLng latLng) {
        switch (geometryType) {
            case CIRCLE:
                addCircle(latLng);
                break;
            case POLYGON:
                addPointToPolygon(latLng);
                break;
        }
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;

        this.googleMap.setOnMapLoadedCallback(() -> {
            //Your code where exception occurs goes here...
            LatLng latLng = new LatLng(BuildConfig.DEFAULT_LOCATION_LAT, BuildConfig.DEFAULT_LOCATION_LNG);
            this.googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 3.5f));

            this.googleMap.setOnMapLongClickListener(EditStaticZoneLocationFragment.this);

            if (editMode) {
                Geometry geometry = viewModel.watchZoneModel.geom.getValue();
                if (geometry != null) {
                    if (viewModel.watchZoneModel.isCircle()) {
                            double[][][] coordinates = geometry.getCoordinates();
                            if (coordinates != null) {
                                double lat = coordinates[0][0][0];
                                double lng = coordinates[0][0][1];
                                LatLng center = new LatLng(lat, lng);
                                addCircle(center);
                            }
                    } else {
                        createPolygon();
                        EditModeAddPolygon(wzData.get(position).getWatchZoneGeoms());
                    }
                }

            }
        });


    }


    private void setWzTitle(LatLng center) {
        TextView text = new TextView(getBaseActivity());
        text.setText(wz_name);
        text.setPadding(10, 10, 10, 10);
        text.setTextColor(Color.WHITE);
        IconGenerator generator = new IconGenerator(getBaseActivity());
//        generator.setBackground(context.getResources().getDrawable(R.drawable.bubble_mask));
        generator.setContentView(text);
        Bitmap icon = generator.makeIcon();
        titleMarker = googleMap.addMarker(new MarkerOptions().position(center).icon(BitmapDescriptorFactory.fromBitmap(icon)));
    }

    private boolean removeCurrentCircle() {
        if (currentCircle != null) {
            currentCircle.remove();
            if (titleMarker != null) {
                titleMarker.remove();
            }
            currentCircle = null;
            titleMarker = null;
            viewModel.clearGeometry();
            return true;
        }
        return false;
    }


    private void AdjustZoom() {
        LatLng center = currentCircle.getCenter();
        double radius = currentCircle.getRadius();
        LatLng targetNorthEast = SphericalUtil.computeOffset(center, radius * Math.sqrt(2), 45);
        LatLng targetSouthWest = SphericalUtil.computeOffset(center, radius * Math.sqrt(2), 225);
        LatLngBounds latLngBounds = new LatLngBounds(targetSouthWest, targetNorthEast);

        googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds, 45));
    }

    @Override
    public void onTextClear() {

    }

    @Override
    public void onItemSelected(Place selectedPlace) {


        LatLng latLng = new LatLng(selectedPlace.getLatLng().latitude, selectedPlace.getLatLng().longitude);
        googleMap.addMarker(new MarkerOptions().position(latLng));
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 55));
    }


    private LatLngBounds getPolygonBounds(List<LatLng> polygonPointsList) {
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (int i = 0; i < polygonPointsList.size(); i++) {
            builder.include(polygonPointsList.get(i));
        }
        return builder.build();
    }

    private void addMarker(LatLng latLng) {
        Drawable drawable = getResources().getDrawable(R.drawable.icon_map_pin);
        BitmapDescriptor icon = getMarkerIconFromDrawable(drawable);
        Marker marker = googleMap.addMarker(new MarkerOptions().position(latLng).icon(icon).zIndex(-1));
        markers.add(marker);
    }

    private BitmapDescriptor getMarkerIconFromDrawable(Drawable drawable) {
        Canvas canvas = new Canvas();
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        canvas.setBitmap(bitmap);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        drawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    private void createPolygon() {
        if (points.size() > 2) {
            wz_loc_inst_layout.setVisibility(View.INVISIBLE);

            if (_polygon != null) {
                LatLng lastPoint = points.get(points.size() - 1);


                boolean locationOnEdge = PolyUtil.containsLocation(lastPoint, _polygon.getPoints(), true);
                if (locationOnEdge) {
                    points.remove(lastPoint);
                    Marker marker = markers.get(markers.size() - 1);
                    marker.remove();
                    Toast.makeText(getBaseActivity(), "Invalid point", Toast.LENGTH_LONG).show();
                }
            }
            rebuildPolygon();
        }
    }

    private void rebuildPolygon() {
        sortPolygon();
        PolygonOptions po = new PolygonOptions();
        po.addAll(points);
        po.fillColor(Color.parseColor(fill_wz_color));
        po.strokeColor(Color.parseColor(fill_wz_color));
        po.strokeWidth(5.0f);
        _polygon = googleMap.addPolygon(po);
        LatLngBounds newBounds = getPolygonBounds(_polygon.getPoints());
        setWzTitle(newBounds.getCenter());

    }

    public void sortPolygon() {
        List<LatLng> tempPoints = points;
        LatLngBounds newBounds = getPolygonBounds(tempPoints);
        LatLng center = newBounds.getCenter();
        List<Map.Entry<LatLng, Double>> entryList = new ArrayList<>();
        for (int i = 0; i < tempPoints.size(); i++) {
            LatLng point = tempPoints.get(i);
            double heading = SphericalUtil.computeHeading(center, point);
            Map.Entry<LatLng, Double> entry =
                    new AbstractMap.SimpleEntry<>(point, heading);
            entryList.add(entry);
        }

        Collections.sort(entryList, (obj1, obj2) -> obj1.getValue().compareTo(obj2.getValue()));
        points.clear();
        for (Map.Entry<LatLng, Double> entry : entryList) {
            points.add(entry.getKey());
        }

    }

    public Polygon GetCurrentPolygon() {

        return _polygon;
    }


    private void RemoveCirclePoints() {
        for (Marker marker : markers) {
            marker.remove();
        }
        markers = new ArrayList<>();
        googleMap.clear();
    }

    public void RemoveCurrentPolygon() {
        points = null;
        points = new ArrayList<>();
        RemoveCirclePoints();
        removePolygon();
        _polygon = null;

        viewModel.clearGeometry();
    }

    private void removePolygon() {
        if (_polygon != null) {
            _polygon.remove();
        }
        if (titleMarker != null) {
            titleMarker.remove();
        }

    }

    public boolean isPolygonAdded() {
        if (this.points != null) //&& this.points.size() > 2
            return true;
        return false;
    }

    public boolean isCircleAdded() {
        if (this.currentCircle != null) {
            return true;
        }
        return false;
    }

    public void showWzChangeAlert(String heading, String text, final boolean typeValue) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getBaseActivity());

        builder.setTitle(heading)
                .setMessage(text)
                .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                    if (typeValue)
                        removeCurrentCircle();
                    else
                        RemoveCurrentPolygon();


                    viewSetting(typeValue ? GeometryType.POLYGON : GeometryType.CIRCLE);


                })
                .setNegativeButton(android.R.string.no, (dialog, which) -> {

                    dialog.dismiss();
                    toggleGeometryType.setToggled(typeValue ? R.id.toggle_polygon : R.id.toggle_circle, true);
                })
                .show();
    }

    public void deleteWz(String heading, String text, final boolean typeValue) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getBaseActivity());

        builder.setTitle(heading)
                .setMessage(text)
                .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                    if (typeValue) {
                        removeCurrentCircle();
                    } else {
                        RemoveCurrentPolygon();
                    }

                })
                .setNegativeButton(android.R.string.no, (dialog, which) -> dialog.dismiss())
                .show();
    }

    public void viewSetting(GeometryType type) {
        geometryType = type;
        switch (type) {
            case CIRCLE:
                radiusLinear.setVisibility(View.VISIBLE);
                loc_info.setText(getResources().getString(R.string.watchzone_location_inst_circle));
                wz_loc_inst_layout.setVisibility(View.VISIBLE);
                //reseting polygon
                this._polygon = null;
                this.points = new ArrayList<>();
                break;
            case POLYGON:
                this.currentCircle = null;//reseting circle
                radiusLinear.setVisibility(View.GONE);
                loc_info.setText(getResources().getString(R.string.watchzone_location_inst_polygon));
                break;
        }
    }


    public Circle GetCurrentCircle() {
        return this.currentCircle;
    }

    public boolean SaveWzInPreference() {
        Circle currentCircle = GetCurrentCircle();
        if (currentCircle != null) {
            viewModel.saveCircle(currentCircle.getCenter().latitude, currentCircle.getCenter().longitude, _discreteSeekBar.getProgress());
        } else {
            Polygon polygon = GetCurrentPolygon();
            viewModel.savePolygon(polygon);
        }
        return true;
    }

    private void addCircle(LatLng center) {
        if (center != null) {
            int radius = binder.radiusSeek.getProgress();
            removeCurrentCircle();
            this.currentCircle = MapUtil.drawCircle(googleMap, center, radius, fill_wz_color, fill_wz_color);
            viewModel.saveCircle(center.latitude, center.longitude, radius);

            setWzTitle(center);
            AdjustZoom();
        }
    }

    private void addPointToPolygon(LatLng point) {
        //polygon
        points.add(point);
        addMarker(point);
        removePolygon();
        createPolygon();
    }

    public void EditModeAddPolygon(WatchZoneGeom geomData) {

        ArrayList<HashMap<String, Double>> cordi = geomData.getCordinate();
        for (int i = 0; i < cordi.size(); i++) {
            HashMap<String, Double> values = cordi.get(i);
            double lat = values.get("latitude");
            double lon = values.get("longitude");
            LatLng latlng = new LatLng(lon, lat);
            points.add(latlng);
            addMarker(latlng);
        }

        removePolygon();
        createPolygon();
        animatePolygon();


    }

    public void animatePolygon() {
        LatLngBounds.Builder builder = new LatLngBounds.Builder();

        for (int i = 0; i < points.size(); i++) {
            builder.include(points.get(i));
        }


        LatLngBounds bounds = builder.build();

        int width = getResources().getDisplayMetrics().widthPixels;
        int height = getResources().getDisplayMetrics().heightPixels;
        int padding = (int) (width * 0.10); // offset from edges of the map 10% of screen

        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding);

        googleMap.animateCamera(cu);
    }
}
