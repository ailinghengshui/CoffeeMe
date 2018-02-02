package com.hzjytech.coffeeme.culture;

import android.support.v4.view.ViewPager;
import android.view.View;

/**
 * Created by Hades on 2016/5/9.
 */
public class ScaleInTransformer extends BasePageTransformer {

    private static final float DEFAULT_MIN_SCALE=0.85f;
    private float mMinScale=DEFAULT_MIN_SCALE;

    public ScaleInTransformer(){};

    public ScaleInTransformer(float minScale){
        this(minScale,NonPageTransformer.INSTANCE);
    }

    public ScaleInTransformer(float minScale,ViewPager.PageTransformer pageTransformer){
        mMinScale=minScale;
        mPageTransformer=pageTransformer;
    }

    @Override
    protected void pageTransform(View view, float position) {
        int pageWidth=view.getWidth();
        int pageHeight=view.getHeight();

        view.setPivotX(pageHeight / 2);
        view.setPivotY(pageWidth/2);

        if(position<-1){
            view.setScaleX(mMinScale);
            view.setScaleY(mMinScale);
            view.setPivotX(pageWidth);
        }else if(position<=1){
            if(position<0){
                float scaleFactor = (1 + position) * (1 - mMinScale) + mMinScale;
                view.setScaleX(scaleFactor);
                view.setScaleY(scaleFactor);

                view.setPivotX(pageWidth * (DEFAULT_CENTER + (DEFAULT_CENTER * -position)));
            }else{
                float scaleFactor = (1 - position) * (1 - mMinScale) + mMinScale;
                view.setScaleX(scaleFactor);
                view.setScaleY(scaleFactor);
                view.setPivotX(pageWidth * ((1 - position) * DEFAULT_CENTER));
            }
        }else{
            view.setPivotX(0);
            view.setScaleX(mMinScale);
            view.setScaleY(mMinScale);
        }

    }
}
