package com.redhelmet.alert2me.ui.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.redhelmet.alert2me.domain.util.PreferenceUtils;
import com.kevalpatel.ringtonepicker.RingtonePickerDialog;
import com.kevalpatel.ringtonepicker.RingtonePickerListener;

import com.redhelmet.alert2me.R;


public class AddStaticZone extends BaseActivity {

    Toolbar toolbar;
    Intent i;
    LinearLayout select_ringtone;
    EditText wz_name;
    TextView notification_sound_text, required_name;
    RingtonePickerDialog.Builder ringtonePickerBuilder;
    Uri _ringtoneURI = null;
    String _ringtoneName = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_static_wz_name);

        ringtonePickerBuilder = new RingtonePickerDialog.Builder(AddStaticZone.this, getSupportFragmentManager());
        ringtonePickerBuilder.addRingtoneType(RingtonePickerDialog.Builder.TYPE_NOTIFICATION);
        ringtonePickerBuilder.setPlaySampleWhileSelection(checkVibrationIsOn(getApplicationContext()));
        ringtonePickerBuilder.setListener(new RingtonePickerListener() {
            @Override
            public void OnRingtoneSelected(String ringtoneName, Uri ringtoneUri) {
                _ringtoneName = ringtoneName;
                _ringtoneURI = ringtoneUri;
                PreferenceUtils.saveToPrefs(getApplicationContext(),getString(R.string.pref_ringtone_name),_ringtoneURI);

                notification_sound_text.setText(ringtoneName);
            }
        });

        initializeToolbar();
        initializeControls();

    }

    private void initializeControls() {


        //removing wz
        if(PreferenceUtils.hasKey(getApplicationContext(),getString(R.string.pref_wz_name)))
            PreferenceUtils.removeFromPrefs(getApplicationContext(),getString(R.string.pref_wz_name));

        if(PreferenceUtils.hasKey(getApplicationContext(),getString(R.string.pref_ringtone_name)))
            PreferenceUtils.removeFromPrefs(getApplicationContext(),getString(R.string.pref_ringtone_name));

        wz_name = (EditText) findViewById(R.id.watch_zone_name);
        wz_name.addTextChangedListener(watchText);
        select_ringtone = (LinearLayout) findViewById(R.id.select_ringtone);
        required_name = (TextView) findViewById(R.id.required_name);
        notification_sound_text = (TextView) findViewById(R.id.notification_sound_text);
        if (_ringtoneURI == null) {
            _ringtoneURI = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        }

        Ringtone ringtone = RingtoneManager.getRingtone(getApplicationContext(), _ringtoneURI);
        _ringtoneName = ringtone.getTitle(getApplicationContext());
        notification_sound_text.setText(ringtone.getTitle(getApplicationContext()));
        select_ringtone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ringtonePickerBuilder.show();
            }
        });
    }

    public void initializeToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }
        ActionBar supportActionBar = getSupportActionBar();

        if (supportActionBar != null) {
            supportActionBar.setDisplayHomeAsUpEnabled(true);
            supportActionBar.setTitle(getString(R.string.lbl_addStaticWZ));

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.watchzone_static_next, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.next_btn:
                if(validation()) {
                    hideSoftKeyBoard(this);
                    i = new Intent(getApplicationContext(), AddStaticZoneLocation.class);
                    startActivity(i);
                }
                return true;
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void hideSoftKeyBoard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getApplicationWindowToken(), 0);
    }
    public static boolean checkVibrationIsOn(Context context) {
        boolean status = false;
        AudioManager am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        switch (am.getRingerMode()) {
            case AudioManager.RINGER_MODE_NORMAL:
                status = true;
                break;
            case AudioManager.RINGER_MODE_SILENT:
                status = false;
                break;
            case AudioManager.RINGER_MODE_VIBRATE:
                status = false;
                break;
        }

        return status;
    }

    @Override
    public void onResume() {
        super.onResume();

        ringtonePickerBuilder.setPlaySampleWhileSelection(checkVibrationIsOn(getApplicationContext()));
    }

    public boolean validation() {
        if(wz_name.getText().toString().matches("^\\s*$") || wz_name.getText().toString().trim().length() == 0)
        {     required_name.setVisibility(View.VISIBLE);
            return false;
        }
        PreferenceUtils.saveToPrefs(getApplicationContext(),getString(R.string.pref_wz_name),wz_name.getText().toString());
        PreferenceUtils.saveToPrefs(getApplicationContext(),getString(R.string.pref_ringtone_name),_ringtoneURI.toString());

        return true;
    }

    TextWatcher watchText = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            required_name.setVisibility(View.INVISIBLE);
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };
}
