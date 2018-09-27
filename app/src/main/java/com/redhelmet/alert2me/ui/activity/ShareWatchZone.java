package com.redhelmet.alert2me.ui.activity;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
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
import com.redhelmet.alert2me.domain.util.PreferenceUtils;
import com.redhelmet.alert2me.domain.util.Utility;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import com.redhelmet.alert2me.R;

/**
 * Created by inbox on 21/11/17.
 */

public class ShareWatchZone extends BaseActivity {

    Toolbar toolbar;
    EditText shareWzCode;
    String shareWzUrl;

    Snackbar snackbar=null;
    private View rootView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_share_wz);

        initializeToolbar();
        initializeControls();


    }

    private void initializeControls() {
        shareWzUrl = BuildConfig.API_ENDPOINT + "apiInfo/" + PreferenceUtils.getFromPrefs(getApplicationContext(), getString(R.string.pref_user_id), "") + "/watchzones";
        shareWzCode = (EditText) findViewById(R.id.watch_zone_share_edit);
    }

    public void initializeToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }
        ActionBar supportActionBar = getSupportActionBar();

        if (supportActionBar != null) {
            supportActionBar.setDisplayHomeAsUpEnabled(true);
            supportActionBar.setTitle(Html.fromHtml("<small>"+getString(R.string.lbl_addShareWZ)+"</small>"));

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.watchzone_share_add, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add_share_code:
                if (shareWzCodeValidation()) {
                    shareWzCall();
                }
//finish();
                return true;
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public boolean shareWzCodeValidation() {

        if (shareWzCode.getText().toString().matches("^\\s*$") || shareWzCode.getText().toString().trim().length() == 0) {
            Toast.makeText(getApplicationContext(), getString(R.string.share_empty_wz), Toast.LENGTH_LONG).show();

            return false;
        }

        return true;
    }

    public void shareWzCall() {

        InputMethodManager imm = (InputMethodManager)getSystemService(getApplicationContext().INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(shareWzCode.getWindowToken(), 0);

        if(!Utility.isInternetConnected(getApplicationContext())) {
            Toast.makeText(getApplicationContext(), getString(R.string.noInternet), Toast.LENGTH_LONG).show();
            return;
        }

        Toast.makeText(getApplicationContext(), getString(R.string.msg_addingShareWZ), Toast.LENGTH_LONG).show();

        Map<String, Object> mParams = new HashMap<String, Object>();

        mParams.put("shareCode", shareWzCode.getText().toString());


        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        JsonObjectRequest jsonObjReq =
                new JsonObjectRequest(Request.Method.POST,
                shareWzUrl, new JSONObject(mParams),
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {

                        try {

                            if (Boolean.valueOf(response.getString("success"))) {

                                Toast.makeText(getApplicationContext(), getString(R.string.msg_addedShareWZ), Toast.LENGTH_LONG).show();
                               finish();

                            }
                            else {
                                Toast.makeText(getApplicationContext(), getString(R.string.msg_unableShareWZ), Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError e) {

                    Toast.makeText(getApplicationContext(), getString(R.string.timeOut), Toast.LENGTH_LONG).show();
            }
        }) {
            /**
             * Passing some request headers
             * */
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