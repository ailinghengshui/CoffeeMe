package com.hzjytech.banner;

import android.support.v4.view.ViewPager;

/**
 * Created by Hades on 2016/2/19.
 */
public interface PageIndicator extends ViewPager.OnPageChangeListener {

    void setViewPager(ViewPager viewPager);

    void setViewPager(ViewPager viewPager,int initialPosition);

    void setCurrentItem(int item);

    void setOnPageChangeListener(ViewPager.OnPageChangeListener listener);

    void notifyDataSetChanged();
}
