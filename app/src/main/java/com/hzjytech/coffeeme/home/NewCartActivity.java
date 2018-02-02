package com.hzjytech.coffeeme.home;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.hzjytech.coffeeme.BaseActivity;
import com.hzjytech.coffeeme.R;
import com.hzjytech.coffeeme.adapters.NewCartAdapter;
import com.hzjytech.coffeeme.adapterutil.VerticalSpaceItemDecoration;
import com.hzjytech.coffeeme.configurations.Configurations;
import com.hzjytech.coffeeme.configurations.UmengConfig;
import com.hzjytech.coffeeme.entities.Good;
import com.hzjytech.coffeeme.utils.DensityUtil;
import com.hzjytech.coffeeme.utils.LogUtil;
import com.hzjytech.coffeeme.utils.SignUtils;
import com.hzjytech.coffeeme.utils.TimeUtil;
import com.hzjytech.coffeeme.utils.ToastUtil;
import com.hzjytech.coffeeme.utils.UserUtils;
import com.hzjytech.coffeeme.widgets.SuperSwipeRefreshLayout;
import com.hzjytech.coffeeme.widgets.TitleBar;
import com.hzjytech.coffeeme.widgets.TouchableRecyclerView;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import cn.jpush.android.api.JPushInterface;

@ContentView(R.layout.activity_cart_new)
public class NewCartActivity extends BaseActivity {

    public static final int CHANGESEL = 100;

    @ViewInject(R.id.titleBar)
    private TitleBar tbCartTitle;

    @ViewInject(R.id.superSwipeCartRefresh)
    private SuperSwipeRefreshLayout superSwipeCartRefresh;

    @ViewInject(R.id.rcyViewCartShow)
    private TouchableRecyclerView rcyViewCartShow;
//    private XRecyclerView rcyViewCartShow;
      @ViewInject(R.id.ll_select_all)
      private RelativeLayout ll_select_all;
    @ViewInject(R.id.rbCartSelectall)
    private TextView rbCartSelectall;

    @ViewInject(R.id.rbCartSelectalltitle)
    private TextView rbCartSelectalltitle;

    @ViewInject(R.id.tvCartitemSum)
    private TextView tvCartitemSum;

    @ViewInject(R.id.emptyview)
    private TextView emptyview;

    @ViewInject(R.id.btnCart)
    private Button btnCart;
    private ProgressBar pbCartHeader;
    private ImageView ivCartHeader;
    private TextView tvCartHeader;

    private static NewCartActivity mInstance;
    private int page;
    private ProgressBar pbFooter;
    private ImageView ivFooter;
    private TextView tvFooter;

    public static NewCartActivity Instance() {
        if (null == mInstance)
            mInstance = new NewCartActivity();
        return mInstance;
    }

    /**
     * first Integer is the good id
     * second integer is the good count
     */

    @Event(R.id.ll_select_all)
    private void onCartSelectallClick(View v) {
        if (rbCartSelectall.isSelected()) {
            rbCartSelectall.setSelected(false);
            adapter.disSelectAll();
        } else {
            rbCartSelectall.setSelected(true);
            adapter.selectAll();
        }
    }


    @Event(R.id.rbCartSelectalltitle)
    private void onCartSelectalltitleClick(View v) {
        if (rbCartSelectall.isSelected()) {
            rbCartSelectall.setSelected(false);
            adapter.disSelectAll();
        } else {
            rbCartSelectall.setSelected(true);
            adapter.selectAll();
        }
    }


    @Event(R.id.btnCart)
    private void onbtnCartClick(View v) {
        int count = 0;
        for (int i : adapter.getSelectGood().values()) {
            count += i;
        }
        if (count == 0) {

            ToastUtil.showShort(NewCartActivity.this, getString(R.string.Cart_noselinfo));
            return;
        } else if (count > 9) {
            ToastUtil.showShort(NewCartActivity.this, getString(R.string.str_countlimit));
            return;
        }
        Intent intent = new Intent(NewCartActivity.this, NewPaymentActivity.class);
        intent.putExtra("type", 1);
        intent.putExtra("selgoods", (Serializable) adapter.getSelectGood());
        intent.putExtra("sum", Float.parseFloat(tvCartitemSum.getText().toString()));
        startActivity(intent);
    }

    private List<Good> goods = new ArrayList<>();
    private NewCartAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mInstance = this;
        initTitle();

    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(UmengConfig.NEWCARTACTIVITY);
        MobclickAgent.onResume(this);

        initView();
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(UmengConfig.NEWCARTACTIVITY);
        MobclickAgent.onPause(this);
    }

    private void initTitle() {
        tbCartTitle.setTitle("购物车");
        tbCartTitle.setTitleColor(Color.WHITE);
        tbCartTitle.setLeftImageResource(R.drawable.icon_back);
        tbCartTitle.setLeftClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                NewCartActivity.this.overridePendingTransition(R.anim.slide_in_left_base,
                        R.anim.slide_out_right_base);
            }
        });
    }

    private void initView() {
        final DecimalFormat fnum = new DecimalFormat("##0.00");
        adapter = new NewCartAdapter(this, goods);
        adapter.setOnCartAdapterListener(new NewCartAdapter.OnNewCartAdapterListener() {
            @Override
            public void notifySum(String sum) {
                tvCartitemSum.setText(String.valueOf(fnum.format(Float.valueOf(sum))));
            }

            @Override
            public void onSelectAll(boolean isSelectll) {
                rbCartSelectall.setSelected(isSelectll);
            }

            @Override
            public void onSelectedCount(int count) {
                if(count==0){
                    btnCart.setBackgroundColor(ContextCompat.getColor(getApplicationContext(),R.color.normal_grey));
                }else{
                    btnCart.setBackgroundColor(ContextCompat.getColor(getApplicationContext(),R.color.coffee));
                }
                btnCart.setText("提交订单"+"("+count+")");
            }
        });

        rcyViewCartShow.setLayoutManager(new LinearLayoutManager(NewCartActivity.this));
        rcyViewCartShow.setAdapter(adapter);
        superSwipeCartRefresh.setHeaderViewBackgroundColor(getResources().getColor(R.color.light_grey));
        superSwipeCartRefresh.setHeaderView(createHeaderView());
        superSwipeCartRefresh.setFooterView(createFooterView());
        superSwipeCartRefresh.setTargetScrollWithLayout(true);
        superSwipeCartRefresh.setOnPullRefreshListener(new SuperSwipeRefreshLayout.OnPullRefreshListener() {
            @Override
            public void onRefresh() {
                tvCartHeader.setText("正在刷新");
                ivCartHeader.setVisibility(View.GONE);
                pbCartHeader.setVisibility(View.VISIBLE);

                getGoods();

                adapter.closeOpenedSwipeItemLayoutWithAnim();
            }

            @Override
            public void onPullDistance(int distance) {

            }

            @Override
            public void onPullEnable(boolean enable) {
                tvCartHeader.setText(enable ? "松开刷新" : "下拉刷新");
                ivCartHeader.setVisibility(View.VISIBLE);
                ivCartHeader.setRotation(enable ? 180 : 0);
            }
        });
        superSwipeCartRefresh.setOnPushLoadMoreListener(new SuperSwipeRefreshLayout.OnPushLoadMoreListener() {
            @Override
            public void onLoadMore() {
                tvFooter.setText(getString(R.string.str_loading));
                ivFooter.setVisibility(View.INVISIBLE);
                pbFooter.setVisibility(View.VISIBLE);
                ivFooter.setVisibility(View.VISIBLE);
                pbFooter.setVisibility(View.INVISIBLE);
                loadMoreCarts();
            }

            @Override
            public void onPushDistance(int distance) {

            }

            @Override
            public void onPushEnable(boolean enable) {
                tvFooter.setText(enable ? getString(R.string.str_releasetoload) : getString(R.string.str_pushload));
                ivFooter.setVisibility(View.VISIBLE);
                ivFooter.setRotation(enable ? 0 : 180);
            }
        });

        showLoading();
        getGoods();

        adapter.closeOpenedSwipeItemLayoutWithAnim();
    }

    private void loadMoreCarts() {
        page+=1;
        RequestParams entity = new RequestParams(Configurations.URL_GOODS);

        if(UserUtils.getUserInfo() !=null) {
            entity.addParameter(Configurations.AUTH_TOKEN, UserUtils.getUserInfo().getAuth_token());

            String device_id= JPushInterface.getRegistrationID(NewCartActivity.this);
            String timeStamp= TimeUtil.getCurrentTimeString();
            entity.addParameter(Configurations.TIMESTAMP,timeStamp);
            entity.addParameter(Configurations.DEVICE_ID,device_id );
            entity.addParameter(Configurations.PAGE, page);
            Map<String, String> map=new TreeMap<String, String>();
            map.put(Configurations.AUTH_TOKEN, UserUtils.getUserInfo().getAuth_token());
            map.put(Configurations.PAGE,String.valueOf(page));
            entity.addParameter(Configurations.SIGN, SignUtils.createSignString(device_id,timeStamp,map));
        }
        x.http().get(entity, new Callback.CommonCallback<JSONObject>() {

            @Override
            public void onSuccess(JSONObject result) {


                checkResOld(result);
                try {
                    if (result.getInt(Configurations.STATUSCODE) == 200) {
                        superSwipeCartRefresh.setLoadMore(false);
                        List<Good> moreGoods = JSON.parseArray(result.getJSONObject("results").getString("goods"), Good.class);
                        LogUtil.e("goods", NewCartActivity.this.goods.toString());
                        if(moreGoods.size()>0){
                            adapter.addLoadMoreAll(moreGoods);
                            rbCartSelectall.setSelected(false);
                        }else{
                            page--;
                            ToastUtil.showShort(NewCartActivity.this, "沒有更多了");
                        }

                    }else{
                        superSwipeCartRefresh.setLoadMore(false);
                        ToastUtil.showShort(NewCartActivity.this,result.getString(Configurations.STATUSMSG));
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                superSwipeCartRefresh.setLoadMore(false);
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

    private View createHeaderView(){
        View headerView = LayoutInflater.from(superSwipeCartRefresh.getContext())
                .inflate(R.layout.cart_layout_head, null);
        pbCartHeader = (ProgressBar) headerView
                .findViewById(R.id.pbCartHeader);
        ivCartHeader = (ImageView) headerView
                .findViewById(R.id.ivCartHeader);
        tvCartHeader = (TextView) headerView
                .findViewById(R.id.tvCartHeader);
        pbCartHeader.setVisibility(View.GONE);
        ivCartHeader.setVisibility(View.VISIBLE);
        ivCartHeader.setImageResource(R.drawable.ic_pulltorefresh_arrow);
        tvCartHeader.setText("下拉刷新");
        return headerView;
    }
    private View createFooterView() {
        View footerView = LayoutInflater.from(superSwipeCartRefresh.getContext())
                .inflate(R.layout.layout_footer, null);
        pbFooter = (ProgressBar) footerView.findViewById(R.id.pbFooter);
        ivFooter = (ImageView) footerView.findViewById(R.id.ivFooter);
        tvFooter = (TextView) footerView.findViewById(R.id.tvFooter);
        pbFooter.setVisibility(View.INVISIBLE);
        ivFooter.setVisibility(View.VISIBLE);
        ivFooter.setImageResource(R.drawable.ic_pulltorefresh_arrow);
        tvFooter.setText(getString(R.string.str_loadmore));
        return footerView;

    }

    private void getGoods() {
        page =1;
        RequestParams entity = new RequestParams(Configurations.URL_GOODS);

        if(UserUtils.getUserInfo() !=null) {
            entity.addParameter(Configurations.AUTH_TOKEN, UserUtils.getUserInfo().getAuth_token());

            String device_id= JPushInterface.getRegistrationID(NewCartActivity.this);
            String timeStamp= TimeUtil.getCurrentTimeString();
            entity.addParameter(Configurations.TIMESTAMP,timeStamp);
            entity.addParameter(Configurations.DEVICE_ID,device_id );
             entity.addParameter(Configurations.PAGE, page);
            Map<String, String> map=new TreeMap<String, String>();
            map.put(Configurations.AUTH_TOKEN, UserUtils.getUserInfo().getAuth_token());
            map.put(Configurations.PAGE,String.valueOf(page));
            entity.addParameter(Configurations.SIGN, SignUtils.createSignString(device_id,timeStamp,map));
        }
        x.http().get(entity, new Callback.CommonCallback<JSONObject>() {

            @Override
            public void onSuccess(JSONObject result) {


                checkResOld(result);
                try {
                    if (result.getInt(Configurations.STATUSCODE) == 200) {

                        hideLoading();
                        superSwipeCartRefresh.setRefreshing(false);
                        pbCartHeader.setVisibility(View.GONE);
                        goods = JSON.parseArray(result.getJSONObject("results").getString("goods"), Good.class);
                        LogUtil.e("goods",goods.toString());
                        if(goods.size()>0){
                            adapter.addRefreshAll(goods);
                            emptyview.setVisibility(View.GONE);

                            tvCartitemSum.setText("0");
                            rbCartSelectall.setSelected(false);
                            adapter.disSelectAll();
                            btnCart.setText("提交订单" + "(" + 0 + ")");
                        }else{
                            adapter.addRefreshAll(goods);
                            emptyview.setVisibility(View.VISIBLE);
                            emptyview.setText("您的购物车为空~");
                        }

                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                hideLoading();
                superSwipeCartRefresh.setRefreshing(false);
                showNetError();
                pbCartHeader.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });
    }

}