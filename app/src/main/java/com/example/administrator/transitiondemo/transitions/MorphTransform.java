package com.example.administrator.transitiondemo.transitions;

import android.animation.Animator;
import android.animation.TimeInterpolator;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.transition.ChangeBounds;
import android.transition.TransitionValues;
import android.view.View;
import android.view.ViewGroup;

import com.example.administrator.transitiondemo.drawable.MorphDrawable;
import com.example.administrator.transitiondemo.util.AnimUtils;

/**
 * Created by Lbin on 2017/10/19.
 */

public class MorphTransform extends ChangeBounds {

    private static final String EXTRA_SHARED_ELEMENT_START_COLOR =
            "EXTRA_SHARED_ELEMENT_START_COLOR";
    private static final String EXTRA_SHARED_ELEMENT_START_CORNER_RADIUS =
            "EXTRA_SHARED_ELEMENT_START_CORNER_RADIUS";
    private static final long DEFAULT_DURATION = 300L;

    private final int startColor;
    private final int endColor;
    private final int startCornerRadius;
    private final int endCornerRadius;

    public MorphTransform(@ColorInt int startColor, @ColorInt int endColor, int startCornerRadius, int endCornerRadius) {
        this.startColor = startColor;
        this.endColor = endColor;
        this.startCornerRadius = startCornerRadius;
        this.endCornerRadius = endCornerRadius;
        setDuration(DEFAULT_DURATION);
        setPathMotion(new GravityArcMotion());
    }

    public static void addExtras(@NonNull Intent intent,
                                 @ColorInt int startColor,
                                 int startCornerRadius) {
        intent.putExtra(EXTRA_SHARED_ELEMENT_START_COLOR, startColor);
        intent.putExtra(EXTRA_SHARED_ELEMENT_START_CORNER_RADIUS, startCornerRadius);
    }

    /**
     * @param activity
     * @param target
     * @param endColor
     * @param endCornerRadius
     *
     *  给activity设置transitions
     *
     */
    public void setUp(@NonNull Activity activity, @NonNull View target, @ColorInt int endColor, int endCornerRadius) {
        final Intent intent = activity.getIntent();
        if (intent == null
                || !intent.hasExtra(EXTRA_SHARED_ELEMENT_START_COLOR)
                || !intent.hasExtra(EXTRA_SHARED_ELEMENT_START_CORNER_RADIUS)){ // 如果没有数据了返回
            return;
        }

        final int startColor = activity.getIntent().
                getIntExtra(EXTRA_SHARED_ELEMENT_START_COLOR, Color.TRANSPARENT);
        final int startCornerRadius =
                intent.getIntExtra(EXTRA_SHARED_ELEMENT_START_CORNER_RADIUS, 0);

        final MorphTransform sharedEnter = new MorphTransform(startColor , endColor , startCornerRadius , endCornerRadius);
        final MorphTransform sharedReturn = new MorphTransform(endColor , startColor , endCornerRadius , startCornerRadius);

        if (target != null) {
            sharedEnter.addTarget(target);
            sharedReturn.addTarget(target);
        }
        activity.getWindow().setSharedElementEnterTransition(sharedEnter);
        activity.getWindow().setSharedElementReturnTransition(sharedReturn);
    }

    @Override
    public Animator createAnimator(ViewGroup sceneRoot, TransitionValues startValues, TransitionValues endValues) {
        final Animator changeBounds = super.createAnimator(sceneRoot, startValues, endValues);
        if (changeBounds == null) return null;

        TimeInterpolator interpolator = getInterpolator();
        if (interpolator == null) {
            interpolator = AnimUtils.getFastOutSlowInInterpolator(sceneRoot.getContext());
        }
        final MorphDrawable background = new MorphDrawable(startColor, startCornerRadius);

        return super.createAnimator(sceneRoot, startValues, endValues);


    }
}
