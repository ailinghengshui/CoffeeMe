package com.hzjytech.coffeeme.widgets.ropeprogressbar;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.CornerPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.os.Looper;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Interpolator;

import com.hzjytech.coffeeme.R;

/**
 * Created by Hades on 2016/7/25.
 */
public class RopeProgressBar extends View {

    private final Paint mBubblePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint mLinesPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private final float m1Dip;
    private final float m1Sp;
    private int mProgress;
    private int mMax;
    private int mMin;

    private int mPrimaryColor;
    private int mSecondaryColor;
    private float mSlack;
    private boolean mDynamicLayout;
    private ProgressFormatter mFormatter;

    private final Rect mBounds = new Rect();
    private final Path mBubble = new Path();
    private final Path mTriangle = new Path();

    private static final Interpolator INTERPOLATOR = new DampingInterpolator(5);
    private ValueAnimator mAnimator;
    private float mSlackBounce;
    private int mStartProgress;
    private boolean mDeferred;

    private final Runnable mRequestLayoutRunnable = new Runnable() {
        @Override
        public void run() {
            requestLayout();
        }
    };


    public RopeProgressBar(final Context context) {
        this(context, null);
    }

    public RopeProgressBar(final Context context, final AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RopeProgressBar(
            final Context context,
            final AttributeSet attrs,
            final int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        m1Dip = getResources().getDisplayMetrics().density;
        m1Sp = getResources().getDisplayMetrics().scaledDensity;

        int max = 0;

        int progress = 0;

        float width = dips(8);
        float slack = dips(32);
        boolean dynamicLayout = false;

        int primaryColor = 0xFF009688;
        int secondaryColor = 0xFFDADADA;

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            final TypedValue out = new TypedValue();
//
//            context.getTheme().resolveAttribute(R.attr.colorControlActivated, out, true);
//            primaryColor = out.data;
//            context.getTheme().resolveAttribute(R.attr.colorControlHighlight, out, true);
//            secondaryColor = out.data;
//        }

        final TypedArray a = context.obtainStyledAttributes(
                attrs, R.styleable.RopeProgressBar, defStyleAttr, 0);

        if (a != null) {
            max = a.getInt(R.styleable.RopeProgressBar_max, max);
            progress = a.getInt(R.styleable.RopeProgressBar_progress, progress);

            primaryColor = a.getColor(R.styleable.RopeProgressBar_primaryColor, primaryColor);
            secondaryColor = a.getColor(R.styleable.RopeProgressBar_secondaryColor, secondaryColor);
            slack = a.getDimension(R.styleable.RopeProgressBar_slack, slack);
            width = a.getDimension(R.styleable.RopeProgressBar_ropeStrokeWidth, width);
            dynamicLayout = a.getBoolean(R.styleable.RopeProgressBar_dynamicLayout, false);

            a.recycle();
        }

        mPrimaryColor = primaryColor;
        mSecondaryColor = secondaryColor;
        mSlack = slack;
        mDynamicLayout = dynamicLayout;

        mLinesPaint.setStrokeWidth(width);
        mLinesPaint.setStyle(Paint.Style.STROKE);
        mLinesPaint.setStrokeCap(Paint.Cap.ROUND);

        mBubblePaint.setColor(getResources().getColor(R.color.orange));
        mBubblePaint.setStyle(Paint.Style.FILL);
        mBubblePaint.setPathEffect(new CornerPathEffect(dips(2)));

        mTextPaint.setColor(getResources().getColor(R.color.little_black));
        mTextPaint.setTextSize(sp(12));
        mTextPaint.setTextAlign(Paint.Align.CENTER);
        mTextPaint.setTypeface(Typeface.create("sans-serif-condensed-light", 0));

        setMax(max);
        setProgress(progress);
        setLayerType(LAYER_TYPE_SOFTWARE, null);
    }

    private void dynamicRequestLayout() {
        if (mDynamicLayout) {
            // We need to calculate our new height, since the progress affect the slack
            if (Looper.getMainLooper() == Looper.myLooper()) {
                requestLayout();
            } else {
                post(mRequestLayoutRunnable);
            }
        }
    }

    @Override
    protected synchronized void onMeasure(final int widthMeasureSpec, final int heightMeasureSpec) {

        // Recalculate how tall the text needs to be, width is ignored
        final String progress = getBubbleText();
        mTextPaint.getTextBounds(progress, 0, progress.length(), mBounds);

        final int bubbleHeight = (int) Math.ceil(getBubbleVerticalDisplacement());
        final float slack = mDynamicLayout ? getCurrentSlackHeight() : getSlack() + getSlackBounce();

        final float strokeWidth = getStrokeWidth();
        final int dw = (int) Math.ceil(getPaddingLeft() + getPaddingRight() + strokeWidth);
        final int dh = (int) Math.ceil(getPaddingTop() + getPaddingBottom() + strokeWidth + slack);

        setMeasuredDimension(
                resolveSizeAndState(dw, widthMeasureSpec, 0),
                resolveSizeAndState(dh + bubbleHeight, heightMeasureSpec, 0));

        // Make the triangle Path
        mTriangle.reset();
        mTriangle.moveTo(0, 0);
        mTriangle.lineTo(getTriangleWidth(), 0);
        mTriangle.lineTo(getTriangleWidth() / 2f, getTriangleHeight());
        mTriangle.lineTo(0, 0);
    }

    @SuppressWarnings("UnnecessaryLocalVariable")
    @Override
    protected synchronized void onDraw(final Canvas canvas) {

        final float radius = getStrokeWidth() / 2;

        final float bubbleDisplacement = getBubbleVerticalDisplacement();
        final float top = getPaddingTop() + radius + bubbleDisplacement;
        final float left = getPaddingLeft() + radius;
        final float end = getWidth() - getPaddingRight() - radius;

        final float max = getMax();
        final float offset = (max == 0) ? 0 : (getProgress() / max);
        final float slackHeight = getCurrentSlackHeight();
        final float progressEnd = lerp(left, end, offset);

        // Draw the secondary background line
        mLinesPaint.setColor(mSecondaryColor);
        canvas.drawLine(progressEnd, top + slackHeight, end, top, mLinesPaint);

        // Draw the primary progress line
        mLinesPaint.setColor(mPrimaryColor);
        if (progressEnd == left) {
            // Draw the highlghted part as small as possible
            mLinesPaint.setStyle(Paint.Style.FILL);
            canvas.drawCircle(left, top, radius, mLinesPaint);
            mLinesPaint.setStyle(Paint.Style.STROKE);

        } else {
            canvas.drawLine(left, top, progressEnd, top + slackHeight, mLinesPaint);
        }

        final String progress = getBubbleText();
        mTextPaint.getTextBounds(progress, 0, progress.length(), mBounds);

        // Draw the bubble text background
        final float bubbleWidth = getBubbleWidth();
        final float bubbleHeight = getBubbleHeight();
        mBubble.reset();
//        mBubble.addRect(0, 0, bubbleWidth, bubbleHeight, Path.Direction.CW);

        final float bubbleLeft = Math.min(
                getWidth() - bubbleWidth, Math.max(
                        0, progressEnd - (bubbleWidth / 2)));
        final float bubbleTop = Math.max(slackHeight, 0);
//
        final int saveCount = canvas.save();
        canvas.translate(bubbleLeft, bubbleTop);

//        canvas.drawPath(mBubble, mBubblePaint);

        // Draw the triangle part of the bubble
        final float triangleLeft = Math.min(
                getWidth() - getTriangleWidth(),
                Math.max(0, progressEnd - (getTriangleWidth() / 2) - bubbleLeft));
        final float triangleTop = bubbleHeight;

        mTriangle.offset(triangleLeft, triangleTop);
        canvas.drawPath(mTriangle, mBubblePaint);
        mTriangle.offset(-triangleLeft, -triangleTop);

        // Draw the progress text part of the bubble
        final float textX = bubbleWidth / 2;
        final float textY = bubbleHeight - dips(8);

        canvas.drawText(progress, textX, textY, mTextPaint);

        canvas.restoreToCount(saveCount);
    }

    private float getCurrentSlackHeight() {
        final float max = getMax();
        final float offset = (max == 0) ? 0 : (getProgress() / max);
        return perp(offset) * (getSlack() + mSlackBounce);
    }

    private float getBubbleVerticalDisplacement() {
        return getBubbleMargin() + getBubbleHeight() + getTriangleHeight();
    }

    public float getBubbleMargin() {
        return dips(4);
    }

    public float getBubbleWidth() {
        return mBounds.width() + /* padding */ dips(16);
    }

    public float getBubbleHeight() {
        return mBounds.height() + /* padding */ dips(16);
    }

    public float getTriangleWidth() {
        return dips(12);
    }

    public float getTriangleHeight() {
        return dips(6);
    }

    public float getSlackBounce() {
        return dips(4);
    }

    public String getBubbleText() {
        if (mFormatter != null) {
            return mFormatter.getFormattedText(getProgress(), getMax());

        } else {
            final int progress = (int) ( getProgress()+mMin);
            return progress + "";
        }
    }

    public void defer() {
        if (!mDeferred) {
            mDeferred = true;
            mStartProgress = getProgress();
        }
    }

    public void endDefer() {
        if (mDeferred) {
            mDeferred = false;
            bounceAnimation(mStartProgress);
        }
    }

    public void setMin(int min){
        this.mMin=min;
    }

    public int getMin(){
        return mMin;
    }

    public synchronized void setProgress(int progress) {
        progress = Math.max(0, Math.min(getMax(), progress));
        if (progress == mProgress) {
            return;
        }

        if (!mDeferred) {
            bounceAnimation(progress);
        }

        dynamicRequestLayout();
        mProgress = progress;
        postInvalidate();
    }

    public void animateProgress(int progress) {
        // Speed of animation is interpolated from 0 --> MAX in 2s
        // Minimum time duration is 500ms because anything faster than that is waaaay too quick
        progress = Math.max(0, Math.min(getMax(), progress));
        final int diff = Math.abs(getProgress() - progress);
        final long duration = Math.max(500L, (long) (2000L * (diff / (float) getMax())));

        final ValueAnimator animator = ValueAnimator.ofInt(getProgress(), progress);
        animator.setDuration(duration);
        animator.setInterpolator(new AccelerateInterpolator());
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(final Animator animation) {
                defer();
            }

            @Override
            public void onAnimationEnd(final Animator animation) {
                endDefer();
            }
        });

        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(final ValueAnimator animation) {
                setProgress((Integer) animation.getAnimatedValue());
            }
        });

        animator.start();
    }

    private void bounceAnimation(final int startProgress) {
        // Moving the progress by at least 1/4 of the total distance will invoke
        // the "max" possible slack bouncing at the end progress value
        final int diff = Math.abs(startProgress - mProgress);
        final float diffPercent = Math.min(1f, 4 * diff / (float) getMax());
        if (mAnimator != null) {
            mAnimator.cancel();
        }

        mAnimator = ValueAnimator.ofFloat(0, diffPercent * getSlackBounce());
        mAnimator.setInterpolator(INTERPOLATOR);
        mAnimator.setDuration(1000L);
        mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(final ValueAnimator animation) {
                mSlackBounce = (Float) animation.getAnimatedValue();
                dynamicRequestLayout();
                invalidate();
            }
        });
        mAnimator.start();
    }

    public int getProgress() {
        return mProgress;
    }

    public void setMax(int max) {
        max = Math.max(0, max);

        if (max != mMax) {

            dynamicRequestLayout();
            mMax = max;
            postInvalidate();

            if (mProgress > max) {
                mProgress = max;
            }
        }
    }

    public int getMax() {
        return mMax;
    }

    public void setDynamicLayout(final boolean isDynamic) {
        if (mDynamicLayout != isDynamic) {
            mDynamicLayout = isDynamic;

            requestLayout();
            invalidate();
        }
    }

    public void setPrimaryColor(final int color) {
        mPrimaryColor = color;

        invalidate();
    }

    public int getPrimaryColor() {
        return mPrimaryColor;
    }

    public void setSecondaryColor(final int color) {
        mSecondaryColor = color;

        invalidate();
    }

    public int getSecondaryColor() {
        return mSecondaryColor;
    }

    public void setSlack(final float slack) {
        mSlack = slack;

        requestLayout();
        invalidate();
    }

    public float getSlack() {
        return mSlack;
    }

    public void setStrokeWidth(final float width) {
        mLinesPaint.setStrokeWidth(width);

        requestLayout();
        invalidate();
    }

    public float getStrokeWidth() {
        return mLinesPaint.getStrokeWidth();
    }

    public void setTextPaint(final Paint paint) {
        mTextPaint.set(paint);

        requestLayout();
        invalidate();
    }

    public void setProgressFormatter(final ProgressFormatter formatter) {
        mFormatter = formatter;

        requestLayout();
        invalidate();
    }

    /**
     * Return a copy so that fields can only be modified through {@link #setTextPaint}
     */
    public Paint getTextPaint() {
        return new Paint(mTextPaint);
    }

    private float perp(float t) {
        // eh, could be more mathematically accurate to use a catenary function,
        // but the max difference between the two is only 0.005
        return (float) (-Math.pow(2 * t - 1, 2) + 1);
    }

    private float lerp(float v0, float v1, float t) {
        return (t == 1) ? v1 : (v0 + t * (v1 - v0));
    }

    private float dips(final float dips) {
        return dips * m1Dip;
    }

    private float sp(final int sp) {
        return sp * m1Sp;
    }

}
