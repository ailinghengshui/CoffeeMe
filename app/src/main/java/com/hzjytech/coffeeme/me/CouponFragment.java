package com.hzjytech.coffeeme.me;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.hzjytech.coffeeme.R;
import com.hzjytech.coffeeme.fragments.BaseFragment;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

/**
 * A simple {@link Fragment} subclass.
 */

@ContentView(R.layout.fragment_coupon)
public class CouponFragment extends BaseFragment {


    @Event(R.id.ibCouponScan)
    private void onCouponScanClick(View view){
    }

    @ViewInject(R.id.etCouponId)
    private EditText etCouponId;

    @Event(R.id.btnCouponAdd)
    private void onCouponAddClick(View view){

    }

    @ViewInject(R.id.rcyViewCoupon)
    private RecyclerView rcyViewCoupon;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        CouponItemAdapter adapte=new CouponItemAdapter();
        rcyViewCoupon.setAdapter(adapte);
        rcyViewCoupon.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    public CouponFragment() {
        // Required empty public constructor
    }


    class CouponItemAdapter extends RecyclerView.Adapter<CouponItemAdapter.ViewHolder>{
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            Context context=parent.getContext();
            LayoutInflater inflater=LayoutInflater.from(context);

            View view=inflater.inflate(R.layout.coupon_item, parent, false);
            ViewHolder viewHolder=new ViewHolder(view);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {

        }

        @Override
        public int getItemCount() {
            return 2;
        }

        public  class ViewHolder extends RecyclerView.ViewHolder{

            public ViewHolder(View itemView) {
                super(itemView);
            }
        }
    }


}
