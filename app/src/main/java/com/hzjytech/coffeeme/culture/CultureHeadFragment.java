package com.hzjytech.coffeeme.culture;


import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.hzjytech.coffeeme.R;
import com.hzjytech.coffeeme.configurations.UmengConfig;
import com.hzjytech.coffeeme.entities.NewCulture;
import com.hzjytech.coffeeme.fragments.BaseFragment;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.umeng.analytics.MobclickAgent;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;

@ContentView(R.layout.fragment_culture_head)
public class CultureHeadFragment extends BaseFragment {
    private static final String URL = "url";
    private static final String URL_ARTICLE ="url_article" ;

    private String pageName;
    private  NewCulture.BannersBean.BannersContentBean mBanner;
    private ImageView ivCultureHeadShow;
    private TextView tvCultureHeadShow;

    @Event(R.id.ivCultureBannerShow)
    private void onCulturePosterShowClick(View v){
        Context context = getActivity();
        MobclickAgent.onEvent(context,UmengConfig.EVENT_CULTURE_CLICK);
        Intent intent=new Intent(context, CultureDetailActivity.class);
        intent.putExtra(URL_ARTICLE,mBanner.getArticle_url());
        context.startActivity(intent);
    }


    public CultureHeadFragment() {
        pageName=CultureHeadFragment.class.getSimpleName();
        // Required empty public constructor
    }
    public static CultureHeadFragment newInstance(NewCulture.BannersBean.BannersContentBean bannersBean) {
        CultureHeadFragment fragment = new CultureHeadFragment();
        Bundle args = new Bundle();
        args.putParcelable("banner",bannersBean);
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onResume() {
        super.onResume();

        if(getUserVisibleHint()){
            onVisibilityChangedToUser(true,false);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if(getUserVisibleHint()){
            onVisibilityChangedToUser(false, false);
        }

    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if(isResumed()){
            onVisibilityChangedToUser(isVisibleToUser, true);
        }
    }

    public void onVisibilityChangedToUser(boolean isVisibleToUser, boolean isHappenedInSetUserVisibleHintMethod){
        if(isVisibleToUser){
            if(pageName != null){
                MobclickAgent.onPageStart(UmengConfig.CULTUREPOSTERFRAGMENT);
            }
        }else{
            if(pageName != null){
                MobclickAgent.onPageEnd(UmengConfig.CULTUREPOSTERFRAGMENT);
            }
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mBanner = getArguments().getParcelable("banner");

        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ivCultureHeadShow=(ImageView)view.findViewById(R.id.ivCultureBannerShow);
        tvCultureHeadShow=(TextView)view.findViewById(R.id.tv_banner_head_title);

        DisplayImageOptions options=new DisplayImageOptions.Builder()
                .cacheInMemory(true)/*缓存至内存*/
                .cacheOnDisk(true)/*缓存值SDcard*/
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();
        ImageLoader.getInstance().displayImage(mBanner.getImage_url(),ivCultureHeadShow,options);
        tvCultureHeadShow.setText(mBanner.getTitle());

    }


}
