package com.phuocbui.mvvm.ui.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Parcel;
import android.os.Parcelable;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.transition.Transition;
import android.transition.TransitionInflater;
import android.transition.TransitionManager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.phuocbui.mvvm.R;

public class CollapsibleCard extends LinearLayout {

    private boolean expanded = false;
    private TextView cardTitleView;
    private TextView cardDescriptionView;
    private ImageView expandIcon;
    private View titleContainer;
    private Transition toggle;
    private View root;
    private String cardTitle;
    private String cardDescription;

    public CollapsibleCard(Context context) {
        this(context, null);
    }

    public CollapsibleCard(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CollapsibleCard(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray arr = context.obtainStyledAttributes(attrs, R.styleable.CollapsibleCard, 0, 0);
        try {
            cardTitle = arr.getString(R.styleable.CollapsibleCard_cardTitle);
            cardDescription = arr.getString(R.styleable.CollapsibleCard_cardDescription);
        } finally {
            arr.recycle();
        }

        root = LayoutInflater.from(context).inflate(R.layout.collapsible_card_content, this, true);
        titleContainer = root.findViewById(R.id.title_container);
        cardTitleView = root.findViewById(R.id.card_title);
        cardTitleView.setText(cardTitle);
        setTitleContentDescription(cardTitle);
        cardDescriptionView = root.findViewById(R.id.card_description);
        cardDescriptionView.setText(cardDescription);
        expandIcon = root.findViewById(R.id.expand_icon);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
//            expandIcon.setImageTintList(AppCompatResources.getColorStateList(context, R.color.collapsing_section));
//        }

        toggle = TransitionInflater.from(context).inflateTransition(R.transition.info_card_toggle);

        titleContainer.setOnClickListener(v -> toggleExpanded());
    }

    private void setTitleContentDescription(String cardTitle) {
        cardTitleView.setContentDescription(cardTitle + (expanded ? ", expand" : ", collapsed"));
    }

    private void toggleExpanded() {
        expanded = !expanded;
        toggle.setDuration(expanded ? 300L : 200L);
        TransitionManager.beginDelayedTransition((ViewGroup) root.getParent(), toggle);
        cardDescriptionView.setVisibility(expanded ? VISIBLE : GONE);
        expandIcon.setRotation(expanded ? 180f : 0f);
        // activated used to tint controls when expanded
        expandIcon.setActivated(expanded);
        cardTitleView.setActivated(expanded);
        setTitleContentDescription(cardTitle);
    }

    @Override
    public Parcelable onSaveInstanceState() {
        SavedState savedState = new SavedState(super.onSaveInstanceState());
        savedState.expanded = expanded;
        return savedState;
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        if (state instanceof SavedState) {
            super.onRestoreInstanceState(((SavedState) state).getSuperState());
            if (expanded != ((SavedState) state).expanded) {
                toggleExpanded();
            }
        } else {
            super.onRestoreInstanceState(state);
        }
    }

    // get set for data binding
    public String getCardTitle() {
        return cardTitle;
    }

    public void setCardTitle(String cardTitle) {
        this.cardTitle = cardTitle;
        cardTitleView.setText(cardTitle);
    }

    public String getCardDescription() {
        return cardDescription;
    }

    public void setCardDescription(String cardDescription) {
        this.cardDescription = cardDescription;
        cardDescriptionView.setText(cardDescription);
    }

    private static class SavedState extends BaseSavedState {

        private boolean expanded = false;

        SavedState(Parcel source) {
            super(source);
        }

        SavedState(Parcelable superState) {
            super(superState);
        }

        @Override
        public void writeToParcel(Parcel out, int flags) {
            super.writeToParcel(out, flags);
            out.writeByte((byte) (expanded ? 1 : 0));
        }

        static Parcelable.Creator<SavedState> CREATOR = new Parcelable.Creator<SavedState>() {
            @Override
            public SavedState createFromParcel(Parcel source) {
                return new SavedState(source);
            }

            @Override
            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };
    }
}
