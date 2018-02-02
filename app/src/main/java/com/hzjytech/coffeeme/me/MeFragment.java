package com.hzjytech.coffeeme.me;


import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hzjytech.coffeeme.Dialogs.ITwoButtonClick;
import com.hzjytech.coffeeme.Dialogs.NewCustomBenefitDialog;
import com.hzjytech.coffeeme.Dialogs.NewOutTimeCouponDialog;
import com.hzjytech.coffeeme.R;
import com.hzjytech.coffeeme.configurations.Configurations;
import com.hzjytech.coffeeme.configurations.UmengConfig;
import com.hzjytech.coffeeme.entities.Coupon;
import com.hzjytech.coffeeme.entities.User;
import com.hzjytech.coffeeme.fragments.BaseFragment;
import com.hzjytech.coffeeme.utils.DateTimeUtil;
import com.hzjytech.coffeeme.utils.DateUtil;
import com.hzjytech.coffeeme.utils.LogUtil;
import com.hzjytech.coffeeme.utils.MyApplication;
import com.hzjytech.coffeeme.utils.SharedPrefUtil;
import com.hzjytech.coffeeme.utils.SignUtils;
import com.hzjytech.coffeeme.utils.TextUtil;
import com.hzjytech.coffeeme.utils.TimeUtil;
import com.hzjytech.coffeeme.utils.ToastUtil;
import com.hzjytech.coffeeme.utils.UserUtils;
import com.hzjytech.coffeeme.widgets.CircleImageView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.umeng.analytics.MobclickAgent;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import cn.jpush.android.api.JPushInterface;

@ContentView(R.layout.fragment_me)
public class MeFragment extends BaseFragment {

    private static int MEFRAGMENT_REQUEST_CODE = 0x00001;
    private User user;
    private boolean loadFromUser = true;
    private DecimalFormat df;
    private boolean meFragmentVisible = true;
    private MyApplication application;

    public interface MeFragmentable {
        void onMeFragmentClick(String info);
    }

    /**
     * Adapter all data list
     */
    @ViewInject(R.id.btnMefrgBalance)
    private TextView btnMefrgBalance;

    @ViewInject(R.id.btnMefrgCoupon)
    private TextView btnMefrgCoupon;

    @ViewInject(R.id.btnMeExgCoupon)
    private TextView btnMeExgCoupon;

    @ViewInject(R.id.llMefrgBalance)
    private LinearLayout llMefrgBalance;

    @ViewInject(R.id.llMefrgCoupon)
    private LinearLayout llMefrgCoupon;

    @ViewInject(R.id.civMefrgAvator)
    private CircleImageView civMefrgAvator;

    @ViewInject(R.id.tvMefrgNickname)
    private TextView tvMefrgNickname;

    @ViewInject(R.id.llMefrgLevelContainer)
    private LinearLayout llMefrgLevelContainer;

    @ViewInject(R.id.tvMeFrgLevelName)
    private TextView tvMeFrgLevelName;

    @ViewInject(R.id.ivMeFrgLevelImage)
    private ImageView ivMeFrgLevelImage;

    @ViewInject(R.id.MefrgSharelayout)
    private LinearLayout MefrgSharelayout;

    @ViewInject(R.id.iv_recharge)
    private ImageView mIvRecharge;
    @Event(R.id.ivMefrgSetting)
    private void onMefrgSettingClick(View view) {
        Intent intent = new Intent(getActivity(), SettingActivity.class);
        startActivity(intent);
    }


    @Event(value = {R.id.MefrgPointlayout,R.id.llMefrgLevelContainer})
    private void onMefrgLevelContainerClick(View view) {
        if (null == user) {
            goLogin();
            return;
        }
        Intent intent = new Intent(getActivity(), PointRateActivity.class);
        startActivity(intent);
    }
    @Event(R.id.llMeExgCoupon)
    private void onLlMeExgCouponClick(View view){
        if (null == user) {
            goLogin();
            return;
        }
        Intent intent = new Intent(getActivity(), MyCouponActivity.class);
        intent.putExtra("type","redeem");
        startActivity(intent);
    }
    @Event(R.id.iv_recharge)
     private void onRechargeClick(View view){
        if (null == user) {
            goLogin();
            return;
        }
        Intent intent = new Intent(getActivity(), NewRechargeActivity.class);
        startActivity(intent);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        application = (MyApplication) getActivity().getApplication();
        df = new DecimalFormat("0.00");
        super.onViewCreated(view, savedInstanceState);
        if (SharedPrefUtil.getLoginType()
                .equals(SharedPrefUtil.LOGINING)) {
            user = UserUtils.getUserInfo();
        } else {
            user = null;
        }

        civMefrgAvator.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (null == user) {
                    goLogin();
                    return;
                }
                Intent intent = new Intent(getActivity(), MeActivity.class);
                startActivityForResult(intent, MeFragment.MEFRAGMENT_REQUEST_CODE);
            }
        });

        llMefrgCoupon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null == user) {
                    goLogin();
                    return;
                }
                Intent intent = new Intent(getActivity(), MyCouponActivity.class);
                intent.putExtra("type","coupon");
                startActivity(intent);
            }
        });


        llMefrgBalance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null == user) {

                    goLogin();
                    return;
                }
                Intent intent = new Intent(getActivity(), MyBalanceActivity.class);
                startActivity(intent);
            }
        });
        MefrgSharelayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null == user) {

                    goLogin();
                    return;
                }
                Intent intent = new Intent(getActivity(), ShareActivity.class);
                startActivity(intent);
            }
        });

        if (null == user) {
            llMefrgLevelContainer.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (hidden) {
            meFragmentVisible = false;
        } else {
            if (!meFragmentVisible) {
                LogUtil.e("meFragmentVisible", meFragmentVisible + "");
                meFragmentVisible = true;
                initAllData();
            }

        }
    }

    @Override
    public void onResume() {
        if (meFragmentVisible) {
            initAllData();
            LogUtil.e("meFragmentVisible", true + "");
        } else {

        }

        super.onResume();

    }

    //初始化所有数据
    private void initAllData() {
        initBenefitDialog();
        //登录后检查是否有快过期的优惠券
        checkCouponsIsOutOfTime();
        initData();
        MobclickAgent.onPageStart(UmengConfig.MEFRAGMENT);
        if (SharedPrefUtil.getLoginType()
                .equals(SharedPrefUtil.LOGINING)) {
            user = UserUtils.getUserInfo();
        } else {
            user = null;
        }
        initMefrgCoupon();
        initMeExgCoupon();
        initMefrgBalance();
        initMefrgAvator();
        intiMefrgNickname();
        initMefrgLevel();

    }

    private void initData() {
        showLoading();
        RequestParams entity = new RequestParams(Configurations.URL_CHECK_TOKEN);
        if (UserUtils.getUserInfo() != null) {
            entity.addParameter(Configurations.AUTH_TOKEN,
                    UserUtils.getUserInfo()
                            .getAuth_token());

            String timeStamp = TimeUtil.getCurrentTimeString();
            String device_id = JPushInterface.getRegistrationID(getActivity());
            entity.addParameter(Configurations.TIMESTAMP, timeStamp);
            entity.addParameter(Configurations.DEVICE_ID, device_id);

            Map<String, String> map = new TreeMap<>();
            map.put(Configurations.AUTH_TOKEN,
                    UserUtils.getUserInfo()
                            .getAuth_token());
            entity.addParameter(Configurations.SIGN,
                    com.hzjytech.coffeeme.utils.SignUtils.createSignString(device_id,
                            timeStamp,
                            map));
        }

        x.http()
                .get(entity, new Callback.CommonCallback<JSONObject>() {
                    @Override
                    public void onSuccess(JSONObject result) {
                        hideLoading();
                        try {
                            if (result.getInt(Configurations.STATUSCODE) == 200) {
                                user = JSON.parseObject(result.getJSONObject("results")
                                        .getString("user"), User.class);
                                UserUtils.saveUserInfo(user);
                                initMefrgLevel();
                            } else {
                            }
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
                        hideLoading();
                    }
                });
    }

    //新用户注册赠送优惠券
    //// TODO: 2017/1/3 修改user类中userbenefit字段，加入有效期字段
    private void initBenefitDialog() {
        if (application.getHasShowDialog()) {
            return;
        }
        if (SharedPrefUtil.getIsFirstRegister()) {
            User user = UserUtils.getUserInfo();
            if (user == null) {
                return;
            }
            User.BenefitCouponBean benefit = user.getBenefit_coupon();
            if (benefit == null) {
                return;
            }
            int coupon_type = benefit.getCoupon_type();
            String title = benefit.getTitle();
            String value = benefit.getValue();
            String end_date = benefit.getEnd_date();
            String count = null;
            String unit = null;
            String able = null;
            String date = null;
            switch (coupon_type) {
                //打折优惠劵
                case 1:
                    DecimalFormat decimalFormat = new DecimalFormat("##0.0");
                    count = decimalFormat.format(Float.valueOf(value) * 0.1f);
                    unit = "折";
                    able = "可直接使用";
                    break;
                //满减优惠券
                case 2:
                    if (!TextUtils.isEmpty(value)) {
                        String[] strings = value.split("-");
                        count = strings[1];
                        unit = "￥";
                        able = "满" + strings[0] + "元可用";
                    }
                    break;
                //立减优惠券
                case 3:
                    count = value;
                    unit = "￥";
                    able = "可直接使用";
                    break;
            }
            date =DateTimeUtil.longToShort7(Long.valueOf(end_date));

            final NewCustomBenefitDialog newCustomBenefitDialog = NewCustomBenefitDialog
                    .newInstance(
                    title,
                    count,
                    unit,
                    able,
                    "有效期至" + date);
            newCustomBenefitDialog.setOnTwoButtonClick(new ITwoButtonClick() {
                @Override
                public void onLeftButtonClick() {
                    newCustomBenefitDialog.dismiss();
                    application.setHasShowDialog(false);
                }

                @Override
                public void onRightButtonClick() {
                    Intent intent = new Intent(getActivity(), MyCouponActivity.class);
                    intent.putExtra("type","coupon");
                    startActivity(intent);
                    newCustomBenefitDialog.dismiss();
                    application.setHasShowDialog(false);
                }
            });
            newCustomBenefitDialog.show(getFragmentManager(), "newBenefit");
            application.setHasShowDialog(true);
            SharedPrefUtil.saveIsFirstRegister(false);
        }

    }

    private void initMefrgLevel() {
        if (null != user) {
            llMefrgLevelContainer.setVisibility(View.VISIBLE);
            if (UserUtils.getUserInfo()
                    .getUser_level() != null) {
                tvMeFrgLevelName.setText(TextUtils.isEmpty(UserUtils.getUserInfo()
                        .getUser_level()
                        .getLevel_name()) ? "" : UserUtils.getUserInfo()
                        .getUser_level()
                        .getLevel_name());

                if (TextUtils.isEmpty(UserUtils.getUserInfo()
                        .getUser_level()
                        .getImage())) {
                    ivMeFrgLevelImage.setVisibility(View.INVISIBLE);

                } else {
                    ivMeFrgLevelImage.setVisibility(View.VISIBLE);
                    DisplayImageOptions options = new DisplayImageOptions.Builder().cacheInMemory(
                            true)/*缓存至内存*/
                            .cacheOnDisk(true)/*缓存值SDcard*/
                            .bitmapConfig(Bitmap.Config.RGB_565)
                            .imageScaleType(ImageScaleType.EXACTLY)
                            .build();
                    ImageLoader.getInstance()
                            .displayImage(UserUtils.getUserInfo()
                                    .getUser_level()
                                    .getImage(), ivMeFrgLevelImage, options);
                }

            }

        } else {
            llMefrgLevelContainer.setVisibility(View.INVISIBLE);
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == MEFRAGMENT_REQUEST_CODE && resultCode == MeActivity.RESULTCODE_OK) {
            if (null != SharedPrefUtil.getUri()) {

                DisplayImageOptions options = new DisplayImageOptions.Builder().cacheInMemory
                        (true)/*缓存至内存*/
                        .cacheOnDisk(true)/*缓存值SDcard*/
                        .bitmapConfig(Bitmap.Config.RGB_565)
                        .imageScaleType(ImageScaleType.EXACTLY)
                        .showImageOnLoading(R.drawable.icon_user_default)
                        .build();
                ImageLoader.getInstance()
                        .displayImage(SharedPrefUtil.getUri(), civMefrgAvator, options);
            } else {
                civMefrgAvator.setImageResource(R.drawable.icon_user_default);
            }
        } else if (requestCode == MEFRAGMENT_REQUEST_CODE && resultCode == MeActivity
                .RESULTCODE_CAMERA_OK) {

            if (null != SharedPrefUtil.getUri()) {

                DisplayImageOptions options = new DisplayImageOptions.Builder().cacheInMemory
                        (true)/*缓存至内存*/
                        .cacheOnDisk(true)/*缓存值SDcard*/
                        .bitmapConfig(Bitmap.Config.RGB_565)
                        .imageScaleType(ImageScaleType.EXACTLY)
                        .showImageOnLoading(R.drawable.icon_user_default)
                        .build();
                ImageLoader.getInstance()
                        .displayImage(SharedPrefUtil.getUri(), civMefrgAvator, options);
            } else {
                civMefrgAvator.setImageResource(R.drawable.icon_user_default);
            }
        }
    }


    private void intiMefrgNickname() {

        if (null != user) {
            tvMefrgNickname.setVisibility(View.VISIBLE);
            if (loadFromUser) {
                if (!TextUtils.isEmpty(user.getNickname())) {
                    tvMefrgNickname.setText(user.getNickname());
                } else {
                    tvMefrgNickname.setText(user.getPhone());
                }
            } else {
                if (!TextUtils.isEmpty(UserUtils.getUserInfo()
                        .getNickname())) {
                    tvMefrgNickname.setText(UserUtils.getUserInfo()
                            .getNickname());
                } else {
                    tvMefrgNickname.setText(UserUtils.getUserInfo()
                            .getPhone());
                }
            }

        } else {
            tvMefrgNickname.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        loadFromUser = false;

        MobclickAgent.onPageEnd(UmengConfig.MEFRAGMENT);
    }

    private void initMefrgAvator() {

        if (!TextUtils.isEmpty(SharedPrefUtil.getUri())) {

            DisplayImageOptions options = new DisplayImageOptions.Builder().cacheInMemory(true)
                    /*缓存至内存*/
                    .cacheOnDisk(true)/*缓存值SDcard*/
                    .bitmapConfig(Bitmap.Config.RGB_565)
                    .imageScaleType(ImageScaleType.EXACTLY)
                    .build();
            ImageLoader.getInstance()
                    .displayImage(SharedPrefUtil.getUri(),
                            civMefrgAvator,
                            options,
                            new ImageLoadingListener() {
                                @Override
                                public void onLoadingStarted(String s, View view) {

                                }

                                @Override
                                public void onLoadingFailed(
                                        String s,
                                        View view,
                                        FailReason failReason) {
                                    civMefrgAvator.setImageResource(R.drawable.icon_user_default);
                                }

                                @Override
                                public void onLoadingComplete(String s, View view, Bitmap bitmap) {

                                }

                                @Override
                                public void onLoadingCancelled(String s, View view) {

                                }
                            });

        } else if (UserUtils.getUserInfo() != null && !TextUtils.isEmpty(UserUtils.getUserInfo()
                .getAvator_url())) {
            DisplayImageOptions options = new DisplayImageOptions.Builder().cacheInMemory(true)
                    /*缓存至内存*/
                    .cacheOnDisk(true)/*缓存值SDcard*/
                    .bitmapConfig(Bitmap.Config.RGB_565)
                    .build();
            ImageLoader.getInstance()
                    .displayImage(UserUtils.getUserInfo()
                            .getAvator_url(), civMefrgAvator, options, new ImageLoadingListener() {
                        @Override
                        public void onLoadingStarted(String s, View view) {

                        }

                        @Override
                        public void onLoadingFailed(String s, View view, FailReason failReason) {
                            civMefrgAvator.setImageResource(R.drawable.icon_user_default);
                        }

                        @Override
                        public void onLoadingComplete(String s, View view, Bitmap bitmap) {

                        }

                        @Override
                        public void onLoadingCancelled(String s, View view) {

                        }
                    });

        } else if (user != null) {

            if (!TextUtils.isEmpty(UserUtils.getUserInfo()
                    .getAvator_url())) {
                DisplayImageOptions options = new DisplayImageOptions.Builder().cacheInMemory
                        (true)/*缓存至内存*/
                        .cacheOnDisk(true)/*缓存值SDcard*/
                        .bitmapConfig(Bitmap.Config.RGB_565)
                        .build();
                ImageLoader.getInstance()
                        .displayImage(UserUtils.getUserInfo()
                                        .getAvator_url(),
                                civMefrgAvator,
                                options,
                                new ImageLoadingListener() {
                                    @Override
                                    public void onLoadingStarted(String s, View view) {

                                    }

                                    @Override
                                    public void onLoadingFailed(
                                            String s,
                                            View view,
                                            FailReason failReason) {
                                        civMefrgAvator.setImageResource(R.drawable
                                                .icon_user_default);
                                    }

                                    @Override
                                    public void onLoadingComplete(
                                            String s,
                                            View view,
                                            Bitmap bitmap) {

                                    }

                                    @Override
                                    public void onLoadingCancelled(String s, View view) {

                                    }
                                });
            } else {
                civMefrgAvator.setImageResource(R.drawable.icon_user_default);
            }
        } else {
            civMefrgAvator.setImageResource(R.drawable.icon_user_default);
        }
    }

    private void initMefrgBalance() {

        if (null != user) {
            RequestParams params = new RequestParams(Configurations.URL_CHECK_TOKEN);
            params.addParameter(Configurations.AUTH_TOKEN,
                    UserUtils.getUserInfo()
                            .getAuth_token());

            String timeStamp = TimeUtil.getCurrentTimeString();

            String device_id = JPushInterface.getRegistrationID(getContext());
            params.addParameter(Configurations.TIMESTAMP, timeStamp);
            params.addParameter(Configurations.DEVICE_ID, device_id);

            Map<String, String> map = new TreeMap<>();
            map.put(Configurations.AUTH_TOKEN,
                    UserUtils.getUserInfo()
                            .getAuth_token());
            params.addParameter(Configurations.SIGN,
                    com.hzjytech.coffeeme.utils.SignUtils.createSignString(device_id,
                            timeStamp,
                            map));

            x.http()
                    .get(params, new Callback.CommonCallback<JSONObject>() {
                        @Override
                        public void onSuccess(JSONObject result) {

                            try {
                                if (result.getInt(Configurations.STATUSCODE) == 200) {
                                    String balance = result.getJSONObject("results")
                                            .getJSONObject("user")
                                            .getString("balance");
                                    btnMefrgBalance.setText(df.format(Float.valueOf(balance)));
                                } else {
                                    btnMefrgBalance.setText("0.00");
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
        } else {
            btnMefrgBalance.setText("0.00");
        }
    }


    private void initMefrgCoupon() {

        if (null == user) {
            btnMefrgCoupon.setText("0张优惠券");
            return;
        }
        //获取优惠券数量
        String authToken = user.getAuth_token();
        RequestParams entity = new RequestParams(Configurations.URL_COUPONS);
        entity.addParameter(Configurations.TOKEN, authToken);
        entity.addParameter(Configurations.TYPE,"coupon");
        String device_id = JPushInterface.getRegistrationID(getContext());
        String timeStamp = TimeUtil.getCurrentTimeString();
        entity.addParameter(Configurations.TIMESTAMP, timeStamp);
        entity.addParameter(Configurations.DEVICE_ID, device_id);

        Map<String, String> map = new TreeMap<String, String>();
        map.put(Configurations.TOKEN, authToken);
        map.put(Configurations.TYPE,"coupon");
        entity.addParameter(Configurations.SIGN,
                SignUtils.createSignString(device_id, timeStamp, map));

        x.http()
                .get(entity, new Callback.CommonCallback<String>() {
                    @Override
                    public void onSuccess(String result) {
                        try {
                            JSONObject object = new JSONObject(result);
                            if (object.getInt(Configurations.STATUSCODE) == 200) {
                                List<Coupon> coupons = new Gson().fromJson(object.getJSONObject(
                                        "results")
                                                .getString("coupons"),
                                        new TypeToken<ArrayList<Coupon>>() {}.getType());
                                int num = 0;
                                for (Coupon coupon : coupons) {
                                    String start_date = coupon.getStart_date();
                                    String end_date = coupon.getEnd_date();
                                    if ((start_date != null && !start_date.equals("") &&
                                            DateTimeUtil.after(
                                            start_date,
                                            System.currentTimeMillis()) || (end_date != null &&
                                            !end_date.equals(
                                            "") && DateTimeUtil.before(end_date,
                                            System.currentTimeMillis())))) {
                                       LogUtil.e("coupon",coupon.toString());
                                    }else{
                                        num++;
                                    }
                                }
                                btnMefrgCoupon.setText(num + "张优惠券");
                            } else {
                                btnMefrgCoupon.setText(0 + "张优惠券");
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
    //获取兑换券数量
    private void initMeExgCoupon() {

        if (null == user) {
            btnMeExgCoupon.setText("0张兑换券");
            return;
        }
        //获取优惠券数量
        String authToken = user.getAuth_token();
        RequestParams entity = new RequestParams(Configurations.URL_COUPONS);
        entity.addParameter(Configurations.TOKEN, authToken);
        entity.addParameter(Configurations.TYPE,"redeem");
        String device_id = JPushInterface.getRegistrationID(getContext());
        String timeStamp = TimeUtil.getCurrentTimeString();
        entity.addParameter(Configurations.TIMESTAMP, timeStamp);
        entity.addParameter(Configurations.DEVICE_ID, device_id);

        Map<String, String> map = new TreeMap<String, String>();
        map.put(Configurations.TOKEN, authToken);
        map.put(Configurations.TYPE,"redeem");
        entity.addParameter(Configurations.SIGN,
                SignUtils.createSignString(device_id, timeStamp, map));

        x.http()
                .get(entity, new Callback.CommonCallback<String>() {
                    @Override
                    public void onSuccess(String result) {
                        try {
                            JSONObject object = new JSONObject(result);
                            if (object.getInt(Configurations.STATUSCODE) == 200) {
                                List<Coupon> coupons = new Gson().fromJson(object.getJSONObject(
                                        "results")
                                                .getString("coupons"),
                                        new TypeToken<ArrayList<Coupon>>() {}.getType());
                                int num = 0;
                                for (Coupon coupon : coupons) {
                                    String start_date = coupon.getStart_date();
                                    String end_date = coupon.getEnd_date();
                                    if ((start_date != null && !start_date.equals("") &&
                                            DateTimeUtil.after(
                                            start_date,
                                            System.currentTimeMillis()) || (end_date != null &&
                                            !end_date.equals(
                                            "") && DateTimeUtil.before(end_date,
                                            System.currentTimeMillis())))) {
                                       LogUtil.e("coupon",coupon.toString());
                                    }else{
                                        num++;
                                    }
                                }
                                btnMeExgCoupon.setText(num + "张兑换券");
                            } else {
                                btnMefrgCoupon.setText(0 + "张兑换券");
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

    private void checkCouponsIsOutOfTime() {
        if (application.getHasShowDialog()) {

        } else {
            loadCoupons();
        }

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
                        showOutOfTimeCoupouDialog(difDays + "天",coupons.get(i).getCoupon_type());
                        SharedPrefUtil.putLong("nowday",
                                Calendar.getInstance()
                                        .getTimeInMillis());
                        break;
                    }
                }

            } else {
                ToastUtil.showShort(getActivity(), object.getString(Configurations.STATUSMSG));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    //弹出优惠券过期提示弹窗
    private void showOutOfTimeCoupouDialog(String date, final int coupon_type) {
        final NewOutTimeCouponDialog newOutTimeCouponDialog = NewOutTimeCouponDialog.newInstance(
                date);
        newOutTimeCouponDialog.setOnTwoButtonClick(new ITwoButtonClick() {
            @Override
            public void onLeftButtonClick() {
                newOutTimeCouponDialog.dismiss();
                application.setHasShowDialog(false);
            }

            @Override
            public void onRightButtonClick() {
                Intent intent = new Intent(getActivity(), MyCouponActivity.class);
                if(coupon_type==4){
                    intent.putExtra("type","redeem");
                }else {   intent.putExtra("type","coupon");}
                startActivity(intent);
                newOutTimeCouponDialog.dismiss();
                application.setHasShowDialog(false);
            }
        });
        newOutTimeCouponDialog.show(getFragmentManager(), "outTimeDialog");
        application.setHasShowDialog(true);
    }

}