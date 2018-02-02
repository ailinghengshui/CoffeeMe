package com.hzjytech.coffeeme.me;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.hzjytech.coffeeme.BaseActivity;
import com.hzjytech.coffeeme.R;
import com.hzjytech.coffeeme.configurations.UmengConfig;
import com.hzjytech.coffeeme.utils.AppUtil;
import com.hzjytech.coffeeme.widgets.TitleBar;
import com.umeng.analytics.MobclickAgent;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;

@ContentView(R.layout.activity_aboutus)
public class AboutusActivity extends BaseActivity {

    @ViewInject(R.id.titleBar)
    private TitleBar tbAboutusTitle;
    @ViewInject(R.id.tvAboutusIntro)
    private TextView tvAboutusIntro;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tbAboutusTitle.setTitle("关于我们");
        tbAboutusTitle.setTitleColor(Color.WHITE);
        tbAboutusTitle.setLeftImageResource(R.drawable.icon_left);
        tbAboutusTitle.setLeftClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        tvAboutusIntro.setText(getString(R.string.version)+AppUtil.getVersionName(this));
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(UmengConfig.ABOUTUSACTIVITY);
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(UmengConfig.ABOUTUSACTIVITY);
        MobclickAgent.onPause(this);
    }
}