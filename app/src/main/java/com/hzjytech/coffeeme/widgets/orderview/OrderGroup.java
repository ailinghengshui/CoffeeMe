package com.hzjytech.coffeeme.widgets.orderview;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import com.hzjytech.coffeeme.entities.Good;

import java.util.List;

/**
 * Created by Hades on 2016/4/29.
 */
public class OrderGroup extends LinearLayout {
    private final Context context;

    public OrderGroup(Context context) {
        super(context);
        this.context=context;
        setOrientation(VERTICAL);
    }

    public OrderGroup(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context=context;
        setOrientation(VERTICAL);
    }

    public OrderGroup(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context=context;
        setOrientation(VERTICAL);
    }

    public void clearData(){
        removeAllViews();
    }

    public void setData(List<Good> goods){

        if(goods.size()>0&&goods!=null) {

            if(goods.size() > 3) {
                for (int i = 0; i < 3; i++) {
                    OrderRow orderRow = new OrderRow(context);
                    orderRow.setText(goods.get(i));
                    addView(orderRow);
                }
            }else{
                for (int i = 0; i < goods.size(); i++) {
                    OrderRow orderRow = new OrderRow(context);
                    orderRow.setText(goods.get(i));
                    addView(orderRow);
                }
            }
        }

    }
}
