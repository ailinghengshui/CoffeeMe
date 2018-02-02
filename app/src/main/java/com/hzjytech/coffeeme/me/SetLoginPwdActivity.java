package com.hzjytech.coffeeme.me;


import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.alibaba.fastjson.JSON;
import com.hzjytech.coffeeme.BaseActivity;
import com.hzjytech.coffeeme.R;
import com.hzjytech.coffeeme.configurations.Configurations;
import com.hzjytech.coffeeme.configurations.UmengConfig;
import com.hzjytech.coffeeme.entities.User;
import com.hzjytech.coffeeme.utils.SharedPrefUtil;
import com.hzjytech.coffeeme.utils.SignUtils;
import com.hzjytech.coffeeme.utils.TextUtil;
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
@ContentView(R.layout.activity_set_login_pwd)
public class SetLoginPwdActivity extends BaseActivity {
    @ViewInject(R.id.etNewPsdPsd)
    private EditText etNewPsdPsd;

    @ViewInject(R.id.ivNewpsdfrgClear)
    private ImageView ivNewpsdfrgClear;

    @ViewInject(R.id.etNewPsdRePsd)
    private EditText etNewPsdRePsd;

    @ViewInject(R.id.ivNewpsdfrgReclear)
    private ImageView ivNewpsdfrgReclear;
    @ViewInject(R.id.titleBar)
    private TitleBar titleBar;
    private String newPsd;
    private String token;

    @Event(R.id.btnNewPsdOK)
    private void onNewPsdOKClick(View v){
        if(TextUtil.checkParams(etNewPsdPsd.getText().toString(),etNewPsdRePsd.getText().toString())){

            if(etNewPsdPsd.getText().toString().trim().length()>5&&etNewPsdRePsd.getText().toString().trim().length()>5){
                if(etNewPsdPsd.getText().toString().equals(etNewPsdRePsd.getText().toString())){
                    newPsd = etNewPsdPsd.getText().toString();
                    checkToken();
                }else{
                    ToastUtil.showShort(this,"两次密码不一致");
                }
            }else{
                ToastUtil.showShort(this,"请输入至少六位密码");
            }


        }else{
            ToastUtil.showShort(this,"密码不能为空");
        }

    }
    private void checkToken() {
        token = UserUtils.getUserInfo().getAuth_token();
        RequestParams params = new RequestParams(Configurations.URL_CHECK_TOKEN);
        params.addParameter(Configurations.AUTH_TOKEN, token);

        long timeStamp = TimeUtil.getCurrentTime();
        String device_id = JPushInterface.getRegistrationID(this);
        params.addParameter(Configurations.TIMESTAMP, String.valueOf(timeStamp));
        params.addParameter(Configurations.DEVICE_ID, device_id);

        Map<String, String> map = new TreeMap<>();
        map.put(Configurations.AUTH_TOKEN, token);
        params.addParameter(Configurations.SIGN, SignUtils.createSignString(device_id, timeStamp, map));

        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                boolean isAuthTokenValid = parseResult(result);
                if (isAuthTokenValid) {
                    goReg();
                }
            }
            private void goReg() {
                RequestParams entity = new RequestParams(Configurations.URL_SET_PASSWORD);

                String device_id = JPushInterface.getRegistrationID(SetLoginPwdActivity.this);

                entity.addParameter(Configurations.TOKEN, token);
                entity.addParameter(Configurations.PASSWORD,newPsd);
                entity.addParameter(Configurations.REG_ID, device_id);

                long timeStamp = TimeUtil.getCurrentTime();
                entity.addParameter(Configurations.TIMESTAMP, String.valueOf(timeStamp));
                entity.addParameter(Configurations.DEVICE_ID, device_id);

                Map<String, String> map = new TreeMap<>();
                map.put(Configurations.TOKEN, token);
                map.put(Configurations.PASSWORD, newPsd);
                map.put(Configurations.REG_ID, device_id);

                entity.addParameter(Configurations.SIGN, SignUtils.createSignString(device_id, timeStamp, map));

                x.http().request(HttpMethod.PUT, entity, new Callback.CommonCallback<String>() {
                    @Override
                    public void onSuccess(String result) {
                        try {
                            JSONObject object = new JSONObject(result);
                            if (object.getInt(Configurations.STATUSCODE) == 200) {

                                MobclickAgent.onEvent(SetLoginPwdActivity.this, UmengConfig.EVENT_REGISTER);
                                User user = JSON.parseObject(object.getJSONObject("results").getString("user"), User.class);
                                UserUtils.saveUserInfo(user);
                                UserUtils.saveLocalMobileAndPwd(user.getPhone(), "");
                                ToastUtil.showShort(SetLoginPwdActivity.this,"手机绑定成功");
                                SetLoginPwdActivity.this.finish();

                            } else {
                                ToastUtil.showShort(SetLoginPwdActivity.this, object.getString(Configurations.STATUSMSG));
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
    private boolean parseResult(String result) {
        boolean isValid = false;
        try {
            int statusCode = new JSONObject(result).getInt("statusCode");

            if (statusCode == 200) {
                isValid = true;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return isValid;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        titleBar.setTitle("设置登录密码");
        titleBar.setTitleColor(Color.WHITE);
        titleBar.setLeftImageResource(R.drawable.icon_left);
        titleBar.setLeftClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
         etNewPsdPsd.addTextChangedListener(mTextWathcer);
         etNewPsdRePsd.addTextChangedListener(mReTextWathcer);

    }

    @Override
    protected void onResume() {
        super.onResume();

    }
    private TextWatcher mTextWathcer=new TextWatcher(){

        private CharSequence temp;

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            temp=s;

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {

            if(temp.length()>0){
                ivNewpsdfrgClear.setVisibility(View.VISIBLE);
                ivNewpsdfrgClear.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        etNewPsdPsd.setText("");
                        ivNewpsdfrgClear.setVisibility(View.GONE);
                    }
                });
            }

            if(temp.length()==0){
                ivNewpsdfrgClear.setVisibility(View.GONE);
            }
        }
    };

    private TextWatcher mReTextWathcer=new TextWatcher(){

        private CharSequence temp;

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            temp=s;

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {

            if(temp.length()>0){
                ivNewpsdfrgReclear.setVisibility(View.VISIBLE);
                ivNewpsdfrgReclear.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        etNewPsdRePsd.setText("");
                        ivNewpsdfrgReclear.setVisibility(View.GONE);
                    }
                });
            }

            if(temp.length()==0){
                ivNewpsdfrgReclear.setVisibility(View.GONE);
            }
        }
    };
}
