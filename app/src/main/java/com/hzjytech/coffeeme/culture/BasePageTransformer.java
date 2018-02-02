package com.hzjytech.coffeeme.culture;

import android.support.v4.view.ViewPager;
import android.view.View;

/**
 * Created by Hades on 2016/5/9.
 */
public abstract class BasePageTransformer implements ViewPager.PageTransformer{

    protected ViewPager.PageTransformer mPageTransformer=NonPageTransformer.INSTANCE;

    public static final float DEFAULT_CENTER=0.5f;

    public void transformPage(View view ,float position){
        if(mPageTransformer!=null){
            mPageTransformer.transformPage(view,position);
        }
        pageTransform(view,position);
    }

    protected abstract void pageTransform(View view,float position);
}
