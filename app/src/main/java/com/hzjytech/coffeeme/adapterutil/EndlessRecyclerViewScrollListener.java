package com.hzjytech.coffeeme.adapterutil;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;

import com.hzjytech.coffeeme.widgets.collectionview.LayoutManager;

/**
 * Created by Hades on 2016/5/4.
 */
public abstract class EndlessRecyclerViewScrollListener extends RecyclerView.OnScrollListener {

    // The minimum amount of items to have below your current scroll position
    // before loading more.
    private int visibleThreshold = 5;
    // The current offset index of data you have loaded
    private int currentPage = 0;
    // The total number of items in the dataset after the last load
    private int previousTotalItemCount = 0;
    // True if we are still waiting for the last set of data to load.
    private boolean loading = true;
    // Sets the starting page index
    private int startingPageIndex = 0;

    private int defaultNoFooterViewType=-1;
    private int footerViewType=-1;

    private String mTag="scroll-listener";

    RecyclerView.LayoutManager mLayoutManager;

    public EndlessRecyclerViewScrollListener(LayoutManager layoutManager) {
        init();
        this.mLayoutManager = layoutManager;
    }

    public EndlessRecyclerViewScrollListener(GridLayoutManager layoutManager) {
        init();
        this.mLayoutManager = layoutManager;
        visibleThreshold = visibleThreshold * layoutManager.getSpanCount();
    }

    public EndlessRecyclerViewScrollListener(StaggeredGridLayoutManager layoutManager) {
        init();
        this.mLayoutManager = layoutManager;
        visibleThreshold = visibleThreshold * layoutManager.getSpanCount();
    }

    private void init(){
        footerViewType=getFooterViewType(defaultNoFooterViewType);
        startingPageIndex=getStartingPageIndex();

        int threshold=getVisibleThreshold();
        if(threshold>visibleThreshold){
            visibleThreshold=threshold;
        }
    }

    // This happens many times a second during a scroll, so be wary of the code you place here.
    // We are given a few useful parameters to help us work out if we need to load some more data,
    // but first we check if we are waiting for the previous load to finish.
    @Override
    public void onScrolled(RecyclerView view, int dx, int dy) {

        if(dy<=0) return;
        RecyclerView.Adapter adapter=view.getAdapter();
        int totalItemCount=adapter.getItemCount();
        int lastVisibleItemPosition=getLastVisibleItemPosition();

        boolean isAllowLoadMore=(lastVisibleItemPosition+visibleThreshold)>totalItemCount;

        if(isAllowLoadMore){
            if(isUseFooterView()){
                if(!isFooterView(adapter)){
                    if(totalItemCount<previousTotalItemCount){
                        this.currentPage=this.startingPageIndex;
                    }else if(totalItemCount==previousTotalItemCount){
                        currentPage=currentPage==startingPageIndex?startingPageIndex:--currentPage;
                    }
                    loading=false;
                }
            }else{
                if(totalItemCount>previousTotalItemCount)loading=false;
            }

            if(!loading){
                previousTotalItemCount=totalItemCount;
                currentPage++;
                onLoadMore(currentPage,totalItemCount);
                loading=true;
            }
        }

    }

    @Override
    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
        super.onScrollStateChanged(recyclerView, newState);
    }

    public boolean isUseFooterView(){
        boolean isUse=footerViewType!=defaultNoFooterViewType;
        return isUse;
    }

    public boolean isFooterView(RecyclerView.Adapter padapter){
        boolean isFooterView=false;
        int ptotalItemCount=padapter.getItemCount();

        if(ptotalItemCount>0){
            int lastPosition=ptotalItemCount-1;
            int lastViewType=padapter.getItemViewType(lastPosition);
            isFooterView=lastViewType==footerViewType;
        }
        return isFooterView;
    }

    private int getLastVisibleItemPosition(){
        int lastVisibleItemPosition=0;

        if(mLayoutManager instanceof StaggeredGridLayoutManager){
            int[] lastVisibleItemPositions=((StaggeredGridLayoutManager)mLayoutManager).findLastVisibleItemPositions(null);
            lastVisibleItemPosition=getLastVisibleItem(lastVisibleItemPositions);
        }else if(mLayoutManager instanceof  LinearLayoutManager){
            lastVisibleItemPosition=((LinearLayoutManager)mLayoutManager).findLastVisibleItemPosition();
        }else if(mLayoutManager instanceof GridLayoutManager){
            lastVisibleItemPosition=((GridLayoutManager)mLayoutManager).findLastVisibleItemPosition();
        }
        return lastVisibleItemPosition;
    }

    public int getLastVisibleItem(int[] lastVisibleItemPositions) {
        int maxSize = 0;
        for (int i = 0; i < lastVisibleItemPositions.length; i++) {
            if (i == 0) {
                maxSize = lastVisibleItemPositions[i];
            }
            else if (lastVisibleItemPositions[i] > maxSize) {
                maxSize = lastVisibleItemPositions[i];
            }
        }
        return maxSize;
    }

    public abstract int getFooterViewType(int defaultNoFooterViewType);

    // Defines the process for actually loading more data based on page
    public abstract void onLoadMore(int page, int totalItemsCount);

    public int getVisibleThreshold(){
        return visibleThreshold;
    }

    public int getStartingPageIndex(){
        return startingPageIndex;
    }
}
