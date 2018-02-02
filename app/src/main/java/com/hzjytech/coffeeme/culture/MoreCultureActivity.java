package com.hzjytech.coffeeme.culture;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.hzjytech.coffeeme.BaseActivity;
import com.hzjytech.coffeeme.R;
import com.hzjytech.coffeeme.adapters.MoreCutureAdapter;
import com.hzjytech.coffeeme.entities.NewCulture;
import com.hzjytech.coffeeme.widgets.TitleBar;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by hehongcan on 2017/11/15.
 */

public class MoreCultureActivity extends BaseActivity {
    private static final int TAG_BANNER = 0;
    private static final int TAG_PLATE = 1;
    @BindView(R.id.titleBar)
    TitleBar mTitleBar;
    @BindView(R.id.rv_more_culture)
    RecyclerView mRvMoreCulture;
    private int tag = -1;
    private NewCulture.PlatesBean mPlates;
    private NewCulture.BannersBean mBanners;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more_culture);
        ButterKnife.bind(this);
        initIntent();
        initView();
    }

    private void initView() {
      mTitleBar.setLeftImageResource(R.drawable.icon_left);
        if(tag==TAG_BANNER){
            mTitleBar.setTitle(mBanners.getBanners_name());
        }else if(tag==TAG_PLATE){
            mTitleBar.setTitle(mPlates.getPlate_name());
        }
        mTitleBar.setLeftClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mTitleBar.setTitleColor(Color.WHITE);
        mRvMoreCulture.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
        mRvMoreCulture.setAdapter(new MoreCutureAdapter(this,tag,mPlates,mBanners!=null?mBanners.getBanners_content():null));
    }

    private void initIntent() {
        Intent intent = getIntent();
        mPlates = intent.getParcelableExtra("plates");
        mBanners = intent.getParcelableExtra("banners");
        if (mBanners != null) {
            tag = TAG_BANNER;
        } else if (mPlates != null) {
            tag = TAG_PLATE;
        } else {
            finish();
        }
    }
}
