//package com.redhelmet.alert2me.ui.fragments;
//
//import android.app.Activity;
//import android.app.Dialog;
//import android.app.ProgressDialog;
//import android.content.Context;
//import android.content.Intent;
//import android.graphics.Point;
//import android.graphics.Rect;
//import android.os.Build;
//import android.os.Bundle;
//import android.support.design.widget.Snackbar;
//import android.support.v4.app.Fragment;
//import android.support.v4.widget.SwipeRefreshLayout;
//import android.support.v7.widget.DividerItemDecoration;
//import android.support.v7.widget.LinearLayoutManager;
//import android.support.v7.widget.RecyclerView;
//import android.support.v7.widget.SwitchCompat;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.Menu;
//import android.view.MenuInflater;
//import android.view.MenuItem;
//import android.view.MotionEvent;
//import android.view.View;
//import android.view.ViewGroup;
//import android.view.Window;
//import android.widget.Button;
//import android.widget.CompoundButton;
//import android.widget.ImageButton;
//import android.widget.ProgressBar;
//import android.widget.RelativeLayout;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import com.android.volley.AuthFailureError;
//import com.android.volley.DefaultRetryPolicy;
//import com.android.volley.NoConnectionError;
//import com.android.volley.Request;
//import com.android.volley.RequestQueue;
//import com.android.volley.Response;
//import com.android.volley.TimeoutError;
//import com.android.volley.VolleyError;
//import com.android.volley.toolbox.JsonObjectRequest;
//import com.daimajia.swipe.util.Attributes;
//import com.nikhilpanju.recyclerviewenhanced.OnActivityTouchListener;
//import com.scalified.fab.ActionButton;
//
//import org.json.JSONArray;
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//import com.redhelmet.alert2me.R;
//import WzListAdapter;
//import DBController;
//import DeviceUtil;
//import RequestHandler;
//import PreferenceUtils;
//import CategoryFilter;
//import CategoryTypeFilter;
//import EditWatchZones;
//import WatchZoneGeom;
//import AddStaticZone;
//import EditWatchZone;
//import HomeActivity;
//import ShareWatchZone;
//
//public class WatchzoneFragment extends Fragment  {
//
//    public SwitchCompat mzSwitch;
//    public ImageButton mzDetail;
//    public TextView infoWzMessage;
//    public View rootView;
//    public Context _context;
//    public Intent i;
//    String WzGetURL;
//    String WzDelURL;
//    String WzTurnOnOffURL;
//    RequestQueue queue;
//    JsonObjectRequest volleyRequest;
//    DeviceUtil deviceUtil;
//    RecyclerView mRecyclerView;
//    SwipeRefreshLayout mSwipeRefreshLayout;
//    WzListAdapter mAdapter;
//    String[] dialogItems;
//    private int openOptionsPosition;
//    private OnActivityTouchListener touchListener;
//    ProgressDialog pdialog;
//    ProgressBar pbdialog;
//    ArrayList <EditWatchZones> wzList;
//    EditWatchZones editWz;
//    DBController dbController;
//    List<String> categoryNamesDB;
//    RelativeLayout watchzoneLayout;
//    Snackbar snackbar=null;
//
//    public WatchzoneFragment() {
//
//    }
//
//    @Override
//    public void onAttach(Context context) {
//        super.onAttach(context);
//        this._context = context;
//
//    }
//
//    @Override
//    public void setUserVisibleHint(boolean visible)
//    {
//        super.setUserVisibleHint(visible);
//        if (visible && isResumed())
//        {
//
//            onResume();
//        }
//    }
//
//    @SuppressWarnings("deprecation")
//    @Override
//    public void onAttach(Activity activity) {
//        super.onAttach(activity);
//
//        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
//            this._context = getActivity();
//        }
//    }
//
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setHasOptionsMenu(true);
//    }
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        rootView = inflater.inflate(R.layout.fragment_watchzone, container, false);
//
//
//
//        initializeControls();
//        initiaizeList();
//        return rootView;
//    }
//
//
//    public void initializeControls() {
//        editWz =EditWatchZones.getInstance();
//        deviceUtil=new DeviceUtil(_context);
//        dbController = new DBController(_context);
//        categoryNamesDB=new ArrayList<>();
//        categoryNamesDB=dbController.getCategoriesNames();
//
//        watchzoneLayout=(RelativeLayout)rootView.findViewById(R.id.layout_watchzone);
//        WzGetURL = _context.getString(R.string.api_url) + "device/" + PreferenceUtils.getFromPrefs(_context, _context.getString(R.string.pref_user_id), "0") + "/watchzones";
//        WzDelURL = _context.getString(R.string.api_url) + "device/" + PreferenceUtils.getFromPrefs(_context, _context.getString(R.string.pref_user_id), "0") + "/watchzones/";
//        WzTurnOnOffURL = _context.getString(R.string.api_url) + "device/" + PreferenceUtils.getFromPrefs(_context, _context.getString(R.string.pref_user_id), "0") + "/watchzones/";
//        mzSwitch = (SwitchCompat) rootView.findViewById(R.id.mzSwitch);
//        mzDetail = (ImageButton) rootView.findViewById(R.id.mzDetailBtn);
//        infoWzMessage = (TextView) rootView.findViewById(R.id.info_message);
//        pbdialog=(ProgressBar)rootView.findViewById(R.id.wzProgress);
//        ActionButton actionButton = (ActionButton) rootView.findViewById(R.id.action_button);
//        actionButton.setButtonColor(getResources().getColor(R.color.fab_material_red_500));
//        actionButton.setButtonColorPressed(getResources().getColor(R.color.fab_material_red_900));
//        actionButton.setType(ActionButton.Type.DEFAULT);
//        actionButton.setImageResource(R.drawable.fab_plus_icon);
//
//
//        mzSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                if (isChecked) {
//                    mzDetail.setVisibility(View.VISIBLE);
//                    infoWzMessage.setText(getString(R.string.wzScreenMessage_2));
//
//                } else {
//                    mzDetail.setVisibility(View.INVISIBLE);
//                    infoWzMessage.setText(getString(R.string.wzScreenMessage_1));
//                }
//
//            }
//
//        });
//
//        mzDetail.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Toast.makeText(_context, "WORKS", Toast.LENGTH_LONG).show();
//            }
//        });
//
//        actionButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                if(PreferenceUtils.hasKey(_context, "wzLocation"))
//                    PreferenceUtils.removeFromPrefs(_context,"wzLocation");
//
//                i = new Intent(_context, AddStaticZone.class);
//                startActivity(i);
//            }
//        });
//
//      //  getWatchZones();
//    }
//
//
//    private void initiaizeList() {
//        pbdialog.setVisibility(View.VISIBLE);
//        mRecyclerView = (RecyclerView) rootView.findViewById(R.id.wz_list);
//        mSwipeRefreshLayout = (SwipeRefreshLayout) rootView.findViewById(R.id.swipeRefreshLayout);
//        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
//            @Override
//            public void onRefresh() {
//                // Refresh items
//                getWatchZones();
//            }
//        });
//
//
//
//        mRecyclerView.setLayoutManager(new LinearLayoutManager(_context));
//        mRecyclerView.addItemDecoration(new DividerItemDecoration(_context, LinearLayoutManager.VERTICAL));
//
//        // mRecyclerView.addItemDecoration(new DividerItemDecoration(_context.getResources().getDrawable(R.drawable.divider)));
//        mRecyclerView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
//            @Override
//            public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
//            switch (rv.getId()){
//                case R.id.wz_delete_row:
//                    Log.e("d",""+rv.getId());
//                    break;
//                case R.id.wz_share_row:
//                    Log.e("r",""+rv.getId());
//                    break;
//                case R.id.rowFG:
//                    Log.e("rehman","dfdfd");
//                    break;
//            }
//                Log.e("hello",""+rv.getId());
//                return false;
//            }
//
//            @Override
//            public void onTouchEvent(RecyclerView rv, MotionEvent e) {
//                Log.e("hello",""+rv.getId());
//            }
//
//            @Override
//            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {
//
//            }
//        });
//        //
//
//
//    }
//
//
//    @Override
//    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
//        inflater.inflate(R.menu.watchzone_main, menu);
//        super.onCreateOptionsMenu(menu, inflater);
//    }
//
//
//
//    public void getWatchZones() {
//
//        showSnack("Fetching Watch Zones...");
//        queue = RequestHandler.getInstance(_context.getApplicationContext()).getRequestQueue(); //Obtain the instance
//
//        volleyRequest = new JsonObjectRequest(Request.Method.GET,WzGetURL,
//                null, new Response.Listener<JSONObject>() {
//            @Override
//            public void onResponse(JSONObject response) {
//                if (response != null) {
//                    try {
//                        if (response.getBoolean("success")) {
//                            changeText("Watch Zones fetched successfully.");
//                            purifyWz(response);
//                             dismisSnackbar();
//                        }
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
////                if(pdialog.isShowing())
////                    pdialog.dismiss();
//                pbdialog.setVisibility(View.INVISIBLE);
//                mSwipeRefreshLayout.setRefreshing(false);
//                infoWzMessage.setVisibility(View.VISIBLE);
//                mRecyclerView.setVisibility(View.INVISIBLE);
//
//                if (error instanceof NoConnectionError) {
//                    //Toast.makeText(_context, getString(R.string.noInternet), Toast.LENGTH_LONG).show();
//                    changeText(getString(R.string.noInternet));
//
//                } else if (error instanceof TimeoutError) {
//                    //Toast.makeText(_context, getString(R.string.timeOut), Toast.LENGTH_LONG).show();
//                    changeText(getString(R.string.timeOut));
//                    error.printStackTrace();
//                }
//                dismisSnackbar();
//
//
//            }
//        });
//
//
//        volleyRequest.setRetryPolicy(new DefaultRetryPolicy(5000,
//                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
//                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
//        queue.add(volleyRequest);
//    }
//
//
//
//    @Override
//    public void onResume() {
//        super.onResume();
//     //   mRecyclerView.addOnItemTouchListener(onTouchListener);
//
//        if (!getUserVisibleHint())
//        {
//            return;
//        }
//        Activity activity = getActivity();
//        if(activity instanceof HomeActivity){
//            HomeActivity home = (HomeActivity) activity;
//            home.initializeToolbar("Watch Zone");
//        }
//
//        mRecyclerView.setVisibility(View.INVISIBLE);
//        pbdialog.setVisibility(View.VISIBLE);
//
//        getWatchZones();
//    }
//
//    @Override
//    public void onPause() {
//        super.onPause();
//       // mRecyclerView.removeOnItemTouchListener(onTouchListener);
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        boolean currentState = false;
//
//        switch (item.getItemId()) {
//            case R.id.menu_swipeable:
//
//                return true;
//            case R.id.menu_clickable:
//
//                return true;
//            case R.id.share:
//
//                i = new Intent(_context, ShareWatchZone.class);
//                startActivity(i);
//                return true;
//            default:
//                return super.onOptionsItemSelected(item);
//        }
//    }
//
//
//
//
//
//    private void purifyWz(JSONObject response) {
//
//
//      wzList =new ArrayList<EditWatchZones>();
//        try {
//            JSONArray wz= response.getJSONArray("watchzones");
//
//
//
//            if(wz.length()>0){
//                for(int i=0;i<wz.length();i++) {
//                    JSONObject data = (JSONObject) wz.get(i);
//
//                    EditWatchZones editModel = new EditWatchZones();
//                    WatchZoneGeom geomModel = new WatchZoneGeom();
//
//                    editModel.setWatchzoneId(data.getString("id"));
//                    editModel.setWatchzoneName(data.getString("name"));
//                    editModel.setWatchzoneDeviceId(data.getString("deviceId"));
//                    editModel.setWatchzoneAddress(data.getString("address"));
//                    editModel.setWatchzoneRadius(data.getString("radius"));
//                    editModel.setWatchzoneType(data.getString("type"));
//
//                    editModel.setWatchzoneProximity(Boolean.valueOf(data.getString("proximity")));
//                    editModel.setWzNoEdit(Boolean.valueOf(data.getString("noEdit")));
//
//
//                    //=== GROUP ID's FILTER
//
//
//
//                   List<Integer> wzFilterGroup=new ArrayList<>();
//                    JSONArray filterGroup =new JSONArray(data.get("filterGroupId").toString());
//
//                    for(int j=0;j<filterGroup.length();j++){
//                        wzFilterGroup.add(Integer.parseInt(filterGroup.get(j).toString()));
//                    }
//
//                    editModel.setWatchzoneFilterGroupId(wzFilterGroup);
//                    //=======///
//
//
//
//                    //==== FILTER
//
//                        List<String> statusCodes;
//                        CategoryTypeFilter categoryTypeFilter = new CategoryTypeFilter();
//                        CategoryFilter categoryFilter = new CategoryFilter();
//
//                        ArrayList<CategoryTypeFilter> filterData = new ArrayList<CategoryTypeFilter>();
//
//                        ArrayList<HashMap<String, CategoryFilter>> filterDetails = new ArrayList<HashMap<String, CategoryFilter>>();
//
//                        JSONObject filterObj = new JSONObject(data.get("filter").toString());
//
//                        for (int j = 0; j < categoryNamesDB.size(); j++) {
//                            for (int f = 0; f < filterObj.length(); f++) {
//
//                                    if(filterObj.has(categoryNamesDB.get(j))) {
//                                        JSONObject categoryObj = filterObj.getJSONObject(categoryNamesDB.get(j));
//                                        JSONArray catTypesArr=categoryObj.getJSONArray("types");
//
//                                        HashMap<String, CategoryFilter> hash = new HashMap<String, CategoryFilter>();
//                                        filterData = new ArrayList<CategoryTypeFilter>();
//                                        categoryFilter = new CategoryFilter();
//
//                                        for (int c = 0; c < catTypesArr.length(); c++) {
//                                            JSONObject typeObj = catTypesArr.getJSONObject(c);
//                                            JSONArray statusArr = typeObj.getJSONArray("status");
//                                            statusCodes = new ArrayList<>();
//                                            for (int p = 0; p < statusArr.length(); p++) {
//
//                                                statusCodes.add(statusArr.get(p).toString());
//                                            }
//                                            categoryTypeFilter = new CategoryTypeFilter();
//                                            categoryTypeFilter.setCode(typeObj.get("code").toString());
//                                            categoryTypeFilter.setStatus(statusCodes);
//                                            filterData.add(categoryTypeFilter);
//
//                                        }
//                                        categoryFilter.setTypes(filterData);
//
//                                        hash.put(categoryNamesDB.get(j), categoryFilter);
//                                        filterDetails.add(hash);
//                                        filterObj.remove(categoryNamesDB.get(j));
//                                    }
//
//
//                                }
//                    }
//
//                    editModel.setWatchzoneFilter(filterDetails);
//
//                    //=====//
//
//                    editModel.setWzDefault(Boolean.valueOf(data.getString("isDefaultFilter")));
//
//                    editModel.setWatchzoneSound(data.getString("sound"));
//                    editModel.setWzEnable(Boolean.valueOf(data.getString("enable")));
//                    editModel.setWatchZoneShareCode(data.getString("shareCode"));
//
//
//                    JSONArray geo=new JSONArray();
//                    geo.put(data.get("geometry"));
//                    JSONObject g=  geo.getJSONObject(0);
//
//                    JSONArray geoArr=new JSONArray(g.get("coordinates").toString());
//                    ArrayList<HashMap<String, Double>> cordinates = new ArrayList<HashMap<String, Double>>();
//                    if(g.get("type").toString().equals("Point")) {
//
//
//                            HashMap<String, Double> hashLoc = new HashMap<>();
//                            hashLoc.put("latitude", Double.parseDouble(geoArr.get(0).toString()));
//                            hashLoc.put("longitude", Double.parseDouble(geoArr.get(1).toString()));
//                            cordinates.add(hashLoc);
//
//
//                    }else{
//                        JSONArray geoArrIn=geoArr.getJSONArray(0);
//                        for (int k = 0; k < geoArrIn.length(); k++) {
//
//                            JSONArray geoA=geoArrIn.getJSONArray(k);
//                            HashMap<String, Double> hashLoc = new HashMap<>();
//                            hashLoc.put("latitude", Double.parseDouble(geoA.get(0).toString()));
//                            hashLoc.put("longitude", Double.parseDouble(geoA.get(1).toString()));
//                            cordinates.add(hashLoc);
//                        }
//                    }
//                    geomModel.setCordinate(cordinates);
//                    geomModel.setType(g.get("type").toString());
//
//                    editModel.setWatchZoneGeoms(geomModel);
//
//                    wzList.add(editModel);
//
//
//                }
//                editWz.setEditWz(wzList);
//
//                Collections.reverse(wzList);
//                infoWzMessage.setVisibility(View.GONE);
//                mAdapter=new WzListAdapter(_context,wzList,WatchzoneFragment.this);
//                mRecyclerView.setVisibility(View.VISIBLE);
//                //mAdapter.setMode(Attributes.Mode.Single));
//                mRecyclerView.setAdapter(mAdapter);
//                mAdapter.setMode(Attributes.Mode.Single);
//                mAdapter.notifyDataSetChanged();
//                mAdapter.setMode(Attributes.Mode.Single);
//
//            }else{
//                mRecyclerView.setVisibility(View.INVISIBLE);
//
//                infoWzMessage.setVisibility(View.VISIBLE);
//            }
//
//        } catch (JSONException e) {
//            e.printStackTrace();
//            mRecyclerView.setVisibility(View.INVISIBLE);
//
//            infoWzMessage.setVisibility(View.VISIBLE);
//        }
//        mSwipeRefreshLayout.setRefreshing(false);
////        if(pdialog.isShowing())
////            pdialog.dismiss();
//        pbdialog.setVisibility(View.INVISIBLE);
//
//    }
//
//
//    public void watchzoneShareCode(final String shareCode){
//         final Dialog dialog = new Dialog(getActivity());
//        dialog.setCancelable(false);
//        View view  = getActivity().getLayoutInflater().inflate(R.layout.custom_share_wz_dialog, null);
//        dialog.setContentView(view);
//        if (dialog != null) {
//            Window window = getActivity().getWindow();
//            Rect displayRectangle = new Rect();
//            window.getDecorView().getWindowVisibleDisplayFrame(displayRectangle);
//            dialog.getWindow().setLayout((int) (displayRectangle.width() * 0.9f),(int) (displayRectangle.height()*0.6f));
//        }
//
//        TextView tv=(TextView)view.findViewById(R.id.watch_zone_share_code);
//        ImageButton copy_btn= (ImageButton) view.findViewById(R.id.copy_to_clipboard);
//        Button ok_btn =(Button)view.findViewById(R.id.accept);
//        tv.setText(shareCode);
//
//        ok_btn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                //Do something
//                dialog.dismiss();
//            }
//        });
//        copy_btn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                //Do something
//                deviceUtil.copyToClipBoard(_context,shareCode); dialog.dismiss();
//            }
//        });
//
//
//        if(shareCode!=null) {
//            dialog.show();
//        }
//    }
//    public static int getScreenWidth(Activity activity) {
//        Point size = new Point();
//        activity.getWindowManager().getDefaultDisplay().getSize(size);
//        return size.x;
//    }
//
//    public static int getScreenHeight(Activity activity) {
//        Point size = new Point();
//        activity.getWindowManager().getDefaultDisplay().getSize(size);
//        return size.y;
//    }
//
//
//    public void deleteWatchZones(String wzId) {
//
//        pbdialog.setVisibility(View.VISIBLE);
//        infoWzMessage.setVisibility(View.INVISIBLE);
//        mRecyclerView.setVisibility(View.INVISIBLE);
//
//        queue = RequestHandler.getInstance(_context.getApplicationContext()).getRequestQueue(); //Obtain the instance
//
//        volleyRequest = new JsonObjectRequest(Request.Method.DELETE, WzDelURL+""+wzId,
//                null, new Response.Listener<JSONObject>() {
//            @Override
//            public void onResponse(JSONObject response) {
//                if (response != null) {
//                    try {
//                        if (response.getBoolean("success")) {
//
//                            onResume();
//
//                        }
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
////                if(pdialog.isShowing())
////                    pdialog.dismiss();
//                pbdialog.setVisibility(View.INVISIBLE);
//                infoWzMessage.setVisibility(View.INVISIBLE);
//                mRecyclerView.setVisibility(View.VISIBLE);
//                mSwipeRefreshLayout.setRefreshing(false);
//                if (error instanceof NoConnectionError) {
//                    Toast.makeText(_context, getString(R.string.noInternet), Toast.LENGTH_LONG).show();
//
//                } else if (error instanceof TimeoutError) {
//                    Toast.makeText(_context, getString(R.string.timeOut), Toast.LENGTH_LONG).show();
//                    error.printStackTrace();
//                }
//
//
//            }
//        }) {
//            /**
//             * Passing some request headers
//             */
//            @Override
//            public Map<String, String> getHeaders() throws AuthFailureError {
//                HashMap<String, String> headers = new HashMap<String, String>();
//                headers.put("Content-Type", "application/json");
//                headers.put("Authorization", "Bearer " + PreferenceUtils.getFromPrefs(_context, getString(R.string.pref_user_token), ""));
//                return headers;
//            }
//        };
//
//
//
//        volleyRequest.setRetryPolicy(new DefaultRetryPolicy(5000,
//                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
//                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
//        queue.add(volleyRequest);
//    }
//
//    public void wzTurnOnOff(String wzId, boolean state_value) {
//        pbdialog.setVisibility(View.VISIBLE);
//        infoWzMessage.setVisibility(View.INVISIBLE);
//        mRecyclerView.setVisibility(View.INVISIBLE);
//
//        String tempUrl=null;
//        if(state_value)
//            tempUrl=  WzTurnOnOffURL+""+ wzId + "/" +"enable";
//        else
//            tempUrl= WzTurnOnOffURL+""+ wzId + "/" +"disable";
//
//        queue = RequestHandler.getInstance(_context.getApplicationContext()).getRequestQueue(); //Obtain the instance
//
//        volleyRequest = new JsonObjectRequest(Request.Method.PUT, tempUrl,
//                null, new Response.Listener<JSONObject>() {
//            @Override
//            public void onResponse(JSONObject response) {
//                if (response != null) {
//                    try {
//                        if (response.getBoolean("success")) {
//
//                            onResume();
//
//                        }
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//        }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
////                if(pdialog.isShowing())
////                    pdialog.dismiss();
//                pbdialog.setVisibility(View.INVISIBLE);
//                infoWzMessage.setVisibility(View.INVISIBLE);
//                mRecyclerView.setVisibility(View.INVISIBLE);
//
//                mSwipeRefreshLayout.setRefreshing(false);
//                if (error instanceof NoConnectionError) {
//                    Toast.makeText(_context, getString(R.string.noInternet), Toast.LENGTH_LONG).show();
//
//                } else if (error instanceof TimeoutError) {
//                    Toast.makeText(_context, getString(R.string.timeOut), Toast.LENGTH_LONG).show();
//                    error.printStackTrace();
//                }
//
//
//            }
//        }) {
//            /**
//             * Passing some request headers
//             */
//            @Override
//            public Map<String, String> getHeaders() throws AuthFailureError {
//                HashMap<String, String> headers = new HashMap<String, String>();
//                headers.put("Content-Type", "application/json");
//                headers.put("Authorization", "Bearer " + PreferenceUtils.getFromPrefs(_context, getString(R.string.pref_user_token), ""));
//                return headers;
//            }
//        };
//
//
//
//        volleyRequest.setRetryPolicy(new DefaultRetryPolicy(5000,
//                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
//                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
//        queue.add(volleyRequest);
//    }
//
//
//    public void EditMode(Boolean wzData,int position){
//      if(wzData) {
//
//          if(PreferenceUtils.hasKey(_context, "wzLocation"))
//              PreferenceUtils.removeFromPrefs(_context,"wzLocation");
//
//
//
//          i = new Intent(_context.getApplicationContext(), EditWatchZone.class);
//          i.putExtra("position",position);
//          i.putExtra("edit",true);
//          startActivity(i);
//      }
//    }
//
//    public void showSnack(String message){
//
//           snackbar = Snackbar
//                    .make(watchzoneLayout, message, Snackbar.LENGTH_INDEFINITE);
//
//            snackbar.show();
//        }
//
//        public void dismisSnackbar(){
//            Thread t = new Thread()
//            {
//                public void run()
//                {
//                    try{
//                        sleep(3000);
//                    }catch(InterruptedException ie)
//                    {
//                        ie.printStackTrace();
//                    }finally
//                    {
//                        if(snackbar.isShown() && snackbar!=null)
//                            snackbar.dismiss();
//                    }
//                }
//            }; t.start();
//
//
//        }
//
//        public void changeText(String message){
//            if(snackbar.isShown() && snackbar!=null)
//            snackbar.setText(message);
//        }
//    }
//
