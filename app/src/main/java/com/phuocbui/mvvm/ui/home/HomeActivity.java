package com.phuocbui.mvvm.ui.home;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import com.phuocbui.mvvm.R;
import com.phuocbui.mvvm.data.model.Event;
import com.phuocbui.mvvm.databinding.ActivityHomeBinding;
import com.phuocbui.basemodule.ui.base.BaseActivity;
import com.phuocbui.basemodule.ui.base.NavigationFragment;
import com.phuocbui.mvvm.ui.event.EventListFragment;
import com.phuocbui.mvvm.ui.eventdetail.EventDetailsActivity;
import com.phuocbui.mvvm.ui.help.HelpFragment;
import com.phuocbui.mvvm.ui.watchzone.WatchZoneFragment;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;

/**
 * Created by inbox on 13/11/17.
 */

public class HomeActivity extends BaseActivity<HomeViewModel, ActivityHomeBinding> {

    @Inject
    ViewModelProvider.Factory factory;

    private static final String LAT_LONG_KEY = "lat_long_key";
    private static final String EVENT_KEY = "event_key";
    
    public static Intent newInstance(Context context, Event event) {
        Intent intent = new Intent(context, HomeActivity.class);
        intent.putExtra(EVENT_KEY, event);
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
        Event event = null;
        if (extras != null) { //edit mode - detect if need to zoom on any specific location at start of activity

            event = (Event) extras.getSerializable(EVENT_KEY);
        }


        // trick to change default item selected, prevent invoking setOnNavigationItemReselectedListener
        // when call setSelectedItemId(R.id.navigation_events);
        binder.navigation.setSelectedItemId(R.id.navigation_help);

        binder.navigation.setOnNavigationItemSelectedListener(menuItem -> {
            switch (menuItem.getItemId()) {
                case R.id.navigation_events:
                    changeFragment(new EventListFragment(), false, true);
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

        if (savedInstanceState == null) {
            binder.navigation.setSelectedItemId(R.id.navigation_events);
        } else {
            currentFragment = (NavigationFragment) getSupportFragmentManager().findFragmentById(getFragmentContainer());
        }

        binder.navigation.setOnNavigationItemReselectedListener(menuItem -> {
        });

        // start event detail when receive event from notification
        if (event != null) {
            startActivity(EventDetailsActivity.newInstance(this, event));
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
