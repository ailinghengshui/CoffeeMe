package com.hzjytech.coffeeme.me;

import android.graphics.Color;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.hzjytech.coffeeme.BaseActivity;
import com.hzjytech.coffeeme.R;
import com.hzjytech.coffeeme.configurations.Configurations;
import com.hzjytech.coffeeme.utils.UserUtils;
import com.hzjytech.coffeeme.widgets.TitleBar;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

@ContentView(R.layout.activity_about_point)
public class AboutPointActivity extends BaseActivity {
    @ViewInject(R.id.titleBar)
    private TitleBar titleBar;

    @ViewInject(R.id.wvAboutPoint)
    private WebView wvAboutPoint;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initTitle();
        initWebView();
    }

    private void initWebView() {
        WebSettings webSettings=wvAboutPoint.getSettings();
        webSettings.setJavaScriptEnabled(true);
        wvAboutPoint.setWebChromeClient(new WebChromeClient(){});
        wvAboutPoint.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                titleBar.setTitle(view.getTitle());
                hideLoading();
            }
        });
        showLoading();
//        wvAboutPoint.loadUrl("http://192.168.0.157:3000/about_point");
        wvAboutPoint.loadUrl(Configurations.URL_ABOUT_POINT);
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
}
