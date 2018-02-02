package com.hzjytech.coffeeme.me;

import android.graphics.Color;
import android.os.Handler;
import android.os.Bundle;
import android.os.Message;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.hzjytech.coffeeme.BaseActivity;
import com.hzjytech.coffeeme.R;
import com.hzjytech.coffeeme.adapters.PointRecordsAdapter;
import com.hzjytech.coffeeme.configurations.Configurations;
import com.hzjytech.coffeeme.configurations.UmengConfig;
import com.hzjytech.coffeeme.entities.PointRecord;
import com.hzjytech.coffeeme.utils.LogUtil;
import com.hzjytech.coffeeme.utils.SignUtils;
import com.hzjytech.coffeeme.utils.TimeUtil;
import com.hzjytech.coffeeme.utils.ToastUtil;
import com.hzjytech.coffeeme.utils.UserUtils;
import com.hzjytech.coffeeme.widgets.RecycleViewDivider;
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

@ContentView(R.layout.activity_point_records)
public class PointRecordsActivity extends BaseActivity {

    private static final int REFRESH_DATA = 0x0000001;
    private static final int MORE_DATA = 0x0000002;
    private int page = 0;
    @ViewInject(R.id.titleBar)
    private TitleBar titleBar;

    @ViewInject(R.id.rcyViewRecordsShow)
    private RecyclerView rcyViewRecordsShow;

    @ViewInject(R.id.swpViewRecordsRefresh)
    private SuperSwipeRefreshLayout swpViewRecordsRefresh;

    @ViewInject(R.id.emptyview)
    private TextView emptyview;

    private PointRecordsAdapter pointRecordsAdapter;

    private List<PointRecord> pointRecords = new ArrayList<>();
    private ProgressBar pbCartHeader;
    private ImageView ivCartHeader;
    private TextView tvCartHeader;
    private ProgressBar pbFooter;
    private ImageView ivFooter;
    private TextView tvFooter;

    private Handler mPointRecordHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case REFRESH_DATA:
                    pointRecordsAdapter.refreshData(pointRecords);
                    break;
                case MORE_DATA:
                    pointRecordsAdapter.loadMoreData(pointRecords);
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        initTitle();

    }


    private void initShow() {
        showLoading();
        rcyViewRecordsShow.setLayoutManager(new LinearLayoutManager(PointRecordsActivity.this));

        pointRecordsAdapter = new PointRecordsAdapter(PointRecordsActivity.this, pointRecords);

        rcyViewRecordsShow.setAdapter(pointRecordsAdapter);

        swpViewRecordsRefresh.setHeaderViewBackgroundColor(getResources().getColor(R.color.light_grey));
        swpViewRecordsRefresh.setHeaderView(createHeaderView());
        swpViewRecordsRefresh.setTargetScrollWithLayout(true);
        swpViewRecordsRefresh.setOnPullRefreshListener(new SuperSwipeRefreshLayout.OnPullRefreshListener() {
            @Override
            public void onRefresh() {
                tvCartHeader.setText("正在刷新");
                ivCartHeader.setVisibility(View.GONE);
                pbCartHeader.setVisibility(View.VISIBLE);

                addRefreshData();

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
        swpViewRecordsRefresh.setFooterView(createFooterView());
        swpViewRecordsRefresh.setOnPushLoadMoreListener(new SuperSwipeRefreshLayout.OnPushLoadMoreListener() {
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

    private void loadMoreData() {
        pointRecords.clear();
        page++;
        RequestParams entity = new RequestParams(Configurations.URL_POINT_RECORDS);
        Map<String, String> map=new TreeMap<String, String>();
        if (UserUtils.getUserInfo() != null) {
            entity.addParameter(Configurations.AUTH_TOKEN, UserUtils.getUserInfo().getAuth_token());
            map.put(Configurations.AUTH_TOKEN, UserUtils.getUserInfo().getAuth_token());
        }
        entity.addParameter(Configurations.PAGE, page);
        map.put(Configurations.PAGE, String.valueOf(page));

        String device_id= JPushInterface.getRegistrationID(PointRecordsActivity.this);
        String timeStamp= TimeUtil.getCurrentTimeString();
        entity.addParameter(Configurations.TIMESTAMP, timeStamp);
        entity.addParameter(Configurations.DEVICE_ID,device_id );



        entity.addParameter(Configurations.SIGN, com.hzjytech.coffeeme.utils.SignUtils.createSignString(device_id,timeStamp,map));

        x.http().get(entity, new Callback.CommonCallback<JSONObject>() {
            @Override
            public void onSuccess(JSONObject result) {
                checkResOld(result);
                swpViewRecordsRefresh.setLoadMore(false);
                try {

                    if (result.getInt(Configurations.STATUSCODE) == 200) {

                        pointRecords = JSON.parseArray(result.getJSONObject("results").getString("point_records"), PointRecord.class);

                        if (pointRecords.isEmpty()) {
                            ToastUtil.showShort(PointRecordsActivity.this, getString(R.string.nomore_loading));
                        }
                        mPointRecordHandler.sendEmptyMessage(MORE_DATA);
                    } else {
                        ToastUtil.showShort(PointRecordsActivity.this, result.getString(Configurations.STATUSMSG));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
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
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(UmengConfig.POINTRECORDSACTIVITY);
        MobclickAgent.onResume(this);

        addRefreshData();
        initShow();
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(UmengConfig.POINTRECORDSACTIVITY);
        MobclickAgent.onPause(this);
    }


    private void addRefreshData() {
        pointRecords.clear();
        page = 1;
        RequestParams entity = new RequestParams(Configurations.URL_POINT_RECORDS);
        Map<String, String> map = new TreeMap<String, String>();
        if (UserUtils.getUserInfo() != null) {
            entity.addParameter(Configurations.AUTH_TOKEN, UserUtils.getUserInfo().getAuth_token());
            map.put(Configurations.AUTH_TOKEN, UserUtils.getUserInfo().getAuth_token());
        }
        entity.addParameter(Configurations.PAGE, page);
        map.put(Configurations.PAGE, String.valueOf(page));

        String device_id = JPushInterface.getRegistrationID(PointRecordsActivity.this);
        String timeStamp= TimeUtil.getCurrentTimeString();
        entity.addParameter(Configurations.TIMESTAMP, timeStamp);
        entity.addParameter(Configurations.DEVICE_ID, device_id);

        entity.addParameter(Configurations.SIGN, SignUtils.createSignString(device_id, timeStamp, map));

        x.http().get(entity, new Callback.CommonCallback<JSONObject>() {
            @Override
            public void onSuccess(JSONObject result) {
                hideLoading();
                checkResOld(result);
                swpViewRecordsRefresh.setRefreshing(false);
                try {

                    if (result.getInt(Configurations.STATUSCODE) == 200) {
                        LogUtil.d("result", result.getJSONObject("results").getString("point_records"));
                        pointRecords = JSON.parseArray(result.getJSONObject("results").getString("point_records"), PointRecord.class);

                        if (pointRecords.isEmpty()) {
                            setEmpty();
                            emptyview.setVisibility(View.VISIBLE);
                        } else {
                            setEmpty();
                            rcyViewRecordsShow.setVisibility(View.VISIBLE);
                        }
                        mPointRecordHandler.sendEmptyMessage(REFRESH_DATA);
                    } else {
                        ToastUtil.showShort(PointRecordsActivity.this, result.getString(Configurations.STATUSMSG));
                        goLogin();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
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

    private void setEmpty() {
        rcyViewRecordsShow.setVisibility(View.GONE);
        emptyview.setVisibility(View.GONE);
    }


    private View createHeaderView() {
        View headerView = LayoutInflater.from(swpViewRecordsRefresh.getContext())
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
        View footerView = LayoutInflater.from(swpViewRecordsRefresh.getContext())
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

    private void initTitle() {
        titleBar.setTitle(getString(R.string.string_pointrecord));
        titleBar.setTitleColor(Color.WHITE);
        titleBar.setLeftImageResource(R.drawable.icon_left);
        titleBar.setLeftClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PointRecordsActivity.this.finish();
            }
        });
    }
}
