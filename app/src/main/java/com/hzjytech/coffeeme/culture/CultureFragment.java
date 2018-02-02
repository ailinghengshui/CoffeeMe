package com.hzjytech.coffeeme.culture;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hzjytech.banner.SmartFragmentStatePagerAdapter;
import com.hzjytech.coffeeme.R;
import com.hzjytech.coffeeme.configurations.Configurations;
import com.hzjytech.coffeeme.configurations.UmengConfig;
import com.hzjytech.coffeeme.entities.Culture;
import com.hzjytech.coffeeme.fragments.BaseFragment;
import com.hzjytech.coffeeme.utils.SignUtils;
import com.hzjytech.coffeeme.utils.TimeUtil;
import com.hzjytech.coffeeme.widgets.TitleBar;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import cn.jpush.android.api.JPushInterface;

/**
 * Created by Hades on 2016/3/9.
 */

@ContentView(R.layout.fragment_culture)
public class CultureFragment extends BaseFragment {

    @ViewInject(R.id.titleBar)
    private TitleBar tbCultureTitle;

    private List<Culture> cultureLists = new ArrayList<Culture>();
    private ViewPager vPgCultureShow;
    private CultureFragmentAdapter adapter;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initTitle();
        vPgCultureShow = (ViewPager) view.findViewById(R.id.vPgCultureShow);

        showLoading();
        RequestParams entity=new RequestParams(Configurations.URL_CULTURE);

        String device_id= JPushInterface.getRegistrationID(getContext());
        String timeStamp= TimeUtil.getCurrentTimeString();
        entity.addParameter(Configurations.TIMESTAMP, timeStamp);
        entity.addParameter(Configurations.DEVICE_ID,device_id );

        entity.addParameter(Configurations.SIGN, SignUtils.createSignString(device_id,timeStamp));


        x.http().get(entity, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                hideLoading();
                cultureLists = parseResult(result);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                hideLoading();
                showNetError();
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });

        vPgCultureShow.setClipToPadding(false);
        vPgCultureShow.setPageMargin(20);
        vPgCultureShow.setOffscreenPageLimit(3);
        adapter=new CultureFragmentAdapter(getChildFragmentManager());

        vPgCultureShow.setPageTransformer(true,new ScaleInTransformer());
        vPgCultureShow.setAdapter(adapter);

    }

    private void initTitle() {
        tbCultureTitle.setTitle("咖啡文化");
        tbCultureTitle.setTitleColor(Color.WHITE);
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(UmengConfig.CULTUREFRAGMENT);
    }


    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(UmengConfig.CULTUREFRAGMENT);
    }

    private List<Culture> parseResult(String result) {
        List<Culture> cultureList=new ArrayList<Culture>();

        try {
            String object=new JSONObject(result).getJSONObject("results").getString("cultures");
            cultureList=new Gson().fromJson(object,new TypeToken<ArrayList<Culture>>(){}.getType());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return cultureList;
    }


    class CultureFragmentAdapter extends SmartFragmentStatePagerAdapter {


        public CultureFragmentAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return CulturePosterFragment.newInstance(cultureLists.get(position).getImage_url(),cultureLists.get(position).getArticle_url());
        }

        @Override
        public int getCount() {
            return cultureLists.size();
        }
    }
}