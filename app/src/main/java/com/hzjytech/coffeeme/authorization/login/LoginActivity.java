package com.hzjytech.coffeeme.authorization.login;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.google.gson.Gson;
import com.hzjytech.coffeeme.BaseActivity;
import com.hzjytech.coffeeme.Dialogs.HintDialog;
import com.hzjytech.coffeeme.R;
import com.hzjytech.coffeeme.authorization.register.RegisterActivity;
import com.hzjytech.coffeeme.configurations.Configurations;
import com.hzjytech.coffeeme.configurations.UmengConfig;
import com.hzjytech.coffeeme.entities.AccessToken;
import com.hzjytech.coffeeme.entities.User;
import com.hzjytech.coffeeme.entities.UserInfoWX;
import com.hzjytech.coffeeme.utils.AppUtil;
import com.hzjytech.coffeeme.utils.LogUtil;
import com.hzjytech.coffeeme.utils.SharedPrefUtil;
import com.hzjytech.coffeeme.utils.SignUtils;
import com.hzjytech.coffeeme.utils.TextUtil;
import com.hzjytech.coffeeme.utils.TimeUtil;
import com.hzjytech.coffeeme.utils.ToastUtil;
import com.hzjytech.coffeeme.utils.UserUtils;
import com.hzjytech.coffeeme.wechatmanager.WeChatManager;
import com.hzjytech.coffeeme.weibomanager.AuthListener;
import com.hzjytech.coffeeme.widgets.MyEditText;
import com.hzjytech.coffeeme.widgets.TitleBar;
import com.sina.weibo.sdk.auth.AuthInfo;
import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.RequestListener;
import com.sina.weibo.sdk.openapi.models.ErrorInfo;
import com.tencent.mm.sdk.constants.ConstantsAPI;
import com.tencent.mm.sdk.modelmsg.SendAuth;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.HttpMethod;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.x;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import cn.jpush.android.api.JPushInterface;


/**
 * Created by Hades on 2016/1/26.
 */
@ContentView(R.layout.activity_login)
public class LoginActivity extends BaseActivity {

    private final static String TAG = LoginActivity.class.getSimpleName();
    // url for get access_token
    public static String sURLAccessToken = "https://api.weixin.qq.com/sns/oauth2/access_token?" +
            "appid=APPID&secret=SECRET&code=CODE&grant_type=authorization_code";
    //url fot get userinfo
    public static String sURLUserInfo = "https://api.weixin.qq.com/sns/userinfo?" +
            "access_token=ACCESS_TOKEN&openid=OPENID";
    //url for refresh access token
    public static String sURLRefreshAccessToken = "https://api.weixin.qq.com/sns/oauth2/refresh_token?" +
            "appid=APPID&grant_type=refresh_token&refresh_token=REFRESH_TOKEN";
    //url for check access token
    public static String sURLValidAccessToken = "https://api.weixin.qq.com/sns/auth?" +
            "access_token=ACCESS_TOKEN&openid=OPENID";

    public static final String APP_KEY = "3812342993";
    public static final String REDIRECT_URL = "https://api.weibo.com/oauth2/default.html";

    public static final String SCOPE = "email,direct_messages_read,direct_messages_write,"
            + "friendships_groups_read,friendships_groups_write,statuses_to_me_read,"
            + "follow_app_official_microblog," + "invitation_write";

    private TitleBar tbLoginTitle;
    private EditText metLoginTel;
    private EditText metLoginPsd;

    private static IWXAPI WXapi;
    private String token;
    private AccessToken accessToken;
    private UserInfoWX userinfo;

    private Button btnLoginRegWeibo;
    private AuthInfo mAuthInfo;

    private SsoHandler mSsoHandler;

    private static LoginActivity mInstance;
    private ImageView ivOldpsdfrgClear1;
    private ImageView ivOldpsdfrgClear2;

    public static LoginActivity Instance() {
        if (null == mInstance)
            mInstance = new LoginActivity();
        return mInstance;
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mInstance = this;
        initLoginByWeibo();

        if (tbLoginTitle == null) {
            tbLoginTitle = (TitleBar) findViewById(R.id.tbLoginTitle);
            tbLoginTitle.setTitle("登录");
            tbLoginTitle.setTitleColor(Color.WHITE);
            tbLoginTitle.setTextSize(20);

            tbLoginTitle.setLeftImageResource(R.drawable.icon_left);
            tbLoginTitle.setLeftClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    LoginActivity.this.finish();
                }
            });

        }

        if (metLoginTel == null || metLoginPsd == null) {

            metLoginTel = (EditText) findViewById(R.id.metLoginTel);
            metLoginPsd = (EditText) findViewById(R.id.metLoginPsd);
            ivOldpsdfrgClear1= (ImageView) findViewById(R.id.ivOldpsdfrgClear1);
            ivOldpsdfrgClear2= (ImageView) findViewById(R.id.ivOldpsdfrgClear2);
            String tel = metLoginTel.getText().toString().trim();
            String pwd = metLoginPsd.getText().toString().trim();



            metLoginTel.addTextChangedListener(mTextWathcer1);
            metLoginPsd.addTextChangedListener(mTextWathcer2);

            metLoginTel.setText(UserUtils.getMobile());
            if(UserUtils.getMobile()!=null&&!UserUtils.getMobile().equals("")){
                metLoginTel.setSelection(metLoginTel.getText().length());
                ivOldpsdfrgClear1.setVisibility(View.VISIBLE);
                ivOldpsdfrgClear1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        metLoginTel.setText("");
                        ivOldpsdfrgClear1.setVisibility(View.GONE);
                    }
                });
            }

        }
    }

    private TextWatcher mTextWathcer1=new TextWatcher(){

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
                ivOldpsdfrgClear1.setVisibility(View.VISIBLE);
                ivOldpsdfrgClear1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        metLoginTel.setText("");
                        ivOldpsdfrgClear1.setVisibility(View.GONE);
                    }
                });
            }

            if(temp.length()==0){
                ivOldpsdfrgClear1.setVisibility(View.GONE);
            }
        }
    };

    private TextWatcher mTextWathcer2=new TextWatcher(){

        private CharSequence temp;

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            temp=s;
            if(temp.length()>0){
                ivOldpsdfrgClear2.setVisibility(View.VISIBLE);
                ivOldpsdfrgClear2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        metLoginPsd.setText("");
                        ivOldpsdfrgClear2.setVisibility(View.GONE);
                    }
                });
            }

            if(temp.length()==0){
                ivOldpsdfrgClear2.setVisibility(View.GONE);
            }

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {

            if(temp.length()>0){
                ivOldpsdfrgClear2.setVisibility(View.VISIBLE);
                ivOldpsdfrgClear2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        metLoginPsd.setText("");
                        ivOldpsdfrgClear2.setVisibility(View.GONE);
                    }
                });
            }

            if(temp.length()==0){
                ivOldpsdfrgClear2.setVisibility(View.GONE);
            }
        }
    };


    private void initLoginByWeibo() {
        btnLoginRegWeibo = (Button) findViewById(R.id.btnLoginRegWeibo);
        mAuthInfo = new AuthInfo(LoginActivity.this, APP_KEY, REDIRECT_URL, SCOPE);
        mSsoHandler = new SsoHandler(LoginActivity.this, mAuthInfo);
        btnLoginRegWeibo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MobclickAgent.onEvent(LoginActivity.this, UmengConfig.EVENT_LOGIN_WEIBO);
                mSsoHandler.authorize(new AuthListener(LoginActivity.this, mListener));
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (mSsoHandler != null) {
            mSsoHandler.authorizeCallBack(requestCode, resultCode, data);
        }
    }


    private RequestListener mListener = new RequestListener() {
        @Override
        public void onComplete(String response) {
            if (!TextUtils.isEmpty(response)) {
                LogUtil.i(TAG, response);
                // 调用 User#parse 将JSON串解析成User对象
                com.sina.weibo.sdk.openapi.models.User weibouser = com.sina.weibo.sdk.openapi.models.User.parse(response);
                if (weibouser != null) {

                    loginViaWeibo(weibouser);
                    MobclickAgent.onProfileSignIn("Weibo", weibouser.screen_name);
                } else {
                    Toast.makeText(LoginActivity.this, response, Toast.LENGTH_LONG).show();
                }
            }
        }

        @Override
        public void onWeiboException(WeiboException e) {
            LogUtil.e(TAG, e.getMessage());
            ErrorInfo info = ErrorInfo.parse(e.getMessage());
            Toast.makeText(LoginActivity.this, info.toString(), Toast.LENGTH_LONG).show();
        }
    };

    private void loginViaWeibo(final com.sina.weibo.sdk.openapi.models.User user) {

        RequestParams entity = new RequestParams(Configurations.URL_THIRD_PARTY_LOGIN);
        entity.addParameter(Configurations.OPEN_ID, user.id);

        String device_id= JPushInterface.getRegistrationID(LoginActivity.this);
        Map<String,String> map=new TreeMap<>();

        if (!TextUtils.isEmpty(user.screen_name)) {
            entity.addParameter(Configurations.NICKNAME, user.screen_name);
            map.put(Configurations.NICKNAME, user.screen_name);
        }
        //showLoading();
        entity.addParameter(Configurations.AVATOR, user.profile_image_url);
        entity.addParameter(Configurations.FROM, "weibo");
        entity.addParameter(Configurations.REG_ID,device_id);


        long timeStamp= TimeUtil.getCurrentTime();
        entity.addParameter(Configurations.TIMESTAMP, String.valueOf(timeStamp));
        entity.addParameter(Configurations.DEVICE_ID,device_id );

        map.put(Configurations.OPEN_ID, user.id);
        map.put(Configurations.AVATOR, user.profile_image_url);
        map.put(Configurations.FROM, "weibo");
        map.put(Configurations.REG_ID,device_id);
        entity.addParameter(Configurations.SIGN, SignUtils.createSignString(device_id,timeStamp,map));



        x.http().post(entity, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {

                hideLoading();
                try {
                    JSONObject weiboObj = new JSONObject(result);
                    if (weiboObj.getInt("statusCode") == 200) {
                       // Log.e("weiboResult",result);
                        User user = JSON.parseObject(weiboObj.getJSONObject("results").getString("user"), User.class);
                        //新用户赠送优惠券
                        User.BenefitCouponBean benefit_coupon = user.getBenefit_coupon();
                        if(benefit_coupon!=null){
                            SharedPrefUtil.saveIsFirstRegister(true);
                        }
                        SharedPrefUtil.loginSuccess(SharedPrefUtil.LOGIN_VIA_WEIBO);
                        UserUtils.saveUserInfo(user);
                        finish();
                    } else {
                        ToastUtil.showShort(LoginActivity.this, weiboObj.getString(Configurations.STATUSMSG));
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

    @Event(R.id.btnLoginFgpsd)
    private void onLoginFgpsw(View view) {

        if (AppUtil.isFastClick())
            return;

        Intent intent = new Intent(LoginActivity.this, ForgetPsdActivity.class);
        startActivity(intent);

    }

    @Event(R.id.btnLoginLogin)
    private void onLoginLogin(View view) {

        if (AppUtil.isFastClick())
            return;

        if (TextUtil.checkParams(metLoginTel.getText().toString().trim(), metLoginPsd.getText().toString().trim())) {
            loginButtonOnClick();
        } else {
            ToastUtil.showShort(LoginActivity.this, "请输入电话号码或密码");
        }
    }

    @Event(R.id.btnLoginReg)
    private void onLoginReg(View view) {

        if (AppUtil.isFastClick())
            return;

        Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(intent);

    }

    @Event(R.id.btnLoginRegWechat)
    private void onLoginRegWechat(View v) {

        if (AppUtil.isFastClick())
            return;

        MobclickAgent.onEvent(LoginActivity.this, UmengConfig.EVENT_LOGIN_WEICHAT);
        if (WXapi == null) {
            WXapi = WXAPIFactory.createWXAPI(this, Configurations.WX_APP_ID, true);
        }
        if (!WXapi.isWXAppInstalled()) {
            HintDialog.newInstance("提示", "手机上没有安装微信", "确定").show(getSupportFragmentManager(), "hintDialog");
        }
        if (!WXapi.isWXAppSupportAPI()) {
            return;
        }
        WXapi.registerApp(Configurations.WX_APP_ID);
        SendAuth.Req req = new SendAuth.Req();
        req.scope = "snsapi_userinfo";
        req.state = "com.hzjytech.coffeeme";
        WXapi.sendReq(req);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(UmengConfig.LOGINACTIVITY);
        MobclickAgent.onPause(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        MobclickAgent.onPageStart(UmengConfig.LOGINACTIVITY);
        MobclickAgent.onResume(this);

        hideLoading();
        if (Configurations.baseResp != null) {
            if (Configurations.baseResp.getType() == ConstantsAPI.COMMAND_SENDAUTH) {
                //token take the place of code
                token = ((SendAuth.Resp) Configurations.baseResp).code;
                //Get AccessToken
                if (token != null) {
                    RequestParams params = new RequestParams(WeChatManager.getAccessTokenUrl(sURLAccessToken, token));
                    x.http().request(HttpMethod.GET, params, new Callback.CommonCallback<String>() {
                        @Override
                        public void onSuccess(String result) {
                            accessToken = new Gson().fromJson(result, AccessToken.class);
                            checkAccessToken();

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
            }
        } else {
            LogUtil.d(TAG, "baseResp is null");
        }

    }

    private void getUserInfo(boolean checkAccessTokenResult, final AccessToken accessToken) {
        //check access token valid
        if (!checkAccessTokenResult) {
            //access token is not valid refresh
            RequestParams params = new RequestParams(WeChatManager.getRefreshAccessTokenUrl(sURLRefreshAccessToken, accessToken));
            x.http().request(HttpMethod.GET, params, new Callback.CommonCallback<String>() {
                @Override
                public void onSuccess(String result) {
                    parseRefreshAccessTokenResult(result);
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

        showLoading();
        //get userinfo
        RequestParams requestParams = new RequestParams(WeChatManager.getUserInfoUrl(sURLUserInfo, accessToken));
        x.http().request(HttpMethod.GET, requestParams, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                userinfo = new Gson().fromJson(result, UserInfoWX.class);

                String device_id= JPushInterface.getRegistrationID(LoginActivity.this);
                RequestParams entity = new RequestParams(Configurations.URL_THIRD_PARTY_LOGIN);
                entity.addParameter(Configurations.OPEN_ID, userinfo.getUnionid());
                entity.addParameter(Configurations.NICKNAME, userinfo.getNickname());
                entity.addParameter(Configurations.AVATOR, userinfo.getHeadimgurl());
                entity.addParameter("from", "wx");
                entity.addParameter(Configurations.REG_ID,device_id);


                long timeStamp= TimeUtil.getCurrentTime();
                entity.addParameter(Configurations.TIMESTAMP, String.valueOf(timeStamp));
                entity.addParameter(Configurations.DEVICE_ID,device_id );

                Map<String,String> map=new TreeMap<>();
                map.put(Configurations.OPEN_ID, userinfo.getUnionid());
                map.put(Configurations.NICKNAME, userinfo.getNickname());
                map.put(Configurations.AVATOR, userinfo.getHeadimgurl());
                map.put("from", "wx");
                map.put(Configurations.REG_ID,device_id);
                entity.addParameter(Configurations.SIGN, SignUtils.createSignString(device_id,timeStamp,map));


                x.http().request(HttpMethod.POST, entity, new CommonCallback<String>() {
                    @Override
                    public void onSuccess(String result) {
                        LogUtil.d(TAG, result);
                        hideLoading();

                        try {
                            JSONObject object = new JSONObject(result);
                            if (object.getInt(Configurations.STATUSCODE) == 200) {
                                User user = JSON.parseObject(object.getJSONObject("results").getString("user"), User.class);
                                //新用户赠送优惠券
                                User.BenefitCouponBean benefit_coupon = user.getBenefit_coupon();
                                if(benefit_coupon!=null){
                                    SharedPrefUtil.saveIsFirstRegister(true);
                                }
                                SharedPrefUtil.loginSuccess(SharedPrefUtil.LOGIN_VIA_WECHAT);
                                UserUtils.saveUserInfo(user);

                                MobclickAgent.onProfileSignIn("WeChat", user.getNickname());
                                finish();
                            } else {
                                ToastUtil.showShort(LoginActivity.this, object.getString(Configurations.STATUSMSG));
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void onError(Throwable ex, boolean isOnCallback) {
                        hideLoading();
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
            }

            @Override
            public void onCancelled(CancelledException cex) {
            }

            @Override
            public void onFinished() {
            }
        });
    }


    private void checkAccessToken() {

        RequestParams params = new RequestParams(WeChatManager.getValidAccessTokenUrl(sURLValidAccessToken, accessToken));
        x.http().request(HttpMethod.GET, params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                Log.d(TAG, result);
                boolean checkAccessTokenResult = parseValidAccessTokenResult(result);
                getUserInfo(checkAccessTokenResult, accessToken);
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

    private boolean parseValidAccessTokenResult(String result) {
        try {
            int errcode = new JSONObject(result).getInt("errcode");
            if (errcode == 0) {
                return true;
            } else {
                return false;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return false;
    }


    private void parseRefreshAccessTokenResult(String result) {
        AccessToken refreshAccessToken = new Gson().fromJson(result, AccessToken.class);
        accessToken.setAccess_token(refreshAccessToken.getAccess_token());
        accessToken.setExpires_in(refreshAccessToken.getExpires_in());
        accessToken.setRefresh_token(refreshAccessToken.getRefresh_token());
        accessToken.setOpenid(refreshAccessToken.getOpenid());
        accessToken.setScope(refreshAccessToken.getScope());

    }


    private void loginButtonOnClick() {

        RequestParams params = new RequestParams(Configurations.URL_SESSIONS);
        String device_id=JPushInterface.getRegistrationID(LoginActivity.this);

        params.addParameter(Configurations.PHONE, metLoginTel.getText().toString());
        params.addParameter(Configurations.PASSWORD, metLoginPsd.getText().toString());
//        params.addParameter(Configurations.REG_ID, JPushInterface.getRegistrationID(getApplicationContext()));
        params.addParameter(Configurations.REG_ID, device_id);

        long timeStamp= TimeUtil.getCurrentTime();
        params.addParameter(Configurations.TIMESTAMP, String.valueOf(timeStamp));
        params.addParameter(Configurations.DEVICE_ID,device_id );

        Map<String,String> map=new TreeMap<>();
        map.put(Configurations.PHONE, metLoginTel.getText().toString());
        map.put(Configurations.PASSWORD, metLoginPsd.getText().toString());
        map.put(Configurations.REG_ID, device_id);
        params.addParameter(Configurations.SIGN, SignUtils.createSignString(device_id,timeStamp,map));

        x.http().request(HttpMethod.POST, params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {

                Log.d("md5",result);
                try {
                    JSONObject object = new JSONObject(result);
                    if (object.getInt(Configurations.STATUSCODE) == 200) {
                        MobclickAgent.onEvent(LoginActivity.this, UmengConfig.EVENT_LOGIN);
                        User user = JSON.parseObject(object.getJSONObject("results").getString("user"), User.class);
                        MobclickAgent.onProfileSignIn(user.getPhone());
                        SharedPrefUtil.loginSuccess(SharedPrefUtil.LOGIN_VIA_PHONE);
                        UserUtils.saveUserInfo(user);
                        UserUtils.saveLocalMobileAndPwd(user.getPhone(), "");
                        LoginActivity.this.finish();


                    } else {
                        ToastUtil.showShort(LoginActivity.this, object.getString(Configurations.STATUSMSG));
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