package com.hzjytech.coffeeme.me;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hzjytech.coffeeme.BaseActivity;
import com.hzjytech.coffeeme.R;
import com.hzjytech.coffeeme.widgets.TitleBar;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

/**
 * Created by hehongcan on 2017/2/16.
 */
@ContentView(R.layout.activity_change_recipe_name)
public class ChangeRecipeName extends BaseActivity{
    private static final int RESULT_CHANGED_NAME = 778;
    @ViewInject(R.id.titleBar)
    private TitleBar titleBar;
    @ViewInject(R.id.etUpdatename)
    private EditText etUpDateName;
    @ViewInject(R.id.ivOldClear)
    private ImageView ivOldClear;
    private TextView rightTextView;
    private String name;
    private boolean isAdjust=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initTitle();
        Intent intent = getIntent();
        name = intent.getStringExtra("name");
        LinearLayout ll_container = (LinearLayout) titleBar.getChildAt(2);
        titleBar.addAction(new TitleBar.Action() {
            @Override
            public String getText() {
                return "保存";
            }

            @Override
            public int getDrawable() {
                return 0;
            }

            @Override
            public void performAction(View view) {

            }
        });
        rightTextView = (TextView) ll_container.getChildAt(0);
        rightTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                String result = isAdjust ? etUpDateName.getText().toString() + "(自调)" : etUpDateName.getText().toString();
                intent.putExtra("changedName",result);
                setResult(RESULT_CHANGED_NAME,intent);
                finish();
            }
        });
        etUpDateName.setText(name);
        etUpDateName.setFocusable(true);
        etUpDateName.requestFocus();
        etUpDateName.setSelection(etUpDateName.getText().length());
        etUpDateName.addTextChangedListener(mTextWathcer);
        if(etUpDateName.getText()!=null&&!etUpDateName.getText().toString().equals("")){
            ivOldClear.setVisibility(View.VISIBLE);
            ivOldClear.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    etUpDateName.setText("");
                    ivOldClear.setVisibility(View.INVISIBLE);
                    rightTextView.setTextColor(Color.GRAY);
                    rightTextView.setClickable(false);
                }
            });
        }

    }

    private void initTitle() {
        if(titleBar==null){
            return;
        }
        titleBar.setTitle("名称");
        titleBar.setLeftTextColor(Color.WHITE);
        titleBar.setTitleColor(Color.WHITE);
        titleBar.setLeftText("取消");
        titleBar.setLeftClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_CANCELED,null);
                finish();
            }
        });
    }

    private TextWatcher mTextWathcer=new TextWatcher(){

        private CharSequence temp;

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            temp=s;

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
                if(temp.length()>0){
                    ivOldClear.setVisibility(View.VISIBLE);
                    rightTextView.setTextColor(Color.WHITE);
                    rightTextView.setClickable(true);
                    ivOldClear.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            etUpDateName.setText("");
                            ivOldClear.setVisibility(View.INVISIBLE);
                            rightTextView.setTextColor(Color.GRAY);
                            rightTextView.setClickable(false);
                        }
                    });
                }
                if(temp.length()==0){
                    ivOldClear.setVisibility(View.INVISIBLE);
                    rightTextView.setTextColor(Color.GRAY);
                    rightTextView.setClickable(false);
            }

        }
    };

}
