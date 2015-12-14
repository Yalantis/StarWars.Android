package com.yalantis.starwarsdemo.widget;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LevelListDrawable;
import android.support.annotation.IntRange;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.SwitchCompat;
import android.util.AttributeSet;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;

import com.yalantis.starwarsdemo.R;

/**
 * Created by Artem Kholodnyi on 11/17/15.
 */
public class BackgroundDrawableSwitchCompat extends SwitchCompat {
    public static final int OPAQUE = 255;
    public static final int TRANSPARENT = 0;

    // Duration taken from SwitchCompat.java
    public static final int THUMB_ANIMATION_DURATION = 250;

    private int mFirstDrawableAlpha;
    private int mSecondDrawableAlpha;
    private TwoStatesTrackDrawable mTwoStatesDrawable;
    private ValueAnimator animator;

    public BackgroundDrawableSwitchCompat(Context context) {
        super(context);
        init(context);
    }

    public BackgroundDrawableSwitchCompat(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public BackgroundDrawableSwitchCompat(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        Drawable offDrawable = ContextCompat.getDrawable(context, R.drawable.track_off);
        Drawable onDrawable = ContextCompat.getDrawable(context, R.drawable.track_on);
        mTwoStatesDrawable = new TwoStatesTrackDrawable(offDrawable, onDrawable);
        setTrackDrawable(mTwoStatesDrawable);
    }

    @Override
    public void setChecked(boolean checked) {
        super.setChecked(checked);
        if (mTwoStatesDrawable != null) {
            startAnimation(checked);
        }
    }

    public void setCheckedImmediate(boolean checked) {
        super.setChecked(checked);
        mTwoStatesDrawable.setBlend(checked ? OPAQUE : TRANSPARENT);
    }

    private class TwoStatesTrackDrawable extends LevelListDrawable {
        private final Drawable mFirstDrawable;
        private final Drawable mSecondDrawable;
        private Animation mBackgroundAnimator;

        public TwoStatesTrackDrawable(Drawable firstDrawable, Drawable secondDrawable) {
            mFirstDrawable = firstDrawable;
            mSecondDrawable = secondDrawable;
        }

        @Override
        public void draw(Canvas canvas) {
            mFirstDrawable.setAlpha(mFirstDrawableAlpha);
            mSecondDrawable.setAlpha(mSecondDrawableAlpha);
            int paddingX = getThumbDrawable().getIntrinsicWidth() / 4;
            int paddingY = getThumbDrawable().getIntrinsicHeight() / 4;
            Rect rect = new Rect(paddingX, paddingY, canvas.getWidth() - paddingX, canvas.getHeight() - paddingY);

            mFirstDrawable.setBounds(rect);
            mSecondDrawable.setBounds(rect);

            mFirstDrawable.draw(canvas);
            mSecondDrawable.draw(canvas);
        }

        @Override
        public void setAlpha(int alpha) {

        }

        @Override
        public void setColorFilter(ColorFilter colorFilter) {

        }

        @Override
        public int getOpacity() {
            return 0;
        }

        private void setBlend(@IntRange(from = TRANSPARENT, to = OPAQUE) int k) {
            mFirstDrawableAlpha = OPAQUE - k;
            mSecondDrawableAlpha = k;
            invalidate();
        }
    }

    private void startAnimation(boolean firstToSecond) {
        if (animator != null) {
            animator.cancel();
        }
        animator = ValueAnimator.ofInt(firstToSecond ? OPAQUE : TRANSPARENT, firstToSecond ? TRANSPARENT : OPAQUE);
        animator.setDuration(THUMB_ANIMATION_DURATION);
        animator.setInterpolator(new LinearInterpolator());
//        animator.setRepeatMode(ValueAnimator.RESTART);
//        animator.setRepeatCount(ValueAnimator.INFINITE);

        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mFirstDrawableAlpha = (int) animation.getAnimatedValue();
                mSecondDrawableAlpha = OPAQUE - (int) animation.getAnimatedValue();
                invalidate();
            }
        });

        animator.start();
    }

}
