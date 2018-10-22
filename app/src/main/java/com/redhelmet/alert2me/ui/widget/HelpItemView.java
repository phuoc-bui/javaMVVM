package com.redhelmet.alert2me.ui.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.constraint.ConstraintLayout;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.redhelmet.alert2me.R;

public class HelpItemView extends ConstraintLayout {

    private String title, description;
    private boolean showArrow;
    private TextView txtTitle, txtDescription;
    private View arrow;

    public HelpItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.HelpItemView, 0, 0);
        title = array.getString(R.styleable.HelpItemView_title);
        description = array.getString(R.styleable.HelpItemView_description);
        showArrow = array.getBoolean(R.styleable.HelpItemView_showArrow, true);
        array.recycle();

        View view = LayoutInflater.from(context).inflate(R.layout.item_help, this, true);
        txtTitle = view.findViewById(R.id.txt_title);
        txtDescription = view.findViewById(R.id.txt_description);
        arrow = view.findViewById(R.id.iv_right_arrow);

        txtTitle.setText(title);
        txtDescription.setText(description);
        arrow.setVisibility(showArrow ? VISIBLE : GONE);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
        txtTitle.setText(title);
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
        txtDescription.setText(description);
    }

    public boolean isShowArrow() {
        return showArrow;
    }

    public void setShowArrow(boolean showArrow) {
        this.showArrow = showArrow;
        arrow.setVisibility(showArrow ? VISIBLE : GONE);
    }
}
