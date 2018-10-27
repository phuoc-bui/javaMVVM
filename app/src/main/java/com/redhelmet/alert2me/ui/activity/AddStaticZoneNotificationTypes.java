package com.redhelmet.alert2me.ui.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import com.redhelmet.alert2me.R;
import com.redhelmet.alert2me.adapters.CustomNotificationTypeAdapter;
import com.redhelmet.alert2me.data.model.Category;
import com.redhelmet.alert2me.data.model.CategoryType;

public class AddStaticZoneNotificationTypes extends BaseActivity {
    public static final String EXTRA_CATEGORY = "EXTRA_CATEGORY";
    public static final String EXTRA_CATEGORY_INDEX = "EXTRA_CATEGORY_INDEX";
    private static final int REQUEST_CATEGORY_TYPE = 9;

    Toolbar toolbar;
    ListView exTypes;
    private Category selectedCategory;
    private int categoryIndex;

    public static Intent newInstance(Context context, Category category, int index) {
        Intent intent = new Intent(context, AddStaticZoneNotificationTypes.class);
        intent.putExtra(EXTRA_CATEGORY, category);
        intent.putExtra(EXTRA_CATEGORY_INDEX, index);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_static_wz_notification_types);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            selectedCategory = (Category) extras.getSerializable(EXTRA_CATEGORY);
            categoryIndex = extras.getInt(EXTRA_CATEGORY_INDEX);
        }
        initializeToolbar();
        initializeControls();
    }

    public void initializeToolbar() {
        toolbar = findViewById(R.id.toolbar);
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
        exTypes = findViewById(R.id.customCatTypeList);

        CustomNotificationTypeAdapter mAdapter = new CustomNotificationTypeAdapter(selectedCategory);
        exTypes.setItemsCanFocus(true);
        exTypes.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();

        exTypes.setOnItemClickListener((parent, view, position, id) -> {
            Intent i = AddStaticZoneNotificationStatus.newInstance(AddStaticZoneNotificationTypes.this, selectedCategory.getTypes().get(position), position);
            startActivityForResult(i, REQUEST_CATEGORY_TYPE);
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
                Intent result = new Intent();
                result.putExtra(EXTRA_CATEGORY, selectedCategory);
                result.putExtra(EXTRA_CATEGORY_INDEX, categoryIndex);
                setResult(RESULT_OK, result);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == REQUEST_CATEGORY_TYPE && resultCode == Activity.RESULT_OK) {
            CategoryType selectedType = (CategoryType) data.getSerializableExtra(AddStaticZoneNotificationStatus.EXTRA_CATEGORY_TYPE);
            int position = data.getIntExtra(AddStaticZoneNotificationStatus.EXTRA_TYPE_POSITION, 0);

            selectedCategory.getTypes().set(position, selectedType);
        }
    }
}