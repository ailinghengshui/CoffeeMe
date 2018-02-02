package com.hzjytech.banner;

import android.content.Context;
import android.graphics.Canvas;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;


public class IconPageIndicator extends HorizontalScrollView implements PageIndicator {

    private final IcsLinearLayout mIconsLayout;
    private Runnable mIconSelector;
    private ViewPager.OnPageChangeListener mListener;
    private ViewPager mViewPager;
    private int mSelectorIndex;

    public IconPageIndicator(Context context) {
        this(context, null);
    }

    public IconPageIndicator(Context context, AttributeSet attrs) {
        super(context, attrs);
        setHorizontalScrollBarEnabled(false);

        mIconsLayout=new IcsLinearLayout(context,R.attr.vpiIconPageIndicatorStyle);
        addView(mIconsLayout, new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT, Gravity.CENTER));
    }

    private void animateToIcon(final int position){
        final View iconView=mIconsLayout.getChildAt(position);
        if(mIconSelector!=null){
            removeCallbacks(mIconSelector);
        }
        mIconSelector=new Runnable(){

            @Override
            public void run() {
                final int scrollPos=iconView.getLeft()-(getWidth()-iconView.getWidth())/2;
                smoothScrollTo(scrollPos,0);
                mIconSelector=null;
            }
        };
        post(mIconSelector);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        if(mIconSelector!=null){
            post(mIconSelector);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if(mIconSelector!=null){
            removeCallbacks(mIconSelector);
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        if(mListener!=null){
            mListener.onPageScrollStateChanged(state);
        }

    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        if(mListener!=null){
            mListener.onPageScrolled(position, positionOffset, positionOffsetPixels);
        }
    }

    @Override
    public void onPageSelected(int position) {
        setCurrentItem(position);
        if(mListener!=null){
            mListener.onPageSelected(position);
        }

    }

    @Override
    public void setViewPager(ViewPager viewPager) {
        if(mViewPager==viewPager){
            return ;
        }

        if(mViewPager!=null){
            mViewPager.setOnPageChangeListener(null);
        }
        PagerAdapter adapter=viewPager.getAdapter();
        if(adapter==null){
            throw new IllegalStateException("This ViewPager does not has a adapter");
        }

        mViewPager=viewPager;
        viewPager.setOnPageChangeListener(this);
        notifyDataSetChanged();

    }

    @Override
    public void notifyDataSetChanged() {
        mIconsLayout.removeAllViews();
        IconPagerAdapter iconAdapter=(IconPagerAdapter)mViewPager.getAdapter();
        int count=iconAdapter.getCount();
        for(int i=0;i<count;i++){
            ImageView view = new ImageView(getContext(),null,R.attr.vpiIconPageIndicatorStyle);
            view.setImageResource(iconAdapter.getIconResId(i));

            view.setPadding(0,0,10,0);
            mIconsLayout.addView(view);
        }

        if(mSelectorIndex>count){
            mSelectorIndex=count-1;
        }
        setCurrentItem(mSelectorIndex);
        requestLayout();
    }

    @Override
    public void setViewPager(ViewPager viewPager, int initialPosition) {
        setViewPager(viewPager);
        setCurrentItem(initialPosition);
    }

    @Override
    public void setCurrentItem(int item) {
        if(mViewPager==null){
            throw new IllegalStateException("ViewPager is lost");
        }
        mSelectorIndex=item;
        mViewPager.setCurrentItem(item);

        int tabCount=mIconsLayout.getChildCount();
        for(int i=0;i<tabCount;i++){
            View child=mIconsLayout.getChildAt(i);
            boolean isSelector=(i==item);
            child.setSelected(isSelector);
            if(isSelector){
                animateToIcon(item);
            }
        }

    }

    @Override
    public void setOnPageChangeListener(ViewPager.OnPageChangeListener listener) {
        mListener=listener;
    }

}
