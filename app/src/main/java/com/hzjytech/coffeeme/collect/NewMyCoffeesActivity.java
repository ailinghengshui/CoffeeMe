package com.hzjytech.coffeeme.collect;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.hzjytech.coffeeme.BaseActivity;
import com.hzjytech.coffeeme.R;
import com.hzjytech.coffeeme.adapterutil.FadeInAnimator;
import com.hzjytech.coffeeme.configurations.Configurations;
import com.hzjytech.coffeeme.configurations.UmengConfig;
import com.hzjytech.coffeeme.entities.AppItems;
import com.hzjytech.coffeeme.utils.LogUtil;
import com.hzjytech.coffeeme.utils.SignUtils;
import com.hzjytech.coffeeme.utils.TimeUtil;
import com.hzjytech.coffeeme.utils.ToastUtil;
import com.hzjytech.coffeeme.utils.UserUtils;
import com.hzjytech.coffeeme.widgets.SuperSwipeRefreshLayout;
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

@ContentView(R.layout.activity_new_my_coffees)
public class NewMyCoffeesActivity extends BaseActivity {

    private static final String TAG = NewMyCoffeesActivity.class.getSimpleName();
    private static final int REFRESH_DATA = 2;
    private int page = 1;
    @ViewInject(R.id.titleBar)
    private TitleBar tbMycoffeesTitle;

    @ViewInject(R.id.rcyViewMyCoffeesShow)
    private RecyclerView rcyViewMyCoffeesShow;

    @ViewInject(R.id.swpMyCoffeesRefresh)
    private SuperSwipeRefreshLayout swpMyCoffeesRefresh;

    @ViewInject(R.id.emptyview)
    private TextView emptyview;

    private final int NOTIFY_DATA = 1;

    private List<AppItems> mAppItems = new ArrayList<>();


    private NewMyCoffeesAdapter adapter;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case NOTIFY_DATA:
                    adapter.addMoreData(mAppItems);
                    break;
                case REFRESH_DATA:
                    adapter.addRefreshData(mAppItems);
                    hideLoading();
                    break;
            }
        }

    };
    private ProgressBar pbCartHeader;
    private ImageView ivCartHeader;
    private TextView tvCartHeader;
    private ProgressBar pbFooter;
    private ImageView ivFooter;
    private TextView tvFooter;

    private static NewMyCoffeesActivity mInstance;

    public static NewMyCoffeesActivity Instance() {
        if (null == mInstance)
            mInstance = new NewMyCoffeesActivity();
        return mInstance;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mInstance = this;
        initTitle();
        showLoading();
        setRecyclerView();

        refreshData();

    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(UmengConfig.NEWMYCOFFEESACTIVITY);
        MobclickAgent.onResume(this);
        refreshData();
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(UmengConfig.NEWMYCOFFEESACTIVITY);
        MobclickAgent.onPause(this);
    }

    private void initTitle() {
        tbMycoffeesTitle.setTitle("我的收藏");
        tbMycoffeesTitle.setTitleColor(Color.WHITE);
        tbMycoffeesTitle.setLeftImageResource(R.drawable.icon_left);
        tbMycoffeesTitle.setLeftClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void setRecyclerView() {
        final GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return adapter.isHeader(position) ? gridLayoutManager.getSpanCount() : 1;
            }
        });
        rcyViewMyCoffeesShow.setLayoutManager(gridLayoutManager);
        rcyViewMyCoffeesShow.setItemAnimator(new FadeInAnimator(new OvershootInterpolator(1f)));
        rcyViewMyCoffeesShow.getItemAnimator().setRemoveDuration(200);

        adapter = new NewMyCoffeesAdapter(this, mAppItems);

        adapter.setDelListener(new NewMyCoffeesAdapter.DelListener() {
            @Override
            public void onDelListener() {
                refreshData();
            }
        });
        rcyViewMyCoffeesShow.setAdapter(adapter);

        swpMyCoffeesRefresh.setHeaderViewBackgroundColor(getResources().getColor(R.color.light_grey));
        swpMyCoffeesRefresh.setHeaderView(createHeaderView());
        swpMyCoffeesRefresh.setTargetScrollWithLayout(true);
        swpMyCoffeesRefresh.setOnPullRefreshListener(new SuperSwipeRefreshLayout.OnPullRefreshListener() {
            @Override
            public void onRefresh() {
                tvCartHeader.setText("正在刷新");
                ivCartHeader.setVisibility(View.GONE);
                pbCartHeader.setVisibility(View.VISIBLE);
                refreshData();
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
        swpMyCoffeesRefresh.setFooterView(createFooterView());
        swpMyCoffeesRefresh.setOnPushLoadMoreListener(new SuperSwipeRefreshLayout.OnPushLoadMoreListener() {
            @Override
            public void onLoadMore() {
                tvFooter.setText(getString(R.string.str_loading));
                ivFooter.setVisibility(View.GONE);
                pbFooter.setVisibility(View.VISIBLE);
                ivFooter.setVisibility(View.VISIBLE);
                pbFooter.setVisibility(View.GONE);
                loadMoreData();
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

    }

    private View createHeaderView() {
        View headerView = LayoutInflater.from(swpMyCoffeesRefresh.getContext())
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
        tvCartHeader.setText(getString(R.string.str_refresh));
        return headerView;
    }

    private View createFooterView() {
        View footerView = LayoutInflater.from(swpMyCoffeesRefresh.getContext())
                .inflate(R.layout.layout_footer, null);
        pbFooter = (ProgressBar) footerView.findViewById(R.id.pbFooter);
        ivFooter = (ImageView) footerView.findViewById(R.id.ivFooter);
        tvFooter = (TextView) footerView.findViewById(R.id.tvFooter);
        pbFooter.setVisibility(View.GONE);
        ivFooter.setVisibility(View.VISIBLE);
        ivFooter.setImageResource(R.drawable.ic_pulltorefresh_arrow);
        tvFooter.setText(getString(R.string.str_loadmore));
        return footerView;

    }

    private void refreshData() {
        if(UserUtils.getUserInfo()==null){
            goLogin();
            return;
        }
        page = 1;
        final RequestParams entity = new RequestParams(Configurations.URL_APP_ITEMS + "/my");
        entity.addParameter(Configurations.AUTH_TOKEN, UserUtils.getUserInfo().getAuth_token());
        entity.addParameter(Configurations.PAGE, page);
        String device_id= JPushInterface.getRegistrationID(NewMyCoffeesActivity.this);
        String timeStamp= TimeUtil.getCurrentTimeString();
        entity.addParameter(Configurations.TIMESTAMP, timeStamp);
        entity.addParameter(Configurations.DEVICE_ID,device_id );

        Map<String ,String > map=new TreeMap<>();
        map.put(Configurations.AUTH_TOKEN, UserUtils.getUserInfo().getAuth_token());
        map.put(Configurations.PAGE, String.valueOf(page));

        entity.addParameter(Configurations.SIGN, SignUtils.createSignString(device_id,timeStamp,map));


        x.http().get(entity, new Callback.CommonCallback<JSONObject>() {
            @Override
            public void onSuccess(JSONObject result) {
                checkResOld(result);
                try {
                    if (result.getInt(Configurations.STATUSCODE) == 200) {
                        LogUtil.d("resultSuccess", result.getString("results"));
                        mAppItems = JSON.parseArray(result.getJSONObject("results").getString("app_items"), AppItems.class);
                        if (mAppItems.size() > 0) {
                            mHandler.sendEmptyMessage(REFRESH_DATA);
                            emptyview.setVisibility(View.GONE);
                        } else {
                            emptyview.setVisibility(View.VISIBLE);
                            emptyview.setText("你还没有收藏哦~");
                            ToastUtil.showShort(NewMyCoffeesActivity.this, "沒有更多了");
                        }
                    } else {
                        ToastUtil.showShort(NewMyCoffeesActivity.this, result.getString(Configurations.STATUSMSG));
                    }

                    swpMyCoffeesRefresh.setRefreshing(false);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                if(swpMyCoffeesRefresh.isRefreshing()) {
                    swpMyCoffeesRefresh.setRefreshing(false);
                }
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

    private void loadMoreData() {
        RequestParams entity = new RequestParams(Configurations.URL_APP_ITEMS + "/my");
        entity.addParameter(Configurations.AUTH_TOKEN, UserUtils.getUserInfo().getAuth_token());
        page += 1;
        entity.addParameter(Configurations.PAGE, page);

        String device_id= JPushInterface.getRegistrationID(NewMyCoffeesActivity.this);
        String timeStamp= TimeUtil.getCurrentTimeString();
        entity.addParameter(Configurations.TIMESTAMP, timeStamp);
        entity.addParameter(Configurations.DEVICE_ID,device_id );

        Map<String ,String > map=new TreeMap<>();
        map.put(Configurations.AUTH_TOKEN, UserUtils.getUserInfo().getAuth_token());
        map.put(Configurations.PAGE, String.valueOf(page));

        entity.addParameter(Configurations.SIGN, SignUtils.createSignString(device_id,timeStamp,map));

        x.http().get(entity, new Callback.CommonCallback<JSONObject>() {
            @Override
            public void onSuccess(JSONObject result) {

                checkResOld(result);
                try {
                    if (result.getInt(Configurations.STATUSCODE) == 200) {
                        LogUtil.d("resultSuccess", result.getString("results"));
                        mAppItems = JSON.parseArray(result.getJSONObject("results").getString("app_items"), AppItems.class);
                        if (mAppItems.size() > 0) {
                            page++;
                            mHandler.sendEmptyMessage(NOTIFY_DATA);

                        } else {
                            page--;
                            ToastUtil.showShort(NewMyCoffeesActivity.this, "沒有更多了");
                        }

                    } else {
                        ToastUtil.showShort(NewMyCoffeesActivity.this, result.getString(Configurations.STATUSMSG));
                    }
                    swpMyCoffeesRefresh.setLoadMore(false);

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                swpMyCoffeesRefresh.setLoadMore(false);
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

}