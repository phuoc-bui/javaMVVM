package com.redhelmet.alert2me.ui.activity;

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.redhelmet.alert2me.autocomplete.AutoCompleteLocation;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;
import java.util.Locale;

import com.redhelmet.alert2me.R;

/**
 * Created by inbox on 7/2/18.
 */

public class ObservationLocation extends BaseActivity implements LocationListener,AutoCompleteLocation.AutoCompleteLocationListener, OnMapReadyCallback, GoogleMap.OnMapLongClickListener, GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks {

    Toolbar toolbar;
    Intent i;
    GoogleMap _observationMap;
    ImageButton myLocation;
    GoogleApiClient googleApiClient;
    LatLng locationLatLng;
    Address address;
    Intent intent;
    LocationRequest mLocationRequest;

    private static final int REQUEST_CHECK_SETTINGS = 0x1;
    private static final int ACCESS_FINE_LOCATION_INTENT_ID = 3;
    private static final String BROADCAST_ACTION = "android.location.PROVIDERS_CHANGED";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_observation_location);


        //createPermissionListener();
        initializeToolbar();
        initializeControls();
        initGoogleAPIClient();//Init Google API Client
        checkPermissions();//Check Permission
//        googleApiClient = new GoogleApiClient.Builder(this, this, this).addApi(LocationServices.API).build();
//        geocoder = new Geocoder(this, Locale.getDefault());
//        manager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
//
//        createLocationRequest();

//        googleApiClient = new GoogleApiClient.Builder(getApplicationContext())
//                .addConnectionCallbacks(this)
//                .addOnConnectionFailedListener(this)
//                .addApi(LocationServices.API)
//                .build();
//        googleApiClient.connect();

    }

    /* Initiate Google API Client  */
    private void initGoogleAPIClient() {
        //Without Google API Client Auto Location Dialog will not work
        googleApiClient = new GoogleApiClient.Builder(ObservationLocation.this)
                .addConnectionCallbacks(this)
              .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        googleApiClient.connect();
    }

    /* Check Location Permission for Marshmallow Devices */
    private void checkPermissions() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (ContextCompat.checkSelfPermission(ObservationLocation.this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED)
                requestLocationPermission();
            else
                showSettingDialog();
        } else
            showSettingDialog();

    }

    /*  Show Popup to access User Permission  */
    private void requestLocationPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(ObservationLocation.this, android.Manifest.permission.ACCESS_FINE_LOCATION)) {
            ActivityCompat.requestPermissions(ObservationLocation.this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    ACCESS_FINE_LOCATION_INTENT_ID);

        } else {
            ActivityCompat.requestPermissions(ObservationLocation.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    ACCESS_FINE_LOCATION_INTENT_ID);
        }
    }

    /* Show Location Access Dialog */
    private void showSettingDialog() {
         mLocationRequest = LocationRequest.create();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);//Setting priotity of Location request to high
        mLocationRequest.setInterval(10 * 1000) ;      // 10 seconds, in milliseconds
        mLocationRequest.setFastestInterval(1 * 1000); // 1 second, in millisecondssssss
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest);
        builder.setAlwaysShow(true); //this is the key ingredient to show dialog always when GPS is off

        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                final LocationSettingsStates state = result.getLocationSettingsStates();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        googleApiClient.connect();
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        // Location settings are not satisfied. But could be fixed by showing the user
                        // a dialog.
                        try {
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            status.startResolutionForResult(ObservationLocation.this, REQUEST_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException e) {
                            e.printStackTrace();
                            // Ignore the error.
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        // Location settings are not satisfied. However, we have no way to fix the
                        // settings so we won't show the dialog.
                        break;
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            // Check for the integer request code originally supplied to startResolutionForResult().
            case REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case RESULT_OK:
                        googleApiClient.connect();
                        break;
                    case RESULT_CANCELED:

                        break;
                }
                break;
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(gpsLocationReceiver, new IntentFilter(BROADCAST_ACTION));//Register broadcast receiver to check the status of GPS
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //Unregister receiver on destroy
        if (gpsLocationReceiver != null)
            unregisterReceiver(gpsLocationReceiver);
    }

    //Run on UI
    private Runnable sendUpdatesToUI = new Runnable() {
        public void run() {
            showSettingDialog();
        }
    };

    /* Broadcast receiver to check status of GPS */
    private BroadcastReceiver gpsLocationReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {

            //If Action is Location
            if (intent.getAction().matches(BROADCAST_ACTION)) {
                LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
                //Check if GPS is turned ON or OFF
                if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    Log.e("About GPS", "GPS is Enabled in your apiInfo");

                } else {
                    //If GPS turned OFF show Location Dialog
                    new Handler().postDelayed(sendUpdatesToUI, 10);
                    // showSettingDialog();

                    Log.e("About GPS", "GPS is Disabled in your apiInfo");
                }

            }
        }
    };


    /* On Request permission method to check the permisison is granted or not for Marshmallow+ Devices  */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case ACCESS_FINE_LOCATION_INTENT_ID: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    //If permission granted show location dialog if APIClient is not null
                    if (googleApiClient == null) {
                        initGoogleAPIClient();
                        showSettingDialog();
                    } else
                        showSettingDialog();


                } else {

                    Toast.makeText(ObservationLocation.this, "Location Permission denied.", Toast.LENGTH_SHORT).show();
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
        }
    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();

        inflater.inflate(R.menu.observation_what, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.add_observation:

                if (locationLatLng != null ) {
                    intent = new Intent();
                    setResult(Activity.RESULT_OK, intent);
                    finish();

                }

                return true;
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


//    public void createPermissionListener() {
//        Dexter.withActivity(this)
//                .withPermissions(
//                        Manifest.permission.ACCESS_COARSE_LOCATION,
//                        Manifest.permission.ACCESS_FINE_LOCATION)
//                .withListener(new MultiplePermissionsListener() {
//                    @Override
//                    public void onPermissionsChecked(MultiplePermissionsReport report) {
//                        // check if all permissions are granted
//                        if (report.areAllPermissionsGranted()) {
//                            // do you work now
//
//                        }
//
//                        // check for permanent denial of any permission
//                        if (report.isAnyPermissionPermanentlyDenied()) {
//                            showSettingsDialog();
//                        }
//
//                    }
//
//                    @Override
//                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
//                        token.continuePermissionRequest();
//                    }
//
//                })
//                .onSameThread()
//                .check();
//    }

    private void showSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(ObservationLocation.this);
        builder.setTitle("Location Permission Required");
        builder.setMessage("Location services are not enabled. To re-enable, please go to setting and turn on Location Service for this app.");
        builder.setPositiveButton("Goto Settings", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                openSettings();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();

    }

    private void openSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivityForResult(intent, 101);
    }


    public void initializeToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }
        ActionBar supportActionBar = getSupportActionBar();

        if (supportActionBar != null) {
            supportActionBar.setDisplayHomeAsUpEnabled(true);

            supportActionBar.setTitle("Observation Location");
        }
    }

    public void initializeControls() {
        AutoCompleteLocation autoCompleteLocation =
                (AutoCompleteLocation) findViewById(R.id.observation_autocomplete);
        autoCompleteLocation.setAutoCompleteTextListener(this);
        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.observation_map_location);
        mapFragment.getMapAsync(ObservationLocation.this);

        myLocation = (ImageButton)findViewById(R.id.location_map);
        myLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(googleApiClient!=null) {
                    checkPermissions();
                    googleApiClient.connect();
                }else{
                    initGoogleAPIClient();//Init Google API Client
                    checkPermissions();

                }

            }
        });

    }

    @Override
    public void onTextClear() {

    }

    @Override
    public void onItemSelected(Place selectedPlace) {

        _observationMap.clear();
        LatLng latLng = new LatLng(selectedPlace.getLatLng().latitude, selectedPlace.getLatLng().longitude);
        Drawable drawable = getApplicationContext().getResources().getDrawable(R.drawable.icon_map_pin);
        BitmapDescriptor icon = getMarkerIconFromDrawable(drawable);
        MarkerOptions marker = new MarkerOptions().position(latLng).title(selectedPlace.getAddress().toString()).icon(icon);
        _observationMap.addMarker(marker);
        _observationMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17));
        savePreference(latLng,selectedPlace);
        //latLng = new LatLng(selectedPlace.getl)

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this._observationMap = googleMap;
        _observationMap.getUiSettings().setMapToolbarEnabled(false);
        _observationMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {

                _observationMap.setOnMapLongClickListener(ObservationLocation.this);


            }
        });
    }

    @Override
    public void onMapLongClick(LatLng latLng) {
        _observationMap.clear();

        String address = "Current Location";
        Address returnedAddress=null;

        Drawable drawable = getApplicationContext().getResources().getDrawable(R.drawable.icon_map_pin);
        BitmapDescriptor icon = getMarkerIconFromDrawable(drawable);
        if (getAddress(latLng.latitude, latLng.longitude) != null) {
             returnedAddress=getAddress(latLng.latitude, latLng.longitude);
            StringBuilder strReturnedAddress = new StringBuilder("");

            for (int i = 0; i <= returnedAddress.getMaxAddressLineIndex(); i++) {
                strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n");
            }
            address = String.valueOf(strReturnedAddress.toString());
        }
        MarkerOptions marker = new MarkerOptions().position(latLng).title(address).icon(icon);


        _observationMap.addMarker(marker);
        _observationMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17));
        savePreference(latLng,null);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }





    protected void startLocationUpdates() {
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
        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient,
                mLocationRequest,  this);
    }

    protected void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(
                googleApiClient,  this);
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.d("SplashAct", "LocatinChngListner, loc: " + location.getLatitude() + "," + location.getLongitude());

        if (googleApiClient != null)
            if (googleApiClient.isConnected() || googleApiClient.isConnecting()){
                googleApiClient.disconnect();
                googleApiClient.connect();
            } else if (!googleApiClient.isConnected()){
                googleApiClient.connect();
            }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {


        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {


            Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                    googleApiClient);

            if (mLastLocation == null) {
                startLocationUpdates(); // bind interface if your are not getting the lastlocation. or bind as per your requirement.
            }

            if (mLastLocation != null) {

                Drawable drawable = getApplicationContext().getResources().getDrawable(R.drawable.icon_map_pin);
                BitmapDescriptor icon = getMarkerIconFromDrawable(drawable);
                MarkerOptions marker = new MarkerOptions().position(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude())).title("Current Location").icon(icon);

                _observationMap.clear();
                _observationMap.addMarker(marker);
                _observationMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()), 17));
                savePreference(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude()),null);
                stopLocationUpdates();
            }
        }

    }





    @Override
    public void onConnectionSuspended(int i) {

    }


    private BitmapDescriptor getMarkerIconFromDrawable(Drawable drawable) {
        Canvas canvas = new Canvas();
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        canvas.setBitmap(bitmap);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        drawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }


    public Address getAddress(double latitude, double longitude)
    {
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses != null) {
                Address returnedAddress = addresses.get(0);
                return returnedAddress;
            } else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    public void savePreference(LatLng latLng,Place place){
        this.locationLatLng =latLng;

        addObservation.setLatLng(latLng);

        addObservation.setPlace(place);

    }
}
