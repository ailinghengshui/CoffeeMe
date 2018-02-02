package hehongcan.homefragmentdemo;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import ru.noties.scrollable.CanScrollVerticallyDelegate;
import ru.noties.scrollable.OnFlingOverListener;
import ru.noties.scrollable.OnScrollChangedListener;
import ru.noties.scrollable.ScrollableLayout;

public class MainActivity extends AppCompatActivity {

    private ViewPager mView_pager;
    private View mTabsLayout;
    private ScrollableLayout  scrollableLayout;
    private RecyclerViewFragment mRecyclerViewFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mView_pager = (ViewPager) findViewById(R.id.view_pager);
        mTabsLayout = findViewById(R.id.tabs);
        scrollableLayout = (ScrollableLayout) findViewById(R.id.scrollable_layout);
        initView();
    }

    private void initView() {
        mRecyclerViewFragment = new RecyclerViewFragment();
        final FragmentPagerAdapter adapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public int getCount() {
                return 1;
            }

            @Override
            public Fragment getItem(int position) {
                return mRecyclerViewFragment;
            }};
        mView_pager.setAdapter(adapter);
       // mT.setViewPager(viewPager);

        scrollableLayout.setDraggableView(mTabsLayout);

        scrollableLayout.setCanScrollVerticallyDelegate(new CanScrollVerticallyDelegate() {
            @Override
            public boolean canScrollVertically(int direction) {

                return  mRecyclerViewFragment.canScrollVertically(direction);
            }
        });
        scrollableLayout.setOnFlingOverListener(new OnFlingOverListener() {
            @Override
            public void onFlingOver(int y, long duration) {
                if (mRecyclerViewFragment != null) {
                    mRecyclerViewFragment.onFlingOver(y, duration);
                }
            }
        });

        scrollableLayout.addOnScrollChangedListener(new OnScrollChangedListener() {


            @Override
            public void onScrollChanged(int y, int oldY, int maxY) {

                //                Debug.i("y: %s, oldY: %s, maxY: %s", y, oldY, maxY);

                final float tabsTranslationY;
                if (y < maxY) {
                    tabsTranslationY = .0F;
                } else {
                    tabsTranslationY = y - maxY;
                }

                mTabsLayout.setTranslationY(tabsTranslationY);

                // parallax effect for collapse/expand
                final float ratio = (float) y / maxY;
            }
        });
}}
