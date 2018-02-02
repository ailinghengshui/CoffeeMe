package com.hzjytech.coffeeme.culture;


import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;

import com.hzjytech.coffeeme.R;
import com.hzjytech.coffeeme.configurations.UmengConfig;
import com.hzjytech.coffeeme.fragments.BaseFragment;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.umeng.analytics.MobclickAgent;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;

import java.security.KeyStore;

@ContentView(R.layout.fragment_culture_poster)
public class CulturePosterFragment extends BaseFragment {
    private static final String URL = "url";
    private static final String URL_ARTICLE ="url_article" ;

    private String url;
    private ImageView ivCulturePosterShow;
    private String url_article;
    private String pageName;

    @Event(R.id.ivCulturePosterShow)
    private void onCulturePosterShowClick(View v){
        MobclickAgent.onEvent(getParentFragment().getContext(),UmengConfig.EVENT_CULTURE_CLICK);
        Intent intent=new Intent(getActivity(), CultureDetailActivity.class);
        intent.putExtra(URL_ARTICLE,url_article);
        startActivity(intent);
    }


    public CulturePosterFragment() {
        pageName=CulturePosterFragment.class.getSimpleName();
        // Required empty public constructor
    }

    public static CulturePosterFragment newInstance(String url,String url_article) {
        CulturePosterFragment fragment = new CulturePosterFragment();
        Bundle args = new Bundle();
        args.putString(URL, url);
        args.putString(URL_ARTICLE,url_article);
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
            url = getArguments().getString(URL);
            url_article=getArguments().getString(URL_ARTICLE);
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ivCulturePosterShow=(ImageView)view.findViewById(R.id.ivCulturePosterShow);

        DisplayImageOptions options=new DisplayImageOptions.Builder()
                .cacheInMemory(true)/*缓存至内存*/
                .cacheOnDisk(true)/*缓存值SDcard*/
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();
        ImageLoader.getInstance().displayImage(url,ivCulturePosterShow,options);

    }
}
