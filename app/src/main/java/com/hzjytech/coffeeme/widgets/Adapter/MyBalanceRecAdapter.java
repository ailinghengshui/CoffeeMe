package com.hzjytech.coffeeme.widgets.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hzjytech.coffeeme.R;


/**
 * Created by dblr4287 on 2016/5/11.
 */
public class MyBalanceRecAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private LayoutInflater mLayoutInflater;
    private Context context;
    private String [] titles;


    public MyBalanceRecAdapter(Context context,String[] titles){
        this.titles = titles;
        this.context = context;
        mLayoutInflater = LayoutInflater.from(context);
    }




    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        return new ItemViewHolder(mLayoutInflater.inflate(R.layout.mybalance_item, parent, false));

    }



    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ItemViewHolder) {
            //((ItemViewHolder) holder).mTextView.setText(titles[position]);
        }
    }

    @Override
    public int getItemCount() {
        return titles == null ? 0 : titles.length;
    }




    public static class ItemViewHolder extends RecyclerView.ViewHolder{
        //TextView mTextView;
        public ItemViewHolder(View itemView) {
            super(itemView);
            //mTextView=(TextView)itemView.findViewById(R.id.tv_item1_text);
        }
    }


}
