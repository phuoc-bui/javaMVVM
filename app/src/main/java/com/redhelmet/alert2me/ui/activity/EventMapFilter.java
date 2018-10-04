package com.redhelmet.alert2me.ui.activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
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
import android.widget.ViewSwitcher;

import com.redhelmet.alert2me.adapters.CustomNotificationCategoryAdapter;
import com.redhelmet.alert2me.adapters.DefaultNotificationAdapter;
import com.redhelmet.alert2me.data.local.database.DBController;
import com.redhelmet.alert2me.domain.util.PreferenceUtils;
import com.redhelmet.alert2me.data.model.Category;
import com.redhelmet.alert2me.data.model.CategoryFilter;
import com.redhelmet.alert2me.data.model.CategoryStatus;
import com.redhelmet.alert2me.data.model.CategoryType;
import com.redhelmet.alert2me.data.model.CategoryTypeFilter;
import com.redhelmet.alert2me.data.model.EventGroup;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.redhelmet.alert2me.R;

public class EventMapFilter extends BaseActivity implements View.OnClickListener {

    Toolbar toolbar;
    Intent i;
    ExpandableListView exDefault;
    private static DefaultNotificationAdapter adapter;
    private CustomNotificationCategoryAdapter mAdapter;
    RelativeLayout defaultView, customView;
    ViewSwitcher notificationView;
    ListView exCustom;
    Button defaultBtn, customBtn;
    DBController dbController;
    public ArrayList <Category> category_data = new ArrayList < Category > ();
    public ArrayList <CategoryType> types_data = new ArrayList < CategoryType > ();
    public ArrayList <CategoryStatus> statuses_data = new ArrayList < CategoryStatus > ();
    public ArrayList <EventGroup> default_data = new ArrayList < EventGroup > ();
    ArrayList<HashMap<String, CategoryFilter>> categoryFilters = new ArrayList<HashMap<String, CategoryFilter>>();
    ArrayList<String> defValues= new ArrayList<String>();

    List<Category> originCategories;
    EventGroup defaultGroup;
    public boolean editMode = false;
    public int position = 0;
    TextView txtHeader, txtSubHeader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_static_wz_notification);
//        dbController = DBController.getInstance(this);
        Gson gson = new Gson();
        String values;

        if (PreferenceUtils.hasKey(getApplicationContext(), getString(R.string.pref_map_isDefault))) {
            if ((boolean) PreferenceUtils.getFromPrefs(getApplicationContext(), getString(R.string.pref_map_isDefault), false)) {
                values = (String) PreferenceUtils.getFromPrefs(getApplicationContext(), getString(R.string.pref_map_filter), "");
                defValues = gson.fromJson(values,
                        ArrayList.class);

            } else {
                values=(String)  PreferenceUtils.getFromPrefs(getApplicationContext(), getString(R.string.pref_map_filter), "");
                categoryFilters = gson.fromJson(values,
                        ArrayList.class);
                categoryFilters = new Gson().fromJson(values, new TypeToken<ArrayList<HashMap<String, CategoryFilter>>>() {}.getType());
            }
            editMode=true;
    }
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
            supportActionBar.setTitle(Html.fromHtml("<small>Event Map Filter</small>"));
        }
    }


    public void initializeControls() {

        originCategories = dataManager.getCategoriesSync();

        defaultBtn = (Button) findViewById(R.id.defaultBtn);
        customBtn = (Button) findViewById(R.id.customBtn);
        notificationView = (ViewSwitcher) findViewById(R.id.notification_switcher);
        customView = (RelativeLayout) findViewById(R.id.custom_view);
        defaultView = (RelativeLayout) findViewById(R.id.default_view);
        defaultBtn.setOnClickListener(this);
        customBtn.setOnClickListener(this);
        txtHeader = (TextView) findViewById(R.id.staticHeader);
        txtSubHeader = (TextView) findViewById(R.id.staticSubHeader);
        exDefault = (ExpandableListView) findViewById(R.id.defaultList);
        exDefault.setGroupIndicator(null);
        simplifyDefaultData(dbController.getDefaultMapFilter(),false);
        adapter = new DefaultNotificationAdapter(EventMapFilter.this, default_data);
        exDefault.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        exCustom = (ListView) findViewById(R.id.customCatList);

        simplifyData(dbController.getCustomCatName(0));
        mAdapter = new CustomNotificationCategoryAdapter(EventMapFilter.this, category_data);
        exCustom.setAdapter(mAdapter);

        SetdefaultView();



        if (editMode) {
            if ((boolean) PreferenceUtils.getFromPrefs(getApplicationContext(), getString(R.string.pref_map_isDefault), false)) {

            } else {
                customView();
            }
        }

        exCustom.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView < ? > parent, View view, int position, long id) {

                i = new Intent(getApplicationContext(), AddStaticZoneNotificationTypes.class);
                i.putExtra("catId", position);
                startActivity(i);

            }
        });
    }

    private void simplifyDefaultData(ArrayList < HashMap > defaultDataWz, boolean reset) {

        default_data = new ArrayList < EventGroup > ();


        if (editMode && !reset) {


            for (int i = 0; i < defaultDataWz.size(); i++) {
                HashMap < String, String > data = defaultDataWz.get(i);
                EventGroup defaultGroup = new EventGroup();
                defaultGroup.setId(Integer.parseInt(data.get(DBController.KEY_DEFAULT_CATEGORY_ID)));
                defaultGroup.setName(data.get(DBController.KEY_DEFAULT_CATEGORY_NAME));
                defaultGroup.setDescription(data.get(DBController.KEY_DEFAULT_CATEGORY_DESC));
                defaultGroup.setDisplayOn(Boolean.valueOf(data.get(DBController.KEY_DEFAULT_CATEGORY_DISPLAY_ON)));
                defaultGroup.setDisplayToggle(Boolean.valueOf(data.get(DBController.KEY_DEFAULT_CATEGORY_DISPLAY_TOGGLE)));
                defaultGroup.setDisplayOnly(Boolean.valueOf(data.get(DBController.KEY_DEFAULT_CATEGORY_DISPLAY_ONLY)));
                for (int f = 0; f < defValues.size(); f++) {
                    if (defValues.get(f).toString().equalsIgnoreCase(data.get(DBController.KEY_DEFAULT_CATEGORY_ID).toString())) {
                        defaultGroup.setFilterOn(true);
                        break;
                    } else {
                        defaultGroup.setFilterOn(false);
                    }
                }
                defaultGroup.setFilterToggle(Boolean.valueOf(data.get(DBController.KEY_DEFAULT_CATEGORY_FILTER_TOGGLE)));
                default_data.add(defaultGroup);
            }
        } else {
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

            originCategories = category_data;



                category_data = new ArrayList < Category > ();

                JSONArray categoryArray = new JSONArray();
                JSONArray typeArray = new JSONArray();
                 int count=0;


                for (Category catData: originCategories) {
                    for (int j = 0; j < categoryFilters.size(); j++) {
                        HashMap<String,CategoryFilter> hashCategoryFilter=categoryFilters.get(j);
                        if(hashCategoryFilter.containsKey(catData.getCategory())){
                             CategoryFilter catFilter=hashCategoryFilter.get(catData.getCategory());

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

           originCategories = category_data;
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

            originCategories = category_data;
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
//                if(editMode){
//                    if (notificationView.getCurrentView() == defaultView) {
//                        //default
//                        editModeNotificationSave(true);
//
//
//                    } else {
//                        //custom
//                        editModeNotificationSave(false);
//                    }
//
//
//                }else {
                    if (notificationView.getCurrentView() == defaultView) {
                        //default
                        saveDataToserver(true);


                    } else {
                        //custom
                        saveDataToserver(false);
                    }
               // }

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

        builder.setTitle(getString(R.string.custom_to_default_popup_display_title))
                .setMessage(R.string.custom_to_default_popup_display)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        defaultView();
                        simplifyDefaultData(dbController.getDefaultMapFilter(),true);
                        adapter = new DefaultNotificationAdapter(EventMapFilter.this, default_data);
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


    public void saveDataToserver(boolean state) {


        Gson gson = new Gson();

        ArrayList<String> defValues= new ArrayList<String>();



            if (state) // default
            {

                for (int i = 0; i < default_data.size(); i++) {
                    EventGroup ev = new EventGroup();
                    ev = default_data.get(i);
                    if (ev.isFilterOn()) {
                        defValues.add(String.valueOf(ev.getId()));
                    }
                }


                PreferenceUtils.saveToPrefs(getApplicationContext(),getString(R.string.pref_map_filter),gson.toJson(defValues));

            } else {

//                LinkedTreeMap<String, Object> categoryFilters = new LinkedTreeMap<>();
//                for (Category catData : cat.getCategoryArray()) {
//                    JSONObject categories = new JSONObject();
//                    JSONArray typeArray = new JSONArray();
//
//                    for (CategoryType catType : catData.getTypes()) {
//                        JSONObject type = new JSONObject();
//                        JSONArray status = new JSONArray();
//
//                        for (CategoryStatus catStatus : catType.getStatuses()) {
//                            if (catStatus.isNotificationDefaultOn()) {
//                                status.put(catStatus.getCode());
//                            }
//                        }
//                        type.put("code", catType.getCode());
//                        type.put("status", status);
//                        typeArray.put(type);
//                    }
//                    categories.put("types", typeArray);
//                    categoryFilters.put(catData.getCategory(), categories);
//                }

                ArrayList<HashMap<String, CategoryFilter>> categoryFilters = new ArrayList<HashMap<String, CategoryFilter>>();

                ArrayList<CategoryTypeFilter> typeArray=new ArrayList<CategoryTypeFilter>();
                for (Category catData : originCategories) {

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
                PreferenceUtils.saveToPrefs(getApplicationContext(),getString(R.string.pref_map_filter),gson.toJson(categoryFilters));

            }
          PreferenceUtils.saveToPrefs(getApplicationContext(),getString(R.string.pref_map_isDefault),state);

        Intent resultIntent = new Intent();

        setResult(Activity.RESULT_OK, resultIntent);
        finish();
    }


    public void defaultView() {
        if (notificationView.getCurrentView() != defaultView) {

            customBtn.setBackgroundResource(R.drawable.border_shadow);
            defaultBtn.setBackgroundResource(R.drawable.button_red_bottom_border);
            txtHeader.setText(getText(R.string.lbl_mapLayersDefaultTitle));
            txtSubHeader.setText(getText(R.string.lbl_mapLayersDefaultSubTitle));
            notificationView.showNext();
        }

    }
    public void customView() {
        if (notificationView.getCurrentView() != customView) {
            defaultBtn.setBackgroundResource(R.drawable.border_shadow);
            customBtn.setBackgroundResource(R.drawable.button_red_bottom_border);
            notificationView.showPrevious();
            txtHeader.setText(getText(R.string.lbl_mapLayersCustomTitle));
            txtSubHeader.setText(getText(R.string.lbl_mapLayersCustomSubTitle));
        }
    }

    public void SetdefaultView() {

            defaultBtn.setBackgroundResource(R.drawable.button_red_bottom_border);
        customBtn.setBackgroundResource(R.drawable.border_shadow);
        txtHeader.setText(getText(R.string.lbl_mapLayersDefaultTitle));
        txtSubHeader.setText(getText(R.string.lbl_mapLayersDefaultSubTitle));
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
                for (Category catData : originCategories) {

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