package com.hzjytech.coffeeme.culture;

import android.graphics.Color;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.hzjytech.coffeeme.BaseActivity;
import com.hzjytech.coffeeme.R;
import com.hzjytech.coffeeme.configurations.UmengConfig;
import com.hzjytech.coffeeme.widgets.TitleBar;
import com.umeng.analytics.MobclickAgent;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

@ContentView(R.layout.activity_culture_detail)
public class CultureDetailActivity extends BaseActivity {

    @ViewInject(R.id.titleBar)
    private TitleBar tbCultureTitle;


    @ViewInject(R.id.wvCultureDetailShow)
    private WebView wvCultureDetailShow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getIntent() != null) {
            String url_article = getIntent().getStringExtra("url_article");
            if(!TextUtils.isEmpty(url_article)){

                showLoading();
                wvCultureDetailShow.getSettings().setJavaScriptEnabled(true);
                wvCultureDetailShow.getSettings().setSupportZoom(false);//缩放
                /**
                 * 用WebView显示图片，可使用这个参数 设置网页布局类型：
                 * 1、LayoutAlgorithm.NARROW_COLUMNS:适应内容大小
                 * 2、LayoutAlgorithm.SINGLE_COLUMN:适应屏幕，内容将自动缩放
                 */
                wvCultureDetailShow.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
                wvCultureDetailShow.getSettings().setUseWideViewPort(true);//将图片调整到适合webview的大小
                wvCultureDetailShow.getSettings().setDomStorageEnabled(true);
                wvCultureDetailShow.getSettings().setLoadWithOverviewMode(true);
                wvCultureDetailShow.loadUrl(url_article);
                wvCultureDetailShow.setWebViewClient(new WebViewClient(){
                    @Override
                    public void onPageFinished(WebView view, String url) {
                        hideLoading();
                        tbCultureTitle.setTitle(view.getTitle());
                    }
                });
            }

        } else {
            tbCultureTitle.setTitle("文化");
        }
        tbCultureTitle.setTitleColor(Color.WHITE);
        tbCultureTitle.setLeftClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        tbCultureTitle.setLeftImageResource(R.drawable.icon_left);

    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(UmengConfig.CULTUREDETAILACTIVITY);
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(UmengConfig.CULTUREDETAILACTIVITY);
        MobclickAgent.onPause(this);
    }
}