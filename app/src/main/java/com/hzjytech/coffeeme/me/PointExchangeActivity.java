package com.hzjytech.coffeeme.me;

import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.hzjytech.coffeeme.BaseActivity;
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
    private boolean fromCoupon=true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initTitle();

    }
    private void initWebView() {
        WebSettings webSettings=wvPointExchange.getSettings();
        webSettings.setJavaScriptEnabled(true);
        wvPointExchange.addJavascriptInterface(new PointExchangeJsInteration(),"control");
        wvPointExchange.setWebChromeClient(new WebChromeClient(){
        });
        wvPointExchange.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                titleBar.setTitle(view.getTitle());
                hideLoading();
            }
        });
        showLoading();
        wvPointExchange.loadUrl(Configurations.URL_COMMODITIES+"?auth_token="+ UserUtils.getUserInfo().getAuth_token());
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
        public void gotoLogin(){
            goLogin();
        }
    }

    private void initTitle() {
        titleBar.setLeftClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
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
}
