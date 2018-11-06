package com.redhelmet.alert2me.ui.addwatchzone;

import android.content.Context;
import android.media.AudioManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.redhelmet.alert2me.R;
import com.redhelmet.alert2me.data.model.EditWatchZones;
import com.redhelmet.alert2me.databinding.ActivityAddStaticZoneBinding;
import com.redhelmet.alert2me.ui.base.BaseActivity;

import javax.inject.Inject;

import androidx.appcompat.app.ActionBar;
import androidx.lifecycle.ViewModelProvider;


public class AddStaticZoneActivity extends BaseActivity<AddStaticZoneViewModel, ActivityAddStaticZoneBinding> {

    private static final String WATCH_ZONE_BUNDLE_EXTRA = "WATCH_ZONE_BUNDLE_EXTRA";

    Menu menu;

    @Inject
    ViewModelProvider.Factory factory;

    public static Bundle createBundle(EditWatchZones watchZones) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(WATCH_ZONE_BUNDLE_EXTRA, watchZones);
        return bundle;
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

        if (getBundle() != null) {
            EditWatchZones watchZone = (EditWatchZones) getBundle().getSerializable(WATCH_ZONE_BUNDLE_EXTRA);
            if (watchZone != null) {
                viewModel.setWatchZone(watchZone);
            }
        }
        initializeToolbar();

        changeFragment(new EditStaticZoneNameFragment());
    }

    public void initializeToolbar() {
        if (binder.toolbar != null) {
            setSupportActionBar(binder.toolbar);
        }
        ActionBar supportActionBar = getSupportActionBar();

        if (supportActionBar != null) {
            supportActionBar.setDisplayHomeAsUpEnabled(true);
            switch (viewModel.watchZoneModel.mode) {
                case ADD:
                    supportActionBar.setTitle(getString(R.string.lbl_addStaticWZ));
                    break;
                case EDIT:
                    supportActionBar.setTitle(getString(R.string.lbl_editStaticWZ));
                    break;
            }

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        getMenuInflater().inflate(R.menu.watchzone_static_next, menu);
        viewModel.currentStep.observe(this, step -> {
            switch (step) {
                case EDIT_NAME:
                    showOption(R.id.next_btn);
                    hideOption(R.id.save_btn);
                    hideOption(R.id.clear_btn);
                    break;
                case EDIT_LOCATION:
                    showOption(R.id.next_btn);
                    hideOption(R.id.save_btn);
                    showOption(R.id.clear_btn);
                    break;
                case EDIT_NOTIFICATION:
                    hideOption(R.id.next_btn);
                    showOption(R.id.save_btn);
                    hideOption(R.id.clear_btn);
                    break;
            }
        });
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
}
