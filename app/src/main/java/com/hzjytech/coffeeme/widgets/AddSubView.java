package com.hzjytech.coffeeme.widgets;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hzjytech.coffeeme.R;
import com.hzjytech.coffeeme.utils.ToastUtil;

/**
 * Created by Hades on 2016/4/26.
 */
public class AddSubView extends LinearLayout {
    private Button btnAddSubSub;
    private TextView tvAddSubShow;
    private Button btnAddSubAdd;
    private Context context;
    private AddSubViewable listener;

    public interface AddSubViewable{
        public void onAddSubViewClick(int count);
    }

    public AddSubView(Context context) {
        super(context);
        this.context=context;
        init();
    }

    public AddSubView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context=context;
        init();
    }

    public AddSubView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context=context;
        init();
    }

    public void setListener(AddSubViewable listener){
        this.listener=listener;
    }

    private void init() {
        View view = LayoutInflater.from(context).inflate(R.layout.view_add_sub,this,true);

        btnAddSubSub= (Button) view.findViewById(R.id.btnAddSubSub);
        tvAddSubShow= (TextView) view.findViewById(R.id.tvAddSubShow);
        btnAddSubAdd= (Button) view.findViewById(R.id.btnAddSubAdd);

        tvAddSubShow.setText("1");

        btnAddSubSub.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                AddSubView.this.btnAddSubAdd.setClickable(true);
                int num=getText();
                if ((--num) < 1) {
                    num++;
                    ToastUtil.showShort(context, "数量已为最小");
                    //AddSubView.this.btnAddSubSub.setClickable(false);
                }else {
                    setText(getText() - 1);
                }

                if(listener!=null){
                    listener.onAddSubViewClick(getText());
                }
            }
        });

        btnAddSubAdd.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                AddSubView.this.btnAddSubSub.setClickable(true);
                int num=getText();
                if((++num)>9){
                    num--;
                    ToastUtil.showShort(context,"数量已为最大");
                    //AddSubView.this.btnAddSubAdd.setClickable(false);
                }else{
                    setText(getText()+1);
                }

                if(listener!=null){
                    listener.onAddSubViewClick(getText());
                }

            }
        });
    }

    public void setText(int i){
        AddSubView.this.tvAddSubShow.setText(String.valueOf(i));
    }

    public int  getText(){
        String count=tvAddSubShow.getText().toString().trim();
        return Integer.parseInt(count);
    }
}
