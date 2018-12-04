package com.phuocbui.mvvm.ui.eventdetail;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.Window;

import com.phuocbui.mvvm.R;
import com.phuocbui.mvvm.data.model.Event;
import com.phuocbui.mvvm.databinding.ActivityEventDetailBinding;
import com.phuocbui.basemodule.ui.base.BaseActivity;

import javax.inject.Inject;

import androidx.appcompat.app.ActionBar;
import androidx.lifecycle.ViewModelProvider;


public class EventDetailsActivity extends BaseActivity<EventDetailViewModel, ActivityEventDetailBinding> {

    private static final String EVENT_EXTRA = "EVENT_EXTRA";

    @Inject
    ViewModelProvider.Factory factory;

    Event event;
    ActionBar supportActionBar;

    public static Intent newInstance(Context context, Event event) {
        Intent intent = new Intent(context, EventDetailsActivity.class);
        Bundle bundle = createDataBundle(event);
        intent.putExtra(BUNDLE_EXTRA, bundle);
        return intent;
    }

    public static Bundle createDataBundle(Event event) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(EVENT_EXTRA, event);
        return bundle;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_event_detail;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        obtainViewModel(factory, EventDetailViewModel.class);
        if (getBundle() != null) { //edit mode
            event = (Event) getBundle().get(EVENT_EXTRA);
        }

        initializeToolbar();

        viewModel.setEvent(event);

    }

    public void initializeToolbar() {
        setSupportActionBar(binder.toolbar);
        supportActionBar = getSupportActionBar();

        if (supportActionBar != null && event != null) {
            supportActionBar.setDisplayHomeAsUpEnabled(true);
            supportActionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor(event.getPrimaryColor())));
            supportActionBar.setTitle(event.getName());
            int color = getResources().getColor(R.color.white);
            if (binder.toolbar.getNavigationIcon() != null)
                binder.toolbar.getNavigationIcon().setColorFilter(color, PorterDuff.Mode.SRC_IN);
            binder.toolbar.setTitleTextColor(color);

            Window window = getWindow();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                window.setStatusBarColor(Color.parseColor(String.valueOf(event.getSecondaryColor())));
            }
        }
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
}
