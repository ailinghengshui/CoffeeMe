package com.hzjytech.coffeeme.culture;

import android.graphics.Color;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
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
                wvCultureDetailShow.loadUrl(url_article);
                wvCultureDetailShow.getSettings().setJavaScriptEnabled(true);

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