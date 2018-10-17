package com.redhelmet.alert2me.ui.eventdetail;

import android.arch.lifecycle.ViewModelProvider;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.text.Html;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.redhelmet.alert2me.R;
import com.redhelmet.alert2me.data.model.Event;
import com.redhelmet.alert2me.data.model.Section;
import com.redhelmet.alert2me.databinding.ActivityEventDetailBinding;
import com.redhelmet.alert2me.domain.util.DetailSectionBuilder;
import com.redhelmet.alert2me.ui.base.BaseActivity;
import com.redhelmet.alert2me.ui.home.HomeActivity;
import com.redhelmet.alert2me.util.EventUtils;
import com.redhelmet.alert2me.util.IconUtils;

import java.util.List;

import javax.inject.Inject;


public class EventDetailsActivity extends BaseActivity<EventDetailViewModel, ActivityEventDetailBinding> implements GoogleMap.OnMapClickListener, GoogleMap.OnMarkerClickListener, OnMapReadyCallback {

    private static final String EVENT_EXTRA = "EVENT_EXTRA";

    @Inject
    ViewModelProvider.Factory factory;

    Event event;
    ActionBar supportActionBar;
    DetailSectionBuilder detailSectionBuilder;
    GoogleMap mMap;
    private Marker marker;

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

        initializeMap();

        initializeControls();
    }

    public void initializeMap() {

        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.detail_map);
        mapFragment.getMapAsync(EventDetailsActivity.this);
    }

    public void initializeToolbar() {
        setSupportActionBar(binder.toolbar);
        supportActionBar = getSupportActionBar();

        if (supportActionBar != null && event != null) {
            supportActionBar.setDisplayHomeAsUpEnabled(true);
            supportActionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor(event.getPrimaryColor())));
            supportActionBar.setTitle(Html.fromHtml("<small style='color:'" + event.getTextColor() + "''>" + event.getName() + "</small>"));

            Window window = getWindow();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                window.setStatusBarColor(Color.parseColor(String.valueOf(event.getSecondaryColor())));
            }
        }
    }

    public void initializeControls() {
        detailSectionBuilder = new DetailSectionBuilder(getApplicationContext());

        //section
        List<Section> sections = event.getSection();
        if (sections != null && sections.size() > 0) {
            for (Section section : sections) {
                View sectionView = detailSectionBuilder.BuildSection(section);
//                sectionContainer.addView(sectionView);
            }
        } else {
            Toast.makeText(getApplicationContext(), "Event Expired", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(this, HomeActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);

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

    @Override
    public void onMapClick(LatLng latLng) {
        if (marker != null) {
            LatLng position = marker.getPosition();
            Intent o = new Intent(EventDetailsActivity.this, HomeActivity.class);
            o.putExtra("marker", position);
            o.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(o);
        }
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        if (marker != null) {
            LatLng position = marker.getPosition();
            Intent o = new Intent(EventDetailsActivity.this, HomeActivity.class);
            o.putExtra("marker", position);
            o.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(o);
        }
        return true;
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setZoomGesturesEnabled(false);
        mMap.getUiSettings().setRotateGesturesEnabled(false);
        mMap.getUiSettings().setAllGesturesEnabled(false);
        LatLng australia = new LatLng(-24, 133);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(australia, (float) 3.5));
        locationSetup();
    }


    public void locationSetup() {
        if (mMap != null && event != null) {
            MarkerOptions marker = EventUtils.eventToMarker(event, event.getArea().get(0));
            Bitmap eventIcon = IconUtils.createEventIcon(this, R.layout.custom_map_layer_icon, event, event.getPrimaryColor(), false, false, "");
            BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.fromBitmap(eventIcon);
            marker.icon(bitmapDescriptor);

            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(marker.getPosition(), 15f));
            this.marker = mMap.addMarker(marker);
            mMap.setOnMarkerClickListener(this);
            mMap.setOnMapClickListener(this);
        }
    }
}
