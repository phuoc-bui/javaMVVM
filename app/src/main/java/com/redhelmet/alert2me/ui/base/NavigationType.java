package com.redhelmet.alert2me.ui.base;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

/**
 * Type of navigation, using in ViewModel, must call setData to set destination (class, Uri, ...)
 * which Activity will uses for onNavigationEvent method
 */
public enum NavigationType {

    /**
     * Start and activity with data is class of destination (eg: MainActivity.class)
     * TODO: add more data: animation, flag, ...
     */
    START_ACTIVITY {
        @Override
        public void navigation(Activity context) {
            if (data.length >= 1 && data[0] instanceof Class) {
                Intent intent = new Intent(context, (Class<?>) data[0]);
                if (data.length >= 2 && data[1] instanceof Bundle) {
                    Bundle bundle = (Bundle) data[1];
                    intent.putExtra(BaseActivity.BUNDLE_EXTRA, bundle);
                }
                context.startActivity(intent);
            } else {
                throw new Error("Wrong data");
            }
        }
    },
    /**
     * Start and activity with data is class of destination (eg: MainActivity.class) and finish current activity
     */
    START_ACTIVITY_AND_FINISH {
        @Override
        public void navigation(Activity context) {
            START_ACTIVITY.navigation(context);
            context.finish();
        }
    },
    FINISH {
        @Override
        public void navigation(Activity context) {
            context.finish();
        }
    },
    START_WEB_VIEW {
        @Override
        public void navigation(Activity context) {
            if (data[0] instanceof Uri) {
                Intent intent = new Intent(Intent.ACTION_VIEW, (Uri) data[0]);
                context.startActivity(intent);
            } else {
                throw new Error(String.format(wrongDataError, context.getLocalClassName(), data[0].toString()));
            }
        }
    },
    SHOW_TOAST {
        @Override
        public void navigation(Activity context) {
            if (data[0] instanceof Integer) {
                Toast.makeText(context, (int) data[0], Toast.LENGTH_SHORT).show();
            } else if (data[0] instanceof String) {
                Toast.makeText(context, (String) data[0], Toast.LENGTH_SHORT).show();
            } else {
                throw new Error(String.format(wrongDataError, context.getLocalClassName(), data[0].toString()));
            }
        }
    };

    Object[] data;
    final String wrongDataError = "Wrong data($2%s) when navigate from $1%s";

    public NavigationType setData(Object... data) {
        this.data = data;
        return this;
    }

    public abstract void navigation(Activity context);
}
