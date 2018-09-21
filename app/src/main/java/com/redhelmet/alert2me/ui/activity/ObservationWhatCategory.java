package com.redhelmet.alert2me.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.redhelmet.alert2me.core.Constants;
import com.redhelmet.alert2me.domain.util.PreferenceUtils;
import com.redhelmet.alert2me.data.model.ObservationGroups;
import com.redhelmet.alert2me.data.model.ObservationTopics;
import com.redhelmet.alert2me.data.model.ObservationTypes;

import java.util.ArrayList;
import java.util.List;

import com.redhelmet.alert2me.R;

/**
 * Created by inbox on 6/2/18.
 */

public class ObservationWhatCategory extends BaseActivity {

    ListView whatCategoryList;
    List<String> whatValuesCat;
    LinearLayout selectedLayout;
    TextView selectedText;
    int initial=0;
    int positionCat;
    int positionCatSub;
    int positionCatSub2;
    boolean addCat=false;
    Intent intent;
    String value;
    Toolbar toolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_observation_what);

        initializeToolbar();

        whatCategoryList=(ListView)findViewById(R.id.whatCategory);
        selectedLayout = (LinearLayout)findViewById(R.id.selected_heading);
        selectedText = (TextView)findViewById(R.id.selectedWhatValue);

        whatValuesCat=new ArrayList<>();
         for (ObservationTopics topics : observations.getTopics()) {
                whatValuesCat.add(topics.getName());
            }

        Bundle extras = getIntent().getExtras();
        if (extras != null) {

            positionCat=extras.getInt("cat");
            positionCatSub=extras.getInt("catSub");
            initial=extras.getInt("initial");

            if(initial == 1)
                value =observations.getTopics().get(positionCat).getName()+"..";
                        else
                value=observations.getTopics().get(positionCat).getName()+ " " + observations.getTopics().get(positionCat).getGroups().get(positionCatSub).getName()+"..";
            selectedText.setText(value);
            selectedLayout.setVisibility(View.VISIBLE);
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams)whatCategoryList.getLayoutParams();
            params.setMargins(50, 0, 0, 0); //substitute parameters for left, top, right, bottom
            whatCategoryList.setLayoutParams(params);

            whatValuesCat=new ArrayList<>();

        }



        if(initial == 1) {
            for (ObservationGroups groups : observations.getTopics().get(positionCat).getGroups()) {
                whatValuesCat.add(groups.getName());
            }
        }else if(initial!=0){
            for (ObservationTypes types : observations.getTopics().get(positionCat).getGroups().get(positionCatSub).getTypes()) {
                whatValuesCat.add(types.getName());
            }
        }


        
         ArrayAdapter<String> mHistory = new ArrayAdapter<String>(this, R.layout.custom_what_category, R.id.what_category_text,whatValuesCat);
        whatCategoryList.setAdapter(mHistory);

        whatCategoryList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                intent = new Intent(ObservationWhatCategory.this, ObservationWhatCategory.class);

                if(initial == 1) {
                    intent.putExtra("initial", 2);
                   // intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                    intent.putExtra("cat", positionCat);
                    intent.putExtra("catSub", i);
                   // intent.setFlags(Intent.FLAG_ACTIVITY_FORWARD_RESULT);
                    startActivity(intent);



                }else if(initial==2){
                    addCat=true;
                    positionCatSub2=i;
                    selectedText.setText(observations.getTopics().get(positionCat).getName()+ " " + observations.getTopics().get(positionCat).getGroups().get(positionCatSub).getTypes().get(i).getName());

                }else{
                    intent.putExtra("initial", 1);
                    //intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                    intent.putExtra("cat", i);
                    intent.putExtra("catSub", 0);
                   // intent.setFlags(Intent.FLAG_ACTIVITY_FORWARD_RESULT);
                    startActivity(intent);

                }


            }
        });
    }

    public void initializeToolbar() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            setSupportActionBar(toolbar);
        }
        ActionBar supportActionBar = getSupportActionBar();

        if (supportActionBar != null) {
            supportActionBar.setDisplayHomeAsUpEnabled(true);

                supportActionBar.setTitle("Select Category");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        if(initial==2)
        inflater.inflate(R.menu.observation_what, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.add_observation:

                if(addCat) {
                   intent = new Intent(ObservationWhatCategory.this,AddObservation.class);
                    PreferenceUtils.saveToPrefs(getApplicationContext(), Constants.observation_first_category_id,positionCat);
                    PreferenceUtils.saveToPrefs(getApplicationContext(), Constants.observation_second_category_id,positionCatSub);
                    PreferenceUtils.saveToPrefs(getApplicationContext(), Constants.observation_third_category_id,positionCatSub2);

                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    setResult(Activity.RESULT_OK, intent);
                   // setResult(RESULT_OK, intent);
                    startActivity(intent);
                    finish();

                }

                return true;
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
