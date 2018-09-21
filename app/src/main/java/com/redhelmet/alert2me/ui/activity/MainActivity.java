package com.redhelmet.alert2me.ui.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.redhelmet.alert2me.BuildConfig;
import com.redhelmet.alert2me.core.DeviceUtil;
import com.redhelmet.alert2me.domain.util.PreferenceUtils;
import com.redhelmet.alert2me.domain.util.Utility;
import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import com.redhelmet.alert2me.R;
import com.redhelmet.alert2me.ui.base.BaseActivity;
import com.redhelmet.alert2me.ui.base.BaseViewModel;
import com.redhelmet.alert2me.ui.hint.HintsActivity;

/**
 * Created by inbox on 13/11/17.
 */

public class MainActivity extends BaseActivity implements View.OnClickListener {



    Button acceptTerms;
    TextView replayHint, termsCondition;
    Intent i;
    private String pref_accepted = "pref_accepted";
    DeviceUtil deviceUtil;
    String apiURL=null;
    ProgressBar pBar;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_main;
    }

    @Override
    protected BaseViewModel obtainViewModel() {
        return null;
    }

    @Override
    protected void configWindow() {
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        InitializeControl();
        getUserId();
    }
    public void InitializeControl(){
        apiURL= BuildConfig.API_ENDPOINT+"device";
        deviceUtil=new DeviceUtil(getApplicationContext());
        pBar=(ProgressBar)findViewById(R.id.progressBar2);
        acceptTerms=(Button) findViewById(R.id.termsAccept);
        replayHint=(TextView) findViewById(R.id.replayHint);
        termsCondition =(TextView) findViewById(R.id.termsCondition);

        acceptTerms.setOnClickListener(this);
        replayHint.setOnClickListener(this);
        termsCondition.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId())
        {
            case R.id.termsAccept:

                pBar.setVisibility(View.VISIBLE);
                getUserId();

                break;
            case R.id.termsCondition:
                Uri webpage  = Uri.parse(config.getTermsAndConditionUrl());
                Intent intent = new Intent(Intent.ACTION_VIEW, webpage);
                startActivity(intent);
                break;
            case R.id.replayHint:
                PreferenceUtils.removeFromPrefs(this,getString(R.string.pref_initialLaunch));
                Intent o = new Intent(MainActivity.this, HintsActivity.class);
                o.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(o);
                break;
        }
    }

    public void getUserId(){
        if(!Utility.isInternetConnected(getApplicationContext())) {
            Toast.makeText(getApplicationContext(), getString(R.string.noInternet), Toast.LENGTH_LONG).show();
            return;
        }
        //if user accept terms and condition get the User id from the server

        Map<String, String> mParams = new HashMap<String, String>();
        mParams.put("platform",getString(R.string.platform));
        mParams.put("deviceToken",FirebaseInstanceId.getInstance().getToken());
        mParams.put("deviceName","android");
        mParams.put("version",getString(R.string.appVersion));
        mParams.put("systemName",Build.VERSION_CODES.class.getFields()[Build.VERSION.SDK_INT].getName());
        mParams.put("systemVersion",deviceUtil.getOsVersion());
        mParams.put("model",deviceUtil.getDeviceName());
        mParams.put("tablet","false");

//        Log.d("valuesUSrs", mParams.toString());
      RequestQueue queue = Volley.newRequestQueue(this);



        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST,
                apiURL, new JSONObject(mParams),
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        pBar.setVisibility(View.INVISIBLE);
                        try {
                            Log.d("response", response.toString());

                            if(Boolean.valueOf(response.getString("success"))){
                                JSONObject user=response.getJSONObject("device");

                                if(user!=null ){
                                    PreferenceUtils.saveToPrefs(getApplicationContext(),getString(R.string.pref_user_id),user.getString("id"));
                                    PreferenceUtils.saveToPrefs(MainActivity.this,getString(R.string.pref_accepted),true);
                                    PreferenceUtils.saveToPrefs(getApplicationContext(),getString(R.string.pref_user_token),user.getString("apiToken"));
                                    i=new Intent(MainActivity.this,HomeActivity.class);
                                    startActivity(i);
                                    finish();
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.d("response", e.toString());
                        }
                        VolleyLog.d("EA", "E");
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {

                    Toast.makeText(getApplicationContext(), getString(R.string.timeOut), Toast.LENGTH_LONG).show();
                    error.printStackTrace();
                PreferenceUtils.saveToPrefs(getApplicationContext(),getString(R.string.pref_user_id),"0");
                PreferenceUtils.saveToPrefs(MainActivity.this,getString(R.string.pref_accepted),true);
                PreferenceUtils.saveToPrefs(getApplicationContext(),getString(R.string.pref_user_token),"sdfsdfs");
                i=new Intent(MainActivity.this,HomeActivity.class);
                startActivity(i);
                finish();

                pBar.setVisibility(View.INVISIBLE);
            }
        }) {
//
//            /**
//             * Passing some request headers
//             * */
//            @Override
//            public Map<String, String> getHeaders() throws AuthFailureError {
//                HashMap<String, String> headers = new HashMap<String, String>();
//                headers.put("Content-Type", "application/json; charset=utf-8");
//                headers.put("User-agent", System.getProperty("http.agent"));
//                return headers;
//
//            }
            @Override
            public String getBodyContentType()
            {
                return "application/json; charset=utf-8";
            }


            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();

               // headers.put("Authorization", webApiToken);
                return headers;
            }


        };


        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(jsonObjReq);
    }

}
