package com.hzjytech.coffeeme.widgets;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hzjytech.coffeeme.R;
import com.hzjytech.coffeeme.listeners.IMethod1Listener;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * Created by hehongcan on 2018/1/3.
 */

public class SelectLinearLayout extends LinearLayout {

    @BindView(R.id.tv_no_sugar)
    TextView mTvNoSugar;
    @BindView(R.id.tv_three_sugar)
    TextView mTvThreeSugar;
    @BindView(R.id.tv_five_sugar)
    TextView mTvFiveSugar;
    @BindView(R.id.tv_seven_sugar)
    TextView mTvSevenSugar;
    private Context mContext;
    private IMethod1Listener mIMethod1Listener;

    public SelectLinearLayout(Context context) {
        this(context, null);
    }

    public SelectLinearLayout(
            Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);

    }


    public SelectLinearLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.mContext = context;
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View inflate = inflater.inflate(R.layout.tv_select, this, false);
        addView(inflate);
        ButterKnife.bind(this);


    }

    public void setSelectIndex(int index) {
        switch (index) {
            case 0:
                mTvNoSugar.setSelected(true);
                break;
            case 1:
                mTvThreeSugar.setSelected(true);
                break;
            case 2:
                mTvFiveSugar.setSelected(true);
                break;
            case 3:
                mTvSevenSugar.setSelected(true);
                break;
            default:
                break;
        }
    }

   public void setOnSelectListener(IMethod1Listener iMethod1Listener){
        this.mIMethod1Listener=iMethod1Listener;
   }
    @OnClick({R.id.tv_no_sugar, R.id.tv_three_sugar, R.id.tv_five_sugar, R.id.tv_seven_sugar})
    public void onViewClicked(View view) {
        mTvNoSugar.setSelected(false);
        mTvThreeSugar.setSelected(false);
        mTvFiveSugar.setSelected(false);
        mTvSevenSugar.setSelected(false);
        switch (view.getId()) {
            case R.id.tv_no_sugar:
                mTvNoSugar.setSelected(true);
                if(mIMethod1Listener!=null){
                    mIMethod1Listener.OnMethod1Listener(0);
                }
                break;
            case R.id.tv_three_sugar:
                mTvThreeSugar.setSelected(true);
                if(mIMethod1Listener!=null){
                    mIMethod1Listener.OnMethod1Listener(1);
                }
                break;
            case R.id.tv_five_sugar:
                mTvFiveSugar.setSelected(true);
                if(mIMethod1Listener!=null){
                    mIMethod1Listener.OnMethod1Listener(2);
                }
                break;
            case R.id.tv_seven_sugar:
                mTvSevenSugar.setSelected(true);
                if(mIMethod1Listener!=null){
                    mIMethod1Listener.OnMethod1Listener(3);
                }
                break;
        }
    }
}
