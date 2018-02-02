package com.hzjytech.coffeeme.widgets.ropeprogressbar;

import android.view.animation.Interpolator;

/**
 * Created by Hades on 2016/7/25.
 */
public class DampingInterpolator implements Interpolator {
    private final float mCycles;

    public DampingInterpolator() {
        this(1);
    }

    public DampingInterpolator(float cycles) {
        mCycles = cycles;
    }

    @Override
    public float getInterpolation(final float input) {
        return (float) (Math.sin(mCycles * 2 * Math.PI * input) * ((input - 1) * (input - 1)));
    }
}
