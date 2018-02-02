package com.hzjytech.coffeeme.home;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.GridLayoutManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.BounceInterpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hzjytech.banner.SmartFragmentStatePagerAdapter;
import com.hzjytech.coffeeme.BaseActivity;
import com.hzjytech.coffeeme.Dialogs.CollectCoffeeDialog;
import com.hzjytech.coffeeme.R;
import com.hzjytech.coffeeme.configurations.Configurations;
import com.hzjytech.coffeeme.configurations.UmengConfig;
import com.hzjytech.coffeeme.culture.ScaleInTransformer;
import com.hzjytech.coffeeme.culture.SharedFragment;
import com.hzjytech.coffeeme.entities.AppDosage;
import com.hzjytech.coffeeme.entities.AppItem;
import com.hzjytech.coffeeme.entities.Good;
import com.hzjytech.coffeeme.listeners.IMethod1Listener;
import com.hzjytech.coffeeme.utils.DensityUtil;
import com.hzjytech.coffeeme.utils.LogUtil;
import com.hzjytech.coffeeme.utils.NetUtil;
import com.hzjytech.coffeeme.utils.SharedPrefUtil;
import com.hzjytech.coffeeme.utils.SignUtils;
import com.hzjytech.coffeeme.utils.TimeUtil;
import com.hzjytech.coffeeme.utils.ToastUtil;
import com.hzjytech.coffeeme.utils.UserUtils;
import com.hzjytech.coffeeme.widgets.BadgeView;
import com.hzjytech.coffeeme.widgets.BubbleSeekBar;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import cn.jpush.android.api.JPushInterface;

//购买界面
@ContentView(R.layout.activity_modulation)
public class ModulationActivity extends BaseActivity {

    private static final String TAG = ModulationActivity.class.getSimpleName();
    private static final String COFFEEBEAN = "咖啡豆";
    private static final String ESPRESSONAME = "意式浓缩";
    private static final String MILK="奶粉";
    private static final String SUGAR="糖";
    private static final String CHOCOLATE="巧克力粉";

    private static ModulationActivity mInstance;
    @ViewInject(R.id.tvModulationName)
    private TextView tvModulationName;

    @ViewInject(R.id.tvModulationPrice)
    private TextView tvModulationPrice;

    @ViewInject(R.id.tabModulationDosage)
    private TabLayout tabModulationDosage;

    @ViewInject(R.id.vPgModulationShow)
    private ViewPager vPgModulationShow;

    @ViewInject(R.id.tvModulationMin)
    private TextView tvModulationMin;

    @ViewInject(R.id.numSkbarModulation)
    private BubbleSeekBar numSkbarModulation;

    @ViewInject(R.id.tvModulationHint)
    private TextView tvModulationHint;

    @ViewInject(R.id.tvModulationMax)
    private TextView tvModulationMax;

    @ViewInject(R.id.btnModulationShow)
    private ImageView btnModulationShow;

    @ViewInject(R.id.tvModulationCount)
    private TextView tvModulationCount;
    @ViewInject(R.id.llModualtionCount)
    private LinearLayout llModualtionCount;

    @ViewInject(R.id.tvModulationUnit)
    private TextView tvModulationUnit;
    @ViewInject(R.id.ivModulationCart)
    private ImageView ivModulationCart;

    @ViewInject(R.id.btnModulationAddCart)
    private ImageView btnModulationAddCart;
    @ViewInject(R.id.rl_addcart)
    private RelativeLayout rl_addcart;

    private AppItem appItem;
    private ModulationViewPagerAdapter modulationViewPagerAdapter;
    private ArrayList<AppDosage> appDosages = new ArrayList<AppDosage>();
    private boolean isPageSelected = false;

    private BadgeView cartBadgeView;
    private boolean isCurrentPrice = true;
    private DecimalFormat df;
    private Handler handler=new Handler();
    private float top;
    private float inTop;

    public static ModulationActivity Instance() {
        if (null == mInstance)
            mInstance = new ModulationActivity();
        return mInstance;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mInstance = this;
        cartBadgeView = new BadgeView(ModulationActivity.this, ivModulationCart);
        cartBadgeView.setBadgeBackgroundColor(Color.WHITE);
        cartBadgeView.setTextColor(Color.BLACK);
        tvModulationHint.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {

                if(motionEvent.getAction()==MotionEvent.ACTION_MOVE){
                    handler.removeCallbacksAndMessages(null);
                    ObjectAnimator anim2 = ObjectAnimator.ofFloat(tvModulationHint, "translationY", top,-inTop);
                    anim2.setDuration(300);
                    anim2.start();
                }
                return false;
            }
        });
        if (getIntent() != null) {
            appItem = (AppItem) getIntent().getSerializableExtra(Configurations.APP_ITEM);
            LogUtil.e("appItem",appItem.toString());
            if (appItem != null) {
                InitData();
                showLoading();
            }
        } else {
            finish();
        }

    }

    private void initTabLayout() {
        for (int i = 0; i < tabModulationDosage.getTabCount(); i++) {
            TabLayout.Tab tab = tabModulationDosage.getTabAt(i);
            tab.setCustomView(modulationViewPagerAdapter.getTabView(i));
        }
    }

    private void initCart() {
        ivModulationCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!SharedPrefUtil.getLoginType().equals(SharedPrefUtil.LOGINING)) {
                    hideLoading();
                    goLogin();
                    return;
                }
                startActivity(new Intent(ModulationActivity.this, NewCartActivity.class));
            }
        });

        if (!SharedPrefUtil.getLoginType().equals(SharedPrefUtil.LOGINING)) {
            hideLoading();
            return;
        }
        RequestParams entity = new RequestParams(Configurations.URL_GOODS);
        entity.addParameter(Configurations.AUTH_TOKEN, UserUtils.getUserInfo().getAuth_token());

        String device_id= JPushInterface.getRegistrationID(ModulationActivity.this);
        String timeStamp= TimeUtil.getCurrentTimeString();
        entity.addParameter(Configurations.TIMESTAMP, timeStamp);
        entity.addParameter(Configurations.DEVICE_ID,device_id );

        Map<String, String> map=new TreeMap<String, String>();
        map.put(Configurations.AUTH_TOKEN, UserUtils.getUserInfo().getAuth_token());
        entity.addParameter(Configurations.SIGN, SignUtils.createSignString(device_id,timeStamp,map));

        x.http().get(entity, new Callback.CommonCallback<JSONObject>() {
            @Override
            public void onSuccess(JSONObject result) {
                hideLoading();
                checkResOld(result);
                try {
                    if (result.getInt(Configurations.STATUSCODE) == 200) {
                        List<Good> goods = JSON.parseArray(result.getJSONObject("results").getString("goods"), Good.class);

                        cartBadgeView.setTextSize(8);
                        cartBadgeView.setBadgePosition(BadgeView.POSITION_TOP_RIGHT);
                        if (goods.size() < 1) {
                            cartBadgeView.hide();

                        } else if (goods.size() < 100) {
                            cartBadgeView.setText(String.valueOf(goods.size()));
                            cartBadgeView.show();

                        } else {
                            cartBadgeView.setText(R.string.cart_count_max_value);
                            cartBadgeView.show();

                        }
                    }
                    result.getJSONObject("results").getString("goods");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                hideLoading();
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


    private void InitData() {
        df = new DecimalFormat("0.00");
        numSkbarModulation.setThumbDrawble(R.drawable.bumb_coffee);
        tvModulationName.setText(appItem.getName());
        tvModulationPrice.setText(String.valueOf(df.format(appItem.getCurrent_price())));

        modulationViewPagerAdapter = new ModulationViewPagerAdapter(getSupportFragmentManager(), ModulationActivity.this);
        vPgModulationShow.setAdapter(modulationViewPagerAdapter);

        vPgModulationShow.setOffscreenPageLimit(3);
        vPgModulationShow.setPageMargin(60);
        vPgModulationShow.setPageTransformer(true, new ScaleInTransformer());

        tvModulationCount.setText("1");
        vPgModulationShow.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }
            @Override
            public void onPageSelected(int position) {
                numSkbarModulation.correctOffsetWhenContainerOnScrolling();
                if (!COFFEEBEAN.equals(appDosages.get(position).getApp_material().getName())) {

                    tvModulationUnit.setText("克");
                } else {

                    tvModulationUnit.setText("%");
                }

                isPageSelected = true;

                float progress;
                if (appItem.getName().equals(ESPRESSONAME)) {
                    numSkbarModulation.setType(BubbleSeekBar.ESPRESSONAME);
                    tvModulationMin.setText(String.valueOf(((int) appDosages.get(position).getMin_weight())));
                    tvModulationMax.setText(String.valueOf(((int) appDosages.get(position).getMax_weight())));
                    numSkbarModulation.setMin(appDosages.get(position).getMin_weight());
                    numSkbarModulation.setMax(appDosages.get(position).getMax_weight());
                    numSkbarModulation.setThumbDrawble(R.drawable.bumb_coffee);
                    numSkbarModulation.setAutoAdjustSectionMark(true);
                    numSkbarModulation.setSectionCount(0);
                    numSkbarModulation.setShowProgressInFloat(false);
                    progress =  getWeightFromTab(new TabViewHolder(tabModulationDosage.getTabAt(position).getCustomView()).tvModulationTabWeight.getText().toString());
//                    progress = (int) ((getWeightFromText(tabModulationDosage.getTabAt(position).getText().toString()))
//                            / (appDosages.get(position).getMin_weight() + appDosages.get(position).getMax_weight()));
                } else {

                    if (!COFFEEBEAN.equals(appDosages.get(position).getApp_material().getName())) {
                        numSkbarModulation.setType(BubbleSeekBar.NORMAL);
                        tvModulationMin.setText(String.valueOf(appDosages.get(position).getMin_weight()));
                        tvModulationMax.setText(String.valueOf(appDosages.get(position).getMax_weight()));
                        numSkbarModulation.setMin(appDosages.get(position).getMin_weight());
                        numSkbarModulation.setMax(appDosages.get(position).getMax_weight());
                        numSkbarModulation.setShowProgressInFloat(true);
                        progress =getWeightFromTab(new TabViewHolder(tabModulationDosage.getTabAt(position).getCustomView()).tvModulationTabWeight.getText().toString());

//                        progress = (int) ((getWeightFromText(tabModulationDosage.getTabAt(position).getText().toString()) - appDosages.get(position).getMin_weight())
//                                / (appDosages.get(position).getMax_weight() - appDosages.get(position).getMin_weight()) * 100);

                    } else {
                        tvModulationMin.setText(String.valueOf(((int) appDosages.get(position).getMin_weight())));
                        tvModulationMax.setText(String.valueOf(((int) appDosages.get(position).getMax_weight())));
                        numSkbarModulation.setMax( appDosages.get(position).getMax_weight());
                        numSkbarModulation.setMin(appDosages.get(position).getMin_weight());
                        numSkbarModulation.setType(BubbleSeekBar.COFFEE);
                        numSkbarModulation.setShowProgressInFloat(false);
                        progress =  getWeightFromTab(new TabViewHolder(tabModulationDosage.getTabAt(position).getCustomView()).tvModulationTabWeight.getText().toString());
//                      progress = (int) ((getWeightFromText(tabModulationDosage.getTabAt(position).getText().toString()) - appDosages.get(position).getMin_weight())
//                                / (appDosages.get(position).getMax_weight() - appDosages.get(position).getMin_weight()) * 100);

                    }
                }
                LogUtil.e("selecnumMax",numSkbarModulation.getMax()+"");
                LogUtil.e("SelectnumMin",numSkbarModulation.getMin()+"");
                numSkbarModulation.setProgress(progress);
                isPageSelected = false;
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                LogUtil.e("StatenumMin",numSkbarModulation.getMin()+"");
               // numSkbarModulation.correctOffsetWhenContainerOnScrolling();
            }
        });
        getAppDosages(appItem.getId());
        numSkbarModulation.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                numSkbarModulation.correctOffsetWhenContainerOnScrolling();
                return false;
            }
        });
        numSkbarModulation.setOnProgressChangedListener(new BubbleSeekBar.OnProgressChangedListenerAdapter() {
            @Override
            public void onProgressChanged(float progress, boolean isDraged) {
                Log.e("actualProgress",progress+"");
                if (!isPageSelected) {
                    TabViewHolder holder = new TabViewHolder(tabModulationDosage.getTabAt(vPgModulationShow.getCurrentItem()).getCustomView());
//                    TabLayout.Tab tab = tabModulationDosage.getTabAt(vPgModulationShow.getCurrentItem());

                    if (appItem.getName().equals(ESPRESSONAME)) {
                        if (numSkbarModulation.getProgress()== 1) {
                            isCurrentPrice = true;
                        } else {
                            isCurrentPrice = false;
                        }
                        holder.tvModulationTabName.setText(appDosages.get(vPgModulationShow.getCurrentItem()).getApp_material().getName());
                        holder.tvModulationTabWeight.setText((int)progress + "份");
//                        tab.setText(appDosages.get(vPgModulationShow.getCurrentItem()).getApp_material().getName()
//                                + "\n" + ((int) numSkbarModulation.getValue()) + "份");
                    } else {
                        if (!COFFEEBEAN.equals(appDosages.get(vPgModulationShow.getCurrentItem()).getApp_material().getName())) {
                            holder.tvModulationTabName.setText(appDosages.get(vPgModulationShow.getCurrentItem()).getApp_material().getName());
                            holder.tvModulationTabWeight.setText(progress+ "克");
//                            tab.setText((appDosages.get(vPgModulationShow.getCurrentItem()).getApp_material().getName()
//                                    + "\n" + numSkbarModulation.getValue() + "克"));
                        } else {
                            holder.tvModulationTabName.setText(getResources().getString(R.string.str_concentration));
                            holder.tvModulationTabWeight.setText(progress + "%");
//                            tab.setText(getResources().getString(R.string.str_concentration)
//                                    + "\n" + ((int) numSkbarModulation.getValue()) + "%");
                        }
                    }


                    tvModulationPrice.setText(String.valueOf(df.format(sum())));

                }
            }

            @Override
            public void getProgressOnActionUp(float progress) {
                if (!isPageSelected) {
                    TabLayout.Tab tab = tabModulationDosage.getTabAt(vPgModulationShow.getCurrentItem());

                    if (appItem.getName().equals(ESPRESSONAME)) {
                        if (numSkbarModulation.getProgress()== 1) {
                        } else {
                            setHintText("很苦很提神哦");
                        }
                    } else {
                        if (!COFFEEBEAN.equals(appDosages.get(vPgModulationShow.getCurrentItem()).getApp_material().getName())) {
                            if ("糖".equals(appDosages.get(vPgModulationShow.getCurrentItem()).getApp_material().getName())) {
                                if (numSkbarModulation.getProgress() > (appDosages.get(vPgModulationShow.getCurrentItem()).getWeight() + appDosages.get(vPgModulationShow.getCurrentItem()).getMax_weight() * 0.3)) {
                                    setHintText("要甜掉牙咯");
                                } else if (numSkbarModulation.getProgress() < (appDosages.get(vPgModulationShow.getCurrentItem()).getWeight() - appDosages.get(vPgModulationShow.getCurrentItem()).getMax_weight() * 0.3)) {
                                    setHintText("小心会很苦哦");
                                }

                            } else if ("奶粉".equals(appDosages.get(vPgModulationShow.getCurrentItem()).getApp_material().getName())) {
                                if (numSkbarModulation.getProgress() > (appDosages.get(vPgModulationShow.getCurrentItem()).getWeight() + appDosages.get(vPgModulationShow.getCurrentItem()).getMax_weight() * 0.3)) {
                                    setHintText("咖啡会很腻哦");
                                } else if (numSkbarModulation.getProgress() < (appDosages.get(vPgModulationShow.getCurrentItem()).getWeight() - appDosages.get(vPgModulationShow.getCurrentItem()).getMax_weight() * 0.3)) {
                                    setHintText("咖啡不够细哦");
                                }
                            } else if ("巧克力粉".equals(appDosages.get(vPgModulationShow.getCurrentItem()).getApp_material().getName())) {
                                if (numSkbarModulation.getProgress() > (appDosages.get(vPgModulationShow.getCurrentItem()).getWeight() + appDosages.get(vPgModulationShow.getCurrentItem()).getMax_weight() * 0.3)) {
                                    setHintText("太多小心蛀牙哦");
                                } else if (numSkbarModulation.getProgress() < (appDosages.get(vPgModulationShow.getCurrentItem()).getWeight() - appDosages.get(vPgModulationShow.getCurrentItem()).getMax_weight() * 0.3)) {
                                    setHintText("少了点巧克力味哦");
                                }
                            }

                        } else {
                            if (numSkbarModulation.getProgress() > (appDosages.get(vPgModulationShow.getCurrentItem()).getWeight() + appDosages.get(vPgModulationShow.getCurrentItem()).getMax_weight() * 0.3)) {
                                setHintText("很苦很提神哦");
                            } else if (numSkbarModulation.getProgress() < (appDosages.get(vPgModulationShow.getCurrentItem()).getWeight() - appDosages.get(vPgModulationShow.getCurrentItem()).getMax_weight() * 0.3)) {
                                setHintText("味道会很淡哦");
                            }

                        }
                    }

                }
            }

            @Override
            public void getProgressOnFinally(float progress) {
                super.getProgressOnFinally(progress);
            }
        });

        final String[] counts = new String[]{"1", "2", "3", "4", "5", "6", "7", "8", "9"};
        llModualtionCount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final SharedFragment countFragment = new SharedFragment();
                countFragment.setAdapter(counts, new IMethod1Listener() {
                    @Override
                    public void OnMethod1Listener(int param) {
                        tvModulationCount.setText(counts[param]);
                        countFragment.dismiss();
                    }
                }, new GridLayoutManager(ModulationActivity.this, 5));
                countFragment.show(getSupportFragmentManager(), "share");
            }
        });


    }

    private void setHintText(String text) {
        top = DensityUtil.dp2px(this, 0);
        inTop = DensityUtil.dp2px(this, 70);
        LogUtil.e("tv_notify","translationY"+tvModulationHint.getTranslationY()+"intop"+ inTop);
        if(tvModulationHint.getText().toString().equals(text)&&(-tvModulationHint.getTranslationY())!= inTop){
            handler.removeCallbacksAndMessages(null);
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    ObjectAnimator anim2 = ObjectAnimator.ofFloat(tvModulationHint, "translationY", top,-inTop);
                    anim2.setDuration(300);
                    anim2.start();
                }
            },2000);
            return;
        }
        tvModulationHint.setText(text);
        tvModulationHint.setVisibility(View.VISIBLE);
        final float translationY = tvModulationHint.getTranslationY();
        final ObjectAnimator anim = ObjectAnimator.ofFloat(tvModulationHint, "translationY", -inTop, top);
        anim.setDuration(300);
        anim.start();
        handler.removeCallbacksAndMessages(null);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                ObjectAnimator anim2 = ObjectAnimator.ofFloat(tvModulationHint, "translationY", top,-inTop);
                anim2.setDuration(300);
                anim2.start();
            }
        },2000);
    }


    @Event(R.id.btnModulationCollect)
    private void onModulationCollectClick(View v) {

        if (NetUtil.isNetworkAvailable(ModulationActivity.this)) {

            if (SharedPrefUtil.getLoginType().equals(SharedPrefUtil.LOGINING)) {
                CollectCoffeeDialog collectCoffeeDialog = new CollectCoffeeDialog();
                collectCoffeeDialog.setListener(new CollectCoffeeDialog.CollectDialogable() {
                    @Override
                    public void onCollectDialog(String string) {
                        colletCoffee(string);
                    }
                });
                collectCoffeeDialog.show(getSupportFragmentManager(), "collectCoffee");

            } else {
                goLogin();
            }

        }
    }

    private void colletCoffee(String name) {
        String auth_token = UserUtils.getUserInfo().getAuth_token();

        /**
         * parameter format
         * {"parent_id": 1, "name": "my卡布奇诺", "price": 17, "description": "my卡布奇诺", "volume": 260}
         */
        JSONObject app_item = new JSONObject();
        try {
            app_item.put("parent_id", appItem.getId());
            app_item.put("name", name);
            app_item.put("price", sum());
            app_item.put("description", appItem.getDescription());
            app_item.put("volume", appItem.getVolume());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JSONArray app_dosages = new JSONArray();

        if(tabModulationDosage.getTabCount()<1){
            ToastUtil.showShort(ModulationActivity.this,"咖啡成分信息错误，请退出重试");
        }else {
            for (int i = 0; i < tabModulationDosage.getTabCount(); i++) {
                JSONObject dosage = new JSONObject();
                try {
                    dosage.put("id", appDosages.get(i).getId());
                    dosage.put("weight", getWeightFromTab(new TabViewHolder(tabModulationDosage.getTabAt(i).getCustomView()).tvModulationTabWeight.getText().toString().trim()));
//                dosage.put("weight", getWeightFromText(tabModulationDosage.getTabAt(i).getText().toString().trim()));
                    dosage.put("water", appDosages.get(i).getWater());
                    app_dosages.put(dosage);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        if (!TextUtils.isEmpty(auth_token)) {
            RequestParams entity = new RequestParams(Configurations.URL_APP_ITEMS);
            entity.addParameter(Configurations.AUTH_TOKEN, auth_token);
            entity.addParameter("app_item", app_item.toString());
            entity.addParameter("app_dosages", app_dosages.toString());

            String device_id= JPushInterface.getRegistrationID(ModulationActivity.this);
            String timeStamp= TimeUtil.getCurrentTimeString();
            entity.addParameter(Configurations.TIMESTAMP, timeStamp);
            entity.addParameter(Configurations.DEVICE_ID,device_id );
            Map<String ,String > map=new TreeMap<>();
            map.put(Configurations.AUTH_TOKEN, auth_token);
            map.put("app_item", app_item.toString());
            map.put("app_dosages", app_dosages.toString());

            entity.addParameter(Configurations.SIGN, SignUtils.createSignString(device_id,timeStamp,map));
            x.http().post(entity, new Callback.CommonCallback<JSONObject>() {

                @Override
                public void onSuccess(JSONObject result) {
                    MobclickAgent.onEvent(ModulationActivity.this, UmengConfig.EVENT_COLLECT_COFFEE);
                    LogUtil.d(TAG, result.toString());

                    checkResOld(result);
                    try {
                        ToastUtil.showShort(ModulationActivity.this, result.getString(Configurations.STATUSMSG));
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

    @Event(R.id.ivModulationBack)
    private void onModulationBackClick(View view) {
        finish();
    }

    @Event(R.id.btnModulationAddCart)
    private void onModulationAddCartClick(View v) {

        if (!SharedPrefUtil.getLoginType().equals(SharedPrefUtil.LOGINING)) {

            goLogin();
            return;
        }

        if (!NetUtil.isNetworkAvailable(mInstance))
            return;


        final int number = Integer.parseInt(tvModulationCount.getText().toString());

        if (number < 1) {
            ToastUtil.showShort(ModulationActivity.this, "购买数量不能为0");
        } else {
            btnModulationAddCart.setClickable(false);
            int[] startLocation = new int[2];
            btnModulationShow.getLocationInWindow(startLocation);
            startLocation[0]+= DensityUtil.dp2px(ModulationActivity.this, 27f);
            startLocation[1]+= DensityUtil.dp2px(ModulationActivity.this, 28f);
            ImageView good = new ImageView(ModulationActivity.this);
            good.setImageResource(R.drawable.cup);
            animation(good, startLocation, number);
        }

    }

    private void animation(final View view, int[] startLocation, final int number) {
        ViewGroup anim = createAnimLayout();
        anim.addView(view);
        final View v = addViewToAnimLayout(anim, view, startLocation);

        int[] endLocation = new int[2];
        ivModulationCart.getLocationInWindow(endLocation);

        int endX = endLocation[0] - startLocation[0];
        int endY = endLocation[1] - startLocation[1];
        TranslateAnimation translateAnimationX = new TranslateAnimation(0, endX, 0, 0);
        translateAnimationX.setInterpolator(new LinearInterpolator());
        translateAnimationX.setRepeatCount(0);
        translateAnimationX.setFillAfter(true);

        TranslateAnimation translateAnimationY = new TranslateAnimation(0, 0,
                0, endY);
        translateAnimationY.setInterpolator(new AccelerateInterpolator());
        translateAnimationY.setRepeatCount(0);// 动画重复执行的次数
        translateAnimationX.setFillAfter(true);
        AnimationSet set = new AnimationSet(false);
        set.setFillAfter(false);
        set.addAnimation(translateAnimationY);
        set.addAnimation(translateAnimationX);
        set.setDuration(300);// 动画的执行时间
        v.startAnimation(set);

        final PropertyValuesHolder pvhY = PropertyValuesHolder.ofFloat("scaleX", 1f,
                1.1f,1f);
        final PropertyValuesHolder pvhZ = PropertyValuesHolder.ofFloat("scaleY", 1f,
                1.1f,1f);
        final ObjectAnimator cartAnim = ObjectAnimator.ofPropertyValuesHolder(rl_addcart, pvhY, pvhZ);
        set.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                view.setVisibility(View.VISIBLE);
                addCart(number);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                view.setVisibility(View.GONE);
                cartAnim.setDuration(200).start();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {


            }
        });

    }

    private ViewGroup createAnimLayout() {
        ViewGroup rootView = (ViewGroup) this.getWindow().getDecorView();
        LinearLayout animLayout = new LinearLayout(this);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.MATCH_PARENT);
        animLayout.setLayoutParams(lp);
        animLayout.setId(Integer.MAX_VALUE - 1);
        animLayout.setBackgroundResource(android.R.color.transparent);
        rootView.addView(animLayout);
        return animLayout;
    }

    private View addViewToAnimLayout(final ViewGroup parent, final View view,
                                     int[] location) {
        int x = location[0];
        int y = location[1];
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(DensityUtil.dp2px(ModulationActivity.this, 44), DensityUtil.dp2px(ModulationActivity.this, 44));
//                LinearLayout.LayoutParams.WRAP_CONTENT,
//                LinearLayout.LayoutParams.WRAP_CONTENT);
        lp.leftMargin = x;
        lp.topMargin = y;
        view.setLayoutParams(lp);
        return view;
    }

    private void addCart(final int number) {
        String auth_token = UserUtils.getUserInfo().getAuth_token();
        int item_id = appItem.getId();

        JSONArray array = new JSONArray();
        try {
            for (int i = 0; i < appDosages.size(); i++) {
                JSONObject object = new JSONObject();
                object.put("id", appDosages.get(i).getId());
                object.put("weight", getWeightFromTab(new TabViewHolder(tabModulationDosage.getTabAt(i).getCustomView()).tvModulationTabWeight.getText().toString().trim()));
//                object.put("weight", getWeightFromText(tabModulationDosage.getTabAt(i).getText().toString().trim()));
                object.put("water", appDosages.get(i).getWater());
                array.put(object);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        String custom_dosages = array.toString();
        float price = Float.parseFloat(tvModulationPrice.getText().toString());

        RequestParams entity = new RequestParams(Configurations.URL_GOODS);
        entity.addParameter(Configurations.AUTH_TOKEN, auth_token);
        entity.addParameter(Configurations.APP_ITEM_ID, item_id);
        entity.addParameter("custom_dosages", custom_dosages);
        entity.addParameter(Configurations.PRICE, price);
        entity.addParameter(Configurations.NUMBER, number);

        String device_id= JPushInterface.getRegistrationID(ModulationActivity.this);
        String timeStamp= TimeUtil.getCurrentTimeString();
        entity.addParameter(Configurations.TIMESTAMP, timeStamp);
        entity.addParameter(Configurations.DEVICE_ID,device_id );

        Map<String, String> map=new TreeMap<String, String>();
        map.put(Configurations.AUTH_TOKEN, auth_token);
        map.put(Configurations.APP_ITEM_ID, String.valueOf(item_id));
        map.put("custom_dosages", custom_dosages);
        map.put(Configurations.PRICE, String.valueOf(price));
        map.put(Configurations.NUMBER, String.valueOf(number));
        entity.addParameter(Configurations.SIGN, SignUtils.createSignString(device_id,timeStamp,map));


        x.http().post(entity, new Callback.CommonCallback<JSONObject>() {

            @Override
            public void onSuccess(JSONObject result) {

                checkResOld(result);
                try {

                    if (result.getInt(Configurations.STATUSCODE) == 200) {

                        btnModulationAddCart.setClickable(true);
                        MobclickAgent.onEvent(ModulationActivity.this, UmengConfig.EVENT_ADD_CART);
                        String cartBadgeCount;

                        if (TextUtils.isEmpty(cartBadgeView.getText().toString())) {
                            cartBadgeView.setText("" + number);
                        } else {
                            if (cartBadgeView.getText().toString().contains("+")) {
                                cartBadgeCount = cartBadgeView.getText().toString().substring(0, cartBadgeView.getText().toString().length() - 1);
                            } else {
                                cartBadgeCount = cartBadgeView.getText().toString();
                            }

                            if (Integer.parseInt(cartBadgeCount) + number < 100) {

                                cartBadgeView.setText(String.valueOf(Integer.parseInt(cartBadgeCount) + number));
                            } else {

                                cartBadgeView.setText(getString(R.string.cart_count_max_value));
                            }
                        }
                        cartBadgeView.show();
                    }else {
                        ToastUtil.showShort(ModulationActivity.this, result.getString(Configurations.STATUSMSG));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        initCart();
        MobclickAgent.onPageStart(UmengConfig.MODULATIONACTIVITY);
        MobclickAgent.onResume(this);

    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(UmengConfig.MODULATIONACTIVITY);
        MobclickAgent.onPause(this);
    }

    @Event(R.id.btnModulationBuy)
    private void onModulationBuyClick(View view) {

        if (!SharedPrefUtil.getLoginType().equals(SharedPrefUtil.LOGINING)) {
            goLogin();
            return;
        }

        Intent intent = new Intent(ModulationActivity.this, NewPaymentActivity.class);
        appItem.setCurrent_price(Float.parseFloat(tvModulationPrice.getText().toString()));
        intent.putExtra("appItem", appItem);
        intent.putExtra("type", 0);
        ArrayList<AppDosage> appDosagesLst = appDosages;

        for (int i = 0; i < appDosages.size(); i++) {
            AppDosage appDosage = appDosages.get(i);
            appDosage.setWeight(getWeightFromTab(new TabViewHolder(tabModulationDosage.getTabAt(i).getCustomView()).tvModulationTabWeight.getText().toString()));
//            appDosage.setWeight(getWeightFromText(tabModulationDosage.getTabAt(i).getText().toString()));
        }
        intent.putParcelableArrayListExtra("appDosages", appDosagesLst);
        if ((Integer.parseInt(tvModulationCount.getText().toString())) < 1) {
            ToastUtil.showShort(ModulationActivity.this, "请选择购买数量");
        } else {
            intent.putExtra("count", (Integer.parseInt(tvModulationCount.getText().toString())));
            startActivity(intent);
        }
    }

    /**
     * Two significant digits after decimal point
     *
     * @param sum
     * @return
     */
    private float toKeep2(float sum) {
        return (float) (Math.round(sum * 100)) / 100;

    }


    private class ModulationViewPagerAdapter extends SmartFragmentStatePagerAdapter {

        private final Context context;

        public ModulationViewPagerAdapter(FragmentManager fm, Context context) {
            super(fm);
            this.context = context;
        }


        @Override
        public Fragment getItem(int position) {
            return ModulationDosageItemFragment.newInstance(initViewFrom(appDosages.get(position).getApp_material().getName()));
        }

        private int initViewFrom(String name) {
            switch (name) {
                //糖
                case "糖":
                    return R.drawable.img_sugar;
                //咖啡豆
                case "咖啡豆":
                    return R.drawable.img_coffeebean;
                //奶粉
                case "奶粉":
                    return R.drawable.img_milk;
                //巧克力粉
                case "巧克力粉":
                    return R.drawable.img_chocolatepowed;
                default:
                    return 0;


            }
        }

        @Override
        public int getCount() {
            return appDosages.size();
        }

//        @Override
//        public CharSequence getPageTitle(int position) {
//
//            if (appItem.getName().equals(ESPRESSONAME)) {
//                return appDosages.get(position).getApp_material().getName()
//                        + "\n" + ((int) appDosages.get(position).getWeight()) + "份";
//            } else {
//                if (!COFFEEBEAN.equals(appDosages.get(position).getApp_material().getName())) {
//
//                    return appDosages.get(position).getApp_material().getName()
//                            + "\n" + appDosages.get(position).getWeight() + "克";
//                } else {
//
//                    return getResources().getString(R.string.str_concentration)
//                            + "\n" + ((int) appDosages.get(position).getWeight()) + "%";
//                }
//            }
//
//        }


        public View getTabView(int position) {
            View view = LayoutInflater.from(context).inflate(R.layout.tab_modulation, null);

            TabViewHolder tabViewHolder = new TabViewHolder(view);
            if (appItem.getName().equals(ESPRESSONAME)) {
                tabViewHolder.tvModulationTabName.setText(appDosages.get(position).getApp_material().getName());
                tabViewHolder.tvModulationTabWeight.setText(((int) appDosages.get(position).getWeight()) + "份");
            } else {
                if (!COFFEEBEAN.equals(appDosages.get(position).getApp_material().getName())) {

                    tabViewHolder.tvModulationTabName.setText(appDosages.get(position).getApp_material().getName());
                    tabViewHolder.tvModulationTabWeight.setText(appDosages.get(position).getWeight() + "克");
                } else {
                    tabViewHolder.tvModulationTabName.setText(getResources().getString(R.string.str_concentration));
                    tabViewHolder.tvModulationTabWeight.setText(((int) appDosages.get(position).getWeight()) + "%");
                }
            }
            return view;
        }
    }


    class TabViewHolder {
        private final TextView tvModulationTabName;
        private final TextView tvModulationTabWeight;

        public TabViewHolder(View view) {
            tvModulationTabName = (TextView) view.findViewById(R.id.tvModulationTabName);
            tvModulationTabWeight = (TextView) view.findViewById(R.id.tvModulationTabWeight);
        }
    }

    private void getAppDosages(int id_item_id) {

        RequestParams entity = new RequestParams(Configurations.URL_APP_DOSAGES);
        entity.addParameter(Configurations.APP_ITEM_ID, id_item_id);

        String device_id= JPushInterface.getRegistrationID(ModulationActivity.this);
        String timeStamp= TimeUtil.getCurrentTimeString();
        entity.addParameter(Configurations.TIMESTAMP, timeStamp);
        entity.addParameter(Configurations.DEVICE_ID,device_id );

        Map<String,String> map=new TreeMap<>();
        map.put(Configurations.APP_ITEM_ID, String.valueOf(id_item_id));
        entity.addParameter(Configurations.SIGN, SignUtils.createSignString(device_id,timeStamp,map));

        x.http().get(entity, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                parseResult(result);
                modulationViewPagerAdapter.notifyDataSetChanged();
                tabModulationDosage.setupWithViewPager(vPgModulationShow);
                initTabLayout();

                tabModulationDosage.getTabAt(0).getCustomView().setSelected(true);


                float progress = appDosages.get(0).getWeight();

                isPageSelected = true;


                if (appItem.getName().equals(ESPRESSONAME)) {
                    tvModulationMin.setText(String.valueOf(((int) appDosages.get(0).getMin_weight())));
                    tvModulationMax.setText(String.valueOf(((int) appDosages.get(0).getMax_weight())));
                    numSkbarModulation.setMin(appDosages.get(0).getMin_weight());
                    numSkbarModulation.setMax(appDosages.get(0).getMax_weight());
                    numSkbarModulation.setType(BubbleSeekBar.ESPRESSONAME);
                    numSkbarModulation.setSectionCount(0);
                    numSkbarModulation.setShowProgressInFloat(false);
                    numSkbarModulation.setAutoAdjustSectionMark(true);
                    tvModulationUnit.setText("份");

                    progress =  appDosages.get(0).getWeight();
                } else {
                    if (COFFEEBEAN.equals(appDosages.get(0).getApp_material().getName())) {
                        tvModulationMin.setText(String.valueOf(((int) appDosages.get(0).getMin_weight())));
                        tvModulationMax.setText(String.valueOf(((int) appDosages.get(0).getMax_weight())));
                        numSkbarModulation.setMax(appDosages.get(0).getMax_weight());
                        numSkbarModulation.setMin(appDosages.get(0).getMin_weight());
                        numSkbarModulation.setType(BubbleSeekBar.COFFEE);
                        numSkbarModulation.setShowProgressInFloat(false);
                        tvModulationUnit.setText("%");

                    } else {
                        numSkbarModulation.setType(BubbleSeekBar.NORMAL);
                        tvModulationMin.setText(String.valueOf(appDosages.get(0).getMin_weight()));
                        tvModulationMax.setText(String.valueOf(appDosages.get(0).getMax_weight()));
                        numSkbarModulation.setMin(appDosages.get(0).getMin_weight());
                        numSkbarModulation.setMax(appDosages.get(0).getMax_weight());
                        numSkbarModulation.setShowProgressInFloat(true);
                        tvModulationUnit.setText("克");
                    }
                }
                //numSkbarModulation.correctOffsetWhenContainerOnScrolling();
                numSkbarModulation.setProgress(progress);
                isPageSelected = false;
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
                appDosages = new Gson().fromJson(results.getJSONObject("results").getString("app_dosages"),
                        new TypeToken<ArrayList<AppDosage>>() {
                        }.getType());

            } else {
                ToastUtil.showShort(ModulationActivity.this, results.getString(Configurations.STATUSMSG));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * +
     *
     * @return
     */
    private float sum() {
        float sum = 0.0f;
        if (tabModulationDosage.getTabCount() == 1) {
            if (isCurrentPrice) {
                sum = appItem.getCurrent_price();
            } else {
                sum = appItem.getPrice();
            }

        } else {
            sum = 0.0f + appItem.getCurrent_price();
            for (int i = 0; i < tabModulationDosage.getTabCount(); i++) {

                if (COFFEEBEAN.equals(appDosages.get(i).getApp_material().getName())) {

                    if (appItem.getName().equals(ESPRESSONAME)) {
                        sum += ((getWeightFromTab(new TabViewHolder(tabModulationDosage.getTabAt(i).getCustomView()).tvModulationTabWeight.getText().toString())
                                - appDosages.get(i).getWeight()) * (appDosages.get(i).getApp_material().getUnit_price()));
//                        sum += ((getWeightFromText(tabModulationDosage.getTabAt(i).getText().toString())
//                                - appDosages.get(i).getWeight()) * (appDosages.get(i).getApp_material().getUnit_price()));
                    } else {
//                        sum += ((getWeightFromText(tabModulationDosage.getTabAt(i).getText().toString())
                        sum += ((getWeightFromTab(new TabViewHolder(tabModulationDosage.getTabAt(i).getCustomView()).tvModulationTabWeight.getText().toString())
                                - appDosages.get(i).getWeight()) * (appDosages.get(i).getApp_material().getUnit_price() / 10));
                    }

                } else {

//                    sum += ((getWeightFromText(tabModulationDosage.getTabAt(i).getText().toString())
                    sum += ((getWeightFromTab(new TabViewHolder(tabModulationDosage.getTabAt(i).getCustomView()).tvModulationTabWeight.getText().toString())
                            - appDosages.get(i).getWeight()) * (appDosages.get(i).getApp_material().getUnit_price()));
                }

            }
        }
        return (float) (Math.round(sum * 100)) / 100;
    }

//    private float getWeightFromText(String text) {
//
//        String[] result = text.substring(0, text.length() - 1).split("\n");
//        if (result.length == 1) {
//            return 0;
//        } else {
//            float fl = Float.parseFloat(result[1]);
//            return fl;
//        }
//    }

    private float getWeightFromTab(String text) {
        if (TextUtils.isEmpty(text.substring(0, text.length() - 1))) {
            return 0;
        } else {
            float fl = Float.parseFloat(text.substring(0, text.length() - 1));
            return fl;
        }
    }

}