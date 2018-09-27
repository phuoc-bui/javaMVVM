
    package com.redhelmet.alert2me.ui.activity;

    import android.content.Intent;
    import android.content.IntentFilter;
    import android.os.Bundle;
    import android.util.Log;
    import android.view.View;
    import android.view.Window;
    import android.view.WindowManager;
    import android.widget.ProgressBar;
    import android.widget.Toast;

    import com.android.volley.DefaultRetryPolicy;
    import com.android.volley.Request;
    import com.android.volley.RequestQueue;
    import com.android.volley.Response;
    import com.android.volley.VolleyError;
    import com.android.volley.toolbox.JsonObjectRequest;
    import com.redhelmet.alert2me.core.CoreFunctions;
    import com.redhelmet.alert2me.core.DBController;
    import com.redhelmet.alert2me.core.RequestHandler;
    import com.redhelmet.alert2me.domain.ExceptionHandler;
    import com.redhelmet.alert2me.domain.util.PreferenceUtils;
    import com.redhelmet.alert2me.receiver.NetworkStateReceiver;

    import org.json.JSONArray;
    import org.json.JSONException;
    import org.json.JSONObject;

    import java.util.ArrayList;
    import java.util.HashMap;

    import com.redhelmet.alert2me.R;

    public class SplashScreen extends BaseActivity implements NetworkStateReceiver.NetworkStateReceiverListener {
        RequestQueue queue;
        JsonObjectRequest volleyRequest;
        private NetworkStateReceiver networkStateReceiver;
        private final int SPLASH_DISPLAY_LENGTH = 3000;
        CoreFunctions cf;
        DBController dbController;
        ProgressBar progressBar;
        IntentLauncher launcher;

        @Override
        public void onCreate(Bundle icicle) {
            super.onCreate(icicle);
            Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler());
             launcher= new IntentLauncher();

            getWindow().requestFeature(Window.FEATURE_NO_TITLE);
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

            setContentView(R.layout.activity_splash_screen);

            dbController = new DBController(getApplicationContext());
            progressBar = (ProgressBar) findViewById(R.id.splash_progress);
            cf = new CoreFunctions(getApplicationContext());

            //setup the broadcast receiver for network change
            networkStateReceiver = new NetworkStateReceiver();
            networkStateReceiver.addListener(this);
            this.registerReceiver(networkStateReceiver, new IntentFilter(android.net.ConnectivityManager.CONNECTIVITY_ACTION));

            //downloading config
//            if(!Utility.isInternetConnected(getApplicationContext())) {
//                Toast.makeText(getApplicationContext(), getString(R.string.noInternet), Toast.LENGTH_LONG).show();
//                return;
//            }

            queue = RequestHandler.getInstance(getApplicationContext()).getRequestQueue(); //Obtain the instance

            volleyRequest = new JsonObjectRequest(Request.Method.GET, cf.ConfigUrl(), // getting config url from COREFUNCTIONS
                    null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    if (response != null) {
                        try {
                            if (response.getBoolean("success")) {
                                JSONObject appDetail = response.getJSONObject("app");
                                savePreferences(appDetail);
                                saveDatabase(response);

                                if (launcher.getState() == Thread.State.NEW)
                                {
                                    launcher.start();
                                }

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    progressBar.setVisibility(View.GONE);
//                    if (error instanceof NoConnectionError) {
                        Toast.makeText(getApplicationContext(),getString(R.string.timeOut), Toast.LENGTH_LONG).show();
                        if (launcher.getState() == Thread.State.NEW)
                        {
                            launcher.start();
                        }
//
//                    } else if (error instanceof TimeoutError) {
//                        Toast.makeText(getApplicationContext(), getString(R.string.timeOut), Toast.LENGTH_LONG).show();
//                        error.printStackTrace();
//                    }


                }
            });

            //TODO: Change the retry policy
            volleyRequest.setRetryPolicy(new DefaultRetryPolicy(5000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            queue.add(volleyRequest);

        }

        @Override
        public void networkAvailable() {
            progressBar.setVisibility(View.VISIBLE);
            queue.add(volleyRequest);


        }

        @Override
        public void networkUnavailable() {

            queue.cancelAll(true);
        }

        public void onDestroy() {
            super.onDestroy();
            networkStateReceiver.removeListener(this);
            this.unregisterReceiver(networkStateReceiver);
        }

        public void savePreferences(JSONObject appDetail) {
            try {

                /*

                Storing necessary param in shared preference or model i.e define in BaseActivity
                 */


                PreferenceUtils.saveToPrefs(getApplicationContext(), getString(R.string.pref_help_support_url), appDetail.getString("helpSupportUrl"));
                PreferenceUtils.saveToPrefs(getApplicationContext(), getString(R.string.pref_help_support_email), appDetail.getString("helpSupportEmail"));
                PreferenceUtils.saveToPrefs(getApplicationContext(), getString(R.string.pref_terms_and_condition_url), appDetail.getString("termsAndConditionUrl"));
                PreferenceUtils.saveToPrefs(getApplicationContext(), getString(R.string.pref_tertiaryColor), appDetail.getString("tertiaryColor"));
                PreferenceUtils.saveToPrefs(getApplicationContext(), getString(R.string.pref_basewms_url), appDetail.getString("baseWms"));
                PreferenceUtils.saveToPrefs(getApplicationContext(), getString(R.string.pref_secondary_color), appDetail.getString("secondaryColor"));


                ArrayList<HashMap<String,String>> hints=new ArrayList<HashMap<String,String>>();
                JSONArray hintArray=appDetail.getJSONArray("hintScreen");
                for(int i=0;i<hintArray.length();i++){
                    JSONObject hintObj=hintArray.getJSONObject(i);
                    HashMap<String,String> hint=new HashMap<String, String>();
                    hint.put("title",hintObj.getString("title"));
                    hint.put("url",hintObj.getString("url"));
                    hints.add(hint);
                }
                config.setHintsScreen(hints);
                config.setNavColor(appDetail.getString("tertiaryColor"));
                config.setPrimaryColor(appDetail.getString("primaryColor"));
                config.setSecondaryColor(appDetail.getString("secondaryColor"));
                config.setStatusBarColor(appDetail.getString("statusBarColor"));
                config.setTermsAndConditionUrl(appDetail.getString("termsAndConditionUrl"));
                config.setTextColor(appDetail.getString("textColor"));

               // PreferenceUtils.saveToPrefs(getApplicationContext(), getString(R.string.pref_about_url), appDetail.getString("aboutUrl"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }



        public void saveDatabase(JSONObject response) {


            /*

            Delete the previous category, type and status relation.... add everything from start

             */

            dbController.deleteTable();
            dbController.createConfigDB();
            try {


                JSONObject root = new JSONObject(response.toString());
                JSONArray arr_cat = root.getJSONArray("category");
                JSONArray default_cat = root.getJSONArray("eventGroup");
                ArrayList<HashMap> default_categories = new ArrayList<HashMap>();
                for (int i = 0; i < default_cat.length(); i++) {
                    JSONObject js_cat = default_cat.getJSONObject(i);

                    HashMap<String, String> category = new HashMap<String, String>();
                    category.put(DBController.KEY_DEFAULT_CATEGORY_ID, js_cat.getString("id"));
                    category.put(DBController.KEY_DEFAULT_CATEGORY_NAME, js_cat.getString("name"));
                    category.put(DBController.KEY_DEFAULT_CATEGORY_DESC, js_cat.getString("description"));
                    category.put(DBController.KEY_DEFAULT_CATEGORY_DISPLAY_ON, js_cat.getString("displayOn"));
                    category.put(DBController.KEY_DEFAULT_CATEGORY_DISPLAY_TOGGLE, js_cat.getString("displayToggle"));
                    category.put(DBController.KEY_DEFAULT_CATEGORY_DISPLAY_ONLY, js_cat.getString("displayOnly"));
                    category.put(DBController.KEY_DEFAULT_CATEGORY_FILTER_ON, js_cat.getString("filterOn"));
                    category.put(DBController.KEY_DEFAULT_CATEGORY_FILTER_TOGGLE, js_cat.getString("filterToggle"));
                    category.put(DBController.KEY_DEFAULT_CATEGORY_DISPLAYFILTER, js_cat.getString("displayFilter"));
                    default_categories.add(category);
                }
                dbController.add_category_default(default_categories);

                for (int i = 0; i < arr_cat.length(); i++) {

                    JSONObject js_cat = arr_cat.getJSONObject(i);
                    JSONArray status_root = js_cat.getJSONArray("statuses");
                    JSONArray type_root = js_cat.getJSONArray("types");

                    HashMap<String, String> category = new HashMap<String, String>();

                    ArrayList<HashMap> categories = new ArrayList<HashMap>();
                    ArrayList<HashMap> statuses = new ArrayList<HashMap>();
                    ArrayList<HashMap> types = new ArrayList<HashMap>();

                    category.put(DBController.KEY_CATEGORY, js_cat.getString("category"));
                    category.put(DBController.KEY_CATEGORY_NAME, js_cat.getString("nameLabel"));
                    category.put(DBController.KEY_CATEGORY_DESC, js_cat.getString("filterDescription"));
                    category.put(DBController.KEY_CATEGORY_DISPLAY_ONLY, js_cat.getString("displayOnly"));
                    category.put(DBController.KEY_CATEGORY_FILTER_ORDER, js_cat.getString("filterOrder"));
                    categories.add(category);
                    long id = dbController.add_custom_config(categories);

                    if (id != -1) {
                        for (int j = 0; j < status_root.length(); j++) {
                            HashMap<String, String> status = new HashMap<String, String>();

                            JSONObject cat_status = status_root.getJSONObject(j);
                            // status.put(DBController.KEY_REF_STATUS_CATEGORY_ID, cat_status.getString("cat_id"));
                            status.put(DBController.KEY_CAT_STATUS_CODE, cat_status.getString("code"));
                            status.put(DBController.KEY_CAT_STATUS_DESC, cat_status.getString("description"));
                            status.put(DBController.KEY_CAT_STATUS_NAME, cat_status.getString("name"));
                            status.put(DBController.KEY_CAT_STATUS_PRIMARY_COLOR, cat_status.getString("primaryColor"));
                            status.put(DBController.KEY_CAT_STATUS_SECONDARY_COLOR, cat_status.getString("secondaryColor"));
                            status.put(DBController.KEY_CAT_STATUS_TEXT_COLOR, cat_status.getString("textColor"));
                            status.put(DBController.KEY_CAT_STATUS_CAN_FILTER, cat_status.getString("canFilter"));
                            status.put(DBController.KEY_CAT_STATUS_DEFAULT, cat_status.getString("defaultOn"));
                            status.put(DBController.KEY_CAT_STATUS_NOTIF_CAN_FILTER, cat_status.getString("notificationCanFilter"));
                            status.put(DBController.KEY_CAT_STATUS_NOTIF_DEFAULT, cat_status.getString("notificationDefaultOn"));
                            statuses.add(status);
                        }

                        for (int j = 0; j < type_root.length(); j++) {
                            HashMap<String, String> type = new HashMap<String, String>();
                            JSONObject cat_type = type_root.getJSONObject(j);
                            // type.put(DBController.KEY_REF_TYPE_CATEGORY_ID, cat_type.getString("cat_id"));
                            type.put(DBController.KEY_CAT_TYPE_CODE, cat_type.getString("code"));
                            type.put(DBController.KEY_CAT_TYPE_NAME, cat_type.getString("name"));
                            type.put(DBController.KEY_CAT_TYPE_ICON, cat_type.getString("icon"));
                            type.put(DBController.KEY_CAT_TYPE_DEFAULT, cat_type.getString("defaultOn"));
                            type.put(DBController.KEY_CAT_TYPE_CAN_FILTER, cat_type.getString("canFilter"));
                            type.put(DBController.KEY_CAT_TYPE_NOTIF_CAN_FILTER, cat_type.getString("notificationCanFilter"));
                            type.put(DBController.KEY_CAT_TYPE_NOTIF_DEFAULT, cat_type.getString("notificationDefaultOn"));
                            types.add(type);
                        }
                        dbController.add_custom_details(types, statuses, id);
                    }


                }


            } catch (JSONException e) {
                e.printStackTrace();
            }

        }


        private class IntentLauncher extends Thread {

            @Override
            /**
             * Sleep for some time and than start new activity.
             */
            public void run() {
                try {
                    // Sleeping
                    Thread.sleep(SPLASH_DISPLAY_LENGTH );
                } catch (Exception e) {
                    Log.e("EM", e.getMessage());
                }

                Intent mainIntent;
                if ((Boolean) PreferenceUtils.getFromPrefs(SplashScreen.this, getString(R.string.pref_initialLaunch), false)) {
                    if ((Boolean) PreferenceUtils.getFromPrefs(SplashScreen.this, getString(R.string.pref_accepted), false)) {
                        mainIntent = new Intent(SplashScreen.this, HomeActivity.class);
                    } else {
                        mainIntent = new Intent(SplashScreen.this, MainActivity.class);
                    }
                } else {
                    mainIntent = new Intent(SplashScreen.this, HintsActivity.class);
                }

                SplashScreen.this.startActivity(mainIntent);
                SplashScreen.this.finish();
            }
        }

    }