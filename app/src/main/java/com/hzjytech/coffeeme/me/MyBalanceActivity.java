package com.hzjytech.coffeeme.me;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hzjytech.coffeeme.BaseActivity;
import com.hzjytech.coffeeme.R;
import com.hzjytech.coffeeme.configurations.Configurations;
import com.hzjytech.coffeeme.configurations.UmengConfig;
import com.hzjytech.coffeeme.entities.BalanceRecord;
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

/**
 * Created by dblr4287 on 2016/5/9.
 */
@ContentView(R.layout.activity_my_balance)
public class MyBalanceActivity extends BaseActivity {
    @ViewInject(R.id.titleBar)
    private TitleBar tbTitle;
    @ViewInject(R.id.mybalance_balance)
    private TextView tvbalance;
    @ViewInject(R.id.mybalance_recharge)
    private Button btnrecharge;
    @ViewInject(R.id.mybalance_reclist)
    private RecyclerView reclist;
    @ViewInject(R.id.superSwipeBalanceRefresh)
    private SuperSwipeRefreshLayout superSwipeRefreshLayout;
    private MyBalanceListAdapter adapter;

    private List<BalanceRecord> records = new ArrayList<BalanceRecord>();
    private ProgressBar pbFooter;
    private ImageView ivFooter;
    private TextView tvFooter;
    private ProgressBar pbCartHeader;
    private ImageView ivCartHeader;
    private TextView tvCartHeader;
    private int page;
    Handler handler = new Handler();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        InitView();
    }
    @Override
    protected void onResume() {
        super.onResume();
        showLoading();
        initData();
        getRecords();
        MobclickAgent.onPageStart(UmengConfig.MYBALANCEACTIVITY);
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(UmengConfig.MYBALANCEACTIVITY);
        MobclickAgent.onPause(this);
    }

    private void InitView() {
        tbTitle.setLeftImageResource(R.drawable.icon_me_back);
        tbTitle.setLeftClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        tbTitle.setTitle(getResources().getString(R.string.title_mybalance));
        tbTitle.setTitleColor(Color.WHITE);
        btnrecharge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(MyBalanceActivity.this, NewRechargeActivity.class);
                startActivity(intent);
            }
        });
        superSwipeRefreshLayout.setFooterView(createFooterView());
        superSwipeRefreshLayout.setTargetScrollWithLayout(true);
        superSwipeRefreshLayout.setCanPullToRefresh(false);
        superSwipeRefreshLayout.setOnPullRefreshListener(null);
        superSwipeRefreshLayout.setOnPushLoadMoreListener(new SuperSwipeRefreshLayout.OnPushLoadMoreListener() {
            @Override
            public void onLoadMore() {
                tvFooter.setText(getString(R.string.str_loading));
                ivFooter.setVisibility(View.INVISIBLE);
                pbFooter.setVisibility(View.VISIBLE);
                ivFooter.setVisibility(View.VISIBLE);
                pbFooter.setVisibility(View.INVISIBLE);
                loadMoreBalance();
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

    private View createFooterView() {
        View footerView = LayoutInflater.from(superSwipeRefreshLayout.getContext())
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
    private View createHeaderView(){
        View headerView = LayoutInflater.from(superSwipeRefreshLayout.getContext())
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


    private void initData() {

        RequestParams params = new RequestParams(Configurations.URL_CHECK_TOKEN);
        params.addParameter(Configurations.AUTH_TOKEN, UserUtils.getUserInfo().getAuth_token());

        String timeStamp= TimeUtil.getCurrentTimeString();
        String device_id= JPushInterface.getRegistrationID(MyBalanceActivity.this);
        params.addParameter(Configurations.TIMESTAMP,timeStamp);
        params.addParameter(Configurations.DEVICE_ID,device_id );

        Map<String,String> map=new TreeMap<>();
        map.put(Configurations.AUTH_TOKEN, UserUtils.getUserInfo().getAuth_token());
        params.addParameter(Configurations.SIGN, SignUtils.createSignString(device_id,timeStamp,map));

        x.http().get(params, new Callback.CommonCallback<JSONObject>() {
            @Override
            public void onSuccess(JSONObject result) {


                try {
                    if (result.getInt(Configurations.STATUSCODE) == 200) {
                        tvbalance.setText("￥" + result.getJSONObject("results").getJSONObject("user").getString("balance"));
                    } else {
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


    private void getRecords() {
        final long startTime = System.currentTimeMillis();
        page =1;
        RequestParams entity = new RequestParams(Configurations.URL_PREPAY);//+"/"+"2016051610430073102432");某个流水记录
        String auth_token = UserUtils.getUserInfo().getAuth_token();
        entity.addParameter(Configurations.AUTH_TOKEN, auth_token);
        entity.addParameter(Configurations.PAGE,page);
        String device_id = JPushInterface.getRegistrationID(MyBalanceActivity.this);
        String timeStamp= TimeUtil.getCurrentTimeString();
        entity.addParameter(Configurations.TIMESTAMP, timeStamp);
        entity.addParameter(Configurations.DEVICE_ID, device_id);

        Map<String, String> map = new TreeMap<String, String>();
        map.put(Configurations.AUTH_TOKEN, auth_token);
        map.put(Configurations.PAGE,String.valueOf(page));
        entity.addParameter(Configurations.SIGN, SignUtils.createSignString(device_id, timeStamp, map));


        x.http().get(entity, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                hideLoading();
                parseResult(result);
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
    private void loadMoreBalance() {
        page+=1;
        RequestParams entity = new RequestParams(Configurations.URL_PREPAY);//+"/"+"2016051610430073102432");某个流水记录
        String auth_token = UserUtils.getUserInfo().getAuth_token();
        entity.addParameter(Configurations.AUTH_TOKEN, auth_token);
        entity.addParameter(Configurations.PAGE,page);
        String device_id = JPushInterface.getRegistrationID(MyBalanceActivity.this);
        String timeStamp= TimeUtil.getCurrentTimeString();
        entity.addParameter(Configurations.TIMESTAMP, timeStamp);
        entity.addParameter(Configurations.DEVICE_ID, device_id);

        Map<String, String> map = new TreeMap<String, String>();
        map.put(Configurations.AUTH_TOKEN, auth_token);
        map.put(Configurations.PAGE,String.valueOf(page));
        entity.addParameter(Configurations.SIGN, SignUtils.createSignString(device_id, timeStamp, map));


        x.http().get(entity, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {

                try {
                    JSONObject object = new JSONObject(result);
                    if(object.getInt(Configurations.STATUSCODE)==200){
                        superSwipeRefreshLayout.setLoadMore(false);
                        ArrayList<BalanceRecord> balanceRecords = new Gson().fromJson(object.getJSONObject("results").getString("balance_records"), new TypeToken<ArrayList<BalanceRecord>>() {
                        }.getType());
                        if(balanceRecords.size()>0){
                            records.addAll(balanceRecords);
                            adapter.notifyDataSetChanged();
                        }else{
                            page--;
                            ToastUtil.showShort(MyBalanceActivity.this, "沒有更多了");
                        }

                    }else{
                        superSwipeRefreshLayout.setLoadMore(false);
                        ToastUtil.showShort(MyBalanceActivity.this,object.getString(Configurations.STATUSMSG));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();

                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                superSwipeRefreshLayout.setLoadMore(false);
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

    private void parseResult(String result) {

        try {
            JSONObject object = new JSONObject(result);

            checkResOld(object);
            if (object.getInt(Configurations.STATUSCODE) == 200) {
                records = new Gson().fromJson(object.getJSONObject("results").getString("balance_records"), new TypeToken<ArrayList<BalanceRecord>>() {
                }.getType());

                adapter = new MyBalanceListAdapter(MyBalanceActivity.this, records);

                LinearLayoutManager layoutManager = new LinearLayoutManager(this);
                layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                reclist.setLayoutManager(layoutManager);
//                reclist.addItemDecoration(new RecycleViewDivider(MyBalanceActivity.this, LinearLayoutManager.HORIZONTAL));
                reclist.setAdapter(adapter);
            } else {
                ToastUtil.showShort(MyBalanceActivity.this, object.getString(Configurations.STATUSMSG));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    class MyBalanceListAdapter extends RecyclerView.Adapter<MyBalanceListAdapter.MyBalanceListHolder> {


        private LayoutInflater mLayoutInflater;
        private Context context;
        private List<BalanceRecord> records;


        public MyBalanceListAdapter(Context context, List<BalanceRecord> records) {
            this.records = records;
            this.context = context;
            mLayoutInflater = LayoutInflater.from(context);
        }


        @Override
        public MyBalanceListHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(MyBalanceActivity.this).inflate(R.layout.mybrecord_item, parent, false);
            return new MyBalanceListHolder(view);
        }

        @Override
        public void onBindViewHolder(MyBalanceListHolder holder, int pos) {

            if (holder instanceof MyBalanceListHolder) {

                if (records.get(pos).getBr_type() == 1) {

                    ((MyBalanceListHolder) holder).nametx.setText("Coffee Me账户充值");
                    ((MyBalanceListHolder) holder).changetx.setText("+" + records.get(pos).getSum());
                    ((MyBalanceListHolder) holder).changetx.setTextColor(Color.GREEN);
                } else if (records.get(pos).getBr_type() == 2) {

                    ((MyBalanceListHolder) holder).nametx.setText("Coffee Me账户消费");
                    ((MyBalanceListHolder) holder).changetx.setText("-" + records.get(pos).getSum());
                    ((MyBalanceListHolder) holder).changetx.setTextColor(Color.RED);
                } else if (records.get(pos).getBr_type() == 3) {

                    ((MyBalanceListHolder) holder).nametx.setText("Coffee Me账户退款");
                    ((MyBalanceListHolder) holder).changetx.setText("+" + records.get(pos).getSum());
                    ((MyBalanceListHolder) holder).changetx.setTextColor(Color.GREEN);
                }

                ((MyBalanceListHolder) holder).datetx.setText(records.get(pos).getUpdated_at().substring(0, records.get(pos).getUpdated_at().lastIndexOf("T")));

            }
        }

        @Override
        public int getItemCount() {
            return records == null ? 0 : records.size();
        }


        class MyBalanceListHolder extends RecyclerView.ViewHolder {

            TextView nametx;
            TextView datetx;
            TextView changetx;

            public MyBalanceListHolder(View itemView) {
                super(itemView);

                nametx = (TextView) itemView.findViewById(R.id.mybrecord_name);
                datetx = (TextView) itemView.findViewById(R.id.mybrecord_date);
                changetx = (TextView) itemView.findViewById(R.id.mybrecord_change);

            }
        }
    }
}