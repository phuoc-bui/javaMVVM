package com.redhelmet.alert2me.ui.watchzone;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.daimajia.swipe.util.Attributes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import com.kevalpatel.ringtonepicker.RingtonePickerDialog;
import com.kevalpatel.ringtonepicker.RingtonePickerListener;
import com.redhelmet.alert2me.BuildConfig;
import com.redhelmet.alert2me.R;
import com.redhelmet.alert2me.adapters.AppViewPagerAdapter;
import com.redhelmet.alert2me.adapters.WzListAdapter;
import com.redhelmet.alert2me.core.Constants;
import com.redhelmet.alert2me.core.RequestHandler;
import com.redhelmet.alert2me.data.database.DBController;
import com.redhelmet.alert2me.data.model.Category;
import com.redhelmet.alert2me.data.model.CategoryFilter;
import com.redhelmet.alert2me.data.model.CategoryStatus;
import com.redhelmet.alert2me.data.model.CategoryType;
import com.redhelmet.alert2me.data.model.CategoryTypeFilter;
import com.redhelmet.alert2me.data.model.EditWatchZones;
import com.redhelmet.alert2me.data.model.EventGroup;
import com.redhelmet.alert2me.data.model.WatchZoneGeom;
import com.redhelmet.alert2me.databinding.FragmentWatchzoneListBinding;
import com.redhelmet.alert2me.domain.util.PreferenceUtils;
import com.redhelmet.alert2me.domain.util.Utility;
import com.redhelmet.alert2me.ui.activity.AddStaticZone;
import com.redhelmet.alert2me.ui.activity.EditWatchZone;
import com.redhelmet.alert2me.ui.activity.ShareWatchZone;
import com.redhelmet.alert2me.ui.base.BaseFragment;
import com.redhelmet.alert2me.ui.services.BackgroundDetectedActivitiesService;
import com.redhelmet.alert2me.util.DeviceUtil;

import net.grandcentrix.tray.AppPreferences;

import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat;
import androidx.viewpager.widget.ViewPager;

public class WatchZoneFragment extends BaseFragment<WatchZoneViewModel, FragmentWatchzoneListBinding> implements SwipeRefreshLayout.OnRefreshListener, View.OnClickListener {

    private Context _context;
    private Intent intent;
    private View rootView;
    private LinearLayout defaultLayout;
    private LinearLayout watchzoneLayout, mobile_wz_sound_layout, mobile_wz_notification_layout;
    private TextView addWzPopup, heading, subHeading, radiusValue, notificationSound;
    private FloatingActionButton addWatchZoneBtn;
    RecyclerView _watchzoneList;
    SwipeRefreshLayout swipeRefreshLayout;
    Button staticBtn, mobileBtn;
    RelativeLayout staticLayout, mobileLayout;
    ViewSwitcher viewSwitcher;
    ScrollView scrollMobileView;
    SwitchCompat mobileWzSwitch;
    DiscreteSeekBar mobileRadiusSeek;
    String apiURL = null;
    String _watchzoneURL;
    String _deleteWzURL;
    String _stateWzURL; //enable/disable url
    RequestQueue queue;
    JsonObjectRequest volleyRequest;
    WzListAdapter _adapter;
    ArrayList<EditWatchZones> _watchzoneArray;
    ArrayList<EditWatchZones> arryMobileWZ;
    EditWatchZones _editWatchzone;
    EditWatchZones dictMobileWZ;
    DBController _dbController;
    List<String> _dbCategories;
    Snackbar snackbar = null;
    RingtonePickerDialog.Builder ringtonePickerBuilder;
    Uri _ringtoneURI = null;
    String _ringtoneName = null;
    int sliderValue = Constants.DEFAULT_VALUE_RADIUS;
    private static final int REQUEST_COARSE_LOCATION = 8;
    boolean isMobileWZValueChanged = false;
    boolean isBackButtonClicked = false;
    public GoogleApiClient mApiClient;
    private Menu mOptionsMenu;
    public ArrayList<Category> category_data = new ArrayList<Category>();
    public ArrayList<CategoryType> types_data = new ArrayList<CategoryType>();
    public ArrayList<CategoryStatus> statuses_data = new ArrayList<CategoryStatus>();
    public ArrayList<EventGroup> default_data = new ArrayList<EventGroup>();


    @Inject
    ViewModelProvider.Factory factory;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_watchzone_list;
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        obtainViewModel(factory, WatchZoneViewModel.class);

        setupViewPager();
        getBaseActivity().updateToolbarTitle(getString(R.string.wz_title));

        viewModel.proximityEnable.observe(this, enable -> binder.viewpager.setCurrentItem(enable ? 1 : 0));

//        initializeControl();
//        initializeVariables();
//        initializeListener();
//        initializeWzList();
    }

    private void setupViewPager() {
        AppViewPagerAdapter adapter = new AppViewPagerAdapter(getChildFragmentManager());
        adapter.addFrag(new StaticWatchZoneFragment(), getString(R.string.lbl_staticWZHeading));
        adapter.addFrag(new MobileWatchZoneFragment(), getString(R.string.lbl_mobileWZHeading));
        binder.viewpager.setAdapter(adapter);
        binder.viewpager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                updateOptionsMenu();
                if (position == 0) binder.floatingActionButton.show();
                else binder.floatingActionButton.hide();
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        inflater.inflate(R.menu.watchzone_main, menu);
        MenuItem cancelMenuItem = menu.getItem(0);

        VectorDrawableCompat vectorDrawableCompat = VectorDrawableCompat.create(getResources(), R.drawable.icon_queue, null);
        cancelMenuItem.setIcon(vectorDrawableCompat);

        super.onCreateOptionsMenu(menu, inflater);
        this.mOptionsMenu = menu;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.share:
                intent = new Intent(getBaseActivity(), ShareWatchZone.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void initializeVariables() {
        _dbController = DBController.getInstance(getContext());
        _dbCategories = new ArrayList<>();
        _dbCategories = _dbController.getCategoriesNames();
        _watchzoneArray = new ArrayList<>();

//        if(Utility.isProximityEnabled(_context)) {
//            setLocation();
//        }

        getMobileWZData();

        mobileWzSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                setMobileWzStatus();
                if (isChecked) {
                    Log.d("SWitch", "checked");
                    if (dictMobileWZ != null) {

                        disableProximity(true);
                    } else {
                        setMobileWZFilters();
                    }
                } else {
                    Log.d("SWitch", "unchecked");
                    if (dictMobileWZ != null) {
                        disableProximity(false);
                    }
                }
            }
        });
        _watchzoneURL = BuildConfig.API_ENDPOINT + "apiInfo/" + PreferenceUtils.getFromPrefs(_context, _context.getString(R.string.pref_user_id), "0") + "/watchzones";
        _deleteWzURL = BuildConfig.API_ENDPOINT + "apiInfo/" + PreferenceUtils.getFromPrefs(_context, _context.getString(R.string.pref_user_id), "0") + "/watchzones/";
        _stateWzURL = BuildConfig.API_ENDPOINT + "apiInfo/" + PreferenceUtils.getFromPrefs(_context, _context.getString(R.string.pref_user_id), "0") + "/watchzones/";
        ringtonePickerBuilder = new RingtonePickerDialog.Builder(_context, getActivity().getSupportFragmentManager());
        ringtonePickerBuilder.addRingtoneType(RingtonePickerDialog.Builder.TYPE_NOTIFICATION);
        ringtonePickerBuilder.setPlaySampleWhileSelection(checkVibrationIsOn(_context));
        ringtonePickerBuilder.setListener(new RingtonePickerListener() {
            @Override
            public void OnRingtoneSelected(String ringtoneName, Uri ringtoneUri) {
                _ringtoneName = ringtoneName;
                _ringtoneURI = ringtoneUri;
                Log.d("ringtone", ringtoneUri.toString());
                notificationSound.setText(ringtoneName);
            }
        });

        setMobileWzStatus();
        setMobileWZData();
    }


    public void setMobileWZData() {
        Ringtone ringtone;

        if (_ringtoneURI == null) {
            _ringtoneURI = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        }
        setupSliderValue();
        if (arryMobileWZ != null) {

            if (arryMobileWZ.size() > 0) {
                dictMobileWZ = arryMobileWZ.get(0);
//                mobileRadiusSeek.setProgress(Integer.parseInt(dictMobileWZ.getWatchzoneRadius()));
                sliderValue = mobileRadiusSeek.getProgress();
                setupSliderValue();
//                _ringtoneURI = Uri.parse(dictMobileWZ.getWatchzoneSound());

//            setMobileWzStatus();
            }
        } else {
            mobileRadiusSeek.setProgress(Constants.DEFAULT_VALUE_RADIUS);
            sliderValue = mobileRadiusSeek.getProgress();
            setupSliderValue();

        }

        //Get Notification Sound name
        ringtone = RingtoneManager.getRingtone(_context, _ringtoneURI);
        _ringtoneName = ringtone.getTitle(_context);
        if (_ringtoneName == "") {
            _ringtoneURI = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            ringtone = RingtoneManager.getRingtone(_context, _ringtoneURI);

            _ringtoneName = ringtone.getTitle(_context);
        }

        ringtonePickerBuilder.setCurrentRingtoneUri(_ringtoneURI);
        notificationSound.setText(_ringtoneName);
    }

    public void updateMobileWZDataOrNot() {
        getMobileWZData();

        if (dictMobileWZ != null) {

            if ((sliderValue != Integer.parseInt(dictMobileWZ.getRadius())) || (!_ringtoneURI.toString().equalsIgnoreCase(Uri.parse(dictMobileWZ.getSound()).toString()))) {
                isMobileWZValueChanged = true;
            } else {
                if (isBackButtonClicked == true) {
                    isMobileWZValueChanged = false;
                }
            }

            if (isMobileWZValueChanged == true) {

                if (isBackButtonClicked == true) {
                    updateMobileWZRadius();
                } else {
                    if (viewSwitcher.getCurrentView() == staticLayout) {
                        updateMobileWZRadius();
                    }
                }
            } else {
                if (isBackButtonClicked == true) {
                    backButtonClicked();
                }

            }

        }

    }

    public void backButtonClicked() {

    }

    public void updateMobileWZRadius() {


        if (Utility.isProximityEnabled(_context)) {

            //Attach filtes to dic
            ArrayList<String> defValues = new ArrayList<String>();


//            if (dictMobileWZ.getFilterGroupId() != null) {
//
//
//                for (Integer item : dictMobileWZ.getFilterGroupId()) {
//                    defValues.add(item.toString());
//                }
//
//
//            }
//            sendProximity(dictMobileWZ.getFilter(), defValues, true);
        } else {

            mobileRadiusSeek.setProgress(Integer.parseInt(dictMobileWZ.getRadius()));
            sliderValue = mobileRadiusSeek.getProgress();
            setupSliderValue();
            if (isBackButtonClicked == true) {

//                self.showAlertWithComplitionSingleOptionWithTitle(TR_MSG_UNABLETOSAVEMOBILEWZ,title: TR_MSG_ERROR ,handler: { (action:UIAlertAction) -> Void in
//                self.backTab()
                //  })


            } else {
                // self.showAlert(TR_MSG_UNABLETOSAVEMOBILEWZ, title: TR_MSG_ERROR)
            }


        }


    }

//    public void initializeControl() {
//        defaultLayout = (LinearLayout) rootView.findViewById(R.id.defaultLayout);
//        watchzoneLayout = (LinearLayout) rootView.findViewById(R.id.watchzoneLayout);
//        addWzPopup = (TextView) rootView.findViewById(R.id.addWzPopup);
//        heading = (TextView) rootView.findViewById(R.id.textHeading);
//        subHeading = (TextView) rootView.findViewById(R.id.textSubHeading);
//        notificationSound = (TextView) rootView.findViewById(R.id.mobile_wz_sound_text);
//        radiusValue = (TextView) rootView.findViewById(R.id.mobileRadius);
//        addWatchZoneBtn = (FloatingActionButton) rootView.findViewById(R.id.floatingActionButton);
//        swipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipeRefreshLayout);
//        _watchzoneList = (RecyclerView) rootView.findViewById(R.id.wz_list);
//        staticBtn = (Button) rootView.findViewById(R.id.staticBtn);
//        mobileBtn = (Button) rootView.findViewById(R.id.mobileBtn);
//        staticLayout = (RelativeLayout) rootView.findViewById(R.id.staticLayout);
//        mobileLayout = (RelativeLayout) rootView.findViewById(R.id.mobileLayout);
//        viewSwitcher = (ViewSwitcher) rootView.findViewById(R.id.viewSwitcherWz);
//        scrollMobileView = (ScrollView) rootView.findViewById(R.id.scrollMobileView);
//        mobileWzSwitch = (SwitchCompat) rootView.findViewById(R.id.mobileWzSwitch);
//        mobileRadiusSeek = (DiscreteSeekBar) rootView.findViewById(R.id.mobile_radius_seek);
//        mobile_wz_sound_layout = (LinearLayout) rootView.findViewById(R.id.mobile_wz_sound_layout);
//        mobile_wz_notification_layout = (LinearLayout) rootView.findViewById(R.id.mobile_wz_notification_layout);
//        defaultView();
//
//        hideShowWzPopup();
//
//    }

    public void initializeListener() {

        swipeRefreshLayout.setOnRefreshListener(this);
        addWatchZoneBtn.setOnClickListener(this);
        staticBtn.setOnClickListener(this);
        mobileBtn.setOnClickListener(this);
        mobileWzSwitch.setOnClickListener(this);
        mobile_wz_sound_layout.setOnClickListener(this);
        mobile_wz_notification_layout.setOnClickListener(this);

        mobileRadiusSeek.setOnProgressChangeListener(new DiscreteSeekBar.OnProgressChangeListener() {
            @Override
            public void onProgressChanged(DiscreteSeekBar seekBar, int radius, boolean fromUser) {
                sliderValue = radius;
                Log.d("Radius", String.valueOf(radius) + String.valueOf(sliderValue));
                setupSliderValue();
            }

            @Override
            public void onStartTrackingTouch(DiscreteSeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(DiscreteSeekBar seekBar) {

            }
        });
    }

    void setupSliderValue() {

        radiusValue.setText(getString(R.string.txtRadiusValue) + " " + sliderValue + getString(R.string.txtRadiusKM));
    }

    public void initializeWzList() {

        _watchzoneList.setLayoutManager(new LinearLayoutManager(_context));
        _watchzoneList.addItemDecoration(new DividerItemDecoration(_context, LinearLayoutManager.VERTICAL));


    }


    public void hideShowWzPopup() {
        final Handler handler = new Handler();
        addWzPopup.setVisibility(View.VISIBLE);
        new Thread(new Runnable() {
            public void run() {
                try {
                    Thread.sleep(3000);
                } catch (Exception e) {
                }
                handler.post(new Runnable() {
                    public void run() {
                        addWzPopup.setVisibility(View.GONE);
                    }
                });
            }
        }).start();
    }

    public void hideShowDefaultLayout(int visibility) {
        //0 visible
        //4 invisible
        //8 gone

        defaultLayout.setVisibility(visibility);
    }

    @Override
    public void onRefresh() {
        getWatchZones();
    }


    @Override
    public void onResume() {
        super.onResume();
        //   mRecyclerView.addOnItemTouchListener(onTouchListener);

//        if (!getUserVisibleHint()) {
//            return;
//        }
//        Activity activity = getActivity();
//        if (activity instanceof HomeActivity) {
//            HomeActivity home = (HomeActivity) activity;
//            home.updateToolbarTitle("Watch Zone");
//        }
//
//        if (viewSwitcher.getCurrentView() == staticLayout) {
//            getWatchZones();
//        } else {
//            getMobileWZData();
//        }
    }

    public void getMobileWZData() {

        arryMobileWZ = new ArrayList<EditWatchZones>();

        if (PreferenceUtils.hasKey(_context, Constants.KEY_VALUE_PROXIMITY_DATA)) {

            Gson gson = new Gson();

            String mobileWZValues = (String) PreferenceUtils.getFromPrefs(_context, Constants.KEY_VALUE_PROXIMITY_DATA, "");

            EditWatchZones[] arryMobileWZmodel = gson.fromJson(mobileWZValues, EditWatchZones[].class);

            List<EditWatchZones> items = Arrays.asList(arryMobileWZmodel);
            arryMobileWZ = new ArrayList<EditWatchZones>(items);

        }


        Log.d("sdfsd", arryMobileWZ.toString());

        if (arryMobileWZ != null) {

            if (arryMobileWZ.size() > 0) {
                dictMobileWZ = arryMobileWZ.get(0);
            }
        }

    }

    public void EditMode(Boolean wzData, int position) {
        if (wzData) {

            if (PreferenceUtils.hasKey(_context, "wzLocation"))
                PreferenceUtils.removeFromPrefs(_context, "wzLocation");

            intent = new Intent(_context.getApplicationContext(), EditWatchZone.class);
            intent.putExtra("position", position);
            intent.putExtra("edit", true);
            startActivity(intent);
        }
    }

    public void showSnack(String message) {

        try {
            if (rootView != null) {
                snackbar = Snackbar.make(rootView, message, Snackbar.LENGTH_INDEFINITE);
                View view = snackbar.getView();
                FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) view.getLayoutParams();
                params.gravity = Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL;
                view.setLayoutParams(params);
                snackbar.show();
            }
        } catch (Exception e) {
            Log.d("ExCeption", e.getMessage().toString());
        }


    }

    public void dismisSnackbar() {
        Thread t = new Thread() {
            public void run() {
                try {
                    sleep(3000);
                } catch (InterruptedException ie) {
                    ie.printStackTrace();
                } finally {
                    if (snackbar != null) {
                        if (snackbar.isShown())
                            snackbar.dismiss();
                    }

                }
            }
        };
        t.start();

    }

    public void changeText(String message) {

        if (snackbar != null) {
            if (snackbar.isShown())
                snackbar.setText(message);
        }

    }


    public void watchzoneShareCode(final String shareCode) {
        final Dialog dialog = new Dialog(getActivity());
        dialog.setCancelable(false);
        View view = getActivity().getLayoutInflater().inflate(R.layout.custom_share_wz_dialog, null);
        dialog.setContentView(view);
        if (dialog != null) {
            Window window = getActivity().getWindow();
            Rect displayRectangle = new Rect();
            window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);
            dialog.getWindow().setLayout((int) (displayRectangle.width() * 0.9f), (int) (displayRectangle.height() * 0.6f));
        }

        TextView tv = (TextView) view.findViewById(R.id.watch_zone_share_code);
        ImageButton copy_btn = (ImageButton) view.findViewById(R.id.copy_to_clipboard);
        Button ok_btn = (Button) view.findViewById(R.id.accept);
        tv.setText(shareCode);

        ok_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dialog.dismiss();
            }
        });
        copy_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Do something
                DeviceUtil.copyToClipBoard(_context, shareCode);
                dialog.dismiss();
            }
        });


        if (shareCode != null) {
            dialog.show();
        }
    }

    public void callDeleteWz(String _watchzoneId, final String _name, final int position) {

        if (!Utility.isInternetConnected(_context)) {
            Toast.makeText(_context, getString(R.string.noInternet), Toast.LENGTH_LONG).show();
            return;
        }


        showSnack(getString(R.string.msg_deletingWZ) + " " + _name);


        queue = RequestHandler.getInstance(_context.getApplicationContext()).getRequestQueue(); //Obtain the instance
        volleyRequest = new JsonObjectRequest(Request.Method.DELETE, _deleteWzURL + "" + _watchzoneId,
                null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if (response != null) {
                    try {
                        if (response.getBoolean("success")) {
                            changeText(getString(R.string.msg_WZ) + " " + _name + " " + getString(R.string.msg_deleted));
                            dismisSnackbar();
                            // _adapter.remove(position); // myDataset is List<MyObject>
                            _adapter.notifyItemRemoved(position);

                            onResume();
                        } else {
                            changeText(getString(R.string.msg_unableDeleteWZ));

                            dismisSnackbar();

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    changeText(getString(R.string.msg_unableDeleteWZ));

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
             */
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json");
                headers.put("Authorization", "Bearer " + PreferenceUtils.getFromPrefs(_context, getString(R.string.pref_user_token), ""));
                return headers;
            }
        };

        volleyRequest.setRetryPolicy(new DefaultRetryPolicy(5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(volleyRequest);
    }

    public void callEnableDisableWz(String _watchzoneId, final boolean state, final String _name) {

        if (!Utility.isInternetConnected(_context)) {
            Toast.makeText(_context, getString(R.string.noInternet), Toast.LENGTH_LONG).show();
            return;
        }

        if (state)
            showSnack(getString(R.string.msg_enableWZ) + " " + _name);
        else
            showSnack(getString(R.string.msg_disableWZ) + " " + _name);

        String url = null;
        if (state)
            url = _stateWzURL + "" + _watchzoneId + "/" + "enable";
        else
            url = _stateWzURL + "" + _watchzoneId + "/" + "disable";

        queue = RequestHandler.getInstance(_context.getApplicationContext()).getRequestQueue(); //Obtain the instance
        volleyRequest = new JsonObjectRequest(Request.Method.PUT, url,
                null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if (response != null) {
                    try {
                        if (response.getBoolean("success")) {
                            changeText(_name + " " + getString(R.string.msg_updatedWZ));

                        } else {
                            changeText(getString(R.string.msg_unableUpdateWZ));
                        }
                        dismisSnackbar();
                        onResume();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    changeText(getString(R.string.msg_unableUpdateWZ));
                    dismisSnackbar();
                    onResume();
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
             */
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json");
                headers.put("Authorization", "Bearer " + PreferenceUtils.getFromPrefs(_context, getString(R.string.pref_user_token), ""));
                return headers;
            }
        };

        volleyRequest.setRetryPolicy(new DefaultRetryPolicy(5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(volleyRequest);
    }

    private void purifyWz(JSONObject response, boolean mobileWZ) {


        if (mobileWZ) {
            arryMobileWZ = new ArrayList<EditWatchZones>();
        } else {
            _watchzoneArray = new ArrayList<EditWatchZones>();
        }


        try {
            JSONArray wz = response.getJSONArray("watchzones");
            if (wz.length() > 0) {

                for (int i = 0; i < wz.length(); i++) {
                    JSONObject data = (JSONObject) wz.get(i);


                    EditWatchZones editModel = new EditWatchZones();
                    WatchZoneGeom geomModel = new WatchZoneGeom();

//                    editModel.setId(data.getString("id"));
                    editModel.setName(data.getString("name"));
                    editModel.setDeviceId(data.getString("deviceId"));
                    editModel.setAddress(data.getString("address"));
                    editModel.setRadius(data.getString("radius"));
                    editModel.setWzType(data.getString("type"));
                    editModel.setProximity(Boolean.valueOf(data.getString("proximity")));
                    editModel.setNoEdit(Boolean.valueOf(data.getString("noEdit")));


                    //=== GROUP ID's FILTER


                    List<Integer> wzFilterGroup = new ArrayList<>();
                    JSONArray filterGroup = new JSONArray(data.get("filterGroupId").toString());

                    for (int j = 0; j < filterGroup.length(); j++) {
                        wzFilterGroup.add(Integer.parseInt(filterGroup.get(j).toString()));
                    }

//                    editModel.setFilterGroupId(wzFilterGroup);
                    //=======///


                    //==== FILTER

                    List<String> statusCodes;
                    CategoryTypeFilter categoryTypeFilter = new CategoryTypeFilter();
                    CategoryFilter categoryFilter = new CategoryFilter();

                    ArrayList<CategoryTypeFilter> filterData = new ArrayList<CategoryTypeFilter>();

                    ArrayList<HashMap<String, CategoryFilter>> filterDetails = new ArrayList<HashMap<String, CategoryFilter>>();

                    JSONObject filterObj = new JSONObject(data.get("filter").toString());

                    for (int j = 0; j < _dbCategories.size(); j++) {
                        for (int f = 0; f < filterObj.length(); f++) {

                            if (filterObj.has(_dbCategories.get(j))) {
                                JSONObject categoryObj = filterObj.getJSONObject(_dbCategories.get(j));
                                JSONArray catTypesArr = categoryObj.getJSONArray("types");

                                HashMap<String, CategoryFilter> hash = new HashMap<String, CategoryFilter>();
                                filterData = new ArrayList<CategoryTypeFilter>();
                                categoryFilter = new CategoryFilter();

                                for (int c = 0; c < catTypesArr.length(); c++) {
                                    JSONObject typeObj = catTypesArr.getJSONObject(c);
                                    JSONArray statusArr = typeObj.getJSONArray("status");
                                    statusCodes = new ArrayList<>();
                                    for (int p = 0; p < statusArr.length(); p++) {

                                        statusCodes.add(statusArr.get(p).toString());
                                    }
                                    categoryTypeFilter = new CategoryTypeFilter();
                                    categoryTypeFilter.setCode(typeObj.get("code").toString());
                                    categoryTypeFilter.setStatus(statusCodes);
                                    filterData.add(categoryTypeFilter);

                                }
                                categoryFilter.setTypes(filterData);

                                hash.put(_dbCategories.get(j), categoryFilter);
                                filterDetails.add(hash);
                                filterObj.remove(_dbCategories.get(j));
                            }


                        }
                    }

//                    editModel.setFilter(filterDetails);

                    //=====//

                    editModel.setDefault(Boolean.valueOf(data.getString("isDefaultFilter")));

                    editModel.setSound(data.getString("sound"));
                    editModel.setEnable(Boolean.valueOf(data.getString("enable")));
                    editModel.setShareCode(data.getString("shareCode"));


                    JSONArray geo = new JSONArray();
                    geo.put(data.get("geometry"));
                    JSONObject g = geo.getJSONObject(0);

                    JSONArray geoArr = new JSONArray(g.get("coordinates").toString());
                    ArrayList<HashMap<String, Double>> cordinates = new ArrayList<HashMap<String, Double>>();
                    if (g.get("type").toString().equals("Point")) {


                        HashMap<String, Double> hashLoc = new HashMap<>();
                        hashLoc.put("latitude", Double.parseDouble(geoArr.get(0).toString()));
                        hashLoc.put("longitude", Double.parseDouble(geoArr.get(1).toString()));
                        cordinates.add(hashLoc);


                    } else {
                        JSONArray geoArrIn = geoArr.getJSONArray(0);
                        for (int k = 0; k < geoArrIn.length(); k++) {

                            JSONArray geoA = geoArrIn.getJSONArray(k);
                            HashMap<String, Double> hashLoc = new HashMap<>();
                            hashLoc.put("latitude", Double.parseDouble(geoA.get(0).toString()));
                            hashLoc.put("longitude", Double.parseDouble(geoA.get(1).toString()));
                            cordinates.add(hashLoc);
                        }
                    }
                    geomModel.setCordinate(cordinates);
                    geomModel.setType(g.get("type").toString());

//                    editModel.setWatchZoneGeoms(geomModel);


                    if (mobileWZ) {

                        arryMobileWZ.add(editModel);

                        Gson gson = new Gson();


                        PreferenceUtils.saveToPrefs(_context, Constants.KEY_VALUE_PROXIMITY_DATA, gson.toJson(arryMobileWZ));


                    } else {
                        if (!Boolean.valueOf(data.getString("proximity")) && !mobileWZ) {
                            _watchzoneArray.add(editModel);
                        }


                    }


                }


                if (mobileWZ) {
                    changeText(getString(R.string.msg_savedMobileWZ));
                    dictMobileWZ = arryMobileWZ.get(0);
                    //getMobileWZData();

                } else {
                    changeText(getString(R.string.msg_fechtedWZ));
//                    _editWatchzone.setEditWz(_watchzoneArray);
                    saveWZToDatabase();
                    Collections.reverse(_watchzoneArray);

                    //      hideShowWzPopup();
                }


            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (mobileWZ) {


        } else {
            swipeRefreshLayout.setRefreshing(false);
        }


    }


    public void saveWZToDatabase() {
        _dbController.deleteWZ();

        ArrayList<HashMap> watchzones = new ArrayList<HashMap>();
        for (int i = 0; i < _watchzoneArray.size(); i++) {
            EditWatchZones wz = (EditWatchZones) _watchzoneArray.get(i);

            HashMap<String, String> values = new HashMap<String, String>();


//            values.put(DBController.KEY_REF_WZ_ID, wz.getId());
            values.put(DBController.KEY_REF_WZ_DEVICE_ID, wz.getDeviceId());
            values.put(DBController.KEY_REF_WZ_SOUND, wz.getSound());
            values.put(DBController.KEY_REF_WZ_ADDRESS, wz.getAddress());
            values.put(DBController.KEY_REF_WZ_NAME, wz.getName());
            values.put(DBController.KEY_REF_WZ_RADIUS, wz.getRadius());
            values.put(DBController.KEY_REF_WZ_TYPE, wz.getWzType());

            String wzFilter = "", wzFilterGroupID = "";

            Gson gson = new Gson();

//            if (wz.getFilter() != null)
//                wzFilter = gson.toJson(wz.getFilter());
//
//            if (wz.getFilterGroupId() != null)
//                wzFilterGroupID = gson.toJson(wz.getFilterGroupId());

            values.put(DBController.KEY_REF_WZ_FILTER, wzFilter);
            values.put(DBController.KEY_REF_WZ_FILTERGROUPID, wzFilterGroupID);
            values.put(DBController.KEY_REF_WZ_ENABLE, String.valueOf(wz.isEnable()));
//            values.put(DBController.KEY_REF_WZ_PROXIMITY, String.valueOf(wz.getWatchzoneProximity()));
//            values.put(DBController.KEY_REF_WZ_ISDEFAULT, String.valueOf(wz.getWatchzoneProximity()));
//            values.put(DBController.KEY_REF_WZ_NOEDIT, String.valueOf(wz.getWatchzoneProximity()));
            values.put(DBController.KEY_REF_WZ_SHARECODE, wz.getShareCode());
//            values.put(DBController.KEY_REF_WZ_GEOMS, gson.toJson(wz.getWatchZoneGeoms()));

            watchzones.add(values);
        }

        _dbController.add_WzData(watchzones);
    }

    public void getWZFromDB() {
        _watchzoneArray = _dbController.getAllWZ();
//        _editWatchzone.setEditWz(_watchzoneArray);

//        Log.d("EditWZ", _editWatchzone.getEditWz().toString());
        Collections.reverse(_watchzoneArray);

        setUpdateForWatchzones();


    }

    public void setUpdateForWatchzones() {
        if (_watchzoneArray != null) {
            if (_watchzoneArray.size() > 0) {
                _adapter = new WzListAdapter(_context, _watchzoneArray, WatchZoneFragment.this);

                _watchzoneList.setVisibility(View.VISIBLE);
                _watchzoneList.setAdapter(_adapter);
                _adapter.setMode(Attributes.Mode.Single);
                _adapter.notifyDataSetChanged();
                _adapter.setMode(Attributes.Mode.Single);
                hideShowDefaultLayout(8); //8 gone, 4 invisible

            } else {
                hideShowDefaultLayout(0);
            }


        } else {
            hideShowDefaultLayout(0);
        }

    }

    public void getWatchZones() {


        if (!Utility.isInternetConnected(_context)) {
            Toast.makeText(_context, getString(R.string.noInternet), Toast.LENGTH_LONG).show();
            swipeRefreshLayout.setRefreshing(false);
            getWZFromDB();

            return;
        }

        showSnack(getString(R.string.msg_fetchWZ));
        queue = RequestHandler.getInstance(_context.getApplicationContext()).getRequestQueue(); //Obtain the instance

        volleyRequest = new JsonObjectRequest(Request.Method.GET, _watchzoneURL,
                null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if (response != null) {
                    try {
                        if (response.getBoolean("success")) {

                            purifyWz(response, false);
                            setUpdateForWatchzones();
                            dismisSnackbar();
                        } else {
                            changeText(getString(R.string.msg_unableFetchWZ));
                            dismisSnackbar();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else {
                    changeText(getString(R.string.msg_unableFetchWZ));
                    dismisSnackbar();
                }
                swipeRefreshLayout.setRefreshing(false);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                swipeRefreshLayout.setRefreshing(false);

                changeText(getString(R.string.timeOut));
                error.printStackTrace();

                dismisSnackbar();


            }
        });


        volleyRequest.setRetryPolicy(new DefaultRetryPolicy(5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(volleyRequest);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.floatingActionButton:
                if (PreferenceUtils.hasKey(_context, "wzLocation"))
                    PreferenceUtils.removeFromPrefs(_context, "wzLocation");

                intent = new Intent(_context, AddStaticZone.class);
                startActivity(intent);
                break;

//            case R.id.staticBtn:
//
//                defaultView();
//                isBackButtonClicked = false;
//                isMobileWZValueChanged = false;
//                updateMobileWZDataOrNot();
//                Log.d("viewDAta", "Staticview");
//                updateOptionsMenu();
//                break;
//
//            case R.id.mobileBtn:
//                mobileView();
//                isBackButtonClicked = false;
//                isMobileWZValueChanged = false;
//                updateMobileWZDataOrNot();
//                updateOptionsMenu();
//                Log.d("viewDAta", "mobileView");
//                break;
//            case R.id.mobileWzSwitch:
////                setMobileWzStatus();
//                break;
//            case R.id.mobile_wz_sound_layout:
//                ringtonePickerBuilder.setPlaySampleWhileSelection(checkVibrationIsOn(_context));
//
//                ringtonePickerBuilder.show();
//                break;
//            case R.id.mobile_wz_notification_layout:
//                intent = new Intent(_context, AddStaticZoneNotification.class);
//                intent.putExtra("position", 0);
//                intent.putExtra("edit", true);
//                intent.putExtra("mobile", true);
//                intent.putExtra("mobileRadius", sliderValue);
//                intent.putExtra("mobileSound", _ringtoneURI.toString());
//                if (dictMobileWZ == null) {
//                    return;
//                }
//                Bundle b = new Bundle();
//                b.putSerializable("mobileWZ", (Serializable) dictMobileWZ);
//                intent.putExtras(b); //pass bundle to your intent
//                startActivityForResult(intent, REQUEST_NOTIFICATION);
//                break;
        }
    }

    private void updateOptionsMenu() {
        if (this.mOptionsMenu != null) {
            onPrepareOptionsMenu(this.mOptionsMenu);
        }
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
//        if (menu.findItem(R.id.share) != null)
//            if (viewSwitcher.getCurrentView() == staticLayout) {
//                menu.findItem(R.id.share).setVisible(true);
//
//            } else {
//                menu.findItem(R.id.share).setVisible(false);
//
//            }
    }

    public void mobileView() {
        if (viewSwitcher.getCurrentView() != mobileLayout) {

            heading.setText(getString(R.string.lbl_mobileWZHeading));
            subHeading.setText(getString(R.string.lbl_mobileWZSubHeading));
            mobileBtn.setBackgroundResource(R.drawable.button_red_bottom_border);
            staticBtn.setBackgroundResource(R.drawable.border_shadow);
            ;

            viewSwitcher.showNext();
        }
    }

    public void defaultView() {
        if (viewSwitcher.getCurrentView() != staticLayout) {
            heading.setText(getString(R.string.lbl_staticWZHeading));
            subHeading.setText(getString(R.string.lbl_staticWZSubHeading));
            staticBtn.setBackgroundResource(R.drawable.button_red_bottom_border);
            mobileBtn.setBackgroundResource(R.drawable.border_shadow);

            viewSwitcher.showPrevious();
        }
    }

    public void setMobileWzStatus() {

        if (mobileWzSwitch.isChecked()) {
            scrollMobileView.setVisibility(View.VISIBLE);
        } else {
            scrollMobileView.setVisibility(View.INVISIBLE);
        }


    }


    public static boolean checkVibrationIsOn(Context context) {
        boolean status = false;
        AudioManager am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        switch (am.getRingerMode()) {
            case AudioManager.RINGER_MODE_NORMAL:
                status = true;
                break;
            case AudioManager.RINGER_MODE_SILENT:
                status = false;
                break;
            case AudioManager.RINGER_MODE_VIBRATE:
                status = false;
                break;
        }

        return status;
    }


    private void setLocation() {


        LocationManager locationManager = (LocationManager) _context.getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        // Get the name of the best provider
        String provider = locationManager.getBestProvider(criteria, true);
        if (ActivityCompat.checkSelfPermission(_context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(_context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_COARSE_LOCATION);
            return;
        }
        Location lastKnownLocation = locationManager.getLastKnownLocation(provider);
        if (lastKnownLocation != null) {
            Log.d("LocationData", String.valueOf(lastKnownLocation.getLatitude()));

            PreferenceUtils.saveToPrefs(_context, Constants.KEY_USERLATITUDE, String.valueOf(lastKnownLocation.getLatitude()));
            PreferenceUtils.saveToPrefs(_context, Constants.KEY_USERLONGITUDE, String.valueOf(lastKnownLocation.getLongitude()));

        }
    }

    private void setMobileWZFilters() {

        ArrayList<HashMap> defaultDataWz = _dbController.getDefaultDataWz();
        default_data = new ArrayList<EventGroup>();

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


        //Attch filtes to dic
        ArrayList<String> defValues = new ArrayList<String>();
        for (int i = 0; i < default_data.size(); i++) {
            EventGroup ev = new EventGroup();
            ev = default_data.get(i);
            if (ev.isFilterOn()) {
                defValues.add(String.valueOf(ev.getId()));
            }
        }

        Log.d("mobileWZdefaultFilter", defValues.toString());
        // mParams.put("filterGroupId", defValues);
        //  mParams.put("filter", new ArrayList<String>());

        sendProximity(new ArrayList<HashMap<String, CategoryFilter>>(), defValues, mobileWzSwitch.isChecked());

    }

    public void sendProximity(ArrayList<HashMap<String, CategoryFilter>> filter, ArrayList<String> filterId, final boolean enable) {


        if (!Utility.isInternetConnected(_context)) {
            Toast.makeText(_context, getString(R.string.noInternet), Toast.LENGTH_LONG).show();
            return;
        }

        showSnack(getString(R.string.msg_makingChangeTtoMobileWZ));

        Log.d("location", PreferenceUtils.getFromPrefs(_context, Constants.KEY_USERLATITUDE, " ").toString());


        HashMap<String, Object> mParams = new HashMap<String, Object>();

        Gson gson = new Gson();
        mParams.put("radius", sliderValue);
        mParams.put("sound", _ringtoneURI.toString());
        mParams.put("enable", enable);
        mParams.put("latitude", (String) PreferenceUtils.getFromPrefs(_context, Constants.KEY_USERLATITUDE, " "));
        mParams.put("longitude", (String) PreferenceUtils.getFromPrefs(_context, Constants.KEY_USERLONGITUDE, " "));
        mParams.put("filterGroupId", filterId);

        LinkedTreeMap<String, Object> categoryFilters = new LinkedTreeMap<>();

        try {

            for (int d = 0; d < _dbCategories.size(); d++) {
                for (int j = 0; j < filter.size(); j++) {
                    HashMap<String, CategoryFilter> tempData = filter.get(j);

                    if (tempData.keySet().toArray()[0].equals(_dbCategories.get(d).toString())) {

                        JSONObject category = new JSONObject();
                        JSONArray typeArray = new JSONArray();
                        JSONObject catTypeObj = new JSONObject();
                        CategoryFilter catFilter = tempData.get(_dbCategories.get(d).toString());
                        List<CategoryTypeFilter> ct = catFilter.getTypes();


                        for (int x = 0; x < ct.size(); x++) {
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
                        categoryFilters.put(_dbCategories.get(d).toString(), category);

                        filter.remove(tempData);
                    }
                }

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        mParams.put("filter", categoryFilters);

//
//            if(filter.size() >0)
//            {
//                mParams.put("filter", filter.get(0));
//            }
//            else {
//                mParams.put("filter",new HashMap < String, CategoryFilter> ());
//            }

        mParams.put("speed", "0.0 km/hr");
        mParams.put("movement", "Not Moving");

        Log.d("proximityDict", mParams.toString());

        queue = RequestHandler.getInstance(_context.getApplicationContext()).getRequestQueue(); //Obtain the instance
        volleyRequest = new JsonObjectRequest(Request.Method.POST, apiURL, new JSONObject(mParams),
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {

                        if (response != null) {
                            try {
                                if (response.getBoolean("success")) {

                                    Log.e("EA", response.toString());
                                    purifyWz(response, true);
                                    PreferenceUtils.saveToPrefs(_context, Constants.KEY_VALUE_ENABLEPROXI, enable);
                                    changeText(getString(R.string.msg_savedMobileWZ));

                                } else {
                                    changeText(getString(R.string.msg_failedToSavedMobileWZ));

                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        } else {
                            changeText(getString(R.string.msg_failedToSavedMobileWZ));

                        }
                        dismisSnackbar();
                        getMobileWZData();
                        setMobileWzStatus();
                        setMobileWZData();

                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {

                swipeRefreshLayout.setRefreshing(false);
                getMobileWZData();
                setMobileWzStatus();
                setMobileWZData();
                changeText(getString(R.string.timeOut));
                error.printStackTrace();

                dismisSnackbar();

            }
        }) {
            /**
             * Passing some request headers
             */
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json");
                headers.put("Authorization", "Bearer " + PreferenceUtils.getFromPrefs(_context, getString(R.string.pref_user_token), ""));
                Log.e("Token", "Bearer " + PreferenceUtils.getFromPrefs(_context, getString(R.string.pref_user_token), ""));
                return headers;
            }

        };

        volleyRequest.setRetryPolicy(new DefaultRetryPolicy(5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(volleyRequest);


    }


    public void disableProximity(final boolean enable) {


        if (!Utility.isInternetConnected(_context)) {
            Toast.makeText(_context, getString(R.string.noInternet), Toast.LENGTH_LONG).show();
            return;
        }

        String strURL = "";
        if (enable) {
            strURL = BuildConfig.API_ENDPOINT + "apiInfo/" + PreferenceUtils.getFromPrefs(_context, getString(R.string.pref_user_id), "") + "/" + "watchzones/proximity/enable";
        } else {
            strURL = BuildConfig.API_ENDPOINT + "apiInfo/" + PreferenceUtils.getFromPrefs(_context, getString(R.string.pref_user_id), "") + "/" + "watchzones/proximity/disable";
        }


        showSnack(getString(R.string.msg_makingChangeTtoMobileWZ));
        Log.d("location", PreferenceUtils.getFromPrefs(_context, Constants.KEY_USERLATITUDE, " ").toString());


        HashMap<String, Object> mParams = new HashMap<String, Object>();

        Gson gson = new Gson();
        Log.d("url", strURL);
        queue = RequestHandler.getInstance(_context.getApplicationContext()).getRequestQueue(); //Obtain the instance
        volleyRequest = new JsonObjectRequest(Request.Method.PUT, strURL, new JSONObject(mParams),
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {

                        if (response != null) {
                            try {
                                if (response.getBoolean("success")) {

                                    Log.e("EA", response.toString());
                                    changeText(getString(R.string.msg_savedMobileWZ));
                                    purifyWz(response, true);
                                    PreferenceUtils.saveToPrefs(_context, Constants.KEY_VALUE_ENABLEPROXI, enable);
                                    dismisSnackbar();
                                    if (!enable) {
                                        // stopTracking();

                                    } else {
                                        // startTracking();
                                    }

                                } else {
                                    changeText(getString(R.string.msg_failedToSavedMobileWZ));
                                    // mobileWzSwitch.setChecked(!enable);
                                    getMobileWZData();
                                    setMobileWzStatus();
                                    setMobileWZData();


                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        } else {
                            changeText(getString(R.string.msg_failedToSavedMobileWZ));
                            //mobileWzSwitch.setChecked(!enable);
                            getMobileWZData();
                            setMobileWzStatus();
                            setMobileWZData();
                        }
                        dismisSnackbar();

                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {

                getMobileWZData();
                setMobileWzStatus();
                setMobileWZData();
                changeText(getString(R.string.timeOut));
                error.printStackTrace();

                dismisSnackbar();


            }
        }) {
            /**
             * Passing some request headers
             */
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json");
                headers.put("Authorization", "Bearer " + PreferenceUtils.getFromPrefs(_context, getString(R.string.pref_user_token), ""));
                return headers;
            }

        };

        volleyRequest.setRetryPolicy(new DefaultRetryPolicy(5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(volleyRequest);


    }

    public void proximityLocationCheckin() {

        if (!Utility.isInternetConnected(_context)) {
            //Toast.makeText(_context, getString(R.string.noInternet), Toast.LENGTH_LONG).show();
            return;
        }

        final AppPreferences appPreferences = new AppPreferences(_context);
        String apiURL = BuildConfig.API_ENDPOINT + "apiInfo/" + PreferenceUtils.getFromPrefs(_context, getString(R.string.pref_user_id), "") + "/" + "watchzones/proximity/location";
        Log.d("location", PreferenceUtils.getFromPrefs(_context, Constants.KEY_USERLATITUDE, " ").toString());


        HashMap<String, Object> mParams = new HashMap<String, Object>();

        Gson gson = new Gson();
        //mParams.put("accuracy","0");
        mParams.put("speed", "0.0 km/hr");
        mParams.put("movement", "Not Moving");
        mParams.put("latitude", Double.valueOf((String) PreferenceUtils.getFromPrefs(_context, Constants.KEY_USERLATITUDE, " ")));
        mParams.put("longitude", Double.valueOf((String) PreferenceUtils.getFromPrefs(_context, Constants.KEY_USERLONGITUDE, " ")));
        Log.d("proximityDictlocation", mParams.toString());


        queue = RequestHandler.getInstance(_context.getApplicationContext()).getRequestQueue(); //Obtain the instance
        volleyRequest = new JsonObjectRequest(Request.Method.PUT, apiURL, new JSONObject(mParams),
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {

                        if (response != null) {
                            try {
                                if (response.getBoolean("success")) {


                                    Log.e("EA", response.toString());
                                    PreferenceUtils.saveToPrefs(_context, Constants.KEY_LASTUPDATEDUSERLATITUDE, String.valueOf(Double.valueOf((String) PreferenceUtils.getFromPrefs(_context, Constants.KEY_USERLATITUDE, "0"))));
                                    PreferenceUtils.saveToPrefs(_context, Constants.KEY_LASTUPDATEDUSERLONGITUDE, String.valueOf(Double.valueOf((String) PreferenceUtils.getFromPrefs(_context, Constants.KEY_USERLONGITUDE, "0"))));
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

                //Toast.makeText(_context, getString(R.string.timeOut), Toast.LENGTH_LONG).show();


            }
        }) {
            /**
             * Passing some request headers
             */
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json");
                headers.put("Authorization", "Bearer " + PreferenceUtils.getFromPrefs(_context, getString(R.string.pref_user_token), ""));
                return headers;
            }

        };


        volleyRequest.setRetryPolicy(new DefaultRetryPolicy(5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(volleyRequest);


    }

    private void startTracking() {
        Intent intent1 = new Intent(_context, BackgroundDetectedActivitiesService.class);
        Log.d("BackgroundDetected", "started.......................");
        _context.startService(intent1);
    }

    private void stopTracking() {
        Intent intent = new Intent(_context, BackgroundDetectedActivitiesService.class);
        Log.d("BackgroundDetected", "stopped.......................");
        _context.stopService(intent);
    }
}
