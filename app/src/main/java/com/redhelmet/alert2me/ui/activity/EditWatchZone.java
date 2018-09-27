package com.redhelmet.alert2me.ui.activity;

import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
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
import com.redhelmet.alert2me.data.local.database.DBController;
import com.redhelmet.alert2me.domain.util.PreferenceUtils;
import com.redhelmet.alert2me.domain.util.Utility;
import com.redhelmet.alert2me.data.model.CategoryFilter;
import com.redhelmet.alert2me.data.model.CategoryTypeFilter;
import com.redhelmet.alert2me.data.model.EditWatchZones;
import com.redhelmet.alert2me.data.model.WatchZoneGeom;
import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import com.kevalpatel.ringtonepicker.RingtonePickerDialog;
import com.kevalpatel.ringtonepicker.RingtonePickerListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.redhelmet.alert2me.R;

import static com.redhelmet.alert2me.R.id.notification_sound_text;
import static com.redhelmet.alert2me.R.id.select_ringtone;


public class EditWatchZone extends AppCompatActivity implements View.OnClickListener {


    Toolbar toolbar;
    Intent i;
    LinearLayout editModeSound, EditModeLocation, EditModeSetting;
    TextView soundName;
    TextView required_name;
    EditText wzName;
    ArrayList <EditWatchZones> wzData = new ArrayList < EditWatchZones > ();
    EditWatchZones editWz;
    int position;
    RingtonePickerDialog.Builder ringtonePickerBuilder;
    String _ringtoneName;
    Uri _ringtoneURI;
    String editWzURL;
    String TAG = "hello";
    DBController dbController;
    List<String> categoryNamesDB;
    public final static int REQUEST_NOTIFICATION = 001;
    LinearLayout _editWatchzoneLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_edit_watchzone);
        editWz =  EditWatchZones.getInstance();
        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            position = extras.getInt("position");
        }

        dbController = new DBController(getApplicationContext());
        categoryNamesDB=new ArrayList<>();
        categoryNamesDB=dbController.getCategoriesNames();

        wzData = editWz.getEditWz();
        editWzURL =BuildConfig.API_ENDPOINT + "apiInfo/" + wzData.get(position).getWatchzoneDeviceId() + "/watchzones/" + wzData.get(position).getWatchzoneId();
        ringtonePickerBuilder = new RingtonePickerDialog.Builder(EditWatchZone.this, getSupportFragmentManager());
        ringtonePickerBuilder.addRingtoneType(RingtonePickerDialog.Builder.TYPE_NOTIFICATION);
        ringtonePickerBuilder.setPlaySampleWhileSelection(AddStaticZone.checkVibrationIsOn(getApplicationContext()));
        ringtonePickerBuilder.setListener(new RingtonePickerListener() {
            @Override
            public void OnRingtoneSelected(String ringtoneName, Uri ringtoneUri) {
                _ringtoneName = ringtoneName;
                _ringtoneURI = ringtoneUri;
                wzData.get(position).setWatchzoneSound(ringtoneUri.toString());
                soundName.setText(ringtoneName);
            }
        });


        initializeToolbar();
        initializeControls();
    }



    public void initializeToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }
        ActionBar supportActionBar = getSupportActionBar();

        if (supportActionBar != null) {
            supportActionBar.setDisplayHomeAsUpEnabled(true);
            supportActionBar.setTitle(Html.fromHtml("<small>Edit Static Zone</small>"));
        }
    }

    private void initializeControls() {
        Ringtone ringtone;

        wzName = (EditText) findViewById(R.id.watch_zone_name);
        required_name = (TextView) findViewById(R.id.required_name);
        wzName.setText(wzData.get(position).getWatchzoneName());
        wzName.addTextChangedListener(watchText);
        soundName = (TextView) findViewById(notification_sound_text);
        editModeSound = (LinearLayout) findViewById(select_ringtone);
        EditModeLocation = (LinearLayout) findViewById(R.id.edit_watch_zone_location);
        EditModeSetting = (LinearLayout) findViewById(R.id.edit_watch_zone_filter);
        _editWatchzoneLayout=(LinearLayout)findViewById(R.id.content_edit_watch_zone);
        editModeSound.setOnClickListener(this);
        EditModeLocation.setOnClickListener(this);
        EditModeSetting.setOnClickListener(this);

        if (wzData.get(position).getWatchzoneSound() == null) {
            _ringtoneURI = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        } else {
            _ringtoneURI = Uri.parse(wzData.get(position).getWatchzoneSound());
        }
        ringtone = RingtoneManager.getRingtone(getApplicationContext(), _ringtoneURI);
        _ringtoneName = ringtone.getTitle(getApplicationContext());
        if (_ringtoneName == "") {
            _ringtoneURI = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            ringtone = RingtoneManager.getRingtone(getApplicationContext(), _ringtoneURI);

            _ringtoneName = ringtone.getTitle(getApplicationContext());
        }
        soundName.setText(ringtone.getTitle(getApplicationContext()));
        wzData.get(position).setWatchzoneName(wzName.getText().toString());

    }

    @Override
    public void onClick(View v) {
        wzData.get(position).setWatchzoneName(wzName.getText().toString());
        switch (v.getId()) {
            case R.id.select_ringtone:
                ringtonePickerBuilder.show();
                break;
            case R.id.edit_watch_zone_location:

                i = new Intent(EditWatchZone.this, AddStaticZoneLocation.class);
                i.putExtra("position", position);
                i.putExtra("edit", true);
                startActivity(i);
                break;
            case R.id.edit_watch_zone_filter:
                i = new Intent(EditWatchZone.this, AddStaticZoneNotification.class);
                i.putExtra("position", position);
                i.putExtra("edit", true);
                i.putExtra("mobile", false);
                startActivityForResult(i,REQUEST_NOTIFICATION);

                break;

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.watchzone_static_done, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.done_btn:
                if (validation()) {
                    updateData();
                }
                return true;
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    public boolean validation() {
        if (wzName.getText().toString().matches("^\\s*$") || wzName.getText().toString().trim().length() == 0) {
            required_name.setVisibility(View.VISIBLE);
            return false;
        }
        return true;
    }
    TextWatcher watchText = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            required_name.setVisibility(View.INVISIBLE);
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    public void updateData() {
        if(!Utility.isInternetConnected(getApplicationContext())) {
            Toast.makeText(getApplicationContext(), getString(R.string.noInternet), Toast.LENGTH_LONG).show();
            return;
        }

        Map < String, Object > mParams = new HashMap < String, Object > ();
        Gson gson = new Gson();
        ArrayList defValues = new ArrayList < > ();
        try {

            //setting wz name
            wzData.get(position).setWatchzoneName(wzName.getText().toString());
            mParams.put("name", wzData.get(position).getWatchzoneName());
            showSnack(_editWatchzoneLayout,getString(R.string.msg_upatingWZ));

            //setting wz sound
            mParams.put("sound", wzData.get(position).getWatchzoneSound());

            //checking location scenerio
            String locationData;
            WatchZoneGeom locationGeom;
            if (PreferenceUtils.hasKey(getApplicationContext(), "wzLocation")) {
                locationData = (String) PreferenceUtils.getFromPrefs(getApplicationContext(), "wzLocation", " ");
                JSONObject obj = gson.fromJson(locationData, JSONObject.class);
                int result = obj.getInt("radius");
                mParams.put("radius", Integer.toString(result));
                mParams.put("geom", obj.getString("geom"));
                mParams.put("type", obj.getString("type"));

            } else {
                locationGeom = wzData.get(position).getWatchZoneGeoms();
                if(locationGeom.getType().equals("Point"))
                mParams.put("type", "STANDARD");
                else
                    mParams.put("type", "VARIABLE");

                StringBuilder st = new StringBuilder();
                if (locationGeom.getType().equals("Point")) {
                    ArrayList<HashMap<String,Double>>  location = locationGeom.getCordinate();

                    HashMap<String,Double> values =  location.get(0);
                    double lon=values.get("latitude");
                    double lat=values.get("longitude");


                    st.append(String.format("POINT(%s %s)", lon, lat));
                    mParams.put("geom", st.toString());
                } else {
                    ArrayList<HashMap<String,Double>> cordi= locationGeom.getCordinate();
                    String formattedPoint="";

                    for (int i = 0; i <= cordi.size() - 1; i++) {
                        HashMap<String,Double> values =  cordi.get(i);
                        double lat=values.get("latitude");
                        double lon=values.get("longitude");
                        formattedPoint += String.format("%s %s,", lat, lon);
                        if (i == cordi.size() - 1) {
                            formattedPoint += String.format("%s %s", lat, lon);
                        }
                    }
                    mParams.put("geom", String.format("POLYGON((%s))", formattedPoint));

                }

                mParams.put("radius", wzData.get(position).getWatchzoneRadius());
            }

            List < Integer > filterGroup = wzData.get(position).getWatchzoneFilterGroupId();
            mParams.put("filterGroupId", filterGroup);
            LinkedTreeMap<String, Object> categoryFilters = new LinkedTreeMap<>();
            ArrayList<HashMap<String, CategoryFilter>> groupData = wzData.get(position).getWatchzoneFilter();

            for (int d=0;d<categoryNamesDB.size();d++) {
                for (int j = 0; j < groupData.size(); j++) {
                    HashMap<String, CategoryFilter> tempData = groupData.get(j);

                    if (tempData.keySet().toArray()[0].equals(categoryNamesDB.get(d).toString())) {

                         JSONObject category = new JSONObject();
                        JSONArray typeArray = new JSONArray();
                        JSONObject catTypeObj = new JSONObject();
                           CategoryFilter catFilter = tempData.get(categoryNamesDB.get(d).toString());
                         List<CategoryTypeFilter> ct = catFilter.getTypes();


                        for(int x=0;x<ct.size();x++) {
                            JSONObject type = new JSONObject();
                            JSONArray status = new JSONArray();
                            for (String catStatus : ct.get(x).getStatus()) {

                                status.put(catStatus);

                            }
                            type.put("code", ct.get(x).getCode());
                            type.put("status", status);
                            typeArray.put(type);
                        }

                        category.put("types", typeArray);
                        categoryFilters.put(categoryNamesDB.get(d).toString(), category);

                         groupData.remove(tempData);
                    }
                }

            }


            mParams.put("filter", categoryFilters);
            mParams.put("proximity", String.valueOf("false"));
            mParams.put("address", "");



        } catch (JSONException e) {
            e.printStackTrace();
        }

        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.PUT,
                editWzURL, new JSONObject(mParams),
                new Response.Listener < JSONObject > () {

                    @Override
                    public void onResponse(JSONObject response) {

                        try {

                            if (Boolean.valueOf(response.getString("success"))) {

                                changeText(wzData.get(position).getWatchzoneName()+ " " + getString(R.string.msg_updatedWZ));
                                dismisSnackbar();
                                Thread.sleep(2000);
                                onBackPressed();
                            }
                            else {
                                changeText( getString(R.string.msg_unableUpdateWZ));
                                dismisSnackbar();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            dismisSnackbar();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                            dismisSnackbar();
                        }

                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {

                changeText(getString(R.string.timeOut));
                error.printStackTrace();

                dismisSnackbar();
            }
        }) {
            /**
             * Passing some request headers
             * */
            @Override
            public Map < String, String > getHeaders() throws AuthFailureError {
                HashMap < String, String > headers = new HashMap < String, String > ();
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch(requestCode) {
            case REQUEST_NOTIFICATION:
               if(resultCode==RESULT_OK) {
                    Intent intent = data;
                    ArrayList<HashMap<String, CategoryFilter>> filter = (ArrayList<HashMap<String, CategoryFilter>>) intent.getSerializableExtra("filter");
                    List<Integer> filterGroup = (List<Integer>) intent.getSerializableExtra("filterGroup");
                       wzData.get(position).setWzDefault(intent.getBooleanExtra("default", wzData.get(position).isWzDefault()));
                    wzData.get(position).setWatchzoneFilter(filter);
                    wzData.get(position).setWatchzoneFilterGroupId(filterGroup);
                }

                break;

        }
    }
}