package com.hzjytech.coffeeme.culture;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hzjytech.banner.SmartFragmentStatePagerAdapter;
import com.hzjytech.coffeeme.R;
import com.hzjytech.coffeeme.adapters.NewCultureAdapter;
import com.hzjytech.coffeeme.configurations.Configurations;
import com.hzjytech.coffeeme.configurations.UmengConfig;
import com.hzjytech.coffeeme.entities.Culture;
import com.hzjytech.coffeeme.entities.NewCulture;
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
    TitleBar tbCultureTitle;
    @ViewInject(R.id.ll_old_culture)
    LinearLayout mLLOldCulture;
    @ViewInject(R.id.ll_new_culture)
    private LinearLayout mLlNewCulture;
    @ViewInject(R.id.rv_new_culture)
    private RecyclerView mRvCulture;

    private List<Culture> cultureLists = new ArrayList<Culture>();
    private ViewPager vPgCultureShow;
    private CultureFragmentAdapter adapter;
    private NewCultureAdapter mAdapter;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initTitle();
        vPgCultureShow = (ViewPager) view.findViewById(R.id.vPgCultureShow);

        showLoading();
        newCultureRequest();
        //showNewCulture();


    }

    /**
     * 新接口
     */
    private void newCultureRequest() {
        RequestParams entity = new RequestParams(Configurations.URL_NEW_CULTURE);
        String device_id = JPushInterface.getRegistrationID(getContext());
        String timeStamp = TimeUtil.getCurrentTimeString();
        entity.addParameter(Configurations.TIMESTAMP, timeStamp);
        entity.addParameter(Configurations.DEVICE_ID, device_id);

        entity.addParameter(Configurations.SIGN, SignUtils.createSignString(device_id, timeStamp));


        x.http()
                .get(entity, new Callback.CommonCallback<String>() {
                    @Override
                    public void onSuccess(String result) {
                        try {
                        JSONObject jsonObject = new JSONObject(result);
                        if(jsonObject.getInt(Configurations.STATUSCODE)==200){
                            hideLoading();
                            JSONObject object = jsonObject.getJSONObject("results");
                            NewCulture culture = new Gson().fromJson(object.toString(),
                                    NewCulture.class);
                            showNewCulture();
                            mAdapter.setCulture(culture);
                        }else{
                            showOldCulture();
                        }

                        } catch (JSONException e) {
                            showOldCulture();
                        }
                    }

                    @Override
                    public void onError(Throwable ex, boolean isOnCallback) {
                        showOldCulture();
                    }

                    @Override
                    public void onCancelled(CancelledException cex) {

                    }

                    @Override
                    public void onFinished() {

                    }
                });
    }

    /**
     * 显示新文化页面
     */
    private void showNewCulture() {
        mLlNewCulture.setVisibility(View.VISIBLE);
        mLLOldCulture.setVisibility(View.GONE);
        mRvCulture.setLayoutManager(new LinearLayoutManager(getActivity(),
                LinearLayoutManager.VERTICAL,
                false));
        mAdapter = new NewCultureAdapter(getActivity(), this, null);
        mRvCulture.setAdapter(mAdapter);
        //测试代码
      /*  hideLoading();
        String result = "{\n" + "  \"banners\": {\n" + "    \"banners_id\":1,\n" + "    \"banners_name\":\"极伽活动\",\n" + "    \"banners_content\":[\n" + "    {\n" + "      \"id\": 1,\n" + "      \"title\": \"极伽时光“碰撞”魔都背后的故事\",\n" + "      \"content\":\"极伽时光在魔都的故事，要是不睡觉的话，怕也要说上三 天三夜。\",\n" + "      \"image_url\": \"http://banners.qiniu.jijiakafei.com/UpSfAsYf-APP%E8%BD%AE%E6%92%AD%E5%9B%BE.jpg\",\n" + "      \"article_url\": \"http://www.baidu.com\",\n" + "       \"create_at\":\"2017-11-14 15:00:00\"\n" + "    }, {\n" + "      \"id\": 1,\n" + "      \"title\": \"极伽时光“碰撞”魔都背后的故事\",\n" + "      \"content\":\"极伽时光在魔都的故事，要是不睡觉的话，怕也要说上三 天三夜。\",\n" + "      \"image_url\": \"http://banners.qiniu.jijiakafei.com/UpSfAsYf-APP%E8%BD%AE%E6%92%AD%E5%9B%BE.jpg\",\n" + "      \"article_url\": \"http://www.baidu.com\",\n" + "       \"create_at\":\"2017-11-14 15:00:00\"\n" + "    }, {\n" + "      \"id\": 1,\n" + "      \"title\": \"极伽时光“碰撞”魔都背后的故事\",\n" + "      \"content\":\"极伽时光在魔都的故事，要是不睡觉的话，怕也要说上三 天三夜。\",\n" + "      \"image_url\": \"http://banners.qiniu.jijiakafei.com/UpSfAsYf-APP%E8%BD%AE%E6%92%AD%E5%9B%BE.jpg\",\n" + "      \"article_url\": \"http://www.baidu.com\",\n" + "       \"create_at\":\"2017-11-14 15:00:00\"\n" + "    }, {\n" + "      \"id\": 1,\n" + "      \"title\": \"极伽时光“碰撞”魔都背后的故事\",\n" + "      \"content\":\"极伽时光在魔都的故事，要是不睡觉的话，怕也要说上三 天三夜。\",\n" + "      \"image_url\": \"http://banners.qiniu.jijiakafei.com/UpSfAsYf-APP%E8%BD%AE%E6%92%AD%E5%9B%BE.jpg\",\n" + "      \"article_url\": \"http://www.baidu.com\",\n" + "       \"create_at\":\"2017-11-14 15:00:00\"\n" + "    }, {\n" + "      \"id\": 1,\n" + "      \"title\": \"极伽时光“碰撞”魔都背后的故事\",\n" + "      \"content\":\"极伽时光在魔都的故事，要是不睡觉的话，怕也要说上三 天三夜。\",\n" + "      \"image_url\": \"http://banners.qiniu.jijiakafei.com/UpSfAsYf-APP%E8%BD%AE%E6%92%AD%E5%9B%BE.jpg\",\n" + "      \"article_url\": \"http://www.baidu.com\",\n" + "       \"create_at\":\"2017-11-14 15:00:00\"\n" + "    }\n" + "  ]\n" + "  },\n" + "  \"plates\": [\n" + "    {\n" + "      \"plate_id\": 1,\n" + "      \"plate_name\": \"极伽志趣\",\n" + "      \"plate_content\": [\n" + "      ]\n" + "    },\n" + "    {\n" + "      \"plate_id\": 2,\n" + "      \"plate_name\": \"极伽品牌\",\n" + "      \"plate_content\": [\n" + "        {\n" + "          \"id\": 1,\n" + "          \"title\": \"殊不知，极伽与 CHARLIE’S 还有如此一段 不为人知的故事\",\n" + "          \"content\": \"那时候的“极伽时光”还叫“极伽咖啡”，一切都如襁 褓中的婴儿，只有一个蓬勃生机跟向上的激情...\",\n" + "          \"image_url\": \"http://banners.qiniu.jijiakafei.com/UpSfAsYf-APP%E8%BD%AE%E6%92%AD%E5%9B%BE.jpg\",\n" + "          \"article_url\": \"http://banners.qiniu.jijiakafei.com/UpSfAsYf-APP%E8%BD%AE%E6%92%AD%E5%9B%BE.jpg\",\n" + "          \"create_at\":\"2017-11-14 15:00:00\"\n" + "        },\n" + "         {\n" + "          \"id\": 1,\n" + "          \"title\": \"殊不知，极伽与 CHARLIE’S 还有如此一段 不为人知的故事\",\n" + "          \"content\": \"那时候的“极伽时光”还叫“极伽咖啡”，一切都如襁 褓中的婴儿，只有一个蓬勃生机跟向上的激情...\",\n" + "          \"image_url\": \"http://banners.qiniu.jijiakafei.com/UpSfAsYf-APP%E8%BD%AE%E6%92%AD%E5%9B%BE.jpg\",\n" + "          \"article_url\": \"http://banners.qiniu.jijiakafei.com/UpSfAsYf-APP%E8%BD%AE%E6%92%AD%E5%9B%BE.jpg\",\n" + "          \"create_at\":\"2017-11-14 15:00:00\"\n" + "        }\n" + "      ]\n" + "    }\n" + "  ]\n" + "}";
        NewCulture culture = new Gson().fromJson(result, NewCulture.class);
        mAdapter.setCulture(culture);*/
    }

    /**
     * 显示旧文化页
     */
    private void showOldCulture() {
        mLlNewCulture.setVisibility(View.GONE);
        mLLOldCulture.setVisibility(View.VISIBLE);
        vPgCultureShow.setClipToPadding(false);
        vPgCultureShow.setPageMargin(20);
        vPgCultureShow.setOffscreenPageLimit(3);
        adapter = new CultureFragmentAdapter(getChildFragmentManager());

        vPgCultureShow.setPageTransformer(true, new ScaleInTransformer());
        vPgCultureShow.setAdapter(adapter);
        oldCultureRequest();
    }

    /**
     * luby请求接口
     */
    private void oldCultureRequest() {
        RequestParams entity = new RequestParams(Configurations.URL_CULTURE);

        String device_id = JPushInterface.getRegistrationID(getContext());
        String timeStamp = TimeUtil.getCurrentTimeString();
        entity.addParameter(Configurations.TIMESTAMP, timeStamp);
        entity.addParameter(Configurations.DEVICE_ID, device_id);

        entity.addParameter(Configurations.SIGN, SignUtils.createSignString(device_id, timeStamp));
        x.http()
                .get(entity, new Callback.CommonCallback<String>() {
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
        List<Culture> cultureList = new ArrayList<Culture>();

        try {
            String object = new JSONObject(result).getJSONObject("results")
                    .getString("cultures");
            cultureList = new Gson().fromJson(object,
                    new TypeToken<ArrayList<Culture>>() {}.getType());
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
            return CulturePosterFragment.newInstance(cultureLists.get(position)
                            .getImage_url(),
                    cultureLists.get(position)
                            .getArticle_url());
        }

        @Override
        public int getCount() {
            return cultureLists.size();
        }
    }
}