package com.redhelmet.alert2me.adapters;

import android.content.Context;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.daimajia.swipe.SwipeLayout;
import com.daimajia.swipe.adapters.RecyclerSwipeAdapter;
import com.daimajia.swipe.util.Attributes;

import java.util.ArrayList;

import com.redhelmet.alert2me.R;
import com.redhelmet.alert2me.data.model.EditWatchZones;
import com.redhelmet.alert2me.ui.watchzone.WatchZoneFragment;

/**
 * Created by inbox on 30/11/17.
 */

public class WzListAdapter extends RecyclerSwipeAdapter<WzListAdapter.WzRowHolder>{

    LayoutInflater inflater;
    ArrayList<EditWatchZones> wzList ;
    Context mContext;
    WatchZoneFragment fragment;

    public WzListAdapter(Context context, ArrayList<EditWatchZones> list, WatchZoneFragment watchzoneFragment) {
        inflater = LayoutInflater.from(context);
        wzList = list;
        mContext=context;
        this.fragment = watchzoneFragment;

    }

    @Override
    public void setMode(Attributes.Mode mode) {
        mItemManger.setMode(Attributes.Mode.Single);
    }

    @Override
    public WzRowHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.wz_list_custom_row, parent, false);
        return new WzRowHolder(view);
    }

    @Override
    public void onBindViewHolder(final WzRowHolder holder, final int position) {

        holder.swipeLayout.setShowMode(SwipeLayout.ShowMode.PullOut);
        // Drag From Right

        holder.swipeLayout.addDrag(SwipeLayout.DragEdge.Right, holder.swipeLayout.findViewById(R.id.bottom_wrapper));

        // Handling different events when swiping
        holder.swipeLayout.addSwipeListener(new SwipeLayout.SwipeListener() {
            @Override
            public void onClose(SwipeLayout layout) {
                //when the SurfaceView totally cover the BottomView.
            }

            @Override
            public void onUpdate(SwipeLayout layout, int leftOffset, int topOffset) {
                //you are swiping.
            }

            @Override
            public void onStartOpen(SwipeLayout layout) {


            }

            @Override
            public void onOpen(SwipeLayout layout) {
                //when the BottomView totally show.
            }

            @Override
            public void onStartClose(SwipeLayout layout) {

            }

            @Override
            public void onHandRelease(SwipeLayout layout, float xvel, float yvel) {
                //when user's hand released.
            }
        });

        holder.swipeLayout.getSurfaceView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                fragment.EditMode(wzList.get(position).isEnable(),position);
            }
        });


        holder.deleteWz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.swipeLayout.close(true);
//                fragment.callDeleteWz(wzList.get(position).getId(),wzList.get(position).getName(),position);
            }
        });

        holder.shareWz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.swipeLayout.close(true);
                fragment.watchzoneShareCode(wzList.get(position).getShareCode());
            }
        });

        holder.stateWz.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

               if(buttonView.isPressed()) {
                   holder.stateWz.setChecked(isChecked);

//                       fragment.callEnableDisableWz(wzList.get(position).getId(), isChecked,wzList.get(position).getName());

               }

            }
        });

        holder.bindData(position);
        mItemManger.bindView(holder.itemView, position);
    }

    @Override
    public int getItemCount() {
        return wzList.size();
    }

    @Override
    public int getSwipeLayoutResourceId(int position) {
        return R.id.swipe;
    }

    class WzRowHolder extends RecyclerView.ViewHolder {
        SwipeLayout swipeLayout;
        TextView mainText;
        RelativeLayout shareWz,deleteWz;
        SwitchCompat stateWz;
        public WzRowHolder(View itemView) {
            super(itemView);
            swipeLayout = (SwipeLayout) itemView.findViewById(R.id.swipe);
            shareWz=(RelativeLayout) itemView.findViewById(R.id.wz_share_row);
            deleteWz=(RelativeLayout) itemView.findViewById(R.id.wz_delete_row);
            stateWz=(SwitchCompat) itemView.findViewById(R.id.wz_state);
            mainText = (TextView) itemView.findViewById(R.id.WzName);




        }

        public void bindData(int position) {

            mainText.setText(wzList.get(position).getName());
            stateWz.setChecked(wzList.get(position).isEnable());
        }
    }

}
