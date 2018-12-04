package com.phuocbui.mvvm.ui.widget;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.transition.Transition;
import android.transition.TransitionValues;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

public class RotateTransaction extends Transition {

    private static final String PROP_ROTATION = "a2m:rotate:rotation";
    private static final String[] TRANSITION_PROPERTIES = {PROP_ROTATION};

    public RotateTransaction() {
        super();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public RotateTransaction(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void captureStartValues(TransitionValues transitionValues) {
        captureValues(transitionValues);
    }

    @Override
    public void captureEndValues(TransitionValues transitionValues) {
        captureValues(transitionValues);
    }

    @Override
    public Animator createAnimator(ViewGroup sceneRoot, TransitionValues startValues, TransitionValues endValues) {
        if (startValues == null || endValues == null) return null;

        float startRotation = (float) startValues.values.get(PROP_ROTATION);
        float endRotation = (float) endValues.values.get(PROP_ROTATION);
        if (startRotation == endRotation) return null;

        View view = endValues.view;
        // ensure the pivot is set
        view.setPivotX(view.getWidth() / 2f);
        view.setPivotY(view.getHeight() / 2f);
        return ObjectAnimator.ofFloat(endValues.view, View.ROTATION, startRotation, endRotation);
    }

    @Override
    public String[] getTransitionProperties() {
        return TRANSITION_PROPERTIES;
    }

    private void captureValues(TransitionValues transitionValues) {
        View view = transitionValues.view;
        if (view == null || view.getWidth() <= 0 || view.getHeight() <= 0) return;
        transitionValues.values.put(PROP_ROTATION, view.getRotation());
    }
}
