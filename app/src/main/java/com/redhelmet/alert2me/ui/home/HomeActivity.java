package com.redhelmet.alert2me.ui.home;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v7.app.ActionBar;

import com.google.android.gms.maps.model.LatLng;
import com.redhelmet.alert2me.R;
import com.redhelmet.alert2me.ui.base.BaseActivity;
import com.redhelmet.alert2me.ui.home.event.EventFragment;
import com.redhelmet.alert2me.ui.home.help.HelpFragment;
import com.redhelmet.alert2me.ui.home.watchzone.WatchZoneFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by inbox on 13/11/17.
 */

public class HomeActivity extends BaseActivity<HomeViewModel, ActivityHomeBinding> {

    int positionTabSelected = 0;
    LatLng latLng;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_home;
    }

    @Override
    protected Class<HomeViewModel> obtainViewModel() {
        return HomeViewModel.class;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle extras = getIntent().getExtras();
        if (extras != null) { //edit mode - detect if need to zoom on any specific location at start of activity
            latLng = (LatLng) extras.get("marker");
        }

        setupViewPager();
        binder.tabLayout.setupWithViewPager(binder.viewPager);
        if (binder.toolbar != null) {
            setSupportActionBar(binder.toolbar);
        }

        binder.tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                //do stuff here
                positionTabSelected = tab.getPosition();
                switch (positionTabSelected) {
                    case 0:
                        initializeToolbar(getString(R.string.lblEvent) + " " + getString(R.string.lblMap));
                        //startTracking();
                        break;
                    case 1:
                        initializeToolbar(getString(R.string.toolbar_WZ));
                        // stopTracking();
                        break;
                    case 2:
                        initializeToolbar(getString(R.string.toolbar_help));
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
        HomeViewPagerAdapter adapter = new HomeViewPagerAdapter(getSupportFragmentManager());
        adapter.addFrag(new EventFragment(), getString(R.string.tab_events));
        adapter.addFrag(new WatchZoneFragment(), getString(R.string.tab_WZ));
        adapter.addFrag(new HelpFragment(), getString(R.string.tab_help));
        binder.viewPager.setAdapter(adapter);
    }

    public void initializeToolbar(String heading) {
        ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.setDisplayHomeAsUpEnabled(false);
            supportActionBar.setTitle(heading);
        }
    }

    public static class HomeViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public HomeViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFrag(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }
}
