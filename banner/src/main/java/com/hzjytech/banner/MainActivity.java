package com.hzjytech.banner;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import java.util.List;

public class MainActivity extends AppCompatActivity {

//    private TestFragmentAdapter mAdapter;
    private ViewPager mPager;
    private IconPageIndicator mIndicator;
    private MyPagerAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//
////        mAdapter=new TestFragmentAdapter(getSupportFragmentManager());
//        mAdapter=new MyPagerAdapter(getSupportFragmentManager());
//        int[] mIcons=new int[]{
//                R.drawable.icon_banner_group,
//                R.drawable.icon_banner_group,
//                R.drawable.icon_banner_group,
//                R.drawable.icon_banner_group,
//                R.drawable.icon_banner_group,
//                R.drawable.icon_banner_group,
//                R.drawable.icon_banner_group
//        };
//        mAdapter.setmIcons(mIcons);
//        mAdapter.notifyDataSetChanged();
//
//        mPager=(ViewPager)findViewById(R.id.pager);
//        mPager.setAdapter(mAdapter);
//
//        mIndicator=(IconPageIndicator)findViewById(R.id.indicator);
//
//        mIndicator.setViewPager(mPager);


    }

    class TestFragmentAdapter extends FragmentPagerAdapter implements IconPagerAdapter {

        protected final String[] mContent=new String[]{
          "This","is","A","Test"
        };

        protected final int[] mIcons = new int[]{
                R.drawable.icon_banner_group,
                R.drawable.icon_banner_group,
                R.drawable.icon_banner_group,
                R.drawable.icon_banner_group,
                R.drawable.icon_banner_group,
                R.drawable.icon_banner_group,
                R.drawable.icon_banner_group,
                R.drawable.icon_banner_group,

        };

        private int mCount = mIcons.length;

        public TestFragmentAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return TestFragment.newInstance(mContent[position%mContent.length]);
        }

        @Override
        public int getIconResId(int index) {
            return mIcons[index%mIcons.length];
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mContent[position%mContent.length];
        }

        public void setmCount(int mCount) {
            if(mCount>0&&mCount<=10){
                this.mCount=mCount;
                notifyDataSetChanged();
            }
        }

        @Override
        public int getCount() {
            return mCount;
        }
    }

    class MyPagerAdapter extends SmartFragmentStatePagerAdapter implements IconPagerAdapter{

        protected final String[] mContent=new String[]{
                "This","is","A","Test"
        };
        private int[] mIcons=new int[]{};

        public void setmIcons(int[] mIcons){
            this.mIcons=mIcons;
        }

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return TestFragment.newInstance(mContent[position%mContent.length]);
        }

        @Override
        public int getCount() {
            return mIcons.length;
        }

        @Override
        public int getIconResId(int index) {
            return mIcons[index];
        }
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
