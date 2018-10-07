package com.redhelmet.alert2me.ui.eventfilter.custom;

import android.app.Activity;
import android.arch.lifecycle.MutableLiveData;
import android.content.Intent;

import com.google.gson.Gson;
import com.redhelmet.alert2me.R;
import com.redhelmet.alert2me.data.DataManager;
import com.redhelmet.alert2me.data.model.Category;
import com.redhelmet.alert2me.data.model.CategoryFilter;
import com.redhelmet.alert2me.data.model.CategoryStatus;
import com.redhelmet.alert2me.data.model.CategoryType;
import com.redhelmet.alert2me.data.model.CategoryTypeFilter;
import com.redhelmet.alert2me.domain.util.PreferenceUtils;
import com.redhelmet.alert2me.ui.base.BaseViewModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;

public class CustomFilterViewModel extends BaseViewModel {
    public MutableLiveData<List<Category>> allCategories = new MutableLiveData<>();

    public CustomFilterViewModel(DataManager dataManager) {
        super(dataManager);
        disposeBag.add(dataManager.getCategories()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(list -> allCategories.setValue(list)));
    }

    public void saveData(boolean editMode) {
        if (editMode) {
            editModeNotificationSave(true);
        } else {
            saveDataToserver(true);
        }
    }


    private void saveDataToserver(boolean state) {


        Gson gson = new Gson();

        ArrayList<String> defValues = new ArrayList<String>();


//                LinkedTreeMap<String, Object> categoryFilters = new LinkedTreeMap<>();
//                for (Category catData : cat.getCategoryArray()) {
//                    JSONObject allEventGroup = new JSONObject();
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
//                    allEventGroup.put("types", typeArray);
//                    categoryFilters.put(catData.getCategory(), allEventGroup);
//                }

        ArrayList<HashMap<String, CategoryFilter>> categoryFilters = new ArrayList<HashMap<String, CategoryFilter>>();

        ArrayList<CategoryTypeFilter> typeArray = new ArrayList<CategoryTypeFilter>();
            for (Category catData : originCategories) {

                HashMap<String, CategoryFilter> categoryHash = new HashMap<>();
                typeArray = new ArrayList<CategoryTypeFilter>();

                for (CategoryType catType : catData.getTypes()) {


                    List<String> status = new ArrayList<>();
                    CategoryTypeFilter type = new CategoryTypeFilter();

                    for (CategoryStatus catStatus : catType.getStatuses()) {
                        if (catStatus.isNotificationDefaultOn()) {
                            status.add(catStatus.getCode());
                        }
                    }
                    type.setCode(catType.getCode());
                    type.setStatus(status);
                    typeArray.add(type);
                }
                CategoryFilter catFilter = new CategoryFilter();
                catFilter.setTypes(typeArray);

                categoryHash.put(catData.getCategory(), catFilter);
                categoryFilters.add(categoryHash);
            }
        PreferenceUtils.saveToPrefs(getApplicationContext(), getString(R.string.pref_map_filter), gson.toJson(categoryFilters));

        PreferenceUtils.saveToPrefs(getApplicationContext(), getString(R.string.pref_map_isDefault), state);

        Intent resultIntent = new Intent();

        setResult(Activity.RESULT_OK, resultIntent);
        finish();
    }

    private void editModeNotificationSave(boolean state) {
        ArrayList<String> defValues = new ArrayList<String>();
        ArrayList<HashMap<String, CategoryFilter>> categoryFilters = new ArrayList<HashMap<String, CategoryFilter>>();

        ArrayList<CategoryTypeFilter> typeArray = new ArrayList<CategoryTypeFilter>();
//            for (Category catData : originCategories) {
//
//                HashMap<String, CategoryFilter> categoryHash = new HashMap<>();
//                typeArray = new ArrayList<CategoryTypeFilter>();
//
//                for (CategoryType catType : catData.getTypes()) {
//
//
//                    List<String> status = new ArrayList<>();
//                    CategoryTypeFilter type = new CategoryTypeFilter();
//
//                    for (CategoryStatus catStatus : catType.getStatuses()) {
//                        if (catStatus.isNotificationDefaultOn()) {
//                            status.add(catStatus.getCode());
//                        }
//                    }
//                    type.setCode(catType.getCode());
//                    type.setStatus(status);
//                    typeArray.add(type);
//                }
//                CategoryFilter catFilter = new CategoryFilter();
//                catFilter.setTypes(typeArray);
//
//                categoryHash.put(catData.getCategory(), catFilter);
//                categoryFilters.add(categoryHash);
//            }

        //  editWatchZones.setWatchzoneFilter(categoryFilters);
        // editWatchZones.setWatchzoneFilterGroupId(new ArrayList<Integer>());

        Intent resultIntent = new Intent();

        resultIntent.putExtra("default", state);
        resultIntent.putExtra("filter", categoryFilters);
        resultIntent.putExtra("filterGroup", defValues);
        setResult(Activity.RESULT_OK, resultIntent);
        finish();
    }
}
