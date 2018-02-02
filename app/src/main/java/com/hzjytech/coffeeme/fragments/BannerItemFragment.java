package com.hzjytech.coffeeme.fragments;


import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.hzjytech.coffeeme.R;
import com.hzjytech.coffeeme.home.BannerDetailActivity;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;

@ContentView(R.layout.fragment_banner_item)
public class BannerItemFragment extends BaseFragment {

    private static final String TAG =BannerItemFragment.class.getSimpleName() ;
    private static final String PARAM_ARTICLE_URL = "article_url";
    private ImageView ivFgBannerItem;
    private static final String PARAM_IMAGE_KEY = "image_key";

    private String image_key;
    private String article_url;


    @Event(R.id.ivFgBannerItem)
    private void onBannerItemClick(View view){
        Intent intent=new Intent(getActivity(),BannerDetailActivity.class);
        intent.putExtra("url_article",article_url);
        startActivity(intent);

    }


    public BannerItemFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_banner_item,container,false);
        ivFgBannerItem= (ImageView) view.findViewById(R.id.ivFgBannerItem);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        DisplayImageOptions options=new DisplayImageOptions.Builder()
                .cacheInMemory(true)/*缓存至内存*/
                .cacheOnDisk(true)/*缓存值SDcard*/
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();
        ImageLoader.getInstance().displayImage(image_key,ivFgBannerItem,options);

    }

    public static BannerItemFragment newInstance(String image_key,String article_url) {
        BannerItemFragment fragment = new BannerItemFragment();
        Bundle args = new Bundle();
        args.putString(PARAM_IMAGE_KEY, image_key);
        args.putString(PARAM_ARTICLE_URL, article_url);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            image_key = getArguments().getString(PARAM_IMAGE_KEY);
            article_url= getArguments().getString(PARAM_ARTICLE_URL);
        }
    }

}
