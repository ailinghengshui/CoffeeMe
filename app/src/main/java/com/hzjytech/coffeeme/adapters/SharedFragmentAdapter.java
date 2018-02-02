package com.hzjytech.coffeeme.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.hzjytech.coffeeme.R;

/**
 * Created by Hades on 2016/6/4.
 */
public class SharedFragmentAdapter extends RecyclerView.Adapter<SharedFragmentAdapter.ViewHolder>{

    public interface SharedFragmentListener{
        void onSharedFragment(int shared);
    }

    public static final int SHARED_WEIBO =0;
    public static final int SHARED_FRIENDCIRCLE =1;
    public static final int SHARED_WECHAT =2;

    private int[] mImages;
    private String[] mTitles;
    private SharedFragmentListener mSharedFragmentListener;

    public SharedFragmentAdapter(int[] images,String[] titles,SharedFragmentListener sharedFragmentListener){
        this.mImages=images;
        this.mTitles=titles;
        this.mSharedFragmentListener=sharedFragmentListener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private final ImageView ivSharedFragmentItemShow;
        private final TextView tvSharedFragmentItem;

        public ViewHolder(View itemView) {
            super(itemView);
            ivSharedFragmentItemShow=(ImageView)itemView.findViewById(R.id.ivSharedFragmentItemShow);
            tvSharedFragmentItem=(TextView)itemView.findViewById(R.id.tvSharedFragmentItem);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            final int position=getLayoutPosition();
            if(mSharedFragmentListener!=null) {
                switch (mImages[position]) {
                    case R.drawable.icon_share_weibo:
                        mSharedFragmentListener.onSharedFragment(SHARED_WEIBO);
                        break;
                    case R.drawable.icon_share_friendcircle:
                        mSharedFragmentListener.onSharedFragment(SHARED_FRIENDCIRCLE);
                        break;
                    case R.drawable.icon_share_wechat:
                        mSharedFragmentListener.onSharedFragment(SHARED_WECHAT);
                        break;
                }
            }

        }
    }

    @Override
    public SharedFragmentAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context=parent.getContext();
        LayoutInflater inflater=LayoutInflater.from(context);
        View view=inflater.inflate(R.layout.fragment_shared_item, parent, false);
        ViewHolder viewHolder=new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(SharedFragmentAdapter.ViewHolder holder, int position) {
        holder.ivSharedFragmentItemShow.setImageResource(mImages[position]);
        holder.tvSharedFragmentItem.setText(mTitles[position]);

    }

    @Override
    public int getItemCount() {
        return mImages.length;
    }
}