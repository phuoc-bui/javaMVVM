package com.redhelmet.alert2me.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.redhelmet.alert2me.R;
import com.redhelmet.alert2me.adapters.CustomNotificationStatusAdapter;
import com.redhelmet.alert2me.data.model.Category;

import java.util.ArrayList;

public class EventMapStatus extends BaseActivity implements View.OnClickListener {

    Toolbar toolbar;
    Intent i;
    private CustomNotificationStatusAdapter mAdapter;
    ListView exTypes;
    ArrayList<Category> categories;
    int selectedType;
    int selectedCategory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_static_wz_notification_types);
        categories = (ArrayList<Category>) dataManager.getCategoriesSync();

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            selectedType = extras.getInt("typeId");
            selectedCategory = extras.getInt("catId");

        }
        initializeToolbar();
        initializeControls();

    }

    public void initializeToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }
        ActionBar supportActionBar = getSupportActionBar();

        if (supportActionBar != null) {
            supportActionBar.setDisplayHomeAsUpEnabled(true);
            supportActionBar.setTitle(Html.fromHtml("<small>" + categories.get(selectedCategory).getTypes().get(selectedType).getName() + "</small>"));
        }
    }


    public void initializeControls() {

        exTypes = (ListView) findViewById(R.id.customCatTypeList);
        mAdapter = new CustomNotificationStatusAdapter(EventMapStatus.this, categories, selectedCategory, selectedType);
        exTypes.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.watchzone_static_done, menu);
        menu.getItem(0).setVisible(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.done_btn:
                Toast.makeText(getApplicationContext(),
                        "next static",
                        Toast.LENGTH_SHORT).show();

                return true;

            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    public void onClick(View v) {

    }
}
