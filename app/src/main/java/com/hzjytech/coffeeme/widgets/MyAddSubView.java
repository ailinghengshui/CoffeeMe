package com.hzjytech.coffeeme.widgets;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hzjytech.coffeeme.R;
import com.hzjytech.coffeeme.utils.ToastUtil;

/**
 * Created by hehongcan on 2017/10/19.
 */

public class MyAddSubView extends LinearLayout {
    private final int mActionBackGroundColor;
    private final int mNumberTextColor;
    private Button btnAddSubSub;
    private TextView tvAddSubShow;
    private Button btnAddSubAdd;
    private Context context;
    private AddSubView.AddSubViewable listener;
    private int mActionTextColor;

    public interface AddSubViewable{
        public void onAddSubViewClick(int count);
    }

    public MyAddSubView(Context context) {
        this(context,null);

    }

    public MyAddSubView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public MyAddSubView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context=context;
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.MyAddSubView);
        mActionBackGroundColor = a.getColor(R.styleable.MyAddSubView_action_backgroud,-1);
        mActionTextColor = a.getColor(R.styleable.MyAddSubView_action_text_color, ContextCompat.getColor(context, R.color.light_red));
        mNumberTextColor = a.getColor(R.styleable.MyAddSubView_number_text_color,ContextCompat.getColor(context, R.color.light_red));
        a.recycle();
        init();
    }

    public void setListener(AddSubView.AddSubViewable listener){
        this.listener=listener;
    }

    private void init() {
        View view = LayoutInflater.from(context).inflate(R.layout.view_my_add_sub,this,true);

        btnAddSubSub= (Button) view.findViewById(R.id.btnAddSubSub);
        tvAddSubShow= (TextView) view.findViewById(R.id.tvAddSubShow);
        btnAddSubAdd= (Button) view.findViewById(R.id.btnAddSubAdd);
        tvAddSubShow.setText("1");
        btnAddSubSub.setEnabled(false);
        btnAddSubSub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyAddSubView.this.btnAddSubAdd.setClickable(true);
                int num=getText();
                if (num < 3) {

                    btnAddSubSub.setEnabled(false);
                    //AddSubView.this.btnAddSubSub.setClickable(false);
                }else {
                    btnAddSubAdd.setEnabled(true);
                }
                setText(getText() - 1);
                if(listener!=null){
                    listener.onAddSubViewClick(getText());
                }
            }
        });

        btnAddSubAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MyAddSubView.this.btnAddSubSub.setClickable(true);
                int num=getText();
                if(num>7){
                    btnAddSubAdd.setEnabled(false);
                    //AddSubView.this.btnAddSubAdd.setClickable(false);
                }else{
                    btnAddSubSub.setEnabled(true);
                }
                setText(getText()+1);
                if(listener!=null){
                    listener.onAddSubViewClick(getText());
                }

            }
        });
    }

    public void setText(int i){
        MyAddSubView.this.tvAddSubShow.setText(String.valueOf(i));
        if(i<2){
            btnAddSubSub.setEnabled(false);
        }
        if(i>8){
            btnAddSubAdd.setEnabled(false);
        }
    }

    public int  getText(){
        String count=tvAddSubShow.getText().toString().trim();
        return Integer.parseInt(count);
    }}
