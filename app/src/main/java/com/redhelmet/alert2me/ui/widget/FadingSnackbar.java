package com.redhelmet.alert2me.ui.widget;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.redhelmet.alert2me.R;

public class FadingSnackbar extends FrameLayout {

    private final long ENTER_DURATION = 300L;
    private final long EXIT_DURATION = 200L;
    private final long SHORT_DURATION = 1_500L;
    private final long LONG_DURATION = 2_750L;

    private TextView message;
    private Button action;

    public FadingSnackbar(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.fading_snackbar_layout, this, true);
        message = view.findViewById(R.id.snackbar_text);
        action = view.findViewById(R.id.snackbar_action);
    }

    public void dismiss() {
        if (getVisibility() == VISIBLE && getAlpha() == 1f) {
            animate()
                    .alpha(0f)
                    .withEndAction(() -> FadingSnackbar.this.setVisibility(View.GONE))
                    .setDuration(EXIT_DURATION);
        }
    }

    public void show(@StringRes int messageId,
                     CharSequence messageText,
                     @StringRes int actionId,
                     boolean longDuration,
                     Runnable actionClick,
                     Runnable dismissListener) {
        message.setText(messageText != null ? messageText : getContext().getString(messageId));
        String actionText = getContext().getString(actionId);
        if (actionText != null) {
            action.setVisibility(VISIBLE);
            action.setText(actionText);
            if (actionClick != null) {
                action.setOnClickListener(v -> actionClick.run());
            }
        } else {
            action.setVisibility(GONE);
        }
        setAlpha(0f);
        setVisibility(VISIBLE);
        animate()
                .alpha(1f)
                .setDuration(ENTER_DURATION);
        long showDuration = ENTER_DURATION + (longDuration ? LONG_DURATION : SHORT_DURATION);
        postDelayed(() -> {
            dismiss();
            dismissListener.run();
        }, showDuration);
    }
}
