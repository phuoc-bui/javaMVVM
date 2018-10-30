package com.redhelmet.alert2me.ui.addwatchzone;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kevalpatel.ringtonepicker.RingtonePickerDialog;
import com.kevalpatel.ringtonepicker.RingtonePickerListener;
import com.redhelmet.alert2me.R;
import com.redhelmet.alert2me.data.model.EditWatchZones;
import com.redhelmet.alert2me.databinding.ActivityAddStaticZoneBinding;
import com.redhelmet.alert2me.domain.util.PreferenceUtils;
import com.redhelmet.alert2me.ui.base.BaseActivity;

import javax.inject.Inject;

import androidx.appcompat.app.ActionBar;
import androidx.lifecycle.ViewModelProvider;


public class AddStaticZoneActivity extends BaseActivity<AddStaticZoneViewModel, ActivityAddStaticZoneBinding> {

    private static final String WATCH_ZONE_BUNDLE_EXTRA = "WATCH_ZONE_BUNDLE_EXTRA";

    LinearLayout select_ringtone;
    EditText wz_name;
    TextView notification_sound_text, required_name;
    RingtonePickerDialog.Builder ringtonePickerBuilder;
    Uri _ringtoneURI = null;
    String _ringtoneName = null;
    Menu menu;

    @Inject
    ViewModelProvider.Factory factory;

    public static Intent newInstance(Context context) {
        return newInstance(context, null);
    }

    public static Intent newInstance(Context context, EditWatchZones watchZone) {
        Intent i = new Intent(context, AddStaticZoneActivity.class);
        i.putExtra(WATCH_ZONE_BUNDLE_EXTRA, watchZone);
        return i;
    }

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

        EditWatchZones watchZone = (EditWatchZones) getIntent().getSerializableExtra(WATCH_ZONE_BUNDLE_EXTRA);
        if (watchZone != null) {
            viewModel.setWatchZone(watchZone);
        }

        ringtonePickerBuilder = new RingtonePickerDialog.Builder(AddStaticZoneActivity.this, getSupportFragmentManager());
        ringtonePickerBuilder.addRingtoneType(RingtonePickerDialog.Builder.TYPE_NOTIFICATION);
        ringtonePickerBuilder.setPlaySampleWhileSelection(checkVibrationIsOn(getApplicationContext()));
        ringtonePickerBuilder.setListener(new RingtonePickerListener() {
            @Override
            public void OnRingtoneSelected(String ringtoneName, Uri ringtoneUri) {
                _ringtoneName = ringtoneName;
                _ringtoneURI = ringtoneUri;
                PreferenceUtils.saveToPrefs(getApplicationContext(), getString(R.string.pref_ringtone_name), _ringtoneURI);

                notification_sound_text.setText(ringtoneName);
            }
        });

        initializeToolbar();
//        initializeControls();

        changeFragment(new EditStaticZoneNameFragment());
    }

    private void initializeControls() {


        wz_name = (EditText) findViewById(R.id.watch_zone_name);
        select_ringtone = (LinearLayout) findViewById(R.id.select_ringtone);
        required_name = (TextView) findViewById(R.id.required_name);
        notification_sound_text = (TextView) findViewById(R.id.notification_sound_text);

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

        viewModel.currentStep.observe(this, step -> {
            switch (step) {
                case EDIT_NAME:
                case EDIT_LOCATION:
                    showOption(R.id.next_btn);
                    hideOption(R.id.save_btn);
                    break;
                case EDIT_NOTIFICATION:
                    hideOption(R.id.next_btn);
                    showOption(R.id.save_btn);
                    break;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
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
                hideKeyboard();
                viewModel.onBackClick();
                return true;
            case R.id.save_btn:
                hideKeyboard();
                viewModel.onSaveClick();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void hideOption(int id) {
        if (menu != null) {
            MenuItem item = menu.findItem(id);
            item.setVisible(false);
        }
    }

    private void showOption(int id) {
        if (menu != null) {
            MenuItem item = menu.findItem(id);
            item.setVisible(true);
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
