package com.redhelmet.alert2me.ui.home.event;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.maps.android.clustering.ClusterManager;
import com.redhelmet.alert2me.R;
import com.redhelmet.alert2me.adapters.EmptyListRecyclerAdapter;
import com.redhelmet.alert2me.adapters.EventListRecyclerAdapter;
import com.redhelmet.alert2me.autocomplete.AutoCompleteLocation;
import com.redhelmet.alert2me.core.Constants;
import com.redhelmet.alert2me.core.TileProviderFactory;
import com.redhelmet.alert2me.core.WmsTileProvider;
import com.redhelmet.alert2me.data.local.database.DBController;
import com.redhelmet.alert2me.data.model.Area;
import com.redhelmet.alert2me.data.model.Category;
import com.redhelmet.alert2me.data.model.CategoryFilter;
import com.redhelmet.alert2me.data.model.CategoryStatus;
import com.redhelmet.alert2me.data.model.CategoryType;
import com.redhelmet.alert2me.data.model.CategoryTypeFilter;
import com.redhelmet.alert2me.data.model.ClusterMarker;
import com.redhelmet.alert2me.data.model.Event;
import com.redhelmet.alert2me.databinding.FragmentEventMapBinding;
import com.redhelmet.alert2me.domain.util.CustomClusterRenderer;
import com.redhelmet.alert2me.domain.util.PreferenceUtils;
import com.redhelmet.alert2me.global.Constant;
import com.redhelmet.alert2me.ui.activity.ClusterEventList;
import com.redhelmet.alert2me.ui.activity.EventDetailsActivity;
import com.redhelmet.alert2me.ui.base.BaseFragment;
import com.redhelmet.alert2me.util.PermissionUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.redhelmet.alert2me.core.CoreFunctions._context;

public class MapFragment extends BaseFragment<MapViewModel, FragmentEventMapBinding> implements
        OnMapReadyCallback, GoogleMap.OnMarkerClickListener,
        GoogleMap.OnMyLocationClickListener,
        GoogleMap.OnMyLocationButtonClickListener,
        AutoCompleteLocation.AutoCompleteLocationListener {
    private static final String VIEW_MODEL_KEY = "viewModel";

    /**
     * Flag indicating whether a requested permission has been denied after returning in
     * {@link #onRequestPermissionsResult(int, String[], int[])}.
     */
    private boolean mPermissionDenied = false;

    private GoogleMap mMapView;
    private ClusterManager<ClusterMarker> clusterManager;
    private HashMap<String, String> _markerOptionsHashMap;
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
    protected Class<MapViewModel> getViewModelClass() {
        return MapViewModel.class;
    }

    @Override
    protected MapViewModel obtainViewModel() {
        Object data = getArguments().getSerializable(VIEW_MODEL_KEY);
        if (data instanceof MapViewModel) {
            return (MapViewModel) data;
        } else {
            throw new Error("viewModel must is not null");
        }
    }

    public static EventListFragment newInstance(MapViewModel viewModel) {
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
    }

    private void initView() {
        binder.clusterEvents.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (PreferenceUtils.hasKey(_context, getString(R.string.pref_cluster_map_state))) {
                    PreferenceUtils.removeFromPrefs(_context, getString(R.string.pref_cluster_map_state));
                    binder.clusterEvents.setImageResource(R.drawable.ic_cluster);
                    ProcessEvents(_events);
                } else {
                    binder.clusterEvents.setImageResource(R.drawable.ic_cluster_red);
                    PreferenceUtils.saveToPrefs(_context, getString(R.string.pref_cluster_map_state), true);
                    ProcessClusteredEvents(_events);
                }

                infoWindowClickedForMarkers();
            }
        });
    }

    /**
     * Show a dialog to the user requesting that GPS be enabled
     */
    private void showDialogGPS() {
        AlertDialog.Builder builder = new AlertDialog.Builder(_context);
        builder.setCancelable(false);
        builder.setTitle(getString(R.string.title_enableGPS));
        builder.setMessage(getString(R.string.msg_enableGPS));
        builder.setInverseBackgroundForced(true);
        builder.setPositiveButton(getString(R.string.btn_enable), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                startActivity(
                        new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
            }
        });
        builder.setNegativeButton(getString(R.string.btn_ignore), new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        return false;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMapView = googleMap;
        initMap(true);

        ProcessDetailEvent();

        setupCluster();

        applyFilters();

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
        mMapView.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
        binder.mapType1.setSelected(type == 1);
        binder.mapType2.setSelected(type == 2);
        binder.mapType3.setSelected(type == 3);
        setMapIcons(type);
    }

    public void setMapIcons(int type) {
        binder.mapType1.setImageResource(type == 1 ? R.drawable.ic_aerial_blue : R.drawable.ic_aerial);
        binder.mapType2.setImageResource(type == 2 ? R.drawable.ic_road_blue : R.drawable.ic_road);
        binder.mapType3.setImageResource(type == 3 ? R.drawable.ic_hybrid_blue : R.drawable.ic_hybrid);
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

            updateMapType(2);
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case Constant.LOCATION_PERMISSION_REQUEST_CODE:
                if (getBaseActivity().isPermissionGranted(permissions, grantResults, Manifest.permission.ACCESS_FINE_LOCATION)) {
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

    public void ProcessDetailEvent() {
        if (latlng != null) {
            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(latlng)
                    .zoom(15)
                    .bearing(0)
                    .tilt(45)
                    .build();
            mMapView.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }
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

    public ArrayList<Category> createConfigUtility(ArrayList[] data) {

        ArrayList<HashMap> categories = new ArrayList<HashMap>();
        ArrayList<HashMap> types = new ArrayList<HashMap>();
        ArrayList<HashMap> statuses = new ArrayList<HashMap>();

        HashMap<String, String> hash_categories = new HashMap<>();
        HashMap<String, String> hash_types = new HashMap<>();
        HashMap<String, String> hash_status = new HashMap<>();

        categories = (ArrayList<HashMap>) data[0];
        types = (ArrayList<HashMap>) data[1];
        statuses = (ArrayList<HashMap>) data[2];
        category_data = new ArrayList<Category>();

        for (int i = 0; i < categories.size(); i++) {
            Category category = new Category();
            hash_categories = categories.get(i);
            types_data = new ArrayList<CategoryType>();

            for (int t = 0; t < types.size(); t++) {
                statuses_data = new ArrayList<CategoryStatus>();
                hash_types = types.get(t);
                boolean typeValue = false;

                if (hash_types.get(DBController.KEY_REF_TYPE_CATEGORY_ID).equals(hash_categories.get(DBController.KEY_CATEGORY_ID))) {
                    typeValue = Boolean.valueOf(hash_types.get(DBController.KEY_CAT_TYPE_NOTIF_DEFAULT));
                }
                //status
                for (int s = 0; s < statuses.size(); s++) {
                    hash_status = statuses.get(s);
                    if (hash_status.get(DBController.KEY_REF_STATUS_CATEGORY_ID).equals(hash_categories.get(DBController.KEY_CATEGORY_ID))) {

                        CategoryStatus status_model = new CategoryStatus();

                        status_model.setName(hash_status.get(DBController.KEY_CAT_STATUS_NAME));
                        status_model.setDefaultOn(Boolean.valueOf(hash_status.get(DBController.KEY_CAT_STATUS_DEFAULT)));
                        status_model.setCanFilter(Boolean.valueOf(hash_status.get(DBController.KEY_CAT_STATUS_CAN_FILTER)));
                        status_model.setCode(hash_status.get(DBController.KEY_CAT_STATUS_CODE));
                        status_model.setDescription(hash_status.get(DBController.KEY_CAT_STATUS_DESC));
                        status_model.setPrimaryColor(hash_status.get(DBController.KEY_CAT_STATUS_PRIMARY_COLOR));
                        status_model.setSecondaryColor(hash_status.get(DBController.KEY_CAT_STATUS_SECONDARY_COLOR));
                        status_model.setTextColor(hash_status.get(DBController.KEY_CAT_STATUS_TEXT_COLOR));
                        status_model.setNotificationDefaultOn(typeValue);//Boolean.valueOf(hash_status.get(DBController.KEY_CAT_STATUS_NOTIF_DEFAULT))
                        status_model.setNotificationCanFilter(Boolean.valueOf(hash_status.get(DBController.KEY_CAT_STATUS_NOTIF_CAN_FILTER)));
                        statuses_data.add(status_model);

                    }
                }

                //==end


                if (hash_types.get(DBController.KEY_REF_TYPE_CATEGORY_ID).equals(hash_categories.get(DBController.KEY_CATEGORY_ID))) {
                    CategoryType type_model = new CategoryType();
                    type_model.setName(hash_types.get(DBController.KEY_CAT_TYPE_NAME));
                    type_model.setNameLabel(hash_types.get(DBController.KEY_CAT_TYPE_NAME));
                    type_model.setCode(hash_types.get(DBController.KEY_CAT_TYPE_CODE));
                    type_model.setCanFilter(Boolean.valueOf(hash_types.get(DBController.KEY_CAT_TYPE_CAN_FILTER)));
                    type_model.setDefaultOn(Boolean.valueOf(hash_types.get(DBController.KEY_CAT_TYPE_DEFAULT)));
                    type_model.setIcon(hash_types.get(DBController.KEY_CAT_TYPE_ICON));
                    type_model.setNotificationDefaultOn(Boolean.valueOf(hash_types.get(DBController.KEY_CAT_TYPE_NOTIF_DEFAULT)));
                    type_model.setNotificationCanFilter(Boolean.valueOf(hash_types.get(DBController.KEY_CAT_TYPE_NOTIF_CAN_FILTER)));
                    type_model.setStatuses(statuses_data);
                    types_data.add(type_model);

                }
            }


            category.setCategory(hash_categories.get(DBController.KEY_CATEGORY));
            category.setNameLabel(hash_categories.get(DBController.KEY_CATEGORY_NAME));
            category.setFilterDescription(hash_categories.get(DBController.KEY_CATEGORY_DESC));
            category.setDisplayOnly(Boolean.valueOf(hash_categories.get(DBController.KEY_CATEGORY_DISPLAY_ONLY)));
            category.setFilterOrder(hash_categories.get(DBController.KEY_CATEGORY_FILTER_ORDER));
            category.setTypes(types_data);


            category_data.add(category);
        }


        return category_data;

    }

    //Clusters
    private void ProcessClusteredEvents(List<Event> eventList) {
        if (clusterManager != null) {
            clusterManager.clearItems();

            if (_markerOptionsHashMap != null)
                _markerOptionsHashMap.clear();

            if (mMapView != null)
                mMapView.clear();

            Boolean defaultfilter = true;
            if (PreferenceUtils.hasKey(getBaseActivity(), getString(R.string.pref_map_isDefault))) {
                if ((boolean) PreferenceUtils.getFromPrefs(getBaseActivity(), getString(R.string.pref_map_isDefault), false)) {
                    defaultfilter = true;

                } else {
                    defaultfilter = false;
                }
            }


            for (int i = 0; i < eventList.size(); i++) {
                Event event = eventList.get(i);
                if (defaultfilter) {
                    if (event.isShowOn()) {
                        List<Area> areas = event.getArea();
                        for (int j = 0; j < areas.size(); j++) {
                            ClusterMarker customMarker = new ClusterMarker(eventList.get(i), areas.get(j));
                            clusterManager.addItem(customMarker);
                        }
                    }
                } else {
                    List<Area> areas = event.getArea();
                    for (int j = 0; j < areas.size(); j++) {
                        ClusterMarker customMarker = new ClusterMarker(eventList.get(i), areas.get(j));
                        clusterManager.addItem(customMarker);
                    }
                }

            }

            float zoom = mMapView.getCameraPosition().zoom;
            zoom = zoom + 0.1f;
            mMapView.moveCamera(CameraUpdateFactory.zoomTo(zoom));
            clusterManager.cluster();
        }
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

    public void applyFilters() {

//        if((PreferenceUtils.hasKey(_context, getString(R.string.pref_cluster_map_state)))) {
//            PreferenceUtils.saveToPrefs(_context,getString(R.string.pref_map_isDefault),true);
//        }
//
//        Boolean valueDummy = (boolean) PreferenceUtils.getFromPrefs(_context, getString(R.string.pref_map_isDefault), false);
//        Log.d("isDefault",valueDummy.toString());

        if (PreferenceUtils.hasKey(_context, getString(R.string.pref_map_isDefault))) {
            if ((boolean) PreferenceUtils.getFromPrefs(_context, getString(R.string.pref_map_isDefault), false)) {
                defaultFilter(dbController.getDefaultMapFilter());

            } else {
                customFilter(dbController.getCustomCatName(0));
            }
        }

//        else{
        if (PreferenceUtils.hasKey(_context, getString(R.string.pref_cluster_map_state))) {
            ProcessClusteredEvents(_events);
            clusterBtn.setImageResource(R.drawable.ic_cluster_red);
        } else {
            ProcessEvents(_events);
            clusterBtn.setImageResource(R.drawable.ic_cluster);

        }

        //}
    }

    //marker events
    private void ProcessEvents(List<Event> eventList) {

        if (_context != null) {

            if (clusterManager != null) {
                clusterManager.clearItems();


            }
            if (mMapView != null) {

                mMapView.clear();
            }
            if (_markerOptionsHashMap != null) {
                _markerOptionsHashMap.clear();
            }

            Boolean defaultfilter = true;
            if (PreferenceUtils.hasKey(_context, getString(R.string.pref_map_isDefault))) {
                if ((boolean) PreferenceUtils.getFromPrefs(_context, getString(R.string.pref_map_isDefault), false)) {
                    defaultfilter = true;

                } else {
                    defaultfilter = false;
                }
            }

            for (int i = 0; i < eventList.size(); i++) {
                try {
                    Event event = eventList.get(i);

                    if (defaultfilter) {
                        if (event.isShowOn()) {
                            List<Area> areas = event.getArea();
                            for (int j = 0; j < areas.size(); j++) {

                                MarkerOptions markerOptions = eventUtils.eventToMarker(event, areas.get(j));

                                BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.fromBitmap(iconUtils.createEventIcon(R.layout.custom_map_layer_icon, event, event.getPrimaryColor(), false, false, ""));
                                markerOptions.icon(bitmapDescriptor);
                                Marker marker = mMapView.addMarker(markerOptions);
                                marker.setTag(event);
                                String eventId = String.format("%s__%s__%s", event.getId(), event.getCategory(), event.getStatus());
                                Log.e("DefaulMapPins:", eventId);
                                _markerOptionsHashMap.put(marker.getId(), eventId);

                            }
                        }
                    } else {
                        List<Area> areas = event.getArea();
                        for (int j = 0; j < areas.size(); j++) {

                            MarkerOptions markerOptions = eventUtils.eventToMarker(event, areas.get(j));

                            BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.fromBitmap(iconUtils.createEventIcon(R.layout.custom_map_layer_icon, event, event.getPrimaryColor(), false, false, ""));
                            markerOptions.icon(bitmapDescriptor);
                            Marker marker = mMapView.addMarker(markerOptions);
                            marker.setTag(event);
                            String eventId = String.format("%s__%s__%s", event.getId(), event.getCategory(), event.getStatus());
                            _markerOptionsHashMap.put(marker.getId(), eventId);
                        }
                    }


                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    public void defaultFilter(ArrayList<HashMap> defaultDataWz) {
        mapOverlay = new ArrayList<>();
        mapLayers = new ArrayList<>();

        _events = new ArrayList<>();

        Gson gson = new Gson();

        if (PreferenceUtils.hasKey(_context, getString(R.string.pref_map_isDefault)) && (boolean) PreferenceUtils.getFromPrefs(_context, getString(R.string.pref_map_isDefault), false)) {
            String values = (String) PreferenceUtils.getFromPrefs(_context, getString(R.string.pref_map_filter), "");
            ArrayList<String> defValues = gson.fromJson(values, ArrayList.class);

            try {
                for (int i = 0; i < defaultDataWz.size(); i++) {
                    HashMap<String, String> data = defaultDataWz.get(i);
                    for (int k = 0; k < defValues.size(); k++) {
                        if (defValues.get(k).toString().equalsIgnoreCase(data.get(DBController.KEY_DEFAULT_CATEGORY_ID))) {
                            JSONArray jsonObject = new JSONArray(data.get(DBController.KEY_DEFAULT_CATEGORY_DISPLAYFILTER));
                            JSONObject filterData = jsonObject.getJSONObject(0);
                            JSONArray overlayData = filterData.getJSONArray("overlays");
                            JSONArray layerData = filterData.getJSONArray("layers");
                            for (int j = 0; j < layerData.length(); j++) {
                                mapLayers.add((String) layerData.get(j));
                            }
                            for (int j = 0; j < overlayData.length(); j++) {
                                mapOverlay.add((String) overlayData.get(j));
                            }
                        }

                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        for (int i = 0; i < _tempEvents.size(); i++) {
            try {
                Event event = _tempEvents.get(i);
                for (int j = 0; j < mapLayers.size(); j++) {
                    if (event.getGroup().toString().equalsIgnoreCase(mapLayers.get(j))) {
                        event.setShowOn(true);
                    }

                }
                if (event.isAlwaysOn()) {
                    event.setShowOn(event.isAlwaysOn());
                }

                if (event.isShowOn()) {
                    _events.add(event);
                }

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        if (PreferenceUtils.hasKey(_context, getString(R.string.pref_cluster_map_state))) {
            ProcessClusteredEvents(_events);
            clusterBtn.setImageResource(R.drawable.ic_cluster_red);
        } else {
            ProcessEvents(_events);
            clusterBtn.setImageResource(R.drawable.ic_cluster);
        }


        SetEventListDataSource();
        wmsLayers();
    }

    private void SetEventListDataSource() {

        SortList();

        if (listEventIcon != null) {
            if (_events.size() > 0) {
                mAdapter = new EventListRecyclerAdapter(getActivity(), _events, false);
                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
                listEventIcon.setLayoutManager(mLayoutManager);
                listEventIcon.setItemAnimator(new DefaultItemAnimator());
                listEventIcon.setAdapter(mAdapter);
                mAdapter.notifyDataSetChanged();
            } else {


                String emptyText = _context.getString(R.string.no_data_to_display);

                EmptyListRecyclerAdapter emptyListRecyclerAdapter = new EmptyListRecyclerAdapter(emptyText);
                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext());
                listEventIcon.setLayoutManager(mLayoutManager);
                listEventIcon.setItemAnimator(new DefaultItemAnimator());
                listEventIcon.setAdapter(emptyListRecyclerAdapter);
                emptyListRecyclerAdapter.notifyDataSetChanged();
            }
            mProgress.setVisibility(View.INVISIBLE);
        }
    }

    public void customFilter(ArrayList[] customFilter) {
        mapOverlay = new ArrayList<>();
        mapLayers = new ArrayList<>();
        _events = new ArrayList<>();


        ArrayList<HashMap<String, CategoryFilter>> categoryFilters = new ArrayList<HashMap<String, CategoryFilter>>();

        Gson gson = new Gson();
        String values = (String) PreferenceUtils.getFromPrefs(_context, getString(R.string.pref_map_filter), "");
        categoryFilters = new Gson().fromJson(values, new TypeToken<ArrayList<HashMap<String, CategoryFilter>>>() {
        }.getType());

        for (int j = 0; j < _tempEvents.size(); j++) {
            Event event = _tempEvents.get(j);
            List<Area> areas = event.getArea();


            for (int i = 0; i < categoryFilters.size(); i++) {
                HashMap<String, CategoryFilter> hashCat = categoryFilters.get(i);
                if (hashCat.containsKey(event.getCategory())) {
                    CategoryFilter catFilter = hashCat.get(event.getCategory());
                    for (int k = 0; k < catFilter.getTypes().size(); k++) {
                        CategoryTypeFilter categoryTypeFilter = catFilter.getTypes().get(k);
                        if (event.getEventTypeCode().equalsIgnoreCase(categoryTypeFilter.getCode())) {
                            for (String statusCode : categoryTypeFilter.getStatus()) {
                                if (statusCode.equalsIgnoreCase(event.getStatusCode())) {
//                                    event.setShowOn(true);
                                    _events.add(event);
                                }
                            }
                        }
                    }
                }
            }
        }
        if (PreferenceUtils.hasKey(_context, getString(R.string.pref_cluster_map_state))) {
            ProcessClusteredEvents(_events);
            clusterBtn.setImageResource(R.drawable.ic_cluster_red);
        } else {
            ProcessEvents(_events);
            clusterBtn.setImageResource(R.drawable.ic_cluster);
        }


        SetEventListDataSource();


    }

    public void wmsLayers() {
        WmsTileProvider provider = tileProviderFactory.GetWmsTileProvider(baseWmsURL, mapOverlay);
        if (provider != null && mMapView != null)
            mMapView.addTileOverlay(new TileOverlayOptions().tileProvider(provider));

    }

    private void InitializeDetailsPage(Event event) {

        if (event != null) {
            Intent intent = new Intent(getActivity(), EventDetailsActivity.class);
            intent.putExtra("event", event);
            startActivity(intent);
        } else {
            Toast.makeText(getContext(), "Unable to get event details", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == Activity_Filter_Result) {
            if (resultCode == Activity.RESULT_OK) {
                applyFilters();
            }

        }
    }//onActivityResult
}
