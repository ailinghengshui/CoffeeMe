package com.hzjytech.coffeeme.widgets;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.View;

import com.hzjytech.coffeeme.R;
import com.hzjytech.coffeeme.utils.DensityUtil;
import com.hzjytech.coffeeme.widgets.AnimatorPath.AnimatorPath;
import com.hzjytech.coffeeme.widgets.AnimatorPath.PathEvaluator;
import com.hzjytech.coffeeme.widgets.AnimatorPath.PathPoint;

import java.util.ArrayList;

/**
 * Created by hehongcan on 2017/3/21.
 */
public class CompletelyCustomView extends View{
    public static final int COFFEE = 1;
    public static final int SUGAR = 2;
    public static final int MILK = 3;
    public static final int CHOCOLATE = 4;
    private  Context context;
    private int type;
    private float startX;
    private float startY;
    private float endX;
    private float endY;
    private AnimatorPath animatorPath;
    private ObjectAnimator anim;
    private float mWaterCenterX;
    private float mWaterCenterY;
    private Paint paint;
    private ArrayList<PointF> pointFs;

    public CompletelyCustomView(Context context) {
        this(context,null);
    }

    public CompletelyCustomView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public CompletelyCustomView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context=context;
        pointFs = new ArrayList<>();
        paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setStrokeWidth(DensityUtil.dp2px(context,5));
    }
    
    public void setStartAndEnd(float startX,float startY,float endX,float endY){
        this.startX=startX;
        this.startY=startY;
        this.endX=endX;
        this.endY=endY;
    }
    public void startRunningWater(int type){
        final PointF pointF = new PointF();
        pointFs.add(pointF);
        this.type=type;
        anim = ObjectAnimator.ofObject(this, "center", new PathEvaluator(), animatorPath.getPoints().toArray());
        anim.setDuration(1000);
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                PathPoint pathPoint = (PathPoint) valueAnimator.getAnimatedValue();
                //points.get(points.size()-1).set(pathPoint.mX,pathPoint.mY);
                pointF.set(pathPoint.mX,pathPoint.mY);
                if(pathPoint.mY==2*getHeight()){
                    pointFs.remove(pointF);
                }
                postInvalidate();
            }
        });
        anim.start();
    }
    public void setCenter(PathPoint newLoc){
        mWaterCenterX =newLoc.mX;
        mWaterCenterY =newLoc.mY;
    }
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        this.startX=getWidth()/2;
        this.startY=0;
        this.endX=getWidth()/2;
        this.endY=getHeight();
        AnimatorPath animatorPath = new AnimatorPath();
        animatorPath.moveTo(startX,startY);
        animatorPath.lineTo(endX,endY+getHeight());
        this.animatorPath=animatorPath;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        switch (type){
            case COFFEE:
                paint.setColor(getResources().getColor(R.color.coffee));
                break;
            case SUGAR:
                paint.setColor(getResources().getColor(R.color.sugar_bottom_water));
                break;
            case MILK:
                paint.setColor(getResources().getColor(R.color.milk_bottom_water));
                break;
            case CHOCOLATE:
                paint.setColor(getResources().getColor(R.color.chocolate_bottom_water));
                break;
        }
        for (PointF pointF : pointFs) {
            canvas.drawLine(mWaterCenterX,pointF.y,mWaterCenterX,pointF.y-getHeight(),paint);
        }

    }
}
