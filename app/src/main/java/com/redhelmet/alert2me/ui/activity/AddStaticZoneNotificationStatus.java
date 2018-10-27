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
import android.widget.ListView;
import android.widget.Toast;

import com.redhelmet.alert2me.R;
import com.redhelmet.alert2me.adapters.CustomNotificationStatusAdapter;
import com.redhelmet.alert2me.data.model.CategoryStatus;
import com.redhelmet.alert2me.data.model.CategoryType;

public class AddStaticZoneNotificationStatus extends BaseActivity implements View.OnClickListener {
    public static final String EXTRA_CATEGORY_TYPE = "EXTRA_CATEGORY_TYPE";
    public static final String EXTRA_TYPE_POSITION = "EXTRA_TYPE_POSITION";
    Toolbar toolbar;
    ListView exTypes;
    private CategoryType selectedType;
    private int typePosition;

    public static Intent newInstance(Context context, CategoryType categoryType, int typePosition) {
        Intent intent = new Intent(context, AddStaticZoneNotificationStatus.class);
        intent.putExtra(EXTRA_CATEGORY_TYPE, categoryType);
        intent.putExtra(EXTRA_TYPE_POSITION, typePosition);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_static_wz_notification_types);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            selectedType = (CategoryType) extras.getSerializable(EXTRA_CATEGORY_TYPE);
            typePosition = extras.getInt(EXTRA_TYPE_POSITION);
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
            supportActionBar.setTitle(Html.fromHtml("<small>" + selectedType.getName() + "</small>"));
        }
    }


    public void initializeControls() {

        exTypes = (ListView) findViewById(R.id.customCatTypeList);
        CustomNotificationStatusAdapter mAdapter = new CustomNotificationStatusAdapter(selectedType);
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
                updateSelectedTypeStatus();
                Intent result = new Intent();
                result.putExtra(EXTRA_CATEGORY_TYPE, selectedType);
                result.putExtra(EXTRA_TYPE_POSITION, typePosition);
                setResult(RESULT_OK, result);
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void updateSelectedTypeStatus() {
        int enabledCount = 0;
        for (int i = 0; i < selectedType.getStatuses().size(); i++) {
            CategoryStatus status = selectedType.getStatuses().get(i);
            if (status.isNotificationDefaultOn()) enabledCount++;
        }
        if (enabledCount > 0) {
            selectedType.setNotificationDefaultOn(true);
        }
    }

    @Override
    public void onClick(View v) {

    }
}
