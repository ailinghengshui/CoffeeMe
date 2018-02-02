
package com.hzjytech.coffeeme.me;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.JsPromptResult;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.hzjytech.coffeeme.BaseActivity;
import com.hzjytech.coffeeme.Dialogs.BottomSelectDialog;
import com.hzjytech.coffeeme.Dialogs.HintDialog;
import com.hzjytech.coffeeme.R;
import com.hzjytech.coffeeme.adapters.SharedFragmentAdapter;
import com.hzjytech.coffeeme.authorization.login.LoginActivity;
import com.hzjytech.coffeeme.configurations.Configurations;
import com.hzjytech.coffeeme.configurations.UmengConfig;
import com.hzjytech.coffeeme.entities.User;
import com.hzjytech.coffeeme.listeners.IMethod1Listener;
import com.hzjytech.coffeeme.utils.BitmapUtil;
import com.hzjytech.coffeeme.utils.LogUtil;
import com.hzjytech.coffeeme.utils.ToastUtil;
import com.hzjytech.coffeeme.utils.UserUtils;
import com.hzjytech.coffeeme.weibomanager.AccessTokenKeeper;
import com.hzjytech.coffeeme.widgets.TitleBar;
import com.hzjytech.scan.decoding.Intents;
import com.sina.weibo.sdk.api.TextObject;
import com.sina.weibo.sdk.api.WebpageObject;
import com.sina.weibo.sdk.api.WeiboMultiMessage;
import com.sina.weibo.sdk.api.share.BaseResponse;
import com.sina.weibo.sdk.api.share.IWeiboHandler;
import com.sina.weibo.sdk.api.share.IWeiboShareAPI;
import com.sina.weibo.sdk.api.share.SendMultiMessageToWeiboRequest;
import com.sina.weibo.sdk.api.share.WeiboShareSDK;
import com.sina.weibo.sdk.auth.AuthInfo;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WeiboAuthListener;
import com.sina.weibo.sdk.constant.WBConstants;
import com.sina.weibo.sdk.exception.WeiboException;
import com.squareup.okhttp.internal.Util;
import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
import com.tencent.mm.sdk.modelmsg.WXTextObject;
import com.tencent.mm.sdk.modelmsg.WXWebpageObject;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.umeng.analytics.MobclickAgent;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;

import java.io.ByteArrayOutputStream;
import java.util.UUID;

/**
 * Created by hehongcan on 2016/12/29.
 */
@ContentView(R.layout.activity_share)
public class ShareActivity extends BaseActivity implements IWeiboHandler.Response {
    @ViewInject(R.id.titleBar)
    private TitleBar titleBar;
    @ViewInject(R.id.pb)
    private ProgressBar mPb;
    @ViewInject(R.id.wv_share)
    private WebView mWv;
    private IWeiboShareAPI mWeiboShareAPI;
    private IWXAPI api;
    private User user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //mInstance=this;
        initView();
        mWeiboShareAPI = WeiboShareSDK.createWeiboAPI(this, LoginActivity.APP_KEY);
        mWeiboShareAPI.registerApp();
        if (savedInstanceState != null) {
            mWeiboShareAPI.handleWeiboResponse(getIntent(), this);
        }
        api = WXAPIFactory.createWXAPI(this, Configurations.WX_APP_ID);
    }

    @Override
    protected void onResume() {
        hideLoading();
        super.onResume();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        hideLoading();
        super.onNewIntent(intent);

    }

    private void initView() {
        initTitleBar();
        initWebView();
    }

    private void initWebView() {
        mPb.setMax(100);
        WebSettings webSettings=mWv.getSettings();
        webSettings.setJavaScriptEnabled(true);
        //适当缩小页面内容
        // webSettings.setUseWideViewPort(true);
        //webSettings.setLoadWithOverviewMode(true);
        //允许js打开窗口
        //webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webSettings.setBlockNetworkImage(false);
        //允许输入获取焦点
        mWv.requestFocusFromTouch();
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);//关闭缓存
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP)
            webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        mWv.addJavascriptInterface(new ShareJsInteration(),"control");
        mWv.setWebChromeClient(new WebChromeClient(){
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                LogUtil.e("progress",newProgress+"");
                if(newProgress<100){
                    mPb.setVisibility(View.VISIBLE);
                    mPb.setProgress(newProgress);
                }else {
                    mPb.setProgress(newProgress);
                    mPb.setVisibility(View.GONE);
                }
                super.onProgressChanged(view, newProgress);
            }

            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);
                titleBar.setTitle(view.getTitle());
            }


            @Override
            public boolean onJsPrompt(
                    WebView view,
                    String url,
                    String message,
                    String defaultValue,
                    JsPromptResult result) {
                return super.onJsPrompt(view, url, message, defaultValue, result);
            }
        });

        mWv.loadUrl(Configurations.URL_INVITINGFRIENDS+"/"+ UserUtils.getUserInfo().getAuth_token());
    }

    @Override
    public void onResponse(BaseResponse baseResponse) {
        switch (baseResponse.errCode) {
            case WBConstants.ErrorCode.ERR_OK:
                ToastUtil.showShort(this, "分享成功");
                break;
            case WBConstants.ErrorCode.ERR_CANCEL:
                ToastUtil.showShort(this, "分享取消");
                break;
            case WBConstants.ErrorCode.ERR_FAIL:
                ToastUtil.showShort(this, "分享失败");
                break;
        }
    }

    public class ShareJsInteration {

        private String mTitle="";
        private String mContent="";
        private String mShareUr="";

        @JavascriptInterface
        public void showShare(String mTitle,String mContent,String mShareUr){

            onMefrgShareClick(mTitle,mContent,mShareUr);
            //onMefrgShareClick("share","share","share");
        }
    }


    private void initTitleBar() {
        titleBar.setTitle(R.string.string_share);
        titleBar.setTitleColor(Color.WHITE);
        titleBar.setLeftClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        titleBar.setLeftImageResource(R.drawable.icon_left);
    }

    private void onMefrgShareClick(final String title, final String content, final String shareUr) {
        int[] images = new int[]{R.drawable.icon_share_weibo,  R.drawable.icon_share_wechat,R.drawable.icon_share_friendcircle};
        String[] titles = new String[]{"新浪微博",  "微信好友","微信朋友圈"};

        final BottomSelectDialog bottomSelectDialog=new BottomSelectDialog();

        bottomSelectDialog.setAdapter(this,images,titles,new GridLayoutManager(this,3));
        bottomSelectDialog.setListener(new IMethod1Listener() {
            @Override
            public void OnMethod1Listener(int param) {
                switch (param){
                    //新浪微博
                    case 0:
                        shareViaWeibo(title,content,shareUr);
                        break;
                    //微信好友
                    case 1:
                        shareViaWeChat(title,content,shareUr,true);
                        break;
                    //微信朋友圈
                    case 2:
                        shareViaWeChat(title,content,shareUr,false);
                        break;
                }
                bottomSelectDialog.dismiss();
            }
        });
        bottomSelectDialog.show(getSupportFragmentManager(),"share");

    }
    private void shareViaWeibo(String title, String content, String shareUr) {
        showLoading();
        MobclickAgent.onEvent(this, UmengConfig.EVENT_SHAREREFERRALCODE_WEIBO);
        WebpageObject webpageObject = new WebpageObject(); //分享网页是这个
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize=2;
        Bitmap thumb = BitmapFactory.decodeResource(getResources(), R.drawable.logo,options);
        webpageObject.setThumbImage(thumb); //注意，它会按照jpeg做85%的压缩，压缩后的大小不能超过32K
        webpageObject.title = title;//不能超过512
        webpageObject.actionUrl = shareUr;// 不能超过512
        webpageObject.description = content;//不能超过1024
        webpageObject.identify = UUID.randomUUID().toString();//这个不知道做啥的
        webpageObject.defaultText = "Webpage 默认文案";//这个也不知道做啥的
//上面这些，一条都不能少，不然就会出现分享失败，主要是接口调用失败，而不会通过activity返回错误的intent

//下面这个，就是用户在分享网页的时候，自定义的微博内容
        TextObject textObject = new TextObject();
        textObject.text = title;

        WeiboMultiMessage msg = new WeiboMultiMessage();
        msg.mediaObject=webpageObject;
        msg.textObject=textObject;
        SendMultiMessageToWeiboRequest request = new SendMultiMessageToWeiboRequest();
        request.transaction = String.valueOf(System.currentTimeMillis());
        request.multiMessage = msg;

        AuthInfo authInfo = new AuthInfo(this, LoginActivity.APP_KEY, LoginActivity.REDIRECT_URL, LoginActivity.SCOPE);
        Oauth2AccessToken accessToken = AccessTokenKeeper.readAccessToken(getApplicationContext());
        String token = "";
        if (accessToken != null) {
            token = accessToken.getToken();
        }
        mWeiboShareAPI.sendRequest(this, request, authInfo, token, new WeiboAuthListener() {
            @Override
            public void onComplete(Bundle bundle) {
                Oauth2AccessToken newToken = Oauth2AccessToken.parseAccessToken(bundle);
                AccessTokenKeeper.writeAccessToken(getApplicationContext(), newToken);
            }

            @Override
            public void onWeiboException(WeiboException e) {
            }
            @Override
            public void onCancel() {
            }
        });

    }

    private void shareViaWeChat(String title, String content, String shareUr,boolean isWechat) {
        showLoading();

        if (!api.isWXAppInstalled()) {
            hideLoading();
            HintDialog.newInstance("提示", "手机上没有安装微信", "确定").show(getSupportFragmentManager(), "personInfoHint");
        }
        MobclickAgent.onEvent(this, UmengConfig.EVENT_SHAREREFERRALCODE_WECHAT);
       /* WXTextObject textObj = new WXTextObject();
        textObj.text = "我的Coffee Me专属优惠码： " + UserUtils.getUserInfo().getReferral_code();
        WXMediaMessage msg = new WXMediaMessage();
        msg.mediaObject = textObj;
        msg.description = "我的Coffee Me专属优惠码： " + UserUtils.getUserInfo().getReferral_code();*/
        WXWebpageObject webpage = new WXWebpageObject();
        webpage.webpageUrl=shareUr;
        WXMediaMessage msg = new WXMediaMessage(webpage);
        msg.title=title;
        msg.description=content;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize=2;
        Bitmap thumb = BitmapFactory.decodeResource(getResources(), R.drawable.logo,options);
        msg.thumbData= BitmapUtil.bmpToByteArray(thumb,true);
        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = String.valueOf(System.currentTimeMillis());
        req.message = msg;
        req.scene = isWechat == true ? SendMessageToWX.Req.WXSceneSession : SendMessageToWX.Req.WXSceneTimeline;
        api.sendReq(req);

    }

}
