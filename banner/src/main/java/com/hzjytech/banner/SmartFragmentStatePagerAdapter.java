package com.hzjytech.banner;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.SparseArray;
import android.view.ViewGroup;

/**
 * Created by Hades on 2016/2/22.
 */
public abstract class SmartFragmentStatePagerAdapter extends FragmentStatePagerAdapter {
    //SparseArray has better performance than hashmap
    //keep track of registered fragments in memory
    private SparseArray<Fragment> registeredFragments=new SparseArray<Fragment>();

    public SmartFragmentStatePagerAdapter(FragmentManager fm) {
        super(fm);
    }

    //Register the fragment when the item is instantiated
    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Fragment fragment= (Fragment) super.instantiateItem(container,position);
        registeredFragments.put(position,fragment);
        return super.instantiateItem(container, position);
    }

    //Unregister when the item is inactive
    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        registeredFragments.remove(position);
        super.destroyItem(container, position, object);
    }

    /**
     * Get the fragment of the position
     * @param position
     * @return
     */
    public Fragment getRegisteredFragment(int position){
        return registeredFragments.get(position);
    }
}

























