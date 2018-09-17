package com.redhelmet.alert2me.ui.fragments;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.HashMap;

import com.redhelmet.alert2me.R;

public class HintFragment extends Fragment {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_TEXT = "param1";
    private static final String ARG_DESC = "param2";
    private static final String ARG_IMG = "param3";
    @Nullable
    private String pageText;
    private String pageDesc;
    private String pageImg;

    public HintFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param pageText Parameter 1.
     * @return A new instance of fragment PageFragment.
     */
    public static HintFragment newInstance(@NonNull final HashMap<String, String> pageText) {
        HintFragment fragment = new HintFragment();

        Bundle args = new Bundle();
        args.putString(ARG_TEXT, pageText.get("title"));
        args.putString(ARG_DESC, pageText.get("desc"));
        args.putString(ARG_IMG, pageText.get("img"));
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            pageText = getArguments().getString(ARG_TEXT);
            pageDesc = getArguments().getString(ARG_DESC);
            pageImg = getArguments().getString(ARG_IMG);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
      /*
     create page according to bundle values
      emPage - true if its a last page
       */



        int imgName=R.drawable.hint1;
        boolean emPage=false;
        switch(pageImg){
            case "hint1":
                emPage=false;
                imgName=R.drawable.hint1;
                break;
            case "hint2":
                emPage=false;
                imgName=R.drawable.hint2;
                break;
            case "hint3":
                emPage=false;
                imgName=R.drawable.hint3;
                break;
            case "hint4":
                emPage=true;

                break;
        }
        View view;
        if(emPage)
         view = inflater.inflate(R.layout.activity_intro_second, container, false);
        else
            view = inflater.inflate(R.layout.activity_intro_first, container, false);

        TextView heading = (TextView) view.findViewById(R.id.heading);
        TextView desc = (TextView) view.findViewById(R.id.details);
        if (heading != null) {
            heading.setText(pageText);
            desc.setText(Html.fromHtml(pageDesc));
        }
        final ImageView imageView = (ImageView) view.findViewById(R.id.background);
        if(!emPage)
        imageView.setBackgroundResource(imgName);

        return view;
    }
}