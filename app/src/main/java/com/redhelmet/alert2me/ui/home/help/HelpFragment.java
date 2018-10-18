package com.redhelmet.alert2me.ui.home.help;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.redhelmet.alert2me.BuildConfig;
import com.redhelmet.alert2me.R;
import com.redhelmet.alert2me.core.CoreFunctions;
import com.redhelmet.alert2me.core.RequestHandler;
import com.redhelmet.alert2me.domain.util.PreferenceUtils;
import com.redhelmet.alert2me.domain.util.Utility;
import com.redhelmet.alert2me.ui.signin.SignInActivity;

import org.json.JSONException;
import org.json.JSONObject;


public class HelpFragment extends Fragment implements View.OnClickListener {

    Snackbar snackbar = null;
    private ViewSwitcher viewSwitcher;
    private View view;
    LinearLayout helpLayout, helpDefaultLayout;
    RelativeLayout optionLayout;
    private Button help_button, option_button, testNotification;
    private ImageButton onlineHelpBtn, emailSupportBtn, aboutBtn, tocBtn;
    LinearLayout watchIntro;
    private Context _context;
    private TextView appSupportCode, appVersionCode;
    Intent intent;
    CoreFunctions cf;
    TextView info2;


    public HelpFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_help, container, false);
        initializationControls();
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this._context = context;
        // Code here
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            // Code here
            this._context = getActivity();
        }
    }

    private void initializationControls() {
        viewSwitcher = (ViewSwitcher) view.findViewById(R.id.helpSwitch);
        optionLayout = (RelativeLayout) view.findViewById(R.id.option_layout);
        helpLayout = (LinearLayout) view.findViewById(R.id.help_layout);
        help_button = (Button) view.findViewById(R.id.helpBtn);
        testNotification = (Button) view.findViewById(R.id.testNotification);
        option_button = (Button) view.findViewById(R.id.optBtn);
        onlineHelpBtn = (ImageButton) view.findViewById(R.id.online_help_btn);
        emailSupportBtn = (ImageButton) view.findViewById(R.id.email_support_btn);
        aboutBtn = (ImageButton) view.findViewById(R.id.about_us_btn);
        tocBtn = (ImageButton) view.findViewById(R.id.terms_condition_btn);
        watchIntro = (LinearLayout) view.findViewById(R.id.watch_intro);
        helpDefaultLayout = (LinearLayout) view.findViewById(R.id.helpDefaultLayout);
        appSupportCode = (TextView) view.findViewById(R.id.app_support_code);
        appVersionCode = (TextView) view.findViewById(R.id.app_version_number);
        info2 = (TextView) view.findViewById(R.id.info2);

        option_button.setOnClickListener(this);
        help_button.setOnClickListener(this);
        onlineHelpBtn.setOnClickListener(this);
        emailSupportBtn.setOnClickListener(this);
        aboutBtn.setOnClickListener(this);
        tocBtn.setOnClickListener(this);
        watchIntro.setOnClickListener(this);
        appVersionCode.setOnClickListener(this);
        appSupportCode.setOnClickListener(this);
        testNotification.setOnClickListener(this);

        appSupportCode.setText((String) PreferenceUtils.getFromPrefs(_context, getString(R.string.pref_user_id), ""));
        appVersionCode.setText(_context.getString(R.string.appVersion));

        info2.setText(getText(R.string.help_email_support_info) + " " + (String) PreferenceUtils.getFromPrefs(_context, getString(R.string.pref_help_support_email), ""));

        cf = new CoreFunctions(_context);

    }

    @Override
    public void onResume() {
        super.onResume();

        if (!getUserVisibleHint()) {
            return;
        }


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.helpBtn:
                if (viewSwitcher.getCurrentView() != helpLayout) {
                    help_button.setBackgroundResource(R.drawable.button_red_bottom_border);
                    option_button.setBackgroundResource(R.drawable.border_shadow);
                    viewSwitcher.showNext();

                }
                break;
            case R.id.optBtn:
                if (viewSwitcher.getCurrentView() != optionLayout) {


                    option_button.setBackgroundResource(R.drawable.button_red_bottom_border);
                    help_button.setBackgroundResource(R.drawable.border_shadow);
                    //  help_button.setBackgroundColor(getResources().getColor(R.color.navigationnarHeaderColor));


                    viewSwitcher.showPrevious();
                }
                break;
            case R.id.online_help_btn:
                openWebPage((String) PreferenceUtils.getFromPrefs(_context, getString(R.string.pref_help_support_url), ""));
                break;
            case R.id.email_support_btn:
                openEmailClient((String) PreferenceUtils.getFromPrefs(_context, getString(R.string.pref_help_support_email), ""));
                break;
            case R.id.about_us_btn:
                openWebPage((String) PreferenceUtils.getFromPrefs(_context, getString(R.string.pref_about_url), ""));
                break;
            case R.id.terms_condition_btn:
                openWebPage((String) PreferenceUtils.getFromPrefs(_context, getString(R.string.pref_terms_and_condition_url), ""));
                break;
            case R.id.watch_intro:
                intent = new Intent(this.getActivity(), SignInActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                break;
            case R.id.testNotification:
                showSnack(getString(R.string.msg_sendNotification));
                sendTestNotification();
                break;


        }
    }

    public void openWebPage(String url) {
        Uri webpage;
        if (url != null && !url.isEmpty()) {
            webpage = Uri.parse(url);
            intent = new Intent(Intent.ACTION_VIEW, webpage);
            getActivity().startActivity(intent);
        }

    }

    public void openEmailClient(String id) {
        if (id != null && !id.isEmpty()) {
            intent = new Intent(Intent.ACTION_SEND);
            intent.setType("plain/text");
            intent.putExtra(Intent.EXTRA_EMAIL, new String[]{id});
            intent.putExtra(Intent.EXTRA_SUBJECT, "subject");
            intent.putExtra(Intent.EXTRA_TEXT, "mail body");
            getActivity().startActivity(Intent.createChooser(intent, ""));
        }
    }

    public void sendTestNotification() {

        if (!Utility.isInternetConnected(_context)) {
            Toast.makeText(_context, getString(R.string.noInternet), Toast.LENGTH_LONG).show();
            return;
        }

        String apiUrl = BuildConfig.API_ENDPOINT + "pns/send/test?device_id=" + (String) PreferenceUtils.getFromPrefs(_context, getString(R.string.pref_user_id), "") + "&appName=" + getString(R.string.appName);
        RequestQueue queue = RequestHandler.getInstance(_context).getRequestQueue(); //Obtain the instance

        JsonObjectRequest volleyRequest = new JsonObjectRequest(Request.Method.GET, apiUrl,
                null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                if (response != null) {
                    try {
                        if (response.getBoolean("success")) {
                            changeText(getString(R.string.msg_notificationSent));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        changeText(getString(R.string.msg_failedNotificationSent));
                    }
                    dismisSnackbar();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                changeText(getString(R.string.timeOut));
                error.printStackTrace();
                dismisSnackbar();


            }
        });

        //TODO: Change the retry policy
        volleyRequest.setRetryPolicy(new DefaultRetryPolicy(5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        queue.add(volleyRequest);
    }

    public void showSnack(String message) {

        snackbar = Snackbar.make(helpDefaultLayout, message, Snackbar.LENGTH_INDEFINITE);
        snackbar.show();
    }

    public void dismisSnackbar() {
        Thread t = new Thread() {
            public void run() {
                try {
                    sleep(3000);
                } catch (InterruptedException ie) {
                    ie.printStackTrace();
                } finally {
                    if (snackbar.isShown() && snackbar != null)
                        snackbar.dismiss();
                }
            }
        };
        t.start();

    }

    public void changeText(String message) {
        if (snackbar.isShown() && snackbar != null)
            snackbar.setText(message);
    }
}
