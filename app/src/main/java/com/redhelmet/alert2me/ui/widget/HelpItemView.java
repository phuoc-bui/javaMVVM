package com.redhelmet.alert2me.ui.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.constraint.ConstraintLayout;
import android.text.InputType;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.redhelmet.alert2me.R;

public class HelpItemView extends ConstraintLayout {

    private String title, description;
    private boolean showArrow, hasDivider, isPasswordField;
    private TextView txtTitle, txtDescription;
    private View arrow;

    public HelpItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.HelpItemView, 0, 0);
        title = array.getString(R.styleable.HelpItemView_title);
        description = array.getString(R.styleable.HelpItemView_description);
        showArrow = array.getBoolean(R.styleable.HelpItemView_showArrow, true);
        hasDivider = array.getBoolean(R.styleable.HelpItemView_hasEndDivider, false);
        isPasswordField = array.getBoolean(R.styleable.HelpItemView_isPasswordField, false);
        array.recycle();

        View view = LayoutInflater.from(context).inflate(R.layout.item_help, this, true);
        txtTitle = view.findViewById(R.id.txt_title);
        txtDescription = view.findViewById(R.id.txt_description);
        arrow = view.findViewById(R.id.iv_right_arrow);
        View divider = view.findViewById(R.id.divider);

        txtTitle.setText(title);
        if (isPasswordField) txtDescription.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
        if (description != null && !description.isEmpty()) txtDescription.setText(description);
        else txtDescription.setVisibility(GONE);
        arrow.setVisibility(showArrow ? VISIBLE : GONE);
        divider.setVisibility(hasDivider ? VISIBLE : GONE);
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
