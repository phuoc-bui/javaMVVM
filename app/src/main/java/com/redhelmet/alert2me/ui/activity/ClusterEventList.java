package com.redhelmet.alert2me.ui.activity;

import android.support.v7.app.ActionBar;
import android.os.Bundle;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.redhelmet.alert2me.adapters.EmptyListRecyclerAdapter;
import com.redhelmet.alert2me.adapters.EventListRecyclerAdapter;
import com.redhelmet.alert2me.adapters.RecyclerTouchListener;
import com.redhelmet.alert2me.model.Event;

import java.util.List;

import com.redhelmet.alert2me.R;

public class ClusterEventList extends  BaseActivity {

    RecyclerView listEventIcon;
    EventListRecyclerAdapter mAdapter;
    private View view;
    public Context _context;
    public List<Event> _events;
    ProgressBar mProgress;
    Toolbar toolbar;
    Intent i;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cluster_event_list);

        //Get all event list
        Bundle extras = getIntent().getBundleExtra("bundle");;

        if (extras != null) {
                if(extras.getSerializable("clusterEvents") != null) {
                    _events =( List<Event>) extras.getSerializable("clusterEvents");

                }
        }

        initializeToolbar();
        initializeControls();
        SetEventListDataSource();
    }

//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        //Get all event list
//        Bundle extras = getIntent().getBundleExtra("bundle");;
//
//        if (extras != null) {
//            if(extras.getSerializable("clusterEvents") != null) {
//                extras.remove("clusterEvents");
//
//            }
//        }
//
//    }

    public void initializeToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }
        ActionBar supportActionBar = getSupportActionBar();

        if (supportActionBar != null) {
            supportActionBar.setDisplayHomeAsUpEnabled(true);
            supportActionBar.setTitle("Events");
        }
    }

    public void initializeControls() {
        mProgress = (ProgressBar) findViewById(R.id.toolbar_progress_bar);
        listEventIcon = (RecyclerView) findViewById(R.id.listCluster);
        LinearLayoutManager llm = new LinearLayoutManager(_context);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        listEventIcon.setLayoutManager(llm);
        listEventIcon.addOnItemTouchListener(new RecyclerTouchListener(_context, listEventIcon, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
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

    private void SetEventListDataSource() {
        if (listEventIcon != null) {
            if (_events.size() > 0) {
                mAdapter = new EventListRecyclerAdapter(this, _events, false);
                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(_context);
                listEventIcon.setLayoutManager(mLayoutManager);
                listEventIcon.setItemAnimator(new DefaultItemAnimator());
                listEventIcon.setAdapter(mAdapter);
                mAdapter.notifyDataSetChanged();
            } else {


                String emptyText = _context.getString(R.string.no_data_to_display);

                EmptyListRecyclerAdapter emptyListRecyclerAdapter = new EmptyListRecyclerAdapter(emptyText);
                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(_context);
                listEventIcon.setLayoutManager(mLayoutManager);
                listEventIcon.setItemAnimator(new DefaultItemAnimator());
                listEventIcon.setAdapter(emptyListRecyclerAdapter);
                emptyListRecyclerAdapter.notifyDataSetChanged();
            }
            mProgress.setVisibility(View.INVISIBLE);
        }
    }

}
