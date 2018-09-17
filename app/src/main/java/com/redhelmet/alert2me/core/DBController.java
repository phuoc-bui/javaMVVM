package com.redhelmet.alert2me.core;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.redhelmet.alert2me.domain.ExceptionHandler;
import com.redhelmet.alert2me.model.CategoryFilter;
import com.redhelmet.alert2me.model.WatchZoneGeom;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.redhelmet.alert2me.model.EditWatchZones;


/**
 * Created by inbox on 22/11/17.
 */

public class DBController extends SQLiteOpenHelper {


    // 0 - WZ
    // 1 - MAP


    public String TAG="DB";
    // Database Info
    private static final String DATABASE_NAME = "ea_database.db";
    private static final int DATABASE_VERSION =1;

    // Table Names
    public static final String TABLE_DEFAULT_CATEGORY = "default_category";
    public static final String TABLE_CATEGORY = "category";
    public static final String TABLE_STATUS = "status";
    public static final String TABLE_TYPES = "types";
    public static final String TABLE_WATCHZONES = "watchzones";
    public static final String TABLE_OVERLAY = "overlay";


    // DEFAULT category Table Columns
    public static final String KEY_DEFAULT_CATEGORY_ID = "id";
    public static final String KEY_DEFAULT_CATEGORY_NAME = "name";
    public static final String KEY_DEFAULT_CATEGORY_DESC = "description";
    public static final String KEY_DEFAULT_CATEGORY_DISPLAY_ON = "displayOn";
    public static final String KEY_DEFAULT_CATEGORY_DISPLAY_TOGGLE = "displayToggle";
    public static final String KEY_DEFAULT_CATEGORY_DISPLAY_ONLY = "displayOnly";
    public static final String KEY_DEFAULT_CATEGORY_FILTER_ON = "filterOn";
    public static final String KEY_DEFAULT_CATEGORY_FILTER_TOGGLE = "filterToggle";
    public static final String KEY_DEFAULT_CATEGORY_DISPLAYFILTER = "displayFilter";

    // Custom category Table Columns
    public static final String KEY_CATEGORY_ID ="catId";
    public static final String KEY_CATEGORY = "category";
    public static final String KEY_CATEGORY_NAME = "nameLabel";
    public static final String KEY_CATEGORY_DESC = "filterDescription";
    public static final String KEY_CATEGORY_DISPLAY_ONLY = "displayOnly";
    public static final String KEY_CATEGORY_FILTER_ORDER = "filterOrder";

    // status Table Columns
    public static final String KEY_REF_STATUS_CATEGORY_ID = "cat_id";
    public static final String KEY_CAT_STATUS_CODE = "code";
    public static final String KEY_CAT_STATUS_DESC = "description";
    public static final String KEY_CAT_STATUS_NAME = "name";
    public static final String KEY_CAT_STATUS_PRIMARY_COLOR = "primaryColor";
    public static final String KEY_CAT_STATUS_SECONDARY_COLOR = "secondaryColor";
    public static final String KEY_CAT_STATUS_TEXT_COLOR="textColor";
    public static final String KEY_CAT_STATUS_CAN_FILTER = "canFilter";
    public static final String KEY_CAT_STATUS_DEFAULT = "defaultOn";
    public static final String KEY_CAT_STATUS_NOTIF_CAN_FILTER = "notificationCanFilter";
    public static final String KEY_CAT_STATUS_NOTIF_DEFAULT = "notificationDefaultOn";


    // type Table Columns
    public static final String KEY_REF_TYPE_CATEGORY_ID = "cat_id";
    public static final String KEY_CAT_TYPE_CODE = "code";
    public static final String KEY_CAT_TYPE_NAME = "name";
    public static final String KEY_CAT_TYPE_ICON = "icon";
    public static final String KEY_CAT_TYPE_DEFAULT = "defaultOn";
    public static final String KEY_CAT_TYPE_CAN_FILTER = "canFilter";
    public static final String KEY_CAT_TYPE_NOTIF_CAN_FILTER = "notificationCanFilter";
    public static final String KEY_CAT_TYPE_NOTIF_DEFAULT = "notificationDefaultOn";


    // Watchzone Table Columns
    public static final String KEY_REF_WZ_ID = "watchzone_id";
    public static final String KEY_REF_WZ_DEVICE_ID = "watchzone_device_id";
    public static final String KEY_REF_WZ_SOUND = "watchzone_sound";
    public static final String KEY_REF_WZ_ADDRESS = "watchzone_address";
    public static final String KEY_REF_WZ_NAME = "watchzone_name";
    public static final String KEY_REF_WZ_RADIUS = "watchzone_radius";
    public static final String KEY_REF_WZ_TYPE = "watchzone_type";
    public static final String KEY_REF_WZ_FILTER = "watchzone_filter";
    public static final String KEY_REF_WZ_FILTERGROUPID = "watchzone_filterGroupId";
    public static final String KEY_REF_WZ_ENABLE = "watchzone_enable";
    public static final String KEY_REF_WZ_PROXIMITY = "watchzone_proximity";
    public static final String KEY_REF_WZ_ISDEFAULT = "watchzone_isDefault";
    public static final String KEY_REF_WZ_NOEDIT = "watchzone_noEdit";
    public static final String KEY_REF_WZ_SHARECODE = "watchzone_shareCode";
    public static final String KEY_REF_WZ_GEOMS = "watchZoneGeoms";
    public static final String KEY_REF_WZ_EDITLIST = "edit_Wz";


    public DBController(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

        Thread.setDefaultUncaughtExceptionHandler(new ExceptionHandler());

    }
 private static DBController sInstance;

    public static synchronized DBController getInstance(Context context) {
         if (sInstance == null) {
            sInstance = new DBController(context.getApplicationContext());
        }
        return sInstance;
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
       try {

           db.execSQL(CREATE_DEFAULT_CAT_TABLE());
           db.execSQL(CREATE_CAT_TABLE());
           db.execSQL(CREATE_STATUS_TABLE());
           db.execSQL(CREATE_TYPE_TABLE());
           db.execSQL(CREATE_WATCHZONE_TABLE());
       }
        catch (android.database.SQLException e) {
            Log.d(TAG, "Error while trying to add post to database");
            e.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion != newVersion) {
            // Simplest implementation is to drop all old tables and recreate them
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_DEFAULT_CATEGORY);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_CATEGORY);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_STATUS);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_TYPES);
            db.execSQL("DROP TABLE IF EXISTS "+ TABLE_WATCHZONES);
            onCreate(db);
        }
    }


    public  void deleteTable(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS "+ TABLE_CATEGORY);
        db.execSQL("DROP TABLE IF EXISTS "+ TABLE_DEFAULT_CATEGORY);
        db.execSQL("DROP TABLE IF EXISTS "+ TABLE_TYPES);
        db.execSQL("DROP TABLE IF EXISTS "+ TABLE_STATUS);
    }

    public  void deleteWZ(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS "+ TABLE_WATCHZONES);
    }
    public void createConfigDB() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL(CREATE_DEFAULT_CAT_TABLE());
        db.execSQL(CREATE_CAT_TABLE());
        db.execSQL(CREATE_STATUS_TABLE());
        db.execSQL(CREATE_TYPE_TABLE());
    }

    public long add_custom_config(ArrayList<HashMap> category) {
        SQLiteDatabase db = this.getWritableDatabase();
       db.beginTransaction();
        long id=-1;
        try {

            ContentValues values = new ContentValues();
            for(int i=0;i<category.size();i++) {

                values.put(KEY_CATEGORY, category.get(i).get("category").toString());
                values.put(KEY_CATEGORY_NAME, category.get(i).get("nameLabel").toString());
                values.put(KEY_CATEGORY_DESC, category.get(i).get("filterDescription").toString());
                values.put(KEY_CATEGORY_DISPLAY_ONLY, category.get(i).get("displayOnly").toString());
                values.put(KEY_CATEGORY_FILTER_ORDER, category.get(i).get("filterOrder").toString());

               id =db.insert(TABLE_CATEGORY, null, values);

            }
        } catch (android.database.SQLException e) {
             e.printStackTrace();
        } finally {
            db.setTransactionSuccessful();
           db.endTransaction();
            db.close();
            return id;
        }
    }
    public void add_custom_details( ArrayList<HashMap> types,ArrayList<HashMap> status,long id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        ContentValues values;
        try {

            values = new ContentValues();
            for (int i = 0; i < status.size(); i++) {
                values.put(KEY_REF_STATUS_CATEGORY_ID, id);
                values.put(KEY_CAT_STATUS_CODE, status.get(i).get("code").toString());
                values.put(KEY_CAT_STATUS_DESC, status.get(i).get("description").toString());
                values.put(KEY_CAT_STATUS_NAME, status.get(i).get("name").toString());
                values.put(KEY_CAT_STATUS_PRIMARY_COLOR, status.get(i).get("primaryColor").toString());
                values.put(KEY_CAT_STATUS_SECONDARY_COLOR, status.get(i).get("secondaryColor").toString());
                values.put(KEY_CAT_STATUS_CAN_FILTER, status.get(i).get("canFilter").toString());
                values.put(KEY_CAT_STATUS_DEFAULT, status.get(i).get("defaultOn").toString());
                values.put(KEY_CAT_STATUS_NOTIF_CAN_FILTER, status.get(i).get("notificationCanFilter").toString());
                values.put(KEY_CAT_STATUS_NOTIF_DEFAULT, status.get(i).get("notificationDefaultOn").toString());

                db.insert(TABLE_STATUS, null, values);

            }
        } catch (android.database.SQLException e) {
            e.printStackTrace();
        } finally {
            db.setTransactionSuccessful();
            db.endTransaction();

        }
        db.beginTransaction();
        values = new ContentValues();
        try {
            for (int i = 0; i < types.size(); i++) {

                values.put(KEY_REF_TYPE_CATEGORY_ID, id);
                values.put(KEY_CAT_TYPE_CODE, types.get(i).get("code").toString());
                values.put(KEY_CAT_TYPE_NAME, types.get(i).get("name").toString());
                values.put(KEY_CAT_TYPE_ICON, types.get(i).get("icon").toString());
                values.put(KEY_CAT_TYPE_DEFAULT, types.get(i).get("defaultOn").toString());
                values.put(KEY_CAT_TYPE_CAN_FILTER, types.get(i).get("canFilter").toString());
                values.put(KEY_CAT_TYPE_NOTIF_CAN_FILTER, types.get(i).get("notificationCanFilter").toString());
                values.put(KEY_CAT_TYPE_NOTIF_DEFAULT, types.get(i).get("notificationDefaultOn").toString());

                db.insert(TABLE_TYPES, null, values);

            }

        } catch (android.database.SQLException e) {
            e.printStackTrace();
        } finally {
            db.setTransactionSuccessful();
            db.endTransaction();
            db.close();
        }
    }

    public void add_WzData(ArrayList<HashMap> queryValues) {
        SQLiteDatabase db = this.getWritableDatabase();


        db.beginTransaction();
        db.execSQL(CREATE_WATCHZONE_TABLE());
        try {

            ContentValues values = new ContentValues();
            for(int i=0;i<queryValues.size();i++) {

                values.put(KEY_REF_WZ_ID, queryValues.get(i).get(KEY_REF_WZ_ID).toString());
                values.put(KEY_REF_WZ_DEVICE_ID, queryValues.get(i).get(KEY_REF_WZ_DEVICE_ID).toString());
                values.put(KEY_REF_WZ_SOUND, queryValues.get(i).get(KEY_REF_WZ_SOUND).toString());
                values.put(KEY_REF_WZ_ADDRESS, queryValues.get(i).get(KEY_REF_WZ_ADDRESS).toString());
                values.put(KEY_REF_WZ_NAME, queryValues.get(i).get(KEY_REF_WZ_NAME).toString());
                values.put(KEY_REF_WZ_RADIUS, queryValues.get(i).get(KEY_REF_WZ_RADIUS).toString());
                values.put(KEY_REF_WZ_TYPE, queryValues.get(i).get(KEY_REF_WZ_TYPE).toString());
                values.put(KEY_REF_WZ_FILTER, queryValues.get(i).get(KEY_REF_WZ_FILTER).toString());
                values.put(KEY_REF_WZ_FILTERGROUPID, queryValues.get(i).get(KEY_REF_WZ_FILTERGROUPID).toString());

                int enable =  Boolean.valueOf(queryValues.get(i).get(KEY_REF_WZ_ENABLE).toString()) ? 1: 0;
                int proximity =  Boolean.valueOf(queryValues.get(i).get(KEY_REF_WZ_PROXIMITY).toString()) ? 1: 0;
                int isDefault =  Boolean.valueOf(queryValues.get(i).get(KEY_REF_WZ_ISDEFAULT).toString()) ? 1: 0;
                int noEdit =  Boolean.valueOf(queryValues.get(i).get(KEY_REF_WZ_NOEDIT).toString()) ? 1: 0;

                values.put(KEY_REF_WZ_ENABLE, enable);
                values.put(KEY_REF_WZ_PROXIMITY,proximity);
                values.put(KEY_REF_WZ_ISDEFAULT,isDefault);
                values.put(KEY_REF_WZ_NOEDIT, noEdit);
                values.put(KEY_REF_WZ_SHARECODE, queryValues.get(i).get(KEY_REF_WZ_SHARECODE).toString());
                values.put(KEY_REF_WZ_GEOMS, queryValues.get(i).get(KEY_REF_WZ_GEOMS).toString());

                db.insert(TABLE_WATCHZONES, null, values);

            }
        } catch (android.database.SQLException e) {
            e.printStackTrace();
        } finally {
            db.setTransactionSuccessful();
            db.endTransaction();
            db.close();
        }
    }


    public void add_category_default(ArrayList<HashMap> queryValues) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();

        try {

            ContentValues values = new ContentValues();
            for(int i=0;i<queryValues.size();i++) {

                values.put(KEY_DEFAULT_CATEGORY_ID, queryValues.get(i).get("id").toString());
                values.put(KEY_DEFAULT_CATEGORY_NAME, queryValues.get(i).get("name").toString());
                values.put(KEY_DEFAULT_CATEGORY_DESC, queryValues.get(i).get("description").toString());
                values.put(KEY_DEFAULT_CATEGORY_DISPLAY_ON, queryValues.get(i).get("displayOn").toString());
                values.put(KEY_DEFAULT_CATEGORY_DISPLAY_TOGGLE, queryValues.get(i).get("displayToggle").toString());
                values.put(KEY_DEFAULT_CATEGORY_DISPLAY_ONLY, queryValues.get(i).get("displayOnly").toString());
                values.put(KEY_DEFAULT_CATEGORY_FILTER_ON, queryValues.get(i).get("filterOn").toString());
                values.put(KEY_DEFAULT_CATEGORY_FILTER_TOGGLE, queryValues.get(i).get("filterToggle").toString());
                String v=queryValues.get(i).get("displayFilter").toString();
                values.put(KEY_DEFAULT_CATEGORY_DISPLAYFILTER, queryValues.get(i).get("displayFilter").toString());

                db.insert(TABLE_DEFAULT_CATEGORY, null, values);

            }
        } catch (android.database.SQLException e) {
            e.printStackTrace();
        } finally {
            db.setTransactionSuccessful();
            db.endTransaction();
            db.close();
        }
    }

    public ArrayList<HashMap> getDefaultMapFilter() {
        ArrayList<HashMap> data= new ArrayList<HashMap>();

        String default_query ="SELECT * FROM "+TABLE_DEFAULT_CATEGORY ;//+ " WHERE "+KEY_DEFAULT_CATEGORY_DISPLAY_ONLY +" = 'false'";

        ArrayList<HashMap> categories=new ArrayList<HashMap>();
        HashMap<String, String> category=null;
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(default_query, null);
        try {
            if (cursor.moveToFirst()) {
                do {
                    category =  new  HashMap<String, String>();

                    category.put(KEY_DEFAULT_CATEGORY_ID,cursor.getString(cursor.getColumnIndex(KEY_DEFAULT_CATEGORY_ID)));
                    category.put(KEY_DEFAULT_CATEGORY_NAME,cursor.getString(cursor.getColumnIndex(KEY_DEFAULT_CATEGORY_NAME)));
                    category.put(KEY_DEFAULT_CATEGORY_DESC,cursor.getString(cursor.getColumnIndex(KEY_DEFAULT_CATEGORY_DESC)));
                    category.put(KEY_DEFAULT_CATEGORY_DISPLAY_ON,cursor.getString(cursor.getColumnIndex(KEY_DEFAULT_CATEGORY_DISPLAY_ON)));
                    category.put(KEY_DEFAULT_CATEGORY_DISPLAY_ONLY,cursor.getString(cursor.getColumnIndex(KEY_DEFAULT_CATEGORY_DISPLAY_ONLY)));
                    category.put(KEY_DEFAULT_CATEGORY_DISPLAY_TOGGLE,cursor.getString(cursor.getColumnIndex(KEY_DEFAULT_CATEGORY_DISPLAY_TOGGLE)));
                    category.put(KEY_DEFAULT_CATEGORY_FILTER_ON,cursor.getString(cursor.getColumnIndex(KEY_DEFAULT_CATEGORY_FILTER_ON)));
                    category.put(KEY_DEFAULT_CATEGORY_FILTER_TOGGLE,cursor.getString(cursor.getColumnIndex(KEY_DEFAULT_CATEGORY_FILTER_TOGGLE)));
                    category.put(KEY_DEFAULT_CATEGORY_DISPLAYFILTER,cursor.getString(cursor.getColumnIndex(KEY_DEFAULT_CATEGORY_DISPLAYFILTER)));

                    categories.add(category);

                } while(cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.d(TAG, "Error while trying to get posts from database");
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
        return categories;
    }


    public ArrayList<HashMap> getDefaultDataWz() {
        ArrayList<HashMap> data= new ArrayList<HashMap>();

        String default_query ="SELECT * FROM "+TABLE_DEFAULT_CATEGORY + " WHERE "+KEY_DEFAULT_CATEGORY_DISPLAY_ONLY +" = 'false'";

        ArrayList<HashMap> categories=new ArrayList<HashMap>();
        HashMap<String, String> category=null;
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(default_query, null);
        try {
            if (cursor.moveToFirst()) {
                do {
                    category =  new  HashMap<String, String>();

                    category.put(KEY_DEFAULT_CATEGORY_ID,cursor.getString(cursor.getColumnIndex(KEY_DEFAULT_CATEGORY_ID)));
                    category.put(KEY_DEFAULT_CATEGORY_NAME,cursor.getString(cursor.getColumnIndex(KEY_DEFAULT_CATEGORY_NAME)));
                    category.put(KEY_DEFAULT_CATEGORY_DESC,cursor.getString(cursor.getColumnIndex(KEY_DEFAULT_CATEGORY_DESC)));
                    category.put(KEY_DEFAULT_CATEGORY_DISPLAY_ON,cursor.getString(cursor.getColumnIndex(KEY_DEFAULT_CATEGORY_DISPLAY_ON)));
                    category.put(KEY_DEFAULT_CATEGORY_DISPLAY_ONLY,cursor.getString(cursor.getColumnIndex(KEY_DEFAULT_CATEGORY_DISPLAY_ONLY)));
                    category.put(KEY_DEFAULT_CATEGORY_DISPLAY_TOGGLE,cursor.getString(cursor.getColumnIndex(KEY_DEFAULT_CATEGORY_DISPLAY_TOGGLE)));
                    category.put(KEY_DEFAULT_CATEGORY_FILTER_ON,cursor.getString(cursor.getColumnIndex(KEY_DEFAULT_CATEGORY_FILTER_ON)));
                    category.put(KEY_DEFAULT_CATEGORY_FILTER_TOGGLE,cursor.getString(cursor.getColumnIndex(KEY_DEFAULT_CATEGORY_FILTER_TOGGLE)));

                    categories.add(category);

                } while(cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.d(TAG, "Error while trying to get posts from database");
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
        return categories;
    }



    public ArrayList<EditWatchZones> getAllWZ() {

        String default_query ="SELECT * FROM "+TABLE_WATCHZONES;

        ArrayList<EditWatchZones> watchZones=new ArrayList<EditWatchZones>();
        EditWatchZones wz=null;
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(default_query, null);
        try {
            if (cursor.moveToFirst()) {
                do {
                    wz =  new EditWatchZones();

                    wz.setWatchzoneId(cursor.getString(cursor.getColumnIndex(KEY_REF_WZ_ID))); //(KEY_REF_WZ_ID,cursor.getString(cursor.getColumnIndex(KEY_REF_WZ_ID)));
                    wz.setWatchzoneDeviceId(cursor.getString(cursor.getColumnIndex(KEY_REF_WZ_DEVICE_ID)));
                    wz.setWatchzoneSound(cursor.getString(cursor.getColumnIndex(KEY_REF_WZ_SOUND)));
                    wz.setWatchzoneAddress(cursor.getString(cursor.getColumnIndex(KEY_REF_WZ_ADDRESS)));
                    wz.setWatchzoneName(cursor.getString(cursor.getColumnIndex(KEY_REF_WZ_NAME)));
                    wz.setWatchzoneRadius(cursor.getString(cursor.getColumnIndex(KEY_REF_WZ_RADIUS)));
                    wz.setWatchzoneType(cursor.getString(cursor.getColumnIndex(KEY_REF_WZ_TYPE)));

                    Gson gson = new Gson();

                    //ArrayList<HashMap<String, CategoryFilter>> filter = new ArrayList<HashMap<String, CategoryFilter>>();

                    String filters = cursor.getString(cursor.getColumnIndex(KEY_REF_WZ_FILTER));
                    ArrayList<HashMap<String, CategoryFilter>>  filter = gson.fromJson(filters,ArrayList.class );

                    wz.setWatchzoneFilter(filter);
                    String grpIds = cursor.getString(cursor.getColumnIndex(KEY_REF_WZ_FILTERGROUPID));

                    List<Integer> arrrGrpId =  gson.fromJson(grpIds,ArrayList.class);

                    wz.setWatchzoneFilterGroupId(arrrGrpId);

                    Boolean enable =  (cursor.getInt(cursor.getColumnIndex(KEY_REF_WZ_ENABLE)) == 1);
                    Boolean proximity =  (cursor.getInt(cursor.getColumnIndex(KEY_REF_WZ_PROXIMITY)) == 1);
                    Boolean isDefault = (cursor.getInt(cursor.getColumnIndex(KEY_REF_WZ_ISDEFAULT)) == 1);
                    Boolean noEdit = (cursor.getInt(cursor.getColumnIndex(KEY_REF_WZ_ISDEFAULT)) == 1);

                    wz.setWzEnable(enable);
                    wz.setWatchzoneProximity(proximity);
                    wz.setWzDefault(isDefault);
                    wz.setWzNoEdit(noEdit);
                    wz.setWatchZoneShareCode(cursor.getString(cursor.getColumnIndex(KEY_REF_WZ_SHARECODE)));


                    String geoms = cursor.getString(cursor.getColumnIndex(KEY_REF_WZ_GEOMS));

                    WatchZoneGeom arrrGeoms =  gson.fromJson(geoms,WatchZoneGeom.class);

                    wz.setWatchZoneGeoms(arrrGeoms);

                    watchZones.add(wz);

                } while(cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.d(TAG, "Error while trying to get posts from database");
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
        return watchZones;
    }

    public ArrayList[] getCustomCatName(int query) {
        ArrayList<HashMap> types= new ArrayList<HashMap>();
        ArrayList<HashMap> statuses= new ArrayList<HashMap>();
        ArrayList<HashMap> categories=new ArrayList<HashMap>();
        HashMap<String, String> category=null;
        HashMap<String, String> type=null;
        HashMap<String, String> status=null;

        String default_query = null;

        switch(query){
            case 0:
                default_query  ="SELECT * FROM "+TABLE_CATEGORY;

                break;
            case 1:
                default_query  ="SELECT * FROM "+TABLE_CATEGORY + " WHERE "+KEY_CATEGORY_DISPLAY_ONLY +" = 'false'";

                break;
            case 2:
                break;
        }



        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(default_query, null);
        try {
            if (cursor.moveToFirst()) {
                do {
                    category =  new  HashMap<String, String>();
                    category.put(KEY_CATEGORY_ID,cursor.getString(cursor.getColumnIndex(KEY_CATEGORY_ID)));
                    category.put(KEY_CATEGORY,cursor.getString(cursor.getColumnIndex(KEY_CATEGORY)));
                    category.put(KEY_CATEGORY_NAME,cursor.getString(cursor.getColumnIndex(KEY_CATEGORY_NAME)));
                    category.put(KEY_CATEGORY_DESC,cursor.getString(cursor.getColumnIndex(KEY_CATEGORY_DESC)));
                    category.put(KEY_CATEGORY_DISPLAY_ONLY,cursor.getString(cursor.getColumnIndex(KEY_CATEGORY_DISPLAY_ONLY)));
                    category.put(KEY_CATEGORY_FILTER_ORDER,cursor.getString(cursor.getColumnIndex(KEY_CATEGORY_FILTER_ORDER)));
                    categories.add(category);

                } while(cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.e(TAG, "Error while trying to get posts from database");
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }


       String default_type ="SELECT * FROM "+TABLE_TYPES;
        cursor =null;
         cursor = db.rawQuery(default_type, null);
        try {
            if (cursor.moveToFirst()) {
                do {
                    type =  new  HashMap<String, String>();
                    type.put(KEY_REF_TYPE_CATEGORY_ID,cursor.getString(cursor.getColumnIndex(KEY_REF_TYPE_CATEGORY_ID)));
                    type.put(KEY_CAT_TYPE_CODE,cursor.getString(cursor.getColumnIndex(KEY_CAT_TYPE_CODE)));
                    type.put(KEY_CAT_TYPE_NAME,cursor.getString(cursor.getColumnIndex(KEY_CAT_TYPE_NAME)));
                    type.put(KEY_CAT_TYPE_ICON,cursor.getString(cursor.getColumnIndex(KEY_CAT_TYPE_ICON)));
                    type.put(KEY_CAT_TYPE_DEFAULT,cursor.getString(cursor.getColumnIndex(KEY_CAT_TYPE_DEFAULT)));
                    type.put(KEY_CAT_TYPE_CAN_FILTER,cursor.getString(cursor.getColumnIndex(KEY_CAT_TYPE_CAN_FILTER)));
                    type.put(KEY_CAT_TYPE_NOTIF_CAN_FILTER,cursor.getString(cursor.getColumnIndex(KEY_CAT_TYPE_NOTIF_CAN_FILTER)));
                    type.put(KEY_CAT_TYPE_NOTIF_DEFAULT,cursor.getString(cursor.getColumnIndex(KEY_CAT_TYPE_NOTIF_DEFAULT)));
                    types.add(type);

                } while(cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.e(TAG, "Error while trying to get posts from database");
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }


        String default_status ="SELECT * FROM "+TABLE_STATUS;

        cursor =null;
                cursor= db.rawQuery(default_status, null);
        try {
            if (cursor.moveToFirst()) {
                do {
                    status =  new  HashMap<String, String>();

                    status.put(KEY_REF_TYPE_CATEGORY_ID,cursor.getString(cursor.getColumnIndex(KEY_REF_TYPE_CATEGORY_ID)));
                    status.put(KEY_CAT_STATUS_CODE,cursor.getString(cursor.getColumnIndex(KEY_CAT_STATUS_CODE)));
                    status.put(KEY_CAT_STATUS_NAME,cursor.getString(cursor.getColumnIndex(KEY_CAT_STATUS_NAME)));
                    status.put(KEY_CAT_STATUS_DESC,cursor.getString(cursor.getColumnIndex(KEY_CAT_STATUS_DESC)));
                    status.put(KEY_CAT_STATUS_PRIMARY_COLOR,cursor.getString(cursor.getColumnIndex(KEY_CAT_STATUS_PRIMARY_COLOR)));
                    status.put(KEY_CAT_STATUS_SECONDARY_COLOR,cursor.getString(cursor.getColumnIndex(KEY_CAT_STATUS_SECONDARY_COLOR)));
                    status.put(KEY_CAT_STATUS_CAN_FILTER,cursor.getString(cursor.getColumnIndex(KEY_CAT_STATUS_CAN_FILTER)));
                    status.put(KEY_CAT_STATUS_DEFAULT,cursor.getString(cursor.getColumnIndex(KEY_CAT_STATUS_DEFAULT)));
                    status.put(KEY_CAT_STATUS_NOTIF_CAN_FILTER,cursor.getString(cursor.getColumnIndex(KEY_CAT_STATUS_NOTIF_CAN_FILTER)));
                    status.put(KEY_CAT_STATUS_NOTIF_DEFAULT,cursor.getString(cursor.getColumnIndex(KEY_CAT_STATUS_NOTIF_DEFAULT)));
                    statuses.add(status);

                } while(cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.e(TAG, "Error while trying to get posts from database");
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
                db.close();
            }
        }


        return new ArrayList[] {categories,types,statuses};
    }

    public List<String> getCategoriesNames(){
        String default_query ="SELECT * FROM "+TABLE_CATEGORY + " WHERE "+KEY_CATEGORY_DISPLAY_ONLY +" = 'false'";
        List<String> catNames = new ArrayList<>();


        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(default_query, null);
        try {
            if (cursor.moveToFirst()) {
                do {
                    catNames.add(cursor.getString(cursor.getColumnIndex(KEY_CATEGORY)));

                } while(cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.e(TAG, "Error while trying to get posts from database");
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
            db.close();
        }
        return catNames;
    }
//    public ArrayList<HashMap> getCustomCatTypeName(String catId) {
//        ArrayList<HashMap> data= new ArrayList<HashMap>();
//
//        String default_query ="SELECT * FROM "+TABLE_TYPES + " WHERE "+KEY_REF_TYPE_CATEGORY_ID +" = "+catId;
//
//        ArrayList<HashMap> categories=new ArrayList<HashMap>();
//        HashMap<String, String> category=null;
//        SQLiteDatabase db = getReadableDatabase();
//        Cursor cursor = db.rawQuery(default_query, null);
//        try {
//            if (cursor.moveToFirst()) {
//                do {
//                    category =  new  HashMap<String, String>();
//                     category.put(KEY_REF_TYPE_CATEGORY_ID,catId);
//                    category.put(KEY_CAT_TYPE_CODE,cursor.getString(cursor.getColumnIndex(KEY_CAT_TYPE_CODE)));
//                    category.put(KEY_CAT_TYPE_NAME,cursor.getString(cursor.getColumnIndex(KEY_CAT_TYPE_NAME)));
//                    category.put(KEY_CAT_TYPE_ICON,cursor.getString(cursor.getColumnIndex(KEY_CAT_TYPE_ICON)));
//                    category.put(KEY_CAT_TYPE_DEFAULT,cursor.getString(cursor.getColumnIndex(KEY_CAT_TYPE_DEFAULT)));
//                    category.put(KEY_CAT_TYPE_CAN_FILTER,cursor.getString(cursor.getColumnIndex(KEY_CAT_TYPE_CAN_FILTER)));
//                    category.put(KEY_CAT_TYPE_NOTIF_CAN_FILTER,cursor.getString(cursor.getColumnIndex(KEY_CAT_TYPE_NOTIF_CAN_FILTER)));
//                    category.put(KEY_CAT_TYPE_NOTIF_DEFAULT,cursor.getString(cursor.getColumnIndex(KEY_CAT_TYPE_NOTIF_DEFAULT)));
//
//                    categories.add(category);
//
//                } while(cursor.moveToNext());
//            }
//        } catch (Exception e) {
//            Log.e(TAG, "Error while trying to get posts from database");
//            e.printStackTrace();
//        } finally {
//            if (cursor != null && !cursor.isClosed()) {
//                cursor.close();
//            }
//        }
//        return categories;
//    }
//
//    public ArrayList<HashMap> getCustomCatStatusName(String catId) {
//        ArrayList<HashMap> data= new ArrayList<HashMap>();
//
//        String default_query ="SELECT * FROM "+TABLE_STATUS + " WHERE "+KEY_REF_TYPE_CATEGORY_ID +" = "+catId;
//
//        ArrayList<HashMap> categories=new ArrayList<HashMap>();
//        HashMap<String, String> category=null;
//        SQLiteDatabase db = getReadableDatabase();
//        Cursor cursor = db.rawQuery(default_query, null);
//        try {
//            if (cursor.moveToFirst()) {
//                do {
//                    category =  new  HashMap<String, String>();
//
//
//                    categories.add(category);
//
//                } while(cursor.moveToNext());
//            }
//        } catch (Exception e) {
//            Log.e(TAG, "Error while trying to get posts from database");
//            e.printStackTrace();
//        } finally {
//            if (cursor != null && !cursor.isClosed()) {
//                cursor.close();
//            }
//        }
//        return categories;
//    }
    public String CREATE_DEFAULT_CAT_TABLE(){
        return "CREATE TABLE " + TABLE_DEFAULT_CATEGORY +
                "(" +
                KEY_DEFAULT_CATEGORY_ID + " INTEGER, " + // Define a primary key
                KEY_DEFAULT_CATEGORY_NAME + " TEXT, " + // Define a foreign key
                KEY_DEFAULT_CATEGORY_DESC + " TEXT, " +
                KEY_DEFAULT_CATEGORY_DISPLAY_ON + " TEXT, " +
                KEY_DEFAULT_CATEGORY_DISPLAY_TOGGLE + " TEXT, " +
                KEY_DEFAULT_CATEGORY_DISPLAY_ONLY + " TEXT, " +
                KEY_DEFAULT_CATEGORY_FILTER_ON + " TEXT, " +
                KEY_DEFAULT_CATEGORY_FILTER_TOGGLE + " TEXT, "+
                KEY_DEFAULT_CATEGORY_DISPLAYFILTER + " TEXT "+

                ")";
    }
    public String CREATE_CAT_TABLE(){
        return "CREATE TABLE " + TABLE_CATEGORY +
                "(" +
                KEY_CATEGORY_ID + " INTEGER PRIMARY KEY," + // Define a primary key=
                KEY_CATEGORY + " TEXT, " + // Define a primary key
                KEY_CATEGORY_NAME + " TEXT, " + // Define a foreign key
                KEY_CATEGORY_DESC + " TEXT, " +
                KEY_CATEGORY_DISPLAY_ONLY + " TEXT, " +
                KEY_CATEGORY_FILTER_ORDER + " INTEGER "+
                ")";
    }

    public String CREATE_STATUS_TABLE(){
        return "CREATE TABLE " + TABLE_STATUS +
                "(" +
                KEY_REF_STATUS_CATEGORY_ID + " INTEGER," + // Define a primary key
                KEY_CAT_STATUS_CODE + " TEXT, " + // Define a foreign key
                KEY_CAT_STATUS_DESC + " TEXT, " +
                KEY_CAT_STATUS_NAME + " TEXT, " +
                KEY_CAT_STATUS_PRIMARY_COLOR + " TEXT, " +
                KEY_CAT_STATUS_SECONDARY_COLOR + " TEXT, " +
                KEY_CAT_STATUS_CAN_FILTER + " TEXT, " +
                KEY_CAT_STATUS_DEFAULT + " TEXT, " +
                KEY_CAT_STATUS_NOTIF_CAN_FILTER + " TEXT, " +
                KEY_CAT_STATUS_NOTIF_DEFAULT + " TEXT " +
                ")";
    }
    public String CREATE_TYPE_TABLE(){
        return "CREATE TABLE " + TABLE_TYPES +
                "(" +
                KEY_REF_TYPE_CATEGORY_ID + " INTEGER," + // Define a primary key
                KEY_CAT_TYPE_CODE + " TEXT, " + // Define a foreign key
                KEY_CAT_TYPE_NAME + " TEXT, " +
                KEY_CAT_TYPE_ICON + " TEXT, " +
                KEY_CAT_TYPE_DEFAULT + " TEXT, " +
                KEY_CAT_TYPE_CAN_FILTER + " TEXT, " +
                KEY_CAT_TYPE_NOTIF_CAN_FILTER + " TEXT, " +
                KEY_CAT_TYPE_NOTIF_DEFAULT + " TEXT " +
                ")";
    }

    public String CREATE_WATCHZONE_TABLE(){
        return "CREATE TABLE " + TABLE_WATCHZONES +
                "(" +
                KEY_REF_WZ_ID + " INTEGER," + // Define a primary key
                KEY_REF_WZ_DEVICE_ID + " INTEGER, " +
                KEY_REF_WZ_SOUND + " TEXT, " +
                KEY_REF_WZ_ADDRESS + " TEXT, " +
                KEY_REF_WZ_NAME + " TEXT, " +
                KEY_REF_WZ_RADIUS + " TEXT, " +
                KEY_REF_WZ_TYPE + " TEXT, " +
                KEY_REF_WZ_FILTER + " TEXT, " +
                KEY_REF_WZ_FILTERGROUPID + " TEXT, " +
                KEY_REF_WZ_ENABLE + " INTEGER, " +
                KEY_REF_WZ_PROXIMITY + " INTEGER, " +
                KEY_REF_WZ_ISDEFAULT + " INTEGER ," +
                KEY_REF_WZ_NOEDIT + " INTEGER, " +
                KEY_REF_WZ_SHARECODE + " TEXT, " +
                KEY_REF_WZ_GEOMS + " TEXT " +
                ")";
    }

}
