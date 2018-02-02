package com.hzjytech.coffeeme.widgets.Dosageview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hzjytech.coffeeme.R;

/**
 * Created by Hades on 2016/4/18.
 */
public class DosageRow extends LinearLayout {
    private final Context context;
    private TextView tvCouponrowName;
    private TextView etCouponrowWeight;
    private TextView tvCouponrowUnit;

    public DosageRow(Context context) {
        super(context);
        this.context=context;
        initView();
    }

    public DosageRow(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context=context;
        initView();
    }

    public DosageRow(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context=context;
        initView();
    }

    private void initView() {
        View view=LayoutInflater.from(context).inflate(R.layout.dosagerow,this,true);
        tvCouponrowName=(TextView)view.findViewById(R.id.tvCouponrowName);
        etCouponrowWeight=(TextView)view.findViewById(R.id.etCouponrowWeight);
        tvCouponrowUnit=(TextView)view.findViewById(R.id.tvCouponrowUnit);
    }

    public void setText(DosageRowDesc couponRowDesc){
        tvCouponrowName.setText(couponRowDesc.getName());
        etCouponrowWeight.setText(couponRowDesc.getWeight());
        tvCouponrowUnit.setText(couponRowDesc.getUnit());
    }
}
