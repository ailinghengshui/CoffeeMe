package com.hzjytech.coffeeme.widgets.xrecyclerview.progressindicator.indicator;

import android.animation.Animator;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.view.View;

import java.util.List;

/**
 * Created by Hades on 2016/5/12.
 */
public abstract class BaseIndicatorController {

    private View mTarget;
    private List<Animator> mAnimators;

    public View getTarget() {
        return mTarget;
    }

    public void setTarget(View mTarget) {
        this.mTarget = mTarget;
    }

    public int getWidth(){
        return mTarget.getWidth();
    }

    public int getHeight(){
        return mTarget.getHeight();
    }

    public void postInvalidate(){
        mTarget.postInvalidate();
    }

    public abstract void draw(Canvas canvas, Paint paint);

    public abstract List<Animator> createAnimation();

    public void initAnimation(){
        mAnimators=createAnimation();
    }

    public void setAnimationStatus(AnimStatus animStatus){
        if(mAnimators==null){
            return;
        }
        int count=mAnimators.size();
        for(int i=0;i<count;i++){
            Animator animator=mAnimators.get(i);
            boolean isRunning=animator.isRunning();
            switch (animStatus){
                case START:
                    if(!isRunning){
                        animator.start();
                    }
                    break;
                case END:
                    if(isRunning){
                        animator.end();
                    }
                    break;
                case CANCEL:
                    if(isRunning){
                        animator.cancel();
                    }
                    break;
            }
        }
    }

    public enum AnimStatus{
        START,END,CANCEL
    }
}
