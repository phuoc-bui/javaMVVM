package com.redhelmet.alert2me.ui.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.InputType;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.redhelmet.alert2me.R;

import androidx.constraintlayout.widget.ConstraintLayout;

public class HelpItemView extends ConstraintLayout {

    private String title, description;
    private boolean showArrow, hasDivider, isPasswordField;
    private TextView txtTitle, txtDescription;
    private View arrow, divider;

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
        divider = view.findViewById(R.id.divider);

        txtTitle.setText(title);
        if (isPasswordField) txtDescription.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
        setDescription(description);
        setShowArrow(showArrow);
        setHasDivider(hasDivider);
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
        if (description != null && !description.isEmpty()){
            txtDescription.setVisibility(VISIBLE);
            txtDescription.setText(description);
        }
        else txtDescription.setVisibility(GONE);
    }

    public boolean isShowArrow() {
        return showArrow;
    }

    public void setShowArrow(boolean showArrow) {
        this.showArrow = showArrow;
        arrow.setVisibility(showArrow ? VISIBLE : GONE);
    }

    public boolean isHasDivider() {
        return hasDivider;
    }

    public void setHasDivider(boolean hasDivider) {
        this.hasDivider = hasDivider;
        divider.setVisibility(hasDivider ? VISIBLE : GONE);
    }

    public boolean isPasswordField() {
        return isPasswordField;
    }

    public void setPasswordField(boolean passwordField) {
        isPasswordField = passwordField;
    }
}
