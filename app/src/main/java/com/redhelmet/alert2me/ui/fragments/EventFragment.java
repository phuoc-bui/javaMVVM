package com.redhelmet.alert2me.ui.fragments;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.redhelmet.alert2me.adapters.EmptyListRecyclerAdapter;
import com.redhelmet.alert2me.adapters.EventListRecyclerAdapter;
import com.redhelmet.alert2me.adapters.RecyclerTouchListener;
import com.redhelmet.alert2me.autocomplete.AutoCompleteLocation;
import com.redhelmet.alert2me.core.CoreFunctions;
import com.redhelmet.alert2me.core.DBController;
import com.redhelmet.alert2me.core.RequestHandler;
import com.redhelmet.alert2me.core.TileProviderFactory;
import com.redhelmet.alert2me.core.WmsTileProvider;
import com.redhelmet.alert2me.domain.ExceptionHandler;
import com.redhelmet.alert2me.domain.util.EventUtils;
import com.redhelmet.alert2me.domain.util.IconUtils;
import com.redhelmet.alert2me.domain.util.PreferenceUtils;
import com.redhelmet.alert2me.domain.util.Utility;
import com.redhelmet.alert2me.interfaces.ServerCallback;
import com.redhelmet.alert2me.model.Area;
import com.redhelmet.alert2me.model.Category;
import com.redhelmet.alert2me.model.CategoryFilter;
import com.redhelmet.alert2me.model.CategoryStatus;
import com.redhelmet.alert2me.model.CategoryType;
import com.redhelmet.alert2me.model.CategoryTypeFilter;
import com.redhelmet.alert2me.model.Event;
import com.redhelmet.alert2me.model.EventFeed;
import com.redhelmet.alert2me.model.EventGroup;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
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
import com.google.common.collect.ComparisonChain;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import com.redhelmet.alert2me.R;

import com.redhelmet.alert2me.core.Constants;
import com.redhelmet.alert2me.domain.util.CustomClusterRenderer;
import com.redhelmet.alert2me.model.CustomMarker;
import com.redhelmet.alert2me.ui.activity.AddObservation;
import com.redhelmet.alert2me.ui.activity.ClusterEventList;
import com.redhelmet.alert2me.ui.activity.EventDetailsActivity;
import com.redhelmet.alert2me.ui.activity.EventMapFilter;
import com.redhelmet.alert2me.ui.activity.HomeActivity;

import static com.redhelmet.alert2me.R.id.map;
//import static com.redhelmet.alert2me.R.id.match_global_nicknames;


public class EventFragment extends Fragment implements OnMapReadyCallback,
        GoogleMap.OnMarkerClickListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,SwipeRefreshLayout.OnRefreshListener,LocationListener,AutoCompleteLocation.AutoCompleteLocationListener {

    SwipeRefreshLayout mSwipeRefreshLayout;
    FloatingActionMenu observation;
    FloatingActionButton withImage,withoutImage;
    private ViewSwitcher eventViewSwitch;
    private View view;
    private RelativeLayout mapView, eventView;
    private Button mapBtn, eventBtn;
    private ImageButton clusterBtn, locationBtn;
    private Menu mOptionsMenu;
    GoogleMap mMapView;
    public final int GRANTED_FINE_LOCATION = 999;
    private static final int REQUEST_COARSE_LOCATION = 8;
    Intent intent;
    int Activity_Filter_Result = 77;
    private GoogleApiClient googleApiClient;
    TileProviderFactory tileProviderFactory;
    String baseWmsURL = "";
    public Context _context;
    private String toolbarHeading = "Event Map";
    private CoreFunctions cf;
    RecyclerView listEventIcon;
    EventListRecyclerAdapter mAdapter;
    DBController dbController = null;
    public ArrayList<Category> category_data = new ArrayList<Category>();
    public ArrayList<CategoryType> types_data = new ArrayList<CategoryType>();
    public ArrayList<CategoryStatus> statuses_data = new ArrayList<CategoryStatus>();
    public ArrayList<String> mapOverlay = new ArrayList<String>();
    public ArrayList<String> mapLayers = new ArrayList<String>();

    Category cat;
    ProgressBar mProgress;
    private List<Event> _events;
    private List<Event> _tempEvents;
    private List<Event> _tempEventsMain;
    private ClusterManager<CustomMarker> clusterManager;
    EventUtils eventUtils;
    IconUtils iconUtils;
    private HashMap<String, String> _markerOptionsHashMap;
    LatLng latlng = null;
    boolean selectedMapState = true;
    private String sortByList;
    private LocationManager locationManager;
    AutoCompleteLocation autoCompleteLocation ;

    private ImageButton selectedMapType;
    private ImageButton mapType1;
    private ImageButton mapType2;
    private ImageButton mapType3;

    RequestQueue queue;
    StringRequest volleyRequest;

    public EventFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler());
        setHasOptionsMenu(true);
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true); //To set vector change resource


    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this._context = context;

        // Code here
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            // Code here
            this._context = getActivity();

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (view != null) {
            ViewGroup parent = (ViewGroup) view.getParent();
            if (parent != null)
                parent.removeView(view);
        }
        try {
            view = inflater.inflate(R.layout.fragment_event, container, false);
        } catch (InflateException e) {
        /* map is already there, just return view as it is */
        }

        if (PreferenceUtils.hasKey(_context, "detailLat")) {
            double def = 0.0;
            double lat = (Double) PreferenceUtils.getFromPrefs(_context, "detailLat", def);
            double lon = (Double) PreferenceUtils.getFromPrefs(_context, "detailLon", def);
            latlng = new LatLng(lat, lon);
            PreferenceUtils.removeFromPrefs(_context, "detailLat");
            PreferenceUtils.removeFromPrefs(_context, "detailLon");
        }

         initializationControls();
        initializeListener();
        InitializeMapTypesListener();
        viewSwitch(true);
//        downloadFile();

        getEvent();

        String str = "Bearer " + PreferenceUtils.getFromPrefs(_context, getString(R.string.pref_user_token), "");
        Log.d("Token", str);
        return view;
    }

    private void getEvent() {
        queue = RequestHandler.getInstance(_context.getApplicationContext()).getRequestQueue(); //Obtain the instance

        volleyRequest = new StringRequest(Request.Method.GET, getString(R.string.api_url) + "events", // getting config url from COREFUNCTIONS
                new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                mProgress.setVisibility(View.INVISIBLE);
                mSwipeRefreshLayout.setRefreshing(false);
                if (response != null) {
                    GetEvents(response);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mProgress.setVisibility(View.INVISIBLE);
                // Stopping swipe refresh
                mSwipeRefreshLayout.setRefreshing(false);
                Toast.makeText(_context, getString(R.string.msgUnableToGetEvent), Toast.LENGTH_LONG).show();

            }
        });

        //TODO: Change the retry policy
        volleyRequest.setRetryPolicy(new DefaultRetryPolicy(5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(volleyRequest);
    }

    private void InitializeMapTypesListener() {
        selectedMapType = (ImageButton) view.findViewById(R.id.selected_map_type);


        selectedMapType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setMapIcons();
                hideShowMapTypeButtons();

            }
        });
        mapType1 = (ImageButton) view.findViewById(R.id.map_type1);
        mapType1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mMapView.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                mapType1.setSelected(true);
                mapType2.setSelected(false);
                mapType3.setSelected(false);
                setMapIcons();
                hideShowMapTypeButtons();
            }
        });

        mapType2 = (ImageButton) view.findViewById(R.id.map_type2);
        mapType2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mMapView.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                mapType1.setSelected(false);
                mapType2.setSelected(true);
                mapType3.setSelected(false);


                hideShowMapTypeButtons();
            }
        });
        mapType3 = (ImageButton) view.findViewById(R.id.map_type3);
        mapType3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mMapView.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                mapType1.setSelected(false);
                mapType2.setSelected(false);
                mapType3.setSelected(true);
                setMapIcons();

                hideShowMapTypeButtons();
            }
        });



    }


    public  void setMapIcons() {
        if(mapType1.isSelected()) {

            mapType1.setImageResource(R.drawable.ic_aerial_blue);
            mapType2.setImageResource(R.drawable.ic_road);
            mapType3.setImageResource(R.drawable.ic_hybrid);
        }
        else if (mapType2.isSelected()) {
            mapType1.setImageResource(R.drawable.ic_aerial);
            mapType2.setImageResource(R.drawable.ic_road_blue);
            mapType3.setImageResource(R.drawable.ic_hybrid);
        }
        else {
            mapType1.setImageResource(R.drawable.ic_aerial);
            mapType2.setImageResource(R.drawable.ic_road);
            mapType3.setImageResource(R.drawable.ic_hybrid_blue);
        }
    }

    public void hideShowMapTypeButtons() {
        if (mapType1 != null && mapType2 != null && mapType3 != null) {

            if (mapType1.getVisibility() == View.VISIBLE) {
                mapType1.setVisibility(View.GONE);
                mapType2.setVisibility(View.GONE);
                mapType3.setVisibility(View.GONE);
            } else {
                mapType1.setVisibility(View.VISIBLE);
                mapType2.setVisibility(View.VISIBLE);
                mapType3.setVisibility(View.VISIBLE);

            }
        }
    }
    private void initializationControls() {


        googleApiClient = new GoogleApiClient.Builder(_context)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
        googleApiClient.connect();
        locationManager = (LocationManager) _context.getSystemService(Context.LOCATION_SERVICE);

       autoCompleteLocation =
                (AutoCompleteLocation) view.findViewById(R.id.autocomplete_location);
        autoCompleteLocation.setAutoCompleteTextListener(this);


        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(map);
        mapFragment.getMapAsync(this);

        eventViewSwitch = (ViewSwitcher) view.findViewById(R.id.event_view_switch);
        mapView = (RelativeLayout) view.findViewById(R.id.mapView);
        eventView = (RelativeLayout) view.findViewById(R.id.eventView);
        mapBtn = (Button) view.findViewById(R.id.mapBtn);
        eventBtn = (Button) view.findViewById(R.id.eventBtn);
        clusterBtn = (ImageButton) view.findViewById(R.id.cluster_events);
        locationBtn = (ImageButton) view.findViewById(R.id.location_map);
        observation = (FloatingActionMenu) view.findViewById(R.id.menuFloat);
        withImage = (FloatingActionButton) view.findViewById(R.id.with);
        withoutImage = (FloatingActionButton) view.findViewById(R.id.without);
        withoutImage.setImageResource(R.drawable.icon_no_camera);
        withImage.setImageResource(R.drawable.icon_camera_fab);

        observation.setIconAnimated(false);



        cf = new CoreFunctions(_context);
        dbController = new DBController(_context);
        cat = Category.getInstance();
        mProgress = (ProgressBar) getActivity().findViewById(R.id.toolbar_progress_bar);
        _events = new ArrayList<Event>();
        _tempEvents = new ArrayList<Event>();

        _tempEventsMain = new ArrayList<Event>();
        eventUtils = new EventUtils();
        iconUtils = new IconUtils(_context);
        //googleApiClient = new GoogleApiClient.Builder(_context, this, this).addApi(LocationServices.API).build();
        tileProviderFactory = new TileProviderFactory();
        listEventIcon = (RecyclerView) view.findViewById(R.id.listEventIcon);
        LinearLayoutManager llm = new LinearLayoutManager(_context);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        listEventIcon.setLayoutManager(llm);
        listEventIcon.addOnItemTouchListener(new RecyclerTouchListener(_context, listEventIcon, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                if (_events.size() > 0) {
                    Event event = _events.get(position);

                    if (event != null) {
                        Intent intent = new Intent(getActivity(), EventDetailsActivity.class);
                        intent.putExtra("event", event);
                        startActivity(intent);
                    } else {
                        Toast.makeText(_context,  getString(R.string.msgUnableToGetEDetails), Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

        if (!PreferenceUtils.hasKey(_context, getString(R.string.pref_map_isDefault))) {
            PreferenceUtils.saveToPrefs(_context, getString(R.string.pref_map_isDefault), true);

            ArrayList <EventGroup> default_data = new ArrayList < EventGroup > ();
            ArrayList<String> defValues= new ArrayList<String>();
            Gson gson = new Gson();
            ArrayList<HashMap> defaultDataWz =   dbController.getDefaultMapFilter();
            for (int i = 0; i < defaultDataWz.size(); i++) {
                HashMap<String, String> data = defaultDataWz.get(i);
                EventGroup defaultGroup = new EventGroup();
                defaultGroup.setId(Integer.parseInt(data.get(DBController.KEY_DEFAULT_CATEGORY_ID)));
                defaultGroup.setName(data.get(DBController.KEY_DEFAULT_CATEGORY_NAME));
                defaultGroup.setDescription(data.get(DBController.KEY_DEFAULT_CATEGORY_DESC));
                defaultGroup.setDisplayOn(Boolean.valueOf(data.get(DBController.KEY_DEFAULT_CATEGORY_DISPLAY_ON)));
                defaultGroup.setDisplayToggle(Boolean.valueOf(data.get(DBController.KEY_DEFAULT_CATEGORY_DISPLAY_TOGGLE)));
                defaultGroup.setDisplayOnly(Boolean.valueOf(data.get(DBController.KEY_DEFAULT_CATEGORY_DISPLAY_ONLY)));
                defaultGroup.setFilterOn(Boolean.valueOf(data.get(DBController.KEY_DEFAULT_CATEGORY_FILTER_ON)));
                defaultGroup.setFilterToggle(Boolean.valueOf(data.get(DBController.KEY_DEFAULT_CATEGORY_FILTER_TOGGLE)));
                default_data.add(defaultGroup);
            }

            for (int i = 0; i < default_data.size(); i++) {
                EventGroup ev = new EventGroup();
                ev = default_data.get(i);
                if (ev.isFilterOn()) {
                    defValues.add(String.valueOf(ev.getId()));
                }
            }


            PreferenceUtils.saveToPrefs(_context,getString(R.string.pref_map_filter),gson.toJson(defValues));

        }

        setLocation();

        if (PreferenceUtils.hasKey(_context, getString(R.string.pref_basewms_url)))
            baseWmsURL = (String) PreferenceUtils.getFromPrefs(_context, getString(R.string.pref_basewms_url), "");

        if (baseWmsURL == "") baseWmsURL = "http://ex-dev-mapping.ripeintel.info/ra-wms-proxy/wms";


        // SwipeRefreshLayout
        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_container);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary,
                android.R.color.holo_green_dark,
                android.R.color.holo_orange_dark,
                android.R.color.holo_blue_dark);

        /**
         * Showing Swipe Refresh animation on activity create
         * As animation won't start on onCreate, post runnable is used
         */
        mSwipeRefreshLayout.post(new Runnable() {

            @Override
            public void run() {

                refeshData();
            }
        });

    }

    /**
     * This method is called when swipe refresh is pulled down
     */
    @Override
    public void onRefresh() {

        //Get USers' location

        setLocation();
// Fetching data from server
        refeshData();
    }


    public void refeshData() {
        if(mSwipeRefreshLayout != null){
            mSwipeRefreshLayout.setRefreshing(true);
        }
        getEvent();
    }

    @Override
    public void OnMapReadyCallback() {

    }

    @Override
    public void OnMapLoadedCallback() {

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



    @Override
    public void onLocationChanged(Location location) {
        if (location != null && googleApiClient != null && googleApiClient.isConnected()) {
            PreferenceUtils.saveToPrefs(_context, Constants.KEY_USERLATITUDE, String.valueOf(location.getLatitude()));
            PreferenceUtils.saveToPrefs(_context, Constants.KEY_USERLONGITUDE, String.valueOf(location.getLongitude()));
            LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
        }
    }
    public void initializeListener() {


        withImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                observation.close(true);
                intent = new Intent(getActivity(), AddObservation.class);
                startActivity(intent);

            }
        });
        withoutImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                observation.close(true);
                intent = new Intent(getActivity(), AddObservation.class);
                startActivity(intent);

            }
        });
        mapBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                viewSwitch(true);
            }
        });
        eventBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                viewSwitch(false);
            }
        });
        clusterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (PreferenceUtils.hasKey(_context, getString(R.string.pref_cluster_map_state))) {
                    PreferenceUtils.removeFromPrefs(_context, getString(R.string.pref_cluster_map_state));
                    clusterBtn.setImageResource(R.drawable.ic_cluster);
                    ProcessEvents(_events);
                } else {
                    clusterBtn.setImageResource(R.drawable.ic_cluster_red);
                    PreferenceUtils.saveToPrefs(_context, getString(R.string.pref_cluster_map_state), true);
                    ProcessClusteredEvents(_events);
                }

                infoWindowClickedForMarkers();
            }
        });

        locationBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkLocationandAddToMap();
            }
        });
    }


    public void viewSwitch(boolean state) {

        if (state) {
            mapBtn.setBackgroundResource(R.drawable.button_red_bottom_border);
            eventBtn.setBackgroundResource(R.drawable.border_shadow);

            toolbarHeading = getString(R.string.lblEvent) + " " + getString(R.string.lblMap);
            selectedMapState = true;

            if (eventViewSwitch.getCurrentView() != mapView) {
                eventViewSwitch.showNext();
            }
        } else {

            if (eventViewSwitch.getCurrentView() != eventView) {

                eventBtn.setBackgroundResource(R.drawable.button_red_bottom_border);
                mapBtn.setBackgroundResource(R.drawable.border_shadow);

                toolbarHeading = getString(R.string.lblEvent) + " " + getString(R.string.lblList);
                selectedMapState = false;
                eventViewSwitch.showPrevious();
            }

            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(view.getContext().INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getApplicationWindowToken(), 0);


        }

        Activity activity = getActivity();
        if (activity instanceof HomeActivity) {
            HomeActivity home = (HomeActivity) activity;
            home.initializeToolbar(toolbarHeading);
        }



   updateOptionsMenu();
    }

    public boolean checkFile() {
        File file = new File(_context.getFilesDir() + "/Downloads/events_full.json");
        if (file.exists())
            return true;
        return false;
    }

    private Event SetDistanceForEvents(Event event) {
//        if (_events != null) {
//            for (int i = 0; i < _events.size(); i++) {
//                Event event = _events.get(i);
                List<Area> areas = event.getArea();

                Area area = areas.get(0);
                Location userLocation = new Location("User Location"); ;
                    if(PreferenceUtils.hasKey(_context, Constants.KEY_USERLATITUDE) && (PreferenceUtils.hasKey(_context, Constants.KEY_USERLONGITUDE))) {

                        final Double latitude  =  Double.valueOf((String ) PreferenceUtils.getFromPrefs(_context, Constants.KEY_USERLATITUDE, "0"));
                        final Double longitude  =  Double.valueOf((String ) PreferenceUtils.getFromPrefs(_context, Constants.KEY_USERLONGITUDE, "0"));
                        userLocation = new Location("User Location");
                        userLocation.setLatitude(latitude);
                        userLocation.setLongitude(longitude);
                    }
                    Location eventLocation = new Location("EventLocation");
                    eventLocation.setLatitude(area.getLatitude());
                    eventLocation.setLongitude(area.getLongitude());

                    if (userLocation.getLatitude() == 0 && userLocation.getLongitude() == 0) {

                        event.setDistanceTo((double) 0.0f);
                    } else {
                        double isdistance = userLocation.distanceTo(eventLocation);
                        event.setDistanceTo(isdistance);
                    }
//
//                }
//            }
        return  event;
        }


    private void setLocation() {

        if (Utility.isLocationEnabled(_context)) {

            long UPDATE_INTERVAL = 1000;
            long FASTEST_INTERVAL = 1000;

            LocationRequest mLocationRequest = LocationRequest.create()
                    .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                    .setInterval(UPDATE_INTERVAL)
                    .setFastestInterval(FASTEST_INTERVAL);
            if (ActivityCompat.checkSelfPermission(_context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(_context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_COARSE_LOCATION);

            }
            googleApiClient.connect();

            if(googleApiClient != null)
            {
                if (googleApiClient.isConnected())
                    LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, mLocationRequest, this);
            }


        }
        else {
            showDialogGPS();
        }
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
    public void GetEvents(String jsonFileContent) {


        ArrayList<Category> category_db = new ArrayList<Category>();
        _markerOptionsHashMap = new HashMap<>();

        if (jsonFileContent != null) {

            if (dbController != null) {

                category_db = createConfigUtility(dbController.getCustomCatName(0));
            }
            Gson gson = new Gson();
            try {
                EventFeed eventFeed = gson.fromJson(jsonFileContent, EventFeed.class);
                if (eventFeed != null) {


                     List<Event> allEvents = new ArrayList<Event>();
                    for (Event event : eventFeed.getEvents()) {

                        for (Category category : category_db) {
                            if (event.getCategory().equals(category.getCategory())) {

                                for (CategoryType categoryType : category.getTypes()) {
                                    if (event.getEventTypeCode().equals(categoryType.getCode())) {
                                        for (CategoryStatus categoryStatus : categoryType.getStatuses()) {
                                            if (event.getStatusCode().equals(categoryStatus.getCode())) {
                                                event.setPrimaryColor(categoryStatus.getPrimaryColor());
                                                event.setSecondaryColor(categoryStatus.getSecondaryColor());
                                                event.setTextColor(categoryStatus.getTextColor());
                                                event.setName(category.getNameLabel());

                                                List<Area> areas = event.getArea();
                                                for (int k = 0; k < areas.size(); k++) {

                                                    List<Area> singleAreas =  new ArrayList<>();
                                                    singleAreas.add(areas.get(k ));
                                                    event.setArea(singleAreas);

                                                    allEvents.add(SetDistanceForEvents(event));

                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    _events = allEvents; //eventFeed.getEvents();

                    _tempEvents = allEvents;//eventFeed.getEvents();

                    applyFilters();


//                      _preferenceUtils.AddToPreferences(ConfigurationKeys.EVENT_PREFERENCE_KEY, eventFeed);
//                    eventFeed.setEvents(ApplyFilters(eventFeed.getEvents(), isStateWideFilter, isMap));
//                    eventFeed.setEvents(SortBySeverity(eventFeed.getEvents()));
//                    _eventsView.onGetEvents(eventFeed, isClustered);

                    if (PreferenceUtils.hasKey(_context, getString(R.string.pref_cluster_map_state))) {
                        ProcessClusteredEvents(_events);
                        clusterBtn.setImageResource(R.drawable.ic_cluster_red);
                    } else {
                        ProcessEvents(_events);
                        clusterBtn.setImageResource(R.drawable.ic_cluster);

                    }

                    ProcessDetailEvent();

                } else {
                   // downloadFile();
                }
            } catch (Throwable t) {
                t.printStackTrace();
            }
        } else {
           // downloadFile();
        }
//     applyFilters();
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
    private void SortList() {


        if (PreferenceUtils.hasKey(_context,Constants.SORT_PREFERENCE_KEY)) {
            String sortBy = (String) PreferenceUtils.getFromPrefs(_context,Constants.SORT_PREFERENCE_KEY,"2");
            switch (sortBy) {
                case "0":
                    SortByDistance();
                    break;
                case "1":
                    SortByTime();
                    break;
                case "2":
                    SortByStatus();
                    break;
                default:
                    SortByStatus();
                    break;
            }
        } else {
            PreferenceUtils.saveToPrefs(_context,Constants.SORT_PREFERENCE_KEY,"2");
            SortByStatus();

        }

    }

    private void ShowSortDialog() {

        final CharSequence[] items = {getString(R.string.listSortOrderDistance), getString(R.string.listSortOrderTime), getString(R.string.listSortOrderStatus)};
        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(_context, R.style.MaterialThemeDialog);
        int selectedSortItem = 2;

        if (PreferenceUtils.hasKey(_context,Constants.SORT_PREFERENCE_KEY)) {

            String selectedSort = (String)  PreferenceUtils.getFromPrefs(_context,Constants.SORT_PREFERENCE_KEY,"2");
            if (selectedSort != null && !selectedSort.equals(""))
                selectedSortItem = Integer.parseInt(selectedSort);
        }
        sortByList = String.valueOf(selectedSortItem);
        dialogBuilder.setTitle(getString(R.string.listSortOrder));

        dialogBuilder.setSingleChoiceItems(items, selectedSortItem, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                sortByList = String.valueOf(i);
            }
        });
        dialogBuilder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        dialogBuilder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                PreferenceUtils.saveToPrefs(_context,Constants.SORT_PREFERENCE_KEY,sortByList);
                SortList();
                dialogInterface.dismiss();

            }
        });
        AlertDialog dialog = dialogBuilder.create();
        dialog.show();
    }

    private void SortByTime() {
        Collections.sort(_events, new Comparator<Event>() {
            @Override
            public int compare(Event event, Event event2) {
                return event.getUpdated() < event2.getUpdated() ? 1 : (event.getUpdated() > event2.getUpdated() ? -1 : 0);
            }
        });
    }

    private void SortByDistance() {

        Collections.sort(_events, new Comparator<Event>() {
            @Override
            public int compare(Event event, Event event2) {
                return event.getDistanceTo() > event2.getDistanceTo() ? 1 : (event.getDistanceTo() < event2.getDistanceTo() ? -1 : 0);
            }
        });
    }

    private void SortByStatus() {
        Collections.sort(_events, new Comparator<Event>() {
            @Override
            public int compare(Event event, Event event2) {
                return ComparisonChain.start().compare(event2.getSeverity(), event.getSeverity()).compare(event2.getUpdated(), event.getUpdated()).result();
                //return event.getSeverity() < event2.getSeverity() ? 1 : (event.getSeverity() > event2.getSeverity() ? -1 : 0);
            }
        });
    }

    //marker events
    private void ProcessEvents(List<Event> eventList) {

        if (_context != null) {

            if (clusterManager != null) {
                clusterManager.clearItems();


            }
if(mMapView != null) {

    mMapView.clear();
}
if(_markerOptionsHashMap != null){
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

                    if(defaultfilter) {
                        if (event.isShowOn()) {
                            List<Area> areas = event.getArea();
                            for (int j = 0; j < areas.size(); j++) {

                                MarkerOptions markerOptions = eventUtils.eventToMarker(event, areas.get(j));

                                BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.fromBitmap(iconUtils.createEventIcon(R.layout.custom_map_layer_icon, event, event.getPrimaryColor(), false, false, ""));
                                markerOptions.icon(bitmapDescriptor);
                                Marker marker = mMapView.addMarker(markerOptions);
                                marker.setTag(event);
                                String eventId = String.format("%s__%s__%s", event.getId(), event.getCategory(), event.getStatus());
                                Log.e("DefaulMapPins:",eventId );
                                _markerOptionsHashMap.put(marker.getId(), eventId);

                            }
                        }
                    }
                    else {
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

    //Clusters
    private void ProcessClusteredEvents(List<Event> eventList) {
        if (clusterManager != null) {
            clusterManager.clearItems();

            if(_markerOptionsHashMap != null)
            _markerOptionsHashMap.clear();

            if(mMapView != null)
            mMapView.clear();

            Boolean defaultfilter = true;
            if (PreferenceUtils.hasKey(_context, getString(R.string.pref_map_isDefault))) {
                if ((boolean) PreferenceUtils.getFromPrefs(_context, getString(R.string.pref_map_isDefault), false)) {
                    defaultfilter = true;

                } else {
                    defaultfilter = false;
                }
            }


            for (int i = 0; i < eventList.size(); i++) {
                Event event = eventList.get(i);
                if(defaultfilter) {
                    if (event.isShowOn()) {
                        List<Area> areas = event.getArea();
                        for (int j = 0; j < areas.size(); j++) {
                            CustomMarker customMarker = new CustomMarker(eventList.get(i), areas.get(j));
                            clusterManager.addItem(customMarker);
                        }
                    }
                }
                else {
                        List<Area> areas = event.getArea();
                        for (int j = 0; j < areas.size(); j++) {
                            CustomMarker customMarker = new CustomMarker(eventList.get(i), areas.get(j));
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


    @Override
    public boolean onMarkerClick(Marker marker) {
        return false;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMapView = googleMap;
        ProcessDetailEvent();
if(!mapType1.isSelected() && !mapType2.isSelected() && !mapType3.isSelected()) {
    mMapView.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
    mapType1.setSelected(false);
    mapType2.setSelected(true);
    mapType3.setSelected(false);
    setMapIcons();

}

        clusterManager = new ClusterManager<CustomMarker>(_context, mMapView);
        mMapView.setOnMarkerClickListener(clusterManager);
        clusterManager.setRenderer(new CustomClusterRenderer(getActivity(), mMapView, clusterManager));
        mMapView.setOnCameraChangeListener(clusterManager);
        // Set a listener for info window events.

        applyFilters();


        clusterManager.setOnClusterItemClickListener(new ClusterManager.OnClusterItemClickListener<CustomMarker>() {
            @Override
            public boolean onClusterItemClick(CustomMarker customMarker) {

              // Toast.makeText(getActivity(), "Cluster specific item click!!!!", Toast.LENGTH_SHORT).show();
                return false;
            }
        });
        clusterManager.setOnClusterClickListener(new ClusterManager.OnClusterClickListener<CustomMarker>() {
            @Override
            public boolean onClusterClick(Cluster<CustomMarker> cluster) {
                //Toast.makeText(getActivity(), "Cluster item click!!!!" + cluster.getSize(), Toast.LENGTH_SHORT).show();



                if (cluster.getSize() > 0) {

                    List<Event> events = new ArrayList<>();
                    // for each loop
                    for (CustomMarker item : cluster.getItems())
                    {
                        if (item.getEvent() != null)
                        {
                            events.add(item.getEvent());
                        }
                    }
                    Bundle bundle=new Bundle();
                    bundle.putSerializable("clusterEvents",(Serializable)events);
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
            }
        });
        infoWindowClickedForMarkers();
        checkLocationandAddToMap();

    }

void infoWindowClickedForMarkers(){


        if(mMapView != null) {

            if (PreferenceUtils.hasKey(_context, getString(R.string.pref_cluster_map_state))) {

                if(clusterManager != null) {
                    Log.d("dfsd", "Cluster value");
                    mMapView.setOnInfoWindowClickListener(clusterManager);
                    clusterManager.setOnClusterItemInfoWindowClickListener(
                            new ClusterManager.OnClusterItemInfoWindowClickListener<CustomMarker>() {
                                @Override public void onClusterItemInfoWindowClick(CustomMarker customMarker) {
                                    Event event = (Event) customMarker.getEvent();
                                    if (event != null) {
                                        InitializeDetailsPage(event);
                                    } else {
                                        Toast.makeText(_context, "Unable to get event details", Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                }


            } else {

                Log.d("dfsd", "map single value");
                mMapView.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener()
                {
                    @Override public void onInfoWindowClick(Marker marker) {
                        Event event = (Event) marker.getTag();
                        if (event != null) {
                            InitializeDetailsPage(event);
                        } else {
                            Toast.makeText(_context, "Unable to get event details", Toast.LENGTH_LONG).show();
                        }
                    }

                });


            }

        }
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
    public void onStart() {
        super.onStart();
        googleApiClient.connect();
    }
    @Override
    public void onStop() {
        super.onStop();
        googleApiClient.disconnect();

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        checkLocationandAddToMap();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case GRANTED_FINE_LOCATION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    checkLocationandAddToMap();
                } else
                    Toast.makeText(_context, "Location Permission Denied", Toast.LENGTH_SHORT).show();
                break;

        }

    }

    private void checkLocationandAddToMap() {
        if (ActivityCompat.checkSelfPermission(_context, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(_context, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((Activity) _context, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, GRANTED_FINE_LOCATION);
            return;
        }
       Location location = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
        if(location!=null) {
                  mMapView.setMyLocationEnabled(true);


            mMapView.getUiSettings().setMyLocationButtonEnabled(false);

            mMapView.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 13));

            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(new LatLng(location.getLatitude(), location.getLongitude()))      // Sets the center of the map to location user
                    .zoom(17)                   // Sets the zoom
                     .build();                   // Creates a CameraPosition from the builder
            mMapView.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.event_map, menu);
        super.onCreateOptionsMenu(menu, inflater);
        this.mOptionsMenu = menu ;




    }

    private void updateOptionsMenu() {
        if (this.mOptionsMenu != null) {
            onPrepareOptionsMenu(this.mOptionsMenu);
        }
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        if (menu.findItem(R.id.filter_map) != null )
            if(selectedMapState) {
                menu.findItem(R.id.filter_map).setVisible(true);
                menu.findItem(R.id.refresh_map).setVisible(true);
                menu.findItem(R.id.listOptions).setVisible(false);
            }
        else {
                menu.findItem(R.id.filter_map).setVisible(false);
                menu.findItem(R.id.refresh_map).setVisible(false);
                menu.findItem(R.id.listOptions).setVisible(true); }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.filter_map:  case R.id.menuFilterList:
                intent=new Intent(getActivity(), EventMapFilter.class);
                startActivityForResult(intent, Activity_Filter_Result);
                return true;

            case R.id.refresh_map:   case R.id.menuRefreshList:
                refeshData();
                return true;

            case R.id.menuSortList:

                ShowSortDialog();
                Log.d("sdf","menuSortList clicked");
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == Activity_Filter_Result) {
            if(resultCode == Activity.RESULT_OK){
               applyFilters();
            }

        }
    }//onActivityResult

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

    public void defaultFilter(ArrayList<HashMap> defaultDataWz){
        mapOverlay=new ArrayList<>();
        mapLayers=new ArrayList<>();

        _events=new ArrayList<>();

        Gson gson =new Gson();

        if (PreferenceUtils.hasKey(_context, getString(R.string.pref_map_isDefault)) && (boolean) PreferenceUtils.getFromPrefs(_context, getString(R.string.pref_map_isDefault), false) ) {
        String values = (String) PreferenceUtils.getFromPrefs(_context, getString(R.string.pref_map_filter), "");
        ArrayList<String> defValues = gson.fromJson(values,ArrayList.class);

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
                for(int j=0;j<mapLayers.size();j++) {
                    if (event.getGroup().toString().equalsIgnoreCase(mapLayers.get(j))){
                        event.setShowOn(true);
                    }

                }
                if(event.isAlwaysOn()){
                    event.setShowOn(event.isAlwaysOn());
                }

                if(event.isShowOn()){
                    _events.add(event);
                }

            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        if (PreferenceUtils.hasKey(_context, getString(R.string.pref_cluster_map_state))) {
            ProcessClusteredEvents(_events);
            clusterBtn.setImageResource(R.drawable.ic_cluster_red);
        }
        else {
            ProcessEvents(_events);
            clusterBtn.setImageResource(R.drawable.ic_cluster);
        }


        SetEventListDataSource();
        wmsLayers();
    }

    public void customFilter(ArrayList[] customFilter){
        mapOverlay=new ArrayList<>();
        mapLayers=new ArrayList<>();
        _events=new ArrayList<>();


        ArrayList<HashMap<String, CategoryFilter>> categoryFilters = new ArrayList<HashMap<String, CategoryFilter>>();

        Gson gson =new Gson();
        String values = (String) PreferenceUtils.getFromPrefs(_context, getString(R.string.pref_map_filter), "");
        categoryFilters = new Gson().fromJson(values, new TypeToken<ArrayList<HashMap<String, CategoryFilter>>>() {}.getType());

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
            }
            else {
                ProcessEvents(_events);
                clusterBtn.setImageResource(R.drawable.ic_cluster);
            }


            SetEventListDataSource();


    }
    public void wmsLayers(){
        WmsTileProvider provider = tileProviderFactory.GetWmsTileProvider(baseWmsURL,mapOverlay);
        if (provider != null && mMapView != null)
            mMapView.addTileOverlay(new TileOverlayOptions().tileProvider(provider));

    }

}
