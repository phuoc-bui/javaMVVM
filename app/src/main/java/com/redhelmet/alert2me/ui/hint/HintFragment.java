package com.redhelmet.alert2me.ui.hint;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.redhelmet.alert2me.R;
import com.redhelmet.alert2me.data.model.Hint;

public class HintFragment extends Fragment {
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_HINT = "hint";

    private Hint hint;

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
    public static HintFragment newInstance(@NonNull final Hint pageText) {
        HintFragment fragment = new HintFragment();

        Bundle args = new Bundle();
        args.putSerializable(ARG_HINT, pageText);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            hint = (Hint) getArguments().getSerializable(ARG_HINT);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
//     create page according to bundle values
        View view;
        if (hint.isLast())
            view = inflater.inflate(R.layout.activity_intro_second, container, false);
        else
            view = inflater.inflate(R.layout.activity_intro_first, container, false);

        TextView heading = view.findViewById(R.id.heading);
        TextView desc = view.findViewById(R.id.details);
        if (heading != null) {
            heading.setText(hint.getTitle());
            desc.setText(Html.fromHtml(hint.getDesc()));
        }
        final ImageView imageView = view.findViewById(R.id.background);
        if (!hint.isLast())
            imageView.setBackgroundResource(hint.getUrl());

        return view;
    }
}