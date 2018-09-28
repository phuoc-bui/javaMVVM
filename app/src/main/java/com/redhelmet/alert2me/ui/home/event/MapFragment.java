package com.redhelmet.alert2me.ui.home.event;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

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
import com.google.maps.android.clustering.ClusterManager;
import com.redhelmet.alert2me.R;
import com.redhelmet.alert2me.autocomplete.AutoCompleteLocation;
import com.redhelmet.alert2me.core.Constants;
import com.redhelmet.alert2me.core.TileProviderFactory;
import com.redhelmet.alert2me.data.model.Area;
import com.redhelmet.alert2me.data.model.Category;
import com.redhelmet.alert2me.data.model.CategoryStatus;
import com.redhelmet.alert2me.data.model.CategoryType;
import com.redhelmet.alert2me.data.model.ClusterMarker;
import com.redhelmet.alert2me.data.model.Event;
import com.redhelmet.alert2me.databinding.FragmentEventMapBinding;
import com.redhelmet.alert2me.domain.util.CustomClusterRenderer;
import com.redhelmet.alert2me.domain.util.PreferenceUtils;
import com.redhelmet.alert2me.global.Constant;
import com.redhelmet.alert2me.ui.activity.ClusterEventList;
import com.redhelmet.alert2me.ui.activity.EventDetailsActivity;
import com.redhelmet.alert2me.ui.base.BaseFragment;
import com.redhelmet.alert2me.util.EventUtils;
import com.redhelmet.alert2me.util.IconUtils;
import com.redhelmet.alert2me.util.PermissionUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static com.redhelmet.alert2me.core.CoreFunctions._context;

public class MapFragment extends BaseFragment<EventViewModel, FragmentEventMapBinding> implements
        OnMapReadyCallback, GoogleMap.OnMarkerClickListener,
        GoogleMap.OnMyLocationClickListener,
        GoogleMap.OnMyLocationButtonClickListener,
        AutoCompleteLocation.AutoCompleteLocationListener {
    private static final String VIEW_MODEL_KEY = "viewModel";
    private static final int EVENT_FILTER_REQUEST = 9;

    private static final int MAP_TYPE_1 = GoogleMap.MAP_TYPE_NORMAL;
    private static final int MAP_TYPE_2 = GoogleMap.MAP_TYPE_TERRAIN;
    private static final int MAP_TYPE_3 = GoogleMap.MAP_TYPE_HYBRID;

    /**
     * Flag indicating whether a requested permission has been denied after returning in
     * {@link #onRequestPermissionsResult(int, String[], int[])}.
     */
    private boolean mPermissionDenied = false;

    private GoogleMap mMapView;
    private ClusterManager<ClusterMarker> clusterManager;
    LocationManager locationManager;
    LatLng latlng = null;

    public ArrayList<String> mapOverlay = new ArrayList<String>();
    public ArrayList<String> mapLayers = new ArrayList<String>();

    public ArrayList<Category> category_data = new ArrayList<Category>();
    public ArrayList<CategoryType> types_data = new ArrayList<CategoryType>();
    public ArrayList<CategoryStatus> statuses_data = new ArrayList<CategoryStatus>();

    TileProviderFactory tileProviderFactory;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_event_map;
    }

    @Override
    protected Class<EventViewModel> getViewModelClass() {
        return EventViewModel.class;
    }

    @Override
    protected EventViewModel obtainViewModel() {
        if (getArguments() != null) {
            Object data = getArguments().getSerializable(VIEW_MODEL_KEY);
            if (data instanceof EventViewModel) {
                return (EventViewModel) data;
            } else {
                throw new Error("viewModel must is not null");
            }
        } else {
            throw new Error("viewModel must is not null");
        }
    }

    public static EventListFragment newInstance(EventViewModel viewModel) {
        EventListFragment fragment = new EventListFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(VIEW_MODEL_KEY, viewModel);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        locationManager = (LocationManager) getBaseActivity().getSystemService(Context.LOCATION_SERVICE);

        binder.autocompleteLocation.setAutoCompleteTextListener(this);

        tileProviderFactory = new TileProviderFactory();

        viewModel.events.observe(this, this::processMarker);
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

        processMarker(viewModel.events.getValue());
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
                Bundle bundle = new Bundle();
                bundle.putSerializable("clusterEvents", (Serializable) events);
                Intent intent = new Intent(getActivity(), ClusterEventList.class);
                intent.putExtra("bundle", bundle);
                startActivity(intent);
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
            mMapView.setMyLocationEnabled(true);
            mMapView.getUiSettings().setMyLocationButtonEnabled(false);
            mMapView.setOnMyLocationButtonClickListener(this);
            mMapView.setOnMyLocationClickListener(this);
            binder.locationMap.setOnClickListener(v -> onMyLocationButtonClick());
            binder.clusterEvents.setOnClickListener(v -> {
                processMarker(viewModel.events.getValue());
                infoWindowClickedForMarkers();
                v.setSelected(!v.isSelected());
            });

            binder.selectedMapType.setOnClickListener(v -> {
                showMapType(!v.isSelected());
                v.setSelected(!v.isSelected());
            });

            binder.mapType1.setOnClickListener(v -> updateMapType(MAP_TYPE_1));
            binder.mapType2.setOnClickListener(v -> updateMapType(MAP_TYPE_2));
            binder.mapType3.setOnClickListener(v -> updateMapType(MAP_TYPE_3));

            updateMapType(MAP_TYPE_1);
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
        PreferenceUtils.saveToPrefs(_context, Constants.KEY_USERLATITUDE, String.valueOf(location.getLatitude()));
        PreferenceUtils.saveToPrefs(_context, Constants.KEY_USERLONGITUDE, String.valueOf(location.getLongitude()));
    }

    private void processMarker(List<Event> events) {
        if (mMapView == null || clusterManager == null || events == null) return;
        mMapView.clear();
        clusterManager.clearItems();

        if (binder.clusterEvents.isSelected()) {
            processClusterMarker(events);
        } else {
            processEventMarker(events);
        }
    }

    private void processClusterMarker(List<Event> events) {

        for (int i = 0; i < events.size(); i++) {
            Event event = events.get(i);
            if (viewModel.isDefaultFilter()) {
                if (event.isShowOn()) {
                    List<Area> areas = event.getArea();
                    for (int j = 0; j < areas.size(); j++) {
                        ClusterMarker customMarker = new ClusterMarker(events.get(i), areas.get(j));
                        clusterManager.addItem(customMarker);
                    }
                }
            } else {
                List<Area> areas = event.getArea();
                for (int j = 0; j < areas.size(); j++) {
                    ClusterMarker customMarker = new ClusterMarker(events.get(i), areas.get(j));
                    clusterManager.addItem(customMarker);
                }
            }
        }
        float zoom = mMapView.getCameraPosition().zoom;
        zoom = zoom + 0.1f;
        mMapView.moveCamera(CameraUpdateFactory.zoomTo(zoom));
        clusterManager.cluster();
    }

    private void processEventMarker(List<Event> events) {
        for (int i = 0; i < events.size(); i++) {
            try {
                Event event = events.get(i);
                if (viewModel.isDefaultFilter()) {
                    if (event.isShowOn()) {
                        List<Area> areas = event.getArea();
                        for (int j = 0; j < areas.size(); j++) {

                            MarkerOptions markerOptions = EventUtils.eventToMarker(event, areas.get(j));

                            BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.fromBitmap(IconUtils.createEventIcon(R.layout.custom_map_layer_icon, event, event.getPrimaryColor(), false, false, ""));
                            markerOptions.icon(bitmapDescriptor);
                            Marker marker = mMapView.addMarker(markerOptions);
                            marker.setTag(event);
                            String eventId = String.format("%s__%s__%s", event.getId(), event.getCategory(), event.getStatus());
                            Log.e("DefaulMapPins:", eventId);
//                            _markerOptionsHashMap.put(marker.getId(), eventId);

                        }
                    }
                } else {
                    List<Area> areas = event.getArea();
                    for (int j = 0; j < areas.size(); j++) {

                        MarkerOptions markerOptions = EventUtils.eventToMarker(event, areas.get(j));

                        BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.fromBitmap(IconUtils.createEventIcon(R.layout.custom_map_layer_icon, event, event.getPrimaryColor(), false, false, ""));
                        markerOptions.icon(bitmapDescriptor);
                        Marker marker = mMapView.addMarker(markerOptions);
                        marker.setTag(event);
                        String eventId = String.format("%s__%s__%s", event.getId(), event.getCategory(), event.getStatus());
//                        _markerOptionsHashMap.put(marker.getId(), eventId);
                    }
                }


            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
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
        //mMapView.addMarker(new MarkerOptions().position(latLng));
        mMapView.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 55));
    }

    void infoWindowClickedForMarkers() {
        if (mMapView != null) {

            if (PreferenceUtils.hasKey(getBaseActivity(), getString(R.string.pref_cluster_map_state))) {

                if (clusterManager != null) {
                    Log.d("dfsd", "Cluster value");
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

                Log.d("dfsd", "map single value");
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == EVENT_FILTER_REQUEST && resultCode == Activity.RESULT_OK) {

        }
    }
}
