package com.redhelmet.alert2me.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.redhelmet.alert2me.R;
import com.redhelmet.alert2me.adapters.CustomNotificationTypeAdapter;
import com.redhelmet.alert2me.data.model.Category;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;

public class AddStaticZoneNotificationTypes extends BaseActivity {

    Toolbar toolbar;
    Intent i;
    private CustomNotificationTypeAdapter mAdapter;
    ListView exTypes;
    private int selectedCategory;
    private ArrayList<Category> categories;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_static_wz_notification_types);


        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            selectedCategory = extras.getInt("catId");
        }

        disposeBag.add(dataManager.getCategories()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(list -> {
                    categories = (ArrayList<Category>) list;
                    initializeToolbar();
                    initializeControls();
                }
        ));
    }

    public void initializeToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }
        ActionBar supportActionBar = getSupportActionBar();

        if (supportActionBar != null) {
            supportActionBar.setDisplayHomeAsUpEnabled(true);
            supportActionBar.setTitle(Html.fromHtml("<small>" + categories.get(selectedCategory).getNameLabel() + "</small>"));
        }
    }


    public void initializeControls() {


        exTypes = (ListView) findViewById(R.id.customCatTypeList);

        mAdapter = new CustomNotificationTypeAdapter(this, categories, selectedCategory);
        exTypes.setItemsCanFocus(true);
        exTypes.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();


        exTypes.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //    if (allEventGroup.getCategoryArray().get(selectedCategory).getTypes().get(position).isNotificationDefaultOn()) {


                i = new Intent(getApplicationContext(), AddStaticZoneNotificationStatus.class);
                i.putExtra("typeId", position);
                i.putExtra("catId", selectedCategory);
                startActivity(i);
                //   }
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
}