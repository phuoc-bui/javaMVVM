package com.redhelmet.alert2me.ui.home;

import android.arch.lifecycle.ViewModelProvider;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.design.widget.TabLayout;

import com.google.android.gms.maps.model.LatLng;
import com.redhelmet.alert2me.R;
import com.redhelmet.alert2me.adapters.AppViewPagerAdapter;
import com.redhelmet.alert2me.databinding.ActivityHomeBinding;
import com.redhelmet.alert2me.databinding.CustomHomeTabBinding;
import com.redhelmet.alert2me.ui.base.BaseActivity;
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

    int positionTabSelected = 0;
    LatLng latLng;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_home;
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
            latLng = (LatLng) extras.get("marker");
        }

        setupViewPager();

        binder.tabs.setupWithViewPager(binder.viewpager);
        setupTabIcons();

        binder.tabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                //do stuff here
                positionTabSelected = tab.getPosition();
                switch (positionTabSelected) {
                    case 0:
                        updateToolbarTitle(getString(R.string.lblEvent) + " " + getString(R.string.lblMap));
                        hideToolbar(false);
                        //startTracking();
                        break;
                    case 1:
                        updateToolbarTitle(getString(R.string.toolbar_WZ));
                        hideToolbar(false);
                        // stopTracking();
                        break;
                    case 2:
                        updateToolbarTitle(getString(R.string.toolbar_help));
                        hideToolbar(true);
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    private void setupViewPager() {
        AppViewPagerAdapter adapter = new AppViewPagerAdapter(getSupportFragmentManager());
        adapter.addFrag(new EventFragment(), getString(R.string.tab_events));
        adapter.addFrag(new WatchZoneFragment(), getString(R.string.tab_WZ));
        adapter.addFrag(new HelpFragment(), getString(R.string.tab_help));
        binder.viewpager.setAdapter(adapter);
        binder.viewpager.setOffscreenPageLimit(3);
    }

    private void setupTabIcons() {
        CustomHomeTabBinding reportBinding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.custom_home_tab, binder.tabs, false);
        reportBinding.setIcon(R.drawable.ic_report_problem);
        reportBinding.setTitle(getString(R.string.tab_events));
        binder.tabs.getTabAt(0).setCustomView(reportBinding.getRoot());

        CustomHomeTabBinding watchBinding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.custom_home_tab, binder.tabs, false);
        watchBinding.setIcon(R.drawable.ic_watch_zone);
        watchBinding.setTitle(getString(R.string.tab_WZ));
        binder.tabs.getTabAt(1).setCustomView(watchBinding.getRoot());

        CustomHomeTabBinding helpBinding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.custom_home_tab, binder.tabs, false);
        helpBinding.setIcon(R.drawable.ic_help_white);
        helpBinding.setTitle(getString(R.string.tab_help));
        binder.tabs.getTabAt(2).setCustomView(helpBinding.getRoot());
    }
}
