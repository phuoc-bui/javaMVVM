package com.redhelmet.alert2me.ui.home.event;

import android.Manifest;
import android.annotation.SuppressLint;
import android.arch.lifecycle.ViewModelProvider;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.tasks.Task;
import com.google.maps.android.clustering.ClusterManager;
import com.redhelmet.alert2me.R;
import com.redhelmet.alert2me.autocomplete.AutoCompleteLocation;
import com.redhelmet.alert2me.core.TileProviderFactory;
import com.redhelmet.alert2me.data.model.Area;
import com.redhelmet.alert2me.data.model.ClusterMarker;
import com.redhelmet.alert2me.data.model.Event;
import com.redhelmet.alert2me.databinding.FragmentEventMapBinding;
import com.redhelmet.alert2me.domain.util.CustomClusterRenderer;
import com.redhelmet.alert2me.domain.util.PreferenceUtils;
import com.redhelmet.alert2me.global.Constant;
import com.redhelmet.alert2me.ui.base.BaseFragment;
import com.redhelmet.alert2me.ui.eventdetail.EventDetailsActivity;
import com.redhelmet.alert2me.ui.widget.EventIcon;
import com.redhelmet.alert2me.util.EventUtils;
import com.redhelmet.alert2me.util.PermissionUtils;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class MapFragment extends BaseFragment<EventViewModel, FragmentEventMapBinding> implements
        OnMapReadyCallback, GoogleMap.OnMarkerClickListener,
        GoogleMap.OnMyLocationClickListener,
        GoogleMap.OnMyLocationButtonClickListener,
        AutoCompleteLocation.AutoCompleteLocationListener {

    private static final float DEFAULT_ZOOM = 15f;

    private static final int MAP_TYPE_1 = GoogleMap.MAP_TYPE_TERRAIN;
    private static final int MAP_TYPE_2 = GoogleMap.MAP_TYPE_HYBRID;
    private static final int MAP_TYPE_3 = GoogleMap.MAP_TYPE_SATELLITE;

    @Inject
    ViewModelProvider.Factory factory;

    /**
     * Flag indicating whether a requested permission has been denied after returning in
     * {@link #onRequestPermissionsResult(int, String[], int[])}.
     */
    private boolean mPermissionDenied = false;

    SupportMapFragment mapFragment;
    private GoogleMap mMapView;
    private ClusterManager<ClusterMarker> clusterManager;
    FusedLocationProviderClient mFusedLocationProviderClient;
    TileProviderFactory tileProviderFactory;
    LatLng mDefaultLocation = new LatLng(-24, 133); // australia
    private List<Polygon> polygons = new ArrayList<>();

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_event_map;
    }

    public static MapFragment newInstance() {
        return new MapFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        obtainViewModel(factory, EventViewModel.class);
        mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // Construct a FusedLocationProviderClient.
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getBaseActivity());

        binder.autocompleteLocation.setAutoCompleteTextListener(this);

        tileProviderFactory = new TileProviderFactory();

        if (viewModel.isLoadOneByOne) {
            disposeBag.add(viewModel.eventsOneByOne
                    .subscribe(this::processMarkerForEvent, e -> Toast.makeText(getBaseActivity(), R.string.msgUnableToGetEvent, Toast.LENGTH_SHORT).show()));
        } else {
            viewModel.events.observe(this, this::processMarker);
        }

        viewModel.onClearEvents.observe(this, b -> {
            if (b) clearData();
        });
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        return false;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMapView = googleMap;
        initMap(true);

        setupCluster();

        infoWindowClickedForMarkers();
    }

    private void setupCluster() {
        clusterManager = new ClusterManager<>(getBaseActivity(), mMapView);
        mMapView.setOnMarkerClickListener(clusterManager);
        mMapView.setOnCameraIdleListener(clusterManager);
        clusterManager.setRenderer(new CustomClusterRenderer(getActivity(), mMapView, clusterManager));
        clusterManager.setOnClusterItemClickListener(customMarker -> {
            // Toast.makeText(getActivity(), "Cluster specific item click!!!!", Toast.LENGTH_SHORT).show();
            return false;
        });
        clusterManager.setOnClusterClickListener(cluster -> {
            //Toast.makeText(getActivity(), "Cluster item click!!!!" + cluster.getSize(), Toast.LENGTH_SHORT).show();
            if (cluster.getSize() > 0) {

                List<Event> events = new ArrayList<>();
                // for each loop
                for (ClusterMarker item : cluster.getItems()) {
                    if (item.getEvent() != null) {
                        events.add(item.getEvent());
                    }
                }
                startActivity(ClusterEventListActivity.newInstance(getBaseActivity(), (ArrayList<Event>) events));
            } else {
                Toast.makeText(getContext(), "Unable to get event details", Toast.LENGTH_LONG).show();
            }
            // if true, click handling stops here and do not show info view, do not move camera
            // you can avoid this by calling:
            // renderer.getMarker(clusterItem).showInfoWindow();
            return false;
        });
    }

    private void updateMapType(int type) {
        mMapView.setMapType(type);
        binder.mapType1.setSelected(type == MAP_TYPE_1);
        binder.mapType2.setSelected(type == MAP_TYPE_2);
        binder.mapType3.setSelected(type == MAP_TYPE_3);
        setMapIcons(type);
    }

    public void setMapIcons(int type) {
        binder.mapType1.setImageResource(type == MAP_TYPE_1 ? R.drawable.ic_aerial_blue : R.drawable.ic_aerial);
        binder.mapType2.setImageResource(type == MAP_TYPE_2 ? R.drawable.ic_road_blue : R.drawable.ic_road);
        binder.mapType3.setImageResource(type == MAP_TYPE_3 ? R.drawable.ic_hybrid_blue : R.drawable.ic_hybrid);
    }

    public void showMapType(boolean isShow) {
        binder.groupMapType.setVisibility(isShow ? View.VISIBLE : View.GONE);
    }

    /**
     * Init map if the fine location permission has been granted.
     */
    private void initMap(boolean requestPermission) {
        if (ActivityCompat.checkSelfPermission(getBaseActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Access to the location has been granted to the app.
            if (requestPermission)
                getBaseActivity().requestPermissionsSafe(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, Constant.LOCATION_PERMISSION_REQUEST_CODE);
        } else if (mMapView != null) {
            updateLocationUI();

            // Get the current location of the device and set the position of the map.
            getDeviceLocation();

            binder.clusterEvents.setOnClickListener(v -> {
                v.setSelected(!v.isSelected());
                processMarker(viewModel.events.getValue());
                infoWindowClickedForMarkers();
            });

            binder.selectedMapType.setOnClickListener(v -> {
                showMapType(!v.isSelected());
                v.setSelected(!v.isSelected());
            });

            binder.mapType1.setOnClickListener(v -> updateMapType(MAP_TYPE_1));
            binder.mapType2.setOnClickListener(v -> updateMapType(MAP_TYPE_2));
            binder.mapType3.setOnClickListener(v -> updateMapType(MAP_TYPE_3));

            updateMapType(MAP_TYPE_1);
            if (viewModel.events.getValue() != null)
                processMarker(viewModel.events.getValue());
        }
    }

    @Override
    public boolean onMyLocationButtonClick() {
        // Return false so that we don't consume the event and the default behavior still occurs
        // (the camera animates to the user's current position).
        return false;
    }

    @Override
    public void onMyLocationClick(@NonNull Location location) {
        viewModel.saveUserLocation(location);
    }

    @SuppressLint("MissingPermission")
    private void updateLocationUI() {
        if (mMapView == null) {
            return;
        }
        if (!mPermissionDenied) {
            mMapView.setMyLocationEnabled(true);
            mMapView.setOnMyLocationButtonClickListener(this);
            mMapView.setOnMyLocationClickListener(this);
            // Extract My Location View from maps fragment
            View locationButton = ((View) mapFragment.getView().findViewById(Integer.parseInt("1")).getParent()).findViewById(Integer.parseInt("2"));
            // Change the visibility of my location button
            if (locationButton != null) {
                locationButton.setVisibility(View.GONE);
                binder.locationMap.setOnClickListener(v -> locationButton.callOnClick());
            }
        }
    }

    private void getDeviceLocation() {
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
        try {
            if (!mPermissionDenied) {
                Task<Location> locationResult = mFusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(getBaseActivity(), task -> {
                    if (task.isSuccessful()) {
                        viewModel.saveUserLocation(task.getResult());
                        mMapView.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                new LatLng(task.getResult().getLatitude(),
                                        task.getResult().getLongitude()), DEFAULT_ZOOM));
                    } else {
                        Log.d(TAG, "Current location is null. Using defaults.");
                        Log.e(TAG, "Exception: %s", task.getException());
                        mMapView.moveCamera(CameraUpdateFactory.newLatLngZoom(mDefaultLocation, 3.5f));
                        mMapView.getUiSettings().setMyLocationButtonEnabled(false);
                    }
                });
            }
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    private void clearData() {
        clearMapAndMarker();
        removeAllPolygon();
    }

    private void clearMapAndMarker() {
        if (mMapView == null || clusterManager == null) return;
        mMapView.clear();
        clusterManager.clearItems();
    }

    private void processMarker(List<Event> events) {
        if (events == null) return;
        clearMapAndMarker();

        if (binder.clusterEvents.isSelected()) {
            processClusterMarker(events);
        } else {
            processEventMarker(events);
        }
    }

    private void processMarkerForEvent(Event event) {
        if (mMapView == null || clusterManager == null || event == null) return;
        Log.e(TAG, "add marker for event " + event.getId());
        if (binder.clusterEvents.isSelected()) {
            addClusterMarker(event);
        } else {
            addEventMarker(event);
        }
    }

    private void removeAllPolygon() {
        if (polygons == null || polygons.isEmpty()) return;
        for (Polygon polygon : polygons) {
            polygon.remove();
        }
        polygons.clear();
    }

    private void processClusterMarker(List<Event> events) {
        for (int i = 0; i < events.size(); i++) {
            Event event = events.get(i);
            addClusterMarker(event);
        }
    }

    private void addClusterMarker(Event event) {
        //TODO: is need check isShowOn?
        List<Area> areas = event.getArea();
        for (int j = 0; j < areas.size(); j++) {
            ClusterMarker customMarker = new ClusterMarker(event, areas.get(j));
            clusterManager.addItem(customMarker);
        }
        clusterManager.cluster();
        Polygon p = mMapView.addPolygon(viewModel.createPolygonForEvent(event));
        polygons.add(p);
    }

    private void processEventMarker(List<Event> events) {
        for (int i = 0; i < events.size(); i++) {
            try {
                Event event = events.get(i);
                addEventMarker(event);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    private void addEventMarker(Event event) {
        //TODO: is need check isShowOn?
        List<Area> areas = event.getArea();
        for (int j = 0; j < areas.size(); j++) {

            MarkerOptions markerOptions = EventUtils.eventToMarker(event, areas.get(j));
            EventIcon icon = new EventIcon(getBaseActivity(), event, true, -1);
            BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.fromBitmap(icon.convertToBitMap());
            markerOptions.icon(bitmapDescriptor);
            Marker marker = mMapView.addMarker(markerOptions);
            marker.setTag(event);
        }
        Polygon p = mMapView.addPolygon(viewModel.createPolygonForEvent(event));
        polygons.add(p);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case Constant.LOCATION_PERMISSION_REQUEST_CODE:
                if (getBaseActivity().isPermissionGranted(permissions, grantResults, Manifest.permission.ACCESS_FINE_LOCATION)) {
                    mPermissionDenied = false;
                    initMap(false);
                } else {
                    // Display the missing permission error dialog when the fragments resume.
                    mPermissionDenied = true;
                    Toast.makeText(getBaseActivity(), "Location Permission Denied", Toast.LENGTH_SHORT).show();
                }
                break;

        }

    }

    @Override
    public void onResume() {
        super.onResume();
        if (mPermissionDenied) {
            // Permission was not granted, display error dialog.
            showMissingPermissionError();
            mPermissionDenied = false;
        }
    }

    /**
     * Displays a dialog with error message explaining that the location permission is missing.
     */
    private void showMissingPermissionError() {
        PermissionUtils.PermissionDeniedDialog
                .newInstance(true).show(getChildFragmentManager(), "dialog");
    }

    @Override
    public void onTextClear() {
    }

    @Override
    public void onItemSelected(Place selectedPlace) {
        LatLng latLng = new LatLng(selectedPlace.getLatLng().latitude, selectedPlace.getLatLng().longitude);
        mMapView.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 55));
    }

    void infoWindowClickedForMarkers() {
        if (mMapView != null) {
            if (PreferenceUtils.hasKey(getBaseActivity(), getString(R.string.pref_cluster_map_state))) {
                if (clusterManager != null) {
                    mMapView.setOnInfoWindowClickListener(clusterManager);
                    clusterManager.setOnClusterItemInfoWindowClickListener(
                            customMarker -> {
                                Event event = customMarker.getEvent();
                                if (event != null) {
                                    InitializeDetailsPage(event);
                                } else {
                                    Toast.makeText(getBaseActivity(), "Unable to get event details", Toast.LENGTH_LONG).show();
                                }
                            });
                }
            } else {
                mMapView.setOnInfoWindowClickListener(marker -> {
                    Event event = (Event) marker.getTag();
                    if (event != null) {
                        InitializeDetailsPage(event);
                    } else {
                        Toast.makeText(getBaseActivity(), "Unable to get event details", Toast.LENGTH_LONG).show();
                    }
                });


            }

        }
    }

    private void InitializeDetailsPage(Event event) {
        if (event != null) {
            startActivity(EventDetailsActivity.newInstance(getBaseActivity(), event));
        } else {
            Toast.makeText(getContext(), "Unable to get event details", Toast.LENGTH_LONG).show();
        }
    }
}
