package com.hzjytech.coffeeme.widgets.row;

import android.view.View;

/**
 * Created by Hades on 2016/4/27.
 */
public class RowViewDesc {

    private String name;
    private String desc;
    private FullLineEnum fullLineEnum;
    private int iconResId;
    private View.OnClickListener onClickListener;


    public RowViewDesc(String name,String desc,int iconResId){
        this(name,desc,FullLineEnum.NO_LINE,iconResId,null);
    }

    public RowViewDesc(String name,String desc,int iconResId,View.OnClickListener onClickListener){
        this(name,desc,FullLineEnum.NO_LINE,iconResId,onClickListener);
    }
    public RowViewDesc(String name,String desc,FullLineEnum fullLineEnum){
       this(name,desc,fullLineEnum,0,null);
    }

    public RowViewDesc(String name,String desc,FullLineEnum fullLineEnum,int iconResId,View.OnClickListener onClickListener){
        this.name=name;
        this.desc=desc;
        this.fullLineEnum=fullLineEnum;
        this.iconResId=iconResId;
        this.onClickListener=onClickListener;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public int getIconResId() {
        return iconResId;
    }

    public void setIconResId(int iconResId) {
        this.iconResId = iconResId;
    }

    public View.OnClickListener getOnClickListener() {
        return onClickListener;
    }

    public void setOnClickListener(View.OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public FullLineEnum getFullLineEnum() {
        return fullLineEnum;
    }

    public void setFullLineEnum(FullLineEnum fullLineEnum) {
        this.fullLineEnum = fullLineEnum;
    }
}
