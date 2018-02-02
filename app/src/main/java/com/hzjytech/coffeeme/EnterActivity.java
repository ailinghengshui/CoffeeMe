package com.hzjytech.coffeeme;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hzjytech.coffeeme.configurations.Configurations;
import com.hzjytech.coffeeme.configurations.UmengConfig;
import com.hzjytech.coffeeme.culture.CultureDetailActivity;
import com.hzjytech.coffeeme.utils.LogUtil;
import com.hzjytech.coffeeme.utils.SharedPrefUtil;
import com.hzjytech.coffeeme.utils.SignUtils;
import com.hzjytech.coffeeme.utils.TimeUtil;
import com.hzjytech.coffeeme.utils.UserUtils;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.HttpMethod;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.sql.Time;
import java.util.Map;
import java.util.TreeMap;

import cn.jpush.android.api.JPushInterface;

/**
 * Created by Hades on 2016/1/26.
 */

public class EnterActivity extends AppCompatActivity {

    private static final String URL_ARTICLE ="url_article" ;

    private TextView timeView;
    private TextView jumpView;
    private ImageView mWelcomeView;
    private Context mContext;

    private String url_article;
    private boolean isjump;//跳转广告详情

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {

            Intent intent = new Intent(EnterActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mContext = this;
        isjump = false;
        initWelcomePage();

        MobclickAgent.setDebugMode(true);
        MobclickAgent.openActivityDurationTrack(false);
        MobclickAgent.setScenarioType(this, MobclickAgent.EScenarioType.E_UM_NORMAL);
        //解决home键按下后退出程序而不是后台运行的错误
        if (!isTaskRoot()) {
            finish();
            return;
        }
    }

    private void initWelcomePage(){

        RelativeLayout layout = new RelativeLayout(this);
        mWelcomeView = new ImageView(this);
        mWelcomeView.setScaleType(ImageView.ScaleType.FIT_XY);
        mWelcomeView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        mWelcomeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(null == url_article)
                    return;

                isjump = true;
                Intent intent = new Intent(EnterActivity.this, MainActivity.class);
                startActivity(intent);
                finish();

                MobclickAgent.onEvent(EnterActivity.this,UmengConfig.EVENT_ENTER_CLICK);
                intent=new Intent(EnterActivity.this, CultureDetailActivity.class);
                intent.putExtra(URL_ARTICLE,url_article);
                startActivity(intent);
            }
        });

        LinearLayout hintLayout=new LinearLayout(this);
//        hintLayout.setBackgroundResource(R.drawable.bg_circ_rec_half_trans);
        hintLayout.setOrientation(LinearLayout.HORIZONTAL);
        hintLayout.setGravity(Gravity.CENTER);
        RelativeLayout.LayoutParams hintParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        hintParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        hintParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        hintParams.topMargin = getResources().getDimensionPixelSize(R.dimen.mybanlance_rectitleh);
        hintParams.rightMargin = getResources().getDimensionPixelSize(R.dimen.mybanlance_rectitleh);
        hintLayout.setLayoutParams(hintParams);

        timeView = new TextView(mContext);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams((int) getResources().getDimension(R.dimen.mybanlance_header_h),
                (int) getResources().getDimension(R.dimen.mybanlance_btnh));
//        params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
//        params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
//        params.topMargin = getResources().getDimensionPixelSize(R.dimen.mybanlance_rectitleh);
//        params.rightMargin = getResources().getDimensionPixelSize(R.dimen.mybanlance_header_h);
        timeView.setLayoutParams(params);
        timeView.setText( 3 + "秒");
        timeView.setTextColor(Color.WHITE);
        timeView.setTextSize(16);
        timeView.setGravity(Gravity.CENTER);
        timeView.setVisibility(View.GONE);


        jumpView = new TextView(mContext);
        RelativeLayout.LayoutParams param = new RelativeLayout.LayoutParams((int) getResources().getDimension(R.dimen.mybanlance_title_h),
                (int) getResources().getDimension(R.dimen.mybanlance_btnh));

//        param.addRule(RelativeLayout.ALIGN_PARENT_TOP);
//        param.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
//        param.topMargin = getResources().getDimensionPixelSize(R.dimen.mybanlance_rectitleh);
//        param.rightMargin = getResources().getDimensionPixelSize(R.dimen.mybanlance_item_padh);
        jumpView.setLayoutParams(param);
        jumpView.setText("跳过");
        jumpView.setTextColor(Color.WHITE);
        jumpView.setGravity(Gravity.CENTER);
        jumpView.setTextSize(16);
        jumpView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                isjump = true;
                Intent intent = new Intent(EnterActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
        jumpView.setVisibility(View.GONE);

        hintLayout.setBackgroundResource(R.drawable.bg_circ_rec_black_trans);
        hintLayout.addView(timeView);
        hintLayout.addView(jumpView);
        layout.addView(mWelcomeView);
        layout.addView(hintLayout);

        setContentView(layout);

        checkToken();
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(600);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                getLoadPage();
            }
        });
    }


    private void checkToken(){

        if(!SharedPrefUtil.getLoginType().equals(SharedPrefUtil.LOGINING))
            return;

        String token  = UserUtils.getUserInfo().getAuth_token();
        RequestParams params = new RequestParams(Configurations.URL_CHECK_TOKEN);
        params.addParameter(Configurations.AUTH_TOKEN, token);

        long timeStamp= TimeUtil.getCurrentTime();

        String device_id=JPushInterface.getRegistrationID(EnterActivity.this);
        params.addParameter(Configurations.TIMESTAMP, String.valueOf(timeStamp));
        params.addParameter(Configurations.DEVICE_ID,device_id );

        Map<String,String> map=new TreeMap<>();
        map.put(Configurations.AUTH_TOKEN, token);
        params.addParameter(Configurations.SIGN, SignUtils.createSignString(device_id,timeStamp,map));

        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {

                try {
                    int statusCode= new JSONObject(result).getInt("statusCode");
                    if(statusCode==200){

//                        SharedPrefUtil.loginSuccess(SharedPrefUtil.LOGIN_VIA_PHONE);
                    }else{

                        SharedPrefUtil.loginout();
                        SharedPrefUtil.saveAvatorUrl("");
                        SharedPrefUtil.saveUri("");
                        SharedPrefUtil.saveWeiboId("");
                        UserUtils.saveUserInfo(null);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {

                //showNetError();
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
        MobclickAgent.onPageStart(UmengConfig.ENTERACTIVITY);
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(UmengConfig.ENTERACTIVITY);
        MobclickAgent.onPause(this);
    }

    private void getLoadPage() {

        RequestParams entity = new RequestParams(Configurations.URL_AD);

        long timeStamp= TimeUtil.getCurrentTime();


        String device_id=JPushInterface.getRegistrationID(EnterActivity.this);
        entity.addParameter(Configurations.SIGN, SignUtils.createSignString(device_id,timeStamp));
        entity.addParameter(Configurations.TIMESTAMP, String.valueOf(timeStamp));
        entity.addParameter(Configurations.DEVICE_ID,device_id );

        x.http().request(HttpMethod.GET, entity, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {

                parseLoadResult(result);
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                //showNetError();
            }

            @Override
            public void onCancelled(CancelledException cex) {
            }

            @Override
            public void onFinished() {
            }
        });
    }


    private void parseLoadResult(String result) {
        try{
            JSONObject jsonObject = new JSONObject(result);
            if(jsonObject.getInt(Configurations.STATUSCODE) == 200) {

                String imgurl = jsonObject.getJSONObject("results").getJSONObject("banner").getString("image_url");

                url_article = jsonObject.getJSONObject("results").getJSONObject("banner").getString("article_url");

                DisplayImageOptions options = new DisplayImageOptions.Builder()
                        .cacheInMemory(true)/*缓存至内存*/
                        .cacheOnDisk(true)/*缓存值SDcard*/
                        .bitmapConfig(Bitmap.Config.RGB_565)
                        .build();
                ImageLoader.getInstance().displayImage(imgurl, mWelcomeView, options);
                timeView.setVisibility(View.VISIBLE);
                jumpView.setVisibility(View.VISIBLE);
                new TimeCountDownTask(timeView, 3).execute();

            } else {
                LogUtil.i("","---STATUSMSG---"+jsonObject.getString(Configurations.STATUSMSG));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    private class TimeCountDownTask extends AsyncTask<Void, Void, Boolean> {
        TextView timeView;
        int limit_time = 0;

        TimeCountDownTask(TextView timeView, int time) {
            this.timeView = timeView;
            this.limit_time = time;
        }

        @SuppressWarnings("WrongThread")
        @Override
        protected Boolean doInBackground(Void... params) {
            while (limit_time > 0) {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        timeView.setText( limit_time + "秒");
                    }
                });
                try{
                    Thread.sleep(1000);
                } catch (Exception e){
                    e.printStackTrace();
                }
                limit_time--;
                if(limit_time == 0){
                    if(!isjump)
                        mHandler.sendEmptyMessage(0);
                }
            }
            return null;
        }
    }

}