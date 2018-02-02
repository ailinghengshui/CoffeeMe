package com.hzjytech.coffeeme.authorization;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.hzjytech.coffeeme.BaseActivity;
import com.hzjytech.coffeeme.R;
import com.hzjytech.coffeeme.configurations.Configurations;
import com.hzjytech.coffeeme.widgets.TitleBar;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

/**
 * Created by dblr4287 on 2016/5/30.
 */
@ContentView(R.layout.activity_useragreement)
public class UserAgreementActivity extends BaseActivity{


    @ViewInject(R.id.titleBar)
    private TitleBar titletb;
    @ViewInject(R.id.useragree_web)
    private WebView web;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        titletb.setTitle("用户协议");
        titletb.setTitleColor(Color.WHITE);
        titletb.setLeftImageResource(R.drawable.icon_left);
        titletb.setLeftClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //启用支持javascript
        WebSettings settings = web.getSettings();
        settings.setJavaScriptEnabled(true);
        web.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        web.loadUrl(Configurations.URL_AGREEMENT);
        web.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                view.loadUrl(null);
                return true;
            }
        });
        web.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (newProgress == 100) {
                    // 网页加载完成

                } else {
                    // 加载中

                }

            }
        });

    }
}
