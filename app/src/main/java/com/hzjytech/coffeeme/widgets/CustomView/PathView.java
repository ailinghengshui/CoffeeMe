package com.hzjytech.coffeeme.widgets.CustomView;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.LinearInterpolator;

import com.hzjytech.coffeeme.R;
import com.hzjytech.coffeeme.utils.LogUtil;
import com.hzjytech.coffeeme.widgets.AnimatorPath.AnimatorPath;
import com.hzjytech.coffeeme.widgets.AnimatorPath.PathEvaluator;
import com.hzjytech.coffeeme.widgets.AnimatorPath.PathPoint;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 使用时：
 * 1、初始化路径宽度
 * 1、初始化路径
 * 2、初始化动画路径
 * 3、初始化半径
 * 4、开始动画
 * 5、结束动画
 */

public class PathView extends View {

    public static final Integer WATER = 5;
    public static final Integer COFFEE = 1;
    public static final Integer SUGAR = 2;
    public static final Integer MILK = 3;
    public static final Integer CHOCOLATE = 4;
    private Paint paint;
    private float mCenterX;
    private float mCenterY;
    private float radius;
    private Map<Integer, Path> paths;
    private Path waterPath;
    private Map<Integer, AnimatorPath> animPaths;
    private float mWaterCenterX;
    private float mWaterCenterY;
    private float waterRadius;
    private Map<Integer, Float> pipeWidths;
    private Map<Integer, Float> ballWidths;
    private boolean isEnd=true;
    private int type;
    private ObjectAnimator anim;
    private ObjectAnimator waterAnim;
    private ObjectAnimator waterSecondAnim;
    private ObjectAnimator secondAnim;
    private float mSecondCenterY;
    private float mSecondCenterX;
    private float mWaterSecondCenterX;
    private float mWaterSecondCenterY;
    private ArrayList<PointF> points;
    private ArrayList<PointF> waterPoints;
    private PathPoint pathPoint;
    private int i=0;
    private BallAnimationEndListener ballAnimationEndListener;


    public PathView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    private void initView() {
        points = new ArrayList<>();
        waterPoints=new ArrayList<>();
        paint = new Paint();
        //抗锯齿
        paint.setAntiAlias(true);
        //防抖动
        paint.setDither(true);
        //设置画笔未实心
        paint.setStyle(Paint.Style.FILL);
        //设置颜色
        paint.setColor(Color.YELLOW);
        //设置画笔宽度
        paint.setStrokeWidth(3);
    }

    /**
     * 设置球体颜色
     * @param color
     */
    public void setBallColor(int color){
        paint.setColor(color);
    }

    /**
     * 设置球体半径，以px为单位
     * @param px
     */
    public void setWaterRadius(float px){
        waterRadius=px;
    }
    public void setPipeWidth(Map<Integer,Float>pipeWidths){
        this.pipeWidths=pipeWidths;
    }
   public void setBallWidth(Map<Integer,Float> ballWidths){
       this.ballWidths=ballWidths;
   }
    public void startPipeAnimation(final int type){
        this.type=type;
        if(isEnd){
            isEnd=false;
        }
        if(animPaths==null||animPaths.size()<0){
            return;
        }
        final AnimatorPath animatorPath = animPaths.get(type);
        anim = ObjectAnimator.ofObject(this, "center", new PathEvaluator(), animatorPath.getPoints().toArray());
        final ArrayList<PathPoint> pathPoints = (ArrayList<PathPoint>) animatorPath.getPoints();
        anim.setInterpolator(new LinearInterpolator());
        anim.setDuration((type==COFFEE||type==CHOCOLATE)?1000:500);
        anim.setRepeatCount(0);
        final PointF pointF = new PointF();
        this.points.add(pointF);
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                 LogUtil.e("i",i+"");
                 pathPoint = (PathPoint) valueAnimator.getAnimatedValue();
                 //points.get(points.size()-1).set(pathPoint.mX,pathPoint.mY);
                 pointF.set(pathPoint.mX,pathPoint.mY);
                if(pathPoint.mX==pathPoints.get(pathPoints.size()-1).mX&&pathPoint.mY==pathPoints.get(pathPoints.size()-1).mY){
                    points.remove(pointF);
                }
                 postInvalidate();
                 LogUtil.e("value","mX"+ pathPoint.mX+"mY"+ pathPoint.mY);
            }
        });
        anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);

            }
        });
        anim.start();
    /*    //第二个滚动点
        secondAnim = ObjectAnimator.ofObject(this, "secondCenter", new PathEvaluator(), animatorPath.getPoints().toArray());
        secondAnim.setInterpolator(new LinearInterpolator());
        secondAnim.setDuration(1000);
        secondAnim.setRepeatCount(ValueAnimator.INFINITE);
        secondAnim.setStartDelay(800);
        secondAnim.start();*/
        AnimatorPath waterAnimatorPath = animPaths.get(WATER);
        waterAnim = ObjectAnimator.ofObject(this, "waterCenter", new PathEvaluator(), waterAnimatorPath.getPoints().toArray());
        final ArrayList<PathPoint> waterPathPoints = (ArrayList<PathPoint>) waterAnimatorPath.getPoints();
        waterAnim.setInterpolator(new LinearInterpolator());
        waterAnim.setDuration((type==COFFEE||type==CHOCOLATE)?1000:1000);
        waterAnim.setRepeatCount(0);
        final PointF waterPointF = new PointF();
        waterPoints.add(waterPointF);
        waterAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                PathPoint pathWaterPoint = (PathPoint) valueAnimator.getAnimatedValue();
                waterPointF.set(pathWaterPoint.mX,pathWaterPoint.mY);
                if(pathWaterPoint.mX==waterPathPoints.get(waterPathPoints.size()-1).mX&&pathWaterPoint.mY==waterPathPoints.get(waterPathPoints.size()-1).mY){
                    waterPoints.remove(waterPointF);
                }
                postInvalidate();
            }
        });
        waterAnim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                if(ballAnimationEndListener!=null&&waterPoints.size()==0){
                    ballAnimationEndListener.isEnd(type);
                }
            }
        });
        waterAnim.start();
    }
    public void endPipeAnimation(){
        if(anim!=null&&waterAnim!=null){
            anim.setRepeatCount(0);
            waterAnim.setRepeatCount(0);
        }
    }
    //设置球体默认行动路径
    public void initBallAnimationPath(Map<Integer,AnimatorPath> animPaths){
        this.animPaths=animPaths;
    }
    //设置默认管道路径
    public void initPipePath(Map<Integer,Path> paths){
        this.paths=paths;
    }

    /**
     * 设置View的属性通过ObjectAnimator.ofObject()的反射机制来调用
     * @param newLoc
     */
    public void setCenter(PathPoint newLoc) {
        mCenterX =(newLoc.mX);
        mCenterY =(newLoc.mY);
        postInvalidate();
    }
    public void setWaterCenter(PathPoint waterLoc){
        mWaterCenterX =waterLoc.mX;
        mWaterCenterY =waterLoc.mY;
        postInvalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if(paths!=null&&paths.size()>0){
            paint.setStyle(Paint.Style.STROKE);
            for (Integer key : paths.keySet()) {
                if(key.equals(WATER)){
                    paint.setColor(getResources().getColor(R.color.water_pipe));
                    paint.setStrokeWidth(pipeWidths.get(key));
                    canvas.drawPath(paths.get(key),paint);
                }else{
                    paint.setColor(getResources().getColor(R.color.yellow_pipe));
                    paint.setStrokeWidth(pipeWidths.get(key));
                    canvas.drawPath(paths.get(key),paint);
                }

            }
        }
       // path.cubicTo(160,660,960,1060,260,1260);
       // canvas.drawPath(path,paint);
        if(!isEnd){
            paint.setStyle(Paint.Style.FILL);
            paint.setColor(getResources().getColor(R.color.yellow_pipe));
            for (PointF point : points) {
                canvas.drawCircle(point.x,point.y,ballWidths.get(type),paint);
            }
            paint.setColor(getResources().getColor(R.color.water_pipe));
            for (PointF waterPoint : waterPoints) {
                canvas.drawCircle(waterPoint.x,waterPoint.y,waterRadius,paint);
            }

        }
      
    }
    public void setBallAnimationEndlistener(BallAnimationEndListener ballAnimationEndlistener){
        this.ballAnimationEndListener=ballAnimationEndlistener;
    }
    public interface BallAnimationEndListener{
        void isEnd(int type);
    }
}
