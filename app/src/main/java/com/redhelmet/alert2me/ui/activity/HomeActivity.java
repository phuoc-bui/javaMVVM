package com.redhelmet.alert2me.ui.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.redhelmet.alert2me.BuildConfig;
import com.redhelmet.alert2me.adapters.CustomViewPager;
import com.redhelmet.alert2me.domain.util.PreferenceUtils;
import com.redhelmet.alert2me.domain.util.Utility;
import com.redhelmet.alert2me.ui.fragments.EventFragment;
import com.redhelmet.alert2me.ui.fragments.WatchZoneFragment;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;

import net.grandcentrix.tray.AppPreferences;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.redhelmet.alert2me.R;

import com.redhelmet.alert2me.core.Constants;
import com.redhelmet.alert2me.ui.fragments.HelpFragment;
import com.redhelmet.alert2me.ui.services.BackgroundDetectedActivitiesService;

/**
 * Created by inbox on 13/11/17.
 */

public class HomeActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {


    private TabLayout tabLayout;
    private CustomViewPager viewPager;
    private Toolbar toolbar;
    private int[] tabIcons = { //tab button at the bottom
            R.drawable.ic_report_problem,
            R.drawable.ic_watch_zone,
            R.drawable.ic_help_white
    };
    int positionTabSelected = 0;
    LatLng latLng;
    public GoogleApiClient mApiClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);


        Bundle bundle = new Bundle();

        Bundle extras = getIntent().getExtras();

        if (extras != null) { //edit mode - detect if need to zoom on any specific location at start of activity


            latLng = (LatLng) extras.get("marker");
            PreferenceUtils.saveToPrefs(this, "detailLat", latLng.latitude);
            PreferenceUtils.saveToPrefs(this, "detailLon", latLng.longitude);

        }


        viewPager = (CustomViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }
        setupTabIcons();

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                //do stuff here
                positionTabSelected = tab.getPosition();
                switch (positionTabSelected) {
                    case 0:
                        initializeToolbar(getString(R.string.lblEvent) + " " + getString(R.string.lblMap));
                        //startTracking();
                        break;
                    case 1:
                        initializeToolbar(getString(R.string.toolbar_WZ));
                       // stopTracking();
                        break;
                    case 2:
                        initializeToolbar(getString(R.string.toolbar_help));
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

//        startTracking();

//        //Activity recognition
//
//        mApiClient = new GoogleApiClient.Builder(this)
//                .addApi(ActivityRecognition.API)
//                .addConnectionCallbacks(this)
//                .addOnConnectionFailedListener(this)
//                .build();
//
//        mApiClient.connect();

    }

    private void setupTabIcons() {


        TextView tabOne = (TextView) LayoutInflater.from(this).inflate(R.layout.custom_tab, null);
        tabOne.setText(getString(R.string.tab_events));
        tabOne.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_report_problem, 0, 0);
        tabLayout.getTabAt(0).setCustomView(tabOne);

        TextView tabTwo = (TextView) LayoutInflater.from(this).inflate(R.layout.custom_tab, null);
        tabTwo.setText(getString(R.string.tab_WZ));
        tabTwo.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_watch_zone, 0, 0);
        tabLayout.getTabAt(1).setCustomView(tabTwo);

        TextView tabThree = (TextView) LayoutInflater.from(this).inflate(R.layout.custom_tab, null);
        tabThree.setText(getString(R.string.tab_help));
        tabThree.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.ic_help_white, 0, 0);
        tabLayout.getTabAt(2).setCustomView(tabThree);

    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFrag(new EventFragment(), getString(R.string.tab_events));
        adapter.addFrag(new WatchZoneFragment(), getString(R.string.tab_WZ));
        adapter.addFrag(new HelpFragment(), getString(R.string.tab_help));
        viewPager.setAdapter(adapter);


    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFrag(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    public void initializeToolbar(String heading) {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }
        ActionBar supportActionBar = getSupportActionBar();

        if (supportActionBar != null) {
            supportActionBar.setDisplayHomeAsUpEnabled(false);

            // changing the toolbar... if tab changed

            switch (positionTabSelected) {
                case 0:

                    break;
                case 1:
                    heading =  getString(R.string.toolbar_WZ);
                    break;
                case 2:
                    heading = getString(R.string.toolbar_help);
                    break;
            }

            supportActionBar.setTitle(heading);
        }
    }


    @Override
    public void onConnected(@Nullable Bundle bundle) {

        boolean enableProxi = (boolean) PreferenceUtils.getFromPrefs(getApplicationContext(), Constants.KEY_VALUE_ENABLEPROXI, false);
        if (enableProxi) {
            setLocation();
//            mApiClient.connect();
//            proximityLocationCheckin();

        }


//        Intent intent = new Intent( this,   ActivityRecognizedService.class );
//        PendingIntent pendingIntent = PendingIntent.getService( this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT );
//        ActivityRecognition.ActivityRecognitionApi.requestActivityUpdates( mApiClient, 0, pendingIntent );
    }

    private void setLocation() {
        Log.d("sdfsdf", "inciide locations");
        LocationManager locationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        // Get the name of the best provider
        String provider = locationManager.getBestProvider(criteria, true);
//        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(_context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(get, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_COARSE_LOCATION);
//            return;
//        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Location lastKnownLocation = locationManager.getLastKnownLocation(provider);
        if (lastKnownLocation != null) {
            Log.d("LocationData", String.valueOf(lastKnownLocation.getLatitude()));

            PreferenceUtils.saveToPrefs(getApplicationContext(), Constants.KEY_USERLATITUDE,String.valueOf(lastKnownLocation.getLatitude()));
            PreferenceUtils.saveToPrefs(getApplicationContext(),Constants.KEY_USERLONGITUDE,String.valueOf(lastKnownLocation.getLongitude()));

        }
    }

    private final LocationListener mLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(final Location location) {
            //your code here
        }
    };


    @Override
    public void onStart() {
        //mApiClient.connect();
        super.onStart();
    }

    @Override
    public void onStop() {
      //  mApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onConnectionSuspended(int i) {
        //mApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
       // mApiClient.disconnect();
    }

    private void startTracking() {
        Intent intent1 = new Intent(this, BackgroundDetectedActivitiesService.class);
        Log.d("BackgroundDetected","started.......................");
        startService(intent1);
    }

    private void stopTracking() {
        Intent intent = new Intent(this, BackgroundDetectedActivitiesService.class);
        Log.d("BackgroundDetected","stopped.......................");
        stopService(intent);
    }

    public void proximityLocationCheckin()
    {

        if(!Utility.isInternetConnected(getApplicationContext())) {
        Toast.makeText(getApplicationContext(), getString(R.string.noInternet), Toast.LENGTH_LONG).show();
        return;
    }
        final AppPreferences appPreferences = new AppPreferences(getApplicationContext());
        String apiURL = BuildConfig.API_ENDPOINT + "device/" + PreferenceUtils.getFromPrefs(getApplicationContext(), getString(R.string.pref_user_id), "") + "/" + "watchzones/proximity/location";
        Log.d("location", PreferenceUtils. getFromPrefs(getApplicationContext(), Constants.KEY_USERLATITUDE, " ").toString());


        HashMap<String, Object> mParams = new HashMap < String, Object> ();

        Gson gson = new Gson();
        //mParams.put("accuracy","0");
        mParams.put("speed","0.0 km/hr");
        mParams.put("movement","Not Moving");
        mParams.put("latitude", Double.valueOf((String) PreferenceUtils.getFromPrefs(getApplicationContext(), Constants.KEY_USERLATITUDE, " ")));
        mParams.put("longitude",  Double.valueOf((String ) PreferenceUtils.getFromPrefs(getApplicationContext(), Constants.KEY_USERLONGITUDE, " ")));
        Log.d("proximityDictlocation", mParams.toString());


        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        JsonObjectRequest jsonObjReq =
                new JsonObjectRequest(Request.Method.PUT, apiURL, new JSONObject(mParams),
                new Response.Listener < JSONObject > () {

                    @Override
                    public void onResponse(JSONObject response) {

                        if (response != null) {
                            try {
                                if (response.getBoolean("success")) {

                                    Log.e("EA", response.toString());
                                    PreferenceUtils.saveToPrefs(getApplicationContext(), Constants.KEY_LASTUPDATEDUSERLATITUDE,String.valueOf(Double.valueOf((String) PreferenceUtils.getFromPrefs(getApplicationContext(), Constants.KEY_USERLATITUDE, "0"))));
                                    PreferenceUtils.saveToPrefs(getApplicationContext(),Constants.KEY_LASTUPDATEDUSERLONGITUDE,String.valueOf(Double.valueOf((String ) PreferenceUtils.getFromPrefs(getApplicationContext(), Constants.KEY_USERLONGITUDE, "0"))));
                                    appPreferences.put(Constants.PROXIMITY_MOVEMENT, 3); //Not moving

                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {

                    Toast.makeText(getApplicationContext(), getString(R.string.timeOut), Toast.LENGTH_LONG).show();

            }
        }) {
            /**
             * Passing some request headers
             */
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json");
                headers.put("Authorization", "Bearer " + PreferenceUtils.getFromPrefs(getApplicationContext(), getString(R.string.pref_user_token), ""));
                return headers;
            }

        };


        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(jsonObjReq);


    }
}
