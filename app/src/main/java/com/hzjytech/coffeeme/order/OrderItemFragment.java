package com.hzjytech.coffeeme.order;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.hzjytech.coffeeme.Dialogs.HintDialog;
import com.hzjytech.coffeeme.Dialogs.ITwoButtonClick;
import com.hzjytech.coffeeme.Dialogs.TitleButtonsDialog;
import com.hzjytech.coffeeme.R;
import com.hzjytech.coffeeme.configurations.Configurations;
import com.hzjytech.coffeeme.entities.NewGood;
import com.hzjytech.coffeeme.entities.NewOrder;
import com.hzjytech.coffeeme.entities.NewOrders;
import com.hzjytech.coffeeme.entities.User;
import com.hzjytech.coffeeme.fragments.BaseFragment;
import com.hzjytech.coffeeme.home.NewPaymentActivity;
import com.hzjytech.coffeeme.home.OrderPaymentActivity;
import com.hzjytech.coffeeme.http.JijiaHttpSubscriber;
import com.hzjytech.coffeeme.http.SubscriberOnCompletedListener;
import com.hzjytech.coffeeme.http.SubscriberOnErrorListener;
import com.hzjytech.coffeeme.http.SubscriberOnNextListener;
import com.hzjytech.coffeeme.http.api.OrderApi;
import com.hzjytech.coffeeme.utils.AppUtil;
import com.hzjytech.coffeeme.utils.CameraUtil;
import com.hzjytech.coffeeme.utils.DateTimeUtil;
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
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.HttpMethod;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.jpush.android.api.JPushInterface;
import cz.msebera.android.httpclient.Header;
import rx.Observable;

import static com.hzjytech.coffeeme.order.OrderFragment.ORDER_STATUS_ALL;

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
    public static final String IDENTIFIER = "identifier";
    private User user;
    private int orderStatus;

    private OrderItemAdapter partadapter;
    private OrderItemAdapter alladapter;
    private ArrayList<NewOrder> allorders = new ArrayList<NewOrder>();
    private ArrayList<NewOrder> partorders = new ArrayList<NewOrder>();
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
    private onIReresh listener;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (SharedPrefUtil.getLoginType()
                .equals(SharedPrefUtil.LOGINING)) {
            user = UserUtils.getUserInfo();
        } else {
            user = null;
        }
        partadapter = new OrderItemAdapter();
        partxrcyViewOrderitemfrg.setAdapter(partadapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getParentFragment()
                .getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        partxrcyViewOrderitemfrg.setLayoutManager(layoutManager);

        partxrcyViewOrderitemfrg.setRefreshProgressStyle(ProgressStyle.BallRotate);
        partxrcyViewOrderitemfrg.setLoadingMoreProgressStyle(ProgressStyle.BallRotate);
        partxrcyViewOrderitemfrg.setArrowImageView(R.drawable.ic_pulltorefresh_arrow);

        partxrcyViewOrderitemfrg.setLoadingMoreEnabled(true);
        partxrcyViewOrderitemfrg.setLoadingListener(new XRecyclerView.LoadingListener() {
            @Override
            public void onRefresh() {

                refreshData(false);
            }

            @Override
            public void onLoadMore() {

                loadMoreData();
            }
        });
        partxrcyViewOrderitemfrg.setEmptyView(partemptyview);

        alladapter = new OrderItemAdapter();
        allxrcyViewOrderitemfrg.setAdapter(alladapter);
        LinearLayoutManager alllayoutManager = new LinearLayoutManager(getParentFragment()
                .getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        allxrcyViewOrderitemfrg.setLayoutManager(alllayoutManager);

        allxrcyViewOrderitemfrg.setRefreshProgressStyle(ProgressStyle.BallRotate);
        allxrcyViewOrderitemfrg.setLoadingMoreProgressStyle(ProgressStyle.BallRotate);
        allxrcyViewOrderitemfrg.setLoadingMoreEnabled(true);
        allxrcyViewOrderitemfrg.setArrowImageView(R.drawable.ic_pulltorefresh_arrow);
        allxrcyViewOrderitemfrg.setLoadingListener(new XRecyclerView.LoadingListener() {
            @Override
            public void onRefresh() {

                refreshData(false);
            }

            @Override
            public void onLoadMore() {

                loadMoreData();
            }
        });
        allxrcyViewOrderitemfrg.setEmptyView(allemptyview);

    }


    public void refreshData(boolean isWithLoading) {
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
        if(isWithLoading){
            showLoading();
        }
        Observable<NewOrders> orderListObservable;
        if (orderStatus == OrderFragment.ORDER_STATUS_UNFINISHED) {

            partxrcyViewOrderitemfrg.setVisibility(View.VISIBLE);
            allxrcyViewOrderitemfrg.setVisibility(View.GONE);
            mpartPage = 1;
            partorders = new ArrayList<NewOrder>();
            partxrcyViewOrderitemfrg.reset();
            orderListObservable = OrderApi.getOrderList(getActivity(),
                    user.getAuth_token(),
                    "unfinished",
                    mpartPage);

        } else {

            partxrcyViewOrderitemfrg.setVisibility(View.GONE);
            allxrcyViewOrderitemfrg.setVisibility(View.VISIBLE);
            mPage = 1;
            allorders = new ArrayList<NewOrder>();
            allxrcyViewOrderitemfrg.reset();
            orderListObservable = OrderApi.getOrderList(getActivity(),
                    user.getAuth_token(),
                    null,
                    mPage);
        }
        JijiaHttpSubscriber subscriber = JijiaHttpSubscriber.buildSubscriber(getActivity())
                .setOnNextListener(new SubscriberOnNextListener<NewOrders>() {


                    @Override
                    public void onNext(NewOrders orders) {
                        if (orderStatus == OrderFragment.ORDER_STATUS_UNFINISHED) {
                            partorders.addAll(orders.getOrders());
                            partadapter.SetData(partorders);
                            partadapter.notifyDataSetChanged();

                            if (partorders.size() > 0)
                                partemptyview.setVisibility(View.GONE);
                            else
                                partemptyview.setVisibility(View.VISIBLE);
                        } else {
                            allorders.addAll(orders.getOrders());
                            alladapter.SetData(allorders);
                            alladapter.notifyDataSetChanged();

                            if (allorders.size() > 0)
                                allemptyview.setVisibility(View.GONE);
                            else
                                allemptyview.setVisibility(View.VISIBLE);
                        }
                    }
                })
                .setOnCompletedListener(new SubscriberOnCompletedListener() {
                    @Override
                    public void onCompleted() {
                        hideLoading();
                        if (orderStatus == OrderFragment.ORDER_STATUS_UNFINISHED) {

                            partxrcyViewOrderitemfrg.refreshComplete();
                        } else {

                            allxrcyViewOrderitemfrg.refreshComplete();
                        }
                    }
                })
                .setOnErrorListener(new SubscriberOnErrorListener() {
                    @Override
                    public void onError(Throwable e) {
                        hideLoading();
                        if (orderStatus == OrderFragment.ORDER_STATUS_UNFINISHED) {

                            partxrcyViewOrderitemfrg.refreshComplete();
                        } else {

                            allxrcyViewOrderitemfrg.refreshComplete();
                        }
                    }
                })
                .build();
        orderListObservable.subscribe(subscriber);

    }
    private void refreshAllData(){
        OrderFragment fragment= (OrderFragment) getParentFragment();
        fragment.refreshAll();

    }


    private void loadMoreData() {

        if (null == user)
            return;
        Observable<NewOrders> orderListObservable;
        if (orderStatus == OrderFragment.ORDER_STATUS_UNFINISHED) {
            mpartPage++;
            partxrcyViewOrderitemfrg.setVisibility(View.VISIBLE);
            allxrcyViewOrderitemfrg.setVisibility(View.GONE);
            orderListObservable = OrderApi.getOrderList(getActivity(),
                    user.getAuth_token(),
                    "unfinished",
                    mpartPage);

        } else {
            mPage++;
            partxrcyViewOrderitemfrg.setVisibility(View.GONE);
            allxrcyViewOrderitemfrg.setVisibility(View.VISIBLE);
            orderListObservable = OrderApi.getOrderList(getActivity(),
                    user.getAuth_token(),
                    null,
                    mPage);
        }
        JijiaHttpSubscriber subscriber = JijiaHttpSubscriber.buildSubscriber(getActivity())
                .setOnNextListener(new SubscriberOnNextListener<NewOrders>() {


                    @Override
                    public void onNext(NewOrders orders) {
                        if (orderStatus == OrderFragment.ORDER_STATUS_UNFINISHED) {
                            partorders.addAll(orders.getOrders());
                            partadapter.SetData(partorders);
                            partadapter.notifyDataSetChanged();
                            partxrcyViewOrderitemfrg.reset();
                            if (orders.getOrders()
                                    .size() > 0) {
                                partemptyview.setVisibility(View.GONE);
                            } else {

                                partxrcyViewOrderitemfrg.setNoMore(true);
                            }
                        } else {
                            allorders.addAll(orders.getOrders());
                            alladapter.SetData(allorders);
                            alladapter.notifyDataSetChanged();
                            allxrcyViewOrderitemfrg.reset();
                            if (orders.getOrders()
                                    .size() > 0) {
                                allemptyview.setVisibility(View.GONE);
                            } else {
                                allxrcyViewOrderitemfrg.setNoMore(true);
                            }
                        }
                    }
                })
                .setOnErrorListener(new SubscriberOnErrorListener() {
                    @Override
                    public void onError(Throwable e) {
                        if (orderStatus == OrderFragment.ORDER_STATUS_UNFINISHED) {
                            partxrcyViewOrderitemfrg.reset();
                        } else {
                            allxrcyViewOrderitemfrg.reset();
                        }
                    }
                })
                .build();
        orderListObservable.subscribe(subscriber);


    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    public void setListener(onIReresh listener) {
        this.listener = listener;
    }

    class OrderItemAdapter extends RecyclerView.Adapter {

        private static final int TYPE_PACKAGE = 0x01;
        private static final int TYPE_NORMAL = 0x02;
        DecimalFormat fnum = new DecimalFormat("##0.00");
        private ArrayList<NewOrder> orders = new ArrayList<NewOrder>();

        public void SetData(ArrayList<NewOrder> orders) {
            this.orders = orders;
        }

        class PackageViewHolder extends RecyclerView.ViewHolder {
            @BindView(R.id.imgbtnOrderPackageDel)
            ImageView mImgbtnOrderPackageDel;
            @BindView(R.id.iv_order_package)
            ImageView mIvOrderPackage;
            @BindView(R.id.tvOrderpackageDate)
            TextView mTvOrderpackageDate;
            @BindView(R.id.tvOrderPackageSum)
            TextView mTvOrderPackageSum;
            @BindView(R.id.tvOrderitemdetailStatus)
            TextView mTvOrderitemdetailStatus;
            @BindView(R.id.btnOrderPackage)
            Button mBtnOrderPackage;

            public PackageViewHolder(View view) {
                super(view);
                // R.layout.fragment_order_package_detail
                ButterKnife.bind(this, view);
                mBtnOrderPackage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (AppUtil.isFastClick())
                            return;
                        if (orderStatus == OrderFragment.ORDER_STATUS_UNFINISHED) {
                            partorderpos = getItemPosition(PackageViewHolder.this);
                        } else {
                            allorderpos = getItemPosition(PackageViewHolder.this);
                        }

                        Intent intent;
                        switch (orders.get(getItemPosition(PackageViewHolder.this))
                                .getStatus()) {

                            case 1:
                                goToDetailPackageOrder();
                                break;

                            case 0:
                                goToDetailPackageOrder();
                                break;
                        }
                    }
                });
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (AppUtil.isFastClick())
                            return;
                        goToDetailPackageOrder();


                    }

                });
                mImgbtnOrderPackageDel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (AppUtil.isFastClick())
                            return;
                        if (NetUtil.isNetworkAvailable(getParentFragment().getContext())) {

                            delOrder(PackageViewHolder.this,
                                    orders.get(getItemPosition(PackageViewHolder.this))
                                            .getIdentifier());
                        }
                    }
                });

            }

            private void goToDetailPackageOrder() {
                Intent intent = new Intent(getActivity(), DetailPackageOrderActivity.class);
                intent.putExtra(IDENTIFIER,
                        orders.get(getItemPosition(PackageViewHolder.this))
                                .getIdentifier());
                startActivity(intent);
            }

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
                tvOrderitemdetailFetchcode = (TextView) itemView.findViewById(R.id
                        .tvOrderitemdetailFetchcode);
                tvOrderitemDetailId = (TextView) itemView.findViewById(R.id.tvOrderitemDetailId);
                imgbtnOrderitemdetailDel = (ImageView) itemView.findViewById(R.id
                        .imgbtnOrderitemdetailDel);
                ogOrderitemdetailGoods = (OrderGroup) itemView.findViewById(R.id
                        .ogOrderitemdetailGoods);
                tvOrderitemdetailMore = (TextView) itemView.findViewById(R.id
                        .tvOrderitemdetailMore);
                tvOrderitemdetailDate = (TextView) itemView.findViewById(R.id
                        .tvOrderitemdetailDate);
                tvOrderitemdetailSum = (TextView) itemView.findViewById(R.id.tvOrderitemdetailSum);
                tvOrderitemdetailStatus = (TextView) itemView.findViewById(R.id
                        .tvOrderitemdetailStatus);
                btnOrderitemdetail = (Button) itemView.findViewById(R.id.btnOrderitemdetail);
                ogOrderitemdetailGoods.setItemClickable(false);
                btnOrderitemdetail.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (AppUtil.isFastClick())
                            return;
                        if (!buyEnable(orders.get(getItemPosition(ViewHolder.this))
                                .getGoods())) {
                            return;
                        }
                        if (orderStatus == OrderFragment.ORDER_STATUS_UNFINISHED) {
                            partorderpos = getItemPosition(ViewHolder.this);
                        } else {
                            allorderpos = getItemPosition(ViewHolder.this);
                        }

                        Intent intent;
                        switch (orders.get(getItemPosition(ViewHolder.this))
                                .getStatus()) {

                            case 1:
                            case 6:
                                canGet = true;
                                if (!CameraUtil.isCameraCanUse()) {
                                    //如果没有授权，则请求授权
                                    HintDialog hintDialog = HintDialog.newInstance("提示",
                                            "无法获取摄像头数据，请检查是否已经打开摄像头权限。",
                                            "确定");
                                    hintDialog.show(getFragmentManager(), "cameraHint");
                                } else {
                                    //有授权，直接开启摄像头
                                    intent = new Intent(getActivity(), CaptureActivity.class);
                                    getRootFragment().startActivityForResult(intent,
                                            OrderItemFragment.REQUEST_CODE_FETCH);
                                }

                                break;

                            case 0:
                            case 3:

                                getOrderDetail(orders.get(getItemPosition(ViewHolder.this))
                                        .getIdentifier());

                                break;
                            default:
                                buyMore(orders.get(getItemPosition(ViewHolder.this))
                                        .getId());
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
                        intent.putExtra("identifier",
                                orders.get(getItemPosition(ViewHolder.this))
                                        .getIdentifier());
                        startActivity(intent);
                    }
                });

                imgbtnOrderitemdetailDel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (AppUtil.isFastClick())
                            return;
                        if (NetUtil.isNetworkAvailable(getParentFragment().getContext())) {

                            delOrder(ViewHolder.this,
                                    orders.get(getItemPosition(ViewHolder.this))
                                            .getIdentifier());
                        }
                    }
                });
            }


            private void buyMore(final int orderid) {
                showLoading();
                RequestParams entity = new RequestParams(Configurations.URL_COPY);
                entity.addParameter(Configurations.TOKEN,
                        UserUtils.getUserInfo()
                                .getAuth_token());
                entity.addParameter(Configurations.ORDERID, orderid);

                String device_id = JPushInterface.getRegistrationID(getContext());
                String timeStamp = TimeUtil.getCurrentTimeString();
                entity.addParameter(Configurations.TIMESTAMP, timeStamp);
                entity.addParameter(Configurations.DEVICE_ID, device_id);

                Map<String, String> map = new TreeMap<String, String>();
                map.put(Configurations.TOKEN,
                        UserUtils.getUserInfo()
                                .getAuth_token());
                map.put(Configurations.ORDERID, String.valueOf(orderid));
                entity.addParameter(Configurations.SIGN,
                        SignUtils.createSignString(device_id, timeStamp, map));

                x.http()
                        .get(entity, new Callback.CommonCallback<JSONObject>() {

                            @Override
                            public void onSuccess(JSONObject result) {
                                hideLoading();
                                checkResOld(result);
                                try {
                                    if (result.getInt(Configurations.STATUSCODE) == 200) {
                                        LogUtil.e("order", result.toString());
                                        NewOrder mOrder = new Gson().fromJson(result.getString(
                                                "results"), NewOrder.class);
                                        LogUtil.e("mOrder", mOrder.toString());
                                        if (null != mOrder) {
                                            Intent intent = new Intent(getActivity(),
                                                    NewPaymentActivity.class);
                                            intent.putExtra("type", 2);
                                            intent.putExtra("order", mOrder);
                                            intent.putExtra("order_id", orderid);
                                            startActivity(intent);
                                        }

                                    } else {
                                        ToastUtil.showShort(getActivity(),
                                                result.getString(Configurations.STATUSMSG));
                                    }
                                } catch (JSONException e) {

                                    e.printStackTrace();
                                }
                            }

                            @Override
                            public void onError(Throwable ex, boolean isOnCallback) {
                                showNetError();
                                hideLoading();
                            }

                            @Override
                            public void onCancelled(CancelledException cex) {

                            }

                            @Override
                            public void onFinished() {
                                hideLoading();
                            }
                        });
            }
        }


        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if (viewType == TYPE_NORMAL) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.fragment_order_item_detail, parent, false);
                return new ViewHolder(view);
            } else {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.fragment_order_package_detail, parent, false);
                return new PackageViewHolder(view);
            }

        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder orginHolder, int position) {
            if (orginHolder instanceof OrderItemAdapter.ViewHolder) {
                ViewHolder holder = (ViewHolder) orginHolder;
                holder.tvOrderitemdetailFetchcode.setText(orders.get(position)
                        .getFetch_code());
                holder.ogOrderitemdetailGoods.clearData();
                holder.ogOrderitemdetailGoods.setData(orders.get(position)
                        .getGoods());
                holder.tvOrderitemDetailId.setText(orders.get(position)
                        .getIdentifier());
                List<NewGood> goods = orders.get(position)
                        .getGoods();
                holder.btnOrderitemdetail.setBackgroundResource(R.drawable
                        .bg_cir_rec_coffee_single);
                holder.tvOrderitemdetailDate.setText(DateTimeUtil.longToShort9(orders.get(position)
                        .getCreated_at()));

                holder.tvOrderitemdetailSum.setText(String.valueOf(fnum.format(orders.get(position)
                        .getSum())));

                switch (orders.get(position)
                        .getStatus()) {
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
                        int numUnToken = getNumUnToken(goods);
                        holder.tvOrderitemdetailStatus.setText(String.format(Locale.getDefault(),
                                getString(R.string.leave_goode_untake),
                                numUnToken));
                        holder.btnOrderitemdetail.setText("继续取单");
                        holder.imgbtnOrderitemdetailDel.setVisibility(View.INVISIBLE);
                        break;
                    default:
                        break;
                }
            } else {
                NewOrder packageOrder = orders.get(position);
                PackageViewHolder holder = (PackageViewHolder) orginHolder;
                DisplayImageOptions options = new DisplayImageOptions.Builder().cacheInMemory
                        (true)/*缓存至内存*/
                        .cacheOnDisk(true)/*缓存值SDcard*/
                        .bitmapConfig(Bitmap.Config.RGB_565)
                        .showImageForEmptyUri(R.drawable.package_empty)
                        .showImageOnLoading(R.drawable.package_empty)
                        .showImageOnFail(R.drawable.package_empty)
                        .build();
                String img_url = packageOrder.getPackage_info()
                        .getImg_url();
                String str = img_url.contains("?") ? img_url.substring(0,
                        img_url.indexOf("?")) : img_url;
                ImageLoader.getInstance()
                        .displayImage(str, holder.mIvOrderPackage, options);
                holder.mTvOrderpackageDate.setText(DateTimeUtil.longToShort9(orders.get(position)
                        .getCreated_at()));
                holder.mTvOrderPackageSum.setText(String.valueOf(fnum.format(orders.get(position)
                        .getSum())));
                switch (orders.get(position)
                        .getStatus()) {
                    case 0:
                        holder.mTvOrderitemdetailStatus.setText("待支付");
                        holder.mBtnOrderPackage.setText("查看订单");
                        holder.mImgbtnOrderPackageDel.setVisibility(View.VISIBLE);
                        break;
                    case 1:
                        holder.mTvOrderitemdetailStatus.setText("支付成功");
                        holder.mBtnOrderPackage.setText("查看订单");
                        holder.mImgbtnOrderPackageDel.setVisibility(View.INVISIBLE);
                        break;

                }
            }
        }

        /**
         * 获取未取走的咖啡数量
         *
         * @param goods
         */
        private int getNumUnToken(List<NewGood> goods) {
            if (goods == null) {
                return 0;
            }
            int num = 0;
            for (NewGood good : goods) {
                if (!good.isBe_token()) {
                    num++;
                }
            }
            return num;

        }

        @Override
        public int getItemViewType(int position) {
            if (orders.get(position)
                    .getPackage_info() != null) {
                return TYPE_PACKAGE;
            } else {
                return TYPE_NORMAL;
            }
        }

        @Override
        public int getItemCount() {

            return orders.size();
        }

        public int getItemPosition(RecyclerView.ViewHolder viewHolder) {

            if (orderStatus == OrderFragment.ORDER_STATUS_UNFINISHED) {
                return viewHolder.getLayoutPosition() - partxrcyViewOrderitemfrg.getHeaderCount();
            } else {
                return viewHolder.getLayoutPosition() - allxrcyViewOrderitemfrg.getHeaderCount();
            }

        }

        /**
         * delete order by id
         *
         * @param viewHolder
         * @param id
         */
        private void delOrder(
                final RecyclerView.ViewHolder viewHolder, final String id) {

            TitleButtonsDialog descCenterDialog = TitleButtonsDialog.newInstance("确定删除该订单？",
                    "取消",
                    "确定");
            descCenterDialog.setOnTwoClickListener(new ITwoButtonClick() {
                @Override
                public void onLeftButtonClick() {

                }

                @Override
                public void onRightButtonClick() {
                    showLoading();
                    String delUrl = Configurations.URL_ORDERS + "/" + id;
                    com.loopj.android.http.RequestParams params = new com.loopj.android.http
                            .RequestParams();
                    params.put(Configurations.AUTH_TOKEN, user.getAuth_token());

                    String device_id = JPushInterface.getRegistrationID(getContext());
                    String timeStamp = TimeUtil.getCurrentTimeString();
                    params.put(Configurations.TIMESTAMP, timeStamp);
                    params.put(Configurations.DEVICE_ID, device_id);

                    Map<String, String> map = new TreeMap<String, String>();
                    map.put(Configurations.AUTH_TOKEN,
                            UserUtils.getUserInfo()
                                    .getAuth_token());
                    params.put(Configurations.SIGN,
                            SignUtils.createSignString(device_id, timeStamp, map));


                    AsyncHttpClient client = new AsyncHttpClient();
                    client.delete(delUrl, params, new JsonHttpResponseHandler() {
                        @Override
                        public void onFailure(
                                int statusCode,
                                Header[] headers,
                                Throwable throwable,
                                JSONObject errorResponse) {
                            ToastUtil.showShort(getActivity(), "删除订单失败");
                            hideLoading();
                            super.onFailure(statusCode, headers, throwable, errorResponse);
                        }

                        @Override
                        public void onSuccess(
                                int statusCode, Header[] headers, JSONObject response) {
                            hideLoading();
                            checkResOld(response);
                            try {

                                if (response.getInt(Configurations.STATUSCODE) == 200) {
                                   /*if (orderStatus == OrderFragment.ORDER_STATUS_UNFINISHED) {

                                        partorders.remove(getItemPosition(viewHolder));
                                        partadapter.SetData(partorders);
                                        partadapter.notifyDataSetChanged();
                                    } else {

                                        allorders.remove(getItemPosition(viewHolder));
                                        alladapter.SetData(allorders);
                                        alladapter.notifyDataSetChanged();
                                    }*/

                             if(listener!=null){
                                       listener.refresh();
                             }

                                }
                                ToastUtil.showShort(getActivity(),
                                        response.getString(Configurations.STATUSMSG));
                            } catch (JSONException e) {

                                e.printStackTrace();
                            }
                        }
                    });

                }
            });

            descCenterDialog.show(getFragmentManager(), "askDialog");

        }
    }


    private void getOrderDetail(String identifier) {
        showLoading();
        Observable<NewOrder> observable = OrderApi.getOrderDetail(getActivity(),
                UserUtils.getUserInfo()
                        .getAuth_token(),
                identifier);
        JijiaHttpSubscriber subscriber = JijiaHttpSubscriber.buildSubscriber(getActivity())
                .setOnNextListener(new SubscriberOnNextListener<NewOrder>() {
                    @Override
                    public void onNext(NewOrder order) {
                        Intent intent = new Intent(getActivity(), OrderPaymentActivity.class);
                        intent.putExtra("type", 3);
                        intent.putExtra("order", order);
                        startActivity(intent);
                    }
                })
                .setOnCompletedListener(new SubscriberOnCompletedListener() {
                    @Override
                    public void onCompleted() {
                        hideLoading();
                    }
                })
                .build();
        observable.subscribe(subscriber);
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
        entity.addParameter(Configurations.AUTH_TOKEN,
                UserUtils.getUserInfo()
                        .getAuth_token());
        map.put(Configurations.AUTH_TOKEN,
                UserUtils.getUserInfo()
                        .getAuth_token());
        entity.addParameter(Configurations.VMID, id);
        map.put(Configurations.VMID, id);

        if (orderStatus == OrderFragment.ORDER_STATUS_UNFINISHED) {
            entity.addParameter(Configurations.ORDERID,
                    partorders.get(partorderpos)
                            .getIdentifier());
            map.put(Configurations.ORDERID,
                    partorders.get(partorderpos)
                            .getIdentifier());
        } else {
            entity.addParameter(Configurations.ORDERID,
                    allorders.get(allorderpos)
                            .getIdentifier());
            map.put(Configurations.ORDERID,
                    allorders.get(allorderpos)
                            .getIdentifier());
        }

        String device_id = JPushInterface.getRegistrationID(getContext());
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

                            ToastUtil.showShort(getActivity(),
                                    result.getString(Configurations.STATUSMSG));
                            if(listener!=null){
                                listener.refresh();
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
    public void onResume() {
        super.onResume();
        if (SharedPrefUtil.getLoginType()
                .equals(SharedPrefUtil.LOGINING)) {
            user = UserUtils.getUserInfo();
        } else {
            user = null;
        }
        refreshData(true);
    }


    public void setStatus(int orderStatus) {
        this.orderStatus = orderStatus;

    }

    public interface onIReresh{
        void refresh();
    }
}