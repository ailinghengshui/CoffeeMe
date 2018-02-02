package com.hzjytech.coffeeme.widgets;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.OnScrollListener;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;

import com.hzjytech.coffeeme.R;


/**
 * Created by Hades on 2016/5/5.
 */
public class RefreshLayout extends SwipeRefreshLayout {
    private final int mTouchSlop;
    private  View mFootView;
    private final Context context;
    private RecyclerView mRecyclerView;
    private int mYDown;
    private int mLastY;
    private OnLoadListener mOnLoadListener;

    private boolean isLoading=false;

    public RefreshLayout(Context context) {
        this(context, null);
    }

    public RefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context=context;

        mTouchSlop= ViewConfiguration.get(context).getScaledTouchSlop();

    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        if(mRecyclerView==null){
            getRecyclerView();
        }
    }

    private void getRecyclerView() {
        int childs=getChildCount();
        if(childs>0){
            View childView=getChildAt(0);
            if(childView instanceof RecyclerView){
                mRecyclerView=(RecyclerView)childView;
                mRecyclerView.addOnScrollListener(new OnScrollListener() {
                    @Override
                    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                        super.onScrollStateChanged(recyclerView, newState);
                    }

                    @Override
                    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                        if(canLoad()){
                            loadData();
                        }
                    }
                });
            }
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        final int action=ev.getAction();
        switch (action){
            case MotionEvent.ACTION_DOWN:
                mYDown=(int )ev.getRawY();
                break;
            case MotionEvent.ACTION_MOVE:
                mLastY=(int)ev.getRawY();
                break;
            case MotionEvent.ACTION_UP:
                if(canLoad()){
                    loadData();
                }
                break;
            default:
                break;
        }
        return super.dispatchTouchEvent(ev);
    }

    private boolean canLoad(){
        return isBottom()&&!isLoading&&isPullUp();
    }

    private boolean isBottom() {
        if(mRecyclerView!=null&&mRecyclerView.getAdapter()!=null){
            RecyclerView.LayoutManager layoutManager=mRecyclerView.getLayoutManager();
            if(layoutManager instanceof LinearLayoutManager){
                return ((LinearLayoutManager) layoutManager).findLastVisibleItemPosition()==(mRecyclerView.getAdapter().getItemCount()-1);
            }else if (layoutManager instanceof GridLayoutManager){
            }else if(layoutManager instanceof StaggeredGridLayoutManager){
            }
        }
        return false;

    }

    private boolean isPullUp(){
        return (mYDown-mLastY)>=mTouchSlop;
    }

    private void loadData(){
        if(mOnLoadListener!=null){
            setLoading(true);
            mOnLoadListener.onLoad();
        }
    }

    public void setLoading(boolean loading){
        isLoading=loading;
        if(isLoading){
            if(mFootView==null){
                mFootView= LayoutInflater.from(context).inflate(R.layout.footer,null,false);
            }
            mRecyclerView.getLayoutManager().addView(mFootView,mRecyclerView.getAdapter().getItemCount());
        }else{
            mRecyclerView.getLayoutManager().removeViewAt(mRecyclerView.getAdapter().getItemCount());
            mYDown=0;
            mLastY=0;

        }
    }

    public void setOnLoadListener(OnLoadListener loadListener){
        mOnLoadListener=loadListener;
    }

    public static interface OnLoadListener{
         void onLoad();
    }

}
