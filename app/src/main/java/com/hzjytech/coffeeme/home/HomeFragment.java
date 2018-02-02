package com.hzjytech.coffeeme.home;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hzjytech.coffeeme.Dialogs.HintDialog;
import com.hzjytech.coffeeme.Dialogs.ITwoButtonClick;
import com.hzjytech.coffeeme.Dialogs.NewBenefitDialog;
import com.hzjytech.coffeeme.Dialogs.NewCustomBenefitDialog;
import com.hzjytech.coffeeme.Dialogs.NewOutTimeCouponDialog;
import com.hzjytech.coffeeme.R;
import com.hzjytech.coffeeme.baidumap.BaiduLocationService;
import com.hzjytech.coffeeme.configurations.Configurations;
import com.hzjytech.coffeeme.configurations.UmengConfig;
import com.hzjytech.coffeeme.entities.AppItem;
import com.hzjytech.coffeeme.entities.Banner;
import com.hzjytech.coffeeme.entities.CompletyleAdjust;
import com.hzjytech.coffeeme.entities.Coupon;
import com.hzjytech.coffeeme.entities.Good;
import com.hzjytech.coffeeme.entities.LocationNearby;
import com.hzjytech.coffeeme.entities.Machine;
import com.hzjytech.coffeeme.entities.MoreCoffeeHint;
import com.hzjytech.coffeeme.entities.Order;
import com.hzjytech.coffeeme.fragments.BaseFragment;
import com.hzjytech.coffeeme.me.MyCouponActivity;
import com.hzjytech.coffeeme.order.AbleTakeActivity;
import com.hzjytech.coffeeme.utils.AppUtil;
import com.hzjytech.coffeeme.utils.CameraUtil;
import com.hzjytech.coffeeme.utils.DateUtil;
import com.hzjytech.coffeeme.utils.LogUtil;
import com.hzjytech.coffeeme.utils.MyApplication;
import com.hzjytech.coffeeme.utils.MyMath;
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
import com.hzjytech.scan.decoding.Intents;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import cn.jpush.android.api.JPushInterface;
import in.srain.cube.views.ptr.PtrClassicFrameLayout;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrFrameLayout;

/**
 * A simple {@link Fragment} subclass.
 */
@ContentView(R.layout.fragment_home)
public class HomeFragment extends BaseFragment {

    private static final int REQUEST_CODE_FETCH = 2045;
    @ViewInject(R.id.ivHomefrgCart)
    private ImageView ivHomefrgCart;

//    @ViewInject(R.id.vPgHomeBanner)
//    private InfiniteViewPager vPgHomeBanner;
//
//    @ViewInject(R.id.cirindHomeBanner)
//    private CirclePageIndicator cirindHomeBanner;

    @ViewInject(R.id.rcyViewHomeAppitems)
    private RecyclerView rcyViewHomeAppItems;
    @ViewInject(R.id.tbHomefrgTitle)
    private TransparentToolBar tbHomefrgTitle;
    @ViewInject(R.id.tvHomefrgTitle)
    private TextView tvHomeFrgTitle;
    @ViewInject(R.id.rl_title_coffee)
    private RelativeLayout rl_titile_coffee;
    @ViewInject(R.id.store_house_ptr_frame)
    private PtrClassicFrameLayout mPtrFrame;
    @ViewInject(R.id.ibScan)
    private ImageView ibScan;
    @ViewInject(R.id.tv_guide)
    private TextView tv_guide;
    @ViewInject(R.id.rl_guide)
    private RelativeLayout rl_guide;
    private View fl_shade;

    private static final String TAG = HomeFragment.class.getSimpleName();

    private List<Banner> banners = new ArrayList<Banner>();
    private List<String> initUrlString = new ArrayList<String>();

    private List<Object> appItems = new ArrayList<Object>();
    private AppItemsAdapter appItemsAdapter;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
           // appItemsAdapter.notifyDataSetChanged();
            mPtrFrame.refreshComplete();
                    /*    LinearLayout ll = (LinearLayout) rcyViewHomeAppItems.getChildAt(0);
                        FlyBanner homeBanner = (FlyBanner) ll.findViewById(R.id.homeBanner);
                        homeBanner.requestFocus();
                        homeBanner.startAutoPlay();*/

        }
    };
    //    private MockPagerAdapter pagerAdapter;
    private BadgeView cartBadgeView;
    private BadgeView scanBadgeView;
    //声明mLocationOption对象
//    private AMapLocationClientOption mLocationOption = null;
//    private AMapLocationClient mlocationClient = null;
    private double Latitude;
    private double Longitude;
    FlyBanner homeBanner;

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
    private View headview;
    private int mPage;
    private Typeface font;
    private MyApplication application;
    private String device_id;
    private boolean homeFragmentVisible=true;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initView();
        //检查优惠券是否快过期，并弹出弹窗,如果弹出过弹窗，则无需检验
        application = (MyApplication) getActivity().getApplication();
        if(application.getHasShowDialog()){
           // application.setHasShowDialog(false);
        }else{
            checkCouponsIsOutOfTime();
        }

        //测试手机分辨率
        DisplayMetrics metrics=new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);

        int widthPixels=metrics.widthPixels;
        int heightPixels=metrics.heightPixels;
        LogUtil.e("metric",widthPixels+"====="+heightPixels);

    }
    private void checkCouponsIsOutOfTime() {
       loadCoupons();
    }


    private void loadCoupons() {
        RequestParams entity = new RequestParams(Configurations.URL_COUPONS);
        if(UserUtils.getUserInfo()==null|| UserUtils.getUserInfo().getAuth_token()==null){
            return;
        }
        String auth_token = UserUtils.getUserInfo().getAuth_token();
        entity.addParameter(Configurations.AUTH_TOKEN, UserUtils.getUserInfo().getAuth_token());
        //entity.addParameter(Configurations.AVAILABLE, true);
        String device_id= JPushInterface.getRegistrationID(getActivity());
        String timeStamp= TimeUtil.getCurrentTimeString();
        entity.addParameter(Configurations.TIMESTAMP, timeStamp);
        entity.addParameter(Configurations.DEVICE_ID,device_id );

        Map<String, String> map=new TreeMap<String, String>();
        map.put(Configurations.AUTH_TOKEN, UserUtils.getUserInfo().getAuth_token());
        //map.put(Configurations.AVAILABLE, String.valueOf(true));
        entity.addParameter(Configurations.SIGN, SignUtils.createSignString(device_id,timeStamp,map));

        x.http().get(entity, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                LogUtil.e("result",result);
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
                List<Coupon> coupons  = new Gson().fromJson(object.getJSONObject("results").getString("coupons"), new TypeToken<ArrayList<Coupon>>() {
                }.getType());
                long nowday = SharedPrefUtil.getLong("nowday");
                for (int i = 0; i < coupons.size(); i++) {
                    if(coupons.get(i).getEnd_date()==null){
                        continue;
                    }
                    LogUtil.d("coupon" + i, coupons.get(i).getEnd_date()+"");
                    Calendar calendar = DateUtil.ISO8601toCalendar(coupons.get(i).getEnd_date());
                    long diff = calendar.getTimeInMillis() - Calendar.getInstance().getTimeInMillis();
                    long difDays = (diff / (3600 * 24 * 1000))+1;
                    long olddif = calendar.getTimeInMillis() - nowday;
                    long olddays = (olddif / (3600 * 24 * 1000))+1;
                    LogUtil.e("days",difDays+"+++++"+olddays);
                    if(difDays>0&&difDays<=3&&olddays>3){
                        //根据时间判断，如果已经弹出过则弹出时间3天内的不再弹出
                        showOutOfTimeCoupouDialog();
                        SharedPrefUtil.putLong("nowday",Calendar.getInstance().getTimeInMillis());
                        break;
                    }
                }
               long newestCoupenTime=0;
                Coupon newestCoupon=null;
                for (Coupon coupon : coupons) {
                    Calendar calendar = DateUtil.ISO8601toCalendar(coupon.getStart_date());
                    long timeInMillis = calendar.getTimeInMillis();
                    if(newestCoupenTime<timeInMillis){
                       newestCoupenTime=timeInMillis;
                        newestCoupon=coupon;
                    }
                }
                long recentCouponTime = SharedPrefUtil.getLong("recentCouponTime");
                if(newestCoupenTime>recentCouponTime){
                    SharedPrefUtil.putLong("recentCouponTime",newestCoupenTime);
                    String title = newestCoupon.getTitle();
                    final NewBenefitDialog newBenefitDialog = NewBenefitDialog.newInstance(title);
                    newBenefitDialog.setOnTwoButtonClick(new ITwoButtonClick() {
                        @Override
                        public void onLeftButtonClick() {
                            newBenefitDialog.dismiss();
                            application.setHasShowDialog(false);
                        }

                        @Override
                        public void onRightButtonClick() {
                            Intent intent = new Intent(getActivity(), MyCouponActivity.class);
                            startActivity(intent);
                            newBenefitDialog.dismiss();
                            application.setHasShowDialog(false);
                        }
                    });
                    application.setHasShowDialog(true);
                    newBenefitDialog.show(getFragmentManager(),"outTimeDialog");
                }else{
                }

            } else {
                ToastUtil.showShort(getActivity(), object.getString(Configurations.STATUSMSG));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (ParseException e) {
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
            newOutTimeCouponDialog.show(getFragmentManager(),"outTimeDialog");
    }

    //扫一扫
    @Event(R.id.ibScan)
    private void startScan(View v){
        Intent intent = new Intent(getContext(), CaptureActivity.class);
        intent.putExtra("isFromHome",true);
//检查权限
        if (!CameraUtil.isCameraCanUse()) {
            //如果没有授权，则请求授权
            HintDialog hintDialog = HintDialog.newInstance("提示", "无法获取摄像头数据，请检查是否已经打开摄像头权限。", "确定");
            hintDialog.show(getFragmentManager(),"cameraHint");
            //ToastUtil.showShort(getActivity(),"无法获取摄像头权限，请确认是否开启。");
            //ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CAMERA}, MY_PERMISSIONS_REQUEST_CALL_CAMERA);
        } else {
            //有授权，直接开启摄像头
            startActivityForResult(intent,REQUEST_CODE_FETCH);
        }

    }
    private void initView() {
        if(SharedPrefUtil.getIsFirstEnter()){
            rl_guide.setVisibility(View.GONE);
        }else{
            rl_guide.setVisibility(View.GONE);
        }
        tv_guide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                rl_guide.setVisibility(View.GONE);
                SharedPrefUtil.saveIsFirstEnter(false);
            }
        });
        //设置transparentToolBar高度
        int statusHeight = ScreenUtil.getStatusHeight(getContext());
        int titlebar = (int) getResources().getDimension(R.dimen.title_bar_height);
        int toolBarHeight= statusHeight+titlebar;
        ViewGroup.LayoutParams layoutParams = tbHomefrgTitle.getLayoutParams();
        layoutParams.height=toolBarHeight;
        tbHomefrgTitle.setLayoutParams(layoutParams);
        //设置relativeLayout位置
        RelativeLayout.LayoutParams layoutParams1 = (RelativeLayout.LayoutParams) rl_titile_coffee.getLayoutParams();
        layoutParams1.height=titlebar;
        layoutParams1.setMargins(0,statusHeight,0,0);
       /* RelativeLayout.LayoutParams rlParams=new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        rlParams.setMargins(0,statusHeight,0,0);*/
        rl_titile_coffee.setLayoutParams(layoutParams1);
        cartBadgeView = new BadgeView(getContext(), ivHomefrgCart);
        scanBadgeView=new BadgeView(getContext(),ibScan);
        //设置透明标题栏参数
        tbHomefrgTitle.setBgColor(getResources().getColor(R.color.coffee));
        tbHomefrgTitle.setOffset((float) (toolBarHeight));
        tvHomeFrgTitle.setAlpha(0);
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
                loadBanners();
                //handler发送消息，通知完成刷新

            }
        });
        loadBanners();
        setUpRecyclerView();
//        startAmap();
    }
    //// 获取可取订单数量
    private void getAbleTakeOrders() {
        if (!SharedPrefUtil.getLoginType().equals(SharedPrefUtil.LOGINING)) {
            scanBadgeView.hide();
            return;
        }
        RequestParams entity = new RequestParams(Configurations.URL_ORDERS);
        entity.addParameter(Configurations.AUTH_TOKEN, UserUtils.getUserInfo().getAuth_token());
        String device_id = JPushInterface.getRegistrationID(getActivity());
        String timeStamp = TimeUtil.getCurrentTimeString();
        entity.addParameter(Configurations.TIMESTAMP, timeStamp);
        entity.addParameter(Configurations.DEVICE_ID, device_id);
        entity.addParameter("status", "able_take");
        Map<String, String> map = new TreeMap<String, String>();
        map.put(Configurations.AUTH_TOKEN, UserUtils.getUserInfo().getAuth_token());
        mPage = 1;
        map.put("status", "able_take");
        entity.addParameter(Configurations.PAGE, mPage);
        map.put(Configurations.PAGE, String.valueOf(mPage));
        entity.addParameter(Configurations.SIGN, SignUtils.createSignString(device_id, timeStamp, map));


        x.http().get(entity, new Callback.CommonCallback<JSONObject>() {
            @Override
            public void onSuccess(JSONObject result) {
                LogUtil.e("ableTakeResult",result.toString());
                try {
                    if (result.getInt(Configurations.STATUSCODE) == 200) {
                        int count = result.getJSONObject("results").getInt("able_take_count");
                        scanBadgeView.setBadgePosition(BadgeView.POSITION_TOP_RIGHT);
                        if (count < 1) {
                            scanBadgeView.hide();

                        } else if (count  < 10) {
                            scanBadgeView.setTextSize(9);
                            scanBadgeView.setText(String.valueOf(count ));
                            scanBadgeView.show();

                        } else if (count  < 100) {
                            scanBadgeView.setTextSize(8);
                            scanBadgeView.setText(String.valueOf(count ));
                            scanBadgeView.show();
                        } else {
                            scanBadgeView.setTextSize(8);
                            scanBadgeView.setText(R.string.cart_count_max_value);
                            scanBadgeView.show();
                        }

                    }
//                    result.getJSONObject("results").getString("goods");
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

    private void initBaiduLocationConfig() {
        baiduLocationService = ((MyApplication) (getActivity().getApplication())).baiduLocationService;
        baiduLocationService.registerListener(bdLocationListener);
        baiduLocationService.setLocationOption(baiduLocationService.getDefaultLocationClientOption());
    }

    private void startBaiduMap() {
        initBaiduLocationConfig();
        baiduLocationService.start();
    }

//    private void startAmap() {
//
//        mLocationOption = new AMapLocationClientOption();
//        mlocationClient = new AMapLocationClient(getActivity());
//        //设置定位监听
//        mlocationClient.setLocationListener(mAMapLocationListener);
//        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
//        mlocationClient.setLocationOption(mLocationOption);
//        mlocationClient.startLocation();
//    }


//    private AMapLocationListener mAMapLocationListener = new AMapLocationListener() {
//
//        @Override
//        public void onLocationChanged(AMapLocation location) {
//            if (location != null) {
//                if (location.getErrorCode() == 0) {
//
//                    Latitude = location.getLatitude();//获取纬度
//                    Longitude = location.getLongitude();//获取经度
//                    mlocationClient.stopLocation();
//
//                    getNearby();
//                } else {
//                    //显示错误信息ErrCode是错误码，errInfo是错误信息，详见错误码表。
//                    LogUtil.e("", "---location Error, ErrCode:"
//                            + location.getErrorCode() + ", errInfo:"
//                            + location.getErrorInfo());
//                }
//            }
//        }
//    };


    private void getNearby( double longitude,  double latitude) {
      /*  LogUtil.e("getNearBy","getNearBy");
        com.loopj.android.http.RequestParams params = new com.loopj.android.http.RequestParams();
        params.put(Configurations.LATITUDE, latitude);
        params.put(Configurations.LONGITUDE, longitude);
        params.put(Configurations.SCOPE, 1);
        String timeStamp = TimeUtil.getCurrentTimeString();
        String device_id= JPushInterface.getRegistrationID(getActivity());
        params.put(Configurations.APP_ID, AppUtil.getVersionCode(getActivity()));
        params.put(Configurations.TIMESTAMP,timeStamp);
        params.put(Configurations.DEVICE_ID,device_id);
        Map<String, String> map = new TreeMap<>();
        map.put(Configurations.LATITUDE,String.valueOf(latitude));
        map.put(Configurations.LONGITUDE, String.valueOf(longitude));
        map.put(Configurations.SCOPE,String.valueOf(1));
        map.put(Configurations.APP_ID, String.valueOf(AppUtil.getVersionCode(getActivity())));
        params.put(Configurations.SIGN, SignUtils.createSignString(device_id, timeStamp, map));
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(Configurations.URL_VENDING_MACHINES, params, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        try {
                             LogUtil.e("result",response.toString()+"地址");
                            if (response.getInt(Configurations.STATUSCODE) == 200) {
                                if (response.getJSONObject("results") == null) {
                                    addLocationItem(new LocationNearby(LOCATION_EMPTY, DISTANCE_EMPTY));
                                    appItemsAdapter.notifyDataSetChanged();
                                } else {
                                    machines = JSON.parseArray(response.getJSONObject("results").getString("vending_machines"), Machine.class);
                                    if (machines.size() != 0) {

                                        new Handler().post(new Runnable() {
                                                               @Override
                                                               public void run() {

                                                                   addLocationItem(new LocationNearby(machines.get(0).getAddress(), Float.parseFloat("" + machines.get(0).getLinear_distance())));
                                                                   appItemsAdapter.notifyDataSetChanged();
//

                                                               }
                                                           }


                                        );

                                    } else {

                                        new Handler().post(new Runnable() {
                                            @Override
                                            public void run() {

                                                addLocationItem(new LocationNearby(LOCATION_EMPTY, DISTANCE_EMPTY));
                                                appItemsAdapter.notifyDataSetChanged();
//

                                            }
                                        });

                                    }
                                }
                            }
                        } catch (JSONException e) {

                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        super.onFailure(statusCode, headers, responseString, throwable);
                        LogUtil.e("result",responseString.toString());
                    }

                    @Override
                    public void onFinish() {
                        super.onFinish();
                        LogUtil.e("result","finish");
                    }

                    @Override
                    public void onCancel() {
                        super.onCancel();
                        LogUtil.e("result","cancel");
                    }
                }

        );
*/

        LogUtil.d("Home NearBy","NearBy");
        RequestParams entity = new RequestParams(Configurations.URL_VENDING_MACHINES);
        entity.addParameter(Configurations.LATITUDE, latitude);
        LogUtil.d("Home NearBy","NearBy2");
        entity.addParameter(Configurations.LONGITUDE, longitude);
        LogUtil.d("Home NearBy","NearBy3");
        entity.addParameter(Configurations.SCOPE, 1);
        LogUtil.d("Home NearBy","NearBy4");
        //String device_id= JPushInterface.getRegistrationID(getActivity());
        //LogUtil.d("device_id",JPushInterface.getRegistrationID(getActivity())+"");
        LogUtil.d("Home NearBy","NearBy5");
        long timeStamp = TimeUtil.getCurrentTime();
        LogUtil.d("Home NearBy","NearBy6");
        entity.addParameter(Configurations.TIMESTAMP, String.valueOf(timeStamp));
        LogUtil.d("Home NearBy","NearBy7");
        entity.addParameter(Configurations.DEVICE_ID, device_id);
        LogUtil.d("Home NearBy","NearBy8");

        Map<String, String> map = new TreeMap<>();
        map.put(Configurations.LATITUDE, String.valueOf(latitude));
        map.put(Configurations.LONGITUDE, String.valueOf(longitude));
        map.put(Configurations.SCOPE, String.valueOf(1));
        entity.addParameter(Configurations.SIGN, SignUtils.createSignString(device_id, timeStamp, map));


       for(String key:map.keySet()){
           Log.d("Home map","key:="+key+"values:="+map.get(key));
       }

        x.http().get(entity, new Callback.CommonCallback<JSONObject>() {
            @Override
            public void onSuccess(JSONObject response) {

             try {
        LogUtil.e("result",response.toString()+"地址");
        if (response.getInt(Configurations.STATUSCODE) == 200) {
            if (response.getJSONObject("results") == null) {
                addLocationItem(new LocationNearby(LOCATION_EMPTY, DISTANCE_EMPTY));
                appItemsAdapter.notifyDataSetChanged();
            } else {
                machines = JSON.parseArray(response.getJSONObject("results").getString("vending_machines"), Machine.class);
                if (machines.size() != 0) {

                    new Handler().post(new Runnable() {
                                           @Override
                                           public void run() {

                                               addLocationItem(new LocationNearby(machines.get(0).getAddress(), Float.parseFloat("" + machines.get(0).getLinear_distance())));
                                               appItemsAdapter.notifyDataSetChanged();

                                           }
                                       }


                    );

                } else {

                    new Handler().post(new Runnable() {
                        @Override
                        public void run() {

                            addLocationItem(new LocationNearby(LOCATION_EMPTY, DISTANCE_EMPTY));
                            appItemsAdapter.notifyDataSetChanged();
//

                        }
                    });

                }
            }
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
                Log.d("Home Loc",cex.toString());

            }

            @Override
            public void onFinished() {
                Log.d("Home Loc","Finished");
            }
        });
    }


    private void setUpRecyclerView() {

        appItemsAdapter = new AppItemsAdapter();
        //设置头部
        appItemsAdapter.setHeaderView(headview);

        rcyViewHomeAppItems.setAdapter(appItemsAdapter);
        rcyViewHomeAppItems.setLayoutManager(new LinearLayoutManager(getActivity()));
        rcyViewHomeAppItems.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView,dx,dy);
                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                int position = layoutManager.findFirstVisibleItemPosition();
                View firstVisiableChildView = layoutManager.findViewByPosition(position);
                int itemHeight = firstVisiableChildView.getHeight();
                int distance = (position) * itemHeight - firstVisiableChildView.getTop();

                //标题栏高度
                int minHeight =  tbHomefrgTitle.getHeight();
                //获得顶部高度

                float pix = (float) (( minHeight-distance )*1.0 / minHeight);
                //滚动后上端距离，为minHeight时状态栏显示，透明度为1，dy为0时，透明度为0
                tbHomefrgTitle.setChangeTop((float) (distance*1.0));
                //设置回调，改变字体透明度
                tbHomefrgTitle.setOnScrollStateListener(new TransparentToolBar.OnScrollStateListener() {
                    @Override
                    public void updateFraction(float fraction) {
                        tvHomeFrgTitle.setAlpha(fraction);
                    }
                });

            }
        });
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
                ibScan.performClick();
            }
        };
        broadcastManager.registerReceiver(ableTakeReceiver, intentFilter);
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if(hidden){
            homeFragmentVisible =false;
        }else{
            if(!homeFragmentVisible){
                LogUtil.e("homeFragmentVisible", homeFragmentVisible +"");
                homeFragmentVisible =true;
                MobclickAgent.onPageStart(UmengConfig.HOMEFRAGMENT);
                getCartGoodsCount();
                getAbleTakeOrders();
                checkHasNewCoupon();
            }

        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if(homeFragmentVisible){
            MobclickAgent.onPageStart(UmengConfig.HOMEFRAGMENT);
            getCartGoodsCount();
            getAbleTakeOrders();
            checkHasNewCoupon();
            LogUtil.e("meFragmentVisible",true+"");
        }else{

        }
    }

    private void checkHasNewCoupon() {
        if(application.getHasShowDialog()){
            // application.setHasShowDialog(false);
        }else{
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
//        if (null != mlocationClient) {
//            mlocationClient.onDestroy();
//            mlocationClient = null;
//            mLocationOption = null;
//        }
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
               /* if (!result.startsWith("MDEt")) {
                    TitleButtonsDialog descCenterDialog = TitleButtonsDialog.newInstance("二维码有误", "关闭", "扫一扫");
                    descCenterDialog.setOnTwoClickListener(new ITwoButtonClick() {
                        @Override
                        public void onLeftButtonClick() {

                        }

                        @Override
                        public void onRightButtonClick() {
                            ibScan.performClick();


                        }

                    });
                    descCenterDialog.show(getActivity().getSupportFragmentManager(), "asktag");
                    return;
                }*/
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
//                    isNeedLocated=false;


//                StringBuffer sb = new StringBuffer(256);
//                sb.append(location.getTime());
//                sb.append("\nlocType : ");// 定位类型
//                sb.append(location.getLocType());
//                sb.append("\nlocType description : ");// *****对应的定位类型说明*****
//                sb.append(location.getLocTypeDescription());
//                sb.append("\nlatitude : ");// 纬度
//                sb.append(location.getLatitude());
//                sb.append("\nlontitude : ");// 经度
//                sb.append(location.getLongitude());
//                sb.append("\nradius : ");// 半径
//                sb.append(location.getRadius());
//                sb.append("\nCountryCode : ");// 国家码
//                sb.append(location.getCountryCode());
//                sb.append("\nCountry : ");// 国家名称
//                sb.append(location.getCountry());
//                sb.append("\ncitycode : ");// 城市编码
//                sb.append(location.getCityCode());
//                sb.append("\ncity : ");// 城市
//                sb.append(location.getCity());
//                sb.append("\nDistrict : ");// 区
//                sb.append(location.getDistrict());
//                sb.append("\nStreet : ");// 街道
//                sb.append(location.getStreet());
//                sb.append("\naddr : ");// 地址信息
//                sb.append(location.getAddrStr());
//                sb.append("\nUserIndoorState: ");// *****返回用户室内外判断结果*****
//                sb.append(location.getUserIndoorState());
//                sb.append("\nDirection(not all devices have value): ");
//                sb.append(location.getDirection());// 方向
//                sb.append("\nlocationdescribe: ");
//                sb.append(location.getLocationDescribe());// 位置语义化信息
//                sb.append("\nPoi: ");// POI信息
//
//                LogUtil.d("BaiduMap", sb.toString());

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


    private void loadBanners() {
        RequestParams entity = new RequestParams(Configurations.URL_BANNERS);
        entity.addParameter(Configurations.APP_ID, AppUtil.getVersionCode(getActivity()));
        String device_id= JPushInterface.getRegistrationID(getActivity());
        String timeStamp= TimeUtil.getCurrentTimeString();
        entity.addParameter(Configurations.TIMESTAMP, timeStamp);
        entity.addParameter(Configurations.DEVICE_ID,device_id );
        Map<String,String> map=new TreeMap<>();
        map.put(Configurations.APP_ID, String.valueOf(AppUtil.getVersionCode(getActivity())));
        entity.addParameter(Configurations.SIGN, SignUtils.createSignString(device_id,timeStamp,map));
        x.http().get(entity, new Callback.CommonCallback<JSONObject>() {
            @Override
            public void onSuccess(JSONObject result) {
//                LogUtil.d("Home banner", result.toString());
                try {
                    //LogUtil.e("result",result.toString()+"轮播图");
                    if (result.getInt(Configurations.STATUSCODE) == 200) {
                        banners = JSON.parseArray(result.getJSONObject("results").getString("banners"), Banner.class);
                        urlList = new ArrayList<String>();
                        urlList.clear();
                        for (Banner banner : banners) {
                            urlList.add(banner.getImage_url());
                        }

                        if (isFirst || (initUrlString != null && !initUrlString.equals(urlList))) {
                            headview = View.inflate(getContext(), R.layout.item_recycleview_banner, null);
                            homeBanner = (FlyBanner) headview.findViewById(R.id.homeBanner);
                            appItemsAdapter.setHeaderView(headview);
                            initUrlString.clear();
                            initUrlString.addAll(urlList);
                            isFirst = false;
                            homeBanner.setImagesUrl(banners);
                            homeBanner.setOnItemClickListener(new FlyBanner.OnItemClickListener() {
                                @Override
                                public void onItemClick(int position) {
                                    MobclickAgent.onEvent(getContext(), UmengConfig.EVENT_BANNER_CLICK);
                                    Intent intent = new Intent(getContext(), BannerDetailActivity.class);
                                    intent.putExtra("url_article", banners.get(position).getArticle_url());
                                    getContext().startActivity(intent);
                                }
                            });
                        }


                        getAppItems();
//                        setBannerAdapter();
//                        pagerAdapter.notifyDataSetChanged();
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

    private void getCartGoodsCount() {

        if (!SharedPrefUtil.getLoginType().equals(SharedPrefUtil.LOGINING)) {
            cartBadgeView.hide();
            return;
        }
        RequestParams entity = new RequestParams(Configurations.URL_GOODS);
        entity.addParameter(Configurations.AUTH_TOKEN, UserUtils.getUserInfo().getAuth_token());
        entity.addParameter(Configurations.APP_ID, AppUtil.getVersionCode(getActivity()));
        String device_id= JPushInterface.getRegistrationID(getActivity());
        String timeStamp= TimeUtil.getCurrentTimeString();
        entity.addParameter(Configurations.TIMESTAMP, timeStamp);
        entity.addParameter(Configurations.DEVICE_ID,device_id );
        Map<String,String> map=new TreeMap<>();
        map.put(Configurations.AUTH_TOKEN, UserUtils.getUserInfo().getAuth_token());
        map.put(Configurations.APP_ID, String.valueOf(AppUtil.getVersionCode(getActivity())));
        entity.addParameter(Configurations.SIGN, SignUtils.createSignString(device_id,timeStamp,map));
        x.http().get(entity, new Callback.CommonCallback<JSONObject>() {
            @Override
            public void onSuccess(JSONObject result) {
               // //LogUtil.e("Home goods", result.toString());

                try {
                    LogUtil.e("result",result.toString()+"商品");
                    checkResOld(result);
                    if (result.getInt(Configurations.STATUSCODE) == 200) {
                        int count = result.getJSONObject("results").getInt("goods_count");

                        cartBadgeView.setBadgePosition(BadgeView.POSITION_TOP_RIGHT);
                        if (count < 1) {
                            cartBadgeView.hide();

                        } else if (count < 10) {
                            cartBadgeView.setTextSize(9);
                            cartBadgeView.setText(String.valueOf(count));
                            cartBadgeView.show();

                        } else if (count< 100) {
                            cartBadgeView.setTextSize(8);
                            cartBadgeView.setText(String.valueOf(count));
                            cartBadgeView.show();
                        } else {
                            cartBadgeView.setTextSize(8);
                            cartBadgeView.setText(R.string.cart_count_max_value);
                            cartBadgeView.show();
                        }
                    }
//                    result.getJSONObject("results").getString("goods");
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

    private void initCartGoodsCount() {

        getCartGoodsCount();

        ivHomefrgCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!SharedPrefUtil.getLoginType().equals(SharedPrefUtil.LOGINING)) {

                    goLogin();
                    return;
                }
                startActivity(new Intent(getActivity(), NewCartActivity.class));
//                startActivity(new Intent(getActivity(), JPushMainActivity.class));

            }
        });

    }

    private void getAppItems() {

        RequestParams entity = new RequestParams(Configurations.URL_APP_ITEMS);

        entity.addParameter(Configurations.APP_ID, AppUtil.getVersionCode(getActivity()));
        device_id = JPushInterface.getRegistrationID(getActivity());
        String timeStamp= TimeUtil.getCurrentTimeString();
        entity.addParameter(Configurations.TIMESTAMP, timeStamp);
        entity.addParameter(Configurations.DEVICE_ID, device_id);
        Map<String,String> map=new TreeMap<>();
        map.put(Configurations.APP_ID, String.valueOf(AppUtil.getVersionCode(getActivity())));
        entity.addParameter(Configurations.SIGN, SignUtils.createSignString(device_id,timeStamp,map));
        x.http().get(entity, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                //LogUtil.e("result",result+"商品2");
              //  //LogUtil.e("Home AppItems", result);
                appItems.clear();
                appItems.add(new LocationNearby(LOCATION_LOADING, DISTANCE_LOADING));
                //appItems = parseResult(result);
                appItems.addAll(parseResult(result));
               // appItems.add(parseResult(result).get(0));
                appItems.add(new CompletyleAdjust());
                appItems.add(new MoreCoffeeHint());

                appItemsAdapter.notifyDataSetChanged();

                startBaiduMap();

                initCartGoodsCount();
                getAbleTakeOrders();
                //通过发送消息，通知完成刷新
                mHandler.sendEmptyMessageDelayed(0, 500);


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




    private void addLocationItem(LocationNearby locationNearby) {
        appItems.set(0, locationNearby);
    }
    private List<Object> parseResult(String result) {
        List<Object> items = new ArrayList<Object>();

        try {
            JSONObject object = new JSONObject(result);
            if (object.getInt(Configurations.STATUSCODE) == 200) {
                String appitems = object.getJSONObject(Configurations.RESULTS).getString("app_items");
                items = new Gson().fromJson(appitems, new TypeToken<ArrayList<AppItem>>() {
                }.getType());
            } else {
                ToastUtil.showShort(getActivity(), object.getString(Configurations.STATUSMSG));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return items;
    }


    class AppItemsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        public static final int LOCATION = 1;
        private static final int APPITEM = 2;
        private static final int MORECOFFEEHINT = 3;
        private static final int HEADER = 0;
        private static final int COMPLETELYADJUST = 4;
        private View headerView;

        //插入头部Header
        public void setHeaderView(View headerView) {
            this.headerView = headerView;
            notifyDataSetChanged();

        }

        class AppItemsViewHolder1 extends RecyclerView.ViewHolder implements View.OnClickListener {

            private final TextView tvLocationnearbyName;
            private final TextView tvLocationnearbyDist;
            private final TextView tvLocationnearbyDistUnit;

            public AppItemsViewHolder1(View itemView) {
                super(itemView);
                itemView.setOnClickListener(this);
                tvLocationnearbyName = (TextView) itemView.findViewById(R.id.tvLocationnearbyName);
                tvLocationnearbyDist = (TextView) itemView.findViewById(R.id.tvLocationnearbyDist);
                tvLocationnearbyDistUnit = (TextView) itemView.findViewById(R.id.tvLocationnearbyDistUnit);
            }

            public void setClickable(boolean clickable) {
                itemView.setClickable(clickable);
            }

            public void setVisable(boolean visable) {
                //如果传入false，则将距离和单位设置为不可见
                if (!visable) {
                    tvLocationnearbyDist.setVisibility(View.INVISIBLE);
                    tvLocationnearbyDistUnit.setVisibility(View.INVISIBLE);
                } else {
                    tvLocationnearbyDist.setVisibility(View.VISIBLE);
                    tvLocationnearbyDistUnit.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onClick(View v) {
                MobclickAgent.onEvent(getContext(), UmengConfig.EVENT_LOCATION_CLICK);
                Intent intent = new Intent(getActivity(), FindVendingMachineActivity.class);
                intent.putExtra("machines", (Serializable) machines);
                intent.putExtra("Latitude", Latitude);
                intent.putExtra("Longitude", Longitude);

                startActivity(intent);
            }
        }


        class AppItemsViewHolder2 extends RecyclerView.ViewHolder implements View.OnClickListener {

            public ImageView iv_item;
            public TextView tvAppitemName_ch_;
            public TextView tvAppitemName_eg;
            public TextView tv_desc;
            public TextView tv_price_original;
            public TextView tv_price_current;

            public AppItemsViewHolder2(View itemView) {
                super(itemView);
                if (itemView == headerView)
                    return;
                iv_item = (ImageView) itemView.findViewById(R.id.iv_item);
                tvAppitemName_ch_ = (TextView) itemView.findViewById(R.id.tvAppitemName_ch);
                tvAppitemName_eg = (TextView) itemView.findViewById(R.id.tvAppitemName_eg);
                tv_desc = (TextView) itemView.findViewById(R.id.tv_desc);
                tv_price_original = (TextView) itemView.findViewById(R.id.tv_price_original);
                tv_price_current = (TextView) itemView.findViewById(R.id.tv_price_current);

                itemView.setOnClickListener(this);
            }

            @Override
            public void onClick(View v) {
                int position = headerView == null ? getLayoutPosition() : getLayoutPosition() - 1;
                MobclickAgent.onEvent(getContext(), UmengConfig.EVENT_MODULATION_CLICK);
                Intent intent = new Intent(getActivity(), ModulationActivity.class);
                intent.putExtra(Configurations.APP_ITEM, (AppItem) appItems.get(position));
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.slide_in_right_base,
                        R.anim.slide_out_left_base);
            }
        }
        class AppItemsViewHolder3 extends RecyclerView.ViewHolder implements View.OnClickListener {

            public AppItemsViewHolder3(View itemView) {
                super(itemView);
                itemView.setOnClickListener(this);
            }

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), CompletelyAdjustActivity.class);
                startActivity(intent);
                // 第一个参数是目标Activity进入时的动画，第二个参数是当前Activity退出时的动画
                getActivity().overridePendingTransition(R.anim.slide_in_left_quick,
                        R.anim.slide_out_right_quick);
            }
        }
        class AppItemsViewHolder4 extends RecyclerView.ViewHolder {

            public AppItemsViewHolder4(View itemView) {
                super(itemView);
            }

        }

        @Override
        public int getItemViewType(int position) {
            //如果banner还未设置给recyclerview
            if (null == headerView) {
                if (appItems.get(position) instanceof LocationNearby) {
                    return LOCATION;
                } else if (appItems.get(position) instanceof AppItem) {
                    return APPITEM;
                } else if (appItems.get(position) instanceof MoreCoffeeHint) {
                    return MORECOFFEEHINT;
                }else if(appItems.get(position)instanceof CompletyleAdjust){
                    return COMPLETELYADJUST;
                }
            }
            if (position == 0) {
                return HEADER;
            }
            if (appItems.get(position - 1) instanceof LocationNearby) {
                return LOCATION;
            } else if (appItems.get(position - 1) instanceof AppItem) {
                return APPITEM;
            } else if (appItems.get(position - 1) instanceof MoreCoffeeHint) {
                return MORECOFFEEHINT;
            }else if(appItems.get(position-1)instanceof CompletyleAdjust){
                return COMPLETELYADJUST;
            }
            return -1;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            RecyclerView.ViewHolder viewHolder = null;
            if (headerView != null && viewType == HEADER) {
                viewHolder = new AppItemsViewHolder2(headerView);
                return viewHolder;
            }
            switch (viewType) {
                case LOCATION:
                    View view1 = inflater.inflate(R.layout.locationnearby_item, parent, false);
                    viewHolder = new AppItemsViewHolder1(view1);
                    break;
                case APPITEM:
                    View view2 = inflater.inflate(R.layout.appitem_item, parent, false);
                    viewHolder = new AppItemsViewHolder2(view2);
                    break;
                case MORECOFFEEHINT:
                    View view3 = inflater.inflate(R.layout.appitem_hint, parent, false);
                    viewHolder = new AppItemsViewHolder4(view3);
                    break;
                case COMPLETELYADJUST:
                    View view4 = inflater.inflate(R.layout.appitem_completlyadjust, parent, false);
                    viewHolder = new AppItemsViewHolder3(view4);

            }
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            if (holder.getItemViewType() == HEADER) {
                return;
            }
            int realPos = headerView == null ? position : position - 1;
            switch (holder.getItemViewType()) {
                case LOCATION:
                    AppItemsViewHolder1 holder1 = (AppItemsViewHolder1) holder;
                    LocationNearby locationNearby = (LocationNearby) appItems.get(realPos);
                    holder1.setClickable(true);
                    holder1.setVisable(true);
                    if (locationNearby.getName().equals(LOCATION_EMPTY) || locationNearby.getName().equals(LOCATION_LOADING)) {
                        holder1.setClickable(false);
                        holder1.setVisable(false);
                    }
                    holder1.tvLocationnearbyName.setText(locationNearby.getName());
                    holder1.tvLocationnearbyDist.setText(locationNearby.getDistance().equals(0.0+"")?0+"":locationNearby.getDistance());
                    break;
                case APPITEM:
                    AppItemsViewHolder2 holder2 = (AppItemsViewHolder2) holder;
                    AppItem appItem = (AppItem) appItems.get(realPos);
                    holder2.tvAppitemName_ch_.setText(appItem.getName());
                    holder2.tvAppitemName_eg.setText(appItem.getName_en());
                    holder2.tv_desc.setText(appItem.getDescription());
                    holder2.tv_price_original.setText("￥" + String.valueOf(appItem.getPrice()));
                    holder2.tv_price_original.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
                    holder2.tv_price_current.setText("￥" + String.valueOf(appItem.getCurrent_price()));
                    //holder2.iv_item.setImageResource(getItemDrawable(appItem.getName()));
                    //// TODO: 2017/1/4

                        holder2.iv_item.setImageResource(0);
                        holder2.iv_item.setTag(appItem.getApp_image());
                        holder2.iv_item.setScaleType(ImageView.ScaleType.FIT_CENTER);
                        DisplayImageOptions options=new DisplayImageOptions.Builder()
                                .cacheInMemory(true)/*缓存至内存*/
                                .cacheOnDisk(true)/*缓存值SDcard*/
                                .bitmapConfig(Bitmap.Config.RGB_565)
                                .build();
                        String app_image = appItem.getApp_image();
                      //  String str = app_image.substring(0, app_image.indexOf("?"));
                        ImageLoader.getInstance().displayImage(app_image,holder2.iv_item,options);
//                Picasso.with(getContext())
//                        .load(mImageUrls.get(toRealPosition(position)))
//                        .into(imageView);

        /*            holder2.tv_price_current.setTypeface(font);
                    holder2.tv_price_original.setTypeface(font);
                    holder2.tv_desc.setTypeface(font);
                    holder2.tvAppitemName_ch_.setTypeface(font);
                    holder2.tvAppitemName_eg.setTypeface(font);*/
                    break;

            }

        }

        /**
         * id :1 卡布奇诺
         * id :2 摩卡
         * id :3 意式浓缩
         * id :4 玛琪雅朵
         * id :6 美式加糖
         * id :10 拿铁
         */
       /* private int getItemDrawable(String name) {

            switch (name) {
                case "卡布奇诺":
                    return R.drawable.cappuccino;
                case "摩卡":
                    return R.drawable.mocha;
                case "意式浓缩":、
                    return R.drawable.espresso;
                case "玛琪雅朵":
                    return R.drawable.macchiato;
                case "美式咖啡":
                    return R.drawable.cafeamericano;
                case "拿铁":
                    return R.drawable.latte;
                case "巧克力咖啡":
                    return R.drawable.chocolatecoffee;
                case "牛奶":
                    return R.drawable.milk;
                case "牛奶巧克力":
                    return  R.drawable.milkchocolate;
                case "巧克力":
                    return R.drawable.hotchocolate;
                default:
                    return R.drawable.cappuccino;
            }
        }*/

        @Override
        public int getItemCount() {
            if (appItems == null) {
                return 0;
            } else {
                if (headerView == null) {
                    return appItems.size();
                } else {
                    return appItems.size() + 1;
                }
            }
        }


    }
}