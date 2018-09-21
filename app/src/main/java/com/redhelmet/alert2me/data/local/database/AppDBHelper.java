package com.redhelmet.alert2me.data.local.database;

import com.redhelmet.alert2me.data.model.response.ConfigResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class AppDBHelper implements DBHelper {
    private DBController dbController;

    public AppDBHelper(DBController dbController) {
        this.dbController = dbController;
    }

    @Override
    public void saveConfig(ConfigResponse response) {
        dbController.deleteTable();
        dbController.createConfigDB();
        try {
            JSONObject root = new JSONObject(response.toString());
            JSONArray arr_cat = root.getJSONArray("categories");
            JSONArray default_cat = root.getJSONArray("eventGroups");
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
}
