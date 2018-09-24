package com.redhelmet.alert2me.ui.activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.RelativeLayout;
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
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import com.redhelmet.alert2me.BuildConfig;
import com.redhelmet.alert2me.R;
import com.redhelmet.alert2me.adapters.CustomNotificationCategoryAdapter;
import com.redhelmet.alert2me.adapters.DefaultNotificationAdapter;
import com.redhelmet.alert2me.core.Constants;
import com.redhelmet.alert2me.data.local.database.DBController;
import com.redhelmet.alert2me.data.model.Category;
import com.redhelmet.alert2me.data.model.CategoryFilter;
import com.redhelmet.alert2me.data.model.CategoryStatus;
import com.redhelmet.alert2me.data.model.CategoryType;
import com.redhelmet.alert2me.data.model.CategoryTypeFilter;
import com.redhelmet.alert2me.data.model.EditWatchZones;
import com.redhelmet.alert2me.data.model.EventGroup;
import com.redhelmet.alert2me.data.model.WatchZoneGeom;
import com.redhelmet.alert2me.domain.util.PreferenceUtils;
import com.redhelmet.alert2me.domain.util.Utility;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddStaticZoneNotification extends AppCompatActivity implements View.OnClickListener {

    Toolbar toolbar;
    Intent i;
    ExpandableListView exDefault;
    private static DefaultNotificationAdapter adapter;
    private CustomNotificationCategoryAdapter mAdapter;
    RelativeLayout defaultView, customView, addWz_layout;
    ViewSwitcher notificationView;
    ListView exCustom;
    Button defaultBtn, customBtn;
    DBController dbController;
    public ArrayList <Category> category_data = new ArrayList < Category > ();
    public ArrayList <CategoryType> types_data = new ArrayList < CategoryType > ();
    public ArrayList <CategoryStatus> statuses_data = new ArrayList < CategoryStatus > ();
    public ArrayList <EventGroup> default_data = new ArrayList < EventGroup > ();

    Category cat;
    EventGroup defaultGroup;
    public String apiURL,mobilewzApiURL;
    public boolean editMode = false;
    EditWatchZones editWatchZones = null;
    EditWatchZones editMobileWatchZone = null;
    ArrayList < EditWatchZones > wzData;
    public int position = 0;
    public boolean mobile_wz = false;
    String _ringtoneURI = "";
    int sliderValue = 0;
    List<String> _dbCategories;
    TextView txtHeader, txtSubHeader;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_static_wz_notification);
        dbController = new DBController(getApplicationContext());
        _dbCategories=new ArrayList<>();
        _dbCategories=dbController.getCategoriesNames();
        apiURL = BuildConfig.API_ENDPOINT + "device/" + PreferenceUtils.getFromPrefs(getApplicationContext(), getString(R.string.pref_user_id), "") + "/" + "watchzones";
        mobilewzApiURL = BuildConfig.API_ENDPOINT + "device/" + PreferenceUtils.getFromPrefs(getApplicationContext(), getString(R.string.pref_user_id), "") + "/" + "watchzones/proximity";
        getWZData();

        Log.e("ONCreated", "activity created.....");
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
            if(mobile_wz)
                supportActionBar.setTitle(getString(R.string.lbl_mobileWZ));
                else
            supportActionBar.setTitle(getString(R.string.lbl_addStaticWZ));
        }
    }

    public void getWZData() {
        //edit mode
        Bundle extras = getIntent().getExtras();
        if (extras != null) {

            //edit mode
            mobile_wz = extras.getBoolean("mobile");
            position = extras.getInt("position");
            editMode = extras.getBoolean("edit");
            sliderValue = extras.getInt("mobileRadius");
            _ringtoneURI = extras.getString("mobileSound");
            if(mobile_wz) {

                editMobileWatchZone = (EditWatchZones) extras.getSerializable("mobileWZ");
                extras.remove("mobileWZ");
                if (editMobileWatchZone != null)
                    Log.d("mobileWZ",editMobileWatchZone.toString());
            }
            else {
                editWatchZones =  EditWatchZones.getInstance();
                wzData = editWatchZones.getEditWz();
            }
        }
    }

    public void initializeControls() {

        cat = Category.getInstance();
        defaultGroup = EventGroup.getInstance();

        defaultBtn = (Button) findViewById(R.id.defaultBtn);
        customBtn = (Button) findViewById(R.id.customBtn);
        notificationView = (ViewSwitcher) findViewById(R.id.notification_switcher);
        customView = (RelativeLayout) findViewById(R.id.custom_view);
        defaultView = (RelativeLayout) findViewById(R.id.default_view);
        addWz_layout=(RelativeLayout)findViewById(R.id.addWz_layout);
        defaultBtn.setOnClickListener(this);
        customBtn.setOnClickListener(this);
        txtHeader = (TextView) findViewById(R.id.staticHeader);
        txtSubHeader = (TextView) findViewById(R.id.staticSubHeader);

        exDefault = (ExpandableListView) findViewById(R.id.defaultList);
        exDefault.setGroupIndicator(null);
        SetdefaultView();


        if(wzData != null) {
            Log.e("No tnull", "ITs not null");
        }
        else {
            Log.e("No tnull", "ITs  null");

            return;
        }

    if (editMode) {

        getWZData();
        boolean isDefault = false;
        List<Integer> filterId = new ArrayList<>();
        if (mobile_wz) {
            if (editMobileWatchZone.isWzDefault()) {
                isDefault = true;
            }

        } else {
            if (wzData.get(position).isWzDefault()) {
                isDefault = true;

            }
        }
        if(isDefault) {
            simplifyDefaultData(dbController.getDefaultDataWz(),false); //Edit mode
        }
        else {
            customView();
        }
    }
    else {
        simplifyDefaultData(dbController.getDefaultDataWz(),true); //Defaultvalue mode
    }

    adapter = new DefaultNotificationAdapter(AddStaticZoneNotification.this, default_data);
    exDefault.setAdapter(adapter);
    adapter.notifyDataSetChanged();
    exCustom = (ListView) findViewById(R.id.customCatList);

    simplifyData(dbController.getCustomCatName(1));
    mAdapter = new CustomNotificationCategoryAdapter(AddStaticZoneNotification.this, category_data);
    exCustom.setAdapter(mAdapter);



        exCustom.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView < ? > parent, View view, int position, long id) {

                i = new Intent(getApplicationContext(), AddStaticZoneNotificationTypes.class);
                i.putExtra("catId", position);
                startActivity(i);

            }
        });
    }

    private void simplifyDefaultData(ArrayList < HashMap > defaultDataWz,boolean overrideCustomSetting) {

        default_data = new ArrayList < EventGroup > ();

        if(overrideCustomSetting) {
            //Default mode

                for (int i = 0; i < defaultDataWz.size(); i++) {
                    HashMap < String, String > data = defaultDataWz.get(i);
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


        }
        else {

            //EDit  mode

            if (editMode) {
                List<Integer> filterId = new ArrayList<>();
                if (mobile_wz) {
                    if (editMobileWatchZone.isWzDefault()) {
                        filterId = editMobileWatchZone.getWatchzoneFilterGroupId();
                    }
                } else {
                    if (wzData.get(position).isWzDefault()) {
                        filterId = wzData.get(position).getWatchzoneFilterGroupId();
                    }
                }
                    for (int i = 0; i < defaultDataWz.size(); i++) {

                        HashMap<String, String> data = defaultDataWz.get(i);
                        EventGroup defaultGroup = new EventGroup();
                        defaultGroup.setId(Integer.parseInt(data.get(DBController.KEY_DEFAULT_CATEGORY_ID)));
                        defaultGroup.setName(data.get(DBController.KEY_DEFAULT_CATEGORY_NAME));
                        defaultGroup.setDescription(data.get(DBController.KEY_DEFAULT_CATEGORY_DESC));
                        defaultGroup.setDisplayOn(Boolean.valueOf(data.get(DBController.KEY_DEFAULT_CATEGORY_DISPLAY_ON)));
                        defaultGroup.setDisplayToggle(Boolean.valueOf(data.get(DBController.KEY_DEFAULT_CATEGORY_DISPLAY_TOGGLE)));
                        defaultGroup.setDisplayOnly(Boolean.valueOf(data.get(DBController.KEY_DEFAULT_CATEGORY_DISPLAY_ONLY)));
                        for (int f = 0; f < filterId.size(); f++) {
                            if (filterId.get(f).toString().equalsIgnoreCase(data.get(DBController.KEY_DEFAULT_CATEGORY_ID).toString())) {
                                defaultGroup.setFilterOn(true);
                                break;
                            } else {
                                defaultGroup.setFilterOn(false);
                            }
                        }
                        defaultGroup.setFilterToggle(Boolean.valueOf(data.get(DBController.KEY_DEFAULT_CATEGORY_FILTER_TOGGLE)));
                        default_data.add(defaultGroup);
                    }
                }
            }
    }

    private void simplifyData(ArrayList[] data) {

        ArrayList < HashMap > categories = new ArrayList < HashMap > ();
        ArrayList < HashMap > types = new ArrayList < HashMap > ();
        ArrayList < HashMap > statuses = new ArrayList < HashMap > ();

        HashMap < String, String > hash_categories = new HashMap < > ();
        HashMap < String, String > hash_types = new HashMap < > ();
        HashMap < String, String > hash_status = new HashMap < > ();

        categories = (ArrayList < HashMap > ) data[0];
        types = (ArrayList < HashMap > ) data[1];
        statuses = (ArrayList < HashMap > ) data[2];

        if (editMode) {



            for (int i = 0; i < categories.size(); i++) {
                Category category = new Category();
                hash_categories = categories.get(i);
                types_data = new ArrayList < CategoryType > ();

                for (int t = 0; t < types.size(); t++) {
                    statuses_data = new ArrayList < CategoryStatus > ();
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
                            if (Boolean.valueOf(hash_status.get(DBController.KEY_CAT_STATUS_NOTIF_CAN_FILTER)))
                                status_model.setNotificationDefaultOn(false); //Boolean.valueOf(hash_status.get(DBController.KEY_CAT_STATUS_NOTIF_DEFAULT))
                            else
                                status_model.setNotificationDefaultOn(Boolean.valueOf(hash_status.get(DBController.KEY_CAT_STATUS_NOTIF_DEFAULT)));

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

            cat.setCategoryArray(category_data);



                category_data = new ArrayList < Category > ();
            ArrayList<HashMap<String, CategoryFilter>> groupData;
            if(mobile_wz) {
                groupData = editMobileWatchZone.getWatchzoneFilter();
            }
            else {
                groupData = wzData.get(position).getWatchzoneFilter();
                Log.d("DAta",groupData.toString() );
            }

                ArrayList < Category > c = cat.getCategoryArray();
                JSONArray categoryArray = new JSONArray();
                JSONArray typeArray = new JSONArray();
                 int count=0;


                for (Category catData: cat.getCategoryArray()) {
                    for (int j = 0; j < groupData.size(); j++) {
                        HashMap<String, CategoryFilter> tempData = (HashMap<String, CategoryFilter>) groupData.get(j);

                        if (tempData.keySet().toArray()[0].equals(catData.getCategory())) {

                            CategoryFilter catFilter = tempData.get(catData.getCategory());
                            for (CategoryType catType : catData.getTypes()) {
                                count = 0;
                                for (int ct = 0; ct < catFilter.getTypes().size(); ct++) {

                                    if (catType.getCode().equalsIgnoreCase(catFilter.getTypes().get(ct).getCode())) {


                                        for (CategoryStatus catStatus : catType.getStatuses()) {
                                            for (int i = 0; i < catFilter.getTypes().get(ct).getStatus().size(); i++) {
                                                if (catFilter.getTypes().get(ct).getStatus().get(i).toString().equalsIgnoreCase(catStatus.getCode())) {
                                                    catStatus.setNotificationDefaultOn(true);
                                                    count++;
                                                }
                                            }
                                        }
                                        if (count > 0)
                                            catType.setNotificationDefaultOn(true);

                                    }
                                }
                            }
                        }
                    }
                    category_data.add(catData);
                }

            cat.setCategoryArray(category_data);
        } else {

            // normal


            for (int i = 0; i < categories.size(); i++) {
                Category category = new Category();
                hash_categories = categories.get(i);
                types_data = new ArrayList < CategoryType > ();

                for (int t = 0; t < types.size(); t++) {
                    statuses_data = new ArrayList < CategoryStatus > ();
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

            cat.setCategoryArray(category_data);
        }


    }

    public static int getKeyFromValue(List < CategoryType > hm, String value) {
        int count = 0;
        for (CategoryType ct: hm) {

            if (ct.getCode().toString().equalsIgnoreCase(value.toString())) {
                Log.e("CAT", ct.getCode() + " | " + count);
                return count;
            }
            count++;

        }
        return -1;
    }

    public static int getKeyFromValueStatus(List < CategoryStatus > hm, String value) {
        int count = 0;
        for (CategoryStatus ct: hm) {

            if (ct.getCode().toString().equalsIgnoreCase(value.toString())) {
                Log.e("1", ct.getCode() + " | " + count);
                return count;
            }
            count++;

        }
        return -1;
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
                if(editMode && !mobile_wz){
                    if (notificationView.getCurrentView() == defaultView) {
                        //default
                        editModeNotificationSave(true);


                    } else {
                        //custom
                        editModeNotificationSave(false);
                    }


                }
                else if(editMode && mobile_wz){

                    showSnack(addWz_layout,getString(R.string.msg_upatingWZ));

                    if (notificationView.getCurrentView() == defaultView) {
                        //default
                        sendProximity(true, true);

                    } else {
                        //custom
                        sendProximity(false, true);
                    }
                }
                else {
                    showSnack(addWz_layout,getString(R.string.msg_creatingWZ));

                    if (notificationView.getCurrentView() == defaultView) {
                        //default
                        saveDataToserver(true);


                    } else {
                        //custom
                        saveDataToserver(false);
                    }
                }

                return true;

            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.defaultBtn:
                if (notificationView.getCurrentView() != defaultView) {

                    showCustomToDefaultAlert();
                }
                break;
            case R.id.customBtn:
                if (notificationView.getCurrentView() != customView) {
                    customView();

                }
                break;

        }
    }

    void setListener() {

        exDefault.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {

            int previousGroup = -1;

            @Override
            public void onGroupExpand(int groupPosition) {
                if (groupPosition != previousGroup)
                    exDefault.collapseGroup(previousGroup);
                previousGroup = groupPosition;
            }

        });

    }

    public void showCustomToDefaultAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle(getString(R.string.custom_to_default_popup_notification_title))
                .setMessage(R.string.custom_to_default_popup_notification)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        defaultView();
                        simplifyDefaultData(dbController.getDefaultDataWz(),true);
                        adapter = new DefaultNotificationAdapter(AddStaticZoneNotification.this, default_data);
                        exDefault.setAdapter(adapter);
                        adapter.notifyDataSetChanged();

                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.dismiss();
                    }
                })
                .show();
    }

    private void purifyWz(JSONObject response,boolean mobileWZ) {

        if(mobileWZ) {
            ArrayList<EditWatchZones>  arryMobileWZ = new ArrayList<EditWatchZones>();

            try {
                JSONArray wz= response.getJSONArray("watchzones");
                if(wz.length()>0){

                    for(int i=0;i<wz.length();i++) {
                        JSONObject data = (JSONObject) wz.get(i);


                        EditWatchZones editModel = new EditWatchZones();
                        WatchZoneGeom geomModel = new WatchZoneGeom();

                        editModel.setWatchzoneId(data.getString("id"));
                        editModel.setWatchzoneName(data.getString("name"));
                        editModel.setWatchzoneDeviceId(data.getString("deviceId"));
                        editModel.setWatchzoneAddress(data.getString("address"));
                        editModel.setWatchzoneRadius(data.getString("radius"));
                        editModel.setWatchzoneType(data.getString("type"));

                        editModel.setWatchzoneProximity(Boolean.valueOf(data.getString("proximity")));
                        editModel.setWzNoEdit(Boolean.valueOf(data.getString("noEdit")));


                        //=== GROUP ID's FILTER



                        List<Integer> wzFilterGroup=new ArrayList<>();
                        JSONArray filterGroup =new JSONArray(data.get("filterGroupId").toString());

                        for(int j=0;j<filterGroup.length();j++){
                            wzFilterGroup.add(Integer.parseInt(filterGroup.get(j).toString()));
                        }

                        editModel.setWatchzoneFilterGroupId(wzFilterGroup);
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

                                if(filterObj.has(_dbCategories.get(j))) {
                                    JSONObject categoryObj = filterObj.getJSONObject(_dbCategories.get(j));
                                    JSONArray catTypesArr=categoryObj.getJSONArray("types");

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

                        editModel.setWatchzoneFilter(filterDetails);

                        //=====//

                        editModel.setWzDefault(Boolean.valueOf(data.getString("isDefaultFilter")));

                        editModel.setWatchzoneSound(data.getString("sound"));
                        editModel.setWzEnable(Boolean.valueOf(data.getString("enable")));
                        editModel.setWatchZoneShareCode(data.getString("shareCode"));


                        JSONArray geo=new JSONArray();
                        geo.put(data.get("geometry"));
                        JSONObject g=  geo.getJSONObject(0);

                        JSONArray geoArr=new JSONArray(g.get("coordinates").toString());
                        ArrayList<HashMap<String, Double>> cordinates = new ArrayList<HashMap<String, Double>>();
                        if(g.get("type").toString().equals("Point")) {


                            HashMap<String, Double> hashLoc = new HashMap<>();
                            hashLoc.put("latitude", Double.parseDouble(geoArr.get(0).toString()));
                            hashLoc.put("longitude", Double.parseDouble(geoArr.get(1).toString()));
                            cordinates.add(hashLoc);


                        }else{
                            JSONArray geoArrIn=geoArr.getJSONArray(0);
                            for (int k = 0; k < geoArrIn.length(); k++) {

                                JSONArray geoA=geoArrIn.getJSONArray(k);
                                HashMap<String, Double> hashLoc = new HashMap<>();
                                hashLoc.put("latitude", Double.parseDouble(geoA.get(0).toString()));
                                hashLoc.put("longitude", Double.parseDouble(geoA.get(1).toString()));
                                cordinates.add(hashLoc);
                            }
                        }
                        geomModel.setCordinate(cordinates);
                        geomModel.setType(g.get("type").toString());

                            editModel.setWatchZoneGeoms(geomModel);
                             arryMobileWZ.add(editModel);

                            Gson gson = new Gson();


                            PreferenceUtils.saveToPrefs(getApplicationContext(), Constants.KEY_VALUE_PROXIMITY_DATA,gson.toJson(arryMobileWZ));


                    }

                    if(mobileWZ) {
                        changeText("Mobile Watch Zones updated successfully.");
                        //getMobileWZData();
                    }

                }

            } catch (JSONException e) {
                e.printStackTrace();
            }


        }

    }
    public void sendProximity(boolean state, final boolean enable) {

        if(!Utility.isInternetConnected(getApplicationContext())) {
            Toast.makeText(getApplicationContext(), getString(R.string.noInternet), Toast.LENGTH_LONG).show();
            return;
        }

        Log.d("location", PreferenceUtils.getFromPrefs(getApplicationContext(), Constants.KEY_USERLATITUDE, " ").toString());


        HashMap<String, Object> mParams = new HashMap<String, Object>();

        Gson gson = new Gson();
        mParams.put("radius", sliderValue);
        mParams.put("sound", _ringtoneURI.toString());
        mParams.put("enable", enable);
        mParams.put("latitude", (String) PreferenceUtils.getFromPrefs(getApplicationContext(), Constants.KEY_USERLATITUDE, " "));
        mParams.put("longitude", (String) PreferenceUtils.getFromPrefs(getApplicationContext(), Constants.KEY_USERLONGITUDE, " "));

        ArrayList<String> defValues= new ArrayList<String>();
try {
        if (mobile_wz) {
            if (state) // default
            {

                for (int i = 0; i < default_data.size(); i++) {
                    EventGroup ev = new EventGroup();
                    ev = default_data.get(i);
                    if (ev.isFilterOn()) {
                        defValues.add(String.valueOf(ev.getId()));
                    }
                }

                mParams.put("filterGroupId", defValues);
                mParams.put("filter", new ArrayList<String>());
            } else {

                //Custom
                LinkedTreeMap<String, Object> categoryFilters = new LinkedTreeMap<>();
                for (Category catData : cat.getCategoryArray()) {
                    JSONObject category = new JSONObject();
                    JSONArray typeArray = new JSONArray();

                    for (CategoryType catType : catData.getTypes()) {
                        JSONObject type = new JSONObject();
                        JSONArray status = new JSONArray();

                        for (CategoryStatus catStatus : catType.getStatuses()) {
                            if (catStatus.isNotificationDefaultOn()) {
                                status.put(catStatus.getCode());
                            }
                        }
                        type.put("code", catType.getCode());
                        type.put("status", status);
                        typeArray.put(type);
                    }
                    category.put("types", typeArray);
                    categoryFilters.put(catData.getCategory(), category);
                }
                mParams.put("filter", categoryFilters);
                mParams.put("filterGroupId", new ArrayList<String>());

            }

        }
    }catch (JSONException e) {
            e.printStackTrace();
        }

        mParams.put("speed", "0.0 km/hr");
        mParams.put("movement", "Not Moving");

        Log.d("proximityDict", mParams.toString());

        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST, mobilewzApiURL, new JSONObject(mParams),
                new Response.Listener < JSONObject > () {

                    @Override
                    public void onResponse(JSONObject response) {

                        if (response != null) {
                            try {
                                if (response.getBoolean("success")) {

                                    Log.e("EA", response.toString());
                                    purifyWz(response, true);
                                    PreferenceUtils.saveToPrefs(getApplicationContext(),Constants.KEY_VALUE_ENABLEPROXI,enable);

                                    finish();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            dismisSnackbar();
                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {

                error.printStackTrace();
                changeText(getString(R.string.timeOut));
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
                headers.put("Authorization", "Bearer " + PreferenceUtils.getFromPrefs(getApplicationContext(), getString(R.string.pref_user_token), ""));
                return headers;
            }

        };







        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(jsonObjReq);


    }



    public void saveDataToserver(boolean state) {
        if(!Utility.isInternetConnected(getApplicationContext())) {
            Toast.makeText(getApplicationContext(), getString(R.string.noInternet), Toast.LENGTH_LONG).show();
            return;
        }


        HashMap<String, Object> mParams = new HashMap < String, Object> ();

        Gson gson = new Gson();
        String json = (String) PreferenceUtils.getFromPrefs(getApplicationContext(), "wzLocation", " ");
        JSONObject obj = gson.fromJson(json, JSONObject.class);
        ArrayList<String> defValues= new ArrayList<String>();

        try {
            mParams.put("geom", obj.getString("geom"));

            mParams.put("sound", (String) PreferenceUtils.getFromPrefs(getApplicationContext(), getString(R.string.pref_ringtone_name), " "));
            mParams.put("address", "");
            mParams.put("name", (String) PreferenceUtils.getFromPrefs(getApplicationContext(), getString(R.string.pref_wz_name), " "));
            mParams.put("proximity", String.valueOf("false"));
            int result = obj.getInt("radius");
            mParams.put("radius", Integer.toString(result));
            mParams.put("type", obj.getString("type"));


            if (state) // default
            {

                for (int i = 0; i < default_data.size(); i++) {
                    EventGroup ev = new EventGroup();
                    ev = default_data.get(i);
                    if (ev.isFilterOn()) {
                        defValues.add(String.valueOf(ev.getId()));
                    }
                }

                mParams.put("filterGroupId", defValues);
                mParams.put("filter", new ArrayList<String>());
            } else {

                LinkedTreeMap<String, Object> categoryFilters = new LinkedTreeMap<>();
                for (Category catData : cat.getCategoryArray()) {
                    JSONObject category = new JSONObject();
                    JSONArray typeArray = new JSONArray();

                    for (CategoryType catType : catData.getTypes()) {
                        JSONObject type = new JSONObject();
                        JSONArray status = new JSONArray();

                        for (CategoryStatus catStatus : catType.getStatuses()) {
                            if (catStatus.isNotificationDefaultOn()) {
                                status.put(catStatus.getCode());
                            }
                        }
                        type.put("code", catType.getCode());
                        type.put("status", status);
                        typeArray.put(type);
                    }
                    category.put("types", typeArray);
                    categoryFilters.put(catData.getCategory(), category);
                }
                mParams.put("filter", categoryFilters);
                mParams.put("filterGroupId", new ArrayList<String>());

          }

        }catch (JSONException e) {
            e.printStackTrace();
        }
        // ===========SERVER CALL  http://192.168.0.10:4203/wz/index.php

        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.POST, apiURL, new JSONObject(mParams),
                new Response.Listener < JSONObject > () {

                    @Override
                    public void onResponse(JSONObject response) {


                        try {
                            String msg =  " ";
                            if (Boolean.valueOf(response.getString("success"))) {

                                if(editMode && mobile_wz) {

                                    msg = getString(R.string.msg_updatedWZ);
                                }
                                else {
                                    msg = getString(R.string.msg_createdWZ);
                                }

                                changeText(msg);
                                dismisSnackbar();
                                Thread.sleep(2000);

                                Log.e("EA", response.toString());
                                Intent o = new Intent(AddStaticZoneNotification.this, HomeActivity.class);
                                o.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(o);


                            }
                            else {
                                if(editMode && mobile_wz) {

                                    msg = getString(R.string.msg_unableUpdateWZ );
                                }
                                else {
                                    msg = getString(R.string.msg_unableCreateWZ);
                                }
                                changeText(msg);
                                dismisSnackbar();

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                changeText(getString(R.string.timeOut));
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
                headers.put("Authorization", "Bearer " + PreferenceUtils.getFromPrefs(getApplicationContext(), getString(R.string.pref_user_token), ""));
                return headers;
            }
        };







        jsonObjReq.setRetryPolicy(new DefaultRetryPolicy(5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(jsonObjReq);
    }



    public void defaultView() {
        if (notificationView.getCurrentView() != defaultView) {


            defaultBtn.setBackgroundResource(R.drawable.button_red_bottom_border);
            customBtn.setBackgroundResource(R.drawable.border_shadow);

            notificationView.showNext();
            txtHeader.setText(getText(R.string.lbl_mapLayersDefaultTitle));
            txtSubHeader.setText(getText(R.string.lbl_mapLayersDefaultSubTitle));

            if(mobile_wz) {
                txtSubHeader.setText(getText(R.string.lbl_prefilterHeaderDefaultSubTitle) + " " + getText(R.string.lbl_mobileWzDisplayText));
            }
            else {
                txtSubHeader.setText(getText(R.string.lbl_prefilterHeaderDefaultSubTitle) + " " + getText(R.string.lbl_wzDisplayText));
            }
        }

    }

    public void customView() {
        if (notificationView.getCurrentView() != customView) {

            customBtn.setBackgroundResource(R.drawable.button_red_bottom_border);
            defaultBtn.setBackgroundResource(R.drawable.border_shadow);

            txtHeader.setText(getText(R.string.lbl_prefilterHeaderCustom));
            txtSubHeader.setText(getText(R.string.lbl_selectCustomNoti));

            notificationView.showPrevious();
        }
    }

    public void SetdefaultView() {


        defaultBtn.setBackgroundResource(R.drawable.button_red_bottom_border);
        customBtn.setBackgroundResource(R.drawable.border_shadow);

        txtHeader.setText(getText(R.string.lbl_prefilterHeaderDefault));

        if(mobile_wz) {
            txtSubHeader.setText(getText(R.string.lbl_prefilterHeaderDefaultSubTitle) + " " + getText(R.string.lbl_mobileWzDisplayText));
        }
        else {
            txtSubHeader.setText(getText(R.string.lbl_prefilterHeaderDefaultSubTitle) + " " + getText(R.string.lbl_wzDisplayText));
        }


        // notificationView.showNext();


    }

    public void editModeNotificationSave(boolean state){
        ArrayList<String> defValues= new ArrayList<String>();
        ArrayList<HashMap<String, CategoryFilter>> categoryFilters = new ArrayList<HashMap<String, CategoryFilter>>();
        if (state) // default
        {

            for (int i = 0; i < default_data.size(); i++) {
                EventGroup ev = new EventGroup();
                ev = default_data.get(i);
                if (ev.isFilterOn()) {
                    defValues.add(String.valueOf(ev.getId()));
                }
            }


        } else {


                ArrayList<CategoryTypeFilter> typeArray=new ArrayList<CategoryTypeFilter>();
                for (Category catData : cat.getCategoryArray()) {

                    HashMap<String, CategoryFilter> categoryHash=new HashMap<>();
                    typeArray=new ArrayList<CategoryTypeFilter>();

                    for (CategoryType catType : catData.getTypes()) {


                        List<String> status=new ArrayList<>();
                        CategoryTypeFilter type=new CategoryTypeFilter();

                        for (CategoryStatus catStatus : catType.getStatuses()) {
                            if (catStatus.isNotificationDefaultOn()) {
                                status.add(catStatus.getCode());
                            }
                        }
                        type.setCode(catType.getCode());
                        type.setStatus(status);
                        typeArray.add(type);
                    }
                    CategoryFilter catFilter=new CategoryFilter();
                    catFilter.setTypes(typeArray);

                    categoryHash.put(catData.getCategory(), catFilter);
                    categoryFilters.add(categoryHash);
                }

          //  editWatchZones.setWatchzoneFilter(categoryFilters);
           // editWatchZones.setWatchzoneFilterGroupId(new ArrayList<Integer>());

        }

        Intent resultIntent = new Intent();

        resultIntent.putExtra("default", state);
        resultIntent.putExtra("filter", categoryFilters);
        resultIntent.putExtra("filterGroup", defValues);
        setResult(Activity.RESULT_OK, resultIntent);
        finish();
    }

}