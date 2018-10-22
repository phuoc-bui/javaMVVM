package com.redhelmet.alert2me.ui.event;

import android.arch.lifecycle.ViewModelProvider;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.redhelmet.alert2me.R;
import com.redhelmet.alert2me.adapters.RecyclerTouchListener;
import com.redhelmet.alert2me.data.model.Event;
import com.redhelmet.alert2me.databinding.ActivityClusterEventListBinding;
import com.redhelmet.alert2me.ui.base.BaseActivity;
import com.redhelmet.alert2me.ui.eventdetail.EventDetailsActivity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

public class ClusterEventListActivity extends BaseActivity<ClusterEventsViewModel, ActivityClusterEventListBinding> {

    public static final String CLUSTER_EVENT_EXTRA = "CLUSTER_EVENT_EXTRA";

    @Inject
    ViewModelProvider.Factory factory;

    public Context _context;
    public List<Event> _events;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_cluster_event_list;
    }

    public static Intent newInstance(Context context, ArrayList<Event> clusterEvents) {
        Intent intent = new Intent(context, ClusterEventListActivity.class);
        intent.putExtra(CLUSTER_EVENT_EXTRA, clusterEvents);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        obtainViewModel(factory, ClusterEventsViewModel.class);
        initializeToolbar();
        initializeControls();
        //Get all event list
        Serializable data = getIntent().getSerializableExtra(CLUSTER_EVENT_EXTRA);
        if (data instanceof List) {
            viewModel.setEvents((List<Event>) data);
        }
    }

    public void initializeToolbar() {
        setSupportActionBar(binder.toolbar);
        ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.setDisplayHomeAsUpEnabled(true);
            supportActionBar.setTitle("Events");
        }
    }

    public void initializeControls() {
        binder.listCluster.addOnItemTouchListener(new RecyclerTouchListener(_context, binder.listCluster, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                viewModel.onEventClick(position);
                if (_events.size() > 0) {
                    Event event = _events.get(position);

                    if (event != null) {
                        Intent intent = new Intent(getApplicationContext(), EventDetailsActivity.class);
                        intent.putExtra("event", event);
                        startActivity(intent);
                    } else {
                        Toast.makeText(_context, getString(R.string.msgUnableToGetEDetails), Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));
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
