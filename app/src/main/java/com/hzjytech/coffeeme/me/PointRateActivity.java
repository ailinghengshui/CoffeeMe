package com.hzjytech.coffeeme.me;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.Transformation;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.hzjytech.coffeeme.BaseActivity;
import com.hzjytech.coffeeme.R;
import com.hzjytech.coffeeme.configurations.Configurations;
import com.hzjytech.coffeeme.configurations.UmengConfig;
import com.hzjytech.coffeeme.entities.User;
import com.hzjytech.coffeeme.utils.SharedPrefUtil;
import com.hzjytech.coffeeme.utils.TimeUtil;
import com.hzjytech.coffeeme.utils.ToastUtil;
import com.hzjytech.coffeeme.utils.UserUtils;
import com.hzjytech.coffeeme.widgets.TitleBar;
import com.hzjytech.coffeeme.widgets.ropeprogressbar.RopeProgressBar;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.Map;
import java.util.TreeMap;

import cn.jpush.android.api.JPushInterface;

@ContentView(R.layout.activity_point_rate)
public class PointRateActivity extends BaseActivity {

    @ViewInject(R.id.titleBar)
    private TitleBar titleBar;

    @ViewInject(R.id.tvPointrateName)
    private TextView tvPointrateName;

    @ViewInject(R.id.ivPointrateImage)
    private ImageView ivPointrateImage;

    @ViewInject(R.id.ropeProBar)
    private RopeProgressBar ropeProgressBar;

    @ViewInject(R.id.tvPointrateMin)
    private TextView tvPointrateMin;

    @ViewInject(R.id.tvPointrateMax)
    private TextView tvPointrateMax;

    @ViewInject(R.id.tvPointMin)
    private TextView tvPointMin;

    @ViewInject(R.id.tvPointMax)
    private TextView tvPointMax;
    
    @ViewInject(R.id.tvPointratePoint)
    private TextView tvPointratePoint;
    private User user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initTitle();
    }


    private void initData(){
        showLoading();
        RequestParams entity=new RequestParams(Configurations.URL_CHECK_TOKEN);
        if(UserUtils.getUserInfo()!=null) {
            entity.addParameter(Configurations.AUTH_TOKEN, UserUtils.getUserInfo().getAuth_token());

            String timeStamp= TimeUtil.getCurrentTimeString();
            String device_id= JPushInterface.getRegistrationID(PointRateActivity.this);
            entity.addParameter(Configurations.TIMESTAMP,timeStamp);
            entity.addParameter(Configurations.DEVICE_ID,device_id );

            Map<String,String> map=new TreeMap<>();
            map.put(Configurations.AUTH_TOKEN, UserUtils.getUserInfo().getAuth_token());
            entity.addParameter(Configurations.SIGN, com.hzjytech.coffeeme.utils.SignUtils.createSignString(device_id,timeStamp,map));
        }

        x.http().get(entity, new Callback.CommonCallback<JSONObject>() {
            @Override
            public void onSuccess(JSONObject result) {
                hideLoading();
                checkResOld(result);
                try {
                    if(result.getInt(Configurations.STATUSCODE)==200){
                        user = JSON.parseObject(result.getJSONObject("results").getString("user"), User.class);
                        UserUtils.saveUserInfo(user);
                        initRateName();
                        initRateImage();
                        initProgress();
                        initRateScope();
                        initScope();
                        initPoint();
                    }else{
                        ToastUtil.showShort(PointRateActivity.this,result.getString(Configurations.STATUSMSG));
                        goLogin();
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

            }
        });
    }

    private void initPoint() {
        if(user!=null){
            tvPointratePoint.setText(getPointString(user.getPoint()));
        }else {
            tvPointratePoint.setText(getPointString(0));
        }
    }

    private String getPointString(int point) {
        return ""+point;
    }


    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(UmengConfig.POINTRATEACTIVITY);
        MobclickAgent.onResume(this);

         initData();
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(UmengConfig.POINTRATEACTIVITY);
        MobclickAgent.onPause(this);
    }

    private void initRateName() {

        if (user != null&&user.getUser_level()!=null) {
            if (!TextUtils.isEmpty(user.getUser_level().getLevel_name())) {
                tvPointrateName.setText(user.getUser_level().getLevel_name());
            } else {
                tvPointrateName.setText("");
            }
        } else {
            tvPointrateName.setText("");
        }

    }

    private void initScope() {
        if (user!=null&&user.getUser_level() != null) {
            tvPointMin.setText(getScopeText(user.getUser_level().getStart()));
            tvPointMax.setText(getScopeText(user.getUser_level().getEnd()));
        } else {
            tvPointMin.setText(getScopeText(0));
            tvPointMax.setText(getScopeText(0));
        }

    }

    private String getScopeText(int i) {
        return "" + i;
    }

    private void initRateScope() {
        Typeface font = Typeface.createFromAsset(getAssets(), "fonts/mlsjn.ttf");
        if(user!=null&&user.getUser_level()!=null){
            tvPointrateMin.setText(getRateScopeText(user.getUser_level().getLevel()));
            tvPointrateMax.setText(getRateScopeText(user.getUser_level().getLevel() + 1));
        }else{
            tvPointrateMin.setText(getRateScopeText(0));
            tvPointrateMax.setText(getRateScopeText(0));
        }
        tvPointrateMin.setTypeface(font);
        tvPointrateMax.setTypeface(font);

    }

    private String getRateScopeText(int start) {
        return "V" + start;
    }

    private void initProgress() {
        ropeProgressBar.setPrimaryColor(getResources().getColor(R.color.orange));
//        ropeProgressBar.setSecondaryColor(Color.TRANSPARENT);

        this.ropeProgressBar.setStrokeWidth(15);//设置进度条的宽度
        this.ropeProgressBar.setSlack(0);//进度条行进过程中下坠的最大高度(松弛的最大值), 即当为50%时的值
        this.ropeProgressBar.setDynamicLayout(false);//设置进度条行进过程中，控件的高度是否随着Slack值的变化而变化
        if (user != null) {
            if (user.getUser_level() != null) {

                ropeProgressBar.setMax(user.getUser_level().getEnd() - user.getUser_level().getStart());
                ropeProgressBar.setMin(user.getUser_level().getStart());
                ropeProgressBar.setProgress(user.getLevel() - user.getUser_level().getStart());
//            ropeProgressBar.setProgress(50);

                //----以下为模拟进度条的行进过程-------
            } else {
                ropeProgressBar.setMax(0);
                ropeProgressBar.setMin(0);
                ropeProgressBar.setProgress(0);
            }
        } else {
            ropeProgressBar.setMax(0);
            ropeProgressBar.setMin(0);
            ropeProgressBar.setProgress(0);

        }
        AutoAnimation animation = new AutoAnimation();
        animation.setInterpolator(new LinearInterpolator());
        animation.setDuration(50);
        this.ropeProgressBar.startAnimation(animation);

    }

    private class AutoAnimation extends Animation {
        @Override
        protected void applyTransformation(float interpolatedTime, Transformation t) {
            if (interpolatedTime <= 1) {
                if(user!=null&&user.getUser_level()!=null) {
                    ropeProgressBar.setProgress((int) ((user.getLevel() - user.getUser_level().getStart()) * interpolatedTime));
                }else{
                    ropeProgressBar.setProgress(0);
                }
//                ropeProgressBar.setProgress((int) ((50) * interpolatedTime));
            }
        }
    }

    private void initTitle() {
        titleBar.setTitle(R.string.string_pointrate);
        titleBar.setTitleColor(Color.WHITE);
        titleBar.setLeftClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        titleBar.setLeftImageResource(R.drawable.icon_left);
    }

    public void aboutPoint(View view) {
        startActivity(new Intent(PointRateActivity.this,AboutPointActivity.class));
    }

    @Event(R.id.llPointrateAvailablepoint)
    private void onAvailablePointClick(View view){
        startActivity(new Intent(PointRateActivity.this,PointRecordsActivity.class));

    }

    @Event(R.id.llPointratePointExchange)
    private void onPointExchangeClick(View view){
        SharedPrefUtil.saveIsFromCoupon(false);
        startActivity(new Intent(PointRateActivity.this,PointExchangeActivity.class));
    }

    private void initRateImage() {

        if (user != null) {
            if (!TextUtils.isEmpty(user.getUser_level().getImage())) {

                DisplayImageOptions options = new DisplayImageOptions.Builder()
                        .cacheInMemory(true)/*缓存至内存*/
                        .cacheOnDisk(true)/*缓存值SDcard*/
                        .bitmapConfig(Bitmap.Config.RGB_565)
                        .imageScaleType(ImageScaleType.EXACTLY)
                        .build();
                ImageLoader.getInstance().displayImage(user.getUser_level().getImage(), ivPointrateImage, options);
            }
        }
    }
}
