package com.hzjytech.coffeeme.widgets.Dosageview;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import com.hzjytech.coffeeme.utils.LogUtil;

import java.util.List;

/**
 * Created by Hades on 2016/4/18.
 */
public class DosageGroup extends LinearLayout {
    private final Context context;

    public DosageGroup(Context context) {
        super(context);
        this.context = context;
        setOrientation(VERTICAL);
    }

    public DosageGroup(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOrientation(VERTICAL);
        this.context = context;
    }

    public DosageGroup(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setOrientation(VERTICAL);
        this.context = context;
    }


    public void setData(List<DosageRowDesc> couponRowDescs) {

        if (couponRowDescs != null||couponRowDescs.size()>0) {
            for (DosageRowDesc couponRowDesc : couponRowDescs) {
                DosageRow couponRow = new DosageRow(context);
                couponRow.setText(couponRowDesc);

                addView(couponRow);
            }
        } else {
            LogUtil.d("ConponGroup","set data is null");

        }
    }
}
