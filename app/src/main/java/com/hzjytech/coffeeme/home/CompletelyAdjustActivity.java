package com.hzjytech.coffeeme.home;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.TimeInterpolator;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Path;
import android.graphics.drawable.AnimationDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewParent;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.BounceInterpolator;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alipay.security.mobile.module.commonutils.LOG;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hzjytech.coffeeme.BaseActivity;
import com.hzjytech.coffeeme.Dialogs.ITwoButtonClick;
import com.hzjytech.coffeeme.Dialogs.NewNameCoffeeDialog;
import com.hzjytech.coffeeme.Dialogs.TitleButtonsDialog;
import com.hzjytech.coffeeme.R;
import com.hzjytech.coffeeme.collect.NewMyCoffeesActivity;
import com.hzjytech.coffeeme.configurations.Configurations;
import com.hzjytech.coffeeme.entities.AppDosages;
import com.hzjytech.coffeeme.entities.Material;
import com.hzjytech.coffeeme.service.MusicService;
import com.hzjytech.coffeeme.utils.AppUtil;
import com.hzjytech.coffeeme.utils.DensityUtil;
import com.hzjytech.coffeeme.utils.LogUtil;
import com.hzjytech.coffeeme.utils.MyMath;
import com.hzjytech.coffeeme.utils.NetUtil;
import com.hzjytech.coffeeme.utils.ScreenUtil;
import com.hzjytech.coffeeme.utils.SharedPrefUtil;
import com.hzjytech.coffeeme.utils.SignUtils;
import com.hzjytech.coffeeme.utils.TimeUtil;
import com.hzjytech.coffeeme.utils.ToastUtil;
import com.hzjytech.coffeeme.utils.UserUtils;
import com.hzjytech.coffeeme.widgets.AnimatorPath.AnimatorPath;
import com.hzjytech.coffeeme.widgets.CoffeeCupView;
import com.hzjytech.coffeeme.widgets.CompletelyCustomView;
import com.hzjytech.coffeeme.widgets.CustomView.PathView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import cn.jpush.android.api.JPushInterface;

/**
 * Created by hehongcan on 2017/3/16.
 */
@ContentView(R.layout.activity_completely_adjust)
public class CompletelyAdjustActivity extends BaseActivity implements View.OnTouchListener {
    private static CompletelyAdjustActivity mInstance;
    @ViewInject(R.id.ball_pathview)
    private PathView pathView;
    @ViewInject(R.id.iv_adjust_title)
    private ImageView iv_title;
    @ViewInject(R.id.fl_body)
    private FrameLayout fl_body;
    @ViewInject(R.id.iv_adjust_back)
    private ImageView iv_adjust_back;
    @ViewInject(R.id.bt_music)
    private ImageView bt_music;
    @ViewInject(R.id.ll_adjust_bottom_button)
    private LinearLayout ll_adjust_bottom_button;
    @ViewInject(R.id.waterView)
    private CompletelyCustomView waterView;
    @ViewInject(R.id.tv_volume)
    private TextView tvTotleVolume;
    @ViewInject(R.id.tv_price)
    private TextView tvTotlePrice;
    @ViewInject(R.id.button_coffee)
    private ImageView bt_coffee;
    @ViewInject(R.id.button_milk)
    private ImageView bt_milk;
    @ViewInject(R.id.button_sugar)
    private ImageView bt_sugar;
    @ViewInject(R.id.button_chocolate)
    private ImageView bt_chocolate;
    @ViewInject(R.id.iv_material_coffee)
    private ImageView iv_material_coffee;
    @ViewInject(R.id.iv_material_sugar)
    private ImageView iv_material_sugar;
    @ViewInject(R.id.iv_material_milk)
    private ImageView iv_material_milk;
    @ViewInject(R.id.iv_material_chocolate)
    private ImageView iv_material_chocolate;
    @ViewInject(R.id.cup)
    private CoffeeCupView cupView;
    @ViewInject(R.id.tv_notify)
    private TextView tv_notify;
    @ViewInject(R.id.gear)
    private ImageView gear;
    private ArrayList<Material> materials;
    private ArrayList<AppDosages> appDosages;
    private ArrayList<AppDosages> restoreAppdosages;
    private float totleWater;
    private double totleCoffeeMaterial;
    private double totleSugarMaterial;
    private double totleMilkMaterial;
    private double totleChocolateMaterial;
    private float totleCoffeeWater;
    private float totleSugarWater;
    private float totleMilkWater;
    private long totleChocolateWater;
    private int index = 0;
    private Material coffeeMaterial;
    private Material sugarMaterial;
    private Material milkMaterial;
    private Material chocolateMaterial;
    private float totleVolume;
    private double totlePrice;
    private boolean notNeedReDescend=false;
    private int type = 0;
    private static final int COFFEE = 1;
    private static final int SUGAR = 2;
    private static final int MILK = 3;
    private static final int CHOCOLATE = 4;
    private static final int MAXVOLUE = 240;
    private static final float BASICPRICE = 2.0f;
    private double mChocolateWater_rate;
    private double mChocolateUnitPrice;
    private double mChocolate_max_weight;
    private double mMilk_max_weight;
    private double mMilkUnitPrice;
    private double mMilkWater_rate;
    private double mSugar_max_weight;
    private double mSugarUnitPrice;
    private double mSugarWater_rate;
    private double mCoffee_max_weight;
    private double mCoffeeUnitPrice;
    private double mCoffeeWater_rate;
    Handler mBallHandler=new Handler();
    Handler tipHandler=new Handler();
    private DecimalFormat df;
    private boolean isAnimation;
    private ObjectAnimator trsAnimation;
    private boolean isContinue;
    private int oldType;
    private AppDosages continueAppDosage;
    private int top;
    private int inTop;
    //解决触发限制条件后管道继续流出液体的tag
    private boolean isUnTouch=false;
    //解决手指press多个button后的bug
    private boolean isPressed=false;
    private boolean gearIsRunning=false;
    //是否处于搅拌状态，用于区分连续点击相同物料时是否间隔一段时间的差异
    private boolean isRunning=false;
    //用于处理多次点击同一按钮时，保存第一次点击之前的物料信息
    private boolean hasRestore=false;
    //用于记录音乐按钮的开关
    private boolean musicNotOpen=false;
    //音乐旋转动画
    private ObjectAnimator musicRoAnimation;
    private boolean isToFine;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initData();
        initView();
        pathView.setBallAnimationEndlistener(new PathView.BallAnimationEndListener() {
            @Override
            public void isEnd(int type) {
                //关闭齿轮动画
                if(type==pathView.COFFEE){
                    closeGearAnimation();
                }
                //开始下流
                endMaterialAnim(type);
                realeaseButton(type);
                isRunning =false;
                hasRestore=false;
                cupView.addMaterialWater(restoreAppdosages,appDosages,CompletelyAdjustActivity.this);
            }
        });
        //开启音乐
        musicRoAnimation = ObjectAnimator.ofFloat(bt_music, "rotation", 0, 360);
        musicRoAnimation.setRepeatCount(ValueAnimator.INFINITE);
        musicRoAnimation.setDuration(3000);
        musicRoAnimation.setInterpolator(new LinearInterpolator());
        tipHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                setTipText("水是自动添加的哦！");
                tipHandler.postDelayed(this,60000);
            }
        },60000);


    }
 private void startMusic(){
     Intent intent = new Intent(CompletelyAdjustActivity.this, MusicService.class);
     startService(intent);
     musicRoAnimation.start();
 }
    private void closeMusic(){
        Intent intent = new Intent(CompletelyAdjustActivity.this, MusicService.class);
        stopService(intent);
        musicRoAnimation.end();
    }
  //button放开
    private void realeaseButton(int type) {
        //下料
        switch (type){
            case COFFEE:
                waterView.startRunningWater(waterView.COFFEE);
                // startWater(CompletelyCustomView.COFFEE);
                break;
            case SUGAR:
                waterView.startRunningWater(waterView.SUGAR);
                break;
            case MILK:
                waterView.startRunningWater(waterView.MILK);
                break;
            case CHOCOLATE:
                waterView.startRunningWater(waterView.CHOCOLATE);
                break;
        }


    }
    //button按下的处理过程
    private void pressButton(int id) {
        isRunning=true;
        switch (id){
            case R.id.button_coffee:
                startPipe(pathView.COFFEE);
               // startWater(CompletelyCustomView.COFFEE);
                break;
            case R.id.button_sugar:
                startPipe(pathView.SUGAR);
                break;
            case R.id.button_milk:
                startPipe(pathView.MILK);
                break;
            case R.id.button_chocolate:
                startPipe(pathView.CHOCOLATE);

                break;
        }
    }

    //开启管道动画
    private void startPipe(final int type) {
        mBallHandler.post(new Runnable() {
            @Override
            public void run() {
                pathView.startPipeAnimation(type);
                mBallHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        pathView.startPipeAnimation(type);
                    }
                },250);
                mBallHandler.postDelayed(this,1000);
            }
        });
    }

    private void initView() {
        mInstance=this;
        df = new DecimalFormat("0.00");
        initButton();
        setColume(0, 2.00f);
        initOnLayoutListener();
        iv_adjust_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        bt_music.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(musicNotOpen){
                    musicNotOpen=false;
                    bt_music.setImageResource(R.drawable.music_open);
                    startMusic();
                    SharedPrefUtil.putBoolean("musicNotOpen",false);
                }else{
                    musicNotOpen=true;
                    bt_music.setImageResource(R.drawable.music_close);
                    closeMusic();
                    SharedPrefUtil.putBoolean("musicNotOpen",true);

                }
            }
        });
        initPipeView();
       tv_notify.setOnTouchListener(new View.OnTouchListener() {
           @Override
           public boolean onTouch(View view, MotionEvent motionEvent) {

            if(motionEvent.getAction()==MotionEvent.ACTION_MOVE){
                tipHandler.removeCallbacksAndMessages(null);
                ObjectAnimator anim2 = ObjectAnimator.ofFloat(tv_notify, "translationY", top,-inTop);
                anim2.setDuration(300);
                anim2.start();
            }
               return false;
           }
       });
    }

    private float  getXFromXml(int resId){
       return getResources().getDimension(resId);
   }
//初始化管道动画的各初始值
    private void initPipeView() {
        //咖啡管道数据
        float startY =getXFromXml(R.dimen.startY);
        float coffeeStartX =getXFromXml(R.dimen.coffeeStartX);
        float firstCornerY = getXFromXml(R.dimen.firstCornerY);
        float secondCornerX =getXFromXml(R.dimen.secondCornerX);
        float secondCornerY = getXFromXml(R.dimen.secondCornerY);
        float endX = getXFromXml(R.dimen.endX);
        //糖管道数据
        float sugarStartX = getXFromXml(R.dimen.sugarStartX);
        float sugarStartY = getXFromXml(R.dimen.sugarStartY);
        float sugarFirstCornerY = getXFromXml(R.dimen.sugarFirstCornerY);
        float sugarControlX = getXFromXml(R.dimen.sugarControlX);
        float sugarControlY = getXFromXml(R.dimen.sugarControlY);
        float sugarEndY = getXFromXml(R.dimen.sugarEndY);
        float sugarEndX = getXFromXml(R.dimen.sugarEndX);
        //奶粉管道数据
        float milkStartX = getXFromXml(R.dimen.milkStartX);
        float milkStartY = getXFromXml(R.dimen.milkStartY);
        float milkFirstCornerY = getXFromXml(R.dimen.milkFirstCornerY);
        float milkControlX = getXFromXml(R.dimen.milkControlX);
        float milkControlY = getXFromXml(R.dimen.milkControlY);
        float milkEndY = getXFromXml(R.dimen.milkEndY);
        float milkEndX = getXFromXml(R.dimen.milkEndX);
        //巧克力管道数据
        float chocolateStartY =getXFromXml(R.dimen.chocolateStartY);
        float chocolateStartX =getXFromXml(R.dimen.chocolateStartX);
        float chocolateFirstCornerY = getXFromXml(R.dimen.chocolateFirstCornerY);
        float chocolateSecondCornerX =getXFromXml(R.dimen.chocolateSecondCornerX);
        float chocolateSecondCornerY = getXFromXml(R.dimen.chocolateSecondCornerY);
        float chocolateThirdCornerY = getXFromXml(R.dimen.chocolateThirdCornerY);
        float chocolateThirdCornerX = getXFromXml(R.dimen.chocolateThirdCornerX);
        float chocolateEndX = getXFromXml(R.dimen.chocolateEndX);
        float chocolateEndY = getXFromXml(R.dimen.chocolateEndY);
        //水管数据
        float waterStartX = getXFromXml(R.dimen.waterStartX);
        float waterStartY = getXFromXml(R.dimen.waterStartY);
        float waterFirstCornerY = getXFromXml(R.dimen.waterFirstCornerY);
        float waterSecondCornerX = getXFromXml(R.dimen.waterSecondCornerX);
        float waterSecondCornerY = getXFromXml(R.dimen.waterSecondCornerY);
        float endwaterX = getXFromXml(R.dimen.endWaterX);
        //设置球体路径
        //咖啡球
        AnimatorPath coffeeBallPath = new AnimatorPath();
        coffeeBallPath.moveTo(coffeeStartX,startY);
        coffeeBallPath.lineTo(coffeeStartX,firstCornerY);
        coffeeBallPath.secondBesselCurveTo(coffeeStartX, secondCornerY, secondCornerX, secondCornerY);
        coffeeBallPath.lineTo(endX,secondCornerY);
        //糖球
        AnimatorPath sugarBallPath = new AnimatorPath();
        sugarBallPath.moveTo(sugarStartX,sugarStartY);
        sugarBallPath.lineTo(sugarStartX,sugarFirstCornerY);
        sugarBallPath.secondBesselCurveTo(sugarControlX, sugarControlY, sugarEndX,sugarEndY);
        //奶球
        AnimatorPath milkBallPath = new AnimatorPath();
        milkBallPath.moveTo(milkStartX,milkStartY);
        milkBallPath.lineTo(milkStartX,milkFirstCornerY);
        milkBallPath.secondBesselCurveTo(milkControlX, milkControlY, milkEndX,milkEndY);
        //巧克力球
        AnimatorPath chocolateBallPath = new AnimatorPath();
        chocolateBallPath.moveTo(chocolateStartX,chocolateStartY);
        chocolateBallPath.lineTo(chocolateStartX,chocolateFirstCornerY);
        chocolateBallPath.secondBesselCurveTo(chocolateStartX, chocolateSecondCornerY, chocolateSecondCornerX, chocolateSecondCornerY);
        chocolateBallPath.lineTo(chocolateThirdCornerX,chocolateThirdCornerY);
        chocolateBallPath.lineTo(chocolateEndX,chocolateEndY);
        //水球
        AnimatorPath waterBallPath = new AnimatorPath();
        waterBallPath.moveTo(waterStartX,waterStartY);
        waterBallPath.lineTo(waterStartX,waterFirstCornerY);
        waterBallPath.secondBesselCurveTo(waterStartX,waterSecondCornerY,waterSecondCornerX,waterSecondCornerY);
        waterBallPath.lineTo(endwaterX,waterSecondCornerY);

        HashMap<Integer,AnimatorPath> ballAnimPath = new HashMap<>();
        ballAnimPath.put(pathView.COFFEE,coffeeBallPath);
        ballAnimPath.put(pathView.SUGAR,sugarBallPath);
        ballAnimPath.put(pathView.MILK,milkBallPath);
        ballAnimPath.put(pathView.CHOCOLATE,chocolateBallPath);
        ballAnimPath.put(pathView.WATER,waterBallPath);
        pathView.initBallAnimationPath(ballAnimPath);
        //设置管道路径
        //咖啡管
        Path coffeePipePath = new Path();
        coffeePipePath.moveTo(coffeeStartX,startY);
        coffeePipePath.lineTo(coffeeStartX,firstCornerY);
        coffeePipePath.quadTo(coffeeStartX,secondCornerY, secondCornerX, secondCornerY);
        coffeePipePath.lineTo(endX,secondCornerY);
        //糖管
        Path sugarPipePath = new Path();
        sugarPipePath.moveTo(sugarStartX,sugarStartY);
        sugarPipePath.lineTo(sugarStartX,sugarFirstCornerY);
        sugarPipePath.quadTo(sugarControlX, sugarControlY, sugarEndX,sugarEndY);
        //奶管
        Path milkPipePath = new Path();
        milkPipePath.moveTo(milkStartX,milkStartY);
        milkPipePath.lineTo(milkStartX,milkFirstCornerY);
        milkPipePath.quadTo(milkControlX, milkControlY, milkEndX,milkEndY);
        //巧克力管
        Path chocolatePipePath = new Path();
        chocolatePipePath.moveTo(chocolateStartX,chocolateStartY);
        chocolatePipePath.lineTo(chocolateStartX,chocolateFirstCornerY);
        chocolatePipePath.quadTo(chocolateStartX, chocolateSecondCornerY, chocolateSecondCornerX, chocolateSecondCornerY);
        chocolatePipePath.lineTo(chocolateThirdCornerX,chocolateThirdCornerY);
        chocolatePipePath.lineTo(chocolateEndX,chocolateEndY);
        //水管
        Path waterPipePath = new Path();
        waterPipePath .moveTo(waterStartX,waterStartY);
        waterPipePath .lineTo(waterStartX,waterFirstCornerY);
        waterPipePath.quadTo(waterStartX,waterSecondCornerY,waterSecondCornerX,waterSecondCornerY);
        waterPipePath .lineTo(endwaterX,waterSecondCornerY);
        HashMap<Integer,Path> pipePath = new HashMap<>();
        pipePath.put(pathView.COFFEE,coffeePipePath);
        pipePath.put(pathView.SUGAR,sugarPipePath);
        pipePath.put(pathView.MILK,milkPipePath);
        pipePath.put(pathView.CHOCOLATE,chocolatePipePath);
        pipePath.put(pathView.WATER,waterPipePath);
        pathView.initPipePath(pipePath);
        //设置球体半径
        HashMap<Integer, Float> ballWidhs  = new HashMap<>();
        ballWidhs.put(pathView.COFFEE,Float.valueOf(DensityUtil.dp2px(this,12)));
        ballWidhs.put(pathView.SUGAR,Float.valueOf(DensityUtil.dp2px(this,10)));
        ballWidhs.put(pathView.MILK,Float.valueOf(DensityUtil.dp2px(this,10)));
        ballWidhs.put(pathView.CHOCOLATE,Float.valueOf(DensityUtil.dp2px(this,10)));
        pathView.setBallWidth(ballWidhs);
        //设置水球半径
        pathView.setWaterRadius(DensityUtil.dp2px(this,6));
        //设置管道宽度
        HashMap<Integer,Float> pipeWith = new HashMap<>();
        pipeWith.put(pathView.COFFEE,Float.valueOf(DensityUtil.dp2px(this,16)));
        pipeWith.put(pathView.SUGAR,Float.valueOf(DensityUtil.dp2px(this,12)));
        pipeWith.put(pathView.MILK,Float.valueOf(DensityUtil.dp2px(this,12)));
        pipeWith.put(pathView.CHOCOLATE,Float.valueOf(DensityUtil.dp2px(this,12)));
        pipeWith.put(pathView.WATER, Float.valueOf(DensityUtil.dp2px(this,5)));
        pathView.setPipeWidth(pipeWith);
    }

    /**
     * 为Activity的布局文件添加OnGlobalLayoutListener事件监听，当回调到onGlobalLayout方法的时候我们通过getMeasureHeight和getMeasuredWidth方法可以获取到组件的宽和高
     */
    private void initOnLayoutListener() {
        final ViewTreeObserver viewTreeObserver = this.getWindow().getDecorView().getViewTreeObserver();
        viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onGlobalLayout() {
                startTitleAnimation();
                startBodyAnimation();
                // 移除GlobalLayoutListener监听
                CompletelyAdjustActivity.this.getWindow().getDecorView().getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });
    }
    /**
     * 进入界面整个装置的动画，从左不可见向右滑动至中心点，然后晃动
     */
    private void startBodyAnimation() {
        fl_body.setVisibility(View.VISIBLE);
        float translationX = fl_body.getTranslationX();
        int width = fl_body.getWidth();
        int screenWidth = ScreenUtil.getScreenWidth(this);
        ObjectAnimator anim = ObjectAnimator.ofFloat(fl_body, "translationX", -width,screenWidth-width,translationX);
        anim.setDuration(500);
        anim.setInterpolator(new AccelerateDecelerateInterpolator());
        anim.start();
    }

    private void startTitleAnimation() {
        iv_title.setVisibility(View.VISIBLE);
        float translationX = iv_title.getTranslationX();
        int width = iv_title.getWidth();
        ObjectAnimator anim = ObjectAnimator.ofFloat(iv_title, "translationX",-width,translationX+DensityUtil.dp2px(this,10), translationX);
        anim.setDuration(300);
       // anim.setInterpolator(new BounceInterpolator());
        anim.start();
        anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                startBackIconAnimation();
                startMusicIconAnimation();
                startBottomButtonAnimation();
                startMaterialButtonAniamtion();
            }
        });
    }

    /**
     * 进入界面，物料点击按钮动画
     */
    private void startMaterialButtonAniamtion() {
        bt_coffee.setVisibility(View.VISIBLE);
        final PropertyValuesHolder pvhY = PropertyValuesHolder.ofFloat("scaleX", 0f,
                1f);
        final PropertyValuesHolder pvhZ = PropertyValuesHolder.ofFloat("scaleY", 0f,
                 1f);
        ObjectAnimator anim = ObjectAnimator.ofPropertyValuesHolder(bt_coffee, pvhY, pvhZ);
        anim.setInterpolator(new BounceInterpolator());
        anim.setDuration(200).start();
        anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                bt_sugar.setVisibility(View.VISIBLE);
                ObjectAnimator anim = ObjectAnimator.ofPropertyValuesHolder(bt_sugar, pvhY, pvhZ);
                anim.setInterpolator(new BounceInterpolator());
                anim.setDuration(200).start();
                anim.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        bt_milk.setVisibility(View.VISIBLE);
                        ObjectAnimator anim = ObjectAnimator.ofPropertyValuesHolder(bt_milk, pvhY, pvhZ);
                        anim.setInterpolator(new BounceInterpolator());
                        anim.setDuration(200).start();
                        anim.addListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                super.onAnimationEnd(animation);
                                bt_chocolate.setVisibility(View.VISIBLE);
                                ObjectAnimator anim = ObjectAnimator.ofPropertyValuesHolder(bt_chocolate, pvhY, pvhZ);
                                anim.setInterpolator(new BounceInterpolator());
                                anim.setDuration(200).start();
                                //弹出上方提示
                                tipHandler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        setTipText("快来调一杯自己的专属咖啡吧！");
                                    }
                                },200);
                            }
                        });
                    }
                });

            }
        });
    }

    /**
     * 进入界面，底部按钮的动画
     */
    private void startBottomButtonAnimation() {
        ll_adjust_bottom_button.setVisibility(View.VISIBLE);
        int screenWidth = ScreenUtil.getScreenWidth(this);
        ObjectAnimator anim = ObjectAnimator.ofFloat(ll_adjust_bottom_button, "translationX", -screenWidth, 0);
        anim.setDuration(500);
        anim.start();
    }

    /**
     * 进入界面，返回按钮动画
     */
    private void startBackIconAnimation() {
        iv_adjust_back.setVisibility(View.VISIBLE);
        float translationX = iv_adjust_back.getTranslationX();
        ObjectAnimator anim = ObjectAnimator.ofFloat(iv_adjust_back, "translationX", -DensityUtil.dp2px(this, 50f), translationX);
        anim.setDuration(500);
        anim.start();
    }

    /**
     * 进入界面，音乐按钮动画
     */
    private void startMusicIconAnimation(){
        bt_music.setVisibility(View.VISIBLE);
        float translationX = bt_music.getTranslationX();
        ObjectAnimator anim = ObjectAnimator.ofFloat(bt_music, "translationX",  -DensityUtil.dp2px(this, 50f), translationX);
        anim.setDuration(500);
        anim.start();

    }

    public static CompletelyAdjustActivity Instance() {
        if (null == mInstance)
            mInstance = new CompletelyAdjustActivity();
        return mInstance;
    }

    /**\
     *以下为待处理代码
     */
    //用于更新配料总和信息
    private void getTotleDosages() {
        //Log.e("totle",appDosages.toString());
        totleWater = 0;
        totlePrice = 0;
        totleVolume = 0;
        totleCoffeeMaterial = 0;
        totleSugarMaterial = 0;
        totleMilkMaterial = 0;
        totleChocolateMaterial = 0;
        totleCoffeeWater=0;
        totleMilkWater=0;
        totleSugarWater=0;
        totleChocolateWater=0;
        for (AppDosages appDosage : appDosages) {
            if(appDosage.getMaterial_name()==null){
                appDosages.remove(appDosage);
                return;
            }
            switch (appDosage.getMaterial_name()) {
                case "咖啡浓度":
                    totleCoffeeWater +=(appDosage.getWater()+appDosage.getSelfWater());
                    totleCoffeeMaterial += appDosage.getWeight();
                    totleWater = (totleWater + appDosage.getWater()+appDosage.getSelfWater());
                    break;
                case "糖":
                    totleSugarWater +=appDosage.getWater();
                    totleSugarMaterial += appDosage.getWeight();
                    totleWater = (totleWater + appDosage.getWater()+appDosage.getWeight());
                    break;
                case "奶粉":
                    totleMilkWater +=appDosage.getWater();
                    totleMilkMaterial += appDosage.getWeight();
                    totleWater = (totleWater + appDosage.getWater()+appDosage.getWeight());
                    break;
                case "巧克力粉":
                    totleChocolateWater+=appDosage.getWater();
                    totleChocolateMaterial += appDosage.getWeight();
                    totleWater = (totleWater + appDosage.getWater()+appDosage.getWeight());
                    break;
            }
        }
        // TODO: 2017/3/3 是否处理小数位
        //显示的容量
        totleVolume = (int)Math.round(totleWater);
        //Log.e("price",mSugarUnitPrice+"=="+totleSugarMaterial+"==="+df.format(totleSugarMaterial*mSugarUnitPrice));
        double sum1 = MyMath.add(BASICPRICE+"", MyMath.mul(MyMath.round(totleChocolateMaterial,1)+"",mChocolateUnitPrice+"")+"");
        double sum2 = MyMath.add(sum1+"", MyMath.mul(MyMath.round(totleCoffeeMaterial,1)+"", (mCoffeeUnitPrice / 10.0f)+"")+"" );
        double sum3 = MyMath.add(sum2+"", MyMath.mul(MyMath.round(totleMilkMaterial,1)+"" ,mMilkUnitPrice+"")+"");
        totlePrice = MyMath.add(sum3+"", MyMath.mul(MyMath.round(totleSugarMaterial,1)+"" , mSugarUnitPrice+"")+"");
        /*totlePrice =BASICPRICE+(getOneDec(totleChocolateMaterial))*mChocolateUnitPrice+(getOneDec(totleCoffeeMaterial))*mCoffeeUnitPrice/10.0f
                +getOneDec(totleMilkMaterial)*mMilkUnitPrice+getOneDec(totleSugarMaterial)*mSugarUnitPrice;*/
        LogUtil.e("actualWater",totleWater+"");
        LogUtil.e("actualAppdosages",appDosages+"");
        LogUtil.e("totlePrice",totlePrice+"");
        if(totleVolume>=MAXVOLUE){
            setColume(MAXVOLUE,totlePrice);
        }else {
            if(!checkIsOutOfBoundForColumn()){
                setColume((int) totleVolume, totlePrice);
            }

        }
    }
    //加入物料
    private void setCurrentMaterila(int sec) {
        if(appDosages==null||appDosages.size()==0){
            return;
        }
        AppDosages appDosage = appDosages.get(appDosages.size() - 1);
        appDosage.setId(type);
        switch (type) {
            case SUGAR:
                LogUtil.e("seconds",sec+"");
                appDosage.setMaterial_name("糖");
                appDosage.setSequence(appDosages.size());
                appDosage.setWeight(isContinue?continueAppDosage.getWeight()+sec*2:(float) sec*2);
                appDosage.setWater(isContinue?continueAppDosage.getWater()+(float) (sec*2*mSugarWater_rate):(float) (sec*2*mSugarWater_rate));
                break;
            case COFFEE:
                appDosage.setMaterial_name("咖啡浓度");
                appDosage.setSequence(appDosages.size());
                appDosage.setWeight(isContinue?continueAppDosage.getWeight()+(float) sec*20:(float) sec*20);
                appDosage.setSelfWater(isContinue?continueAppDosage.getSelfWater()+(float) (sec*20*mCoffeeWater_rate):(float) (sec*mCoffeeWater_rate*20));
                // //Log.e("coffee","coffeeWater===="+appDosage.getWater()+"");
                break;
            case MILK:
                appDosage.setMaterial_name("奶粉");
                appDosage.setSequence(appDosages.size());
                appDosage.setWeight(isContinue?continueAppDosage.getWeight()+(float)sec*3:(float)sec*3);
                appDosage.setWater(isContinue?continueAppDosage.getWater()+(float) (sec*3*mMilkWater_rate):(float) (sec*3*mMilkWater_rate));
                break;
            case CHOCOLATE:
                appDosage.setMaterial_name("巧克力粉");
                appDosage.setSequence(appDosages.size());
                appDosage.setWeight(isContinue?continueAppDosage.getWeight()+(float)sec*2:(float)sec*2);
                appDosage.setWater(isContinue?continueAppDosage.getWater()+(float) (sec*2*mChocolateWater_rate):(float) (sec*2*mChocolateWater_rate));
                break;
        }
        getTotleDosages();
        // //Log.e("appdos",appDosages.toString());
        //判断下一秒加入是否会超过边界fond
        // checkWillOutOfBound(appDosage);
    }

    //一位小数四舍五入
    private double getOneDec(double num){
        int i = (int) Math.round(num * 10f);
        double v = i / 10.0f;
        return v;
    }
    private double getTwoDec(double num){
        double round = Math.round(num * 100f);
        return (round/100.0f);

    }
    private void initData() {
        //获取原料信息
        getAppMaterial();
        //上传的加料顺序
        appDosages = new ArrayList<>();
        //传给杯子的加料前数据
         restoreAppdosages=new ArrayList<>();

    }

    private void setColume(int volume, double totlePrice) {
        LogUtil.e("totlePrice",totlePrice+"");
        LogUtil.e("totlePrice+math",MyMath.round(7.145,2)+"");
        tvTotlePrice.setText(df.format(MyMath.round(totlePrice,2))+"");
        tvTotleVolume.setText(volume>0&&volume<0.01?0.01+"":String.valueOf(volume));
    }
    //初始化物料下移动画
    private void startMaterialAnimation(final ImageView iv) {
        isAnimation = true;
        int height = iv.getHeight();
        float translationY = iv.getTranslationY();
        trsAnimation = ObjectAnimator.ofFloat(iv, "translationY", translationY, translationY + height);
        trsAnimation.setDuration(30000);
        trsAnimation.setRepeatCount(100);
        MyAnimatorUpdateListener updateListener = new MyAnimatorUpdateListener(iv);
        //如果已经暂停，是继续播放
        if (updateListener.isPause) updateListener.play();
            //否则就是从头开始播放
        else trsAnimation.start();
        trsAnimation.addUpdateListener(updateListener);

    }

    private void cancelMaterialAnimation(ImageView iv) {
        isAnimation = false;
        trsAnimation.cancel();


    }
    //初始化按钮触摸事件
    private void initButton() {
        bt_chocolate.setOnTouchListener(this);
        bt_coffee.setOnTouchListener(this);
        bt_milk.setOnTouchListener(this);
        bt_sugar.setOnTouchListener(this);
    }
    //底部按钮点击事件
    @Event(value = {R.id.tv_clear, R.id.tv_fine, R.id.tv_generate})
    private void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_clear:
                if(appDosages==null||appDosages.size()==0){

                }else{
                    restoreAppdosages.clear();
                    appDosages.clear();
                }
                //处理加料时清空无效
                cupView.clearData();
                //trsAnimation.setCurrentPlayTime(0);
                resetMaterialLocation();
                getTotleDosages();
                break;
            case R.id.tv_fine:
                if(appDosages.size()==0||totleWater==0){
                    setTipText("至少添加一种原料！");
                    return;
                }
                // restoreRecLen=recLen;
                Intent intent = new Intent(this, FineActivity.class);
                reFormAppDosages(totleVolume);
                intent.putExtra("appDosages", (Serializable) appDosages);
                intent.putParcelableArrayListExtra("materials",materials);
                intent.putExtra("reclen",restoreRecLen);
                startActivityForResult(intent, 1);
                 isToFine =true;
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        musicRoAnimation.end();
                    }
                },300);
                this.overridePendingTransition(R.anim.slide_in_right_base,
                        R.anim.slide_out_left_base);
                break;
            case R.id.tv_generate:
                getTotleDosages();
                if(appDosages.size()==0){
                   // sv.fullScroll(ScrollView.FOCUS_DOWN);
                    setTipText("你还未添加任何原料哦！");
                    return;
                }
                if (totleVolume < 40) {
                    //sv.fullScroll(ScrollView.FOCUS_DOWN);
                    setTipText("饮品容量最少也要40ml哦！");
                    return;
                }
                if (NetUtil.isNetworkAvailable(this)) {

                    if (SharedPrefUtil.getLoginType().equals(SharedPrefUtil.LOGINING)) {
                        if(Math.ceil(totleWater)<MAXVOLUE){
                            TitleButtonsDialog descCenterDialog = TitleButtonsDialog.newInstance("饮品还未满杯，确定不再加点什么吗？", "继续添加", "不加了");
                            descCenterDialog.setOnTwoClickListener(new ITwoButtonClick() {
                                @Override
                                public void onLeftButtonClick() {
                                }

                                @Override
                                public void onRightButtonClick() {
                                    NewNameCoffeeDialog collectCoffeeDialog = new NewNameCoffeeDialog();
                                    collectCoffeeDialog.setListener(new NewNameCoffeeDialog.NewNameCoffeeDialogable() {
                                        @Override
                                        public void onCollectDialog(String string) {
                                            generateCoffee(string);
                                        }
                                    });

                                    collectCoffeeDialog.show(getSupportFragmentManager(), "collectCoffee");
                                }

                            });
                            descCenterDialog.show(CompletelyAdjustActivity.this.getSupportFragmentManager(), "asktag");

                        }else{
                            NewNameCoffeeDialog collectCoffeeDialog = new NewNameCoffeeDialog();
                            collectCoffeeDialog.setListener(new NewNameCoffeeDialog.NewNameCoffeeDialogable() {
                                @Override
                                public void onCollectDialog(String string) {
                                    generateCoffee(string);
                                }
                            });

                            collectCoffeeDialog.show(getSupportFragmentManager(), "collectCoffee");
                        }



                    } else {
                        goLogin();
                    }

                }
                break;
            default:
                break;
        }
    }
//重新设置物料位置
    private void resetMaterialLocation() {
        iv_material_coffee.setY(getXFromXml(R.dimen.material_top));
        iv_material_sugar.setY(getXFromXml(R.dimen.material_top));
        iv_material_milk.setY(getXFromXml(R.dimen.material_top));
        iv_material_chocolate.setY(getXFromXml(R.dimen.material_top));
    }

    private void generateCoffee(String string) {
        String auth_token = UserUtils.getUserInfo().getAuth_token();

        /**
         * parameter format
         * {"parent_id": 1, "name": "my卡布奇诺", "price": 17, "description": "my卡布奇诺", "volume": 260}
         */
        JSONObject app_item = new JSONObject();
        try {
            app_item.put("parent_id", null);
            app_item.put("name", string);
            app_item.put("price", MyMath.round(totlePrice,2));
            app_item.put("description", null);
            app_item.put("volume", totleVolume);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JSONArray app_dosages = new JSONArray();
        //checkIsOutOf8Ml(appDosages);
        int id = -1;
        for (int i = 0; i < appDosages.size(); i++) {
            switch (appDosages.get(i).getMaterial_name()){
                case "糖":
                    id= sugarMaterial.getId();
                    break;
                case "咖啡浓度":
                    id=coffeeMaterial.getId();
                    break;
                case "奶粉":
                    id=milkMaterial.getId();
                    break;
                case "巧克力粉":
                    id=chocolateMaterial.getId();
                    break;
            }
            JSONObject dosage = new JSONObject();
            try {
                dosage.put("id",id);
                dosage.put("weight",(appDosages.get(i).getWeight()) );
//                dosage.put("weight", getWeightFromText(tabModulationDosage.getTabAt(i).getText().toString().trim()));
                switch (appDosages.get(i).getMaterial_name()){
                    case "糖":
                        dosage.put("water", (appDosages.get(i).getWater()));
                        break;
                    case "咖啡浓度":
                        dosage.put("water", (appDosages.get(i).getWater()+appDosages.get(i).getSelfWater()));
                        break;
                    case "奶粉":
                        dosage.put("water",(appDosages.get(i).getWater()));
                        break;
                    case "巧克力粉":
                        dosage.put("water", (appDosages.get(i).getWater()));
                        break;
                }
                dosage.put("sequence", i+1);
                app_dosages.put(dosage);
                LogUtil.e("collectDosage",dosage.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }


        if (!TextUtils.isEmpty(auth_token)) {
            RequestParams entity = new RequestParams(Configurations.URL_APP_ITEMS);
            entity.addParameter(Configurations.AUTH_TOKEN, auth_token);
            entity.addParameter("app_item", app_item.toString());
            entity.addParameter("app_dosages", app_dosages.toString());
            entity.addParameter("app_item_type", 1);

            String device_id = JPushInterface.getRegistrationID(this);
            String timeStamp = TimeUtil.getCurrentTimeString();
            entity.addParameter(Configurations.TIMESTAMP, timeStamp);
            entity.addParameter(Configurations.DEVICE_ID, device_id);
            entity.addParameter(Configurations.APP_ID, AppUtil.getVersionCode(CompletelyAdjustActivity.this));
            Map<String, String> map = new TreeMap<>();
            map.put(Configurations.AUTH_TOKEN, auth_token);
            map.put("app_item", app_item.toString());
            map.put("app_dosages", app_dosages.toString());
            map.put("app_item_type", String.valueOf(1));
            map.put(Configurations.APP_ID, String.valueOf(AppUtil.getVersionCode(CompletelyAdjustActivity.this)));
            entity.addParameter(Configurations.SIGN, SignUtils.createSignString(device_id,timeStamp,map));
            org.xutils.x.http().post(entity, new Callback.CommonCallback<JSONObject>() {
                @Override
                public void onSuccess(JSONObject result) {
                    checkResOld(result);
                    try {
                        if(result.getInt(Configurations.STATUSCODE)!=200){
                            ToastUtil.showShort(CompletelyAdjustActivity.this, result.getString(Configurations.STATUSMSG));
                        }else {
                            TitleButtonsDialog descCenterDialog = TitleButtonsDialog.newInstance("生成配方成功", "继续调制", "查看配方");
                            descCenterDialog.setOnTwoClickListener(new ITwoButtonClick() {
                                @Override
                                public void onLeftButtonClick() {
                                }

                                @Override
                                public void onRightButtonClick() {
                                    Intent intent = new Intent(CompletelyAdjustActivity.this, NewMyCoffeesActivity.class);
                                    startActivity(intent);
                                }

                            });
                            descCenterDialog.show(CompletelyAdjustActivity.this.getSupportFragmentManager(), "asktag");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onError(Throwable ex, boolean isOnCallback) {
                    showNetError();
                }

                @Override
                public void onCancelled(CancelledException cex) {
                }

                @Override
                public void onFinished() {
                }
            });
        }
    }
    //原料加料按钮点击事件
    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        int id = view.getId();
        int action = motionEvent.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                if(isPressed){
                    break;
                }
                //处理按下事件
                if(materials==null){
                    break;
                }
                resorvleDownEvent(id);
                isPressed=true;
                break;
            case MotionEvent.ACTION_UP:
                isPressed=false;
                if(materials==null){
                    break;
                }
                //结束物料下落动画
                endMaterialAnim(type);
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if(!notNeedReDescend){
                            resolveUpEnvent(type);
                        }
                        handler.removeCallbacks(runnable);
                    }
                },50);

                break;
            case MotionEvent.ACTION_CANCEL:
                isPressed=false;
                if(materials==null){
                    break;
                }
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        resolveUpEnvent(type);
                        handler.removeCallbacks(runnable);
                    }
                },50);
                break;
        }
        return false;
    }

    private void endMaterialAnim(int type) {
        switch (type) {
            case COFFEE:
                  /*  AnimationDrawable anim = (AnimationDrawable) ivGear1.getDrawable();
                    if (anim.isRunning()) {
                        anim.stop();
                    }
                    ivGear1.setVisibility(View.GONE);*/
                cancelMaterialAnimation(iv_material_coffee);

                break;
            case SUGAR:
                cancelMaterialAnimation(iv_material_sugar);
                //Log.e("finish",isFinishing()+"");


                break;
            case MILK:
                cancelMaterialAnimation(iv_material_milk);


                break;
            case CHOCOLATE:
                cancelMaterialAnimation(iv_material_chocolate);

                break;
        }
    }

    /**
     * 加入不同物料存储之前的数据
     * 如果是继续并且连续点击，则保留第一次点击之前的数据
     */
    private void restoreBeforeData(boolean isContinue) {
        if(isContinue){
            if(!hasRestore){
                restoreAppdosages.clear();
                for (int i=0;i<appDosages.size();i++) {
                    restoreAppdosages.add(new AppDosages(appDosages.get(i).getId(),appDosages.get(i).getWeight(),
                            appDosages.get(i).getSelfWater(),appDosages.get(i).getWater(),appDosages.get(i).getSequence(),appDosages.get(i).getMaterial_name()));
                }
            }
            hasRestore=true;

        }else{
            restoreAppdosages.clear();
            for (AppDosages appDosage : appDosages) {
                restoreAppdosages.add(new AppDosages(appDosage.getId(),appDosage.getWeight(),
                        appDosage.getSelfWater(),appDosage.getWater(),appDosage.getSequence(),appDosage.getMaterial_name()));
            }
        }
    }


    private void resolveUpEnvent(int type) {
        if(checkIsOutOfBound()){
            handlerOutMaterial();
            getTotleDosages();
        }else if(!checkIsOutOfBound()&&!isUnTouch){

        }
        mBallHandler.removeCallbacksAndMessages(null);
        pathView.endPipeAnimation();
        needReSetData=true;
        restoreRecLen=recLen;
        recLen = 0;


        //物料上升动画
        LogUtil.e("cupViewApp",restoreAppdosages.toString()+"========"+appDosages.toString());
        isUnTouch=false;
        isContinue =false;
    }
   //关闭齿轮动画
    private void closeGearAnimation() {
        gear.setVisibility(View.VISIBLE);
        gear .setImageResource(R.drawable.gear_animlist);
        AnimationDrawable anim = (AnimationDrawable) gear.getDrawable();
        anim.start();
        final PropertyValuesHolder pvhY = PropertyValuesHolder.ofFloat("scaleX", 1f,
                0f);
        final PropertyValuesHolder pvhZ = PropertyValuesHolder.ofFloat("scaleY", 1f,
                0f);
        ObjectAnimator gearAnim = ObjectAnimator.ofPropertyValuesHolder(gear, pvhY, pvhZ);
        gearAnim.setDuration(500).start();
        gearIsRunning=false;
    }

    private void resorvleDownEvent(int id) {
        if(appDosages!=null){
            getTotleDosages();
        }
        switch (id) {
            case R.id.button_coffee:
                // //Log.e("totleCoffee",totleCoffeeMaterial+"");
                if (!(Math.ceil(totleWater)<MAXVOLUE)) {
                    handlerOutMaterial();
                    //setTipText("杯子里已经放不下啦！");
                    break;
                } else if(!(totleCoffeeMaterial < coffeeMaterial.getMax_weight())) {
                    setTipText("咖啡浓度已达上限！");
                    break;
                } else if(appDosages.size()==4&&!(oldType ==COFFEE)){
                    setTipText("制作饮品最多只能添加4次原料哦!");
                    isUnTouch=true;
                    break;
                } else if(appDosages.size()!=0&&appDosages.get(appDosages.size()-1).getMaterial_name()!=null&&(appDosages.get(appDosages.size()-1).getMaterial_name().equals("咖啡浓度"))){
                    type = COFFEE;
                    //存储按下之前的物料数据
                    if(isRunning){
                        restoreBeforeData(true);
                    }else {
                        restoreBeforeData(false);
                    }

                    pressButton(id);
                    continueAddMaterial();
                    startMaterialAnimation(iv_material_coffee);
                    startGearAnimation();
                    //ivGear1.setVisibility(View.VISIBLE);
                   // ivGear1.setImageResource(R.drawable.gear_animlist);
                   // AnimationDrawable anim = (AnimationDrawable) ivGear1.getDrawable();
                   // anim.start();
                    oldType =type;
                    break;
                } else {
                    if((MAXVOLUE-totleWater)<coffeeMaterial.getWater_rate()*10){
                        isUnTouch=true;
                        setTipText("剩余容量太小了，无法再添加新物料!");
                        return;
                    }else{
                        if(isRunning){
                            //正在搅拌时加入不同物料不被允许
                            return;
                        }
                        restoreBeforeData(false);
                        recLen=0;
                        type = COFFEE;
                        pressButton(id);
                        appDosages.add(new AppDosages());
                        handler.post(runnable);
                        startMaterialAnimation(iv_material_coffee);
                        startGearAnimation();
                        oldType =type;
                        return;
                    }

                }

            case R.id.button_sugar:
                if(gearIsRunning){
                    return;
                }
                if (!(Math.ceil(totleWater)<MAXVOLUE)) {
                    handlerOutMaterial();
                    setTipText("杯子里已经放不下啦！");
                    break;
                }
                else if(!(totleSugarMaterial < sugarMaterial.getMax_weight())) {

                    setTipText("该物料已达上限！");
                    break;
                } else if(appDosages.size()==4&&!(oldType ==SUGAR)){
                    setTipText("制作饮品最多只能添加4次原料哦!");
                    isUnTouch=true;
                    return;
                }   else if(appDosages.size()!=0&&appDosages.get(appDosages.size()-1).getMaterial_name()!=null&&(appDosages.get(appDosages.size()-1).getMaterial_name().equals("糖"))){
                    type = SUGAR;
                    if(isRunning){
                        restoreBeforeData(true);
                    }else{
                        restoreBeforeData(false);
                    }

                    pressButton(id);
                    continueAddMaterial();
                    startMaterialAnimation(iv_material_sugar);
                    oldType =type;
                    break;
                }else {
                    if((MAXVOLUE-totleWater)<(8+8f/sugarMaterial.getWater_rate())){
                        isUnTouch=true;
                        setTipText("剩余容量太小了，无法再添加新物料!");

                        return;
                    }else{
                        if(isRunning){
                            return;
                        }
                        restoreBeforeData(false);
                        recLen=0;
                        pressButton(id);
                        type = SUGAR;
                        appDosages.add(new AppDosages());
                        handler.post(runnable);
                        startMaterialAnimation(iv_material_sugar);
                        oldType =type;
                        return;
                    }

                }

            case R.id.button_milk:
                if(gearIsRunning){
                    return;
                }
                if (!(Math.ceil(totleWater)<MAXVOLUE)) {
                    handlerOutMaterial();
                    setTipText("杯子里已经放不下啦！");
                    break;
                } else if(!(totleMilkMaterial < milkMaterial.getMax_weight())) {
                    setTipText("该物料已达上限！");
                    break;
                }else if(appDosages.size()==4&&!(oldType ==MILK)){
                    setTipText("制作饮品最多只能添加4次原料哦!");
                    isUnTouch=true;
                    break;
                }
                else if(appDosages.size()!=0&&appDosages.get(appDosages.size()-1).getMaterial_name()!=null&&(appDosages.get(appDosages.size()-1).getMaterial_name().equals("奶粉"))){
                    type = MILK;
                    if(isRunning){
                        restoreBeforeData(true);
                    }else{
                        restoreBeforeData(false);
                    }

                    pressButton(id);
                    continueAddMaterial();
                    oldType =type;
                    startMaterialAnimation(iv_material_milk);
                    break;
                } else {
                    if((MAXVOLUE-totleWater)<(8+8f/milkMaterial.getWater_rate())){
                        setTipText("剩余容量太小了，无法再添加新物料!");
                        isUnTouch=true;
                        return;
                    }else{
                        if(isRunning){
                            return;
                        }
                        restoreBeforeData(false);
                        recLen=0;
                        type = MILK;
                        pressButton(id);
                        appDosages.add(new AppDosages());
                        handler.post(runnable);
                        startMaterialAnimation(iv_material_milk);
                        oldType =type;
                        return;
                    }

                }

            case R.id.button_chocolate:
                if(gearIsRunning){
                    return;
                }
                if (!(Math.ceil(totleWater)<MAXVOLUE)) {
                    handlerOutMaterial();
                    setTipText("杯子里已经放不下啦！");
                    break;
                } else if(!(totleChocolateMaterial < chocolateMaterial.getMax_weight())) {
                    setTipText("该物料已达上限！");
                    break;
                }else if(appDosages.size()==4&&!(oldType ==CHOCOLATE)){
                    setTipText("制作饮品最多只能添加4次原料哦!");
                    isUnTouch=true;
                    break;
                }
                else if(appDosages.size()!=0&&appDosages.get(appDosages.size()-1).getMaterial_name()!=null&&(appDosages.get(appDosages.size()-1).getMaterial_name().equals("巧克力粉"))){
                    type = CHOCOLATE;
                    if(isRunning){
                        restoreBeforeData(true);
                    }else{
                        restoreBeforeData(false);
                    }

                    pressButton(id);
                    continueAddMaterial();
                    startMaterialAnimation(iv_material_chocolate);
                    oldType =type;
                    break;
                }else {
                    if((MAXVOLUE-totleWater)<(8+8f/chocolateMaterial.getWater_rate())){
                        isUnTouch=true;
                        setTipText("剩余容量太小了，无法再添加新物料!");
                        return;
                    }else{
                        if(isRunning){
                            return;
                        }
                        restoreBeforeData(false);
                        recLen=0;
                        type = CHOCOLATE;
                        pressButton(id);
                        appDosages.add(new AppDosages());
                        handler.post(runnable);
                        startMaterialAnimation(iv_material_chocolate);
                        oldType =type;
                        return;
                    }

                }

        }

    }
/**
 *  开启齿轮动画
 */

    private void startGearAnimation() {
        if(gearIsRunning){
              return;
          }
          gearIsRunning =true;
          gear.setVisibility(View.VISIBLE);
          gear .setImageResource(R.drawable.gear_animlist);
          AnimationDrawable anim = (AnimationDrawable) gear.getDrawable();
         anim.start();
         final PropertyValuesHolder pvhY = PropertyValuesHolder.ofFloat("scaleX", 0f,
                1f);
         final PropertyValuesHolder pvhZ = PropertyValuesHolder.ofFloat("scaleY", 0f,
                1f);
         ObjectAnimator gearAnim = ObjectAnimator.ofPropertyValuesHolder(gear, pvhY, pvhZ);
         gearAnim.setInterpolator(new BounceInterpolator());
         gearAnim.setDuration(500).start();
    }

    private void reFormAppDosages(float totleWater) {
        float decTotleWater = 0;
        for (AppDosages appDosage : appDosages) {
            appDosage.setWeight((float) getOneDec(appDosage.getWeight()));
            appDosage.setSelfWater((float) getOneDec(appDosage.getSelfWater()));
            appDosage.setWater((float) getOneDec(appDosage.getWater()));
            if(appDosage.getMaterial_name().equals("咖啡浓度")){
                decTotleWater+=(appDosage.getWater()+appDosage.getSelfWater());
            }else{
                decTotleWater+=(appDosage.getWeight()+appDosage.getWater());
            }
        }
        float extra = totleWater - decTotleWater;
        //如果咖啡的额外水量加上偏差不小于0
        for (AppDosages appDosage : appDosages) {
            if(appDosage.getMaterial_name().equals("咖啡浓度")){
                if(!((appDosage.getWater()+extra)<0)){
                    appDosage.setWater(appDosage.getWater()+extra);
                    return;
                }else{}
            }else{}
        }
        //如果没有咖啡，则在剩下的物料中取加上偏差后影响最小的
        float maxRatio=0;
        int maxIndex=0;
        for (int i=0;i<appDosages.size();i++) {
            if(!appDosages.get(i).getMaterial_name().equals("咖啡浓度")){
                if(((appDosages.get(i).getWater()+extra)/appDosages.get(i).getWeight())>maxRatio){
                    maxRatio=(appDosages.get(i).getWater()+extra)/appDosages.get(i).getWeight();
                    maxIndex=i;
                }
            }
        }
        appDosages.get(maxIndex).setWater(appDosages.get(maxIndex).getWater()+extra);
        getTotleDosages();
    }

    /**
     *  如果两次加入相同的物料，继续加入
     */
    private void continueAddMaterial() {
        isContinue =true;
        ArrayList<AppDosages> restorAppDosages = new ArrayList<AppDosages>() ;
        for (AppDosages appDosage : appDosages) {
            AppDosages aps = new AppDosages(appDosage.getId(), appDosage.getWeight(),appDosage.getSelfWater(), appDosage.getWater(), appDosage.getSequence(), appDosage.getMaterial_name());
            restorAppDosages.add(aps);
        }
        LogUtil.e("restorAppDosages",restorAppDosages.toString());
        continueAppDosage = restorAppDosages.get(restorAppDosages.size() - 1);
        handler.post(runnable);
    }
    class MyAnimatorUpdateListener implements ValueAnimator.AnimatorUpdateListener {
        private final ImageView iv;
        /**
         * 暂停状态
         */
        private boolean isPause = false;
        /**
         * 是否已经暂停，如果一已经暂停，那么就不需要再次设置停止的一些事件和监听器了
         */
        private boolean isPaused = false;
        /**
         * 当前的动画的播放位置
         */
        private float fraction = 0.0f;
        /**
         * 当前动画的播放运行时间
         */
        private long mCurrentPlayTime = 0l;
        private long currentPlayTime;

        public MyAnimatorUpdateListener(ImageView iv) {
            this.iv = iv;
        }

        /**
         * 是否是暂停状态
         *
         * @return
         */
        public boolean isPause() {
            return isPause;
        }

        /**
         * 停止方法，只是设置标志位，剩余的工作会根据状态位置在onAnimationUpdate进行操作
         */
        public void pause() {
            isPause = true;
        }

        public void play() {
            isPause = false;
            isPaused = false;
        }

        @Override
        public void onAnimationUpdate(ValueAnimator animation) {

            currentPlayTime = animation.getCurrentPlayTime();
            /**
             * 如果是暂停则将状态保持下来，并每个刷新动画的时间了；来设置当前时间，让动画
             * 在时间上处于暂停状态，同时要设置一个静止的时间加速器，来保证动画不会抖动
             */
            if (isPause) {
                if (!isPaused) {
                    mCurrentPlayTime = animation.getCurrentPlayTime();
                    fraction = animation.getAnimatedFraction();
                    animation.setInterpolator(new TimeInterpolator() {
                        @Override
                        public float getInterpolation(float input) {
                            return fraction;

                        }
                    });
                    isPaused = true;
                }
                //每隔动画播放的时间，我们都会将播放时间往回调整，以便重新播放的时候接着使用这个时间,同时也为了让整个动画不结束
                new CountDownTimer(ValueAnimator.getFrameDelay(), ValueAnimator.getFrameDelay()) {

                    @Override
                    public void onTick(long millisUntilFinished) {
                    }

                    @Override
                    public void onFinish() {
                        trsAnimation.setCurrentPlayTime(mCurrentPlayTime);
                    }
                }.start();
            } else {
                //将时间拦截器恢复成线性的，如果您有自己的，也可以在这里进行恢复
                animation.setInterpolator(null);
            }
        }
    }

    private void getAppMaterial() {

        RequestParams entity = new RequestParams(Configurations.URL_APP_MATERIALS);
        entity.addParameter(Configurations.APP_ID, AppUtil.getVersionCode(CompletelyAdjustActivity.this));
        String device_id= JPushInterface.getRegistrationID(CompletelyAdjustActivity.this);
        String timeStamp= TimeUtil.getCurrentTimeString();
        entity.addParameter(Configurations.TIMESTAMP, timeStamp);
        entity.addParameter(Configurations.DEVICE_ID,device_id );
        Map<String,String> map=new TreeMap<>();
        map.put(Configurations.APP_ID, String.valueOf(AppUtil.getVersionCode(CompletelyAdjustActivity.this)));
        entity.addParameter(Configurations.SIGN, SignUtils.createSignString(device_id,timeStamp,map));
        org.xutils.x.http().get(entity, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                hideLoading();

                parseResult(result);

            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                showNetError();
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });
    }

    private void parseResult(String result) {
        try {
            JSONObject results = new JSONObject(result);
            checkResOld(results);
            if (results.getInt(Configurations.STATUSCODE) == 200) {
                materials = new Gson().fromJson(results.getJSONObject("results").getString("app_materials"),
                        new TypeToken<ArrayList<Material>>() {
                        }.getType());
                //Log.e("materials",materials.toString());
                cupView.setMaterialData(materials);
                for (Material material : materials) {
                    switch (material.getName()) {
                        case "咖啡豆":
                            coffeeMaterial = material;
                            mCoffeeWater_rate = coffeeMaterial.getWater_rate();
                            mCoffeeUnitPrice =  Double.valueOf(coffeeMaterial.getAdjust_price());
                            mCoffee_max_weight = coffeeMaterial.getMax_weight();
                            break;
                        case "糖":
                            sugarMaterial = material;
                            mSugarWater_rate = sugarMaterial.getWater_rate();
                            mSugarUnitPrice = Double.valueOf(sugarMaterial.getAdjust_price());
                            mSugar_max_weight = sugarMaterial.getMax_weight();
                            break;
                        case "奶粉":
                            milkMaterial = material;
                            mMilkWater_rate = milkMaterial.getWater_rate();
                            mMilkUnitPrice = Double.valueOf(milkMaterial.getAdjust_price());
                            mMilk_max_weight = milkMaterial.getMax_weight();
                            break;
                        case "巧克力粉":
                            chocolateMaterial = material;
                            mChocolateWater_rate = chocolateMaterial.getWater_rate();
                            mChocolateUnitPrice = Double.valueOf(chocolateMaterial.getAdjust_price());
                            mChocolate_max_weight = chocolateMaterial.getMax_weight();
                            break;
                    }
                }

            } else {
                ToastUtil.showShort(this, results.getString(Configurations.STATUSMSG));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1&&resultCode==2&&data!=null){
            ArrayList<AppDosages> intentAppDosages = (ArrayList<AppDosages>) data.getSerializableExtra("appDosages");
            restoreRecLen=data.getIntExtra("reclen",0);
            appDosages.clear();
            appDosages.addAll(intentAppDosages);
            //处理相邻重复的物料，合并到一起
            combineNextToMaterial();
            getTotleDosages();
            Log.e("new AppDosage",appDosages.toString());
            LogUtil.e("restoreSec",restoreRecLen+"");
            //recLen=restoreRecLen;
            Collections.reverse(appDosages);
            cupView.setData(appDosages,this);
            Collections.reverse(appDosages);
        }
    }

    private void combineNextToMaterial() {
        String tag=null;
        List<Integer> removeIndex=new ArrayList<>();
        for(int i=0;i<appDosages.size();i++){
            if(tag!=null&&tag.equals(appDosages.get(i).getMaterial_name())){
                AppDosages appDosage = this.appDosages.get(i);
                AppDosages lastAppDosage = this.appDosages.get(i - 1);
                lastAppDosage.setWeight(lastAppDosage.getWeight()+appDosage.getWeight());
                lastAppDosage.setWater(lastAppDosage.getWater()+appDosage.getWater());
                lastAppDosage.setSelfWater(lastAppDosage.getSelfWater()+appDosage.getSelfWater());
                removeIndex.add(i);

            }
            tag=appDosages.get(i).getMaterial_name();
        }
        for (int i=0;i<removeIndex.size();i++) {
            appDosages.remove(removeIndex.get(i)-i);
        }
    }

    private int restoreRecLen;
    private int recLen = 0;
    Handler handler = new Handler();
    private boolean needReSetData=true;
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            LogUtil.e("reclen",recLen+"");
            if(isAnimation){

            }
            LogUtil.e("isOut",checkIsOutOfBound()+"");
            if(!checkIsOutOfBound()){
                notNeedReDescend=false;
                setCurrentMaterila(++recLen);
                handler.postDelayed(this, 300);
            }else{
                handler.removeCallbacks(runnable);
                handlerOutMaterial();
                getTotleDosages();
                //reFormAppDosages(totleVolume);
                notNeedReDescend=true;
                resolveUpEnvent(type);
                needReSetData=false;
            }

        }
    };
    //保留一位小数，直接舍掉
    private float restoreOnDec(float var){
        float v = (int) var * 10f;
        float v1 = v / 10f;
        return v1;
    }
    //咖啡必须是10的倍数
    private int restoreTenDec(float var){
        float v = (var / 10f) * 10f;
        return (int) v;
    }
    private void handlerOutMaterial() {
        float desTotleWater=0;
        float desTotleSugar=0;
        float desTotleCoffee=0;
        float desTotleMilk=0;
        float desTotleChocolate=0;
        for (int i=0;i<appDosages.size()-1;i++){
            switch (appDosages.get(i).getMaterial_name()){
                case "糖":
                    desTotleSugar+=appDosages.get(i).getWeight();
                    desTotleWater=(desTotleWater+appDosages.get(i).getWater()+appDosages.get(i).getWeight());
                    break;
                case "咖啡浓度":
                    desTotleCoffee+=appDosages.get(i).getWeight();
                    desTotleWater=(desTotleWater+appDosages.get(i).getWater()+appDosages.get(i).getSelfWater());
                    break;
                case "奶粉":
                    desTotleMilk+=appDosages.get(i).getWeight();
                    desTotleWater+=(appDosages.get(i).getWater()+appDosages.get(i).getWeight());
                    break;
                case "巧克力粉":
                    desTotleChocolate+=appDosages.get(i).getWeight();
                    desTotleWater+=(appDosages.get(i).getWater()+appDosages.get(i).getWeight());
                    break;
            }
        }
        float weight=0f ;
        float water=0f;
        float selfWater=0f;
        AppDosages lastAppDosage = this.appDosages.get(this.appDosages.size() - 1);
        switch (this.appDosages.get(this.appDosages.size()-1).getMaterial_name()){
            case "糖":
                double extraWater = totleWater - desTotleWater - lastAppDosage.getWeight() - lastAppDosage.getWeight() * sugarMaterial.getWater_rate();
                if(!(Math.ceil(totleWater)<MAXVOLUE)){
                    weight= (float) ( (MAXVOLUE - desTotleWater-extraWater) /(1+sugarMaterial.getWater_rate()));
                    //weight= (float) ( (MAXVOLUE - desTotleWater) /(1+sugarMaterial.getWater_rate()));
                    water= MAXVOLUE - desTotleWater - weight;
                }
                if(!(totleSugarMaterial<sugarMaterial.getMax_weight())){
                    weight=(float)mSugar_max_weight-desTotleSugar;
                    //water= (float) (weight*sugarMaterial.getWater_rate());
                    water= (float) ((weight*sugarMaterial.getWater_rate())+extraWater);
                }
                break;
            case "咖啡浓度":
                if(!(Math.ceil(totleWater)<MAXVOLUE)){
                    weight= (float) ((float) (MAXVOLUE - desTotleWater- this.appDosages.get(this.appDosages.size()-1).getWater()) / coffeeMaterial.getWater_rate());
                    selfWater= (float) (MAXVOLUE-desTotleWater- this.appDosages.get(this.appDosages.size()-1).getWater());
                    water= this.appDosages.get(this.appDosages.size()-1).getWater();
                    // weight=restoreTenDec((float) ((float) (MAXVOLUE - desTotleWater-appDosages.get(appDosages.size()-1).getWater()) / coffeeMaterial.getWater_rate()));
                    //  selfWater= (float) (weight*coffeeMaterial.getWater_rate());
                    // water=MAXVOLUE - desTotleWater-selfWater;
                }
               if(!(totleCoffeeMaterial<coffeeMaterial.getMax_weight())){
                    weight=(float) mCoffee_max_weight-desTotleCoffee;
                    selfWater= (float) (weight*coffeeMaterial.getWater_rate());
                    water= this.appDosages.get(this.appDosages.size()-1).getWater();
                }
                break;
            case "奶粉":
                double extraMilkWater = totleWater - desTotleWater - lastAppDosage.getWeight() - lastAppDosage.getWeight() * milkMaterial.getWater_rate();
                if(!(Math.ceil(totleWater)<MAXVOLUE)){
                    // weight=(float) ((MAXVOLUE - desTotleWater) / (1+milkMaterial.getWater_rate()));
                    weight=(float) ((MAXVOLUE - desTotleWater-extraMilkWater) / (1+milkMaterial.getWater_rate()));
                    water= MAXVOLUE - desTotleWater - weight;
                }
                if(!(totleMilkMaterial<milkMaterial.getMax_weight())){
                    weight=(float)mMilk_max_weight-desTotleMilk;
                    //water= (float) (weight*milkMaterial.getWater_rate());
                    water= (float) ((float) (weight*milkMaterial.getWater_rate())+extraMilkWater);
                }
                break;
            case "巧克力粉":
                double extraChocolateWater = totleWater - desTotleWater - lastAppDosage.getWeight() - lastAppDosage.getWeight() * chocolateMaterial.getWater_rate();
                if(!(Math.ceil(totleWater)<MAXVOLUE)){
                    //weight= ((float) ((MAXVOLUE - desTotleWater) / (1+chocolateMaterial.getWater_rate())));
                    weight= ((float) ((MAXVOLUE - desTotleWater-extraChocolateWater) / (1+chocolateMaterial.getWater_rate())));
                    water= MAXVOLUE - desTotleWater - weight;
                }
                if(!(totleChocolateMaterial<chocolateMaterial.getMax_weight())){
                    weight=(float)mChocolate_max_weight-desTotleChocolate;
                    //water= (float) (weight*chocolateMaterial.getWater_rate());
                    water= (float) (weight*chocolateMaterial.getWater_rate()+extraChocolateWater);
                }
                break;
        }
        this.appDosages.get(this.appDosages.size()-1).setWeight( weight);
        this.appDosages.get(this.appDosages.size()-1).setSelfWater(selfWater);
        this.appDosages.get(this.appDosages.size()-1).setWater( water);

    }

    private boolean checkIsOutOfBound() {
        if(appDosages.size()==0||(appDosages.get(appDosages.size()-1).getMaterial_name())==null){
            return false;
        }
        getTotleDosages();
        LogUtil.e("appdos",appDosages.toString());
        if(!(Math.ceil(totleWater)<MAXVOLUE)){
            setTipText("杯子里已经放不下啦！");
            //sv.fullScroll(ScrollView.FOCUS_DOWN);
            // setTipText("杯子里已经放不下啦！");
            return true;
        }
        switch (appDosages.get(appDosages.size()-1).getMaterial_name()){
            case "糖":
                if(!(totleSugarMaterial<mSugar_max_weight)){
                    return true;
                }
                break;
            case "咖啡浓度":if(!(totleCoffeeMaterial<mCoffee_max_weight)){
                //sv.fullScroll(ScrollView.FOCUS_DOWN);
                setTipText("咖啡浓度已达上限！");
                return true;
            }
                break;
            case "奶粉":if(!(totleMilkMaterial<mMilk_max_weight)){
                return true;
            }
                break;
            case "巧克力粉":if(!(totleChocolateMaterial<mChocolate_max_weight)){
                return true;
            }
                break;
        }
        return false;
    }

    /**
     *this is the bound for column's device,to get rid of the useless data
     * @return
     */
    private boolean checkIsOutOfBoundForColumn() {
        if(appDosages.size()==0||(appDosages.get(appDosages.size()-1).getMaterial_name())==null){
            return false;
        }
        // //Log.e("appdos",appDosages.toString());
        if(totleVolume>MAXVOLUE){
            return true;
        }
        switch (appDosages.get(appDosages.size()-1).getMaterial_name()){
            case "糖":
                if(totleSugarMaterial>mSugar_max_weight){
                    return true;
                }
                break;
            case "咖啡浓度":if(totleCoffeeMaterial>mCoffee_max_weight){
                return true;
            }
                break;
            case "奶粉":if(totleMilkMaterial>mMilk_max_weight){
                return true;
            }
                break;
            case "巧克力粉":if(totleChocolateMaterial>mChocolate_max_weight){
                return true;
            }
                break;
            default:break;
        }
        return false;
    }
    private void setTipText(String s){
        top = DensityUtil.dp2px(this, 0);
        inTop = DensityUtil.dp2px(this, 70);
        LogUtil.e("tv_notify","translationY"+tv_notify.getTranslationY()+"intop"+ inTop);
        if(tv_notify.getText().toString().equals(s)&&(-tv_notify.getTranslationY())!= inTop){
            tipHandler.removeCallbacksAndMessages(null);
            tipHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    ObjectAnimator anim2 = ObjectAnimator.ofFloat(tv_notify, "translationY", top,-inTop);
                    anim2.setDuration(300);
                    anim2.start();
                }
            },2000);
            return;
        }
        tv_notify.setText(s);
        tv_notify.setVisibility(View.VISIBLE);
        final float translationY = tv_notify.getTranslationY();
        final ObjectAnimator anim = ObjectAnimator.ofFloat(tv_notify, "translationY", -inTop, top);
        anim.setDuration(300);
        anim.start();
        tipHandler.removeCallbacksAndMessages(null);
        tipHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                ObjectAnimator anim2 = ObjectAnimator.ofFloat(tv_notify, "translationY", top,-inTop);
                anim2.setDuration(300);
                anim2.start();
            }
        },2000);
    }

    @Override
    protected void onResume() {
        isToFine=false;
        musicNotOpen=SharedPrefUtil.getBoolean("musicNotOpen");
        if(!musicNotOpen){
            bt_music.setImageResource(R.drawable.music_open);
            startMusic();
        }else{
            bt_music.setImageResource(R.drawable.music_close);
            closeMusic();
        }
        super.onResume();
    }
    @Override
    protected void onPause() {
        if(!isToFine){
            closeMusic();
        }
        resetMaterialLocation();
        super.onPause();
    }
}
