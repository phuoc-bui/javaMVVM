package com.redhelmet.alert2me.ui.eventfilter;

import androidx.lifecycle.ViewModelProvider;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;

import com.redhelmet.alert2me.R;
import com.redhelmet.alert2me.adapters.AppViewPagerAdapter;
import com.redhelmet.alert2me.databinding.ActivityEventFilterBinding;
import com.redhelmet.alert2me.ui.base.BaseActivity;
import com.redhelmet.alert2me.ui.eventfilter.custom.CustomFilterFragment;
import com.redhelmet.alert2me.ui.eventfilter.defaultfilter.DefaultFilterFragment;

import javax.inject.Inject;

public class EventFilterActivity extends BaseActivity<EventFilterViewModel, ActivityEventFilterBinding> {

    @Inject
    ViewModelProvider.Factory factory;

    public int position = 0;
    private AppViewPagerAdapter adapter;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_event_filter;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        obtainViewModel(factory, EventFilterViewModel.class);
        setupViewPager();
        initializeToolbar();
        initializeControls();
    }

    private void setupViewPager() {
        adapter = new AppViewPagerAdapter(getSupportFragmentManager());
        adapter.addFrag(new DefaultFilterFragment(), getString(R.string.lblDefault));
//        adapter.addFrag(new CustomFilterFragment(), getString(R.string.lblCustom));
        binder.viewpager.setAdapter(adapter);
        binder.viewpager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
//                updateOptionsMenu();
//                updateToolbarTitle();
            }
        });
    }

    public void initializeToolbar() {
        setSupportActionBar(binder.toolbar);
        ActionBar supportActionBar = getSupportActionBar();

        if (supportActionBar != null) {
            supportActionBar.setDisplayHomeAsUpEnabled(true);
            supportActionBar.setTitle(Html.fromHtml("<small>Event Map Filter</small>"));
        }
    }


    public void initializeControls() {
        if (viewModel.isDefaultFilter()) {
            binder.viewpager.setCurrentItem(0, false);
        } else {
            binder.viewpager.setCurrentItem(1, false);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.watchzone_static_done, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.done_btn:
                int currentPosition = binder.viewpager.getCurrentItem();
                Fragment fragment = adapter.getItem(currentPosition);
                if (fragment instanceof OnSaveClickListener) {
                    ((OnSaveClickListener) fragment).onSaveClick();
                }
                return true;

            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void showCustomToDefaultAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle(getString(R.string.custom_to_default_popup_display_title))
                .setMessage(R.string.custom_to_default_popup_display)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
//                        simplifyDefaultData(dbController.getDefaultMapFilter(), true);
//                        adapter = new DefaultNotificationAdapter(EventFilterActivity.this, default_data);
//                        exDefault.setAdapter(adapter);
//                        adapter.notifyDataSetChanged();

                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.dismiss();
                    }
                })
                .show();
    }

    public interface OnSaveClickListener {
        void onSaveClick();
    }
}