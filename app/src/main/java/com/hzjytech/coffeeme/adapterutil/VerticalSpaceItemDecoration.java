package com.hzjytech.coffeeme.adapterutil;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by Hades on 2016/4/5.
 */
public class VerticalSpaceItemDecoration extends RecyclerView.ItemDecoration {

    private final int mVerticalSpaceHeight;

    public VerticalSpaceItemDecoration(int mVerticalSpaceHeight){
        this.mVerticalSpaceHeight=mVerticalSpaceHeight;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        if(parent.getChildAdapterPosition(view)!=parent.getAdapter().getItemCount()-1) {
            outRect.bottom = mVerticalSpaceHeight;
        }
    }
}
