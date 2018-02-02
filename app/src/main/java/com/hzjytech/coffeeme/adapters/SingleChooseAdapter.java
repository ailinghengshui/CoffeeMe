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
public class SingleChooseAdapter extends RecyclerView.Adapter<SingleChooseAdapter.ViewHolder>{

    public interface SingleChooseListener {
        void onSharedFragment(String shared);
    }

    private String[] mTitles;
    private SingleChooseListener mSharedFragmentListener;

    public SingleChooseAdapter( String[] titles, SingleChooseListener sharedFragmentListener){
        this.mTitles=titles;
        this.mSharedFragmentListener=sharedFragmentListener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {


        private final TextView tvSingleChooseItem;

        public ViewHolder(View itemView) {
            super(itemView);
            tvSingleChooseItem=(TextView)itemView.findViewById(R.id.tvSingleChooseItem);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            final int position=getLayoutPosition();
            if(mSharedFragmentListener!=null) {
                mSharedFragmentListener.onSharedFragment(mTitles[position]);
            }

        }
    }

    @Override
    public SingleChooseAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context=parent.getContext();
        LayoutInflater inflater=LayoutInflater.from(context);
        View view=inflater.inflate(R.layout.single_choose_item, parent, false);
        ViewHolder viewHolder=new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(SingleChooseAdapter.ViewHolder holder, int position) {
        holder.tvSingleChooseItem.setText(mTitles[position]);

    }

    @Override
    public int getItemCount() {
        return mTitles.length;
    }
}