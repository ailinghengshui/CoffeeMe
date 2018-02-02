package com.hzjytech.coffeeme.home;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.hzjytech.coffeeme.BaseActivity;
import com.hzjytech.coffeeme.Dialogs.HintDialog;
import com.hzjytech.coffeeme.R;
import com.hzjytech.coffeeme.configurations.Configurations;
import com.hzjytech.coffeeme.entities.NewOrder;
import com.hzjytech.coffeeme.entities.NewOrders;
import com.hzjytech.coffeeme.entities.User;
import com.hzjytech.coffeeme.http.JijiaHttpSubscriber;
import com.hzjytech.coffeeme.http.SubscriberOnCompletedListener;
import com.hzjytech.coffeeme.http.SubscriberOnNextListener;
import com.hzjytech.coffeeme.http.api.OrderApi;
import com.hzjytech.coffeeme.me.PointRateActivity;
import com.hzjytech.coffeeme.utils.AppUtil;
import com.hzjytech.coffeeme.utils.CameraUtil;
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
import com.hzjytech.scan.activity.CaptureActivity;

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

@ContentView(R.layout.activity_success_order)
public class SuccessOrderActivity extends BaseActivity {
    private static final int MY_PERMISSIONS_REQUEST_CALL_CAMERA = 333;
    @ViewInject(R.id.tv_point_count)
    private TextView tv_point_count;
    @ViewInject(R.id.tv_go_point)
    private TextView tv_go_point;
    @ViewInject(R.id.titleBar)
    private TitleBar titleBar;
    @ViewInject(R.id.tvOrderitemdetailFetchcode)
    private TextView tv_fetch_code;
    @ViewInject(R.id.ogOrderitemdetailGoods)
    private OrderGroup orderGroup;
    @ViewInject(R.id.tvOrderitemdetailSum)
    private TextView tv_sum_price;
    @ViewInject(R.id.tvOrderitemdetailMore)
    private TextView tv_more;
    @ViewInject(R.id.tvOrderitemdetailDate)
    private TextView tv_date;
    @ViewInject(R.id.btnOrderitemdetail)
    private Button bt_scan;
    private User user;
    private boolean canGet;
    private int REQUEST_CODE_FETCH = 2222;
    private NewOrder mOrder;
    private JijiaHttpSubscriber mSubscriber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        refreshData();
        initScanButton();
    }

    private void initScanButton() {
        bt_scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (AppUtil.isFastClick())
                    return;

                canGet = true;
                //检查权限
                if (!CameraUtil.isCameraCanUse()) {
                    //如果没有授权，则请求授权
                    HintDialog hintDialog = HintDialog.newInstance("提示",
                            "无法获取摄像头数据，请检查是否已经打开摄像头权限。",
                            "确定");
                    hintDialog.show(getSupportFragmentManager(), "cameraHint");
                } else {
                    //有授权，直接开启摄像头
                    Intent intent = new Intent(SuccessOrderActivity.this, CaptureActivity.class);
                    startActivityForResult(intent, SuccessOrderActivity.this.REQUEST_CODE_FETCH);
                }


            }
        });
    }

    private void initView() {
        initTitle();
        initText();

    }

    private void initText() {
        Intent intent = getIntent();
        int point = intent.getIntExtra("point", 0);
        tv_point_count.setText(point + "");
        tv_go_point.getPaint()
                .setFlags(Paint.UNDERLINE_TEXT_FLAG);
        tv_go_point.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SuccessOrderActivity.this, PointRateActivity.class);
                startActivity(intent);
            }
        });
    }

    private void initTitle() {
        titleBar.setTitle(R.string.string_success_order);
        titleBar.setTitleColor(Color.WHITE);
        titleBar.setLeftClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        titleBar.setLeftImageResource(R.drawable.icon_left);
    }

    public void refreshData() {
        if (SharedPrefUtil.getLoginType()
                .equals(SharedPrefUtil.LOGINING)) {
            user = UserUtils.getUserInfo();
        } else {
            user = null;
        }

        if (null == user)
            return;
        showLoading();
        Observable<NewOrders> orderListObservable;
        orderListObservable = OrderApi.getOrderList(this,user.getAuth_token(), "able_take", 1);
        mSubscriber = JijiaHttpSubscriber.buildSubscriber(this)
                .setOnNextListener(new SubscriberOnNextListener<NewOrders>() {


                    @Override
                    public void onNext(NewOrders resultOrder) {
                        mOrder = resultOrder.getOrders()
                                .get(0);
                        setData(mOrder);
                    }
                })
                .setOnCompletedListener(new SubscriberOnCompletedListener() {
                    @Override
                    public void onCompleted() {
                        hideLoading();
                    }
                })
                .build();
        orderListObservable.subscribe(mSubscriber);

    }

    private void setData(NewOrder order) {
        tv_fetch_code.setText(order.getFetch_code());
        orderGroup.setItemClickable(false);
        orderGroup.setData(order.getGoods());
        tv_more.setVisibility(View.GONE);
        bt_scan.setBackgroundResource(R.drawable.bg_cir_rec_coffee_single);
        tv_date.setText(DateTimeUtil.longToShort9(order.getCreated_at()));
        DecimalFormat fnum = new DecimalFormat("##0.00");
        tv_sum_price.setText(String.valueOf(fnum.format(order.getSum())));
    }

    private List<NewOrder> parseOrdersResult(String result) {
        List<NewOrder> lst = new ArrayList<>();
        try {
            JSONObject object = new JSONObject(result);
            checkResOld(object);
            if (object.getInt(Configurations.STATUSCODE) == 200) {
                lst = JSON.parseArray(object.getJSONObject("results")
                        .getString("orders"), NewOrder.class);

            } else {
                ToastUtil.showShort(this, object.getString(Configurations.STATUSMSG));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return lst;
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
                ToastUtil.showShort(SuccessOrderActivity.this, "扫描失败，待会再试一试");

            }
        } else if (resultCode == Activity.RESULT_CANCELED) {
            ToastUtil.showShort(SuccessOrderActivity.this,
                    getResources().getString(R.string.cancel));
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
        entity.addParameter(Configurations.ORDERID, mOrder.getIdentifier());
        map.put(Configurations.ORDERID, mOrder.getIdentifier());
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
                                ToastUtil.showShort(SuccessOrderActivity.this,
                                        result.getString(Configurations.STATUSMSG));
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
    protected void onDestroy() {
        super.onDestroy();
        CommonUtil.unSubscribeSubs(mSubscriber);
    }
}
