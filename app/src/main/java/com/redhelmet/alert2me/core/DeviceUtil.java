package com.redhelmet.alert2me.core;

import android.content.Context;
import android.os.Build;
import android.provider.Settings;
import android.widget.Toast;

import com.redhelmet.alert2me.domain.util.PreferenceUtils;

import com.redhelmet.alert2me.R;


public class DeviceUtil {
        private Context _context;

        public DeviceUtil(Context context) {
            _context = context;


        }

        public static void copyToClipBoard(Context context, String text) {
            if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.HONEYCOMB) {
                android.text.ClipboardManager clipboard = (android.text.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                clipboard.setText(text);
            } else {
                android.content.ClipboardManager clipboard = (android.content.ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                android.content.ClipData clip = android.content.ClipData.newPlainText("Copied Text", text);
                clipboard.setPrimaryClip(clip);
            }
            Toast.makeText(context,"Code Copied!",Toast.LENGTH_LONG).show();
        }

        private String GenerateDeviceId() {
            String deviceId = Settings.Secure.getString(_context.getContentResolver(),
                    Settings.Secure.ANDROID_ID);
            PreferenceUtils.saveToPrefs(_context,_context.getResources().getString(R.string.pref_device_id), deviceId);
            return deviceId;
            //return "";
        }

        public String getDeviceName() {
            String manufacturer = Build.MANUFACTURER;
            String model = Build.MODEL;
            if (model.startsWith(manufacturer)) {
                return capitalize(model);
            } else {
                return capitalize(manufacturer) + " " + model;
            }
        }

        public String getOsVersion() {
            return Build.VERSION.RELEASE;
        }

        private String capitalize(String s) {
            if (s == null || s.length() == 0) {
                return "";
            }
            char first = s.charAt(0);
            if (Character.isUpperCase(first)) {
                return s;
            } else {
                return Character.toUpperCase(first) + s.substring(1);
            }
        }

        public String getDeviceId() {
            if (PreferenceUtils.hasKey(_context,_context.getResources().getString(R.string.pref_device_id))) {
                return String.valueOf(PreferenceUtils.getFromPrefs(_context,_context.getResources().getString(R.string.pref_device_id),""));
            }
            return GenerateDeviceId();
        }
        public String getDeviceSerial() {
            return Build.SERIAL;
        }



    }