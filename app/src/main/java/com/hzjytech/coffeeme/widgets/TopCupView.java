package com.hzjytech.coffeeme.widgets;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.alipay.security.mobile.module.commonutils.LOG;
import com.hzjytech.coffeeme.utils.DensityUtil;

/**
 * Created by hehongcan on 2016/12/5.
 */
public class TopCupView extends View{

    private float mWidth=115;
    private Paint mPaint;
    private RectF rectF;
    private Context context;
    private int mHeight=0;

    public TopCupView(Context context) {
        this(context,null);
    }

    public TopCupView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public TopCupView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initPaint();
    }


    private void initPaint() {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStyle(Paint.Style.FILL);
    }
    /**
     * 根据杯子的内轮廓和当前最上层的view顶端高度动态获取当前的宽度,此处根据回归方程得出曲线结论，单位为dp
     */
    public void setWidth(Context context,int height,View view){
        this.context=context;
        mHeight = DensityUtil.dp2px(context, height);
        Log.e("height", mHeight +"");
        if(mHeight <65){
            mWidth = (float) (0.012 * (Math.sqrt(mHeight)) + 0.6682 * mHeight + 112.01);
        }else {
            mWidth=205;
        }
        Log.e("width",mWidth+"");
        /*ColorDrawable background = (ColorDrawable) view.getBackground();
        int color = background.getColor();*/
        mPaint.setColor(Color.BLUE);
        invalidate();

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        float left = DensityUtil.dp2px(context, 187 / 2) - mWidth / 2;
        float right = DensityUtil.dp2px(context, 187 / 2) + mWidth / 2;
        Log.e("left",left+"");
        Log.e("right",right+"");
        int bottom = DensityUtil.dp2px(context, DensityUtil.dp2px(context, 91) - mHeight);
        rectF = new RectF(left, -8, right, 0);
        canvas.drawArc(rectF,180,360,true,mPaint);
    }
}
