package com.redhelmet.alert2me.ui.base;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.IntDef;
import android.widget.Toast;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class NavigationItem {
    /**
     * Start an activity with data is class of destination (eg: MainActivity.class)
     */
    public static final int START_ACTIVITY = 1;
    /**
     * Start an activity with data is class of destination (eg: MainActivity.class) and finish current activity
     */
    public static final int START_ACTIVITY_AND_FINISH = 2;
    /**
     * Finish current activity
     */
    public static final int FINISH = 3;
    /**
     * Start an web view with data is Uri
     */
    public static final int START_WEB_VIEW = 4;
    /**
     * Show toast with data is String or String resource
     */
    public static final int SHOW_TOAST = 5;

    @IntDef({START_ACTIVITY, START_ACTIVITY_AND_FINISH, FINISH, START_WEB_VIEW, SHOW_TOAST})
    @Retention(RetentionPolicy.SOURCE)
    public @interface NavigationType {
    }

    private Object[] data;
    private final String wrongDataError = "Wrong data($1%s) when navigate from $2%s";
    private int navigationType;

    public NavigationItem(@NavigationType int type, Object... data) {
        this.data = data;
        this.navigationType = type;
    }

    public void navigation(Activity context) {
        switch (navigationType) {
            case START_ACTIVITY:
                startActivity(context, data);
                break;
            case START_ACTIVITY_AND_FINISH:
                startActivity(context, data);
                context.finish();
                break;
            case FINISH:
                context.finish();
                break;
            case START_WEB_VIEW:
                if (data == null || data.length == 0)
                    throw new Error(String.format(wrongDataError, "null/empty", context.getLocalClassName()));
                if (data[0] instanceof Uri) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, (Uri) data[0]);
                    context.startActivity(intent);
                } else {
                    throw new Error(String.format(wrongDataError, data[0].toString(), context.getLocalClassName()));
                }
                break;
            case SHOW_TOAST:
                if (data == null || data.length == 0)
                    throw new Error(String.format(wrongDataError, "null/empty", context.getLocalClassName()));
                if (data[0] instanceof Integer) {
                    Toast.makeText(context, (int) data[0], Toast.LENGTH_SHORT).show();
                } else if (data[0] instanceof String) {
                    Toast.makeText(context, (String) data[0], Toast.LENGTH_SHORT).show();
                } else {
                    throw new Error(String.format(wrongDataError, data[0].toString(), context.getLocalClassName()));
                }
                break;
        }
    }

    private void startActivity(Activity context, Object... data) {
        if (data == null || data.length == 0)
            throw new Error(String.format(wrongDataError, "null/empty", context.getLocalClassName()));
        if (data[0] instanceof Class) {
            Intent intent = new Intent(context, (Class<?>) data[0]);
            if (data.length >= 2 && data[1] instanceof Bundle) {
                Bundle bundle = (Bundle) data[1];
                intent.putExtra(BaseActivity.BUNDLE_EXTRA, bundle);
            }
            context.startActivity(intent);
        } else {
            throw new Error(String.format(wrongDataError, data[0].toString(), context.getLocalClassName()));
        }
    }
}
