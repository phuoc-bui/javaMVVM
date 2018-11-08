package com.redhelmet.alert2me.ui.home;

import androidx.lifecycle.ViewModelProvider;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import android.view.MenuItem;

import com.google.android.gms.maps.model.LatLng;
import com.redhelmet.alert2me.R;
import com.redhelmet.alert2me.databinding.ActivityHomeBinding;
import com.redhelmet.alert2me.ui.base.BaseActivity;
import com.redhelmet.alert2me.ui.base.NavigationFragment;
import com.redhelmet.alert2me.ui.event.EventFragment;
import com.redhelmet.alert2me.ui.help.HelpFragment;
import com.redhelmet.alert2me.ui.watchzone.WatchZoneFragment;

import javax.inject.Inject;

/**
 * Created by inbox on 13/11/17.
 */

public class HomeActivity extends BaseActivity<HomeViewModel, ActivityHomeBinding> {

    @Inject
    ViewModelProvider.Factory factory;

    private LatLng latLng;

    private static final String LAT_LONG_KEY = "lat_long_key";
    
    public static Intent newInstance(Context context, LatLng point) {
        Intent intent = new Intent(context, HomeActivity.class);
        intent.putExtra(LAT_LONG_KEY, point);
        return intent;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_home;
    }

    @Override
    protected int getFragmentContainer() {
        return R.id.fragment_container;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        obtainViewModel(factory, HomeViewModel.class);
        if (binder.toolbar != null) {
            setSupportActionBar(binder.toolbar);
        }
        Bundle extras = getIntent().getExtras();
        if (extras != null) { //edit mode - detect if need to zoom on any specific location at start of activity
            latLng = (LatLng) extras.get(LAT_LONG_KEY);
        }

        if (latLng != null) viewModel.initPoint = latLng;

        // trick to change default item selected, prevent invoking setOnNavigationItemReselectedListener
        // when call setSelectedItemId(R.id.navigation_events);
        binder.navigation.setSelectedItemId(R.id.navigation_help);

        binder.navigation.setOnNavigationItemSelectedListener(menuItem -> {
            switch (menuItem.getItemId()) {
                case R.id.navigation_events:
                    changeFragment(new EventFragment(), false, true);
                    updateToolbarTitle(getString(R.string.lblEvent) + " " + getString(R.string.lblMap));
                    hideToolbar(false);

                    return true;
                case R.id.navigation_watch_zone:
                    changeFragment(new WatchZoneFragment(), false, true);
                    updateToolbarTitle(getString(R.string.toolbar_WZ));
                    hideToolbar(false);
                    return true;
                case R.id.navigation_help:
                    changeFragment(new HelpFragment(), false, true);
                    updateToolbarTitle(getString(R.string.toolbar_help));
                    hideToolbar(true);
                    return true;

                default:
                    return false;
            }
        });
        binder.navigation.setOnNavigationItemReselectedListener(menuItem -> {
        });

        if (savedInstanceState == null) {
            binder.navigation.setSelectedItemId(R.id.navigation_events);
        } else {
            currentFragment = (NavigationFragment) getSupportFragmentManager().findFragmentById(getFragmentContainer());
        }
    }

    @Override
    public void onBackPressed() {
        if (currentFragment == null || !currentFragment.onBackPressed()) super.onBackPressed();
    }

    @Override
    public void onUserInteraction() {
        super.onUserInteraction();
        if (currentFragment != null)
            currentFragment.onUserInteraction();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
