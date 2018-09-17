package com.redhelmet.alert2me.core;


public class Constants {

    //Preferences

    //observations
    public static String observation_first_category_id = "observation_first_category_id";
    public static String observation_second_category_id = "observation_second_category_id";
    public static String observation_third_category_id = "observation_third_category_id";

//Constant variable values
public static String DEFAULT_VALUE_RINGTONE = "content://settings/system/notification_sound";
    public static int DEFAULT_VALUE_RADIUS = 5;
    public static int DISTANCE_PREFERENCE = 5;


    //User Preference keys

    public static String KEY_VALUE_DISTANCEPROXI = "distanceProx";
    public static String KEY_VALUE_ALERTPROXI = "alertProxy";
    public static String KEY_VALUE_FILTERPROXI = "filterProxy";
    public static String KEY_VALUE_CUSTOMFILTERPROXI = "customFilterProxy";
    public static String KEY_VALUE_CUSTOMFILTERPROXIDICTIONARY = "customFilterProxyDictionary" ;
    public static String KEY_VALUE_ENABLEPROXI = "enableProx" ;
    public static String KEY_VALUE_PROXIMITY_DATA = "dictProxi" ;
    public static String KEY_USERLATITUDE =  "userLatitude" ;
    public static String KEY_USERLONGITUDE  = "userLongitude" ;
    public static String KEY_LASTUPDATEDUSERLATITUDE =  "lastUpdatedUserLatitude" ;
    public static String KEY_LASTUPDATEDUSERLONGITUDE  = "lastUpdatedUserLongitude" ;
    public static  String PROXIMITY_MOVEMENT = "ProximityMovementPreferenceKey";
    public static  String PROXIMITY_STILL = "ProximityStillPreferenceKey";
    public static  String SORT_PREFERENCE_KEY = "sortPreferenceKey";



    //ProxiSettingViewController

    //Timer for Proximity
    public static int  SlOWMOTIONTIMER   = 1 * 60000; //15 MINS
    public static int  AUTOMOTIVETIMER  = 6 * 60000; //6 MINS
    public static int  NOTMOVINGVETIMER   = 1 * 60000 ;//30 mins


    public static final String BROADCAST_DETECTED_ACTIVITY = "activity_intent";

    public static final long DETECTION_INTERVAL_IN_MILLISECONDS = 0;

    public static final int CONFIDENCE = 70;

}
