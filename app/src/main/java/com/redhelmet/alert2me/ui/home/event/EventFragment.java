package com.redhelmet.alert2me.ui.home.event;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.redhelmet.alert2me.R;
import com.redhelmet.alert2me.adapters.AppViewPagerAdapter;
import com.redhelmet.alert2me.core.Constants;
import com.redhelmet.alert2me.databinding.FragmentEventBinding;
import com.redhelmet.alert2me.domain.util.PreferenceUtils;
import com.redhelmet.alert2me.ui.activity.EventMapFilter;
import com.redhelmet.alert2me.ui.base.BaseFragment;


public class EventFragment extends BaseFragment<EventViewModel, FragmentEventBinding> {

    private Menu mOptionsMenu;
    Intent intent;
    int Activity_Filter_Result = 77;
    private int sortByList;
    private MenuItem refreshMenu;
    private Animation rotation;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_event;
    }

    @Override
    protected Class<EventViewModel> getViewModelClass() {
        return EventViewModel.class;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupViewPager();
        rotation = AnimationUtils.loadAnimation(getBaseActivity(), R.anim.rotation);
        disposeBag.add(viewModel.isLoading.asObservable()
                .subscribe(isLoading -> {
                    if (refreshMenu != null && refreshMenu.isVisible()) {
                        View v = refreshMenu.getActionView();
                        if (isLoading) {
                            v.setClickable(false);
                            if (rotation.hasEnded()) v.startAnimation(rotation);
                        }
                        else {
                            v.setClickable(true);
                            v.clearAnimation();
                        }
                    }
                }));
    }

    private void setupViewPager() {
        AppViewPagerAdapter adapter = new AppViewPagerAdapter(getChildFragmentManager());
        adapter.addFrag(MapFragment.newInstance(), getString(R.string.tab_events));
        adapter.addFrag(EventListFragment.newInstance(), getString(R.string.lblList));
        binder.viewpager.setAdapter(adapter);
        binder.viewpager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                updateOptionsMenu();
                String title;
                if (position == 0) {
                    title = getString(R.string.lblEvent) + " " + getString(R.string.lblMap);
                } else {
                    title = getString(R.string.lblEvent) + " " + getString(R.string.lblList);
                }
                getBaseActivity().updateToolbarTitle(title);
            }
        });
    }

    private void ShowSortDialog() {

        final CharSequence[] items = {getString(R.string.listSortOrderDistance), getString(R.string.listSortOrderTime), getString(R.string.listSortOrderStatus)};
        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getBaseActivity());
        int selectedSortItem = 2;

        sortByList = 2;
        dialogBuilder.setTitle(getString(R.string.listSortOrder));

        dialogBuilder.setSingleChoiceItems(items, selectedSortItem, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                sortByList = i;
            }
        });
        dialogBuilder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        dialogBuilder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                PreferenceUtils.saveToPrefs(getBaseActivity(), Constants.SORT_PREFERENCE_KEY, sortByList);
                viewModel.sortList();
                dialogInterface.dismiss();

            }
        });
        AlertDialog dialog = dialogBuilder.create();
        dialog.show();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.event_map, menu);
        this.mOptionsMenu = menu;
        refreshMenu = menu.findItem(R.id.refresh_map);
        if (refreshMenu != null) {
            ImageView imageView = (ImageView) refreshMenu.getActionView();
            imageView.setImageResource(R.drawable.ic_baseline_refresh_24px);
            imageView.setOnClickListener(v -> {
                v.startAnimation(rotation);
                viewModel.onRefresh.run();
            });
        }
        super.onCreateOptionsMenu(menu, inflater);
    }

    private void updateOptionsMenu() {
        if (this.mOptionsMenu != null) {
            onPrepareOptionsMenu(this.mOptionsMenu);
        }
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        if (menu.findItem(R.id.filter_map) != null)
            if (binder.viewpager.getCurrentItem() == 0) {
                menu.findItem(R.id.filter_map).setVisible(true);
                menu.findItem(R.id.refresh_map).setVisible(true);
                menu.findItem(R.id.listOptions).setVisible(false);
            } else {
                menu.findItem(R.id.filter_map).setVisible(false);
                menu.findItem(R.id.refresh_map).setVisible(false);
                menu.findItem(R.id.listOptions).setVisible(true);
            }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.filter_map:
            case R.id.menuFilterList:
                intent = new Intent(getActivity(), EventMapFilter.class);
                startActivityForResult(intent, Activity_Filter_Result);
                return true;

            case R.id.refresh_map:
            case R.id.menuRefreshList:
                viewModel.onRefresh.run();
                return true;

            case R.id.menuSortList:

                ShowSortDialog();
                Log.d("sdf", "menuSortList clicked");
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
