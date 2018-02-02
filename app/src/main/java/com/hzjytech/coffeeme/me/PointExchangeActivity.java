package com.hzjytech.coffeeme.me;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.http.SslError;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.hzjytech.coffeeme.BaseActivity;
import com.hzjytech.coffeeme.Dialogs.HintDialog;
import com.hzjytech.coffeeme.R;
import com.hzjytech.coffeeme.configurations.Configurations;
import com.hzjytech.coffeeme.configurations.UmengConfig;
import com.hzjytech.coffeeme.utils.LogUtil;
import com.hzjytech.coffeeme.utils.SharedPrefUtil;
import com.hzjytech.coffeeme.utils.UserUtils;
import com.hzjytech.coffeeme.widgets.TitleBar;
import com.umeng.analytics.MobclickAgent;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

@ContentView(R.layout.activity_point_exchange)
public class PointExchangeActivity extends BaseActivity {

    @ViewInject(R.id.titleBar)
    private TitleBar titleBar;

    @ViewInject(R.id.wvPointExchange)
    private WebView wvPointExchange;
    @ViewInject(R.id.pb)
    private ProgressBar mPb;
    private boolean fromCoupon=true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initTitle();

    }
    private void initWebView() {
        mPb.setMax(100);
        WebSettings webSettings=wvPointExchange.getSettings();
        webSettings.setJavaScriptEnabled(true);
        //适当缩小页面内容
       // webSettings.setUseWideViewPort(true);
        //webSettings.setLoadWithOverviewMode(true);
        //允许js打开窗口
        //webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webSettings.setBlockNetworkImage(false);
        //允许输入获取焦点
        wvPointExchange.requestFocusFromTouch();
        webSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);//关闭缓存
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP)
            webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        wvPointExchange.addJavascriptInterface(new PointExchangeJsInteration(),"control");
        wvPointExchange.setWebChromeClient(new WebChromeClient(){
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
            public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());

                HintDialog.newInstance("提示", message, "确定").show(getSupportFragmentManager(), "message");
                result.confirm();// 因为没有绑定事件，需要强行confirm,否则页面会变黑显示不了内容。
                return true;
            }

            @Override
            public boolean onJsConfirm(WebView view, String url, String message, JsResult result) {
                return super.onJsConfirm(view, url, message, result);
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
        wvPointExchange.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                LogUtil.e("h5url_finish",url);
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                LogUtil.e("h5url_start",url);
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onLoadResource(WebView view, String url) {
                super.onLoadResource(view, url);
            }



            @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                //super.onReceivedSslError(view, handler, error);
                handler.proceed();
            }
        });
        wvPointExchange.loadUrl(Configurations.URL_NEW_COMMODITIES+"/"+ UserUtils.getUserInfo().getAuth_token());
    }

    public class PointExchangeJsInteration {
        @JavascriptInterface
        public void gotoMyCoupons(){
            if(fromCoupon){
                finish();
                SharedPrefUtil.saveIsFromCoupon(true);
            }
            MyCouponActivity.Instance().finish();
            Intent intent=new Intent(PointExchangeActivity.this,MyCouponActivity.class);
            intent.putExtra(MyCouponActivity.SHOW_BOTTOM_BTN,true);
            startActivity(intent);
        }

        @JavascriptInterface
        public void outLogin(){
            goLogin();
        }
        @JavascriptInterface
        public void goHome(){
            wvPointExchange.clearHistory();
        }
    }

    private void initTitle() {
        titleBar.setLeftClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(wvPointExchange.getTitle().equals("积分商城")){
                    finish();
                }
                if(!wvPointExchange.canGoBack()){
                    finish();
                }else{
                    wvPointExchange.goBack();
                }
            }
        });
        titleBar.setTitleColor(Color.WHITE);
        titleBar.setLeftImageResource(R.drawable.icon_left);
    }

    @Override
    protected void onResume() {
        fromCoupon=SharedPrefUtil.getIsFromCoupon();
        super.onResume();
        MobclickAgent.onPageStart(UmengConfig.POINTEXCHANGEACTIVITY);
        MobclickAgent.onResume(this);
        initWebView();
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(UmengConfig.POINTEXCHANGEACTIVITY);
        MobclickAgent.onPause(this);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        SharedPrefUtil.saveIsFromCoupon(true);
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //改写物理返回键的逻辑
        if(keyCode==KeyEvent.KEYCODE_BACK) {
            if(wvPointExchange.getTitle().equals("积分商城")){
                finish();
            }
            if(wvPointExchange.canGoBack()) {
                wvPointExchange.goBack();//返回上一页面
                return true;
            } else {
                finish();//退出程序
            }
        }
        return super.onKeyDown(keyCode, event);
    }
}
