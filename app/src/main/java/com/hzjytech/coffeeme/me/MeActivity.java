package com.hzjytech.coffeeme.me;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.google.gson.Gson;
import com.hzjytech.coffeeme.BaseActivity;
import com.hzjytech.coffeeme.Dialogs.DescCenterDialog;
import com.hzjytech.coffeeme.Dialogs.HintDialog;
import com.hzjytech.coffeeme.Dialogs.ITwoButtonClick;
import com.hzjytech.coffeeme.Dialogs.TitleButtonsDialog;
import com.hzjytech.coffeeme.EnterActivity;
import com.hzjytech.coffeeme.R;
import com.hzjytech.coffeeme.authorization.login.LoginActivity;
import com.hzjytech.coffeeme.configurations.Configurations;
import com.hzjytech.coffeeme.configurations.UmengConfig;
import com.hzjytech.coffeeme.entities.AccessToken;
import com.hzjytech.coffeeme.entities.UserInfoWX;
import com.hzjytech.coffeeme.order.OrderFragment;
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
import com.hzjytech.coffeeme.widgets.CircleImageView;
import com.hzjytech.coffeeme.widgets.TitleBar;
import com.hzjytech.crop.CropImage;
import com.hzjytech.crop.CropImageView;
import com.hzjytech.qiniutoken.Auth;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UploadManager;
import com.sina.weibo.sdk.auth.AuthInfo;
import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.RequestListener;
import com.sina.weibo.sdk.openapi.models.ErrorInfo;
import com.sina.weibo.sdk.openapi.models.User;
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
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.io.File;
import java.util.Map;
import java.util.TreeMap;

import cn.jpush.android.api.JPushInterface;
import cz.msebera.android.httpclient.Header;

@ContentView(R.layout.activity_me)
public class MeActivity extends BaseActivity implements SettingFragment.OnSettingFragmentable {

    private static final String TAG = MeActivity.class.getSimpleName();
    public static final int RESULTCODE_OK = 0x10001;
    public static final int RESULTCODE_ERROR = 0x10002;
    public static final int RESULTCODE_CAMERA_OK = 0x10003;

    @ViewInject(R.id.titleBar)
    private TitleBar tbMeTitle;


    @ViewInject(R.id.llPsninfoAvator)
    private LinearLayout llPsninfoAvator;

    @ViewInject(R.id.civPsninfofrgAvator)
    private CircleImageView civPsninfofrgAvator;

    @ViewInject(R.id.llPsninfoNickname)
    private LinearLayout llPsninfoNickname;

    @ViewInject(R.id.tvPsninfoNickname)
    private TextView tvPsninfoNickname;

    @ViewInject(R.id.llPsninfoPhone)
    private  LinearLayout llPsninfoPhone;

    @ViewInject(R.id.tvPsninfoPhone)
    private TextView tvPsninfoPhone;

    @ViewInject(R.id.llPsninfoWechat)
    private  LinearLayout  llPsninfoWechat ;

    @ViewInject(R.id.tvPsninfoWechat)
    private TextView tvPsninfoWechat;

    @ViewInject(R.id.llPsninfoWeibo)
    private LinearLayout llPsninfoWeibo;

    @ViewInject(R.id.tvPsninfoWeibo)
    private TextView tvPsninfoWeibo;

    @ViewInject(R.id.llPsninfoPsd)
    private LinearLayout llPsninfoPsd;
    //进入相片选择
    private View.OnClickListener selectedPhotos  = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            CropImage.startPickImageActivity(MeActivity.this);
        }

    };;
    private View.OnClickListener selectNickName=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            startActivity(new Intent(MeActivity.this, UpdateNicknameActivity.class));
        }
    };;

    public MeActivity() {
    }

    @Event(R.id.btnPsninfoExit)
    private void onPsninfoExit(View view) {

        TitleButtonsDialog descCenterDialog=TitleButtonsDialog.newInstance("确定退出当前账号?","取消","确定");
        descCenterDialog.setOnTwoClickListener(new ITwoButtonClick() {
            @Override
            public void onLeftButtonClick() {

            }

            @Override
            public void onRightButtonClick() {
                String delUrl = Configurations.URL_SESSIONS;
                com.loopj.android.http.RequestParams params = new com.loopj.android.http.RequestParams();
                params.put(Configurations.AUTH_TOKEN, UserUtils.getUserInfo().getAuth_token());
                String device_id= JPushInterface.getRegistrationID(MeActivity.this);
                String timeStamp= TimeUtil.getCurrentTimeString();
                params.put(Configurations.TIMESTAMP,timeStamp);
                params.put(Configurations.DEVICE_ID,device_id );

                Map<String,String> map=new TreeMap<>();
                map.put(Configurations.AUTH_TOKEN, UserUtils.getUserInfo().getAuth_token());
                params.put(Configurations.SIGN, SignUtils.createSignString(device_id,timeStamp,map));

                AsyncHttpClient client = new AsyncHttpClient();
                client.delete(delUrl, params, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

                        checkResOld(response);
                        try {
                            if (response.getInt(Configurations.STATUSCODE) == 200) {
                                SharedPrefUtil.loginout();
                                SharedPrefUtil.saveAvatorUrl("");
                                SharedPrefUtil.saveUri("");
                                SharedPrefUtil.saveWeiboId("");
                                UserUtils.saveUserInfo(null);
                                MeActivity.this.finish();
                            }
                            ToastUtil.showShort(MeActivity.this, response.getString(Configurations.STATUSMSG));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });

        descCenterDialog.show(getSupportFragmentManager(),"exitFragment");

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initTitle();


    }

    private void initView() {
        Log.e("wxid",UserUtils.getUserInfo().getWx_open_id()+"");
        tvPsninfoNickname.setText(UserUtils.getUserInfo().getNickname());
        if (!TextUtils.isEmpty(UserUtils.getUserInfo().getPhone())) {
            tvPsninfoPhone.setText(UserUtils.getUserInfo().getPhone());
        }

        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HintDialog.newInstance("提示", "请使用手机号码登录再进行该操作", "确定").show(getSupportFragmentManager(), "hint");
            }
        };


        int loginmethod = SharedPrefUtil.getLoginMethod();
        switch (loginmethod) {
            case SharedPrefUtil.LOGIN_VIA_PHONE:
                if (!TextUtils.isEmpty(UserUtils.getUserInfo().getWeibo_open_id())) {

                    tvPsninfoWeibo.setText(getResources().getString(R.string.str_unbind));
                    llPsninfoWeibo.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            unrelateWeibo();
                        }
                    });
                } else {
                    tvPsninfoWeibo.setText(getResources().getString(R.string.str_nobind));
                    llPsninfoWeibo.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            bindWeibo();
                        }
                    });
                }

                if (!TextUtils.isEmpty(UserUtils.getUserInfo().getWx_open_id())) {
                    tvPsninfoWechat.setText(getResources().getString(R.string.str_unbind));
                    llPsninfoWechat.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                                    unrelateWeChat();

                        }
                    });
                } else {
                    tvPsninfoWechat.setText(getResources().getString(R.string.str_nobind));
                    llPsninfoWechat.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            bindWeChat();
                        }
                    });
                }


                llPsninfoAvator.setOnClickListener(selectedPhotos);


                llPsninfoNickname.setOnClickListener(selectNickName);

                llPsninfoPhone.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                       bindPhone();
                    }
                });

                llPsninfoPsd.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(MeActivity.this, ChangepsdActivity.class));
                    }
                });
                break;
            case SharedPrefUtil.LOGIN_VIA_WECHAT:
                //llPsninfoAvator.setOnClickListener(onClickListener);
                llPsninfoAvator.setOnClickListener(selectedPhotos);
                //llPsninfoNickname.setOnClickListener(onClickListener);
                llPsninfoNickname.setOnClickListener(selectNickName);
                llPsninfoPsd.setOnClickListener(onClickListener);
                tvPsninfoWechat.setText("解除绑定");
                llPsninfoWechat.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(!TextUtils.isEmpty(UserUtils.getUserInfo().getWeibo_open_id())||!TextUtils.isEmpty(UserUtils.getUserInfo().getPhone())){
                            unrelateWeChat();
                        }else {
                            return;
                        }
                    }
                });
                llPsninfoPhone.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        bindPhone();
                    }
                });
                Log.e("id",UserUtils.getUserInfo().getWeibo_open_id()+"");

                if (!TextUtils.isEmpty(UserUtils.getUserInfo().getWeibo_open_id())) {

                    tvPsninfoWeibo.setText(getResources().getString(R.string.str_unbind));
                    llPsninfoWeibo.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            unrelateWeibo();
                        }
                    });

                } else {

                    tvPsninfoWeibo.setText(getResources().getString(R.string.str_nobind));
                    llPsninfoWeibo.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            bindWeibo();
                        }
                    });
                }
                break;
            case SharedPrefUtil.LOGIN_VIA_WEIBO:
               // llPsninfoAvator.setOnClickListener(onClickListener);
                llPsninfoAvator.setOnClickListener(selectedPhotos);
                //llPsninfoNickname.setOnClickListener(onClickListener);
                llPsninfoNickname.setOnClickListener(selectNickName);
                tvPsninfoWeibo.setText("解除绑定");
                llPsninfoPsd.setOnClickListener(onClickListener);
                llPsninfoPhone.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        bindPhone();
                    }
                });
                llPsninfoWeibo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(!TextUtils.isEmpty(UserUtils.getUserInfo().getWx_open_id())||!TextUtils.isEmpty(UserUtils.getUserInfo().getPhone())){
                            unrelateWeibo();                        }else{
                            return;
                        }
                    }
                });
                if (!TextUtils.isEmpty(UserUtils.getUserInfo().getWx_open_id())) {
                    tvPsninfoWechat.setText(getResources().getString(R.string.str_unbind));
                    llPsninfoWechat.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            unrelateWeChat();

                        }
                    });

                } else {
                    tvPsninfoWechat.setText(getResources().getString(R.string.str_nobind));
                    llPsninfoWechat.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            bindWeChat();
                        }
                    });

                }
                break;
            default:
                break;
        }


        if (!TextUtils.isEmpty(SharedPrefUtil.getUri())) {

            DisplayImageOptions options = new DisplayImageOptions.Builder()
                    .cacheInMemory(true)/*缓存至内存*/
                    .cacheOnDisk(true)/*缓存值SDcard*/
                    .bitmapConfig(Bitmap.Config.RGB_565)
                    .imageScaleType(ImageScaleType.EXACTLY)
                    .build();
            ImageLoader.getInstance().displayImage(SharedPrefUtil.getUri(), civPsninfofrgAvator, options, new ImageLoadingListener() {
                @Override
                public void onLoadingStarted(String s, View view) {

                }

                @Override
                public void onLoadingFailed(String s, View view, FailReason failReason) {
                    civPsninfofrgAvator.setImageResource(R.drawable.icon_user_default);
                }

                @Override
                public void onLoadingComplete(String s, View view, Bitmap bitmap) {

                }

                @Override
                public void onLoadingCancelled(String s, View view) {

                }
            });

        }else if (UserUtils.getUserInfo()!=null) {
            if(!TextUtils.isEmpty(UserUtils.getUserInfo().getAvator_url())) {
                DisplayImageOptions options = new DisplayImageOptions.Builder()
                        .cacheInMemory(true)/*缓存至内存*/
                        .cacheOnDisk(true)/*缓存值SDcard*/
                        .bitmapConfig(Bitmap.Config.RGB_565)
                        .build();
                ImageLoader.getInstance().displayImage(UserUtils.getUserInfo().getAvator_url(), civPsninfofrgAvator, options, new ImageLoadingListener() {
                    @Override
                    public void onLoadingStarted(String s, View view) {

                    }

                    @Override
                    public void onLoadingFailed(String s, View view, FailReason failReason) {
                        civPsninfofrgAvator.setImageResource(R.drawable.icon_user_default);
                    }

                    @Override
                    public void onLoadingComplete(String s, View view, Bitmap bitmap) {

                    }

                    @Override
                    public void onLoadingCancelled(String s, View view) {

                    }
                });
            }else{
                civPsninfofrgAvator.setImageResource(R.drawable.icon_user_default);
            }

        } else {
            civPsninfofrgAvator.setImageResource(R.drawable.icon_user_default);
        }
    }

    private void initTitle() {
        tbMeTitle.setTitle(getResources().getString(R.string.str_person_setting));
        tbMeTitle.setTitleColor(Color.WHITE);
        tbMeTitle.setLeftImageResource(R.drawable.icon_left);
        tbMeTitle.setLeftClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MeActivity.this.finish();
            }
        });
    }

    @Override
    public void onSetingExitClick() {
        Intent intent = new Intent(MeActivity.this, EnterActivity.class);
        SharedPrefUtil.clear();
        startActivity(intent);
        finish();
    }

    private void bindPhone() {
        Intent intent = new Intent(MeActivity.this, BindingPhoneActivity.class);
        startActivity(intent);
    }


    private SsoHandler mSsoHandler;
    private AuthInfo mAuthInfo;

    private RequestListener mListener = new RequestListener() {
        @Override
        public void onComplete(String s) {

            if (!TextUtils.isEmpty(s)) {
                com.sina.weibo.sdk.utils.LogUtil.i("PersonInfoFrg", s);
                final User user = User.parse(s);
                if (user != null) {
                    RequestParams entity = new RequestParams(Configurations.URL_BINDING_THIRD_PARTY);
                    entity.addParameter(Configurations.AUTH_TOKEN, UserUtils.getUserInfo().getAuth_token());
                    entity.addParameter(Configurations.OPEN_ID, user.id);
                    entity.addParameter(Configurations.FROM, "weibo");

                    String device_id = JPushInterface.getRegistrationID(MeActivity.this);
                    String timeStamp= TimeUtil.getCurrentTimeString();
                    entity.addParameter(Configurations.TIMESTAMP, timeStamp);
                    entity.addParameter(Configurations.DEVICE_ID, device_id);

                    Map<String, String> map = new TreeMap<String, String>();
                    map.put(Configurations.AUTH_TOKEN, UserUtils.getUserInfo().getAuth_token());
                    map.put(Configurations.OPEN_ID, user.id);
                    map.put(Configurations.FROM, "weibo");
                    entity.addParameter(Configurations.SIGN, SignUtils.createSignString(device_id, timeStamp, map));

                    x.http().request(HttpMethod.PUT, entity, new Callback.CommonCallback<JSONObject>() {

                        @Override
                        public void onSuccess(JSONObject object) {

                            checkResOld(object);
                            try {
                                if (object.getInt(Configurations.STATUSCODE) == 200) {
//                                    SharedPrefUtil.saveWeiboId(user.id);
                                    UserUtils.getUserInfo().setWeibo_open_id(user.id);
                                    tvPsninfoWeibo.setText(getResources().getString(R.string.str_unbind));
                                    llPsninfoWeibo.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            unrelateWeibo();

                                        }
                                    });
                                }
                                ToastUtil.showShort(MeActivity.this, object.getString(Configurations.STATUSMSG));
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


                } else {
                    ToastUtil.showShort(MeActivity.this, s);
                }
            }

        }

        @Override
        public void onWeiboException(WeiboException e) {
            com.sina.weibo.sdk.utils.LogUtil.e("PersonInfoFrg", e.getMessage());
            ErrorInfo info = ErrorInfo.parse(e.getMessage());
            ToastUtil.showShort(MeActivity.this, info.toString());
        }
    };

    private void unrelateWeibo() {
        if (AppUtil.isFastClick()) {
            return;
        }

        DescCenterDialog descCenterDialog = DescCenterDialog.newInstance("解除绑定", "确定要解除账号与微博的关联吗？解除后将无法使用微博登录此账号", "取消", "解除绑定");
        descCenterDialog.setOnTwoClickListener(new ITwoButtonClick() {
            @Override
            public void onLeftButtonClick() {

            }

            @Override
            public void onRightButtonClick() {

                RequestParams entity = new RequestParams(Configurations.URL_UNRELATED_THIRD_PARTY);
                entity.addParameter(Configurations.AUTH_TOKEN, UserUtils.getUserInfo().getAuth_token());
                entity.addParameter(Configurations.FROM, "weibo");

                String device_id = JPushInterface.getRegistrationID(MeActivity.this);
                String timeStamp= TimeUtil.getCurrentTimeString();
                entity.addParameter(Configurations.TIMESTAMP, timeStamp);
                entity.addParameter(Configurations.DEVICE_ID, device_id);

                Map<String, String> map = new TreeMap<String, String>();
                map.put(Configurations.AUTH_TOKEN, UserUtils.getUserInfo().getAuth_token());
                map.put(Configurations.FROM, "weibo");
                entity.addParameter(Configurations.SIGN, SignUtils.createSignString(device_id, timeStamp, map));

                x.http().request(HttpMethod.PUT, entity, new Callback.CommonCallback<JSONObject>() {

                    @Override
                    public void onSuccess(JSONObject result) {

                        checkResOld(result);
                        try {
                            if (result.getInt(Configurations.STATUSCODE) == 200) {
                                tvPsninfoWeibo.setText("未绑定");
                                UserUtils.getUserInfo().setWeibo_open_id(null);
                                if(SharedPrefUtil.getLoginMethod()==SharedPrefUtil.LOGIN_VIA_WEIBO){
                                  if(UserUtils.getUserInfo().getPhone()!=null){
                                        SharedPrefUtil.setLoginMethod(SharedPrefUtil.LOGIN_VIA_PHONE);
                                    }else  if(UserUtils.getUserInfo().getWx_open_id()!=null){
                                        SharedPrefUtil.setLoginMethod(SharedPrefUtil.LOGIN_VIA_WECHAT);
                                    }
                                }
                                llPsninfoWeibo.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        bindWeibo();
                                    }
                                });
                                initView();
                                ToastUtil.showShort(MeActivity.this, "解绑成功！");
                            }else{
                                ToastUtil.showShort(MeActivity.this, result.getString(Configurations.STATUSMSG));
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
        });

        descCenterDialog.show(getSupportFragmentManager(), "weibodescCenterDialog");

    }

    private void bindWeibo() {
        if (AppUtil.isFastClick()) {
            return;
        }

        mAuthInfo = new AuthInfo(MeActivity.this, LoginActivity.APP_KEY, LoginActivity.REDIRECT_URL, LoginActivity.SCOPE);
        mSsoHandler = new SsoHandler(MeActivity.this, mAuthInfo);
        mSsoHandler.authorize(new AuthListener(MeActivity.this, mListener));
        initView();
    }

    private static IWXAPI WXApi;

    private void bindWeChat() {
        if (AppUtil.isFastClick()) {
            return;
        }
        showLoading();
        WXApi = WXAPIFactory.createWXAPI(MeActivity.this, Configurations.WX_APP_ID, true);

        if (!WXApi.isWXAppInstalled()) {
            hideLoading();
            HintDialog.newInstance("提示", "手机上没有安装微信", "确定").show(getSupportFragmentManager(), "personInfoHint");
        }


        WXApi.registerApp(Configurations.WX_APP_ID);
        SendAuth.Req req = new SendAuth.Req();

        req.scope = "snsapi_userinfo";
        req.state = "wxdemo";

        WXApi.sendReq(req);
        initView();

    }

    private String mToken;
    private AccessToken mAccessToken;
    private boolean checkAccessTokenResult = false;

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
        MobclickAgent.onPageEnd(UmengConfig.MEACTIVITY);
    }

    @Override
    public void onResume() {
        super.onResume();
        initView();

        MobclickAgent.onResume(this);
        MobclickAgent.onPageStart(UmengConfig.MEACTIVITY);


        if (Configurations.baseResp != null) {
            hideLoading();
            if (Configurations.baseResp.getType() == ConstantsAPI.COMMAND_SENDAUTH) {
                mToken = ((SendAuth.Resp) Configurations.baseResp).code;

                if (mToken != null) {

                    RequestParams entity = new RequestParams(WeChatManager.getAccessTokenUrl(LoginActivity.sURLAccessToken, mToken));
                    x.http().request(HttpMethod.GET, entity, new Callback.CommonCallback<String>() {
                        @Override
                        public void onSuccess(String result) {
                            mAccessToken = new Gson().fromJson(result, AccessToken.class);
                            getUserInfo(mAccessToken);
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
            }else{
                hideLoading();
            }

        } else {
            com.sina.weibo.sdk.utils.LogUtil.d(TAG, "baseResp is null");
            hideLoading();
        }
    }

    private void getUserInfo(final AccessToken accessToken) {
        //check access token valid
        if (!checkAccessTokenResult) {
            //access token is not valid refresh
            RequestParams params = new RequestParams(WeChatManager.getRefreshAccessTokenUrl(LoginActivity.sURLRefreshAccessToken, accessToken));
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

        final RequestParams requestParams = new RequestParams(WeChatManager.getUserInfoUrl(LoginActivity.sURLUserInfo, accessToken));
        x.http().request(HttpMethod.GET, requestParams, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                final UserInfoWX userinfo = new Gson().fromJson(result, UserInfoWX.class);
                RequestParams entity = new RequestParams(Configurations.URL_BINDING_THIRD_PARTY);
                entity.addParameter(Configurations.AUTH_TOKEN, UserUtils.getUserInfo().getAuth_token());
//                entity.addParameter(Configurations.OPEN_ID, userinfo.getOpenid());
                entity.addParameter(Configurations.OPEN_ID, userinfo.getUnionid());
                entity.addParameter(Configurations.FROM, "wx");

                String device_id = JPushInterface.getRegistrationID(MeActivity.this);
                String timeStamp= TimeUtil.getCurrentTimeString();
                entity.addParameter(Configurations.TIMESTAMP, timeStamp);
                entity.addParameter(Configurations.DEVICE_ID, device_id);

                Map<String, String> map = new TreeMap<String, String>();
                map.put(Configurations.AUTH_TOKEN, UserUtils.getUserInfo().getAuth_token());
                map.put(Configurations.OPEN_ID, userinfo.getUnionid());
                map.put(Configurations.FROM, "wx");
                entity.addParameter(Configurations.SIGN, SignUtils.createSignString(device_id, timeStamp, map));


                x.http().request(HttpMethod.PUT, entity, new CommonCallback<JSONObject>() {
                    @Override
                    public void onSuccess(JSONObject result) {


                        checkResOld(result);
                        try {
                            if (result.getInt(Configurations.STATUSCODE) == 200) {

                                tvPsninfoWechat.setText(getString(R.string.str_unbind));
                                UserUtils.getUserInfo().setWx_open_id(userinfo.getOpenid());
                                llPsninfoWechat.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        unrelateWeChat();

                                    }
                                });
                            }
                            ToastUtil.showShort(MeActivity.this, result.getString(Configurations.STATUSMSG));
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

    private void unrelateWeChat() {
        if (AppUtil.isFastClick()) {
            return;
        }

        DescCenterDialog weichatDescCenterDialog = DescCenterDialog.newInstance("解除绑定", "确定要解除账号与微信的关联吗？解除后将无法使用微信登录此账号", "取消", "解除绑定");
        weichatDescCenterDialog.setOnTwoClickListener(new ITwoButtonClick() {
            @Override
            public void onLeftButtonClick() {

            }

            @Override
            public void onRightButtonClick() {
                RequestParams entity = new RequestParams(Configurations.URL_UNRELATED_THIRD_PARTY);
                entity.addParameter(Configurations.AUTH_TOKEN, UserUtils.getUserInfo().getAuth_token());
                entity.addParameter(Configurations.FROM, "wx");

                String device_id = JPushInterface.getRegistrationID(MeActivity.this);
                String timeStamp= TimeUtil.getCurrentTimeString();
                entity.addParameter(Configurations.TIMESTAMP, timeStamp);
                entity.addParameter(Configurations.DEVICE_ID, device_id);

                Map<String, String> map = new TreeMap<String, String>();
                map.put(Configurations.AUTH_TOKEN, UserUtils.getUserInfo().getAuth_token());
                map.put(Configurations.FROM, "wx");
                entity.addParameter(Configurations.SIGN, SignUtils.createSignString(device_id, timeStamp, map));

                x.http().request(HttpMethod.PUT, entity, new Callback.CommonCallback<JSONObject>() {

                    @Override
                    public void onSuccess(JSONObject result) {

                        checkResOld(result);
                        try {

                            if (result.getInt(Configurations.STATUSCODE) == 200) {
                                tvPsninfoWechat.setText("未绑定");
                                UserUtils.getUserInfo().setWx_open_id(null);
                                if(SharedPrefUtil.getLoginMethod()==SharedPrefUtil.LOGIN_VIA_WECHAT){
                                     if(UserUtils.getUserInfo().getPhone()!=null){
                                        SharedPrefUtil.setLoginMethod(SharedPrefUtil.LOGIN_VIA_PHONE);
                                    }else if(UserUtils.getUserInfo().getWeibo_open_id()!=null){
                                        SharedPrefUtil.setLoginMethod(SharedPrefUtil.LOGIN_VIA_WEIBO);
                                    }

                                }
                                llPsninfoWechat.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        bindWeChat();
                                    }
                                });
                                initView();
                                ToastUtil.showShort(MeActivity.this, "解绑成功！");
                            }else{
                                ToastUtil.showShort(MeActivity.this, result.getString(Configurations.STATUSMSG));
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
        });
        weichatDescCenterDialog.show(getSupportFragmentManager(), "weixindescCenterDialog");

    }

    private void parseRefreshAccessTokenResult(String result) {
        AccessToken refreshAccessToken = new Gson().fromJson(result, AccessToken.class);
        mAccessToken.setAccess_token(refreshAccessToken.getAccess_token());
        mAccessToken.setExpires_in(refreshAccessToken.getExpires_in());
        mAccessToken.setRefresh_token(refreshAccessToken.getRefresh_token());
        mAccessToken.setOpenid(refreshAccessToken.getOpenid());
        mAccessToken.setScope(refreshAccessToken.getScope());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == CropImage.PICK_IMAGE_CHOOSER_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            Uri imageUri = CropImage.getPickImageResultUri(this, data);
            if(imageUri!=null){
            startCropImageActivity(imageUri);
            }
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                SharedPrefUtil.saveUri(result.getUri().toString());
                DisplayImageOptions options = new DisplayImageOptions.Builder()
                        .cacheInMemory(true)/*缓存至内存*/
                        .cacheOnDisk(true)/*缓存值SDcard*/
                        .bitmapConfig(Bitmap.Config.RGB_565)
                        .imageScaleType(ImageScaleType.EXACTLY)
                        .build();
                ImageLoader.getInstance().displayImage(SharedPrefUtil.getUri(), civPsninfofrgAvator, options, new ImageLoadingListener() {
                    @Override
                    public void onLoadingStarted(String s, View view) {

                    }

                    @Override
                    public void onLoadingFailed(String s, View view, FailReason failReason) {
                        civPsninfofrgAvator.setImageResource(R.drawable.icon_user_default);

                    }

                    @Override
                    public void onLoadingComplete(String s, View view, Bitmap bitmap) {

                    }

                    @Override
                    public void onLoadingCancelled(String s, View view) {

                    }
                });
                upload2QiNiuFile(result.getUri().getPath());
//                clearLocalImage();
                setResult(MeActivity.RESULTCODE_CAMERA_OK);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                ToastUtil.showShort(this, getString(R.string.str_cropfail));
                SharedPrefUtil.saveUri("");
                setResult(MeActivity.RESULTCODE_ERROR);
            }
        }

        if (mSsoHandler != null) {
            mSsoHandler.authorizeCallBack(requestCode, resultCode, data);
        }
    }

    private void startCropImageActivity(Uri imageUri) {
        CropImage.activity(imageUri)
                .setGuidelines(CropImageView.Guidelines.ON)
                .start(this);
    }

    private void upload2QiNiu(Uri uri) {

        LogUtil.d("file", uri2File(uri).getPath());

        String token = Auth.create().uploadToken(Configurations.QINIU_BUCKET);
        new UploadManager().put(uri2File(uri), null, token, new UpCompletionHandler() {

            @Override
            public void complete(String key, ResponseInfo info, JSONObject response) {

                com.hzjytech.coffeeme.utils.LogUtil.d(TAG, "key: " + key + " info:" + info + " response: " + response);
                changeAvator(response);

            }
        }, null);
    }

    private void upload2QiNiuFile(String fileName) {

        LogUtil.d("file2", fileName);
        String token = Auth.create().uploadToken(Configurations.QINIU_BUCKET);
        new UploadManager().put(fileName, null, token, new UpCompletionHandler() {

            @Override
            public void complete(String key, ResponseInfo info, JSONObject response) {

                com.hzjytech.coffeeme.utils.LogUtil.d(TAG, "key: " + key + " info:" + info + " response: " + response);
                changeAvator(response);

            }
        }, null);
    }

    private void changeAvator(JSONObject response) {
        String avator = null;
        try {
            avator = response.getString("key");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String auth_token = UserUtils.getUserInfo().getAuth_token();
        RequestParams entity = new RequestParams(Configurations.URL_CHANGE_AVATOR);
        entity.addParameter(Configurations.AUTH_TOKEN, auth_token);
        entity.addParameter(Configurations.AVATOR, avator);

        String device_id= JPushInterface.getRegistrationID(MeActivity.this);
        String timeStamp= TimeUtil.getCurrentTimeString();
        entity.addParameter(Configurations.TIMESTAMP, timeStamp);
        entity.addParameter(Configurations.DEVICE_ID,device_id );

        Map<String,String> map=new TreeMap<>();
        map.put(Configurations.AUTH_TOKEN, auth_token);
        map.put(Configurations.AVATOR, avator);
        entity.addParameter(Configurations.SIGN, SignUtils.createSignString(device_id,timeStamp,map));

        x.http().request(HttpMethod.PUT, entity, new Callback.CommonCallback<JSONObject>() {
            @Override
            public void onSuccess(JSONObject result) {
                try {
                    checkResOld(result);
                    ToastUtil.showShort(MeActivity.this, result.getString(Configurations.STATUSMSG));
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

    private void clearLocalImage() {
        UserUtils.getUserInfo().setAvator_url("");
    }

    private File uri2File(Uri uri) {
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor actualimagecursor = MeActivity.this.managedQuery(uri, proj, null, null, null);
        int actual_image_column_index = actualimagecursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        actualimagecursor.moveToFirst();
        String img_path = actualimagecursor.getString(actual_image_column_index);
        return new File(img_path);
    }

    @Override
    public void onStart() {
        super.onStart();

        if (UserUtils.getUserInfo() != null) {
            tvPsninfoNickname.setText(UserUtils.getUserInfo().getNickname());
        }

        if (!TextUtils.isEmpty(UserUtils.getUserInfo().getPhone())) {
            tvPsninfoPhone.setText(UserUtils.getUserInfo().getPhone());
        }


    }


    private void getUserInfo() {

        String token = UserUtils.getUserInfo().getAuth_token();
        RequestParams params = new RequestParams(Configurations.URL_CHECK_TOKEN);
        params.addParameter(Configurations.AUTH_TOKEN, token);

        String timeStamp= TimeUtil.getCurrentTimeString();
        String device_id= JPushInterface.getRegistrationID(MeActivity.this);
        params.addParameter(Configurations.TIMESTAMP, timeStamp);
        params.addParameter(Configurations.DEVICE_ID,device_id );

        Map<String,String> map=new TreeMap<>();
        map.put(Configurations.AUTH_TOKEN, UserUtils.getUserInfo().getAuth_token());
        params.addParameter(Configurations.SIGN, com.hzjytech.coffeeme.utils.SignUtils.createSignString(device_id,timeStamp,map));

        x.http().get(params, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                try {
                    int statusCode = new JSONObject(result).getInt("statusCode");

                    if (statusCode == 200) {
                        com.hzjytech.coffeeme.entities.User user = JSON.parseObject((new JSONObject(result)).getJSONObject("results").getString("user"), com.hzjytech.coffeeme.entities.User.class);
                        UserUtils.saveUserInfo(user);
                    }
                    finish();

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
}