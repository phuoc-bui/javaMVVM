package com.redhelmet.alert2me.ui.addwatchzone;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.places.Place;
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
import com.redhelmet.alert2me.databinding.FragmentEditStaticZoneLocationBinding;
import com.redhelmet.alert2me.ui.base.BaseFragment;
import com.redhelmet.alert2me.util.MapUtil;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collections;
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

    private GoogleMap googleMap;
    private Circle currentCircle = null;
    private Polygon currentPolygon;
    private ArrayList<Marker> markers;
    private Marker titleMarker;
    private String wz_name = null;

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

        // observe radius change
        disposeBag.add(viewModel.watchZoneModel.radius.asObservable().subscribe(integer -> {
            if (currentCircle != null && integer != null) {
                currentCircle.setRadius(integer);
                adjustZoom();
            }
        }));

        viewModel.watchZoneModel.geometryType.observe(this, type -> {
            switch (type) {
                case CIRCLE:
                    binder.radiusLayout.setVisibility(View.VISIBLE);
                    binder.wzLocInst.setText(getResources().getString(R.string.watchzone_location_inst_circle));
                    binder.wzLocInstLayout.setVisibility(View.VISIBLE);
                    //reseting polygon
                    this.currentPolygon = null;
                    break;
                case POLYGON:
                    this.currentCircle = null;
                    binder.radiusLayout.setVisibility(View.GONE);
                    binder.wzLocInst.setText(getResources().getString(R.string.watchzone_location_inst_polygon));
                    break;
            }
        });
    }

    private void initializeMap() {
        markers = new ArrayList<>();
        binder.autocompleteLocation.setAutoCompleteTextListener(this);

        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.locationMap);
        mapFragment.getMapAsync(this);

        getBaseActivity().hideKeyboard();
    }


    public void initializeControls() {
        binder.toggleGeometry.setToggled(viewModel.watchZoneModel.isCircle() ? R.id.toggle_circle : R.id.toggle_polygon, true);
        binder.toggleGeometry.setOnToggledListener((toggle, selected) -> {
            switch (toggle.getId()) {
                case R.id.toggle_circle:
                    if (currentPolygon != null)
                        showWzChangeAlert(getString(R.string.wz_change_heading_circle), getString(R.string.wz_change_text_circle), false);
                    else {
                        viewModel.watchZoneModel.changeGeometryType(AddStaticZoneViewModel.GeometryType.CIRCLE);
                    }
                    break;
                case R.id.toggle_polygon:
                    if (currentCircle != null)
                        showWzChangeAlert(getString(R.string.wz_change_heading_custom), getString(R.string.wz_change_text_custom), true);
                    else {
                        viewModel.watchZoneModel.changeGeometryType(AddStaticZoneViewModel.GeometryType.POLYGON);
                    }
                    break;
            }
            return null;
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.clear_btn:
                if (viewModel.watchZoneModel.isCircle()) {
                    if (currentCircle != null) {
                        //checking is circle is on map
                        deleteWz(getString(R.string.delete_circle_heading), getString(R.string.delete_circle_text), true);
                    } else {
                        Toast.makeText(getBaseActivity(), R.string.delete_circle_message, Toast.LENGTH_LONG).show();
                    }
                } else {
                    //for polygon
                    //checking polygon on map or not
                    if (currentPolygon != null) {
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
        if (viewModel.watchZoneModel.isCircle()) {
            viewModel.watchZoneModel.points.clear();
            viewModel.watchZoneModel.points.add(latLng);
            createCircle();
        } else {
            viewModel.watchZoneModel.points.add(latLng);
            addPointToPolygon(latLng);
        }
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;

        this.googleMap.setOnMapLoadedCallback(() -> {
            //Your code where exception occurs goes here...
            LatLng latLng = new LatLng(BuildConfig.DEFAULT_LOCATION_LAT, BuildConfig.DEFAULT_LOCATION_LNG);
            this.googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 3.5f));
            this.googleMap.setOnMapLongClickListener(this);

            if (viewModel.watchZoneModel.mode == AddStaticZoneViewModel.Mode.EDIT) {
                if (viewModel.watchZoneModel.isCircle() && currentCircle == null) {
                    createCircle();
                } else if (currentPolygon == null) {
                    for (LatLng point : viewModel.watchZoneModel.points) {
                        addMarker(point);
                    }
                    createPolygon();
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

    private void removeCurrentCircle() {
        if (currentCircle != null) {
            currentCircle.remove();
            if (titleMarker != null) {
                titleMarker.remove();
            }
            currentCircle = null;
            titleMarker = null;
        }
    }


    private void adjustZoom() {
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
        BitmapDescriptor icon = MapUtil.getMarkerIconFromDrawable(drawable);
        Marker marker = googleMap.addMarker(new MarkerOptions().position(latLng).icon(icon).zIndex(-1));
        markers.add(marker);
    }

    private void createPolygon() {
        List<LatLng> points = viewModel.watchZoneModel.points;
        if (points.size() > 2) {
            binder.wzLocInstLayout.setVisibility(View.INVISIBLE);
            if (currentPolygon != null) {
                LatLng lastPoint = points.get(points.size() - 1);
                boolean locationOnEdge = PolyUtil.containsLocation(lastPoint, currentPolygon.getPoints(), true);
                if (locationOnEdge) {
                    points.remove(lastPoint);
                    Marker marker = markers.get(markers.size() - 1);
                    marker.remove();
                    Toast.makeText(getBaseActivity(), "Invalid point", Toast.LENGTH_LONG).show();
                }
            }
            rebuildPolygon(points);
        }
    }

    private void rebuildPolygon(List<LatLng> points) {
        sortPolygon(points);
        PolygonOptions po = new PolygonOptions();
        po.addAll(points);
        po.fillColor(getResources().getColor(R.color.wz_polygon_color));
        po.strokeColor(getResources().getColor(R.color.wz_polygon_color));
        po.strokeWidth(5.0f);
        currentPolygon = googleMap.addPolygon(po);
        LatLngBounds newBounds = getPolygonBounds(currentPolygon.getPoints());
        setWzTitle(newBounds.getCenter());
    }

    private void sortPolygon(List<LatLng> points) {
        LatLngBounds newBounds = getPolygonBounds(points);
        LatLng center = newBounds.getCenter();
        List<Map.Entry<LatLng, Double>> entryList = new ArrayList<>();
        for (int i = 0; i < points.size(); i++) {
            LatLng point = points.get(i);
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

    private void clearPolygon() {
        for (Marker marker : markers) {
            marker.remove();
        }
        markers = new ArrayList<>();
        googleMap.clear();
        removeCurrentPolygon();
    }

    private void removeCurrentPolygon() {
        if (currentPolygon != null) {
            currentPolygon.remove();
        }
        if (titleMarker != null) {
            titleMarker.remove();
        }
        currentPolygon = null;
    }

    private void showWzChangeAlert(String heading, String text, final boolean isChangeToPolygon) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getBaseActivity());

        builder.setTitle(heading)
                .setMessage(text)
                .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                    if (isChangeToPolygon) {
                        removeCurrentCircle();
                        viewModel.watchZoneModel.changeGeometryType(AddStaticZoneViewModel.GeometryType.POLYGON);
                    } else {
                        clearPolygon();
                        viewModel.watchZoneModel.changeGeometryType(AddStaticZoneViewModel.GeometryType.CIRCLE);
                    }
                })
                .setNegativeButton(android.R.string.no, (dialog, which) -> {

                    dialog.dismiss();
                    binder.toggleGeometry.setToggled(isChangeToPolygon ? R.id.toggle_polygon : R.id.toggle_circle, true);
                })
                .show();
    }

    private void deleteWz(String heading, String text, final boolean typeValue) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getBaseActivity());
        builder.setTitle(heading)
                .setMessage(text)
                .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                    if (typeValue) {
                        removeCurrentCircle();
                    } else {
                        clearPolygon();
                    }

                })
                .setNegativeButton(android.R.string.no, (dialog, which) -> dialog.dismiss())
                .show();
    }

    private void createCircle() {
        List<LatLng> points = viewModel.watchZoneModel.points;
        if (points != null && points.size() > 0) {
            LatLng center = points.get(0);
            if (center != null) {
                int radius = binder.radiusSeek.getProgress();
                removeCurrentCircle();
                int color = getColor(R.color.wz_polygon_color);
                this.currentCircle = MapUtil.drawCircle(googleMap, center, radius, color, color);
                setWzTitle(center);
                adjustZoom();
            }
        }
    }

    private void addPointToPolygon(LatLng point) {
        addMarker(point);
        removeCurrentPolygon();
        createPolygon();
    }
}
