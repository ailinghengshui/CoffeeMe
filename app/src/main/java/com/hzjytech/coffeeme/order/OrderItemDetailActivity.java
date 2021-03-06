package com.hzjytech.coffeeme.order;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.hzjytech.coffeeme.BaseActivity;
import com.hzjytech.coffeeme.Dialogs.BottomSelectDialog;
import com.hzjytech.coffeeme.Dialogs.HintDialog;
import com.hzjytech.coffeeme.R;
import com.hzjytech.coffeeme.configurations.Configurations;
import com.hzjytech.coffeeme.configurations.UmengConfig;
import com.hzjytech.coffeeme.entities.Good;
import com.hzjytech.coffeeme.entities.Ingredient;
import com.hzjytech.coffeeme.entities.NewGood;
import com.hzjytech.coffeeme.entities.NewOrder;
import com.hzjytech.coffeeme.entities.Order;
import com.hzjytech.coffeeme.entities.User;
import com.hzjytech.coffeeme.home.NewPaymentActivity;
import com.hzjytech.coffeeme.home.OrderPaymentActivity;
import com.hzjytech.coffeeme.http.JijiaHttpSubscriber;
import com.hzjytech.coffeeme.http.SubscriberOnCompletedListener;
import com.hzjytech.coffeeme.http.SubscriberOnErrorListener;
import com.hzjytech.coffeeme.http.SubscriberOnNextListener;
import com.hzjytech.coffeeme.http.api.OrderApi;
import com.hzjytech.coffeeme.listeners.IMethod1Listener;
import com.hzjytech.coffeeme.utils.AppUtil;
import com.hzjytech.coffeeme.utils.BitmapUtil;
import com.hzjytech.coffeeme.utils.CameraUtil;
import com.hzjytech.coffeeme.utils.CommonUtil;
import com.hzjytech.coffeeme.utils.DateTimeUtil;
import com.hzjytech.coffeeme.utils.DateUtil;
import com.hzjytech.coffeeme.utils.LogUtil;
import com.hzjytech.coffeeme.utils.SignUtils;
import com.hzjytech.coffeeme.utils.StringJointUtil;
import com.hzjytech.coffeeme.utils.TimeUtil;
import com.hzjytech.coffeeme.utils.ToastUtil;
import com.hzjytech.coffeeme.utils.UserUtils;
import com.hzjytech.coffeeme.widgets.TitleBar;
import com.hzjytech.coffeeme.widgets.orderview.OrderGroup;
import com.hzjytech.scan.activity.CaptureActivity;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
import com.tencent.mm.sdk.modelmsg.WXTextObject;
import com.tencent.mm.sdk.modelmsg.WXWebpageObject;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.umeng.analytics.MobclickAgent;

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
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

import cn.jpush.android.api.JPushInterface;
import rx.Observable;

@ContentView(R.layout.activity_order_item_detail)
public class OrderItemDetailActivity extends BaseActivity {

    private static final String TAG = OrderItemDetailActivity.class.getSimpleName();

    //未付款、支付取消
    private static final int STATUS_NO_PAY = 0x00;
    //支付成功
    private static final int STATUS_PAY_SUCCESS = 0x01;
    //全部退款成功 、全部已取
    private static final int STATUS_SUCCESS = 0x02;
    //部分退款成功
    private static final int STATUS_PART_REFUND = 0x03;
    //部分已取
    private static final int STATUS_PART_PICK = 0x04;
    private static final int MY_PERMISSIONS_REQUEST_CALL_CAMERA = 235;
    //取消积分版本时间
    private String VERSION_POINT_CHANGE_TIME ="2017-11-20 00:00:00";

    @ViewInject(R.id.titleBar)
    private TitleBar tbOrderitemactTilte;

    @ViewInject(R.id.rcyViewOrderitemdetail)
    private RecyclerView rcyViewOrderitemdetail;

    @ViewInject(R.id.btnOpeContainer1)
    private Button btnOpeContainer1;

    @ViewInject(R.id.btnOpe1)
    private Button btnOpe1;

    @ViewInject(R.id.btnOpe2)
    private Button btnOpe2;

    @ViewInject(R.id.btnOpeContainer2)
    private LinearLayout btnOpeContainer2;

    private List<NewGood> goods = null;
    private String identifier;
    private OrderItemDetailAdapter adapter;
    private NewOrder mOrder;


    private static OrderItemDetailActivity mInstance;
    private JijiaHttpSubscriber mSubscriber;

    public static OrderItemDetailActivity Instance() {
        if (null == mInstance)
            mInstance = new OrderItemDetailActivity();
        return mInstance;
    }

    /**
     * To deal with different order status
     */
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(final Message msg) {
            super.handleMessage(msg);
            final int orderid = msg.arg1;
            switch (msg.what) {
                case STATUS_PAY_SUCCESS:
                    btnOpeContainer2.setVisibility(View.VISIBLE);
                    btnOpeContainer1.setVisibility(View.GONE);
                    btnOpe1.setText(getResources().getString(R.string.Refund));
                    btnOpe2.setText(getResources().getString(R.string.Scan_TO_Pick));

                    btnOpe1.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            if (AppUtil.isFastClick())
                                return;

                            refund();
                        }
                    });

                    btnOpe2.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            if (AppUtil.isFastClick())
                                return;
                            if(!buyEnable(mOrder.getGoods())){
                                return;
                            }
                            if (!CameraUtil.isCameraCanUse()) {
                                //如果没有授权，则请求授权
                                HintDialog hintDialog = HintDialog.newInstance("提示",
                                        "无法获取摄像头数据，请检查是否已经打开摄像头权限。",
                                        "确定");
                                hintDialog.show(getSupportFragmentManager(), "cameraHint");
                            } else {
                                //有授权，直接开启摄像头
                                Intent intent = new Intent(OrderItemDetailActivity.this,
                                        CaptureActivity.class);
                                startActivityForResult(intent, 0);
                            }


                        }
                    });

                    break;
                case STATUS_SUCCESS:
                    btnOpeContainer2.setVisibility(View.VISIBLE);
                    btnOpeContainer1.setVisibility(View.GONE);
                    btnOpe1.setText(getResources().getString(R.string.share));
                    btnOpe2.setText(getResources().getString(R.string.buy_more));
                    btnOpe1.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            if (AppUtil.isFastClick())
                                return;
                            if(!buyEnable(mOrder.getGoods())){
                                return;
                            }
                            share();
                        }
                    });
                    btnOpe2.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            if (AppUtil.isFastClick())
                                return;
                             if(!buyEnable(mOrder.getGoods())){
                                 return;
                             }
                            buyMore(orderid);

                        }
                    });
                    break;
                case STATUS_PART_REFUND:
                    btnOpeContainer2.setVisibility(View.VISIBLE);
                    btnOpeContainer1.setVisibility(View.GONE);
                    btnOpe1.setText("分享");
                    btnOpe2.setText("再来一单");
                    btnOpe1.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            if (AppUtil.isFastClick())
                                return;
                            if(!buyEnable(mOrder.getGoods())){
                                return;
                            }

                            share();
                        }
                    });
                    btnOpe2.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            if (AppUtil.isFastClick())
                                return;
                            if(!buyEnable(mOrder.getGoods())){
                                return;
                            }

                            Intent intent = new Intent(OrderItemDetailActivity.this,
                                    NewPaymentActivity.class);
                            intent.putExtra("type", 2);
                            intent.putExtra("order", mOrder);
                            intent.putExtra("order_id", mOrder.getId());
                            startActivity(intent);
                        }
                    });
                    break;
                case STATUS_PART_PICK:
                    btnOpeContainer2.setVisibility(View.VISIBLE);
                    btnOpeContainer1.setVisibility(View.GONE);
                    btnOpe1.setText("退款");
                    btnOpe2.setText("继续取单");
                    btnOpe1.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            if (AppUtil.isFastClick())
                                return;

                            refund();
                        }
                    });
                    btnOpe2.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            if (AppUtil.isFastClick())
                                return;
                            if(!buyEnable(mOrder.getGoods())){
                                return;
                            }
                            if (!CameraUtil.isCameraCanUse()) {
                                //如果没有授权，则请求授权
                                HintDialog hintDialog = HintDialog.newInstance("提示",
                                        "无法获取摄像头数据，请检查是否已经打开摄像头权限。",
                                        "确定");
                                hintDialog.show(getSupportFragmentManager(), "cameraHint");
                            } else {
                                //有授权，直接开启摄像头
                                Intent intent = new Intent(OrderItemDetailActivity.this,
                                        CaptureActivity.class);
                                startActivityForResult(intent, 0);
                            }
                        }
                    });
                    break;
                case STATUS_NO_PAY:
                    btnOpeContainer1.setVisibility(View.VISIBLE);
                    btnOpeContainer2.setVisibility(View.GONE);
                    btnOpeContainer1.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            if (AppUtil.isFastClick())
                                return;
                            if(!buyEnable(mOrder.getGoods())){
                                return;
                            }
                            Intent intent = new Intent(OrderItemDetailActivity.this,
                                    OrderPaymentActivity.class);
                            intent.putExtra("type", 3);
                            intent.putExtra("order", mOrder);
                            startActivity(intent);
                        }
                    });
                    break;
            }
        }
    };
    private IWXAPI api;


    //再来一单
    private void buyMore(final int order_id) {

        RequestParams entity = new RequestParams(Configurations.URL_COPY);
        entity.addParameter(Configurations.TOKEN,
                UserUtils.getUserInfo()
                        .getAuth_token());
        entity.addParameter(Configurations.ORDERID, order_id);

        String device_id = JPushInterface.getRegistrationID(OrderItemDetailActivity.this);
        String timeStamp = TimeUtil.getCurrentTimeString();
        entity.addParameter(Configurations.TIMESTAMP, timeStamp);
        entity.addParameter(Configurations.DEVICE_ID, device_id);

        Map<String, String> map = new TreeMap<String, String>();
        map.put(Configurations.TOKEN,
                UserUtils.getUserInfo()
                        .getAuth_token());
        map.put(Configurations.ORDERID, String.valueOf(order_id));
        entity.addParameter(Configurations.SIGN,
                SignUtils.createSignString(device_id, timeStamp, map));

        x.http()
                .get(entity, new Callback.CommonCallback<JSONObject>() {

                    @Override
                    public void onSuccess(JSONObject result) {

                        checkResOld(result);
                        try {
                            if (result.getInt(Configurations.STATUSCODE) == 200) {

                                NewOrder order = JSON.parseObject(result.getString("results")
                                       , NewOrder.class);
                                if (null != order) {
                                    Intent intent = new Intent(OrderItemDetailActivity.this,
                                            NewPaymentActivity.class);
                                    intent.putExtra("type", 2);
                                    intent.putExtra("order", order);
                                    intent.putExtra("order_id", order_id);
                                    startActivity(intent);
                                }

                            } else {
                                ToastUtil.showShort(OrderItemDetailActivity.this,
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

    //分享
    private void share() {
        int[] images = new int[]{R.drawable.icon_share_wechat, R.drawable.icon_share_friendcircle,};
        String[] titles = new String[]{"微信好友", "微信朋友圈"};
        final BottomSelectDialog bottomSelectDialog = new BottomSelectDialog();

        bottomSelectDialog.setAdapter(OrderItemDetailActivity.this,
                images,
                titles,
                new GridLayoutManager(OrderItemDetailActivity.this, 2));
        bottomSelectDialog.setListener(new IMethod1Listener() {
            @Override
            public void OnMethod1Listener(int param) {
                switch (param) {
                    //微信好友
                    case 0:
                        shareViaWX(true);
                        break;
                    //微信朋友圈
                    case 1:
                        shareViaWX(false);
                        break;
                }
                bottomSelectDialog.dismiss();
            }
        });
        bottomSelectDialog.show(getSupportFragmentManager(), "shareOrder");

    }


    /**
     * @param isSceneSession true share via wechat friend
     *                       false share via wechat circle
     */
    private void shareViaWX(final boolean isSceneSession) {
        if (!api.isWXAppInstalled()) {
            hideLoading();
            HintDialog.newInstance("提示", "手机上没有安装微信", "确定")
                    .show(getSupportFragmentManager(), "personInfoHint");
        }
        MobclickAgent.onEvent(OrderItemDetailActivity.this, UmengConfig.EVENT_SHAREORDER_WECHAT);

        WXWebpageObject webpage = new WXWebpageObject();

//修改微信分享的url
        if (null != mOrder.getWx_share_link()) {

            webpage.webpageUrl = mOrder.getWx_share_link();
            final WXMediaMessage msg = new WXMediaMessage(webpage);
            msg.title = mOrder.getWx_share_title();
            msg.description = mOrder.getWx_share_description();

            if (null != mOrder.getWx_share_pic()) {
                ImageLoader.getInstance()
                        .loadImage(mOrder.getWx_share_pic(), new ImageLoadingListener() {
                            @Override
                            public void onLoadingStarted(String s, View view) {

                            }

                            @Override
                            public void onLoadingFailed(
                                    String s,
                                    View view,
                                    FailReason failReason) {
                                LogUtil.d("failReason", failReason.toString());

                            }

                            @Override
                            public void onLoadingComplete(String s, View view, Bitmap bitmap) {
                                msg.thumbData = BitmapUtil.bmpToByteArray(bitmap, true);
                                SendMessageToWX.Req req = new SendMessageToWX.Req();
                                req.transaction = "webpage" + System.currentTimeMillis();
                                req.message = msg;
                                if (isSceneSession) {
                                    req.scene = SendMessageToWX.Req.WXSceneSession;
                                } else {
                                    req.scene = SendMessageToWX.Req.WXSceneTimeline;
                                }
                                api.sendReq(req);

                            }

                            @Override
                            public void onLoadingCancelled(String s, View view) {

                            }
                        });
            } else {
                Bitmap thumb = BitmapFactory.decodeResource(getResources(), R.mipmap.icon_launcher);
                msg.thumbData = BitmapUtil.bmpToByteArray(thumb, true);
                SendMessageToWX.Req req = new SendMessageToWX.Req();
                req.transaction = "webpage" + System.currentTimeMillis();
                req.message = msg;
                if (isSceneSession) {
                    req.scene = SendMessageToWX.Req.WXSceneSession;
                } else {
                    req.scene = SendMessageToWX.Req.WXSceneTimeline;
                }
                api.sendReq(req);
            }


        } else {
            ToastUtil.showShort(OrderItemDetailActivity.this, getString(R.string.share_fail));
        }

    }

    /**
     * 退款
     */
    private void refund() {
        RequestParams entity = new RequestParams(Configurations.URL_REFUND);
        entity.addParameter(Configurations.AUTH_TOKEN,
                UserUtils.getUserInfo()
                        .getAuth_token());
        entity.addParameter("order_id", identifier);

        String device_id = JPushInterface.getRegistrationID(OrderItemDetailActivity.this);
        String timeStamp = TimeUtil.getCurrentTimeString();
        entity.addParameter(Configurations.TIMESTAMP, timeStamp);
        entity.addParameter(Configurations.DEVICE_ID, device_id);

        Map<String, String> map = new TreeMap<String, String>();
        map.put(Configurations.AUTH_TOKEN,
                UserUtils.getUserInfo()
                        .getAuth_token());
        map.put("order_id", identifier);
        entity.addParameter(Configurations.SIGN,
                SignUtils.createSignString(device_id, timeStamp, map));

        x.http()
                .request(HttpMethod.PUT, entity, new Callback.CommonCallback<JSONObject>() {
                    @Override
                    public void onSuccess(JSONObject result) {
                        LogUtil.d(TAG + "refund", result.toString());

                        checkResOld(result);
                        try {
                            if (result.getInt(Configurations.STATUSCODE) == 500) {
                                ToastUtil.showShort(OrderItemDetailActivity.this,
                                        result.getString(Configurations.STATUSMSG));
                                return;
                            }
                            getBanlance();
                            ToastUtil.showShort(OrderItemDetailActivity.this,
                                    result.getString(Configurations.STATUSMSG));

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(Throwable ex, boolean isOnCallback) {
                        LogUtil.i("", "---Throwable---" + ex.toString());
                    }

                    @Override
                    public void onCancelled(CancelledException cex) {

                    }

                    @Override
                    public void onFinished() {

                    }
                });

    }


    private void getBanlance() {

        String token = UserUtils.getUserInfo()
                .getAuth_token();
        RequestParams params = new RequestParams(Configurations.URL_CHECK_TOKEN);
        params.addParameter(Configurations.AUTH_TOKEN, token);

        String timeStamp = TimeUtil.getCurrentTimeString();
        String device_id = JPushInterface.getRegistrationID(OrderItemDetailActivity.this);
        params.addParameter(Configurations.TIMESTAMP, timeStamp);
        params.addParameter(Configurations.DEVICE_ID, device_id);

        Map<String, String> map = new TreeMap<>();
        map.put(Configurations.AUTH_TOKEN,
                UserUtils.getUserInfo()
                        .getAuth_token());
        params.addParameter(Configurations.SIGN,
                com.hzjytech.coffeeme.utils.SignUtils.createSignString(device_id, timeStamp, map));

        x.http()
                .get(params, new Callback.CommonCallback<String>() {
                    @Override
                    public void onSuccess(String result) {
                        try {
                            int statusCode = new JSONObject(result).getInt("statusCode");

                            if (statusCode == 200) {
                                User user = JSON.parseObject((new JSONObject(result)).getJSONObject(
                                        "results")
                                        .getString("user"), User.class);
                                UserUtils.saveUserInfo(user);
                                finish();
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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initData();
        initView(savedInstanceState);
        mInstance = this;

        api = WXAPIFactory.createWXAPI(OrderItemDetailActivity.this, Configurations.WX_APP_ID);

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            String result = data.getStringExtra(CaptureActivity.SCAN_RESULT_KEY);
            qrFetch(result);
        } else if (resultCode == RESULT_CANCELED) {
            ToastUtil.showShort(OrderItemDetailActivity.this,
                    getResources().getString(R.string.cancel));
        }
    }

    /**
     * 二维码取单
     *
     * @param result
     */
    private void qrFetch(String result) {
        if (!TextUtils.isEmpty(result)) {
            RequestParams entity = new RequestParams(Configurations.URL_QRFETCH);
            entity.addParameter(Configurations.AUTH_TOKEN,
                    UserUtils.getUserInfo()
                            .getAuth_token());
            entity.addParameter(Configurations.VM_ID, result);
            entity.addParameter("order_id", mOrder.getIdentifier());

            String device_id = JPushInterface.getRegistrationID(OrderItemDetailActivity.this);
            String timeStamp = TimeUtil.getCurrentTimeString();
            entity.addParameter(Configurations.TIMESTAMP, timeStamp);
            entity.addParameter(Configurations.DEVICE_ID, device_id);

            Map<String, String> map = new TreeMap<String, String>();
            map.put(Configurations.AUTH_TOKEN,
                    UserUtils.getUserInfo()
                            .getAuth_token());
            map.put(Configurations.VM_ID, result);
            map.put("order_id", mOrder.getIdentifier());
            entity.addParameter(Configurations.SIGN,
                    SignUtils.createSignString(device_id, timeStamp, map));


            x.http()
                    .request(HttpMethod.PUT, entity, new Callback.CommonCallback<JSONObject>() {
                        @Override
                        public void onSuccess(JSONObject result) {

                            checkResOld(result);
                            try {
                                ToastUtil.showShort(OrderItemDetailActivity.this,
                                        result.getString(Configurations.STATUSMSG));
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
        } else {
            ToastUtil.showShort(OrderItemDetailActivity.this,
                    getResources().getString(R.string.fail));
        }

    }

    private void initData() {
        if (getIntent() != null) {
            identifier = getIntent().getStringExtra("identifier");
            showLoading();
        }
    }

    private void initView(Bundle savedInstanceState) {
        tbOrderitemactTilte.setTitle("订单详情");
        tbOrderitemactTilte.setTitleColor(Color.WHITE);
        tbOrderitemactTilte.setLeftImageResource(R.drawable.icon_left);
        tbOrderitemactTilte.setLeftClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        adapter = new OrderItemDetailAdapter();
        rcyViewOrderitemdetail.setLayoutManager(new LinearLayoutManager(OrderItemDetailActivity
                .this));
        rcyViewOrderitemdetail.setAdapter(adapter);

        btnOpeContainer1.setVisibility(View.GONE);

    }

    private void loadData() {
        Observable<NewOrder> observable = OrderApi.getOrderDetail(this,UserUtils.getUserInfo()
                .getAuth_token(), identifier);
        /**
         * 0: not pay
         * 1: success
         * 2: all refund
         * 3: cancel pay
         * 4: part refund
         * 5: all take
         * 6: part take
         */mSubscriber = JijiaHttpSubscriber.buildSubscriber(this)
                 .setOnNextListener(new SubscriberOnNextListener<NewOrder>() {
                     @Override
                     public void onNext(NewOrder order) {
                         mOrder = order;
                         /**
                          * 0: not pay
                          * 1: success
                          * 2: all refund
                          * 3: cancel pay
                          * 4: part refund
                          * 5: all take
                          * 6: part take
                          */
                         Message message = new Message();
                         switch (mOrder.getStatus()) {
                             case 1:
                                 message.what = STATUS_PAY_SUCCESS;
                                 break;
                             case 2:
                                 message.what = STATUS_SUCCESS;
                                 message.arg1 = mOrder.getId();
                                 break;
                             case 3:
                                 message.what = STATUS_NO_PAY;
                                 break;
                             case 4:
                                 message.what = STATUS_PART_REFUND;
                                 break;
                             case 5:
                                 message.what = STATUS_SUCCESS;
                                 message.arg1 = mOrder.getId();
                                 break;
                             case 6:
                                 message.what = STATUS_PART_PICK;
                                 break;
                             default:
                                 message.what = STATUS_NO_PAY;
                                 break;

                         }
                         mHandler.sendMessage(message);

                         LogUtil.d(TAG + "mOrder",
                                 "order_id----" + mOrder.getId() + "-----" + mOrder.getFetch_code
                                         () + mOrder.getDescription());

                         for (int i = 0; i < mOrder.getGoods()
                                 .size(); i++) {
                             NewGood good = mOrder.getGoods()
                                     .get(i);
                             goods.add(good);
                         }

                         adapter.notifyDataSetChanged();
                     }
                 })
                 .setOnCompletedListener(new SubscriberOnCompletedListener() {
                     @Override
                     public void onCompleted() {
                         hideLoading();
                     }
                 })
                .setOnErrorListener(new SubscriberOnErrorListener() {
                    @Override
                    public void onError(Throwable e) {
                        hideLoading();
                    }
                })
                 .build();
        observable.subscribe(mSubscriber);
    }

    class OrderItemDetailAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private static final int ORDER_ITEM = 1;
        private static final int ORDER_SUM = 2;
        private static final int ORDER_HEAD = 0;


        class ViewHolderOrderItem extends RecyclerView.ViewHolder {
            private final OrderGroup mOrderGroup;

            public ViewHolderOrderItem(View itemView) {
                super(itemView);
                mOrderGroup = (OrderGroup) itemView.findViewById(R.id.og_detail_group);
            }
        }

        class ViewHolderSum extends RecyclerView.ViewHolder {


            private final TextView tvOrderitemdetailsumOriginalsum;
            private final TextView tvOrderitemdetailsumCouponinfo;
            private final TextView tvOrderitemdetailsumSum;
            private final TextView tvOrderitemdetailsumId;
            private final TextView tvOrderitemdetailsumDate;
            private final TextView tvOrderitemdetailsumPayprovider;
            private final TextView tvOrderitemdetailsumStatus;
            private final TextView tvOrderitemdetailsumFetchcode;
            private final TextView tvOrderitemdetailsumShare;
            private final ImageView ivOrderitemdetailsumShare;
            private final RelativeLayout rlOrderitemdetailShare;
            private final TextView tvOrderitemdetailsumPointinfo;
            private final LinearLayout mLlOrderInfoPoint;
            private final    View mLineOrderInfoPoint;

            public ViewHolderSum(View itemView) {
                super(itemView);
                tvOrderitemdetailsumOriginalsum = (TextView) itemView.findViewById(R.id
                        .tvOrderitemdetailsumOriginalsum);
                tvOrderitemdetailsumCouponinfo = (TextView) itemView.findViewById(R.id
                        .tvOrderitemdetailsumCouponinfo);
                tvOrderitemdetailsumPointinfo = (TextView) itemView.findViewById(R.id
                        .tvOrderitemdetailsumPointinfo);
                tvOrderitemdetailsumSum = (TextView) itemView.findViewById(R.id
                        .tvOrderitemdetailsumSum);
                tvOrderitemdetailsumId = (TextView) itemView.findViewById(R.id
                        .tvOrderitemdetailsumId);
                tvOrderitemdetailsumDate = (TextView) itemView.findViewById(R.id
                        .tvOrderitemdetailsumDate);
                tvOrderitemdetailsumPayprovider = (TextView) itemView.findViewById(R.id
                        .tvOrderitemdetailsumPayprovider);
                tvOrderitemdetailsumStatus = (TextView) itemView.findViewById(R.id
                        .tvOrderitemdetailsumStatus);
                tvOrderitemdetailsumFetchcode = (TextView) itemView.findViewById(R.id
                        .tvOrderitemdetailsumFetchcode);
                tvOrderitemdetailsumShare = (TextView) itemView.findViewById(R.id
                        .tvOrderitemdetailsumShare);
                ivOrderitemdetailsumShare = (ImageView) itemView.findViewById(R.id
                        .ivOrderitemdetailsumShare);
                rlOrderitemdetailShare = (RelativeLayout) itemView.findViewById(R.id
                        .rlOrderitemdetailShare);
                mLlOrderInfoPoint = (LinearLayout) itemView.findViewById(R.id.ll_order_info_point);
                mLineOrderInfoPoint = itemView.findViewById(R.id.line_order_info_point);
            }
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            RecyclerView.ViewHolder viewHolder;
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            switch (viewType) {
                case ORDER_HEAD:
                    View view0 = inflater.inflate(R.layout.order_item_head, parent, false);
                    viewHolder = new ViewHolderOrderItem(view0);
                case ORDER_ITEM:
                    View view1 = inflater.inflate(R.layout.order_item_detail, parent, false);
                    viewHolder = new ViewHolderOrderItem(view1);
                    break;
                default:
                    View view2 = inflater.inflate(R.layout.order_item_detail_sum, parent, false);
                    viewHolder = new ViewHolderSum(view2);
                    break;
            }
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            switch (holder.getItemViewType()) {
                case ORDER_HEAD:
                    break;
                case ORDER_ITEM:
                    ViewHolderOrderItem viewHolderOrderItem = (ViewHolderOrderItem) holder;
                    viewHolderOrderItem.mOrderGroup.setData(goods);
                    break;
                default:
                    ViewHolderSum viewHolderSum = (ViewHolderSum) holder;
                    setViewHolderSum(viewHolderSum, position);
                    break;
            }

        }

        private void setViewHolderSum(ViewHolderSum viewHolderSum, int position) {

            DecimalFormat fnum = new DecimalFormat("##0.00");
            viewHolderSum.tvOrderitemdetailsumOriginalsum.setText(StringJointUtil.obj2String(fnum
                    .format(
                    mOrder.getOriginal_sum()), "￥", true));
            viewHolderSum.tvOrderitemdetailsumCouponinfo.setText(mOrder.getCoupon_info());
            viewHolderSum.tvOrderitemdetailsumPointinfo.setText(StringJointUtil.obj2String(mOrder
                            .getPoint_count(),
                    "",
                    true));
            viewHolderSum.tvOrderitemdetailsumSum.setText(StringJointUtil.obj2String(fnum.format(
                    mOrder.getSum()), "￥", true));
            viewHolderSum.tvOrderitemdetailsumId.setText(mOrder.getIdentifier());
            viewHolderSum.tvOrderitemdetailsumDate.setText(DateTimeUtil.longToShort9(mOrder.getCreated_at()));
            long versionChangeTime = DateTimeUtil.stringToLong(VERSION_POINT_CHANGE_TIME,
                    DateTimeUtil.DATE_FORMAT_LONG);
            boolean afterChangeVersionTime = DateTimeUtil.after(mOrder.getCreated_at(), versionChangeTime);
            if(afterChangeVersionTime){
                viewHolderSum.mLlOrderInfoPoint.setVisibility(View.GONE);
                viewHolderSum. mLineOrderInfoPoint.setVisibility(View.GONE);

            }else{
                viewHolderSum.mLlOrderInfoPoint.setVisibility(View.VISIBLE);
                viewHolderSum. mLineOrderInfoPoint.setVisibility(View.VISIBLE);
            }

            /**
             * 1 : alipay
             * 2 :wechatpay
             * 3 :jijiapay
             */
            switch (mOrder.getPayment_provider()) {
                case 1:
                    viewHolderSum.tvOrderitemdetailsumPayprovider.setText(getResources()
                            .getString(R.string.PayViaAlipay));
                    break;
                case 2:
                    viewHolderSum.tvOrderitemdetailsumPayprovider.setText(getString(R.string
                            .PayViaWechat));
                    break;
                case 3:
                    viewHolderSum.tvOrderitemdetailsumPayprovider.setText(getString(R.string
                            .PayViaBalance));
                    break;
                default:
                    if (mOrder.getSum() > 0.00f) {
                        viewHolderSum.tvOrderitemdetailsumPayprovider.setText(getString(R.string
                                .NoPay));
                    } else {
                        viewHolderSum.tvOrderitemdetailsumPayprovider.setText("");
                    }
                    break;
            }


            switch (mOrder.getStatus()) {
                case 1:
                    viewHolderSum.tvOrderitemdetailsumStatus.setText(getString(R.string
                            .PaySuccess));
                    viewHolderSum.tvOrderitemdetailsumShare.setVisibility(View.VISIBLE);
                    viewHolderSum.ivOrderitemdetailsumShare.setVisibility(View.VISIBLE);
                    viewHolderSum.rlOrderitemdetailShare.setOnClickListener(new View
                            .OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if(!buyEnable(mOrder.getGoods())){
                                return;
                            }
                            shareFetchCode(mOrder);
                        }
                    });

                    break;
                case 2:
                    viewHolderSum.tvOrderitemdetailsumStatus.setText(getString(R.string.RefundAll));
                    viewHolderSum.tvOrderitemdetailsumShare.setVisibility(View.INVISIBLE);
                    viewHolderSum.ivOrderitemdetailsumShare.setVisibility(View.INVISIBLE);
                    viewHolderSum.rlOrderitemdetailShare.setClickable(false);
                    break;
                case 3:
                    viewHolderSum.tvOrderitemdetailsumStatus.setText(getString(R.string.PayCancel));
                    viewHolderSum.tvOrderitemdetailsumShare.setVisibility(View.INVISIBLE);
                    viewHolderSum.ivOrderitemdetailsumShare.setVisibility(View.INVISIBLE);
                    viewHolderSum.rlOrderitemdetailShare.setClickable(false);
                    break;
                case 4:
                    viewHolderSum.tvOrderitemdetailsumStatus.setText(getString(R.string
                            .RefundPart));
                    viewHolderSum.tvOrderitemdetailsumShare.setVisibility(View.INVISIBLE);
                    viewHolderSum.ivOrderitemdetailsumShare.setVisibility(View.INVISIBLE);
                    viewHolderSum.rlOrderitemdetailShare.setClickable(false);
                    break;
                case 5:
                    viewHolderSum.tvOrderitemdetailsumStatus.setText(getString(R.string.TakeAll));
                    viewHolderSum.tvOrderitemdetailsumShare.setVisibility(View.INVISIBLE);
                    viewHolderSum.ivOrderitemdetailsumShare.setVisibility(View.INVISIBLE);
                    viewHolderSum.rlOrderitemdetailShare.setClickable(false);
                    break;
                case 6:
                    viewHolderSum.tvOrderitemdetailsumStatus.setText(String.format(Locale.getDefault(),getString(R.string.leave_goode_untake),getNumUnToken(mOrder.getGoods())));
                    viewHolderSum.tvOrderitemdetailsumShare.setVisibility(View.INVISIBLE);
                    viewHolderSum.ivOrderitemdetailsumShare.setVisibility(View.INVISIBLE);
                    viewHolderSum.rlOrderitemdetailShare.setClickable(false);
                    break;
                default:
                    viewHolderSum.tvOrderitemdetailsumStatus.setText(getString(R.string.PayNo));
                    viewHolderSum.tvOrderitemdetailsumShare.setVisibility(View.INVISIBLE);
                    viewHolderSum.ivOrderitemdetailsumShare.setVisibility(View.INVISIBLE);
                    viewHolderSum.rlOrderitemdetailShare.setClickable(false);
                    break;

            }
            viewHolderSum.tvOrderitemdetailsumFetchcode.setText(mOrder.getFetch_code());


        }
        /**
         * 获取未取走的咖啡数量
         * @param goods
         */
        private int getNumUnToken(List<NewGood> goods) {
            if(goods==null){
                return 0;
            }
            int num=0;
            for (NewGood good : goods) {
                if(!good.isBe_token()){
                    num++;
                }
            }
            return num;

        }



        @Override
        public int getItemCount() {
            return mOrder == null ? 0 : 3;
        }

        @Override
        public int getItemViewType(int position) {

            if (position == 0) {
                return ORDER_HEAD;

            }else if(position==1){
                return ORDER_ITEM;
            } else {
                return ORDER_SUM;
            }
        }
    }

    private void shareFetchCode(final NewOrder order) {
        MobclickAgent.onEvent(OrderItemDetailActivity.this,
                UmengConfig.EVENT_SHAREFETCHCODE_WECHAT);
        WXTextObject textObj = new WXTextObject();
        textObj.text = order.getShare_fetch_code();
        WXMediaMessage msg = new WXMediaMessage();
        msg.mediaObject = textObj;
        msg.description = order.getShare_fetch_code();
        SendMessageToWX.Req req = new SendMessageToWX.Req();
        req.transaction = String.valueOf(System.currentTimeMillis());
        req.message = msg;
        req.scene = SendMessageToWX.Req.WXSceneSession;
        api.sendReq(req);
    }


    @Override
    protected void onResume() {
        super.onResume();

        goods = new ArrayList<>();
        loadData();

        MobclickAgent.onPageStart(UmengConfig.ORDERITEMDETAILACTIVITY);
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(UmengConfig.ORDERITEMDETAILACTIVITY);
        MobclickAgent.onPause(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        CommonUtil.unSubscribeSubs(mSubscriber);
    }

}