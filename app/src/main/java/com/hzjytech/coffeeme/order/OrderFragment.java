package com.hzjytech.coffeeme.order;


import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.hzjytech.banner.SmartFragmentStatePagerAdapter;
import com.hzjytech.coffeeme.R;
import com.hzjytech.coffeeme.configurations.Configurations;
import com.hzjytech.coffeeme.configurations.UmengConfig;
import com.hzjytech.coffeeme.entities.Good;
import com.hzjytech.coffeeme.entities.Order;
import com.hzjytech.coffeeme.fragments.BaseFragment;
import com.hzjytech.coffeeme.home.NewCartActivity;
import com.hzjytech.coffeeme.utils.LogUtil;
import com.hzjytech.coffeeme.utils.SharedPrefUtil;
import com.hzjytech.coffeeme.utils.SignUtils;
import com.hzjytech.coffeeme.utils.TimeUtil;
import com.hzjytech.coffeeme.utils.UserUtils;
import com.hzjytech.coffeeme.widgets.BadgeView;
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

@ContentView(R.layout.fragment_order)
public class OrderFragment extends BaseFragment{

    public static final int ORDER_STATUS_UNFINISHED = 0;
    public static final int ORDER_STATUS_ALL = 1;

    private List<Order> orderList=new ArrayList<>();
    private OrderFragmentAdapter adapter;

    @ViewInject(R.id.titleBar)
    private TitleBar tbOrderfrgTitle;

    @ViewInject(R.id.tblayoutOrderfrgTab)
    private TabLayout tblayoutOrderfrgTab;

    @ViewInject(R.id.vPgOrderfrgShow)
    private ViewPager vPgOrderfrgShow;
    @ViewInject(R.id.ivHomefrgCart)
    private ImageView ivHomefrgCart;

    private OrderItemFragment allfra;
    private OrderItemFragment partfra;


    private List<Fragment> list_fragment;
    private List<String> list_title;
    private BadgeView cartBadgeView;
    private String device_id;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onResume() {
        super.onResume();
        showLoading();
        MobclickAgent.onPageStart(UmengConfig.ORDERFRAGMENT);
        getCartGoodsCount();


    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        //getCartGoodsCount();
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(UmengConfig.ORDERFRAGMENT);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initView();

    }
    private void initView() {
        cartBadgeView = new BadgeView(getContext(),ivHomefrgCart);
        initCartGoodsCount();
        partfra = new OrderItemFragment();
        partfra.setStatus(ORDER_STATUS_UNFINISHED);

        allfra = new OrderItemFragment();
        allfra.setStatus(ORDER_STATUS_ALL);

        list_fragment = new ArrayList<>();
        list_fragment.add(partfra);
        list_fragment.add(allfra);

        list_title = new ArrayList<>();
        list_title.add("未完成");
        list_title.add("全部");

        tbOrderfrgTitle.setTitle("我的订单");
        tbOrderfrgTitle.setTitleColor(Color.WHITE);


        tblayoutOrderfrgTab.setTabMode(TabLayout.MODE_FIXED);
        tblayoutOrderfrgTab.addTab(tblayoutOrderfrgTab.newTab().setText(list_title.get(0)));
        tblayoutOrderfrgTab.addTab(tblayoutOrderfrgTab.newTab().setText(list_title.get(1)));
        adapter = new OrderFragmentAdapter(getChildFragmentManager(),list_fragment,list_title);
        vPgOrderfrgShow.setAdapter(adapter);
        tblayoutOrderfrgTab.setupWithViewPager(vPgOrderfrgShow);

    }
    private void initCartGoodsCount() {
        //getCartGoodsCount();

        ivHomefrgCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!SharedPrefUtil.getLoginType().equals(SharedPrefUtil.LOGINING)) {
                    goLogin();
                    return;
                }
                startActivity(new Intent(getActivity(), NewCartActivity.class));
//                startActivity(new Intent(getActivity(), JPushMainActivity.class));

            }
        });

    }
    private void getCartGoodsCount() {

        if (!SharedPrefUtil.getLoginType().equals(SharedPrefUtil.LOGINING)) {
            cartBadgeView.hide();
            hideLoading();
            return;
        }
        RequestParams entity = new RequestParams(Configurations.URL_GOODS);
        entity.addParameter(Configurations.AUTH_TOKEN, UserUtils.getUserInfo().getAuth_token());
        String device_id= JPushInterface.getRegistrationID(getActivity());
        String timeStamp = TimeUtil.getCurrentTimeString();
        entity.addParameter(Configurations.TIMESTAMP, timeStamp);
        entity.addParameter(Configurations.DEVICE_ID, device_id);

        Map<String, String> map = new TreeMap<String, String>();
        map.put(Configurations.AUTH_TOKEN, UserUtils.getUserInfo().getAuth_token());
        entity.addParameter(Configurations.SIGN, SignUtils.createSignString(device_id, timeStamp, map));


        x.http().get(entity, new Callback.CommonCallback<JSONObject>() {
            @Override
            public void onSuccess(JSONObject result) {

                LogUtil.e("Home goods", result.toString());
                hideLoading();
                try {
                    checkResOld(result);
                    if (result.getInt(Configurations.STATUSCODE) == 200) {
                       int count = result.getJSONObject("results").getInt("goods_count");
                        cartBadgeView.setBadgePosition(BadgeView.POSITION_TOP_RIGHT);
                        if (count < 1) {
                            cartBadgeView.hide();

                        } else if (count < 10) {
                            cartBadgeView.setTextSize(9);
                            cartBadgeView.setText(String.valueOf(count));
                            cartBadgeView.show();

                        } else if (count < 100) {
                            cartBadgeView.setTextSize(8);
                            cartBadgeView.setText(String.valueOf(count));
                            cartBadgeView.show();
                        } else {
                            cartBadgeView.setTextSize(8);
                            cartBadgeView.setText(R.string.cart_count_max_value);
                            cartBadgeView.show();
                        }

                    }
//                    result.getJSONObject("results").getString("goods");
                } catch (JSONException e) {
                    e.printStackTrace();
                }

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


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        List<Fragment> frags = getChildFragmentManager().getFragments();
        if (frags != null) {
            for (Fragment f : frags) {
                if (f != null)
                    f.onActivityResult(requestCode,resultCode,data);
            }
        }
    }
    class OrderFragmentAdapter extends SmartFragmentStatePagerAdapter{

        private List<Fragment> list_fragment;
        private List<String> list_Title;


        public OrderFragmentAdapter(FragmentManager fm,List<Fragment> list_fragment,List<String> list_Title) {
            super(fm);
            this.list_fragment = list_fragment;
            this.list_Title = list_Title;
        }

        @Override
        public Fragment getItem(int position) {
            return list_fragment.get(position);
        }

        @Override
        public int getCount() {
            return list_Title.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return list_Title.get(position % list_Title.size());
        }
    }
}