package com.hzjytech.coffeeme.widgets.orderview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hzjytech.coffeeme.R;
import com.hzjytech.coffeeme.entities.Good;

import java.text.DecimalFormat;

/**
 * Created by Hades on 2016/4/29.
 */
public class OrderRow extends LinearLayout {
    private final Context context;
    private TextView tvOrderrowName;
    private TextView tvOrderrowCount;
    private TextView tvOrderrowPrice;

    public OrderRow(Context context) {
        super(context);
        this.context=context;
        init();
    }

    public OrderRow(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context=context;
        init();
    }

    public OrderRow(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context=context;
        init();
    }

    private void init() {
        View view=LayoutInflater.from(context).inflate(R.layout.order_row,this,true);
        tvOrderrowName=(TextView)view.findViewById(R.id.tvOrderrowName);
        tvOrderrowCount=(TextView)view.findViewById(R.id.tvOrderrowCount);
        tvOrderrowPrice=(TextView)view.findViewById(R.id.tvOrderrowPrice);
    }

    public void setText(Good good){
       this.setText(good,1);
    }

    public void setText(Good good,int count){
        if(good!=null){
            tvOrderrowName.setText(good.getName());
            tvOrderrowCount.setText(count+"杯");
            DecimalFormat fnum = new DecimalFormat("##0.00");
            tvOrderrowPrice.setText(String.valueOf(fnum.format(good.getCurrent_price()*count)));
        }
    }
}
