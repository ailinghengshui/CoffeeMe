package com.hzjytech.coffeeme.widgets.row;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;


import java.util.List;

/**
 * Created by Hades on 2016/4/27.
 */
public class GroupRowView extends LinearLayout {

    private static final String TAG=GroupRowView.class.getSimpleName();
    private final Context context;

    public GroupRowView(Context context) {
        super(context);
        this.context=context;
        setOrientation(LinearLayout.VERTICAL);
    }

    public GroupRowView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context=context;
        setOrientation(LinearLayout.VERTICAL);
    }

    public GroupRowView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context=context;
        setOrientation(LinearLayout.VERTICAL);
    }

    public void setData(List<RowViewDesc> rowViewDescs){

        if(rowViewDescs.size()>0&&rowViewDescs!=null){

          for(int i=0;i<rowViewDescs.size();i++){
              switch (rowViewDescs.get(i).getFullLineEnum()){
                  case FULL_LINE:

                      addFullLine();

                      RowView rowView=new RowView(context);
                      rowView.setData(rowViewDescs.get(i));
                      addView(rowView);

                      if(i==(rowViewDescs.size()-1)){
                          addFullLine();
                      }

                      break;
                  case PART_LINE:

                      if(i==0){
                          addFullLine();

                          RowView rowView2=new RowView(context);
                          rowView2.setData(rowViewDescs.get(i));
                          addView(rowView2);
                      }else{
                          addPartLine();

                          RowView rowView2=new RowView(context);
                          rowView2.setData(rowViewDescs.get(i));
                          addView(rowView2);
                      }

                      if(i==(rowViewDescs.size()-1)){
                         addFullLine();
                      }
                      break;
                  case NO_LINE:
                      RowView rowView3=new RowView(context);
                      rowView3.setData(rowViewDescs.get(i));
                      addView(rowView3);
                      break;

              }
          }

        }else{
            Log.d(TAG,"RowViewDesc init error");

        }
    }

    private void addFullLine(){
        TextView textView=new TextView(context);
        textView.setBackgroundColor(Color.GRAY);
        LayoutParams layoutParams= (LayoutParams) textView.getLayoutParams();
        layoutParams.height= (int) com.hzjytech.coffeeme.utils.DensityUtil.px2dp(context,2);
        textView.setLayoutParams(layoutParams);
        addView(textView);
    }

    private void addPartLine(){
        TextView textView=new TextView(context);
        textView.setBackgroundColor(Color.GRAY);
        LayoutParams layoutParams3= (LayoutParams) textView.getLayoutParams();
        layoutParams3.leftMargin= (int) com.hzjytech.coffeeme.utils.DensityUtil.px2dp(context,44);
        layoutParams3.rightMargin= (int) com.hzjytech.coffeeme.utils.DensityUtil.px2dp(context,44);
        layoutParams3.height= (int) com.hzjytech.coffeeme.utils.DensityUtil.px2dp(context,2);
        textView.setLayoutParams(layoutParams3);
        addView(textView);
    }
}
