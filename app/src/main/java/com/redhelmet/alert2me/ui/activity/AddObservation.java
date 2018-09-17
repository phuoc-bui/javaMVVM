package com.redhelmet.alert2me.ui.activity;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.redhelmet.alert2me.adapters.ObservationImageAdapter;
import com.redhelmet.alert2me.core.Constants;
import com.redhelmet.alert2me.core.DeviceUtil;
import com.redhelmet.alert2me.core.RequestHandler;
import com.redhelmet.alert2me.core.VolleyMultipartRequest;
import com.redhelmet.alert2me.domain.util.PreferenceUtils;
import com.redhelmet.alert2me.domain.util.Utility;
import com.redhelmet.alert2me.model.ObservationTopics;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.snatik.storage.Storage;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.redhelmet.alert2me.R;
import droidninja.filepicker.FilePickerBuilder;
import droidninja.filepicker.FilePickerConst;
import droidninja.filepicker.utils.Orientation;
import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

import static com.redhelmet.alert2me.R.id.observation_map;

/**
 * Created by inbox on 5/2/18.
 */

public class AddObservation extends BaseActivity implements OnMapReadyCallback, View.OnClickListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,GoogleMap.OnMapClickListener,TimePickerDialog.OnTimeSetListener , EasyPermissions.PermissionCallbacks {

    LinearLayout whatLayout,
            whenLayout,
            commentLayout,
            photoLayout;
    Toolbar toolbar;

    TextView whatText,
            locationAddress,
            whenTime,
            description;

    public static final int RC_PHOTO_PICKER_PERM = 123;
    ObservationImageAdapter imageAdapter;
    private int MAX_ATTACHMENT_COUNT = 6;
    public ArrayList<String> photoPaths = new ArrayList<>();
    RecyclerView recyclerView;

    GoogleMap mMapView;
    String client="config/observation";
    String _observationUrl; //enable/disable url
    RequestQueue queue;
    JsonObjectRequest volleyRequest;
    Intent intent;
    GoogleApiClient googleApiClient;
    Geocoder geocoder;
    public static final int RESULT_CODE_WHAT=99;
    public static final int RESULT_CODE_LOCATION=998;
    public static final int RESULT_CODE_COMMENT=997;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_observation);

        _observationUrl=getString(R.string.api_url)+client;

        initializeToolbar();
        initializeControls();
        initializeListeners();
        initializeLocation();
    }

    public void initializeLocation(){
        googleApiClient = new GoogleApiClient.Builder(this, this, this).addApi(LocationServices.API).build();
        geocoder = new Geocoder(this, Locale.getDefault());
        googleApiClient.connect();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();

        inflater.inflate(R.menu.observation_submit, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.post_observation:

                postObservation();
                return true;
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    public void initializeToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }
        ActionBar supportActionBar = getSupportActionBar();

        if (supportActionBar != null) {
            supportActionBar.setDisplayHomeAsUpEnabled(true);

            supportActionBar.setTitle("Add Observation");
        }
    }
    public void initializeControls(){

        whatLayout = (LinearLayout) findViewById(R.id.what_layout);
        whenLayout = (LinearLayout) findViewById(R.id.when_layout);
        commentLayout = (LinearLayout) findViewById(R.id.comment_layout);
        photoLayout = (LinearLayout) findViewById(R.id.photo_layout);
        whatText = (TextView) findViewById(R.id.what_sensing_value);
        whenTime = (TextView) findViewById(R.id.when_time);
        locationAddress = (TextView) findViewById(R.id.observation_address);
        description = (TextView)findViewById(R.id.edit_comment);
        recyclerView = (RecyclerView)findViewById(R.id.recyclerview);

        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(observation_map);
        mapFragment.getMapAsync(this);

        observationRequest();
    }

    public void initializeListeners(){

        whatLayout.setOnClickListener(this);
        whenLayout.setOnClickListener(this);
        commentLayout.setOnClickListener(this);
        locationAddress.setOnClickListener(this);
        photoLayout.setOnClickListener(this);

    }
    @Override
    public void onMapClick(LatLng point) {
        intent=new Intent(AddObservation.this,ObservationLocation.class);
        startActivityForResult(intent, RESULT_CODE_LOCATION);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMapView=googleMap;

        mMapView.getUiSettings().setScrollGesturesEnabled(false);
        mMapView.getUiSettings().setMapToolbarEnabled(false);
        mMapView.getUiSettings().setZoomGesturesEnabled(false);
    }

    public void timePicker(){
        Calendar now = Calendar.getInstance();
        final TimePickerDialog timepickerdialog = TimePickerDialog.newInstance(AddObservation.this,
                now.get(Calendar.HOUR_OF_DAY), now.get(Calendar.MINUTE), true);
        timepickerdialog.setThemeDark(false); //Dark Theme?
        timepickerdialog.vibrate(false); //vibrate on choosing time?
        timepickerdialog.dismissOnPause(false); //dismiss the dialog onPause() called?
        timepickerdialog.enableSeconds(false); //show seconds?
        timepickerdialog.setCancelText("Now");
        timepickerdialog.setAccentColor(Color.RED);
        timepickerdialog.setTitle("How long ago did you observe this?");

        //Handling cancel event
        timepickerdialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                whenTime.setText("Now");
            }
        });




        timepickerdialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {

            }
        });
        timepickerdialog.show(getFragmentManager(), "Timepickerdialog"); //show time picker dialog

    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.what_layout:
                if(observations.getTopics()!=null){
                    intent=new Intent(AddObservation.this,ObservationWhatCategory.class);
                    startActivityForResult(intent, RESULT_CODE_WHAT);
                }
                break;
            case R.id.when_layout:
                timePicker();
                break;
            case R.id.comment_layout:
                intent=new Intent(AddObservation.this,ObservationComments.class);
                startActivityForResult(intent, RESULT_CODE_COMMENT);
                break;
            case R.id.photo_layout:
                photoSelection();
                break;
            case R.id.observation_address:
                intent=new Intent(AddObservation.this,ObservationLocation.class);
                startActivityForResult(intent, RESULT_CODE_LOCATION);
                break;

        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //ONLY FOR CATEGORY - BECAUSE OF BACK
        if (resultCode != RESULT_OK) {

            switch (requestCode){
                case RESULT_CODE_WHAT:
                    int cat= (int) PreferenceUtils.getFromPrefs(getApplicationContext(), Constants.observation_first_category_id,0);
                    int subcat=(int) PreferenceUtils.getFromPrefs(getApplicationContext(), Constants.observation_second_category_id,0);
                    int subcat2 =(int) PreferenceUtils.getFromPrefs(getApplicationContext(), Constants.observation_third_category_id,0);

                    String value = observations.getTopics().get(cat).getGroups().get(subcat).getTypes().get(subcat2).getText();
                    addObservation.setWhatText(value);
                    whatText.setText(value);
                    break;
            }
        }else{
            switch (requestCode) {
                case RESULT_CODE_LOCATION:
                    preferenceLocation();
                    break;

                case RESULT_CODE_COMMENT:
                    if(addObservation.getComments().trim().length()>0)
                    description.setText(addObservation.getComments().trim());
                    break;
                case FilePickerConst.REQUEST_CODE_PHOTO:
                    if (resultCode == Activity.RESULT_OK && data != null) {
                        photoPaths = new ArrayList<>();
                        photoPaths.addAll(data.getStringArrayListExtra(FilePickerConst.KEY_SELECTED_MEDIA));
                    }
                    addThemToView(photoPaths);
                    break;

            }
        }


    }


    public void observationRequest(){


        if(!Utility.isInternetConnected(getApplicationContext())) {
            Toast.makeText(getApplicationContext(), getString(R.string.noInternet), Toast.LENGTH_LONG).show();
            return;
        }
        queue = RequestHandler.getInstance(getApplicationContext().getApplicationContext()).getRequestQueue(); //Obtain the instance

        volleyRequest = new JsonObjectRequest(Request.Method.GET,_observationUrl,
                null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if (response != null) {
                    try {
                        if (response.getBoolean("success")) {
                            JSONObject obse = response.getJSONObject("observations");

                            Type listType = new TypeToken<List<ObservationTopics>>(){}.getType();
                            List<ObservationTopics> obsList = new Gson().fromJson(String.valueOf(obse.getJSONArray("topics")), listType);
                            observations.setTopics(obsList);

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
                error.printStackTrace();
            }
        });


        volleyRequest.setRetryPolicy(new DefaultRetryPolicy(5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(volleyRequest);
    }

    public void clearPreferences(){
        if(PreferenceUtils.hasKey(getApplicationContext(), Constants.observation_first_category_id)) {
            PreferenceUtils.removeFromPrefs(getApplicationContext(), Constants.observation_first_category_id);
            PreferenceUtils.removeFromPrefs(getApplicationContext(), Constants.observation_second_category_id);
            PreferenceUtils.removeFromPrefs(getApplicationContext(), Constants.observation_third_category_id);
        }

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {


        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            Location lastLocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
            if(addObservation.getLatLng()==null) {
                if (lastLocation != null) {


                    Drawable drawable = getApplicationContext().getResources().getDrawable(R.drawable.icon_map_pin);
                    BitmapDescriptor icon = getMarkerIconFromDrawable(drawable);
                    MarkerOptions marker = new MarkerOptions().position(new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude())).title("Current Location").icon(icon);


                    mMapView.addMarker(marker);
                    mMapView.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude()), 17));
                }
            }else{
                preferenceLocation();
            }

        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }


    public String getAddress(double latitude, double longitude)
    {
        String strAdd = "";
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
            if (addresses != null) {
                Address returnedAddress = addresses.get(0);
                StringBuilder strReturnedAddress = new StringBuilder("");

                for (int i = 0; i <= returnedAddress.getMaxAddressLineIndex(); i++) {
                    strReturnedAddress.append(returnedAddress.getAddressLine(i)).append("\n");
                }
                strAdd = strReturnedAddress.toString();
                return strAdd;
            } else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    private BitmapDescriptor getMarkerIconFromDrawable(Drawable drawable) {
        Canvas canvas = new Canvas();
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        canvas.setBitmap(bitmap);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        drawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    @Override
    protected void onStop() {
        googleApiClient.disconnect();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        googleApiClient.disconnect();
        super.onStop();
    }

    public void preferenceLocation(){
        mMapView.clear();

        if(addObservation.getLatLng()!=null){

            LatLng latLng=addObservation.getLatLng();
            String address = "Select One";

            Drawable drawable = getApplicationContext().getResources().getDrawable(R.drawable.icon_map_pin);
            BitmapDescriptor icon = getMarkerIconFromDrawable(drawable);
            if (getAddress(latLng.latitude, latLng.longitude) != null) {
                address = String.valueOf(getAddress(latLng.latitude, latLng.longitude));
            }


            locationAddress.setText(address);
            MarkerOptions marker = new MarkerOptions().position(latLng).icon(icon);
            mMapView.addMarker(marker);
            mMapView.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17));

        }
    }



    @Override
    public void onTimeSet(TimePickerDialog view, int hourOfDay, int minute, int second) {
        String hourString = hourOfDay < 10 ? "0" + hourOfDay : "" + hourOfDay;
        String minuteString = minute < 10 ? "0" + minute : "" + minute;
         whenTime.setText(hourString + " hours " + minuteString + " mins ago");
    }

    public void photoSelection(){
        if (EasyPermissions.hasPermissions(getApplicationContext(), new String[]{FilePickerConst.PERMISSIONS_FILE_PICKER})) {
            onPickPhoto();
        } else {
            // Ask for one permission
            EasyPermissions.requestPermissions(AddObservation.this, getString(R.string.rationale_photo_picker),
                    RC_PHOTO_PICKER_PERM, new String[]{FilePickerConst.PERMISSIONS_FILE_PICKER});
        }
    }


    private void addThemToView(ArrayList<String> imagePaths) {
        ArrayList<String> filePaths = new ArrayList<>();
        if (imagePaths != null) filePaths.addAll(imagePaths);



        if (recyclerView != null) {
            StaggeredGridLayoutManager layoutManager =
                    new StaggeredGridLayoutManager(3, OrientationHelper.VERTICAL);
            layoutManager.setGapStrategy(
                    StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS);
            recyclerView.setLayoutManager(layoutManager);

            imageAdapter = new ObservationImageAdapter(AddObservation.this, filePaths);

            recyclerView.setAdapter(imageAdapter);
            recyclerView.setItemAnimator(new DefaultItemAnimator());
        }

        Toast.makeText(this, "Num of files selected: " + filePaths.size(), Toast.LENGTH_SHORT).show();
    }


    public void onPickPhoto() {
        int maxCount = MAX_ATTACHMENT_COUNT ;
        if ( photoPaths.size()== MAX_ATTACHMENT_COUNT) {
            validateSelection();
        } else {
            FilePickerBuilder.getInstance()
                    .setMaxCount(maxCount)
                    .setSelectedFiles(photoPaths)
                    .setActivityTheme(R.style.FilePickerTheme)
                    .setActivityTitle("Please select media")
                    .enableVideoPicker(true)
                    .enableCameraSupport(true)

                    .showGifs(false)
                    .showFolderView(true)
                    .enableSelectAll(true)
                    .enableImagePicker(true)
                    .setCameraPlaceholder(R.drawable.custom_camera)
                    .withOrientation(Orientation.PORTRAIT_ONLY)
                    .pickPhoto(this);
        }
    }

    @Override public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                                     @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
    }

    @Override public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {

        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            new AppSettingsDialog.Builder(this).build().show();
        }
    }

    public void deleteSuccesfull(int position){
        photoPaths.remove(position);
        imageAdapter.notifyItemRemoved(position);

        imageAdapter.notifyItemRangeChanged(position, imageAdapter.getItemCount());


    }

    public void validateSelection(){
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("Message");
        alertDialogBuilder.setMessage("You can select upto only 6 files");
                alertDialogBuilder.setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {
                               finish();
                            }
                        });



        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    public void postObservation(){



        queue = RequestHandler.getInstance(getApplicationContext()).getRequestQueue(); //Obtain the instance

        String url = getString(R.string.api_url)+"observations/full" ;
        VolleyMultipartRequest multipartRequest = new VolleyMultipartRequest(Request.Method.POST, url, new Response.Listener<NetworkResponse>() {
            @Override
            public void onResponse(NetworkResponse response) {
                String resultResponse = new String(response.data);
                try {
                    JSONObject result = new JSONObject(resultResponse);
                    String status = result.getString("status");
                    String message = result.getString("message");

                    if (status.equals(true)){
                        // tell everybody you have succed upload image and post strings
                        Log.i("Messsage", message);
                    } else {
                        Log.i("Unexpected", message);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                NetworkResponse networkResponse = error.networkResponse;
                String errorMessage = "Unknown error";
                String jsonError;
                if (networkResponse != null && networkResponse.data != null) {
                     jsonError = new String(networkResponse.data);
                     Log.e("EA",jsonError);
                     String abc=new String (networkResponse.data);
                    // Print Error!
                }
                if (networkResponse == null) {
                    if (error.getClass().equals(TimeoutError.class)) {
                        errorMessage = "Request timeout";
                    } else if (error.getClass().equals(NoConnectionError.class)) {
                        errorMessage = "Failed to connect server";
                    }
                } else {
                    String result = new String(networkResponse.data);
                    try {
                        JSONObject response = new JSONObject(result);
                        String status = response.getString("status");
                        String message = response.getString("message");

                        Log.e("Error Status", status);
                        Log.e("Error Message", message);

                        if (networkResponse.statusCode == 404) {
                            errorMessage = "Resource not found";
                        } else if (networkResponse.statusCode == 401) {
                            errorMessage = message+" Please login again";
                        } else if (networkResponse.statusCode == 400) {
                            errorMessage = message+ " Check your inputs";
                        } else if (networkResponse.statusCode == 500) {
                            errorMessage = message+" Something is getting wrong";
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                Log.i("Error", errorMessage);
                error.printStackTrace();
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();

//                "name": "",
//                        "comments": "",
//                        "streetNumber": "617-643",
//                        "streetName": "617-643",
//                        "countryCode": "AU",
//                        "location": "West Melbourne",
//                        "state": "VIC",
//                        "text": "I hear thunder",
//                        "deviceName": "iPhone 5s",
//                        "appVersion": "",
//                        "topic": "I hear",
//                        "group": "Weather",
//                        "type": "Thunder",
//                        "icon": "icons_public_observation_weather",
//                        "category": "public_observation",
//                        "obsWhen": "",
//                        "obsWhere": "",
//                        "osVersion": "10.0.1",
//                        "os": "iOS",
//                        "geoPoint": "POINT(144.94509232244 -37.8067685896883)]",
//                        "timeAgo": "2017-12-12 11:46:07+1100",
//                        "deviceId": 35
                params.put("name", "");
                params.put("comments", "");//temp empty
                params.put("streetNumber", "617-643");
                params.put("streetName", "617-643");
                params.put("countryCode", "AU");
                params.put("location", "West Melbourne");
                params.put("state", "VIC");
                params.put("text", "I hear thunder");//addObservation.getWhatText());
                params.put("deviceName", new DeviceUtil(getApplicationContext()).getDeviceName());
                params.put("appVersion",getString(R.string.appVersion));
                params.put("topic", "");
                params.put("group", "Weather");//addObservation.getGroup());
                params.put("type", "Thunder");//addObservation.getType());
                params.put("icon",  "icons_public_observation_weather");//addObservation.getIcon());
                params.put("category", "public_observation");
                params.put("obsWhen", "");//temp empty
                params.put("obsWhere", "");//temp empty
                params.put("osVersion", new DeviceUtil(getApplicationContext()).getOsVersion());
                params.put("os", "Android");
                params.put("geoPoint", "POINT(144.94509232244 -37.8067685896883)");
                params.put("timeAgo", "2017-12-12 11:46:07+1100");
                params.put("deviceId", (String)PreferenceUtils.getFromPrefs(getApplicationContext(),getString(R.string.pref_user_id),"0"));

                return params;
            }
            @Override
            public Map < String, String > getHeaders() throws AuthFailureError {
                HashMap < String, String > headers = new HashMap < String, String > ();
        // headers.put("Content-Type", "application/json");
                headers.put("Authorization", "Bearer " + PreferenceUtils.getFromPrefs(getApplicationContext(), getString(R.string.pref_user_token), ""));
                return headers;

            }
            @Override
            protected Map<String, DataPart> getByteData() {
                Map<String, DataPart> params = new HashMap<>();
                // file name could found file base or direct access from real path
                // for now just get bitmap data from ImageView
                for(int i=0;i<photoPaths.size();i++){
                params.put("attachment"+i, new DataPart("attachment"+i+".jpeg", getFileDataFromFile(getApplicationContext(),photoPaths.get(i)),"image/jpeg"));
                }
                return params;
            }
        };

        multipartRequest.setRetryPolicy(new DefaultRetryPolicy(5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(multipartRequest);

    }

    public  byte[] getFileDataFromFile(Context context,String url) {

        ArrayList<byte[]> imgBytes=new ArrayList<>();
        Storage storage = new Storage(context);
//       for(int i=0;i<photoPaths.size();i++){
//           imgBytes.add(storage.readFile(photoPaths.get(i)));
//       }
      return storage.readFile(url);
    }


}
