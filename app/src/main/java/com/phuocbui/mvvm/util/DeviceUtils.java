package com.phuocbui.mvvm.util;

import android.content.Context;
import android.os.Build;
import android.widget.Toast;


public class DeviceUtils {

    // prevent init Util class
    private DeviceUtils() {
    }

    public static void copyToClipBoard(Context context, String text) {
        android.content.ClipboardManager clipboard = (android.content.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        android.content.ClipData clip = android.content.ClipData.newPlainText("Copied Text", text);
        if (clipboard != null) clipboard.setPrimaryClip(clip);
        Toast.makeText(context, "Code Copied!", Toast.LENGTH_LONG).show();
    }

    public static String getDeviceName() {
        String manufacturer = Build.MANUFACTURER;
        String model = Build.MODEL;
        if (model.startsWith(manufacturer)) {
            return StringUtils.capitalize(model);
        } else {
            return StringUtils.capitalize(manufacturer) + " " + model;
        }
    }

    public static String getOsVersion() {
        return Build.VERSION.RELEASE;
    }
}