package com.hzjytech.coffeeme.Dialogs;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.baidu.mapapi.map.Text;
import com.hzjytech.coffeeme.R;
import com.hzjytech.coffeeme.listeners.IMethod1Listener;
import com.hzjytech.coffeeme.utils.ToastUtil;

/**
 * Created by Hades on 2016/9/28.
 */
public class BottomSelectDialog extends BottomSheetDialogFragment {

    private Context mContext;
    private RecyclerView.LayoutManager mLayoutManager;
    private int[] mIcons;
    private String[] mTitles;
    private IMethod1Listener mMethod1Listener;

    public void setAdapter(Context context, int[] icons,String[] titles, RecyclerView.LayoutManager layoutManager){
        this.mContext=context;
        this.mIcons=icons;
        this.mTitles=titles;
        this.mLayoutManager=layoutManager;
    }

    public void setListener(IMethod1Listener iMethod1Listener){
        this.mMethod1Listener=iMethod1Listener;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_bottom_select,container,false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        RecyclerView rcyViewSelectbottomdialog =(RecyclerView) view.findViewById(R.id.rcyViewSelectbottomdialog);
        rcyViewSelectbottomdialog.setAdapter(new BottomSelectDialogAdapter(mContext,mIcons,mTitles,mMethod1Listener));
        rcyViewSelectbottomdialog.setLayoutManager(mLayoutManager);

        (view.findViewById(R.id.tvSelectbottomdialogCancel)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getDialog().getWindow().getAttributes().windowAnimations= R.style.BottomSelectAnimStyle;
    }


     public static class BottomSelectDialogAdapter extends RecyclerView.Adapter<BottomSelectDialogAdapter.BottomSelectDialogAdapterViewHolder>{

        private final int[] mIcons;
        private final String[] mTitles;
         private final Context mContext;
         private final IMethod1Listener mIMethod1Listener;

         public BottomSelectDialogAdapter(Context context,int[] icons, String[] titles){
           this(context,icons,titles,null);
        }

         public BottomSelectDialogAdapter(Context context,int[] icons, String[] titles,IMethod1Listener iMethod1Listener){
             this.mContext=context;
             this.mIcons=icons;
             this.mTitles=titles;
             this.mIMethod1Listener=iMethod1Listener;
         }

        @Override
        public BottomSelectDialogAdapter.BottomSelectDialogAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view=LayoutInflater.from(parent.getContext()).inflate(R.layout.bottom_select_item,parent,false);
            return new BottomSelectDialogAdapterViewHolder(view);
        }

        @Override
        public void onBindViewHolder(BottomSelectDialogAdapter.BottomSelectDialogAdapterViewHolder holder, final int position) {
            holder.ivBottomselectitemIcon.setImageResource(mIcons[position]);
            holder.tvBottomselectitemName.setText(mTitles[position]);
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(mIMethod1Listener!=null){
                        mIMethod1Listener.OnMethod1Listener(position);
                    }
                }
            });

        }

        @Override
        public int getItemCount() {
            return mIcons.length;
        }

        class BottomSelectDialogAdapterViewHolder extends RecyclerView.ViewHolder{

            private final ImageView ivBottomselectitemIcon;
            private final TextView tvBottomselectitemName;

            public BottomSelectDialogAdapterViewHolder(View itemView) {
                super(itemView);
                ivBottomselectitemIcon=(ImageView)itemView.findViewById(R.id.ivBottomselectitemIcon);
                tvBottomselectitemName=(TextView)itemView.findViewById(R.id.tvBottomselectitemName);
            }
        }
    }

}

