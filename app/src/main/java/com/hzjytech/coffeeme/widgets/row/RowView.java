package com.hzjytech.coffeeme.widgets.row;

import android.content.Context;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hzjytech.coffeeme.R;

/**
 * Created by Hades on 2016/4/26.
 */
public class RowView extends LinearLayout implements View.OnClickListener {
    private final Context context;
    private TextView rowviewName;
    private TextView rowviewDesc;
    private ImageView rowviewIcon;
    private OnClickListener onClickListener;
    private TextView rowviewDescOld;

    public RowView(Context context) {
        super(context);
        this.context=context;
        init();
    }

    public RowView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context=context;
        init();
    }

    public RowView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context=context;
        init();
    }

    private void init() {
        View view = LayoutInflater.from(context).inflate(R.layout.rowview,this,true);
        rowviewName=(TextView)view.findViewById(R.id.rowviewName);
        rowviewDesc=(TextView)view.findViewById(R.id.rowviewDesc);
        rowviewIcon=(ImageView)view.findViewById(R.id.rowviewIcon);
        rowviewDescOld = (TextView) view.findViewById(R.id.rowviewDesc_old);
        view.setOnClickListener(this);
    }

    public void setData(RowViewDesc desc){
        rowviewDescOld.setVisibility(GONE);
        rowviewName.setText(desc.getName());
        rowviewDesc.setText(desc.getDesc());
        if(desc.getIconResId()==0){
            rowviewIcon.setVisibility(GONE);
        }else{
            rowviewIcon.setImageResource(desc.getIconResId());
            this.onClickListener=desc.getOnClickListener();
        }
    }
    public  void setDesc(String oldDesc,String newDesc){
        rowviewDescOld.setVisibility(VISIBLE);
        rowviewDescOld.setText(oldDesc);
        rowviewDesc.setText(newDesc);
        rowviewDescOld.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
    }
    @Override
    public void onClick(View v) {
        if(onClickListener!=null) {
            onClickListener.onClick(v);
        }
    }
}
