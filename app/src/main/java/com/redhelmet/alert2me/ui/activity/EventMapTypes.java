package com.redhelmet.alert2me.ui.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.redhelmet.alert2me.R;
import com.redhelmet.alert2me.adapters.MapNotificationTypeAdapter;
import com.redhelmet.alert2me.data.model.Category;

public class EventMapTypes extends BaseActivity {
    private static final String EXTRA_CATEGORY = "EXTRA_CATEGORY";
    Toolbar toolbar;
    private MapNotificationTypeAdapter mAdapter;
    ListView exTypes;
    private Category selectedCategory;

    public static Intent newInstance(Context context, Category category) {
        Intent intent = new Intent(context, EventMapTypes.class);
        intent.putExtra(EXTRA_CATEGORY, category);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.fragment_edit_static_zone_notification);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            selectedCategory = (Category) extras.getSerializable(EXTRA_CATEGORY);
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
            supportActionBar.setTitle(Html.fromHtml("<small>" + selectedCategory.getNameLabel() + "</small>"));
        }
    }


    public void initializeControls() {


        exTypes = (ListView) findViewById(R.id.customCatTypeList);

        mAdapter = new MapNotificationTypeAdapter(selectedCategory);
        exTypes.setItemsCanFocus(true);
        exTypes.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();


        exTypes.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = AddStaticZoneNotificationStatus.newInstance(EventMapTypes.this, selectedCategory.getTypes().get(position), position);
                startActivity(i);
            }
        });
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
    public void onResume() {
        super.onResume();

        mAdapter.notifyDataSetChanged();

    }
}