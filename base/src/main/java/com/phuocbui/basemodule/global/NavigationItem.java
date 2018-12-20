package com.phuocbui.basemodule.global;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.IntDef;
import androidx.fragment.app.DialogFragment;

import android.view.View;
import android.widget.Toast;

import com.phuocbui.basemodule.ui.base.BaseActivity;
import com.phuocbui.basemodule.ui.base.BaseDialogFragment;
import com.phuocbui.basemodule.ui.base.NavigationFragment;

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
     * Start a web view with data is Uri
     */
    public static final int START_WEB_VIEW = 4;
    /**
     * Show toast with data is String or String resource
     */
    public static final int SHOW_TOAST = 5;
    /**
     * Finish current activity and return result
     */
    public static final int FINISH_AND_RETURN = 6;

    /**
     * Change fragment and add to back stack
     */
    public static final int CHANGE_FRAGMENT = 7;

    /**
     * Return to previous fragment
     */
    public static final int POP_FRAGMENT_BACK = 8;

    public static final int SHOW_DIALOG = 9;

    public static final int DISMISS_DIALOG = 10;

    public static final int SHOW_LOADING_DIALOG = 11;

    public static final int DISMISS_LOADING_DIALOG = 12;

    public static final int START_ACTIVITY_AND_CLEAR_TASK = 13;

    public static final int CHANGE_FRAGMENT_AND_ADD_TO_BACK_STACK = 14;

    public static final int CHANGE_FRAGMENT_AND_CLEAR_BACK_STACK = 15;

    public static final int DIAL = 16;

    /**
     * Start an activity for result with data is class of destination (eg: MainActivity.class)
     */
    public static final int START_ACTIVITY_FOR_RESULT = 17;

    @IntDef({START_ACTIVITY, START_ACTIVITY_AND_FINISH, FINISH, START_WEB_VIEW,
            SHOW_TOAST, FINISH_AND_RETURN, CHANGE_FRAGMENT, POP_FRAGMENT_BACK,
            SHOW_DIALOG, DISMISS_DIALOG, SHOW_LOADING_DIALOG, DISMISS_LOADING_DIALOG,
            START_ACTIVITY_AND_CLEAR_TASK, CHANGE_FRAGMENT_AND_ADD_TO_BACK_STACK,
            CHANGE_FRAGMENT_AND_CLEAR_BACK_STACK, DIAL, START_ACTIVITY_FOR_RESULT})
    @Retention(RetentionPolicy.SOURCE)
    public @interface NavigationType {
    }

    private static final String DEFAULT_DIALOG_TAG = "dialog";

    private Object[] data;
    private final String wrongDataError = "Wrong data($1%s) when navigate from $2%s";
    private int navigationType;

    public NavigationItem(@NavigationType int type, Object... data) {
        this.data = data;
        this.navigationType = type;
    }

    public void navigation(BaseActivity context) {
        switch (navigationType) {
            case START_ACTIVITY:
                startActivity(context, false, data);
                break;
            case START_ACTIVITY_AND_FINISH:
                startActivity(context, false, data);
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
                } else if (data[0] instanceof String) {
                    Uri uri = Uri.parse((String) data[0]);
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
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
                    throw new Error(String.format(wrongDataError, data[0], context.getLocalClassName()));
                }
                break;
            case FINISH_AND_RETURN:
                if (data == null || data.length == 0)
                    throw new Error(String.format(wrongDataError, "null/empty", context.getLocalClassName()));
                if (data[0] instanceof Intent) {
                    context.setResult(Activity.RESULT_OK, (Intent) data[0]);
                    context.finish();
                } else {
                    throw new Error(String.format(wrongDataError, data[0], context.getLocalClassName()));
                }
                break;
            case CHANGE_FRAGMENT:
                changeFragment(context, false, false, data);
                break;

            case POP_FRAGMENT_BACK:
                context.onBackPressed();
                break;

            case SHOW_DIALOG:
                if (data == null || data.length == 0)
                    throw new Error(String.format(wrongDataError, "null/empty", context.getLocalClassName()));
                if (data[0] instanceof BaseDialogFragment) {
                    BaseDialogFragment dialog = (BaseDialogFragment) data[0];
                    String tag = DEFAULT_DIALOG_TAG;

                    View.OnClickListener listener = null;

                    if (data.length > 1 && data[1] instanceof String) tag = (String) data[1];

                    if (data.length > 1 && data[1] instanceof View.OnClickListener)
                        listener = (View.OnClickListener) data[1];

                    if (data.length > 2 && data[2] instanceof View.OnClickListener) {
                        listener = (View.OnClickListener) data[2];
                    }

                    if (listener == null) {
                        dialog.show(context.getSupportFragmentManager(), tag);
                    } else {
                        dialog.show(context.getSupportFragmentManager(), tag, listener);
                    }

                } else {
                    throw new Error(String.format(wrongDataError, data[0].toString(), context.getLocalClassName()));
                }
                break;

            case DISMISS_DIALOG:
                if (data == null || data.length == 0) {
                    DialogFragment dialog = (DialogFragment) context.getSupportFragmentManager().findFragmentByTag(DEFAULT_DIALOG_TAG);
                    if (dialog != null) dialog.dismiss();
                } else if (data[0] instanceof DialogFragment) {
                    DialogFragment dialog = (DialogFragment) data[0];
                    dialog.dismiss();
                } else if (data[0] instanceof String) {
                    DialogFragment dialog = (DialogFragment) context.getSupportFragmentManager().findFragmentByTag((String) data[0]);
                    if (dialog != null) dialog.dismiss();
                } else {
                    throw new Error(String.format(wrongDataError, data[0].toString(), context.getLocalClassName()));
                }
                break;

            case SHOW_LOADING_DIALOG:
                context.showLoadingDialog(true);
                break;

            case DISMISS_LOADING_DIALOG:
                context.showLoadingDialog(false);
                break;
            case START_ACTIVITY_AND_CLEAR_TASK:
                startActivity(context, true, data);
                break;
            case CHANGE_FRAGMENT_AND_ADD_TO_BACK_STACK:
                changeFragment(context, false, true, data);
                break;
            case CHANGE_FRAGMENT_AND_CLEAR_BACK_STACK:
                changeFragment(context, true, false, data);
                break;
            case DIAL:
                if (data == null || data.length == 0)
                    throw new Error(String.format(wrongDataError, "null/empty", context.getLocalClassName()));
                if (data[0] instanceof String) {
                    String uri = "tel:" + data[0];
                    Intent intent = new Intent(Intent.ACTION_DIAL);
                    intent.setData(Uri.parse(uri));
                    context.startActivity(intent);
                } else if (data[0] instanceof Integer) {
                    String uri = "tel:" + context.getString((Integer) data[0]);
                    Intent intent = new Intent(Intent.ACTION_DIAL);
                    intent.setData(Uri.parse(uri));
                    context.startActivity(intent);
                } else {
                    throw new Error(String.format(wrongDataError, data[0], context.getLocalClassName()));
                }
                break;
            case START_ACTIVITY_FOR_RESULT:
                startActivityForResult(context, data);
                break;
        }
    }

    public int getNavigationType() {
        return navigationType;
    }

    private void startActivity(Activity context, boolean clearTask, Object... data) {
        if (data == null || data.length == 0)
            throw new Error(String.format(wrongDataError, "null/empty", context.getLocalClassName()));
        if (data[0] instanceof Class) {
            Intent intent = new Intent(context, (Class<?>) data[0]);
            if (data.length >= 2 && data[1] instanceof Bundle) {
                Bundle bundle = (Bundle) data[1];
                intent.putExtra(BaseActivity.BUNDLE_EXTRA, bundle);
            }
            if (clearTask) {
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            }
            context.startActivity(intent);
        } else if (data[0] instanceof Intent) {
            context.startActivity((Intent) data[0]);
        } else {
            throw new Error(String.format(wrongDataError, data[0], context.getLocalClassName()));
        }
    }

    private void startActivityForResult(Activity context, Object... data) {
        if (data == null || data.length == 0)
            throw new Error(String.format(wrongDataError, "null/empty", context.getLocalClassName()));
        if (data[0] instanceof Class) {
            Intent intent = new Intent(context, (Class<?>) data[0]);

            if (data.length >= 3 && data[2] instanceof Bundle) {
                Bundle bundle = (Bundle) data[2];
                intent.putExtra(BaseActivity.BUNDLE_EXTRA, bundle);
            }

            if (data.length >= 2 && data[1] instanceof Integer) {
                context.startActivityForResult(intent, (Integer) data[1]);
            } else {
                throw new Error("startActivityForResult must has request code.");
            }

        } else {
            throw new Error(String.format(wrongDataError, data[0], context.getLocalClassName()));
        }
    }

    private void changeFragment(BaseActivity context, boolean clearTask, boolean addToTask, Object... data) {
        if (data == null || data.length == 0)
            throw new Error(String.format(wrongDataError, "null/empty", context.getLocalClassName()));
        if (data[0] instanceof NavigationFragment) {
            context.changeFragment((NavigationFragment) data[0], addToTask, clearTask);
        } else {
            throw new Error(String.format(wrongDataError, data[0].toString(), context.getLocalClassName()));
        }
    }
}
