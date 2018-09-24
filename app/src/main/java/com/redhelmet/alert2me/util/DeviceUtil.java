package com.redhelmet.alert2me.util;

import android.content.Context;
import android.os.Build;
import android.provider.Settings;
import android.widget.Toast;

import com.redhelmet.alert2me.R;
import com.redhelmet.alert2me.domain.util.PreferenceUtils;


public class DeviceUtil {

    // prevent init Util class
    private DeviceUtil() {
    }

    public static void copyToClipBoard(Context context, String text) {
        android.content.ClipboardManager clipboard = (android.content.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        android.content.ClipData clip = android.content.ClipData.newPlainText("Copied Text", text);
        if (clipboard != null) clipboard.setPrimaryClip(clip);
        Toast.makeText(context, "Code Copied!", Toast.LENGTH_LONG).show();
    }

    private static String generateDeviceId(Context context) {
        String deviceId = Settings.Secure.getString(context.getContentResolver(),
                Settings.Secure.ANDROID_ID);
        PreferenceUtils.saveToPrefs(context, context.getResources().getString(R.string.pref_device_id), deviceId);
        return deviceId;
        //return "";
    }

    public static String getDeviceName() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        if (model.startsWith(manufacturer)) {
            return StringUtil.capitalize(model);
        } else {
            return StringUtil.capitalize(manufacturer) + " " + model;
        }
    }

    public static String getOsVersion() {
        return Build.VERSION.RELEASE;
    }

    public static String getDeviceId(Context context) {
        if (PreferenceUtils.hasKey(context, context.getResources().getString(R.string.pref_device_id))) {
            return String.valueOf(PreferenceUtils.getFromPrefs(context, context.getResources().getString(R.string.pref_device_id), ""));
        }
        return generateDeviceId(context);
    }

    public static String getDeviceSerial() {
        return Build.SERIAL;
    }


}