package com.redhelmet.alert2me.ui.eventfilter;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.gson.Gson;
import com.redhelmet.alert2me.R;
import com.redhelmet.alert2me.adapters.AppViewPagerAdapter;
import com.redhelmet.alert2me.data.local.database.DBController;
import com.redhelmet.alert2me.data.model.Category;
import com.redhelmet.alert2me.data.model.CategoryFilter;
import com.redhelmet.alert2me.data.model.CategoryStatus;
import com.redhelmet.alert2me.data.model.CategoryType;
import com.redhelmet.alert2me.data.model.CategoryTypeFilter;
import com.redhelmet.alert2me.data.model.EventGroup;
import com.redhelmet.alert2me.databinding.ActivityEventFilterBinding;
import com.redhelmet.alert2me.domain.util.PreferenceUtils;
import com.redhelmet.alert2me.ui.base.BaseActivity;
import com.redhelmet.alert2me.ui.eventfilter.custom.CustomFilterFragment;
import com.redhelmet.alert2me.ui.eventfilter.defaultfilter.DefaultFilterFragment;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.HashMap;

public class EventFilterActivity extends BaseActivity<EventFilterViewModel, ActivityEventFilterBinding> {

    public ArrayList<Category> category_data = new ArrayList<Category>();
    public ArrayList<CategoryType> types_data = new ArrayList<CategoryType>();
    public ArrayList<CategoryStatus> statuses_data = new ArrayList<CategoryStatus>();
    public ArrayList<EventGroup> default_data = new ArrayList<EventGroup>();
    ArrayList<HashMap<String, CategoryFilter>> categoryFilters = new ArrayList<HashMap<String, CategoryFilter>>();

    public boolean editMode = false;
    public int position = 0;
    private AppViewPagerAdapter adapter;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_event_filter;
    }

    @Override
    protected Class<EventFilterViewModel> obtainViewModel() {
        return EventFilterViewModel.class;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setupViewPager();
        initializeToolbar();

//        Gson gson = new Gson();
//        String values;
//
//        if (PreferenceUtils.hasKey(getApplicationContext(), getString(R.string.pref_map_isDefault))) {
//            if ((boolean) PreferenceUtils.getFromPrefs(getApplicationContext(), getString(R.string.pref_map_isDefault), false)) {
//                values = (String) PreferenceUtils.getFromPrefs(getApplicationContext(), getString(R.string.pref_map_filter), "");
//                defValues = gson.fromJson(values,
//                        ArrayList.class);
//
//            } else {
//                values = (String) PreferenceUtils.getFromPrefs(getApplicationContext(), getString(R.string.pref_map_filter), "");
//                categoryFilters = gson.fromJson(values,
//                        ArrayList.class);
//                categoryFilters = new Gson().fromJson(values, new TypeToken<ArrayList<HashMap<String, CategoryFilter>>>() {
//                }.getType());
//            }
//            editMode = true;
//        }
        initializeControls();
    }

    private void setupViewPager() {
        adapter = new AppViewPagerAdapter(getSupportFragmentManager());
        adapter.addFrag(new DefaultFilterFragment(), getString(R.string.lblDefault));
        adapter.addFrag(new CustomFilterFragment(), getString(R.string.lblCustom));
        binder.viewpager.setAdapter(adapter);
        binder.viewpager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
//                updateOptionsMenu();
//                updateToolbarTitle();
            }
        });
    }

    public void initializeToolbar() {
        setSupportActionBar(binder.toolbar);
        ActionBar supportActionBar = getSupportActionBar();

        if (supportActionBar != null) {
            supportActionBar.setDisplayHomeAsUpEnabled(true);
            supportActionBar.setTitle(Html.fromHtml("<small>Event Map Filter</small>"));
        }
    }


    public void initializeControls() {
        if (viewModel.isDefaultFilter()) {
            binder.viewpager.setCurrentItem(0, false);
        } else {
            binder.viewpager.setCurrentItem(1, false);
        }
    }

    private void simplifyData(ArrayList[] data) {

        ArrayList<HashMap> categories = new ArrayList<HashMap>();
        ArrayList<HashMap> types = new ArrayList<HashMap>();
        ArrayList<HashMap> statuses = new ArrayList<HashMap>();

        HashMap<String, String> hash_categories = new HashMap<>();
        HashMap<String, String> hash_types = new HashMap<>();
        HashMap<String, String> hash_status = new HashMap<>();

        categories = (ArrayList<HashMap>) data[0];
        types = (ArrayList<HashMap>) data[1];
        statuses = (ArrayList<HashMap>) data[2];

        if (editMode) {


            for (int i = 0; i < categories.size(); i++) {
                Category category = new Category();
                hash_categories = categories.get(i);
                types_data = new ArrayList<CategoryType>();

                for (int t = 0; t < types.size(); t++) {
                    statuses_data = new ArrayList<CategoryStatus>();
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

            category_data = new ArrayList<Category>();

            JSONArray categoryArray = new JSONArray();
            JSONArray typeArray = new JSONArray();
            int count = 0;


//            for (Category catData : originCategories) {
//                for (int j = 0; j < categoryFilters.size(); j++) {
//                    HashMap<String, CategoryFilter> hashCategoryFilter = categoryFilters.get(j);
//                    if (hashCategoryFilter.containsKey(catData.getCategory())) {
//                        CategoryFilter catFilter = hashCategoryFilter.get(catData.getCategory());
//
//                        for (CategoryType catType : catData.getTypes()) {
//                            count = 0;
//                            for (int ct = 0; ct < catFilter.getTypes().size(); ct++) {
//
//                                if (catType.getCode().equalsIgnoreCase(catFilter.getTypes().get(ct).getCode())) {
//
//
//                                    for (CategoryStatus catStatus : catType.getStatuses()) {
//                                        for (int i = 0; i < catFilter.getTypes().get(ct).getStatus().size(); i++) {
//                                            if (catFilter.getTypes().get(ct).getStatus().get(i).toString().equalsIgnoreCase(catStatus.getCode())) {
//                                                catStatus.setNotificationDefaultOn(true);
//                                                count++;
//                                            }
//                                        }
//                                    }
//                                    if (count > 0)
//                                        catType.setNotificationDefaultOn(true);
//
//                                }
//                            }
//                        }
//                    }
//                }
//                category_data.add(catData);
//            }
//
//            originCategories = category_data;
        } else {

            // normal


            for (int i = 0; i < categories.size(); i++) {
                Category category = new Category();
                hash_categories = categories.get(i);
                types_data = new ArrayList<CategoryType>();

                for (int t = 0; t < types.size(); t++) {
                    statuses_data = new ArrayList<CategoryStatus>();
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

//            originCategories = category_data;
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
                int currentPosition = binder.viewpager.getCurrentItem();
                Fragment fragment = adapter.getItem(currentPosition);
                if (fragment instanceof OnSaveClickListener) {
                    ((OnSaveClickListener) fragment).onSaveClick(editMode);
                }
                return true;

            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void showCustomToDefaultAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle(getString(R.string.custom_to_default_popup_display_title))
                .setMessage(R.string.custom_to_default_popup_display)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
//                        simplifyDefaultData(dbController.getDefaultMapFilter(), true);
//                        adapter = new DefaultNotificationAdapter(EventFilterActivity.this, default_data);
//                        exDefault.setAdapter(adapter);
//                        adapter.notifyDataSetChanged();

                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.dismiss();
                    }
                })
                .show();
    }

    public interface OnSaveClickListener {
        void onSaveClick(boolean editMode);
    }
}