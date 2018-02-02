package com.hzjytech.coffeeme.widgets.orderview;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import com.hzjytech.coffeeme.entities.DisplayItems;
import com.hzjytech.coffeeme.entities.Good;
import com.hzjytech.coffeeme.entities.NewGood;
import com.hzjytech.coffeeme.entities.NewOrder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import rx.Observable;
import rx.Subscriber;

/**
 * Created by Hades on 2016/4/29.
 */
public class OrderGroup extends LinearLayout {
    private final Context context;
    private boolean mItemClickable=true;

    public OrderGroup(Context context) {
        super(context);
        this.context = context;
        setOrientation(VERTICAL);
    }

    public OrderGroup(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        setOrientation(VERTICAL);
    }

    public OrderGroup(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        setOrientation(VERTICAL);
    }

    public void clearData() {
        removeAllViews();
    }

    public void setItemData(List<DisplayItems.AppItem> list, int count) {
        clearData();
        if (list != null && list.size() > 0) {
            for (DisplayItems.AppItem appItem : list) {
                OrderRow orderRow = new OrderRow(context,mItemClickable);
                orderRow.setItemText(appItem,count);
                addView(orderRow);
            }
        }
    }

    public void setData(List<NewGood> goods) {
        clearData();
        // TODO: 2017/10/25 相同id sugar合并操作
        if (goods.size() > 0 && goods != null) {
            List<DisplayGood> preDisplayGoods=new ArrayList<DisplayGood>();
            for (NewGood good : goods) {
                preDisplayGoods.add(new DisplayGood(1,good));
            }
            List<DisplayGood> displayGoods =listGoods(preDisplayGoods);
            for (DisplayGood displayGood : displayGoods) {
                OrderRow orderRow = new OrderRow(context,mItemClickable);
                orderRow.setText(displayGood.getNewGood(),displayGood.getCount());
                addView(orderRow);
            }
         /*   for (NewGood good : goods) {
                OrderRow orderRow = new OrderRow(context);
                orderRow.setText(good);
                addView(orderRow);
            }*/
        }

    }
    private List<DisplayGood>listGoods(List<DisplayGood> list){
        for (int i = list.size() - 1; i >= 0; i--) {
            boolean tag = true;
            for (int j = i - 1; j >= 0; j--) {
                if (tag) {
                    if (list.get(i)
                             .getNewGood().getItem().getId() == list.get(j).getNewGood()
                            .getItem().getId()&&list.get(i).getNewGood().getSugar()==list.get(j).getNewGood().getSugar()) {
                        list.get(j)
                                .setCount(list.get(i)
                                        .getCount() + list.get(j)
                                        .getCount());
                        list.remove(i);
                        tag = false;
                    }
                }
            }
        }
        return list;


    }

    public void setItemClickable(boolean itemClickable) {
        mItemClickable = itemClickable;
    }

    class DisplayGood {
        private int count;
        private NewGood mNewGood;

        public DisplayGood(int count, NewGood newGood) {
            this.count = count;
            mNewGood = newGood;
        }

        public int getCount() {
            return count;
        }

        public void setCount(int count) {
            this.count = count;
        }

        public NewGood getNewGood() {
            return mNewGood;
        }

        public void setNewGood(NewGood newGood) {
            mNewGood = newGood;
        }



        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            DisplayGood other = (DisplayGood) obj;
            if (getNewGood().getId() == -1) {
                if (other.getNewGood().getId() != -1)
                    return false;
            } else if (!(getNewGood().getId()==(other.getNewGood().getId())))
                return false;
            if (getNewGood().getSugar() == -1) {
                if (other.getNewGood().getSugar() != -1)
                    return false;
            } else if (!(getNewGood().getSugar()==(other.getNewGood().getSugar())))
                return false;
            return true;
        }
    }
}

