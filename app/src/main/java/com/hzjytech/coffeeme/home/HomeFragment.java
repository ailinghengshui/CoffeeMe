package com.hzjytech.coffeeme.home;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hzjytech.banner.SmartFragmentStatePagerAdapter;
import com.hzjytech.coffeeme.Dialogs.HintDialog;
import com.hzjytech.coffeeme.Dialogs.ITwoButtonClick;
import com.hzjytech.coffeeme.Dialogs.NewBenefitDialog;
import com.hzjytech.coffeeme.Dialogs.NewOutTimeCouponDialog;
import com.hzjytech.coffeeme.R;
import com.hzjytech.coffeeme.baidumap.BaiduLocationService;
import com.hzjytech.coffeeme.configurations.Configurations;
import com.hzjytech.coffeeme.configurations.UmengConfig;
import com.hzjytech.coffeeme.entities.Banner;
import com.hzjytech.coffeeme.entities.Banners;
import com.hzjytech.coffeeme.entities.Coupon;
import com.hzjytech.coffeeme.entities.DisplayItems;
import com.hzjytech.coffeeme.entities.Machine;
import com.hzjytech.coffeeme.entities.NewGoods;
import com.hzjytech.coffeeme.entities.NewOrders;
import com.hzjytech.coffeeme.fragments.BaseFragment;
import com.hzjytech.coffeeme.http.JijiaHttpResultZip;
import com.hzjytech.coffeeme.http.JijiaHttpSubscriber;
import com.hzjytech.coffeeme.http.JijiaRZField;
import com.hzjytech.coffeeme.http.SubscriberOnCompletedListener;
import com.hzjytech.coffeeme.http.SubscriberOnErrorListener;
import com.hzjytech.coffeeme.http.SubscriberOnNextListener;
import com.hzjytech.coffeeme.http.api.AppItemApi;
import com.hzjytech.coffeeme.http.api.GoodApi;
import com.hzjytech.coffeeme.http.api.OrderApi;
import com.hzjytech.coffeeme.me.MyCouponActivity;
import com.hzjytech.coffeeme.order.AbleTakeActivity;
import com.hzjytech.coffeeme.utils.AppUtil;
import com.hzjytech.coffeeme.utils.CameraUtil;
import com.hzjytech.coffeeme.utils.CommonUtil;
import com.hzjytech.coffeeme.utils.DateTimeUtil;
import com.hzjytech.coffeeme.utils.DensityUtil;
import com.hzjytech.coffeeme.utils.LogUtil;
import com.hzjytech.coffeeme.utils.MyApplication;
import com.hzjytech.coffeeme.utils.ScreenUtil;
import com.hzjytech.coffeeme.utils.SharedPrefUtil;
import com.hzjytech.coffeeme.utils.SignUtils;
import com.hzjytech.coffeeme.utils.TimeUtil;
import com.hzjytech.coffeeme.utils.ToastUtil;
import com.hzjytech.coffeeme.utils.UserUtils;
import com.hzjytech.coffeeme.widgets.BadgeView;
import com.hzjytech.coffeeme.widgets.FlyBanner;
import com.hzjytech.coffeeme.widgets.TransparentToolBar;
import com.hzjytech.scan.activity.CaptureActivity;
import com.umeng.analytics.MobclickAgent;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.jpush.android.api.JPushInterface;
import in.srain.cube.views.ptr.PtrClassicFrameLayout;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;
import ru.noties.scrollable.CanScrollVerticallyDelegate;
import ru.noties.scrollable.OnFlingOverListener;
import ru.noties.scrollable.OnScrollChangedListener;
import ru.noties.scrollable.ScrollableLayout;
import rx.Observable;
import rx.functions.Func2;
import rx.functions.Func4;

/**
 * Created by hehongcan on 2017/10/17.
 */
public class HomeFragment extends BaseFragment {


    private static final int REQUEST_CODE_FETCH = 2045;
    private static final String HOT = "hot";
    private static final String ICE = "ice";
    private static final String PACKAGE = "package";
    @BindView(R.id.homeBanner)
    FlyBanner mHomeBanner;
    @BindView(R.id.tvLocationnearbyName)
    TextView mTvLocationnearbyName;
    @BindView(R.id.tvLocationnearbyDist)
    TextView mTvLocationnearbyDist;
    @BindView(R.id.tvLocationnearbyDistUnit)
    TextView mTvLocationnearbyDistUnit;
    @BindView(R.id.tblayoutHomefrgTab)
    TabLayout mTblayoutHomefrgTab;
    @BindView(R.id.vPgHomefrgShow)
    ViewPager mVPgHomefrgShow;
    @BindView(R.id.scrollable_layout)
    ScrollableLayout mScrollableLayout;
    @BindView(R.id.store_house_ptr_frame)
    PtrClassicFrameLayout mPtrFrame;
    @BindView(R.id.ibScan)
    ImageView mIbScan;
    @BindView(R.id.tvHomefrgTitle)
    TextView mTvHomefrgTitle;
    @BindView(R.id.ivHomefrgCart)
    ImageView mIvHomefrgCart;
    @BindView(R.id.rl_title_coffee)
    RelativeLayout mRlTitleCoffee;
    @BindView(R.id.tbHomefrgTitle)
    TransparentToolBar tbHomefrgTitle;
    @BindView(R.id.ll_home_location)
    LinearLayout mLlHomeLocation;
    @BindView(R.id.rl_home_drink_tab)
    RelativeLayout mRlHomeDrinkTab;
    private static final String TAG = HomeFragment.class.getSimpleName();

    private List<Banner> banners = new ArrayList<Banner>();
    private List<String> initUrlString = new ArrayList<String>();

    private BadgeView cartBadgeView;
    private BadgeView scanBadgeView;
    private double Latitude;
    private double Longitude;

    private List<Machine> machines = new ArrayList<Machine>();
    private BaiduLocationService baiduLocationService;
    private static final String LOCATION_EMPTY = "附近没有可用的咖啡机...";
    private static final float DISTANCE_EMPTY = 0;
    private static final String LOCATION_LOADING = "正在定位中...";
    private static final float DISTANCE_LOADING = 1;
    private static final int MY_PERMISSIONS_REQUEST_CALL_CAMERA = 123;//请求码，自己定义
    public static final String ACTION = "android.intent.action.CART_BROADCAST";
    private LocalBroadcastManager broadcastManager;
    private BroadcastReceiver ableTakeReceiver;
    private boolean isFirst = true;
    private List<String> urlList;
    private int mPage;
    private MyApplication application;
    private String device_id;
    private boolean homeFragmentVisible = true;
    private DecimalFormat df = new DecimalFormat("0.0");
    private DrinkFragmentAdapter mAdapter;
    private int mStatusHeight;
    private int mToolBarHeight;
    private DrinkItemFragment mHotfra;
    private DrinkItemFragment mIcefra;
    private String mDevice_id;
    private int mAppId;
    private JijiaHttpSubscriber mSubscriber;
    private JijiaHttpSubscriber mSubscriber1;
    private JijiaHttpSubscriber mSubscriber2;
    private PackageItemFragment mPackfra;
    private BadgeView hotBadge;
    private BadgeView iceBadge;
    private BadgeView packageBadge;

    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // TODO: inflate a fragment view
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();
        //检查优惠券是否快过期，并弹出弹窗,如果弹出过弹窗，则无需检验
        application = (MyApplication) getActivity().getApplication();
        if (application.getHasShowDialog()) {
            // application.setHasShowDialog(false);
        } else {
            checkCouponsIsOutOfTime();
        }


    }

    private void checkCouponsIsOutOfTime() {
        loadCoupons();
    }


    private void loadCoupons() {

        RequestParams entity = new RequestParams(Configurations.URL_COUPONS);
        if (UserUtils.getUserInfo() == null || UserUtils.getUserInfo()
                .getAuth_token() == null) {
            return;
        }
        String auth_token = UserUtils.getUserInfo()
                .getAuth_token();
        entity.addParameter(Configurations.TOKEN,
                UserUtils.getUserInfo()
                        .getAuth_token());
        //entity.addParameter(Configurations.AVAILABLE, true);
        String device_id = JPushInterface.getRegistrationID(getActivity());
        String timeStamp = TimeUtil.getCurrentTimeString();
        entity.addParameter(Configurations.TIMESTAMP, timeStamp);
        entity.addParameter(Configurations.DEVICE_ID, device_id);

        Map<String, String> map = new TreeMap<String, String>();
        map.put(Configurations.TOKEN,
                UserUtils.getUserInfo()
                        .getAuth_token());
        //map.put(Configurations.AVAILABLE, String.valueOf(true));
        entity.addParameter(Configurations.SIGN,
                SignUtils.createSignString(device_id, timeStamp, map));

        x.http()
                .get(entity, new Callback.CommonCallback<String>() {
                    @Override
                    public void onSuccess(String result) {
                        LogUtil.e("result", result);
                        parseCouponResult(result);

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

    private void parseCouponResult(String result) {
        try {
            JSONObject object = new JSONObject(result);
            checkResOld(object);
            if (object.getInt(Configurations.STATUSCODE) == 200) {
                List<Coupon> coupons = new Gson().fromJson(object.getJSONObject("results")
                        .getString("coupons"), new TypeToken<ArrayList<Coupon>>() {}.getType());
                long nowday = SharedPrefUtil.getLong("nowday");
                for (int i = 0; i < coupons.size(); i++) {
                    if (coupons.get(i)
                            .getEnd_date() == null || coupons.get(i)
                            .getEnd_date()
                            .equals("")) {
                        continue;
                    }
                    LogUtil.d("coupon" + i,
                            coupons.get(i)
                                    .getEnd_date() + "");
                    DateTime endDate = DateTime.parse(coupons.get(i)
                                    .getEnd_date(),
                            DateTimeFormat.forPattern(DateTimeUtil.DATE_FORMAT_LONG));
                    long diff = endDate.getMillis() - Calendar.getInstance()
                            .getTimeInMillis();
                    long difDays = (diff / (3600 * 24 * 1000)) + 1;
                    long olddif = endDate.getMillis() - nowday;
                    long olddays = (olddif / (3600 * 24 * 1000)) + 1;
                    LogUtil.e("days", difDays + "+++++" + olddays);
                    if (difDays > 0 && difDays <= 3 && olddays > 3) {
                        //根据时间判断，如果已经弹出过则弹出时间3天内的不再弹出
                        showOutOfTimeCoupouDialog();
                        SharedPrefUtil.putLong("nowday",
                                Calendar.getInstance()
                                        .getTimeInMillis());
                        break;
                    }
                }
                long newestCoupenTime = 0;
                Coupon newestCoupon = null;
                for (Coupon coupon : coupons) {
                    DateTime endDate = DateTime.parse(coupon.getStart_date(),
                            DateTimeFormat.forPattern(DateTimeUtil.DATE_FORMAT_LONG));
                    long timeInMillis = endDate.getMillis();
                    if (newestCoupenTime < timeInMillis) {
                        newestCoupenTime = timeInMillis;
                        newestCoupon = coupon;
                    }
                }
                long recentCouponTime = SharedPrefUtil.getLong("recentCouponTime");
                if (newestCoupenTime > recentCouponTime) {
                    SharedPrefUtil.putLong("recentCouponTime", newestCoupenTime);
                    String title = newestCoupon.getTitle();
                    final NewBenefitDialog newBenefitDialog = NewBenefitDialog.newInstance(title);
                    final Coupon finalNewestCoupon = newestCoupon;
                    newBenefitDialog.setOnTwoButtonClick(new ITwoButtonClick() {
                        @Override
                        public void onLeftButtonClick() {
                            newBenefitDialog.dismiss();
                            application.setHasShowDialog(false);
                        }

                        @Override
                        public void onRightButtonClick() {
                            Intent intent = new Intent(getActivity(), MyCouponActivity.class);
                            if (finalNewestCoupon.getCoupon_type() == 4) {
                                intent.putExtra("type", "redeem");
                            } else {
                                intent.putExtra("type", "coupon");
                            }

                            startActivity(intent);
                            newBenefitDialog.dismiss();
                            application.setHasShowDialog(false);
                        }
                    });
                    application.setHasShowDialog(true);
                    newBenefitDialog.show(getFragmentManager(), "outTimeDialog");
                } else {
                }

            } else {
                ToastUtil.showShort(getActivity(), object.getString(Configurations.STATUSMSG));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    //弹出优惠券过期提示弹窗
    private void showOutOfTimeCoupouDialog() {
        final NewOutTimeCouponDialog newOutTimeCouponDialog = NewOutTimeCouponDialog.newInstance();
        newOutTimeCouponDialog.setOnTwoButtonClick(new ITwoButtonClick() {
            @Override
            public void onLeftButtonClick() {
                newOutTimeCouponDialog.dismiss();
                application.setHasShowDialog(false);
            }

            @Override
            public void onRightButtonClick() {
                Intent intent = new Intent(getActivity(), MyCouponActivity.class);
                startActivity(intent);
                newOutTimeCouponDialog.dismiss();
                application.setHasShowDialog(false);
            }
        });
        application.setHasShowDialog(true);
        newOutTimeCouponDialog.show(getFragmentManager(), "outTimeDialog");
    }

    //扫一扫
    @OnClick(R.id.ibScan)
    public void startScan(View v) {
        Intent intent = new Intent(getContext(), CaptureActivity.class);
        intent.putExtra("isFromHome", true);
        //检查权限
        if (!CameraUtil.isCameraCanUse()) {
            //如果没有授权，则请求授权
            HintDialog hintDialog = HintDialog.newInstance("提示", "无法获取摄像头数据，请检查是否已经打开摄像头权限。", "确定");
            hintDialog.show(getFragmentManager(), "cameraHint");
            //ToastUtil.showShort(getActivity(),"无法获取摄像头权限，请确认是否开启。");
            //ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission
            // .CAMERA}, MY_PERMISSIONS_REQUEST_CALL_CAMERA);
        } else {
            //有授权，直接开启摄像头
            startActivityForResult(intent, REQUEST_CODE_FETCH);
        }

    }

    private void initView() {
        mAppId = AppUtil.getVersionCode(getActivity());
        mDevice_id = JPushInterface.getRegistrationID(getActivity());
        //设置transparentToolBar高度
        mStatusHeight = ScreenUtil.getStatusHeight(getContext());
        int titlebar = (int) getResources().getDimension(R.dimen.title_bar_height);
        mToolBarHeight = mStatusHeight + titlebar;
        ViewGroup.LayoutParams layoutParams = tbHomefrgTitle.getLayoutParams();
        layoutParams.height = mToolBarHeight;
        tbHomefrgTitle.setLayoutParams(layoutParams);
        //设置relativeLayout位置
        RelativeLayout.LayoutParams layoutParams1 = (RelativeLayout.LayoutParams) mRlTitleCoffee
                .getLayoutParams();
        layoutParams1.height = titlebar;
        layoutParams1.setMargins(0, mStatusHeight, 0, 0);
        mRlTitleCoffee.setLayoutParams(layoutParams1);
        cartBadgeView = new BadgeView(getContext(), mIvHomefrgCart);
        scanBadgeView = new BadgeView(getContext(), mIbScan);
        //设置透明标题栏参数
        tbHomefrgTitle.setBgColor(getResources().getColor(R.color.coffee));
        tbHomefrgTitle.setOffset((float) mToolBarHeight);
        mTvHomefrgTitle.setAlpha(0);
        //设置下拉刷新参数
        //下拉刷新支持时间
        mPtrFrame.setLastUpdateTimeRelateObject(this);
        //下拉刷新一些设置
        mPtrFrame.setResistance(1.7f);
        mPtrFrame.setRatioOfHeaderHeightToRefresh(1.2f);
        mPtrFrame.setDurationToClose(200);
        mPtrFrame.setDurationToCloseHeader(1000);
        // default is false
        mPtrFrame.setPullToRefresh(false);
        // default is true
        mPtrFrame.setKeepHeaderWhenRefresh(true);
        //解决滑动冲突
        mPtrFrame.disableWhenHorizontalMove(true);
        //下拉刷新
        mPtrFrame.setPtrHandler(new PtrDefaultHandler() {
            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                initData();
                startBaiduMap();

            }
        });
        mIvHomefrgCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!SharedPrefUtil.getLoginType()
                        .equals(SharedPrefUtil.LOGINING)) {

                    goLogin();
                    return;
                }
                startActivity(new Intent(getActivity(), NewCartActivity.class));

            }
        });
        showLoading();
        initData();
        startBaiduMap();
        initViewPager();
        initScrollView();
    }

    private void initData() {
        String timeStamp = TimeUtil.getCurrentTimeString();
        Map<String, String> map = new TreeMap<>();
        map.put(Configurations.APP_ID, String.valueOf(AppUtil.getVersionCode(getActivity())));
        //bannner
        Observable<Banners> bannersObservable = AppItemApi.getBanners(getActivity(), mAppId);
        //items
        Observable<DisplayItems> itemsObservable = AppItemApi.getAppItems(getActivity());

        if (!SharedPrefUtil.getLoginType()
                .equals(SharedPrefUtil.LOGINING)) {
            cartBadgeView.hide();
            scanBadgeView.hide();
            Observable<ResultZip2> zipObservable = Observable.zip(bannersObservable,
                    itemsObservable,
                    new Func2<Banners, DisplayItems, ResultZip2>() {

                        @Override
                        public ResultZip2 call(Banners banners, DisplayItems displayItems) {
                            return new ResultZip2(banners, displayItems);
                        }
                    });
            mSubscriber = JijiaHttpSubscriber.buildSubscriber(getActivity())
                    .setOnNextListener(new SubscriberOnNextListener<ResultZip2>() {
                        @Override
                        public void onNext(ResultZip2 zip) {
                            if (zip != null) {
                                DisplayItems appItems = zip.appItems;
                                setAppItems(appItems);
                                Banners banners = zip.banners;
                                setBanners(banners);
                            }

                        }
                    })
                    .setOnErrorListener(new SubscriberOnErrorListener() {
                        @Override
                        public void onError(Throwable e) {
                            mPtrFrame.refreshComplete();
                            hideLoading();
                        }
                    })
                    .setOnCompletedListener(new SubscriberOnCompletedListener() {
                        @Override
                        public void onCompleted() {
                            mPtrFrame.refreshComplete();
                            hideLoading();
                        }
                    })
                    .build();
            zipObservable.subscribe(mSubscriber);
        } else {
            Observable<NewGoods> cartObservable = GoodApi.getGoodCartList(getActivity(),
                    UserUtils.getUserInfo()
                            .getAuth_token(),
                    1);
            Observable<NewOrders> orderListObservable = OrderApi.getOrderList(getActivity(),
                    UserUtils.getUserInfo()
                            .getAuth_token(),
                    "able_take",
                    1);
            Observable<ResultZip> zipObservable = Observable.zip(bannersObservable,
                    itemsObservable,
                    cartObservable,
                    orderListObservable,
                    new Func4<Banners, DisplayItems, NewGoods, NewOrders, ResultZip>() {
                        @Override
                        public ResultZip call(
                                Banners banners,
                                DisplayItems displayItems,
                                NewGoods goodList,
                                NewOrders orders) {
                            return new ResultZip(banners, displayItems, goodList, orders);
                        }
                    });
            //getAbleTakeOrders();
            mSubscriber1 = JijiaHttpSubscriber.buildSubscriber(getActivity())
                    .setOnNextListener(new SubscriberOnNextListener<ResultZip>() {
                        @Override
                        public void onNext(ResultZip zip) {
                            if (zip != null) {
                                DisplayItems appItems = zip.appItems;
                                setAppItems(appItems);
                                Banners banners = zip.banners;
                                setBanners(banners);
                                if (zip.goodList != null) {
                                    NewGoods goodList = zip.goodList;
                                    setCatNumber(goodList.getTotal());
                                }
                                if (zip.orders != null) {
                                    NewOrders orders = zip.orders;
                                    setScanNumber(orders.getAble_take_count());
                                }
                            }

                        }
                    })
                    .setOnErrorListener(new SubscriberOnErrorListener() {
                        @Override
                        public void onError(Throwable e) {
                            mPtrFrame.refreshComplete();
                            hideLoading();
                        }
                    })
                    .setOnCompletedListener(new SubscriberOnCompletedListener() {
                        @Override
                        public void onCompleted() {
                            mPtrFrame.refreshComplete();
                            hideLoading();
                        }
                    })
                    .build();
            zipObservable.subscribe(mSubscriber1);
        }

    }

    private void setScanNumber(int count) {
        scanBadgeView.setBadgePosition(BadgeView.POSITION_TOP_RIGHT);
        if (count < 1) {
            scanBadgeView.hide();

        } else if (count < 10) {
            scanBadgeView.setTextSize(9);
            scanBadgeView.setText(String.valueOf(count));
            scanBadgeView.show();

        } else if (count < 100) {
            scanBadgeView.setTextSize(8);
            scanBadgeView.setText(String.valueOf(count));
            scanBadgeView.show();
        } else {
            scanBadgeView.setTextSize(8);
            scanBadgeView.setText(R.string.cart_count_max_value);
            scanBadgeView.show();
        }

    }

    private void setCatNumber(int count) {
        cartBadgeView.setBadgePosition(BadgeView.POSITION_TOP_RIGHT);
        if (count < 1) {
            cartBadgeView.hide();

        } else if (count < 10) {
            cartBadgeView.setTextSize(9);
            cartBadgeView.setText(String.valueOf(count));
            cartBadgeView.show();

        } else if (count < 100) {
            cartBadgeView.setTextSize(8);
            cartBadgeView.setText(String.valueOf(count));
            cartBadgeView.show();
        } else {
            cartBadgeView.setTextSize(8);
            cartBadgeView.setText(R.string.cart_count_max_value);
            cartBadgeView.show();
        }

    }

    private void setBanners(Banners banners) {
        HomeFragment.this.banners = banners.getBanners();
        urlList = new ArrayList<String>();
        urlList.clear();
        for (Banner banner : HomeFragment.this.banners) {
            urlList.add(banner.getImage_url());
        }

        if (isFirst || (initUrlString != null && !initUrlString.equals(urlList))) {
            initUrlString.clear();
            initUrlString.addAll(urlList);
            isFirst = false;
            mHomeBanner.setImagesUrl(HomeFragment.this.banners);
            mHomeBanner.setOnItemClickListener(new FlyBanner.OnItemClickListener() {
                @Override
                public void onItemClick(int position) {
                    MobclickAgent.onEvent(getContext(), UmengConfig.EVENT_BANNER_CLICK);
                    Intent intent = new Intent(getContext(), BannerDetailActivity.class);
                    intent.putExtra("url_article",
                            HomeFragment.this.banners.get(position)
                                    .getArticle_url());
                    getContext().startActivity(intent);
                }
            });
        }
    }

    private void setAppItems(DisplayItems appItems) {
        mHotfra.setData(appItems.getHot_items());
        mIcefra.setData(appItems.getIce_items());
        mPackfra.setData(appItems.getPackages());
        checkHasNewItem(appItems.getHot_items(), appItems.getIce_items(), appItems.getPackages());
    }

    /**
     * 检测是否有新的饮品
     * 还要考虑下架再上架饮品的情况
     *
     * @param hot_items
     * @param ice_items
     * @param packages
     */
    private void checkHasNewItem(
            List<DisplayItems.AppItem> hot_items,
            List<DisplayItems.AppItem> ice_items,
            List<DisplayItems.Packages> packages) {
        ArrayList<DisplayItems.AppItem> preHotItems = SharedPrefUtil.getItemInfo(HOT,
                DisplayItems.AppItem.class);

        if (preHotItems == null && hot_items != null) {
            SharedPrefUtil.saveItemInfo(HOT, hot_items);
        } else {
            if (hot_items != null) {
                for (DisplayItems.AppItem hot_item : hot_items) {
                    boolean isHasNewHot=false;
                    for (DisplayItems.AppItem preHotItem : preHotItems) {
                        if(preHotItem.getId()==hot_item.getId()){
                            isHasNewHot=true;
                            break;
                        }
                    }
                    if(!isHasNewHot){
                        showNewItemBadge(HOT);
                        break;
                    }
                }
                SharedPrefUtil.saveItemInfo(HOT, hot_items);
            }
        }
        ArrayList<DisplayItems.AppItem> preIceItems = SharedPrefUtil.getItemInfo(ICE,
                DisplayItems.AppItem.class);
        if (preIceItems == null && ice_items != null) {
            SharedPrefUtil.saveItemInfo(ICE, hot_items);
        } else {
            if (ice_items != null) {
                for (DisplayItems.AppItem ice_item : ice_items) {
                    boolean isHasNewIce=false;
                    for (DisplayItems.AppItem preIceItem : preIceItems) {
                        if(preIceItem.getId()==ice_item.getId()){
                            isHasNewIce=true;
                            break;
                        }
                    }
                    if(!isHasNewIce){
                        showNewItemBadge(ICE);
                        break;
                    }

                }
                SharedPrefUtil.saveItemInfo(ICE, ice_items);
            }
        }
        ArrayList<DisplayItems.Packages> prepackageItems = SharedPrefUtil.getItemInfo(PACKAGE,
                DisplayItems.Packages.class);
        if (prepackageItems == null && packages != null) {
            SharedPrefUtil.saveItemInfo(PACKAGE, packages);
        } else {
            if (packages != null) {
                for (DisplayItems.Packages pack : packages) {
                    boolean isHasNewPackage=false;
                    for (DisplayItems.Packages prepackageItem : prepackageItems) {
                        if(prepackageItem.getId()==pack.getId()){
                            isHasNewPackage=true;
                            break;
                        }
                    }
                    if (!isHasNewPackage) {
                        showNewItemBadge(PACKAGE);
                        break;
                    }
                }
                SharedPrefUtil.saveItemInfo(PACKAGE, packages);
            }
        }

    }

    private void showNewItemBadge(String ice) {
        if (hotBadge == null) {
            hotBadge = new BadgeView(getContext(), mTblayoutHomefrgTab);
            hotBadge.setBadgePosition(BadgeView.POSITION_TOP_RIGHT);
            hotBadge.setBadgeMargin(DensityUtil.dp2px(getActivity(), 188),
                    DensityUtil.dp2px(getActivity(), 5));
            hotBadge.setText(R.string.new_item);
            hotBadge.setTextSize(TypedValue.COMPLEX_UNIT_SP, 9);
        }
        if (iceBadge == null) {
            iceBadge = new BadgeView(getContext(), mTblayoutHomefrgTab);
            iceBadge.setBadgePosition(BadgeView.POSITION_TOP_RIGHT);
            iceBadge.setBadgeMargin(DensityUtil.dp2px(getActivity(), 98),
                    DensityUtil.dp2px(getActivity(), 5));
            iceBadge.setText(R.string.new_item);
            iceBadge.setTextSize(TypedValue.COMPLEX_UNIT_SP, 9);
        }
        if (packageBadge == null) {
            packageBadge = new BadgeView(getContext(), mTblayoutHomefrgTab);
            packageBadge.setBadgePosition(BadgeView.POSITION_TOP_RIGHT);
            packageBadge.setBadgeMargin(13, DensityUtil.dp2px(getActivity(), 5));
            packageBadge.setText(R.string.new_item);
            packageBadge.setTextSize(TypedValue.COMPLEX_UNIT_SP, 9);
        }
        switch (ice) {
            case HOT:
                hotBadge.show();
                break;
            case ICE:
                iceBadge.show();
                break;
            case PACKAGE:
                packageBadge.show();
                break;
            default:
                break;
        }
    }

    private void hideNewItemBadge(int position) {
        switch (position) {
            case 0:
                if (hotBadge != null) {
                    hotBadge.hide();
                }
                break;
            case 1:
                if (iceBadge != null) {
                    iceBadge.hide();
                }
                break;
            case 2:
                if (packageBadge != null) {
                    packageBadge.hide();
                }
                break;
        }


    }

    private class ResultZip extends JijiaHttpResultZip {
        @JijiaRZField
        Banners banners;
        @JijiaRZField
        DisplayItems appItems;
        @JijiaRZField
        NewGoods goodList;
        @JijiaRZField
        NewOrders orders;


        public ResultZip(
                Banners banners, DisplayItems appItems, NewGoods goodList, NewOrders orders) {
            this.banners = banners;
            this.appItems = appItems;
            this.goodList = goodList;
            this.orders = orders;
        }
    }

    private class ResultZip2 extends JijiaHttpResultZip {
        @JijiaRZField
        Banners banners;
        @JijiaRZField
        DisplayItems appItems;

        public ResultZip2(
                Banners banners, DisplayItems appItems) {
            this.banners = banners;
            this.appItems = appItems;
        }
    }

    private class ResultZip3 extends JijiaHttpResultZip {
        @JijiaRZField
        NewGoods goodList;
        @JijiaRZField
        NewOrders orders;


        public ResultZip3(
                NewGoods goodList, NewOrders orders) {
            this.goodList = goodList;
            this.orders = orders;
        }
    }

    private void initScrollView() {
        mScrollableLayout.setDraggableView(mRlHomeDrinkTab);
        //banners+地址的高度
        int headHight = DensityUtil.dp2px(getActivity(), 270);
        //理论可滑动最大高度
        final int maxScrollY = headHight - mToolBarHeight;
        mScrollableLayout.setMaxScrollY(maxScrollY);
        mScrollableLayout.setCanScrollVerticallyDelegate(new CanScrollVerticallyDelegate() {
            @Override
            public boolean canScrollVertically(int direction) {
                if(mVPgHomefrgShow.getCurrentItem()==2){
                    PackageItemFragment item = (PackageItemFragment) mAdapter.getItem(mVPgHomefrgShow
                            .getCurrentItem());
                    return item != null && item.canScrollVertically(direction);
                }else{
                    DrinkItemFragment item = (DrinkItemFragment) mAdapter.getItem(mVPgHomefrgShow
                            .getCurrentItem());
                    return item != null && item.canScrollVertically(direction);
                }

            }
        });
        mScrollableLayout.setOnFlingOverListener(new OnFlingOverListener() {
            @Override
            public void onFlingOver(int y, long duration) {
                if(mVPgHomefrgShow.getCurrentItem()==2){
                    PackageItemFragment fragment = (PackageItemFragment) mAdapter.getItem(mVPgHomefrgShow
                            .getCurrentItem());
                    if (fragment != null) {
                        fragment.onFlingOver(y, duration);
                    }
                }else{
                    DrinkItemFragment fragment = (DrinkItemFragment) mAdapter.getItem(mVPgHomefrgShow
                            .getCurrentItem());
                    if (fragment != null) {
                        fragment.onFlingOver(y, duration);
                    }
                }

            }
        });
        mScrollableLayout.addOnScrollChangedListener(new OnScrollChangedListener() {


            @Override
            public void onScrollChanged(int y, int oldY, int maxY) {

                //滚动后上端距离，为minHeight时状态栏显示，透明度为1，dy为0时，透明度为0

                int top = mTblayoutHomefrgTab.getTop();
                tbHomefrgTitle.setChangeTop((float) (y * 1.0));

                //设置回调，改变字体透明度
                tbHomefrgTitle.setOnScrollStateListener(new TransparentToolBar
                        .OnScrollStateListener() {
                    @Override
                    public void updateFraction(float fraction) {
                        mTvHomefrgTitle.setAlpha(fraction);
                    }
                });
            }
        });
    }

    private void initViewPager() {
        mHotfra = new DrinkItemFragment();
        mIcefra = new DrinkItemFragment();
        mPackfra = new PackageItemFragment();


        List<Fragment> list_fragment = new ArrayList<>();
        list_fragment.add(mHotfra);
        list_fragment.add(mIcefra);
        list_fragment.add(mPackfra);

        List<String> list_title = new ArrayList<>();
        list_title.add("热饮");
        list_title.add("冷饮");
        list_title.add("套餐");
        mTblayoutHomefrgTab.setTabMode(TabLayout.MODE_FIXED);

        mTblayoutHomefrgTab.addTab(mTblayoutHomefrgTab.newTab()
                .setText(list_title.get(0)));
        mTblayoutHomefrgTab.addTab(mTblayoutHomefrgTab.newTab()
                .setText(list_title.get(1)));
        mTblayoutHomefrgTab.addTab(mTblayoutHomefrgTab.newTab()
                .setText(list_title.get(2)));

        mAdapter = new DrinkFragmentAdapter(getChildFragmentManager(), list_fragment, list_title);
        mVPgHomefrgShow.setAdapter(mAdapter);
        mVPgHomefrgShow.setOffscreenPageLimit(2);
        mTblayoutHomefrgTab.setupWithViewPager(mVPgHomefrgShow);
        mVPgHomefrgShow.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(
                    int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                hideNewItemBadge(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }

    private void initBaiduLocationConfig() {
        baiduLocationService = ((MyApplication) (getActivity().getApplication()))
                .baiduLocationService;
        baiduLocationService.registerListener(bdLocationListener);
        baiduLocationService.setLocationOption(baiduLocationService
                .getDefaultLocationClientOption());
    }

    private void startBaiduMap() {
        initBaiduLocationConfig();
        baiduLocationService.start();
    }


    private void getNearby(double longitude, double latitude) {
        if (getActivity() == null) {
            return;
        }
        RequestParams entity = new RequestParams(Configurations.URL_VENDING_MACHINES);
        entity.addParameter(Configurations.LATITUDE, latitude);
        entity.addParameter(Configurations.LONGITUDE, longitude);
        entity.addParameter(Configurations.SCOPE, 1);
        long timeStamp = TimeUtil.getCurrentTime();
        device_id = JPushInterface.getRegistrationID(getActivity());
        entity.addParameter(Configurations.TIMESTAMP, String.valueOf(timeStamp));
        entity.addParameter(Configurations.DEVICE_ID, device_id);
        Map<String, String> map = new TreeMap<>();
        map.put(Configurations.LATITUDE, String.valueOf(latitude));
        map.put(Configurations.LONGITUDE, String.valueOf(longitude));
        map.put(Configurations.SCOPE, String.valueOf(1));
        entity.addParameter(Configurations.SIGN,
                SignUtils.createSignString(device_id, timeStamp, map));

        for (String key : map.keySet()) {
            Log.d("Home map", "key:=" + key + "values:=" + map.get(key));
        }

        x.http()
                .get(entity, new Callback.CommonCallback<JSONObject>() {
                    @Override
                    public void onSuccess(JSONObject response) {
                        resolveLocationResult(response);
                        //回调嵌套

                    }

                    @Override
                    public void onError(Throwable ex, boolean isOnCallback) {
                        showNetError();
                    }

                    @Override
                    public void onCancelled(CancelledException cex) {
                        Log.d("Home Loc", cex.toString());

                    }

                    @Override
                    public void onFinished() {
                        Log.d("Home Loc", "Finished");
                    }
                });
    }

    private void refreshData() {
        if (!SharedPrefUtil.getLoginType()
                .equals(SharedPrefUtil.LOGINING)) {
            cartBadgeView.hide();
            scanBadgeView.hide();
            return;

        }
        Observable<NewGoods> cartObservable = GoodApi.getGoodCartList(getActivity(),
                UserUtils.getUserInfo()
                        .getAuth_token(),
                1);

        Observable<NewOrders> orderListObservable = OrderApi.getOrderList(getActivity(),
                UserUtils.getUserInfo()
                        .getAuth_token(),
                "able_take",
                1);
        Observable<ResultZip3> zipObservable = Observable.zip(cartObservable,
                orderListObservable,
                new Func2<NewGoods, NewOrders, ResultZip3>() {
                    @Override
                    public ResultZip3 call(
                            NewGoods goodList, NewOrders orders) {
                        return new ResultZip3(goodList, orders);
                    }
                });
        mSubscriber2 = JijiaHttpSubscriber.buildSubscriber(getActivity())
                .setOnNextListener(new SubscriberOnNextListener<ResultZip3>() {


                    @Override
                    public void onNext(ResultZip3 zip) {

                        if (zip.goodList != null) {
                            NewGoods goodList = zip.goodList;
                            setCatNumber(goodList.getTotal());
                        }
                        if (zip.orders != null) {
                            NewOrders orders = zip.orders;
                            setScanNumber(orders.getAble_take_count());
                        }


                    }
                })
                .build();
        zipObservable.subscribe(mSubscriber2);

    }

    /**
     * 处理返回地址信息
     *
     * @param response
     */

    private void resolveLocationResult(JSONObject response) {
        try {
            LogUtil.e("result", response.toString() + "地址");
            if (response.getInt(Configurations.STATUSCODE) == 200) {
                if (response.getJSONObject("results") == null) {
                    mLlHomeLocation.setClickable(false);
                    mTvLocationnearbyName.setText(LOCATION_EMPTY);
                    mTvLocationnearbyDist.setVisibility(View.INVISIBLE);
                    mTvLocationnearbyDistUnit.setVisibility(View.INVISIBLE);
                } else {
                    mLlHomeLocation.setClickable(true);
                    machines = JSON.parseArray(response.getJSONObject("results")
                            .getString("vending_machines"), Machine.class);
                    if (machines.size() != 0) {
                        mTvLocationnearbyName.setText(machines.get(0)
                                .getAddress());
                        mTvLocationnearbyDist.setText(df.format(Float.parseFloat("" + machines
                                .get(0)
                                .getLinear_distance())));
                        mLlHomeLocation.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                MobclickAgent.onEvent(getContext(),
                                        UmengConfig.EVENT_LOCATION_CLICK);
                                Intent intent = new Intent(getActivity(),
                                        FindVendingMachineActivity.class);
                                intent.putExtra("machines", (Serializable) machines);
                                intent.putExtra("Latitude", Latitude);
                                intent.putExtra("Longitude", Longitude);
                                startActivity(intent);
                            }
                        });


                    } else {
                        mLlHomeLocation.setClickable(false);
                        mTvLocationnearbyName.setText(LOCATION_EMPTY);
                        mTvLocationnearbyDist.setVisibility(View.INVISIBLE);
                        mTvLocationnearbyDistUnit.setVisibility(View.INVISIBLE);
                    }
                }
            }
        } catch (JSONException e) {

            e.printStackTrace();
        }
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        broadcastManager = LocalBroadcastManager.getInstance(getActivity());
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION);
        ableTakeReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                mIbScan.performClick();
            }
        };
        broadcastManager.registerReceiver(ableTakeReceiver, intentFilter);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshData();
    }

    private void checkHasNewCoupon() {
        if (application.getHasShowDialog()) {
            // application.setHasShowDialog(false);
        } else {
            loadCoupons();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(UmengConfig.HOMEFRAGMENT);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        CommonUtil.unSubscribeSubs(mSubscriber, mSubscriber1, mSubscriber2);
        broadcastManager.unregisterReceiver(ableTakeReceiver);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE_FETCH) {

            if (null == data)
                return;
            String result = data.getStringExtra(CaptureActivity.SCAN_RESULT_KEY);
            if (null != result) {
                Intent intent = new Intent(getContext(), AbleTakeActivity.class);
                intent.putExtra("vmid", result);
                startActivity(intent);

            } else {
                ToastUtil.showShort(getActivity(), "扫描失败，待会再试一试");

            }
        } else if (resultCode == Activity.RESULT_CANCELED) {
            ToastUtil.showShort(getActivity(), getResources().getString(R.string.cancel));
        }

    }


    private BDLocationListener bdLocationListener = new BDLocationListener() {
        @Override
        public void onReceiveLocation(BDLocation location) {
            if (null != location && location.getLocType() != BDLocation.TypeServerError) {

                Longitude = location.getLongitude();
                Latitude = location.getLatitude();

                LogUtil.e("Home ", "Longitude: " + Longitude + "Latitude: " + Latitude);
                //                baiduLocationService.stop();
                baiduLocationService.stop();
                getNearby(Longitude, Latitude);


            } else if (location.getLocType() == BDLocation.TypeServerError) {
                ToastUtil.showShort(getActivity(), "服务端网络定位失败");
                baiduLocationService.stop();
            } else if (location.getLocType() == BDLocation.TypeNetWorkException) {
                ToastUtil.showShort(getActivity(), "网络导致定位失败，请检查网络是否通畅");
                baiduLocationService.stop();
            } else if (location.getLocType() == BDLocation.TypeCriteriaException) {
                ToastUtil.showShort(getActivity(), "您的手机当前不支持定位，请重新设置一下情景模式或者重启手机");
                baiduLocationService.stop();
            }

        }
    };


    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }


    class DrinkFragmentAdapter extends SmartFragmentStatePagerAdapter {

        private List<Fragment> list_fragment;
        private List<String> list_Title;


        public DrinkFragmentAdapter(
                FragmentManager fm, List<Fragment> list_fragment, List<String> list_Title) {
            super(fm);
            this.list_fragment = list_fragment;
            this.list_Title = list_Title;
        }

        @Override
        public Fragment getItem(int position) {
            return list_fragment.get(position);
        }

        @Override
        public int getCount() {
            return list_Title.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return list_Title.get(position % list_Title.size());
        }
    }
}
