package com.hzjytech.coffeeme.home;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.hzjytech.coffeeme.BaseActivity;
import com.hzjytech.coffeeme.R;
import com.hzjytech.coffeeme.configurations.Configurations;
import com.hzjytech.coffeeme.configurations.UmengConfig;
import com.hzjytech.coffeeme.entities.DisplayItems;
import com.hzjytech.coffeeme.entities.NewGoods;
import com.hzjytech.coffeeme.http.JijiaHttpSubscriber;
import com.hzjytech.coffeeme.http.SubscriberOnCompletedListener;
import com.hzjytech.coffeeme.http.SubscriberOnErrorListener;
import com.hzjytech.coffeeme.http.SubscriberOnNextListener;
import com.hzjytech.coffeeme.http.api.GoodApi;
import com.hzjytech.coffeeme.utils.CommonUtil;
import com.hzjytech.coffeeme.utils.LogUtil;
import com.hzjytech.coffeeme.utils.NetUtil;
import com.hzjytech.coffeeme.utils.SharedPrefUtil;
import com.hzjytech.coffeeme.utils.ToastUtil;
import com.hzjytech.coffeeme.utils.UserUtils;
import com.hzjytech.coffeeme.widgets.BadgeView;
import com.hzjytech.coffeeme.widgets.MyAddSubView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.umeng.analytics.MobclickAgent;

import java.text.DecimalFormat;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observable;

//购买界面
public class ModulationActivity extends BaseActivity {

    private static final String ESPRESSO = "意式浓缩";
    private static ModulationActivity mInstance;
    @BindView(R.id.ivModulationCart)
    ImageView mIvModulationCart;
    @BindView(R.id.tv_drink_info)
    TextView mTvDrinkInfo;
    @BindView(R.id.tv_drink_name)
    TextView mTvDrinkName;
    @BindView(R.id.iv_cup)
    ImageView iv_cup;
    @BindView(R.id.tabSugar)
    TabLayout mTabSugar;
    @BindView(R.id.add_num_view)
    MyAddSubView mAddNumView;
    @BindView(R.id.tv_old_price)
    TextView mTvOldPrice;
    @BindView(R.id.tv_current_price)
    TextView mTvCurrentPrice;
    @BindView(R.id.tv_choose_sugar)
    TextView mTvChooseSugar;
    @BindView(R.id.ll_choose_sugar)
    LinearLayout mLlChooseSugar;
    @BindView(R.id.v_line)
    View mVLine;
    @BindView(R.id.tv_add_chart)
    TextView mTvAddChart;
    @BindView(R.id.tv_buy_now)
    TextView mTvBuyNow;
    private BadgeView cartBadgeView;
    private DisplayItems.AppItem appItem;
    private JijiaHttpSubscriber mSubscriber;
    private JijiaHttpSubscriber mSubscriber1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modulation);
        ButterKnife.bind(this);
        mInstance = this;
        cartBadgeView = new BadgeView(ModulationActivity.this, mIvModulationCart);
        cartBadgeView.setBadgeBackgroundColor(Color.RED);
        cartBadgeView.setTextColor(Color.WHITE);
        //cartBadgeView.setBadgePosition(BadgeView.POSITION_TOP_RIGHT);
        if (getIntent() != null) {
            appItem = (DisplayItems.AppItem) getIntent().getSerializableExtra(Configurations
                    .APP_ITEM);
            LogUtil.e("appItem", appItem.toString());
            if (appItem != null) {
                InitView();
            }
        } else {
            finish();
        }

    }

    public static ModulationActivity Instance() {
        if (null == mInstance)
            mInstance = new ModulationActivity();
        return mInstance;
    }

    @OnClick({R.id.ivModulationBack, R.id.ivModulationCart, R.id.tv_add_chart, R.id.tv_buy_now})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ivModulationBack:
                finish();
                break;
            case R.id.ivModulationCart:
                onCartClick();
                break;
            case R.id.tv_add_chart:
                onAddChartClick();
                break;
            case R.id.tv_buy_now:
                onBuyClick();
                break;
        }
    }

    /**
     * 加入购物车
     */
    private void onAddChartClick() {
        if (!SharedPrefUtil.getLoginType()
                .equals(SharedPrefUtil.LOGINING)) {

            goLogin();
            return;
        }
        if (!buyEnable(appItem)) {
            return;
        }

        if (!NetUtil.isNetworkAvailable(mInstance))
            return;


        final int number = mAddNumView.getText();
        addCart(number);
    }

    /**
     * 点击购物车图标
     */
    private void onCartClick() {
        if (!SharedPrefUtil.getLoginType()
                .equals(SharedPrefUtil.LOGINING)) {
            hideLoading();
            goLogin();
            return;
        }
        startActivity(new Intent(ModulationActivity.this, NewCartActivity.class));
    }


    /**
     * 初始化界面
     */
    private void InitView() {
        DisplayImageOptions options = new DisplayImageOptions.Builder().cacheInMemory(true)/*缓存至内存*/
                .cacheOnDisk(true)/*缓存值SDcard*/
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();
        String app_image = appItem.getImage_url();
        //  String str = app_image.substring(0, app_image.indexOf("?"));
        ImageLoader.getInstance()
                .displayImage(app_image, iv_cup, options);
        if (appItem.getDescription() != null ) {
            if(appItem.getDescription()
                    .contains("\r\n")){
                mTvDrinkInfo.setText(appItem.getDescription()
                        .replace("\r\n", " "));
            }else {
                mTvDrinkInfo.setText(appItem.getDescription());
            }

        }
        initCart();
        mTvDrinkName.setText(appItem.getName() + " " + (appItem.getNameEn() == null ? "" :
                appItem.getNameEn()));
        mTvOldPrice.getPaint()
                .setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
        DecimalFormat df = new DecimalFormat("0.00");
        mTvOldPrice.setText("¥ " + df.format(appItem.getPrice()));
        mTvCurrentPrice.setText("¥ " + df.format(appItem.getCurrent_price()));
        initSugarTab();

    }

    /**
     * 初始化糖分选择tab
     */
    private void initSugarTab() {
        if(appItem.getName().equals(ESPRESSO)){
            mTvChooseSugar.setVisibility(View.GONE);
            mLlChooseSugar.setVisibility(View.GONE);
            mVLine.setVisibility(View.GONE);
            return;
        }else{
            mTvChooseSugar.setVisibility(View.VISIBLE);
            mLlChooseSugar.setVisibility(View.VISIBLE);
            mVLine.setVisibility(View.VISIBLE);
        }
        int sugar = appItem.getSugar();
        for (int i = 0; i < 4; i++) {
            mTabSugar.addTab(mTabSugar.newTab().setCustomView(getTabView(i)),i,sugar==i);
        }

       mTabSugar.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                //todo 设置糖分
                appItem.setSugar(tab.getPosition());

                for (int i = 0; i < mTabSugar.getTabCount(); i++) {
                    TabLayout.Tab t = mTabSugar.getTabAt(i);
                    if (t != null) {
                        t.getCustomView().setSelected(i == tab.getPosition());
                    }
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }

        });


    }

    public View getTabView(int position) {

        View view = LayoutInflater.from(this)
                .inflate(R.layout.tab_sugar, null);
        TextView tv_sugar = (TextView) view.findViewById(R.id.tvModulationTabName);
        switch (position) {
            case 0:
                tv_sugar.setText(R.string.no_sugar);
                break;
            case 1:
                tv_sugar.setText(R.string.three_points_sweet);
                break;
            case 2:
                tv_sugar.setText(R.string.five_points_sweet);
                break;
            case 3:
                tv_sugar.setText(R.string.seven_points_sweet);
                break;
        }
        return view;
    }

    /**
     * 初始化购物车角标
     */
    private void initCart() {
        showLoading();
        if (!SharedPrefUtil.getLoginType()
                .equals(SharedPrefUtil.LOGINING)) {
            hideLoading();
            return;
        }
        //carts
        Observable<NewGoods> cartObservable = GoodApi.getGoodCartList(this,
                UserUtils.getUserInfo()
                        .getAuth_token(),
                1);
        mSubscriber = JijiaHttpSubscriber.buildSubscriber(this)
                .setOnNextListener(new SubscriberOnNextListener<NewGoods>() {
                    @Override
                    public void onNext(NewGoods carts) {
                        int total = carts.getTotal();
                        cartBadgeView.setTextSize(8);
                        cartBadgeView.setBadgePosition(BadgeView.POSITION_TOP_RIGHT);
                        if (total < 1) {
                            cartBadgeView.hide();

                        } else if (total < 100) {
                            cartBadgeView.setText(String.valueOf(total));
                            cartBadgeView.show();

                        } else {
                            cartBadgeView.setText(R.string.cart_count_max_value);
                            cartBadgeView.show();

                        }

                    }
                })
                .setOnCompletedListener(new SubscriberOnCompletedListener() {
                    @Override
                    public void onCompleted() {
                        hideLoading();
                    }
                })
                .setOnErrorListener(new SubscriberOnErrorListener() {
                    @Override
                    public void onError(Throwable e) {
                        hideLoading();
                    }
                })
                .build();
        cartObservable.subscribe(mSubscriber);
    }

    private void onBuyClick() {

        if (!SharedPrefUtil.getLoginType()
                .equals(SharedPrefUtil.LOGINING)) {
            goLogin();
            return;
        }
        if (!buyEnable(appItem)) {
            return;
        }

        Intent intent = new Intent(ModulationActivity.this, NewPaymentActivity.class);
        intent.putExtra("appItem", appItem);
        intent.putExtra("type", 0);
        if (mAddNumView.getText() < 1) {
            ToastUtil.showShort(ModulationActivity.this, "请选择购买数量");
        } else {
            intent.putExtra("count", mAddNumView.getText());
            startActivity(intent);
        }
    }


    private void addCart(final int number) {
        showLoading();
        String item = new Gson().toJson(appItem);
        Observable<Object> observable = GoodApi.addCart(this,
                UserUtils.getUserInfo()
                        .getAuth_token(),
                number,
                item);
        mSubscriber1 = JijiaHttpSubscriber.buildSubscriber(this)
                .setOnNextListener(new SubscriberOnNextListener() {
                    @Override
                    public void onNext(Object o) {
                        ToastUtil.showShort(ModulationActivity.this,
                                getString(R.string.add_cart_success));
                        initCart();
                    }
                })
                .setOnCompletedListener(new SubscriberOnCompletedListener() {
                    @Override
                    public void onCompleted() {
                        hideLoading();
                    }
                })
                .setOnErrorListener(new SubscriberOnErrorListener() {
                    @Override
                    public void onError(Throwable e) {
                        hideLoading();
                    }
                })
                .build();
        observable.subscribe(mSubscriber1);

       /* String auth_token = UserUtils.getUserInfo()
                .getAuth_token();
        int item_id = appItem.getId();

        JSONArray array = new JSONArray();
        try {
            for (int i = 0; i < appDosages.size(); i++) {
                JSONObject object = new JSONObject();
                object.put("id",
                        appDosages.get(i)
                                .getId());
                object.put("weight",
                        getWeightFromTab(new TabViewHolder(tabModulationDosage.getTabAt(i)
                                .getCustomView()).tvModulationTabWeight.getText()
                                .toString()
                                .trim()));
                //                object.put("weight", getWeightFromText(tabModulationDosage
                // .getTabAt(i).getText().toString().trim()));
                object.put("water",
                        appDosages.get(i)
                                .getWater());
                array.put(object);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        String custom_dosages = array.toString();
        float price = Float.parseFloat(tvModulationPrice.getText()
                .toString());

        RequestParams entity = new RequestParams(Configurations.URL_GOODS);
        entity.addParameter(Configurations.AUTH_TOKEN, auth_token);
        entity.addParameter(Configurations.APP_ITEM_ID, item_id);
        entity.addParameter("custom_dosages", custom_dosages);
        entity.addParameter(Configurations.PRICE, price);
        entity.addParameter(Configurations.NUMBER, number);

        String device_id = JPushInterface.getRegistrationID(ModulationActivity.this);
        String timeStamp = TimeUtil.getCurrentTimeString();
        entity.addParameter(Configurations.TIMESTAMP, timeStamp);
        entity.addParameter(Configurations.DEVICE_ID, device_id);

        Map<String, String> map = new TreeMap<String, String>();
        map.put(Configurations.AUTH_TOKEN, auth_token);
        map.put(Configurations.APP_ITEM_ID, String.valueOf(item_id));
        map.put("custom_dosages", custom_dosages);
        map.put(Configurations.PRICE, String.valueOf(price));
        map.put(Configurations.NUMBER, String.valueOf(number));
        entity.addParameter(Configurations.SIGN,
                SignUtils.createSignString(device_id, timeStamp, map));


        x.http()
                .post(entity, new Callback.CommonCallback<JSONObject>() {

                    @Override
                    public void onSuccess(JSONObject result) {

                        checkResOld(result);
                        try {

                            if (result.getInt(Configurations.STATUSCODE) == 200) {

                                btnModulationAddCart.setClickable(true);
                                MobclickAgent.onEvent(ModulationActivity.this,
                                        UmengConfig.EVENT_ADD_CART);
                                String cartBadgeCount;

                                if (TextUtils.isEmpty(cartBadgeView.getText()
                                        .toString())) {
                                    cartBadgeView.setText("" + number);
                                } else {
                                    if (cartBadgeView.getText()
                                            .toString()
                                            .contains("+")) {
                                        cartBadgeCount = cartBadgeView.getText()
                                                .toString()
                                                .substring(0,
                                                        cartBadgeView.getText()
                                                                .toString()
                                                                .length() - 1);
                                    } else {
                                        cartBadgeCount = cartBadgeView.getText()
                                                .toString();
                                    }

                                    if (Integer.parseInt(cartBadgeCount) + number < 100) {

                                        cartBadgeView.setText(String.valueOf(Integer.parseInt(
                                                cartBadgeCount) + number));
                                    } else {

                                        cartBadgeView.setText(getString(R.string
                                                .cart_count_max_value));
                                    }
                                }
                                cartBadgeView.show();
                            } else {
                                ToastUtil.showShort(ModulationActivity.this,
                                        result.getString(Configurations.STATUSMSG));
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
                });*/
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(UmengConfig.MODULATIONACTIVITY);
        MobclickAgent.onResume(this);

    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(UmengConfig.MODULATIONACTIVITY);
        MobclickAgent.onPause(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        CommonUtil.unSubscribeSubs(mSubscriber, mSubscriber1);
    }
}