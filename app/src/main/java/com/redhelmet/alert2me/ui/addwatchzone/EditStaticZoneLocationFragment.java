package com.redhelmet.alert2me.ui.addwatchzone;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.gson.Gson;
import com.google.maps.android.PolyUtil;
import com.google.maps.android.SphericalUtil;
import com.google.maps.android.ui.IconGenerator;
import com.redhelmet.alert2me.BuildConfig;
import com.redhelmet.alert2me.R;
import com.redhelmet.alert2me.autocomplete.AutoCompleteLocation;
import com.redhelmet.alert2me.data.model.AppConfig;
import com.redhelmet.alert2me.data.model.EditWatchZones;
import com.redhelmet.alert2me.data.model.WatchZoneGeom;
import com.redhelmet.alert2me.databinding.FragmentEditStaticZoneLocationBinding;
import com.redhelmet.alert2me.domain.util.PreferenceUtils;
import com.redhelmet.alert2me.ui.activity.AddStaticZoneLocation;
import com.redhelmet.alert2me.ui.activity.AddStaticZoneNotification;
import com.redhelmet.alert2me.ui.base.BaseFragment;

import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;

public class EditStaticZoneLocationFragment extends BaseFragment<AddStaticZoneViewModel, FragmentEditStaticZoneLocationBinding> implements View.OnClickListener, GoogleMap.OnMapLongClickListener, AutoCompleteLocation.AutoCompleteLocationListener, OnMapReadyCallback {

    @Inject
    ViewModelProvider.Factory factory;

    String fill_wz_color = "#8Ce0701e";//"#8C8EB9E8";
    Intent i;
    GoogleMap _locationMap;
    Button circleBtn, polygonBtn;
    LinearLayout radiusLinear;
    LinearLayout wz_loc_inst_layout;
    TextView loc_info;
    private Circle _circle = null;
    private Polygon _polygon;
    private ArrayList<LatLng> points;
    private ArrayList<Marker> markers;
    private DiscreteSeekBar _discreteSeekBar;
    Marker titleMarker;
    TextView txtRadius;
    int _radius = 5;
    private int seekRadius = 5;
    boolean viewStatus = true; //enum circle 0 , poly 1
    public String wz_name = null;
    ArrayList<EditWatchZones> wzData;
    int position = 0;
    boolean editMode;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_edit_static_zone_location;
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        obtainViewModel(factory, AddStaticZoneViewModel.class);

        initializeControls();
        initializeMap();
    }

    public void initializeMap() {
        if (editMode) {
            if (wzData.get(position).getWzType().equalsIgnoreCase("STANDARD")) {
                viewSetting(true);
            } else {
                viewSetting(false);
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
        circleBtn = binder.circleBtn;
        polygonBtn = binder.polygonBtn;
        radiusLinear = binder.radiusLayout;
        wz_loc_inst_layout = binder.wzLocInstLayout;
        loc_info = binder.wzLocInst;
        _discreteSeekBar = binder.radiusSeek;
        circleBtn.setOnClickListener(this);
        polygonBtn.setOnClickListener(this);
        txtRadius = binder.txtRadius;
        setupSliderValue();

        _discreteSeekBar.setOnProgressChangeListener(new DiscreteSeekBar.OnProgressChangeListener() {
            @Override
            public void onProgressChanged(DiscreteSeekBar seekBar, int radius, boolean fromUser) {
                if (_circle != null) {
                    _radius = radius * 100;
                    seekRadius = radius;

                    setupSliderValue();
                    _circle.setRadius(_radius);
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

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.watchzone_static_next, menu);
//        menu.getItem(0).setVisible(true);
//        if (editMode)
//            menu.getItem(1).setTitle(getString(R.string.done));
//
//        return true;
//    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.next_btn:
                taskForNextBtn();


                return true;
            case R.id.delete_temp_watch_zone:

                if (viewStatus) {
                    //circle
                    if (isCircleAdded()) {
                        //cecking is circle is on map
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
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.circleBtn:
                if (isPolygonAdded())
                    showWzChangeAlert(getString(R.string.wz_change_heading_circle), getString(R.string.wz_change_text_circle), false);
                else
                    viewSetting(true);

                break;
            case R.id.polygonBtn:
                if (isCircleAdded())
                    showWzChangeAlert(getString(R.string.wz_change_heading_custom), getString(R.string.wz_change_text_custom), true);
                else
                    viewSetting(false);
                break;
        }
    }

    @Override
    public void onMapLongClick(LatLng latLng) {
        if (viewStatus) {
            //circle

            RemoveCurrentCircle();
            this._radius = seekRadius * 100;
            this._circle = _locationMap.addCircle(new CircleOptions()
                    .center(latLng)
                    .radius(_radius)
                    .strokeColor(Color.parseColor(fill_wz_color))
                    .fillColor(Color.parseColor(fill_wz_color)));
            _discreteSeekBar.setProgress(seekRadius);
            setWzTitle(latLng);
            AdjustZoom();
        } else {
            //polygon
            points.add(latLng);
            addMarker(latLng);
            RemovePolygon();
            createPolygon();
        }

    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        _locationMap = googleMap;

        _locationMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {
                //Your code where exception occurs goes here...
                LatLng latLng = new LatLng(BuildConfig.DEFAULT_LOCATION_LAT, BuildConfig.DEFAULT_LOCATION_LNG);
                _locationMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 3.5f));

                _locationMap.setOnMapLongClickListener(EditStaticZoneLocationFragment.this);

//                if (editMode) {
//                    if (wzData.get(position).getType().toString().equalsIgnoreCase("STANDARD")) {
//                        EditModeAddCircle(wzData.get(position).getWatchZoneGeoms(), wzData.get(position).getRadius());
//                    } else {
//                        EditModeAddPolygon(wzData.get(position).getWatchZoneGeoms());
//                    }
//
//                }
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
        titleMarker = _locationMap.addMarker(new MarkerOptions().position(center).icon(BitmapDescriptorFactory.fromBitmap(icon)));
    }

    private boolean RemoveCurrentCircle() {
        if (_circle != null) {
            _circle.remove();
            if (titleMarker != null) {
                titleMarker.remove();
            }
            _circle = null;
            titleMarker = null;

            return true;
        }
        return false;
    }


    private void AdjustZoom() {
        LatLng center = _circle.getCenter();
        double radius = _circle.getRadius();
        LatLng targetNorthEast = SphericalUtil.computeOffset(center, radius * Math.sqrt(2), 45);
        LatLng targetSouthWest = SphericalUtil.computeOffset(center, radius * Math.sqrt(2), 225);
        LatLngBounds latLngBounds = new LatLngBounds(targetSouthWest, targetNorthEast);

        _locationMap.animateCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds, 45));
    }

    @Override
    public void onTextClear() {

    }

    @Override
    public void onItemSelected(Place selectedPlace) {


        LatLng latLng = new LatLng(selectedPlace.getLatLng().latitude, selectedPlace.getLatLng().longitude);
        _locationMap.addMarker(new MarkerOptions().position(latLng));
        _locationMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 55));
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
        Marker marker = _locationMap.addMarker(new MarkerOptions().position(latLng).icon(icon).zIndex(-1));
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
        _polygon = _locationMap.addPolygon(po);
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

        Collections.sort(entryList, new Comparator<Map.Entry<LatLng, Double>>() {

            @Override
            public int compare(Map.Entry<LatLng, Double> obj1, Map.Entry<LatLng, Double> obj2) {
                return obj1.getValue().compareTo(obj2.getValue());
            }
        });
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
        _locationMap.clear();
    }

    public void RemoveCurrentPolygon() {
        points = null;
        points = new ArrayList<>();
        RemoveCirclePoints();
        RemovePolygon();
        _polygon = null;


    }

    private void RemovePolygon() {
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
        if (this._circle != null) {
            return true;
        }
        return false;
    }

    public void showWzChangeAlert(String heading, String text, final boolean typeValue) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getBaseActivity());

        builder.setTitle(heading)
                .setMessage(text)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if (typeValue)
                            RemoveCurrentCircle();
                        else
                            RemoveCurrentPolygon();


                        viewSetting(!typeValue);


                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.dismiss();
                    }
                })
                .show();
    }

    public void deleteWz(String heading, String text, final boolean typeValue) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getBaseActivity());

        builder.setTitle(heading)
                .setMessage(text)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if (typeValue) {
                            RemoveCurrentCircle();
                        } else {
                            RemoveCurrentPolygon();
                        }

                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.dismiss();
                    }
                })
                .show();
    }

    public void viewSetting(boolean state) {
        if (state) {
            radiusLinear.setVisibility(View.VISIBLE);
            loc_info.setText(getResources().getString(R.string.watchzone_location_inst_circle));
            viewStatus = true;
            wz_loc_inst_layout.setVisibility(View.VISIBLE);
            circleBtn.setBackgroundResource(R.drawable.button_red_bottom_border);
            polygonBtn.setBackgroundResource(R.drawable.border_shadow);
            //reseting polygon
            this._polygon = null;
            this.points = new ArrayList<>();
        } else {
            polygonBtn.setBackgroundResource(R.drawable.button_red_bottom_border);
            circleBtn.setBackgroundResource(R.drawable.border_shadow);
            this._circle = null;//reseting circle
            radiusLinear.setVisibility(View.GONE);

            loc_info.setText(getResources().getString(R.string.watchzone_location_inst_polygon));
            viewStatus = false;
        }
    }


    public Circle GetCurrentCircle() {
        return this._circle;
    }

    public boolean SaveWzInPreference() {
        Circle currentCircle = GetCurrentCircle();
        JSONObject wzLoc;
        Gson gson = new Gson();

        if (currentCircle != null) {
            double longitude = currentCircle.getCenter().longitude;
            double latitude = currentCircle.getCenter().latitude;
            ArrayList<Object> coordinates = new ArrayList<>();
            coordinates.add(latitude);
            coordinates.add(longitude);

            wzLoc = new JSONObject();

            JSONObject wzGeom = new JSONObject();
            try {
                wzLoc.put("radius", this._discreteSeekBar.getProgress());
                wzLoc.put("geom", String.format("POINT(%s %s)", longitude, latitude));
                wzLoc.put("type", "STANDARD");

                wzGeom.put("coordinates", coordinates);
                wzGeom.put("type", "POINT");
                wzLoc.put("geometry", wzGeom);

                PreferenceUtils.saveToPrefs(getBaseActivity(), "wzLocation", gson.toJson(wzLoc));
                return true;

            } catch (JSONException e) {
                e.printStackTrace();
            }

        } else {
            Polygon polygon = GetCurrentPolygon();
            wzLoc = new JSONObject();
            if (polygon != null) {
                List<LatLng> points = polygon.getPoints();
                String formattedPoints = "";
                for (int i = 0; i <= points.size() - 1; i++) {
                    formattedPoints += String.format("%s %s,", points.get(i).longitude, points.get(i).latitude);
                    if (i == points.size() - 1) {
                        formattedPoints += String.format("%s %s", points.get(i).longitude, points.get(i).latitude);
                    }
                }
                StringBuilder st = new StringBuilder();
                st.append(String.format("POLYGON((%s))", formattedPoints));

                try {
                    wzLoc.put("radius", 0);
                    wzLoc.put("geom", st.toString());
                    wzLoc.put("type", "VARIABLE");
                    PreferenceUtils.saveToPrefs(getBaseActivity(), "wzLocation", gson.toJson(wzLoc));
                    return true;
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        }

        return false;
    }

    public void EditModeAddCircle(WatchZoneGeom geomData, String watchzoneRadius) {

        seekRadius = Integer.parseInt(watchzoneRadius);
        ArrayList<HashMap<String, Double>> cordi = geomData.getCordinate();
        HashMap<String, Double> values = cordi.get(0);
        double lon = values.get("latitude");
        double lat = values.get("longitude");

        LatLng latlng = new LatLng(lat, lon);
        if (latlng != null) {

            RemoveCurrentCircle();
            this._radius = seekRadius * 100;
            this._circle = _locationMap.addCircle(new CircleOptions()
                    .center(latlng)
                    .radius(_radius)
                    .strokeColor(Color.parseColor(fill_wz_color))
                    .strokeWidth(5.0f)
                    .fillColor(Color.parseColor(fill_wz_color)));

            _discreteSeekBar.setProgress(seekRadius);
            setWzTitle(latlng);
            AdjustZoom();
        } else {
            Toast.makeText(getBaseActivity(), getString(R.string.circle_create_failed), Toast.LENGTH_SHORT).show();
        }


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

        RemovePolygon();
        createPolygon();
        AnimatePolygon();


    }

    public void AnimatePolygon() {
        LatLngBounds.Builder builder = new LatLngBounds.Builder();

        for (int i = 0; i < points.size(); i++) {
            builder.include(points.get(i));
        }


        LatLngBounds bounds = builder.build();

        int width = getResources().getDisplayMetrics().widthPixels;
        int height = getResources().getDisplayMetrics().heightPixels;
        int padding = (int) (width * 0.10); // offset from edges of the map 10% of screen

        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding);

        _locationMap.animateCamera(cu);
    }

    public void taskForNextBtn() {

        if (editMode) {//edit WZ

            if (viewStatus)//circle
            {
                if (isCircleAdded()) {
                    if (SaveWzInPreference()) {
                        onBackPressed();
                    }
                } else {
                    Toast.makeText(getBaseActivity(), R.string.next_location_message, Toast.LENGTH_LONG).show();
                }
            } else { //polygon
                if (isPolygonAdded()) {
                    if (SaveWzInPreference()) {
                        onBackPressed();
                    }
                } else {
                    Toast.makeText(getBaseActivity(), R.string.next_location_message, Toast.LENGTH_LONG).show();
                }
            }


        } else {
            if (viewStatus)//circle
            {
                if (isCircleAdded()) {
                    if (SaveWzInPreference()) {
                        i = new Intent(getBaseActivity(), AddStaticZoneNotification.class);
                        startActivity(i);
                    }
                } else {
                    Toast.makeText(getBaseActivity(), R.string.next_location_message, Toast.LENGTH_LONG).show();
                }
            } else {
                if (isPolygonAdded()) {
                    if (SaveWzInPreference()) {
                        i = new Intent(getBaseActivity(), AddStaticZoneNotification.class);
                        startActivity(i);
                    }
                } else {
                    Toast.makeText(getBaseActivity(), R.string.next_location_message, Toast.LENGTH_LONG).show();
                }
            }

        }
    }
}
