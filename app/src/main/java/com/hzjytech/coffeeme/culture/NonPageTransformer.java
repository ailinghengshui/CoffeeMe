package com.hzjytech.coffeeme.culture;

import android.support.v4.view.ViewPager;
import android.view.View;

/**
 * Created by Hades on 2016/5/9.
 */
public class NonPageTransformer implements ViewPager.PageTransformer{
    public  static final ViewPager.PageTransformer INSTANCE=new NonPageTransformer();
    @Override
    public void transformPage(View page, float position) {
        page.setScaleX(0.999f);


    }
}
