package com.hzjytech.coffeeme.order;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.hzjytech.coffeeme.BaseActivity;
import com.hzjytech.coffeeme.Dialogs.HintDialog;
import com.hzjytech.coffeeme.Dialogs.ITwoButtonClick;
import com.hzjytech.coffeeme.Dialogs.TitleButtonsDialog;
import com.hzjytech.coffeeme.R;
import com.hzjytech.coffeeme.configurations.Configurations;
import com.hzjytech.coffeeme.entities.NewGood;
import com.hzjytech.coffeeme.entities.NewOrder;
import com.hzjytech.coffeeme.entities.NewOrders;
import com.hzjytech.coffeeme.entities.Order;
import com.hzjytech.coffeeme.entities.User;
import com.hzjytech.coffeeme.http.JijiaHttpSubscriber;
import com.hzjytech.coffeeme.http.SubscriberOnCompletedListener;
import com.hzjytech.coffeeme.http.SubscriberOnErrorListener;
import com.hzjytech.coffeeme.http.SubscriberOnNextListener;
import com.hzjytech.coffeeme.http.api.OrderApi;
import com.hzjytech.coffeeme.utils.AppUtil;
import com.hzjytech.coffeeme.utils.CommonUtil;
import com.hzjytech.coffeeme.utils.DateTimeUtil;
import com.hzjytech.coffeeme.utils.DateUtil;
import com.hzjytech.coffeeme.utils.SharedPrefUtil;
import com.hzjytech.coffeeme.utils.SignUtils;
import com.hzjytech.coffeeme.utils.TimeUtil;
import com.hzjytech.coffeeme.utils.ToastUtil;
import com.hzjytech.coffeeme.utils.UserUtils;
import com.hzjytech.coffeeme.widgets.TitleBar;
import com.hzjytech.coffeeme.widgets.orderview.OrderGroup;
import com.hzjytech.coffeeme.widgets.xrecyclerview.ProgressStyle;
import com.hzjytech.coffeeme.widgets.xrecyclerview.XRecyclerView;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.HttpMethod;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import cn.jpush.android.api.JPushInterface;
import rx.Observable;

@ContentView(R.layout.activity_able_take)
public class AbleTakeActivity extends BaseActivity {
    private static final int REQUEST_CODE_FETCH = 2044;
    @ViewInject(R.id.titleBar)
    private TitleBar titleBar;
    @ViewInject(R.id.xrv_able_take)
    private XRecyclerView xrv_able_take;
    @ViewInject(R.id.tv_emptyview)
    private TextView emptyView;
    private User user;
    private boolean canGet = false;
    private AbleTackOrderItemAdapter partadapter;

    private int mpartPage = 1;
    private ArrayList<NewOrder> orders = new ArrayList<>();
    private AbleTackOrderItemAdapter.ViewHolder viewHolder;
    private String vmid;
    private JijiaHttpSubscriber mSubscriber;
    private JijiaHttpSubscriber mSubscriber1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        showLoading();
        titleBar.setTitle("取单");
        titleBar.setTitleColor(Color.WHITE);
        titleBar.setLeftImageResource(R.drawable.icon_left);
        titleBar.setLeftTextColor(Color.WHITE);
        titleBar.setLeftText("扫一扫");
        titleBar.setLeftClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performClickHomeScan();
                finish();

            }
        });

    }


    private void initView() {
        if (SharedPrefUtil.getLoginType()
                .equals(SharedPrefUtil.LOGINING)) {
            user = UserUtils.getUserInfo();
        } else {
            user = null;
        }
        partadapter = new AbleTackOrderItemAdapter();
        xrv_able_take.setAdapter(partadapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        xrv_able_take.setLayoutManager(layoutManager);

        xrv_able_take.setRefreshProgressStyle(ProgressStyle.BallRotate);
        xrv_able_take.setLoadingMoreProgressStyle(ProgressStyle.BallRotate);
        xrv_able_take.setArrowImageView(R.drawable.ic_pulltorefresh_arrow);

        xrv_able_take.setLoadingMoreEnabled(true);
        xrv_able_take.setLoadingListener(new XRecyclerView.LoadingListener() {
            @Override
            public void onRefresh() {

                refreshData();
            }

            @Override
            public void onLoadMore() {

                loadMoreData();
            }
        });
        xrv_able_take.setEmptyView(emptyView);


    }

    //倒计时
    private CountDownTimer timer = new CountDownTimer(90000, 1000) {

        @Override
        public void onTick(long millisUntilFinished) {
        }

        @Override
        public void onFinish() {
            timer.cancel();
            reScan();
        }
    };

    //重新扫描二维码
    private void reScan() {
        TitleButtonsDialog descCenterDialog = TitleButtonsDialog.newInstance("二维码已过期，请重新扫一扫",
                "关闭",
                "扫一扫");
        descCenterDialog.setOnTwoClickListener(new ITwoButtonClick() {
            @Override
            public void onLeftButtonClick() {

            }

            @Override
            public void onRightButtonClick() {
                finish();
                performClickHomeScan();


            }

        });
        descCenterDialog.show(getSupportFragmentManager(), "asktag");
    }

    private void performClickHomeScan() {
        Intent intent = new Intent("android.intent.action.CART_BROADCAST");
        LocalBroadcastManager.getInstance(this)
                .sendBroadcast(intent);

    }

    @Override
    protected void onResume() {
        super.onResume();
        initView();
        refreshData();
        hideLoading();
        timer.start();

    }

    @Override
    protected void onPause() {
        super.onPause();
        timer.cancel();
    }

    private class AbleTackOrderItemAdapter extends RecyclerView.Adapter<AbleTackOrderItemAdapter
            .ViewHolder> {


        private ArrayList<NewOrder> orders = new ArrayList<NewOrder>();

        public void SetData(ArrayList<NewOrder> orders) {
            this.orders = orders;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View inflate = LayoutInflater.from(AbleTakeActivity.this)
                    .inflate(R.layout.fragment_order_item_detail, parent, false);
            viewHolder = new ViewHolder(inflate);
            return viewHolder;
        }


        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.tvOrderitemdetailFetchcode.setText(orders.get(position)
                    .getFetch_code());
            holder.ogOrderitemdetailGoods.clearData();
            holder.ogOrderitemdetailGoods.setData(orders.get(position)
                    .getGoods());
            holder.tvOrderitemDetailId.setText(orders.get(position)
                    .getIdentifier());

            Calendar calendar = DateTimeUtil.stringToCalendar(orders.get(position)
                    .getCreated_at());
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            int mon = calendar.get(Calendar.MONTH) + 1;//Calendar里取出来的month比实际的月份少1，所以要加上
            int year = calendar.get(Calendar.YEAR);

            String date = year + "年" + mon + "月" + day + "日";//+ "  " + hour + ":" + minute;
            holder.tvOrderitemdetailDate.setText(date);
            DecimalFormat fnum = new DecimalFormat("##0.00");
            holder.tvOrderitemdetailSum.setText(String.valueOf(fnum.format(orders.get(position)
                    .getSum())));
            holder.btnOrderitemdetail.setText("取单");

            holder.imgbtnOrderitemdetailDel.setVisibility(View.INVISIBLE);
            holder.btnOrderitemdetail.setBackgroundResource(R.drawable.bg_cir_rec_coffee_single);


        }

        @Override
        public int getItemCount() {
            return orders == null ? 0 : orders.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            private int orderpos;

            private TextView tvOrderitemdetailFetchcode;
            private TextView tvOrderitemDetailId;
            private ImageView imgbtnOrderitemdetailDel;
            private OrderGroup ogOrderitemdetailGoods;
            private TextView tvOrderitemdetailDate;
            private TextView tvOrderitemdetailSum;
            private TextView tvOrderitemdetailStatus;
            private Button btnOrderitemdetail;

            public ViewHolder(View itemView) {
                super(itemView);
                tvOrderitemdetailFetchcode = (TextView) itemView.findViewById(R.id
                        .tvOrderitemdetailFetchcode);
                tvOrderitemDetailId = (TextView) itemView.findViewById(R.id.tvOrderitemDetailId);
                imgbtnOrderitemdetailDel = (ImageView) itemView.findViewById(R.id
                        .imgbtnOrderitemdetailDel);
                ogOrderitemdetailGoods = (OrderGroup) itemView.findViewById(R.id
                        .ogOrderitemdetailGoods);
                tvOrderitemdetailDate = (TextView) itemView.findViewById(R.id
                        .tvOrderitemdetailDate);
                tvOrderitemdetailSum = (TextView) itemView.findViewById(R.id.tvOrderitemdetailSum);
                tvOrderitemdetailStatus = (TextView) itemView.findViewById(R.id
                        .tvOrderitemdetailStatus);
                btnOrderitemdetail = (Button) itemView.findViewById(R.id.btnOrderitemdetail);
                btnOrderitemdetail.setOnClickListener(new View.OnClickListener() {

                    private int orderpos;

                    @Override
                    public void onClick(View v) {

                        if (AppUtil.isFastClick())
                            return;
                        if (!buyEnable(orders.get(getItemPosition()).getGoods())){
                            return;
                        }
                        orderpos = getItemPosition();
                        canGet = true;
                        Intent intent = getIntent();
                        vmid = intent.getStringExtra("vmid");
                        getCoffeeMId(vmid, orderpos);


                    }
                });

            }

            public int getItemPosition() {
                return getLayoutPosition() - xrv_able_take.getHeaderCount();
            }

        }


    }

    private void getCoffeeMId(String id, int pos) {
        if (!canGet)
            return;

        canGet = false;
        RequestParams entity = new RequestParams(Configurations.URL_QRFETCH);
        Map<String, String> map = new TreeMap<String, String>();
        entity.addParameter(Configurations.AUTH_TOKEN,
                UserUtils.getUserInfo()
                        .getAuth_token());
        map.put(Configurations.AUTH_TOKEN,
                UserUtils.getUserInfo()
                        .getAuth_token());
        entity.addParameter(Configurations.VMID, id);
        map.put(Configurations.VMID, id);


        entity.addParameter(Configurations.ORDERID,
                orders.get(pos)
                        .getIdentifier());
        map.put(Configurations.ORDERID,
                orders.get(pos)
                        .getIdentifier());
        String device_id = JPushInterface.getRegistrationID(this);
        String timeStamp = TimeUtil.getCurrentTimeString();
        entity.addParameter(Configurations.TIMESTAMP, timeStamp);
        entity.addParameter(Configurations.DEVICE_ID, device_id);
        entity.addParameter(Configurations.SIGN,
                SignUtils.createSignString(device_id, timeStamp, map));


        x.http()
                .request(HttpMethod.PUT, entity, new Callback.CommonCallback<JSONObject>() {
                    @Override
                    public void onSuccess(JSONObject result) {

                        checkResOld(result);
                        try {
                            if (result.getInt(Configurations.STATUSCODE) == 200) {
                                HintDialog hintDialog = HintDialog.newInstance("提示",
                                        result.getString(Configurations.STATUSMSG),
                                        "确定");
                                hintDialog.show(getSupportFragmentManager(), "hintDialog");
                                hintDialog.setForseCloseActivityListener(new HintDialog
                                        .ForseCloseActivity() {
                                    @Override
                                    public void close() {
                                        finish();
                                    }
                                });

                            } else {
                                if (result.getInt(Configurations.STATUSCODE) == 408) {
                                    reScan();
                                }
                                ToastUtil.showShort(AbleTakeActivity.this,
                                        result.getString(Configurations.STATUSMSG));

                                refreshData();
                                partadapter.notifyDataSetChanged();
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

    public void refreshData() {

        if (null == user) {
            emptyView.setText("你还没有登录哦～");
            xrv_able_take.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
        } else {
            emptyView.setText("你还没有可取的订单哦～");
            emptyView.setVisibility(View.GONE);
        }

        if (null == user)
            return;
        mpartPage = 1;
        xrv_able_take.setVisibility(View.VISIBLE);
        xrv_able_take.reset();
        Observable<NewOrders> orderListObservable = OrderApi.getOrderList(this,user.getAuth_token(),
                "able_take",
                mpartPage);
        mSubscriber = JijiaHttpSubscriber.buildSubscriber(this)
                .setOnNextListener(new SubscriberOnNextListener<NewOrders>() {


                    @Override
                    public void onNext(NewOrders resultOrders) {
                        orders.addAll(resultOrders.getOrders());
                        partadapter.SetData(orders);
                        partadapter.notifyDataSetChanged();

                        if (orders.size() > 0)
                            emptyView.setVisibility(View.GONE);
                        else
                            emptyView.setVisibility(View.VISIBLE);
                    }
                })
                .setOnCompletedListener(new SubscriberOnCompletedListener() {
                    @Override
                    public void onCompleted() {
                        xrv_able_take.refreshComplete();
                    }
                }).setOnErrorListener(new SubscriberOnErrorListener() {
                    @Override
                    public void onError(Throwable e) {
                        xrv_able_take.refreshComplete();
                    }
                })
                .build();
        orderListObservable.subscribe(mSubscriber);
     /*   RequestParams entity = new RequestParams(Configurations.URL_ORDERS);
        Log.e("token", user.getAuth_token());
        entity.addParameter(Configurations.AUTH_TOKEN, user.getAuth_token());

        String device_id = JPushInterface.getRegistrationID(this);
        String timeStamp = TimeUtil.getCurrentTimeString();
        entity.addParameter(Configurations.TIMESTAMP, timeStamp);
        entity.addParameter(Configurations.DEVICE_ID, device_id);

        Map<String, String> map = new TreeMap<String, String>();
        map.put(Configurations.AUTH_TOKEN,
                UserUtils.getUserInfo()
                        .getAuth_token());
        xrv_able_take.setVisibility(View.VISIBLE);
        entity.addParameter("status", "able_take");
        map.put("status", "able_take");
        mpartPage = 1;
        orders = new ArrayList<Order>();
        entity.addParameter(Configurations.PAGE, mpartPage);
        map.put(Configurations.PAGE, String.valueOf(mpartPage));

        xrv_able_take.reset();

        entity.addParameter(Configurations.SIGN,
                SignUtils.createSignString(device_id, timeStamp, map));

        x.http()
                .get(entity, new Callback.CommonCallback<String>() {
                    @Override
                    public void onSuccess(String result) {
                        orders.addAll(parseOrdersResult(result));
                        partadapter.SetData(orders);
                        xrv_able_take.refreshComplete();
                        partadapter.notifyDataSetChanged();

                        if (orders.size() > 0)
                            emptyView.setVisibility(View.GONE);
                        else
                            emptyView.setVisibility(View.VISIBLE);

                    }

                    @Override
                    public void onError(Throwable ex, boolean isOnCallback) {
                        showNetError();

                        xrv_able_take.refreshComplete();

                    }

                    @Override
                    public void onCancelled(CancelledException cex) {
                    }

                    @Override
                    public void onFinished() {
                    }
                });*/

    }
    // TODO: 2017/3/13 由于原接口为分页获取订单，无法获取订单总数

    private void loadMoreData() {

        if (null == user)
            return;
        mpartPage++;
        xrv_able_take.setVisibility(View.VISIBLE);
        Observable<NewOrders> orderListObservable = OrderApi.getOrderList(this,user.getAuth_token(),
                "able_take",
                mpartPage);
        mSubscriber1 = JijiaHttpSubscriber.buildSubscriber(this)
                .setOnNextListener(new SubscriberOnNextListener<NewOrders>() {


                    @Override
                    public void onNext(NewOrders resultOrders) {
                        orders.addAll(resultOrders.getOrders());
                        partadapter.SetData(orders);
                        partadapter.notifyDataSetChanged();


                        if (resultOrders.getOrders()
                                .size() > 0) {
                            emptyView.setVisibility(View.GONE);
                            xrv_able_take.refreshComplete();
                            xrv_able_take.reset();
                        } else {

                            xrv_able_take.setNoMore(true);

                        }
                    }
                })
                .setOnCompletedListener(new SubscriberOnCompletedListener() {
                    @Override
                    public void onCompleted() {
                        xrv_able_take.refreshComplete();
                        xrv_able_take.reset();

                    }
                }).setOnErrorListener(new SubscriberOnErrorListener() {
                    @Override
                    public void onError(Throwable e) {
                        xrv_able_take.refreshComplete();
                        xrv_able_take.reset();
                    }
                })
                .build();
        orderListObservable.subscribe(mSubscriber1);
        /* RequestParams entity = new RequestParams(Configurations.URL_ORDERS);
        entity.addParameter(Configurations.AUTH_TOKEN, user.getAuth_token());

        String device_id = JPushInterface.getRegistrationID(this);
        String timeStamp = TimeUtil.getCurrentTimeString();
        entity.addParameter(Configurations.TIMESTAMP, timeStamp);
        entity.addParameter(Configurations.DEVICE_ID, device_id);

        Map<String, String> map = new TreeMap<String, String>();
        map.put(Configurations.AUTH_TOKEN,
                UserUtils.getUserInfo()
                        .getAuth_token());


        mpartPage++;
        entity.addParameter(Configurations.PAGE, mpartPage);
        map.put(Configurations.PAGE, String.valueOf(mpartPage));
        xrv_able_take.setVisibility(View.VISIBLE);


        entity.addParameter(Configurations.SIGN,
                SignUtils.createSignString(device_id, timeStamp, map));

        x.http()
                .get(entity, new Callback.CommonCallback<String>() {
                    @Override
                    public void onSuccess(String result) {
                        orders.addAll(parseOrdersResult(result));
                        partadapter.SetData(orders);
                        partadapter.notifyDataSetChanged();

                        if (parseOrdersResult(result).size() > 0) {
                            emptyView.setVisibility(View.GONE);
                            xrv_able_take.refreshComplete();
                            xrv_able_take.reset();
                        } else {

                            xrv_able_take.setNoMore(true);

                        }
                    }

                    @Override
                    public void onError(Throwable ex, boolean isOnCallback) {
                        showNetError();


                        xrv_able_take.refreshComplete();
                        xrv_able_take.reset();

                    }

                    @Override
                    public void onCancelled(CancelledException cex) {

                    }

                    @Override
                    public void onFinished() {

                    }
                });*/

    }

    private List<Order> parseOrdersResult(String result) {
        List<Order> lst = new ArrayList<>();
        try {
            JSONObject object = new JSONObject(result);

            checkResOld(object);
            if (object.getInt(Configurations.STATUSCODE) == 200) {
                lst = JSON.parseArray(object.getJSONObject("results")
                        .getString("orders"), Order.class);

            } else {
                ToastUtil.showShort(this, object.getString(Configurations.STATUSMSG));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return lst;
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        CommonUtil.unSubscribeSubs(mSubscriber,mSubscriber1);
    }
}

