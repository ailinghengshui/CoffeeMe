package com.hzjytech.coffeeme.me;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.hzjytech.coffeeme.BaseActivity;
import com.hzjytech.coffeeme.R;
import com.hzjytech.coffeeme.configurations.Configurations;
import com.hzjytech.coffeeme.configurations.UmengConfig;
import com.hzjytech.coffeeme.utils.LogUtil;
import com.hzjytech.coffeeme.utils.NetUtil;
import com.hzjytech.coffeeme.utils.SignUtils;
import com.hzjytech.coffeeme.utils.StringUtil;
import com.hzjytech.coffeeme.utils.TimeUtil;
import com.hzjytech.coffeeme.utils.ToastUtil;
import com.hzjytech.coffeeme.utils.UserUtils;
import com.hzjytech.coffeeme.widgets.TitleBar;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.HttpMethod;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.Map;
import java.util.TreeMap;

import cn.jpush.android.api.JPushInterface;

@ContentView(R.layout.activity_binding_phone)
public class BindingPhoneActivity extends BaseActivity {


    @ViewInject(R.id.titleBar)
    private TitleBar tbBindingphoneTitle;

    @ViewInject(R.id.etBindingphonePhone)
    private EditText etBindingphonePhone;

    @ViewInject(R.id.btnBindingphoneSmscode)
    private Button btnBindingphoneSmscode;

    @ViewInject(R.id.etBindingphoneSmscode)
    private EditText etBindingphoneSmscode;

    @ViewInject(R.id.ivOldpsdfrgClear)
    private ImageView ivOldpsdfrgClear;
    private TimeCount timeCount;
    @ViewInject(R.id.btnBindingphone)
    private Button btnBindingphone;

    @Event(R.id.btnBindingphoneSmscode)
    private void onBindingphoneSmsCode(View view) {
        String trimPhone = etBindingphonePhone.getText().toString().trim();
        if (!TextUtils.isEmpty(trimPhone)&& StringUtil.isMobile(trimPhone)) {
            if (NetUtil.isNetworkAvailable(BindingPhoneActivity.this)) {
                RequestParams entity = new RequestParams(Configurations.URL_SEND_SMS_CODE);
                entity.addParameter(Configurations.PHONE, etBindingphonePhone.getText().toString().trim());
                entity.addParameter(Configurations.TYPE, "bind_phone");
                entity.addParameter(Configurations.AUTH_TOKEN, UserUtils.getUserInfo().getAuth_token());

                String device_id = JPushInterface.getRegistrationID(BindingPhoneActivity.this);
                String timeStamp = TimeUtil.getCurrentTimeString();
                entity.addParameter(Configurations.TIMESTAMP, timeStamp);
                entity.addParameter(Configurations.DEVICE_ID, device_id);

                Map<String, String> map = new TreeMap<>();
                map.put(Configurations.PHONE, etBindingphonePhone.getText().toString().trim());
                map.put(Configurations.TYPE, "bind_phone");
                map.put(Configurations.AUTH_TOKEN, UserUtils.getUserInfo().getAuth_token());

                entity.addParameter(Configurations.SIGN, SignUtils.createSignString(device_id, timeStamp, map));


                x.http().post(entity, new Callback.CommonCallback<String>() {
                    @Override
                    public void onSuccess(String result) {
                        LogUtil.e("smsresult",result);
                        try {
                            JSONObject object = new JSONObject(result);
                            checkResOld(object);
                            if(object.getInt(Configurations.STATUSCODE)==200){
                                timeCount = new TimeCount(60000, 1000);
                                timeCount.start();
                            }
                            ToastUtil.showShort(BindingPhoneActivity.this, object.getString(Configurations.STATUSMSG));
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
        } else {
            ToastUtil.showShort(BindingPhoneActivity.this, "请输入正确的手机号码");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(UmengConfig.BINDINGPHONEACTIVITY);
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(UmengConfig.BINDINGPHONEACTIVITY);
        MobclickAgent.onPause(this);
    }

    @Event(R.id.btnBindingphone)
    private void onBindingphoneClick(View view) {
        String trimPhone = etBindingphonePhone.getText().toString().trim();
        if(TextUtils.isEmpty(trimPhone)|| !StringUtil.isMobile(trimPhone)){
            ToastUtil.showShort(BindingPhoneActivity.this, "请输入正确的手机号码");
            return;
        }

        RequestParams entity = new RequestParams(Configurations.URL_BINDING_PHONE);
        entity.addParameter(Configurations.AUTH_TOKEN, UserUtils.getUserInfo().getAuth_token());
        entity.addParameter(Configurations.PHONE, etBindingphonePhone.getText().toString().trim());
        entity.addParameter(Configurations.SMS_CODE, etBindingphoneSmscode.getText().toString().trim());

        String device_id = JPushInterface.getRegistrationID(BindingPhoneActivity.this);
        String timeStamp = TimeUtil.getCurrentTimeString();
        entity.addParameter(Configurations.TIMESTAMP, timeStamp);
        entity.addParameter(Configurations.DEVICE_ID, device_id);

        Map<String, String> map = new TreeMap<String, String>();
        map.put(Configurations.AUTH_TOKEN, UserUtils.getUserInfo().getAuth_token());
        map.put(Configurations.PHONE, etBindingphonePhone.getText().toString().trim());
        map.put(Configurations.SMS_CODE, etBindingphoneSmscode.getText().toString().trim());
        entity.addParameter(Configurations.SIGN, SignUtils.createSignString(device_id, timeStamp, map));


        x.http().request(HttpMethod.PUT, entity, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                try {
                    JSONObject object = new JSONObject(result);
                    checkResOld(object);
                    if (object.getInt(Configurations.STATUSCODE) == 200) {
                        if(UserUtils.getUserInfo().getPhone()!=null){
                            UserUtils.getUserInfo().setPhone(etBindingphonePhone.getText().toString().trim());
                            ToastUtil.showShort(BindingPhoneActivity.this, object.getString(Configurations.STATUSMSG));
                        }
                       else {
                            UserUtils.getUserInfo().setPhone(etBindingphonePhone.getText().toString().trim());
                        Intent intent = new Intent(BindingPhoneActivity.this, SetLoginPwdActivity.class);
                        startActivity(intent);}
                        finish();
                    } else {
                        ToastUtil.showShort(BindingPhoneActivity.this, object.getString(Configurations.STATUSMSG));
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

    class TimeCount extends CountDownTimer {

        /**
         * @param millisInFuture    The number of millis in the future from the call
         *                          to {@link #start()} until the countdown is done and {@link #onFinish()}
         *                          is called.
         * @param countDownInterval The interval along the way to receive
         *                          {@link #onTick(long)} callbacks.
         */
        public TimeCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            btnBindingphoneSmscode.setClickable(false);
            btnBindingphoneSmscode.setText(((int) millisUntilFinished / 1000) + "秒");
        }

        @Override
        public void onFinish() {
            btnBindingphoneSmscode.setText("重新发送验证码");
            btnBindingphoneSmscode.setClickable(true);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        tbBindingphoneTitle.setTitle("绑定手机");
        tbBindingphoneTitle.setTitleColor(Color.WHITE);
        tbBindingphoneTitle.setLeftImageResource(R.drawable.icon_left);
        tbBindingphoneTitle.setLeftClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        if(UserUtils.getUserInfo().getPhone()!=null){
            btnBindingphone.setText("完成");
            tbBindingphoneTitle.setTitle("换绑手机");
        }

        etBindingphonePhone.addTextChangedListener(new TextWatcher() {

            private CharSequence temp;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                temp = s;

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                if (temp.length() > 0) {
                    ivOldpsdfrgClear.setVisibility(View.VISIBLE);
                    ivOldpsdfrgClear.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            etBindingphonePhone.setText("");
                            ivOldpsdfrgClear.setVisibility(View.INVISIBLE);
                        }
                    });
                }

                if (temp.length() == 0) {
                    ivOldpsdfrgClear.setVisibility(View.INVISIBLE);
                }
            }
        });
    }
}