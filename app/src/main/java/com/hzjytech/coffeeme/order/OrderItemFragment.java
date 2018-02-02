package com.hzjytech.coffeeme.order;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.hzjytech.coffeeme.Dialogs.HintDialog;
import com.hzjytech.coffeeme.Dialogs.ITwoButtonClick;
import com.hzjytech.coffeeme.Dialogs.TitleButtonsDialog;
import com.hzjytech.coffeeme.R;
import com.hzjytech.coffeeme.configurations.Configurations;
import com.hzjytech.coffeeme.entities.Order;
import com.hzjytech.coffeeme.entities.User;
import com.hzjytech.coffeeme.fragments.BaseFragment;
import com.hzjytech.coffeeme.home.NewPaymentActivity;
import com.hzjytech.coffeeme.home.OrderPaymentActivity;
import com.hzjytech.coffeeme.utils.AppUtil;
import com.hzjytech.coffeeme.utils.CameraUtil;
import com.hzjytech.coffeeme.utils.DateUtil;
import com.hzjytech.coffeeme.utils.LogUtil;
import com.hzjytech.coffeeme.utils.NetUtil;
import com.hzjytech.coffeeme.utils.SharedPrefUtil;
import com.hzjytech.coffeeme.utils.SignUtils;
import com.hzjytech.coffeeme.utils.TimeUtil;
import com.hzjytech.coffeeme.utils.ToastUtil;
import com.hzjytech.coffeeme.utils.UserUtils;
import com.hzjytech.coffeeme.widgets.orderview.OrderGroup;
import com.hzjytech.coffeeme.widgets.xrecyclerview.ProgressStyle;
import com.hzjytech.coffeeme.widgets.xrecyclerview.XRecyclerView;
import com.hzjytech.scan.activity.CaptureActivity;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

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
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import cn.jpush.android.api.JPushInterface;
import cz.msebera.android.httpclient.Header;

/**
 * A simple {@link Fragment} subclass.
 */

@ContentView(R.layout.fragment_order_item)
public class OrderItemFragment extends BaseFragment {

    private static final String USER = "user";
    private static final String ORDER_STATUS = "order_status";
    private static final int CASE_FETCH = 0x2001;
    private static final int REQUEST_CODE_FETCH = 2046;
    private static final int MY_PERMISSIONS_REQUEST_CALL_CAMERA = 236;
    private User user;
    private int orderStatus;

    private OrderItemAdapter partadapter;
    private OrderItemAdapter alladapter;
    private ArrayList<Order> allorders = new ArrayList<Order>();
    private ArrayList<Order> partorders = new ArrayList<Order>();
    private int mpartPage = 1;
    private int mPage = 1;
    @ViewInject(R.id.partemptyview)
    private TextView partemptyview;
    @ViewInject(R.id.partxrcyViewOrderitemfrg)
    private XRecyclerView partxrcyViewOrderitemfrg;

    @ViewInject(R.id.allemptyview)
    private TextView allemptyview;
    @ViewInject(R.id.allxrcyViewOrderitemfrg)
    private XRecyclerView allxrcyViewOrderitemfrg;

    private int partorderpos = 0;
    private int allorderpos = 0;

    private boolean canGet = false;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (SharedPrefUtil.getLoginType().equals(SharedPrefUtil.LOGINING)) {
            user = UserUtils.getUserInfo();
        } else {
            user = null;
        }
        partadapter = new OrderItemAdapter();
        partxrcyViewOrderitemfrg.setAdapter(partadapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getParentFragment().getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        partxrcyViewOrderitemfrg.setLayoutManager(layoutManager);

        partxrcyViewOrderitemfrg.setRefreshProgressStyle(ProgressStyle.BallRotate);
        partxrcyViewOrderitemfrg.setLoadingMoreProgressStyle(ProgressStyle.BallRotate);
        partxrcyViewOrderitemfrg.setArrowImageView(R.drawable.ic_pulltorefresh_arrow);

        partxrcyViewOrderitemfrg.setLoadingMoreEnabled(true);
        partxrcyViewOrderitemfrg.setLoadingListener(new XRecyclerView.LoadingListener() {
            @Override
            public void onRefresh() {

                refreshData();
            }

            @Override
            public void onLoadMore() {

                loadMoreData();
            }
        });
        partxrcyViewOrderitemfrg.setEmptyView(partemptyview);

        alladapter = new OrderItemAdapter();
        allxrcyViewOrderitemfrg.setAdapter(alladapter);
        LinearLayoutManager alllayoutManager = new LinearLayoutManager(getParentFragment().getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        allxrcyViewOrderitemfrg.setLayoutManager(alllayoutManager);

        allxrcyViewOrderitemfrg.setRefreshProgressStyle(ProgressStyle.BallRotate);
        allxrcyViewOrderitemfrg.setLoadingMoreProgressStyle(ProgressStyle.BallRotate);
        allxrcyViewOrderitemfrg.setLoadingMoreEnabled(true);
        allxrcyViewOrderitemfrg.setArrowImageView(R.drawable.ic_pulltorefresh_arrow);
        allxrcyViewOrderitemfrg.setLoadingListener(new XRecyclerView.LoadingListener() {
            @Override
            public void onRefresh() {

                refreshData();
            }

            @Override
            public void onLoadMore() {

                loadMoreData();
            }
        });
        allxrcyViewOrderitemfrg.setEmptyView(allemptyview);

    }

    public void refreshData() {

        if (null == user) {
            partemptyview.setText("你还没有登录哦～");
            allemptyview.setText("你还没有登录哦～");
            partxrcyViewOrderitemfrg.setVisibility(View.GONE);
            allxrcyViewOrderitemfrg.setVisibility(View.GONE);
            partemptyview.setVisibility(View.VISIBLE);
            allemptyview.setVisibility(View.VISIBLE);
        } else {
            partemptyview.setText("你还没有下单哦～");
            allemptyview.setText("你还没有下单哦～");
            partemptyview.setVisibility(View.GONE);
            allemptyview.setVisibility(View.GONE);
        }

        if (null == user)
            return;
        RequestParams entity = new RequestParams(Configurations.URL_ORDERS);
        entity.addParameter(Configurations.AUTH_TOKEN, user.getAuth_token());

        String device_id = JPushInterface.getRegistrationID(getContext());
        String timeStamp = TimeUtil.getCurrentTimeString();
        entity.addParameter(Configurations.TIMESTAMP, timeStamp);
        entity.addParameter(Configurations.DEVICE_ID, device_id);

        Map<String, String> map = new TreeMap<String, String>();
        map.put(Configurations.AUTH_TOKEN, UserUtils.getUserInfo().getAuth_token());


        if (orderStatus == OrderFragment.ORDER_STATUS_UNFINISHED) {

            partxrcyViewOrderitemfrg.setVisibility(View.VISIBLE);
            allxrcyViewOrderitemfrg.setVisibility(View.GONE);
            entity.addParameter("status", "unfinished");
            map.put("status", "unfinished");
            mpartPage = 1;
            partorders = new ArrayList<Order>();
            entity.addParameter(Configurations.PAGE, mpartPage);
            map.put(Configurations.PAGE, String.valueOf(mpartPage));

            partxrcyViewOrderitemfrg.reset();

        } else {

            partxrcyViewOrderitemfrg.setVisibility(View.GONE);
            allxrcyViewOrderitemfrg.setVisibility(View.VISIBLE);
            mPage = 1;
            allorders = new ArrayList<Order>();
            entity.addParameter(Configurations.PAGE, mPage);
            map.put(Configurations.PAGE, String.valueOf(mPage));

            allxrcyViewOrderitemfrg.reset();
        }

        entity.addParameter(Configurations.SIGN, SignUtils.createSignString(device_id, timeStamp, map));

        x.http().get(entity, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {

                if (orderStatus == OrderFragment.ORDER_STATUS_UNFINISHED) {

                    partorders.addAll(parseOrdersResult(result));
                    partadapter.SetData(partorders);
                    partxrcyViewOrderitemfrg.refreshComplete();
                    partadapter.notifyDataSetChanged();

                    if (partorders.size() > 0)
                        partemptyview.setVisibility(View.GONE);
                    else
                        partemptyview.setVisibility(View.VISIBLE);
                } else {
                    allorders.addAll(parseOrdersResult(result));
                    alladapter.SetData(allorders);
                    allxrcyViewOrderitemfrg.refreshComplete();
                    alladapter.notifyDataSetChanged();

                    if (allorders.size() > 0)
                        allemptyview.setVisibility(View.GONE);
                    else
                        allemptyview.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                showNetError();
                if (orderStatus == OrderFragment.ORDER_STATUS_UNFINISHED) {

                    partxrcyViewOrderitemfrg.refreshComplete();
                } else {

                    allxrcyViewOrderitemfrg.refreshComplete();
                }
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

        if (null == user)
            return;

        RequestParams entity = new RequestParams(Configurations.URL_ORDERS);
        entity.addParameter(Configurations.AUTH_TOKEN, user.getAuth_token());

        String device_id = JPushInterface.getRegistrationID(getContext());
        String timeStamp = TimeUtil.getCurrentTimeString();
        entity.addParameter(Configurations.TIMESTAMP, timeStamp);
        entity.addParameter(Configurations.DEVICE_ID, device_id);

        Map<String, String> map = new TreeMap<String, String>();
        map.put(Configurations.AUTH_TOKEN, UserUtils.getUserInfo().getAuth_token());


        if (orderStatus == OrderFragment.ORDER_STATUS_UNFINISHED) {
            entity.addParameter("status", "unfinished");
            map.put("status", "unfinished");
            mpartPage++;
            entity.addParameter(Configurations.PAGE, mpartPage);
            map.put(Configurations.PAGE, String.valueOf(mpartPage));
            partxrcyViewOrderitemfrg.setVisibility(View.VISIBLE);
            allxrcyViewOrderitemfrg.setVisibility(View.GONE);

        } else {
            mPage++;
            entity.addParameter(Configurations.PAGE, mPage);
            map.put(Configurations.PAGE, String.valueOf(mPage));
            partxrcyViewOrderitemfrg.setVisibility(View.GONE);
            allxrcyViewOrderitemfrg.setVisibility(View.VISIBLE);
        }

        entity.addParameter(Configurations.SIGN, SignUtils.createSignString(device_id, timeStamp, map));

        x.http().get(entity, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {

                if (orderStatus == OrderFragment.ORDER_STATUS_UNFINISHED) {

                    partorders.addAll(parseOrdersResult(result));
                    partadapter.SetData(partorders);
                    partadapter.notifyDataSetChanged();

                    if (parseOrdersResult(result).size() > 0) {
                        partemptyview.setVisibility(View.GONE);
                        partxrcyViewOrderitemfrg.refreshComplete();
                        partxrcyViewOrderitemfrg.reset();
                    } else {

                        partxrcyViewOrderitemfrg.setNoMore(true);
                    }
                } else {
                    allorders.addAll(parseOrdersResult(result));
                    alladapter.SetData(allorders);
                    alladapter.notifyDataSetChanged();

                    if (parseOrdersResult(result).size() > 0) {
                        allemptyview.setVisibility(View.GONE);
                        allxrcyViewOrderitemfrg.refreshComplete();
                        allxrcyViewOrderitemfrg.reset();
                    } else {
                        allxrcyViewOrderitemfrg.setNoMore(true);
                    }
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                showNetError();
                if (orderStatus == OrderFragment.ORDER_STATUS_UNFINISHED) {

                    partxrcyViewOrderitemfrg.refreshComplete();
                    partxrcyViewOrderitemfrg.reset();
                } else {

                    allxrcyViewOrderitemfrg.refreshComplete();
                    allxrcyViewOrderitemfrg.reset();
                }
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });

    }

    private List<Order> parseOrdersResult(String result) {
        List<Order> lst = new ArrayList<>();
        try {
            JSONObject object = new JSONObject(result);

            checkResOld(object);
            if (object.getInt(Configurations.STATUSCODE) == 200) {
                lst = JSON.parseArray(object.getJSONObject("results").getString("orders"), Order.class);

            } else {
                ToastUtil.showShort(getParentFragment().getContext(), object.getString(Configurations.STATUSMSG));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return lst;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    class OrderItemAdapter extends RecyclerView.Adapter<OrderItemAdapter.ViewHolder> {

        private ArrayList<Order> orders = new ArrayList<Order>();

        public void SetData(ArrayList<Order> orders) {
            this.orders = orders;
        }

        class ViewHolder extends RecyclerView.ViewHolder {

            private final TextView tvOrderitemdetailFetchcode;
            private final OrderGroup ogOrderitemdetailGoods;
            private final TextView tvOrderitemdetailMore;
            private final ImageView imgbtnOrderitemdetailDel;
            private final TextView tvOrderitemdetailDate;
            private final TextView tvOrderitemdetailSum;
            private final TextView tvOrderitemdetailStatus;
            private final Button btnOrderitemdetail;
            private final TextView tvOrderitemDetailId;

            public ViewHolder(View itemView) {
                super(itemView);

                tvOrderitemdetailFetchcode = (TextView) itemView.findViewById(R.id.tvOrderitemdetailFetchcode);
                tvOrderitemDetailId = (TextView) itemView.findViewById(R.id.tvOrderitemDetailId);
                imgbtnOrderitemdetailDel = (ImageView) itemView.findViewById(R.id.imgbtnOrderitemdetailDel);
                ogOrderitemdetailGoods = (OrderGroup) itemView.findViewById(R.id.ogOrderitemdetailGoods);
                tvOrderitemdetailMore = (TextView) itemView.findViewById(R.id.tvOrderitemdetailMore);
                tvOrderitemdetailDate = (TextView) itemView.findViewById(R.id.tvOrderitemdetailDate);
                tvOrderitemdetailSum = (TextView) itemView.findViewById(R.id.tvOrderitemdetailSum);
                tvOrderitemdetailStatus = (TextView) itemView.findViewById(R.id.tvOrderitemdetailStatus);
                btnOrderitemdetail = (Button) itemView.findViewById(R.id.btnOrderitemdetail);

                btnOrderitemdetail.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (AppUtil.isFastClick())
                            return;

                        if (orderStatus == OrderFragment.ORDER_STATUS_UNFINISHED) {
                            partorderpos = getItemPosition();
                        } else {
                            allorderpos = getItemPosition();
                        }

                        Intent intent;
                        switch (orders.get(getItemPosition()).getStatus()) {

                            case 1:
                            case 6:
                                canGet = true;
                                if (!CameraUtil.isCameraCanUse()) {
                                    //如果没有授权，则请求授权
                                    HintDialog hintDialog = HintDialog.newInstance("提示", "无法获取摄像头数据，请检查是否已经打开摄像头权限。", "确定");
                                    hintDialog.show(getFragmentManager(),"cameraHint");
                                } else {
                                    //有授权，直接开启摄像头
                                    intent = new Intent(getActivity(), CaptureActivity.class);
                                    getRootFragment().startActivityForResult(intent, OrderItemFragment.REQUEST_CODE_FETCH);
                                }

                                break;

                            case 0:
                            case 3:

                                getOrderDetail(orders.get(getItemPosition()).getIdentifier());

                                break;
                            default:
                                buyMore(orders.get(getItemPosition()).getId());
                                break;
                        }
                    }
                });

                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (AppUtil.isFastClick())
                            return;

                        Intent intent = new Intent(getActivity(), OrderItemDetailActivity.class);
                        intent.putExtra("identifier", orders.get(getItemPosition()).getIdentifier());
                        startActivity(intent);
                    }
                });

                imgbtnOrderitemdetailDel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (AppUtil.isFastClick())
                            return;

                        if (NetUtil.isNetworkAvailable(getParentFragment().getContext())) {

                            delOrder(orders.get(getItemPosition()).getIdentifier());
                        }
                    }
                });
            }

            public int getItemPosition() {

                if (orderStatus == OrderFragment.ORDER_STATUS_UNFINISHED) {
                    return getLayoutPosition() - partxrcyViewOrderitemfrg.getHeaderCount();
                } else {
                    return getLayoutPosition() - allxrcyViewOrderitemfrg.getHeaderCount();
                }

            }

            /**
             * delete order by id
             *
             * @param id
             */
            private void delOrder(final String id) {

                TitleButtonsDialog descCenterDialog = TitleButtonsDialog.newInstance("确定删除该订单？", "取消", "确定");
                descCenterDialog.setOnTwoClickListener(new ITwoButtonClick() {
                    @Override
                    public void onLeftButtonClick() {

                    }

                    @Override
                    public void onRightButtonClick() {
                        String delUrl = Configurations.URL_ORDERS + "/" + id;
                        com.loopj.android.http.RequestParams params = new com.loopj.android.http.RequestParams();
                        params.put(Configurations.AUTH_TOKEN, user.getAuth_token());

                        String device_id = JPushInterface.getRegistrationID(getContext());
                        String timeStamp = TimeUtil.getCurrentTimeString();
                        params.put(Configurations.TIMESTAMP, timeStamp);
                        params.put(Configurations.DEVICE_ID, device_id);

                        Map<String, String> map = new TreeMap<String, String>();
                        map.put(Configurations.AUTH_TOKEN, UserUtils.getUserInfo().getAuth_token());
                        params.put(Configurations.SIGN, SignUtils.createSignString(device_id, timeStamp, map));


                        AsyncHttpClient client = new AsyncHttpClient();
                        client.delete(delUrl, params, new JsonHttpResponseHandler() {
                            @Override
                            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

                                checkResOld(response);
                                try {

                                    if (response.getInt(Configurations.STATUSCODE) == 200) {
                                        if (orderStatus == OrderFragment.ORDER_STATUS_UNFINISHED) {

                                            partorders.remove(getItemPosition());
                                            partadapter.SetData(partorders);
                                            partadapter.notifyDataSetChanged();
                                        } else {

                                            allorders.remove(getItemPosition());
                                            alladapter.SetData(allorders);
                                            alladapter.notifyDataSetChanged();
                                        }

                                    }
                                    ToastUtil.showShort(getActivity(), response.getString(Configurations.STATUSMSG));
                                } catch (JSONException e) {

                                    e.printStackTrace();
                                }
                            }
                        });
                    }
                });

                descCenterDialog.show(getFragmentManager(), "askDialog");

            }


            private void buyMore(final int orderid) {

                RequestParams entity = new RequestParams(Configurations.URL_COPY);
                entity.addParameter(Configurations.AUTH_TOKEN, UserUtils.getUserInfo().getAuth_token());
                entity.addParameter(Configurations.ORDERID, orderid);

                String device_id = JPushInterface.getRegistrationID(getContext());
                String timeStamp = TimeUtil.getCurrentTimeString();
                entity.addParameter(Configurations.TIMESTAMP, timeStamp);
                entity.addParameter(Configurations.DEVICE_ID, device_id);

                Map<String, String> map = new TreeMap<String, String>();
                map.put(Configurations.AUTH_TOKEN, UserUtils.getUserInfo().getAuth_token());
                map.put(Configurations.ORDERID, String.valueOf(orderid));
                entity.addParameter(Configurations.SIGN, SignUtils.createSignString(device_id, timeStamp, map));

                x.http().get(entity, new Callback.CommonCallback<JSONObject>() {

                    @Override
                    public void onSuccess(JSONObject result) {

                        checkResOld(result);
                        try {
                            if (result.getInt(Configurations.STATUSCODE) == 200) {
                                LogUtil.e("order",result.toString());
                                Order mOrder = JSON.parseObject(result.getJSONObject("results").getString("order"), Order.class);
                                LogUtil.e("mOrder",mOrder.toString());
                                if (null != mOrder) {
                                    Intent intent = new Intent(getActivity(), NewPaymentActivity.class);
                                    intent.putExtra("type", 2);
                                    intent.putExtra("order", mOrder);
                                    intent.putExtra("order_id", orderid);
                                    startActivity(intent);
                                }

                            } else {
                                ToastUtil.showShort(getActivity(), result.getString(Configurations.STATUSMSG));
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
        }


        @Override
        public OrderItemAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_order_item_detail, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(OrderItemAdapter.ViewHolder holder, int position) {

            holder.tvOrderitemdetailFetchcode.setText(orders.get(position).getFetch_code());

            holder.ogOrderitemdetailGoods.clearData();
            holder.ogOrderitemdetailGoods.setData(orders.get(position).getGoods());
            holder.tvOrderitemDetailId.setText(orders.get(position).getIdentifier());

            if (orders.get(position).getGoods().size() < 2) {
                holder.tvOrderitemdetailMore.setVisibility(View.GONE);
                holder.btnOrderitemdetail.setBackgroundResource(R.drawable.bg_cir_rec_coffee_single);
            } else {
                holder.tvOrderitemdetailMore.setVisibility(View.VISIBLE);
                holder.tvOrderitemdetailMore.setText("等" + orders.get(position).getGoods().size() + "件饮品");

            }

            try {
                Calendar calendar = DateUtil.ISO8601toCalendar(orders.get(position).getCreated_at());
                int day = DateUtil.getDay(calendar);
                int mon = DateUtil.getMonth(calendar);//Calendar里取出来的month比实际的月份少1，所以要加上
                int year = calendar.get(Calendar.YEAR);
                int hour=DateUtil.getHour(calendar);
                String minute=DateUtil.getMinute(calendar);
                String date = mon + "月" + day + "日"+ "  " + hour + ":" + minute;
                holder.tvOrderitemdetailDate.setText(date);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            DecimalFormat fnum = new DecimalFormat("##0.00");
            holder.tvOrderitemdetailSum.setText(String.valueOf(fnum.format(orders.get(position).getSum())));

            switch (orders.get(position).getStatus()) {
                case 0:
                    holder.tvOrderitemdetailStatus.setText("待支付");
                    holder.btnOrderitemdetail.setText("去支付");

                    holder.imgbtnOrderitemdetailDel.setVisibility(View.VISIBLE);
                    break;
                case 1:
                    holder.tvOrderitemdetailStatus.setText("待取");
                    holder.btnOrderitemdetail.setText("扫一扫取单");

                    holder.imgbtnOrderitemdetailDel.setVisibility(View.INVISIBLE);
                    break;
                case 2:
                    holder.tvOrderitemdetailStatus.setText("退款成功");
                    holder.btnOrderitemdetail.setText("再来一单");

                    holder.imgbtnOrderitemdetailDel.setVisibility(View.VISIBLE);
                    break;
                case 3:
                    holder.tvOrderitemdetailStatus.setText("待支付");
                    holder.btnOrderitemdetail.setText("去支付");

                    holder.imgbtnOrderitemdetailDel.setVisibility(View.VISIBLE);
                    break;
                case 4:
                    holder.tvOrderitemdetailStatus.setText("部分退款成功");
                    holder.btnOrderitemdetail.setText("再来一单");

                    holder.imgbtnOrderitemdetailDel.setVisibility(View.VISIBLE);
                    break;
                case 5:
                    holder.tvOrderitemdetailStatus.setText("已完成");
                    holder.btnOrderitemdetail.setText("再来一单");

                    holder.imgbtnOrderitemdetailDel.setVisibility(View.VISIBLE);
                    break;
                case 6:
                    holder.tvOrderitemdetailStatus.setText("部分已取");
                    holder.btnOrderitemdetail.setText("扫一扫取单");

                    holder.imgbtnOrderitemdetailDel.setVisibility(View.INVISIBLE);
                    break;
                default:
                    break;
            }
        }

        @Override
        public int getItemCount() {

            return orders.size();
        }
    }


    private void getOrderDetail(String identifier) {
        RequestParams entity = new RequestParams(Configurations.URL_ORDERS + "/" + identifier);
        entity.addParameter(Configurations.AUTH_TOKEN, UserUtils.getUserInfo().getAuth_token());

        String device_id = JPushInterface.getRegistrationID(getContext());
        String timeStamp = TimeUtil.getCurrentTimeString();
        entity.addParameter(Configurations.TIMESTAMP, timeStamp);
        entity.addParameter(Configurations.DEVICE_ID, device_id);

        Map<String, String> map = new TreeMap<String, String>();
        map.put(Configurations.AUTH_TOKEN, UserUtils.getUserInfo().getAuth_token());
        entity.addParameter(Configurations.SIGN, SignUtils.createSignString(device_id, timeStamp, map));

        x.http().get(entity, new Callback.CommonCallback<JSONObject>() {

            @Override
            public void onSuccess(JSONObject result) {

                checkResOld(result);
                try {
                    if (result.getInt(Configurations.STATUSCODE) == 200) {

                        Order mOrder = JSON.parseObject(result.getJSONObject("results").getString("order"), Order.class);

                        if (null != mOrder) {
                            Intent intent = new Intent(getActivity(), OrderPaymentActivity.class);
                            intent.putExtra("type", 3);
                            intent.putExtra("order", mOrder);
                            startActivity(intent);
                        }

                    } else {
                        ToastUtil.showShort(getActivity(), result.getString(Configurations.STATUSMSG));

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

    private Fragment getRootFragment() {

        Fragment fragment = getParentFragment();
        while (fragment.getParentFragment() != null) {
            fragment = fragment.getParentFragment();
        }
        return fragment;
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE_FETCH) {

            if (null == data)
                return;
            String result = data.getStringExtra(CaptureActivity.SCAN_RESULT_KEY);

            if (null != result) {

                getCoffeeMId(result);

            } else {
                ToastUtil.showShort(getActivity(), "扫描失败，待会再试一试");

            }
        } else if (resultCode == Activity.RESULT_CANCELED) {
            ToastUtil.showShort(getActivity(), getResources().getString(R.string.cancel));
        }
    }


    private void getCoffeeMId(String id) {

        if (!canGet)
            return;

        canGet = false;
        RequestParams entity = new RequestParams(Configurations.URL_QRFETCH);
        Map<String, String> map = new TreeMap<String, String>();
        entity.addParameter(Configurations.AUTH_TOKEN, UserUtils.getUserInfo().getAuth_token());
        map.put(Configurations.AUTH_TOKEN, UserUtils.getUserInfo().getAuth_token());
        entity.addParameter(Configurations.VMID, id);
        map.put(Configurations.VMID, id);

        if (orderStatus == OrderFragment.ORDER_STATUS_UNFINISHED) {
            entity.addParameter(Configurations.ORDERID, partorders.get(partorderpos).getIdentifier());
            map.put(Configurations.ORDERID, partorders.get(partorderpos).getIdentifier());
        } else {
            entity.addParameter(Configurations.ORDERID, allorders.get(allorderpos).getIdentifier());
            map.put(Configurations.ORDERID, allorders.get(allorderpos).getIdentifier());
        }

        String device_id = JPushInterface.getRegistrationID(getContext());
        String timeStamp = TimeUtil.getCurrentTimeString();
        entity.addParameter(Configurations.TIMESTAMP, timeStamp);
        entity.addParameter(Configurations.DEVICE_ID, device_id);


        entity.addParameter(Configurations.SIGN, SignUtils.createSignString(device_id, timeStamp, map));


        x.http().request(HttpMethod.PUT, entity, new Callback.CommonCallback<JSONObject>() {
            @Override
            public void onSuccess(JSONObject result) {

                checkResOld(result);
                try {

                    ToastUtil.showShort(getActivity(), result.getString(Configurations.STATUSMSG));
                    refreshData();

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
    public void onResume() {
        super.onResume();
        if (SharedPrefUtil.getLoginType().equals(SharedPrefUtil.LOGINING)) {
            user = UserUtils.getUserInfo();
        } else {
            user = null;
        }
        refreshData();
    }


    public void setStatus(int orderStatus) {
        this.orderStatus = orderStatus;

    }
}