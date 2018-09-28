package com.redhelmet.alert2me.ui.home.event;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.common.collect.ComparisonChain;
import com.google.gson.Gson;
import com.redhelmet.alert2me.BuildConfig;
import com.redhelmet.alert2me.R;
import com.redhelmet.alert2me.adapters.AppViewPagerAdapter;
import com.redhelmet.alert2me.core.Constants;
import com.redhelmet.alert2me.data.local.database.DBController;
import com.redhelmet.alert2me.data.model.Area;
import com.redhelmet.alert2me.data.model.Event;
import com.redhelmet.alert2me.data.model.EventGroup;
import com.redhelmet.alert2me.databinding.FragmentEventBinding;
import com.redhelmet.alert2me.domain.util.PreferenceUtils;
import com.redhelmet.alert2me.domain.util.Utility;
import com.redhelmet.alert2me.interfaces.ServerCallback;
import com.redhelmet.alert2me.ui.activity.AddObservation;
import com.redhelmet.alert2me.ui.activity.EventMapFilter;
import com.redhelmet.alert2me.ui.base.BaseFragment;
import com.redhelmet.alert2me.ui.home.HomeActivity;

import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;


public class EventFragment extends BaseFragment<EventViewModel, FragmentEventBinding> implements SwipeRefreshLayout.OnRefreshListener {

    private View view;
    private Menu mOptionsMenu;
    public final int GRANTED_FINE_LOCATION = 999;
    private static final int REQUEST_COARSE_LOCATION = 8;
    Intent intent;
    int Activity_Filter_Result = 77;

    public Context _context;
    private String toolbarHeading = "Event Map";
    RecyclerView listEventIcon;
    private String sortByList;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_event;
    }

    @Override
    protected Class<EventViewModel> getViewModelClass() {
        return EventViewModel.class;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true); //To set vector change resource
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (PreferenceUtils.hasKey(_context, "detailLat")) {
            double def = 0.0;
            double lat = (Double) PreferenceUtils.getFromPrefs(_context, "detailLat", def);
            double lon = (Double) PreferenceUtils.getFromPrefs(_context, "detailLon", def);
//            latlng = new LatLng(lat, lon);
            PreferenceUtils.removeFromPrefs(_context, "detailLat");
            PreferenceUtils.removeFromPrefs(_context, "detailLon");
        }

        initializationControls();
        initializeListener();
        viewSwitch(true);
        downloadFile();

        String str = "Bearer " + PreferenceUtils.getFromPrefs(_context, getString(R.string.pref_user_token), "");
        Log.d("Token", str);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupViewPager();
    }

    private void setupViewPager() {
        AppViewPagerAdapter adapter = new AppViewPagerAdapter(getChildFragmentManager());
        adapter.addFrag(MapFragment.newInstance(viewModel.getMapViewModel()), getString(R.string.tab_events));
        adapter.addFrag(EventListFragment.newInstance(viewModel.getEventListViewModel()), getString(R.string.tab_events));
        binder.viewpager.setAdapter(adapter);
    }

    private void initializationControls() {

        if (!PreferenceUtils.hasKey(_context, getString(R.string.pref_map_isDefault))) {
            PreferenceUtils.saveToPrefs(_context, getString(R.string.pref_map_isDefault), true);

            ArrayList<EventGroup> default_data = new ArrayList<EventGroup>();
            ArrayList<String> defValues = new ArrayList<String>();
            Gson gson = new Gson();
            ArrayList<HashMap> defaultDataWz = dbController.getDefaultMapFilter();
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


            PreferenceUtils.saveToPrefs(_context, getString(R.string.pref_map_filter), gson.toJson(defValues));

        }

        setLocation();

        if (PreferenceUtils.hasKey(_context, getString(R.string.pref_basewms_url)))
            baseWmsURL = (String) PreferenceUtils.getFromPrefs(_context, getString(R.string.pref_basewms_url), "");

        if (baseWmsURL == "") baseWmsURL = "http://ex-dev-mapping.ripeintel.info/ra-wms-proxy/wms";

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
        if (mSwipeRefreshLayout != null) {
            mSwipeRefreshLayout.setRefreshing(true);
        }
        downloadFile();
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

    public void downloadFile() {
        if (!Utility.isInternetConnected(_context)) {
            Toast.makeText(_context, getString(R.string.noInternet), Toast.LENGTH_LONG).show();

            mSwipeRefreshLayout.setRefreshing(false);
            getOfflineEvent();
        } else {
            mProgress.setVisibility(View.VISIBLE);
            // Showing refresh animation before making http call
            mSwipeRefreshLayout.setRefreshing(true);
            cf.ZipDownload(BuildConfig.API_ENDPOINT + "events/full?zip=true", new ServerCallback() {
                @Override
                public void onSuccess(boolean result) {

                    if (result) {


                        mProgress.setVisibility(View.INVISIBLE);
                        // Stopping swipe refresh
                        mSwipeRefreshLayout.setRefreshing(false);
                        if (checkFile()) {

                            Log.d("Result", "Downloaded new event file");
                            GetEvents();
                            SetEventListDataSource();
                        } else {
                            getOfflineEvent();
                            Toast.makeText(_context, getString(R.string.msgUnableToGetEvent), Toast.LENGTH_LONG).show();
                        }


                    } else {
                        mProgress.setVisibility(View.INVISIBLE);
                        // Stopping swipe refresh
                        mSwipeRefreshLayout.setRefreshing(false);
                        getOfflineEvent();
                        Toast.makeText(_context, getString(R.string.msgUnableToGetEvent), Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onSuccess(JSONObject response) {
                    mSwipeRefreshLayout.setRefreshing(false);
                }
            });
        }


    }

    private Event SetDistanceForEvents(Event event) {
//        if (_events != null) {
//            for (int i = 0; i < _events.size(); i++) {
//                Event event = _events.get(i);
        List<Area> areas = event.getArea();

        Area area = areas.get(0);
        Location userLocation = new Location("User Location");
        ;
        if (PreferenceUtils.hasKey(_context, Constants.KEY_USERLATITUDE) && (PreferenceUtils.hasKey(_context, Constants.KEY_USERLONGITUDE))) {

            final Double latitude = Double.valueOf((String) PreferenceUtils.getFromPrefs(_context, Constants.KEY_USERLATITUDE, "0"));
            final Double longitude = Double.valueOf((String) PreferenceUtils.getFromPrefs(_context, Constants.KEY_USERLONGITUDE, "0"));
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
        return event;
    }

    private void SortList() {


        if (PreferenceUtils.hasKey(_context, Constants.SORT_PREFERENCE_KEY)) {
            String sortBy = (String) PreferenceUtils.getFromPrefs(_context, Constants.SORT_PREFERENCE_KEY, "2");
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
            PreferenceUtils.saveToPrefs(_context, Constants.SORT_PREFERENCE_KEY, "2");
            SortByStatus();

        }

    }

    private void ShowSortDialog() {

        final CharSequence[] items = {getString(R.string.listSortOrderDistance), getString(R.string.listSortOrderTime), getString(R.string.listSortOrderStatus)};
        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(_context, R.style.MaterialThemeDialog);
        int selectedSortItem = 2;

        if (PreferenceUtils.hasKey(_context, Constants.SORT_PREFERENCE_KEY)) {

            String selectedSort = (String) PreferenceUtils.getFromPrefs(_context, Constants.SORT_PREFERENCE_KEY, "2");
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
                PreferenceUtils.saveToPrefs(_context, Constants.SORT_PREFERENCE_KEY, sortByList);
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
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.event_map, menu);
        super.onCreateOptionsMenu(menu, inflater);
        this.mOptionsMenu = menu;


    }

    private void updateOptionsMenu() {
        if (this.mOptionsMenu != null) {
            onPrepareOptionsMenu(this.mOptionsMenu);
        }
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        if (menu.findItem(R.id.filter_map) != null)
            if (selectedMapState) {
                menu.findItem(R.id.filter_map).setVisible(true);
                menu.findItem(R.id.refresh_map).setVisible(true);
                menu.findItem(R.id.listOptions).setVisible(false);
            } else {
                menu.findItem(R.id.filter_map).setVisible(false);
                menu.findItem(R.id.refresh_map).setVisible(false);
                menu.findItem(R.id.listOptions).setVisible(true);
            }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.filter_map:
            case R.id.menuFilterList:
                intent = new Intent(getActivity(), EventMapFilter.class);
                startActivityForResult(intent, Activity_Filter_Result);
                return true;

            case R.id.refresh_map:
            case R.id.menuRefreshList:
                refeshData();
                return true;

            case R.id.menuSortList:

                ShowSortDialog();
                Log.d("sdf", "menuSortList clicked");
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
