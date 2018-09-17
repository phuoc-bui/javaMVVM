package com.redhelmet.alert2me.adapters;

import android.content.Context;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.MimeTypeMap;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.io.File;
import java.util.ArrayList;

import com.redhelmet.alert2me.R;
import com.redhelmet.alert2me.ui.activity.AddObservation;
import droidninja.filepicker.utils.FilePickerUtils;


/**
 * Created by inbox on 12/2/18.
 */

public class ObservationImageAdapter extends RecyclerView.Adapter<ObservationImageAdapter.FileViewHolder> {

    private final ArrayList<String> paths;
    private final Context context;
    private int imageSize;

    public ObservationImageAdapter(Context context, ArrayList<String> paths) {
        this.context = context;
        this.paths = paths;
        setColumnNumber(context, 3);
    }

    private void setColumnNumber(Context context, int columnNum) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics metrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(metrics);
        int widthPixels = metrics.widthPixels;
        imageSize = widthPixels / columnNum;
    }

    @Override public FileViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView =
                LayoutInflater.from(parent.getContext()).inflate(R.layout.item_layout, parent, false);

        return new FileViewHolder(itemView);
    }

    @Override public void onBindViewHolder(FileViewHolder holder, final int position) {
        String path = paths.get(position);

        if(isImage(getMimeTypeTest(path))){
            Glide.with(context)
                    .load(new File(path))
                    .apply(RequestOptions.centerCropTransform()
                            .dontAnimate()
                            .override(imageSize, imageSize)
                            .placeholder(droidninja.filepicker.R.drawable.image_placeholder))
                    .thumbnail(0.5f)

                    .into(holder.imageView);
        }else{

            Glide.with(context)
                    .load(new File(path))
                    .apply(RequestOptions.centerCropTransform()
                            .dontAnimate()
                            .override(imageSize, imageSize)
                            .placeholder(droidninja.filepicker.R.drawable.image_placeholder))
                    .thumbnail(0.5f)

                    .into(holder.imageView);
            holder.videoMark.setVisibility(View.VISIBLE);

        }



        holder.deleteImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                paths.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, getItemCount());
                if(context instanceof AddObservation){
                    ((AddObservation)context).deleteSuccesfull(position);
                }
            }
        });

    }

    @Override public int getItemCount() {
        return paths.size();
    }

    public static class FileViewHolder extends RecyclerView.ViewHolder {

        AppCompatImageView imageView;
        ImageButton deleteImage;
        ImageView videoMark;

        public FileViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.iv_photo);
            deleteImage=itemView.findViewById(R.id.dlt);
            videoMark=itemView.findViewById(R.id.video_icon);
        }
    }

    public static String getMimeTypeTest(String url) {
        String type = null;
        String extension = MimeTypeMap.getFileExtensionFromUrl(url);
        if (extension != null) {
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        }
        return type;
    }




    public boolean isImage(String format) {
        String[] types = { "jpg", "jpeg", "png" };
        return FilePickerUtils.contains(types, format);
    }
}
