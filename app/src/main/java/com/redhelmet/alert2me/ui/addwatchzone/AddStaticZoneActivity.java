package com.redhelmet.alert2me.ui.addwatchzone;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.redhelmet.alert2me.databinding.ActivityAddStaticZoneBinding;
import com.redhelmet.alert2me.domain.util.PreferenceUtils;
import com.kevalpatel.ringtonepicker.RingtonePickerDialog;
import com.kevalpatel.ringtonepicker.RingtonePickerListener;

import com.redhelmet.alert2me.R;
import com.redhelmet.alert2me.ui.activity.AddStaticZoneLocation;
import com.redhelmet.alert2me.ui.base.BaseActivity;

import javax.inject.Inject;


public class AddStaticZoneActivity extends BaseActivity<AddStaticZoneViewModel, ActivityAddStaticZoneBinding> {

    LinearLayout select_ringtone;
    EditText wz_name;
    TextView notification_sound_text, required_name;
    RingtonePickerDialog.Builder ringtonePickerBuilder;
    Uri _ringtoneURI = null;
    String _ringtoneName = null;

    @Inject
    ViewModelProvider.Factory factory;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_add_static_zone;
    }

    @Override
    protected int getFragmentContainer() {
        return R.id.fragment_container;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        obtainViewModel(factory, AddStaticZoneViewModel.class);

        ringtonePickerBuilder = new RingtonePickerDialog.Builder(AddStaticZoneActivity.this, getSupportFragmentManager());
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
//        initializeControls();

        changeFragment(new EditStaticZoneNameFragment());
    }

    private void initializeControls() {


        //removing wz
        if(PreferenceUtils.hasKey(getApplicationContext(),getString(R.string.pref_wz_name)))
            PreferenceUtils.removeFromPrefs(getApplicationContext(),getString(R.string.pref_wz_name));

        if(PreferenceUtils.hasKey(getApplicationContext(),getString(R.string.pref_ringtone_name)))
            PreferenceUtils.removeFromPrefs(getApplicationContext(),getString(R.string.pref_ringtone_name));

        wz_name = (EditText) findViewById(R.id.watch_zone_name);
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
        if (binder.toolbar != null) {
            setSupportActionBar(binder.toolbar);
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
                hideKeyboard();
                viewModel.onNextClick();
                return true;
            case android.R.id.home:
                viewModel.onBackClick();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
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
}
