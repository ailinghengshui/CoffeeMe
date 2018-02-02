package com.hzjytech.coffeeme.widgets;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import com.hzjytech.coffeeme.R;
import com.hzjytech.coffeeme.entities.AppDosage;
import com.hzjytech.coffeeme.entities.AppDosages;
import com.hzjytech.coffeeme.entities.Material;
import com.hzjytech.coffeeme.utils.DensityUtil;
import com.hzjytech.coffeeme.utils.LogUtil;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by hehongcan on 2016/12/2.
 */
public class CoffeeCupView extends LinearLayout {
    private Context context;
    private ArrayList<AppDosages> appDosages=new ArrayList<>();
    private int measuredHeight;
    private int measuredWidth;
    private TopCupView topCupView;
    private ArrayList<Material> materials;
    private Material coffeeMaterial;
    private Material sugarMaterial;
    private Material milkMaterila;
    private Material chocolateMaterial;
    private int useFullHeight;
    private int bottomHeight;
    private float itemRealHeight;
    private ArrayList<AppDosages> restoreAppDosages;
    private static int MAXVOLUME=240;
    private ArrayList<AppDosages> cupAppdosages;
    private ValueAnimator selfWaterAnimation;
    private ValueAnimator waterAnimation;
    private ValueAnimator selfWaterAnimation1;
    private ValueAnimator waterAnimation1;
    public CoffeeCupView(Context context) {
        this(context,null);
    }

    public CoffeeCupView(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public CoffeeCupView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        cupAppdosages = new ArrayList<>();

    }

    private void initData() {
        if(materials!=null){
            for (Material material : materials) {
                switch (material.getName()){
                    case "咖啡豆":
                        coffeeMaterial = material;
                        break;
                    case "糖":
                        sugarMaterial = material;
                        break;
                    case "奶粉":
                        milkMaterila = material;
                        break;
                    case "巧克力粉":
                        chocolateMaterial = material;
                        break;
                }
            }
        }
    }

    private void initView(Context context) {
        //设置总布局为垂直布局
        setOrientation(LinearLayout.VERTICAL);
        //初始添加一个完全透明的view在顶部占位，weight=1
        View view = new View(context);
        view.setBackgroundColor(Color.TRANSPARENT);
        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, 0, 1f);
        view.setLayoutParams(params);
        this.addView(view);
        //获取每个条目的能给出的最大高度
    }
    /**
     * 要求：根据咖啡杯剪切宽度
     *       每个view放在前面一个的上面,addView(1,view)
     *       可以切换view的位置
     *
     */
    /**
     * 初始化数据
     * @param
     */
    public void setData(ArrayList<AppDosages> fineAppDosages, Context context){
        removeAllViews();
        this.context=context;
        this.restoreAppDosages=fineAppDosages;
        appDosages.clear();
        if(fineAppDosages==null||fineAppDosages.size()==0){
            return;
        }
        for (AppDosages appDosage : fineAppDosages) {
            appDosages.add(new AppDosages(appDosage.getId(),appDosage.getWeight(),appDosage.getSelfWater(),appDosage.getWater(),appDosage.getSequence(),appDosage.getMaterial_name()));
        }
        LogUtil.e("fineAppDOsages",fineAppDosages.toString());
        initView(context);
        if (measuredHeight!=0){
            float realTotalHeight = measuredHeight;
            //每个item的最大高度
            for (int i=0;i<appDosages.size();i++) {
                AppDosages appDosage = appDosages.get(i);
                float ratio = (appDosage.getWater()+appDosage.getSelfWater()) / (float) (MAXVOLUME);
                itemRealHeight =realTotalHeight*ratio;
                View view = new View(context);
                if(appDosage==null||appDosage.getMaterial_name()==null){
                    return;
                }
                switch (appDosage.getMaterial_name()){
                    case "糖":
                        view.setBackgroundColor(getResources().getColor(R.color.sugar_bottom_water));
                        break;
                    case "巧克力粉":
                        view.setBackgroundColor(getResources().getColor(R.color.chocolate_bottom_water));
                        break;
                    case "咖啡浓度":
                        view.setBackgroundColor(getResources().getColor(R.color.coffee_bottom_water));
                        break;
                    case "奶粉":
                        view.setBackgroundColor(getResources().getColor(R.color.milk_bottom_water));
                        break;
                    default:
                        break;
                }
                addView(view);
                LayoutParams layoutParams = (LayoutParams) view.getLayoutParams();
                layoutParams.height= (int) itemRealHeight;
                view.setLayoutParams(layoutParams);

            }
        }

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);

    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        measuredHeight = this.getMeasuredHeight();
        measuredWidth = this.getMeasuredWidth();
        setData(restoreAppDosages,context);

    }

    public void setMaterialData(ArrayList<Material> materialData) {
        this.materials = materialData;
        initData();
    }

    public void addMaterialWater(ArrayList<AppDosages> oldAppdosages, final ArrayList<AppDosages> appDosages, final Context context) {
        cupAppdosages.clear();
        if(appDosages==null||appDosages.size()==0){
            return;
        }
        for (AppDosages appDosage : appDosages) {
            cupAppdosages.add(new AppDosages(appDosage.getId(),appDosage.getWeight(),appDosage.getSelfWater(),appDosage.getWater(),appDosage.getSequence(),appDosage.getMaterial_name()));
        }
        Collections.reverse(cupAppdosages);
        Collections.reverse(oldAppdosages);
        if(oldAppdosages!=null&&oldAppdosages.size()>0&&oldAppdosages.get(0).getMaterial_name()!=null&&oldAppdosages.get(0).getMaterial_name().equals(cupAppdosages.get(0).getMaterial_name())){
            //继续添加，改变物料值
            float nowSelfWater = cupAppdosages.get(0).getSelfWater();
            float nowWater = cupAppdosages.get(0).getWater();
            float beforeSelfWater = oldAppdosages.get(0).getSelfWater();
            float beforeWater = oldAppdosages.get(0).getWater();
            selfWaterAnimation = ValueAnimator.ofFloat(beforeSelfWater, nowSelfWater);
            selfWaterAnimation.setDuration(500);
            selfWaterAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    cupAppdosages.get(0).setSelfWater((Float) valueAnimator.getAnimatedValue());
                    setData(cupAppdosages,context);
                }
            });

            selfWaterAnimation.start();
            waterAnimation = ValueAnimator.ofFloat(beforeWater, nowWater);
            waterAnimation.setDuration(500);
            waterAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    cupAppdosages.get(0).setWater((Float) valueAnimator.getAnimatedValue());
                    setData(cupAppdosages,context);
                }
            });

            waterAnimation.start();
        }else{
            float nowSelfWater = cupAppdosages.get(0).getSelfWater();
            float nowWater = cupAppdosages.get(0).getWater();
            selfWaterAnimation1 = ValueAnimator.ofFloat(0, nowSelfWater);
            selfWaterAnimation1.setDuration(500);
            selfWaterAnimation1.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    cupAppdosages.get(0).setSelfWater((Float) valueAnimator.getAnimatedValue());
                    setData(cupAppdosages,context);
                }
            });

            selfWaterAnimation1.start();
            waterAnimation1 = ValueAnimator.ofFloat(0, nowWater);
            waterAnimation1.setDuration(500);
            waterAnimation1.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                    LogUtil.e("value",valueAnimator.getAnimatedValue()+"");
                    cupAppdosages.get(0).setWater((Float) valueAnimator.getAnimatedValue());
                    setData(cupAppdosages,context);
                }
            });

            waterAnimation1.start();
        }
    }
    public void clearData() {
       removeAllViews();

    }
}