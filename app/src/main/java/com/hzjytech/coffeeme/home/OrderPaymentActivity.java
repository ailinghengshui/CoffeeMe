package com.hzjytech.coffeeme.home;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alipay.sdk.app.PayTask;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hzjytech.coffeeme.BaseActivity;
import com.hzjytech.coffeeme.Dialogs.CustomDialogWithTwoDiffButton;
import com.hzjytech.coffeeme.Dialogs.ITwoButtonClick;
import com.hzjytech.coffeeme.MainActivity;
import com.hzjytech.coffeeme.R;
import com.hzjytech.coffeeme.configurations.Configurations;
import com.hzjytech.coffeeme.configurations.UmengConfig;
import com.hzjytech.coffeeme.entities.AppDosage;
import com.hzjytech.coffeeme.entities.DisplayItems;
import com.hzjytech.coffeeme.entities.Good;
import com.hzjytech.coffeeme.entities.Ingredient;
import com.hzjytech.coffeeme.entities.NewGood;
import com.hzjytech.coffeeme.entities.NewOrder;
import com.hzjytech.coffeeme.entities.Order;
import com.hzjytech.coffeeme.entities.User;
import com.hzjytech.coffeeme.entities.preWxPayInfo;
import com.hzjytech.coffeeme.me.NewRechargeActivity;
import com.hzjytech.coffeeme.order.OrderItemDetailActivity;
import com.hzjytech.coffeeme.pays.alipay.PayResult;
import com.hzjytech.coffeeme.pays.alipay.SignUtils;
import com.hzjytech.coffeeme.utils.AppUtil;
import com.hzjytech.coffeeme.utils.LogUtil;
import com.hzjytech.coffeeme.utils.NetUtil;
import com.hzjytech.coffeeme.utils.StringUtil;
import com.hzjytech.coffeeme.utils.TimeUtil;
import com.hzjytech.coffeeme.utils.ToastUtil;
import com.hzjytech.coffeeme.utils.UserUtils;
import com.hzjytech.coffeeme.widgets.TitleBar;
import com.hzjytech.coffeeme.widgets.orderview.OrderGroup;
import com.hzjytech.coffeeme.widgets.row.RowView;
import com.hzjytech.coffeeme.widgets.row.RowViewDesc;
import com.hzjytech.coffeeme.wxapi.WXPayEntryActivity;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.HttpMethod;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import cn.jpush.android.api.JPushInterface;

//订单支付界面
@ContentView(R.layout.activity_order_payment)
public class OrderPaymentActivity extends BaseActivity {

    private static final int PAYMENT_REQUEST_CODE = 1;
    private static final String TAG = OrderPaymentActivity.class.getSimpleName();
    private static final int WECHAT_PAY_SUCCESS = 0x11;
    private static final int WECHAT_PAY_FAILED = 0x12;
    private static final int WECHAT_PAY_CANCEL = 0x13;


    private DecimalFormat fnum = new DecimalFormat("##0.00");

    float realPay = 0.00f;
    float pay = 0.00f;

    PaymentDetailAdapter adapter = new PaymentDetailAdapter();

    @ViewInject(R.id.titleBar)
    private TitleBar tbPaymentTitle;

    @ViewInject(R.id.rcyViewPaymentDetail)
    private RecyclerView rcyViewPaymentDetail;
    @ViewInject(R.id.ll_order_point)
    private LinearLayout mLlOrderPoint;
    @ViewInject(R.id.line_order_point)
    private View mLineView;
/*
    @ViewInject(R.id.imPaymentback)
    private ImageView imPaymentback;

    @ViewInject(R.id.ivPaymentIcon)
    private ImageView ivPaymentIcon;

    @ViewInject(R.id.tvPaymentMethodName)
    private TextView tvPaymentMethodName;*/

    private CustomDialogWithTwoDiffButton customDialogWithTwoDiffButton;
    private String coffeeMePocketBalance;

/*    @Event(R.id.imPaymentback)
    private void onimPaymentbackClick(View view) {
        finish();
    }*/


    @ViewInject(R.id.rvPaymentamountAmount)
    private RowView rvPaymentamountAmount;

    @ViewInject(R.id.rvPaymentamountCoupon)
    private RowView rvPaymentamountCoupon;

    @ViewInject(R.id.tvPaymentamountRatepoint)
    private TextView tvPaymentamountRatepoint;

    @ViewInject(R.id.tvPaymentamountRatepointMoney)
    private TextView tvPaymentamountRatepointMoney;


    @ViewInject(R.id.tvPaymentamountRealpay)
    private TextView tvPaymentamountRealpay;

    @ViewInject(R.id.llPaymentPayInit)
    private LinearLayout llPaymentPayInit;

    @ViewInject(R.id.llPaymentPay)
    private LinearLayout llPaymentPay;


    //购买数量
    private int count;

    /**
     * define pay method
     * 0 for jijia
     * 1 for apipay
     * 2 for wxpay
     */
    private int payMethod = 0;
    private ArrayList<AppDosage> appDosages = new ArrayList<AppDosage>();
    private List<NewGood> goods;
    private Map<NewGood, Integer> selectGood = new HashMap<>();
    //coupon id
    private int couponId = -1;
    private String identifier;
    private int id;

    private NewOrder mOrder;

    //   0   'buy_now'为立即购买类型，  1  'cart_buy’ 为购物车结算,    2  'order_again’,为再来一单类型    3   待支付订单
    private int buytype = -1;
    private preWxPayInfo preWxInfo;
    private boolean isPaying=false;


/*    @Event(R.id.rlPaymentPay)
    private void onPaymentPayClick(View view) {

        if (AppUtil.isFastClick())
            return;

        if (buytype == 3 && null != mOrder) {
            identifier = mOrder.getIdentifier();
            payOrder();
        }

    }*/

    @Event(R.id.btnPaymentPay)
    private void onBalancePayClick(View view) {
        if (AppUtil.isFastClick())
            return;

        payMethod = 0;
        payOrder();
    }


    private void payOrder() {
        showLoading();
        RequestParams entity = new RequestParams(Configurations.URL_ORDER_PAY);
        entity.addParameter(Configurations.TOKEN, UserUtils.getUserInfo().getAuth_token());
        entity.addParameter(Configurations.IP, NetUtil.getIPAddress(true));
        entity.addParameter(Configurations.IDENTIFIER, identifier);

        String device_id= JPushInterface.getRegistrationID(OrderPaymentActivity.this);
        String timeStamp= TimeUtil.getCurrentTimeString();
        entity.addParameter(Configurations.TIMESTAMP, timeStamp);
        entity.addParameter(Configurations.DEVICE_ID,device_id );

        Map<String, String> map=new TreeMap<String, String>();
        map.put(Configurations.TOKEN, UserUtils.getUserInfo().getAuth_token());
        map.put(Configurations.IP, NetUtil.getIPAddress(true));
        map.put(Configurations.IDENTIFIER, identifier);

        switch (payMethod) {
            //jijiapay
            case 0:
                entity.addParameter(Configurations.PAYMENT_PROVIDER, "jijiapay");
                map.put(Configurations.PAYMENT_PROVIDER, "jijiapay");
                break;
            //alipay
            case 1:
                entity.addParameter(Configurations.PAYMENT_PROVIDER, "alipay");
                map.put(Configurations.PAYMENT_PROVIDER, "alipay");
                break;
            //wxpay
            case 2:
                entity.addParameter(Configurations.PAYMENT_PROVIDER, "wxpay");
                map.put(Configurations.PAYMENT_PROVIDER, "wxpay");
                break;
        }

        entity.addParameter(Configurations.SIGN, com.hzjytech.coffeeme.utils.SignUtils.createSignString(device_id,timeStamp,map));

        x.http().request(HttpMethod.PUT, entity, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                LogUtil.e("result",result);
                try {
                    JSONObject object = new JSONObject(result);
                    if (object.getInt(Configurations.STATUSCODE) == 200) {
                        if (payMethod == 0) {
                            parsepayOrderResult(result);
                        } else if (payMethod == 1) {

                            startAliPay();
                        } else if (payMethod == 2) {

                            startWxPay(result);
                        }
                    }else{
                        final CustomDialogWithTwoDiffButton customDialogWithTwoDiffButton =
                                CustomDialogWithTwoDiffButton.newInstance("支付失败", R.drawable.icon_pay_err, object.getString(Configurations.STATUSMSG));
                        customDialogWithTwoDiffButton.setOnTwoButtonClick(new ITwoButtonClick() {
                            @Override
                            public void onLeftButtonClick() {

                            }

                            @Override
                            public void onRightButtonClick() {
                                MainActivity.Instance().goOrder = true;
                                ModulationActivity.Instance().finish();
                                NewCartActivity.Instance().finish();

                                OrderItemDetailActivity.Instance().finish();
                                customDialogWithTwoDiffButton.dismiss();
                                isPayFailure=true;
                                isPaying=false;
                                // finish();
                            }
                        });
                        //这里直接调用show方法会报java.lang.IllegalStateException: Can not perform this action after onSaveInstanceState
                        //因为show方法中是通过commit进行的提交(通过查看源码)
                        //这里为了修复这个问题，使用commitAllowingStateLoss()方法
                        //注意：DialogFragment是继承自android.app.Fragment，这里要注意同v4包中的Fragment区分，别调用串了
                        //DialogFragment有自己的好处，可能也会带来别的问题
                        //dialog.show(getFragmentManager(), "SignDialog");
                    /*    FragmentManager fm = getSupportFragmentManager();
                        FragmentTransaction ft = fm.beginTransaction();
                        ft.add(customDialogWithTwoDiffButton,"customDialog");
                        ft.commitAllowingStateLoss();*/
                        customDialogWithTwoDiffButton.show(getSupportFragmentManager(),"customDialog");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }


                hideLoading();
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                hideLoading();
                showNetError();
                isPaying=false;
            }

            @Override
            public void onCancelled(CancelledException cex) {
                hideLoading();
            }

            @Override
            public void onFinished() {
                hideLoading();
            }
        });
    }


    private String subject = null;
    private String total_fee = null;
    private String body = null;
    private String notify_url = null;
    private String orderid = null;

    private void startAliPay() {

        DecimalFormat fnum = new DecimalFormat("##0.00");
        subject = "CoffeeMe支付";
        total_fee = "" + fnum.format(realPay);

        body = "咖啡订单支付";
        notify_url = Configurations.URL_ALIPAYNOTIFY;
        orderid = identifier;

        if (!StringUtil.isNullOrEmpty(subject) && !StringUtil.isNullOrEmpty(total_fee) && !StringUtil.isNullOrEmpty(body)
                && !StringUtil.isNullOrEmpty(orderid)
                && !StringUtil.isNullOrEmpty(notify_url)) {
            pay();
        }
    }


    private void startWxPay(String result) {

        showLoading();
        try {
            JSONObject object = new JSONObject(result);
            if (object.getInt(Configurations.STATUSCODE) == 200) {

                preWxInfo = new preWxPayInfo();
                preWxInfo = new Gson().fromJson(object.getJSONObject("results").getString("prepay"), new TypeToken<preWxPayInfo>() {
                }.getType());


            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String appid = preWxInfo.getAppid();
        String noncestr = preWxInfo.getNoncestr();
        String partnerid = preWxInfo.getPartnerid();
        String prepayid = preWxInfo.getPrepayid();
        String timestamp = preWxInfo.getTimestamp();
        String sign = preWxInfo.getSign();

        Intent intent = new Intent(OrderPaymentActivity.this, WXPayEntryActivity.class);
        intent.putExtra("appid", appid);
        intent.putExtra("noncestr", noncestr);
        intent.putExtra("partnerid", partnerid);
        intent.putExtra("prepayid", prepayid);
        intent.putExtra("timestamp", timestamp);
        intent.putExtra("sign", sign);
        startActivityForResult(intent, NewRechargeActivity.REQUEST_WECHAT_PAY);

    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(UmengConfig.PAYMENTACTIVITY);
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(UmengConfig.PAYMENTACTIVITY);
        MobclickAgent.onPause(this);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == NewRechargeActivity.REQUEST_WECHAT_PAY) {

            hideLoading();
            if (data != null) {

                Bundle b = data.getExtras();
                if (b != null) {
                    int errCode = b.getInt("errCode");
                    switch (errCode) {
                        case 0:// success
                            Map<String, String> map = new HashMap<>();
                            map.put(UmengConfig.PARAM_PAYMETHOD, UmengConfig.PARAM_WECHATPAY);
                            MobclickAgent.onEventValue(OrderPaymentActivity.this, UmengConfig.EVENT_PAYORDER_WECHATPAY, map, (int) realPay);

                            mHandler.sendEmptyMessage(WECHAT_PAY_SUCCESS);

                            break;
                        case -1:// failed
                            mHandler.sendEmptyMessage(WECHAT_PAY_FAILED);
                            break;
                        case -2:// cancel
                            mHandler.sendEmptyMessage(WECHAT_PAY_CANCEL);
                            break;
                        default:
                            break;
                    }

                }
            }
        }
    }


    // 商户PID
    public static final String PARTNER = "2088911832538297";
    // 商户收款账号
    public static final String SELLER = "jykj@hzjytech.com";
    // 商户私钥，pkcs8格式
    public static final String RSA_PRIVATE = "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAL" +
            "/aKG8LlTUMAaatZAyN6Ik9J63jBSZYcYic9GLpGSkGCGQy1viNcJDqpQCPirek78c7jJnwbEI+y6" +
            "/lPupaHbVXa5eNM49vkAtcUfcVup38KrTxvuzHjq8/iXH96KaRoy/5mxDanHAJtHpRtCvAxy1YXnZQWuSusf1V" +
            "/QlhpxWNAgMBAAECgYBf/TD5zlvK" +
            "/MTetiY2udG8yKJPuCRdeSbk6VLFIkd6ZRBRwbYZCftmJmCEFU2ccKOflUXVx5sC0hRrX1HJQ2lU8TIsIyGgSmXKefyK" +
            "+87BvZsww1ezVkZUKZyS0hIUSF2b+IRdJ1U0IoQErWh3o3tbkfIqMVlcLaxuVXxzMOVpwQJBAPOxAqohdTbeGGI6clHaxc8" +
            "+pqr6LOS2QQ2dtpDV/TkfHNW98uU7DxFAjmDbDN5/6j7mDNNQfDpuwquvM1CMicUCQQDJitpsgvP7OFogztNRq" +
            "/39sk92f55ophn4hCBeb2C0HIxCyyG1V6u0K1oHEwLXMyxywZih6Pclq4S5vZ2AIkEpAkBFb+CkfZgapDoqcyDz9fR7UwzGlzaHjO8IZDILHw5iYl0jAlbdvnBSqCRpsMD" +
            "//U7P0VlCNSLQu5K4vNlQUFtJAkEAqGzRfD0zh43gQ+qTznzdFVYd8flUeD" +
            "/XFEfWhRSuy9XPoUIAOXu6kiZoZeENTqoFSyFeXHBDmcdsc4xyQtJdgQJATVHNq79TAXE8u2C9" +
            "RqGKH41ZrrCxoEsTwVmeOu9lZmlf7ZLngLsiAVEvezeAdTL6xRUUX7DjQkyjKLnvCpV82g==";
    // 支付宝公钥
    public static final String RSA_PUBLIC = "";
    private static final int SDK_PAY_FLAG = 1;


    private boolean isPayFailure;
    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @SuppressWarnings("unused")
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SDK_PAY_FLAG: {
                    PayResult payResult = new PayResult((String) msg.obj);
                    /**
                     * 同步返回的结果必须放置到服务端进行验证（验证的规则请看https://doc.open.alipay.com/doc2/
                     * detail.htm?spm=0.0.0.0.xdvAU6&treeId=59&articleId=103665&
                     * docType=1) 建议商户依赖异步通知
                     */
                    String resultInfo = payResult.getResult();// 同步返回需要验证的信息

                    String resultStatus = payResult.getResultStatus();
                    // 判断resultStatus 为“9000”则代表支付成功，具体状态码代表含义可参考接口文档
                    if (TextUtils.equals(resultStatus, "9000")) {

                        Map<String, String> map = new HashMap<>();
                        map.put(UmengConfig.PARAM_PAYMETHOD, UmengConfig.PARAM_ALIPAY);
                        MobclickAgent.onEventValue(OrderPaymentActivity.this, UmengConfig.EVENT_PAYORDER_ALIPAY, map, (int) realPay);
                        goSuccessOrder((int) mOrder.getGet_point());
                        isPaying=false;
                    } else {
                        // 判断resultStatus 为非"9000"则代表可能支付失败
                        // "8000"代表支付结果因为支付渠道原因或者系统原因还在等待支付结果确认，最终交易是否成功以服务端异步通知为准（小概率状态）
                        if (TextUtils.equals(resultStatus, "8000")) {
                            Toast.makeText(OrderPaymentActivity.this, "支付结果确认中", Toast.LENGTH_SHORT).show();

                        } else {
                            // 其他值就可以判断为支付失败，包括用户主动取消支付，或者系统返回的错误
                            customDialogWithTwoDiffButton = CustomDialogWithTwoDiffButton.newInstance("支付失败", R.drawable.icon_pay_err, 0, false);
                            customDialogWithTwoDiffButton.setOnTwoButtonClick(new ITwoButtonClick() {
                                @Override
                                public void onLeftButtonClick() {

                                }

                                @Override
                                public void onRightButtonClick() {
                                    MainActivity.Instance().goOrder = true;
                                    ModulationActivity.Instance().finish();
                                    NewCartActivity.Instance().finish();

                                    OrderItemDetailActivity.Instance().finish();
                                    isPayFailure=true;
                                    customDialogWithTwoDiffButton.dismiss();
                                    //finish();
                                }
                            });
                            //这里直接调用show方法会报java.lang.IllegalStateException: Can not perform this action after onSaveInstanceState
                            //因为show方法中是通过commit进行的提交(通过查看源码)
                            //这里为了修复这个问题，使用commitAllowingStateLoss()方法
                            //注意：DialogFragment是继承自android.app.Fragment，这里要注意同v4包中的Fragment区分，别调用串了
                            //DialogFragment有自己的好处，可能也会带来别的问题
                            //dialog.show(getFragmentManager(), "SignDialog");
                            FragmentManager fm = getSupportFragmentManager();
                            FragmentTransaction ft = fm.beginTransaction();
                            ft.add(customDialogWithTwoDiffButton,"customDialog");
                            ft.commitAllowingStateLoss();
                        }
                        isPaying=false;
                    }
                    break;
                }

                case WECHAT_PAY_SUCCESS:
                    goSuccessOrder((int) mOrder.getGet_point());
                    isPaying=false;

                    break;
                case WECHAT_PAY_FAILED:

                    customDialogWithTwoDiffButton = CustomDialogWithTwoDiffButton.newInstance("支付失败", R.drawable.icon_pay_err, 0, false);
                    customDialogWithTwoDiffButton.setOnTwoButtonClick(new ITwoButtonClick() {
                        @Override
                        public void onLeftButtonClick() {

                        }

                        @Override
                        public void onRightButtonClick() {
                            MainActivity.Instance().goOrder = true;
                            ModulationActivity.Instance().finish();
                            NewCartActivity.Instance().finish();

                            OrderItemDetailActivity.Instance().finish();
                           // finish();
                            isPayFailure=true;
                            customDialogWithTwoDiffButton.dismiss();
                        }
                    });
                    customDialogWithTwoDiffButton.show(getSupportFragmentManager(), "FullPointPay");
                    isPaying=false;

                    break;
                case WECHAT_PAY_CANCEL:
                    customDialogWithTwoDiffButton = CustomDialogWithTwoDiffButton.newInstance("支付失败", R.drawable.icon_pay_err, "用户中途取消");
                    customDialogWithTwoDiffButton.setOnTwoButtonClick(new ITwoButtonClick() {
                        @Override
                        public void onLeftButtonClick() {

                        }

                        @Override
                        public void onRightButtonClick() {
                            MainActivity.Instance().goOrder = true;
                            ModulationActivity.Instance().finish();
                            NewCartActivity.Instance().finish();

                            OrderItemDetailActivity.Instance().finish();
                            //finish();
                            isPayFailure=true;
                            customDialogWithTwoDiffButton.dismiss();
                        }
                    });
                    customDialogWithTwoDiffButton.show(getSupportFragmentManager(), "FullPointPay");
                    isPaying=false;
                    break;
                default:
                    break;
            }
        }

        ;
    };
    private void goSuccessOrder(int point){
        Intent intent = new Intent(this,SuccessOrderActivity.class);
        intent.putExtra("point",point);
        startActivity(intent);
        MainActivity.Instance().goOrder = true;
        ModulationActivity.Instance().finish();
        NewCartActivity.Instance().finish();

        OrderItemDetailActivity.Instance().finish();
        finish();
    }

    /**
     * call alipay sdk pay. 调用SDK支付
     */
    public void pay() {

        String orderInfo = getOrderInfo(subject, body, total_fee);

        /**
         * 特别注意，这里的签名逻辑需要放在服务端，切勿将私钥泄露在代码中！
         */
        String sign = sign(orderInfo);
        try {
            /**
             * 仅需对sign 做URL编码
             */
            sign = URLEncoder.encode(sign, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        /**
         * 完整的符合支付宝参数规范的订单信息
         */
        final String payInfo = orderInfo + "&sign=\"" + sign + "\"&" + getSignType();

        Runnable payRunnable = new Runnable() {

            @Override
            public void run() {
                // 构造PayTask 对象
                PayTask alipay = new PayTask(OrderPaymentActivity.this);
                // 调用支付接口，获取支付结果
                String result = alipay.pay(payInfo, true);

                Message msg = new Message();
                msg.what = SDK_PAY_FLAG;
                msg.obj = result;
                mHandler.sendMessage(msg);
            }
        };

        // 必须异步调用
        Thread payThread = new Thread(payRunnable);
        payThread.start();
    }

    /**
     * get the sdk version. 获取SDK版本号
     */
    public void getSDKVersion() {
        PayTask payTask = new PayTask(this);
        String version = payTask.getVersion();
        Toast.makeText(this, version, Toast.LENGTH_SHORT).show();
    }


    /**
     * create the order info. 创建订单信息
     */
    private String getOrderInfo(String subject, String body, String price) {

        // 签约合作者身份ID
        String orderInfo = "partner=" + "\"" + PARTNER + "\"";

        // 签约卖家支付宝账号
        orderInfo += "&seller_id=" + "\"" + SELLER + "\"";

        // 商户网站唯一订单号
        orderInfo += "&out_trade_no=" + "\"" + getOutTradeNo() + "\"";

        // 商品名称
        orderInfo += "&subject=" + "\"" + subject + "\"";

        // 商品详情
        orderInfo += "&body=" + "\"" + body + "\"";

        // 商品金额
        orderInfo += "&total_fee=" + "\"" + price + "\"";

        // 服务器异步通知页面路径
        orderInfo += "&notify_url=" + "\"" + notify_url + "\"";

        // 服务接口名称， 固定值
        orderInfo += "&service=\"mobile.securitypay.pay\"";

        // 支付类型， 固定值
        orderInfo += "&payment_type=\"1\"";

        // 参数编码， 固定值
        orderInfo += "&_input_charset=\"utf-8\"";

        // 设置未付款交易的超时时间
        // 默认30分钟，一旦超时，该笔交易就会自动被关闭。
        // 取值范围：1m～15d。
        // m-分钟，h-小时，d-天，1c-当天（无论交易何时创建，都在0点关闭）。
        // 该参数数值不接受小数点，如1.5h，可转换为90m。
        orderInfo += "&it_b_pay=\"30m\"";

        // extern_token为经过快登授权获取到的alipay_open_id,带上此参数用户将使用授权的账户进行支付
        // orderInfo += "&extern_token=" + "\"" + extern_token + "\"";

        // 支付宝处理完请求后，当前页面跳转到商户指定页面的路径，可空
        orderInfo += "&return_url=\"m.alipay.com\"";

        // 调用银行卡支付，需配置此参数，参与签名， 固定值 （需要签约《无线银行卡快捷支付》才能使用）
        // orderInfo += "&paymethod=\"expressGateway\"";

        return orderInfo;
    }

    /**
     * get the out_trade_no for an order. 生成商户订单号，该值在商户端应保持唯一（可自定义格式规范）
     */
    private String getOutTradeNo() {
/*		SimpleDateFormat format = new SimpleDateFormat("MMddHHmmss", Locale.getDefault());
        Date date = new Date();
		String key = format.format(date);

		Random r = new Random();
		key = key + r.nextInt();
		key = key.substring(0, 15);*/
        return orderid;
    }

    /**
     * sign the order info. 对订单信息进行签名
     *
     * @param content 待签名订单信息
     */
    private String sign(String content) {
        return SignUtils.sign(content, RSA_PRIVATE);
    }

    /**
     * get the sign type we use. 获取签名方式
     */
    private String getSignType() {
        return "sign_type=\"RSA\"";
    }

    private void parsepayOrderResult(String result) {

        try {
            JSONObject jsonObject = new JSONObject(result);

            checkResOld(jsonObject);
            if (jsonObject.getInt(Configurations.STATUSCODE) == 200) {

                Map<String, String> map = new HashMap<>();
                map.put(UmengConfig.PARAM_PAYMETHOD, UmengConfig.PARAM_BALANCEPAY);
                MobclickAgent.onEventValue(OrderPaymentActivity.this, UmengConfig.EVENT_PAYORDER_BALANCEPAY, map, (int) realPay);

                User user = JSON.parseObject(jsonObject.getJSONObject("results").getString("user"), User.class);
                UserUtils.getUserInfo().setBalance(user.getBalance());
                goSuccessOrder((int) mOrder.getGet_point());
                isPaying=false;

            } else {
                ToastUtil.showShort(OrderPaymentActivity.this, jsonObject.getString(Configurations.STATUSMSG));
                checkResOld(jsonObject);
                isPaying=false;
//                customDialogWithTwoDiffButton = CustomDialogWithTwoDiffButton.newInstance("支付失败", R.drawable.icon_pay_err, 0, false);
//                customDialogWithTwoDiffButton.setOnTwoButtonClick(new ITwoButtonClick() {
//                    @Override
//                    public void onLeftButtonClick() {
//
//                    }
//
//                    @Override
//                    public void onRightButtonClick() {
//                        MainActivity.Instance().goOrder = true;
//                        ModulationActivity.Instance().finish();
//                        NewCartActivity.Instance().finish();
//                        NewMyCoffeesActivity.Instance().finish();
//                        finish();
//                    }
//                });
//                customDialogWithTwoDiffButton.show(getSupportFragmentManager(), "FullPointPay");

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    //替换为一直显示的支付方式选择按钮
    @Event(R.id.rl_coffeeMePay)
    private void onCoffeeMePayClick(View v){
        payMethod=0;
        if (AppUtil.isFastClick())
            return;
        if(isPaying){
            return;
        }
        isPaying=true;
        if (buytype == 3 && null != mOrder) {
            identifier = mOrder.getIdentifier();
            payOrder();
        }
    }
    @Event(R.id.rl_weChatPay)
    private void onWeChatPay(View v){
        payMethod=2;
        if (AppUtil.isFastClick())
            return;
        if(isPaying){
            return;
        }
        isPaying=true;
        if (buytype == 3 && null != mOrder) {
            identifier = mOrder.getIdentifier();
            payOrder();
        }
    }
    @Event(R.id.rl_aliPay)
    private void onAliPay(View v){
        payMethod=1;
        if (AppUtil.isFastClick())
            return;
        if(isPaying){
            return;
        }
        isPaying=true;
        if (buytype == 3 && null != mOrder) {
            identifier = mOrder.getIdentifier();
            payOrder();
        }
    }

   // private SelectDialog payMethodDialog ;

   /* @Event(R.id.ivPaymentMore)
    private void onPaymentMoreClick(View v) {

        getCoffeeMePockectBalance();

    }*/
    //获取coffeeme钱包余额
    private String getCoffeeMePockectBalance() {
        RequestParams params = new RequestParams(Configurations.URL_CHECK_TOKEN);
        params.addParameter(Configurations.AUTH_TOKEN, UserUtils.getUserInfo().getAuth_token());

        String timeStamp= TimeUtil.getCurrentTimeString();
        String device_id= JPushInterface.getRegistrationID(OrderPaymentActivity.this);
        params.addParameter(Configurations.TIMESTAMP, timeStamp);
        params.addParameter(Configurations.DEVICE_ID,device_id );

        Map<String,String> map=new TreeMap<>();
        map.put(Configurations.AUTH_TOKEN, UserUtils.getUserInfo().getAuth_token());
        params.addParameter(Configurations.SIGN, com.hzjytech.coffeeme.utils.SignUtils.createSignString(device_id,timeStamp,map));

        x.http().get(params, new Callback.CommonCallback<JSONObject>() {
            @Override
            public void onSuccess(JSONObject result) {
                try {
                    if (result.getInt(Configurations.STATUSCODE) == 200) {
                       // payMethodDialog = SelectDialog.newInstance(R.layout.dialog_select,result.getJSONObject("results").getJSONObject("user").getString("balance"));
                        coffeeMePocketBalance = result.getJSONObject("results").getJSONObject("user").getString("balance");
                    }else{
                        //payMethodDialog = SelectDialog.newInstance(R.layout.dialog_select,"0.0");
                        coffeeMePocketBalance="0.0";
                    }
                   /* payMethodDialog.setListener(new SelectDialog.SelectDialogListener() {
                        @Override
                        public void onSelectDialogListener(int which) {
                            ivPaymentIcon.setImageResource(SelectDialog.icons[which]);
                            tvPaymentMethodName.setText(SelectDialog.titles[which]);
                            payMethod = which;
                            payMethodDialog.dismiss();
                        }
                    });

                    payMethodDialog.setWrappableGridLayoutManager(new GridLayoutManager(OrderPaymentActivity.this, 1));
                    payMethodDialog.show(getSupportFragmentManager(), "selectDialog");*/
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
        return  coffeeMePocketBalance;
    }

    private DisplayItems mDisplayItems;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        tbPaymentTitle.setTitle("支付");
        tbPaymentTitle.setTitleColor(Color.WHITE);
        tbPaymentTitle.setLeftImageResource(R.drawable.icon_left);
        tbPaymentTitle.setLeftClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OrderPaymentActivity.this.finish();
            }
        });

        llPaymentPay.setVisibility(View.GONE);
        llPaymentPayInit.setVisibility(View.VISIBLE);
        mLlOrderPoint.setVisibility(View.GONE);
        mLineView.setVisibility(View.GONE);
        initView();


    }

    private void initView() {

        if (getIntent() != null) {
            rcyViewPaymentDetail.setAdapter(adapter);
            rcyViewPaymentDetail.setLayoutManager(new LinearLayoutManager(OrderPaymentActivity.this));

            buytype = getIntent().getIntExtra("type", 0);

            if (buytype == 3) {

                mOrder = (NewOrder) getIntent().getSerializableExtra("order");
                goods = mOrder.getGoods();
                adapter.setData(goods);
                count = mOrder.getGoods()
                        .size();
                realPay = Float.parseFloat("" + mOrder.getSum());
                pay = realPay;

            }
            RowViewDesc mRowViewDesc = new RowViewDesc("优惠券：", mOrder.getCoupon_info(), 0);
            if (buytype == 3 && null != mOrder) {
                rvPaymentamountAmount.setData(new RowViewDesc("总价",
                        "￥" + fnum.format(mOrder.getOriginal_sum()),
                        0));
            }
            rvPaymentamountCoupon.setData(mRowViewDesc);

            if (buytype == 3 && null != mOrder)
                tvPaymentamountRealpay.setText(String.valueOf(fnum.format(mOrder.getSum())));

            if (mOrder.getSum() > 0.00f) {
                llPaymentPayInit.setVisibility(View.VISIBLE);
                llPaymentPay.setVisibility(View.GONE);
            } else {
                llPaymentPayInit.setVisibility(View.GONE);
                llPaymentPay.setVisibility(View.VISIBLE);
            }

            tvPaymentamountRatepoint.setText(getString(R.string.single_string,
                    mOrder.getPoint_count() + ""));
            tvPaymentamountRatepointMoney.setText(getString(R.string.single_string,
                    mOrder.getPoint_value() + ""));

        } else {
            finish();
        }

    }

    class PaymentDetailAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {


        private List<NewGood> mData;

        public void setData(List<NewGood> data) {
            mData = data;
            notifyDataSetChanged();
        }

        class MViewHolder1 extends RecyclerView.ViewHolder {
            private  OrderGroup mOrderGroup;


            public MViewHolder1(View itemView) {
                super(itemView);
                mOrderGroup = (OrderGroup) itemView.findViewById(R.id.ogItemPay);
            }
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view1 = LayoutInflater.from(parent.getContext()).inflate(R.layout.payment_item, parent, false);
            return new MViewHolder1(view1);

        }


        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

            MViewHolder1 mViewHolder1 = (MViewHolder1) holder;


            if (buytype == 3) {
               mViewHolder1.mOrderGroup.setItemClickable(false);
               mViewHolder1.mOrderGroup.setData(mData);
            }

        }

        @Override
        public int getItemCount() {
            return mData==null?0:1;
        }
    }

}