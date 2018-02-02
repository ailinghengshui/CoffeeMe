package com.hzjytech.coffeeme.order;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
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
import com.hzjytech.coffeeme.entities.NewOrder;
import com.hzjytech.coffeeme.entities.PackageOrder;
import com.hzjytech.coffeeme.entities.User;
import com.hzjytech.coffeeme.entities.preWxPayInfo;
import com.hzjytech.coffeeme.home.NewCartActivity;
import com.hzjytech.coffeeme.home.PackageModulationActivity;
import com.hzjytech.coffeeme.home.SuccessPackageOrderActivity;
import com.hzjytech.coffeeme.http.JijiaHttpSubscriber;
import com.hzjytech.coffeeme.http.SubscriberOnCompletedListener;
import com.hzjytech.coffeeme.http.SubscriberOnErrorListener;
import com.hzjytech.coffeeme.http.SubscriberOnNextListener;
import com.hzjytech.coffeeme.http.api.OrderApi;
import com.hzjytech.coffeeme.me.NewRechargeActivity;
import com.hzjytech.coffeeme.pays.alipay.PayResult;
import com.hzjytech.coffeeme.pays.alipay.SignUtils;
import com.hzjytech.coffeeme.utils.AppUtil;
import com.hzjytech.coffeeme.utils.DateTimeUtil;
import com.hzjytech.coffeeme.utils.DateUtil;
import com.hzjytech.coffeeme.utils.LogUtil;
import com.hzjytech.coffeeme.utils.NetUtil;
import com.hzjytech.coffeeme.utils.SharedPrefUtil;
import com.hzjytech.coffeeme.utils.StringJointUtil;
import com.hzjytech.coffeeme.utils.StringUtil;
import com.hzjytech.coffeeme.utils.TimeUtil;
import com.hzjytech.coffeeme.utils.ToastUtil;
import com.hzjytech.coffeeme.utils.UserUtils;
import com.hzjytech.coffeeme.widgets.PayMethodPopWindow;
import com.hzjytech.coffeeme.widgets.TitleBar;
import com.hzjytech.coffeeme.wxapi.WXPayEntryActivity;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.HttpMethod;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.jpush.android.api.JPushInterface;
import rx.Observable;

import static com.hzjytech.coffeeme.home.PackageModulationActivity.ORDER_FLAG;
import static com.hzjytech.coffeeme.home.PackageModulationActivity.PACKAGE_IMAGE;
import static com.hzjytech.coffeeme.home.SuccessPackageOrderActivity.IDENTIFERID;
import static com.hzjytech.coffeeme.order.OrderItemFragment.IDENTIFIER;
import static com.hzjytech.coffeeme.widgets.PayMethodPopWindow.payMethod.ALIPAY;
import static com.hzjytech.coffeeme.widgets.PayMethodPopWindow.payMethod.COFFEEMEPAY;
import static com.hzjytech.coffeeme.widgets.PayMethodPopWindow.payMethod.WXPAY;

/**
 * Created by hehongcan on 2018/1/8.
 */

public class DetailPackageOrderActivity extends BaseActivity {
    private static final int WECHAT_PAY_SUCCSS = 0x100001;
    private static final int WECHAT_PAY_FAILED = 0x100002;
    private static final int WECHAT_PAY_CANCEL = 0x100003;
    public static final String REDEEM_LIST = "redeem_list";
    public static final String PAY_STATUS = "pay_status";
    public static final String ORDER_BUY = "order_buy";
    private static final int MES_COFFEE_ME_PAY_SUCCESS = 0x100004;
    private static DetailPackageOrderActivity mInstance;
    @BindView(R.id.titleBar)
    TitleBar mTitleBar;
    @BindView(R.id.btnOpeContainer1)
    Button mBtnOpeContainer1;
    @BindView(R.id.btnOpe1)
    Button mBtnOpe1;
    @BindView(R.id.btnOpe2)
    Button mBtnOpe2;
    @BindView(R.id.btnOpeContainer2)
    LinearLayout mBtnOpeContainer2;
    @BindView(R.id.iv_detial_package)
    ImageView mIvDetialPackage;
    @BindView(R.id.tvDetailPackageOriginalsum)
    TextView mTvDetailPackageOriginalsum;
    @BindView(R.id.tvDetailPackageRealSum)
    TextView mTvDetailPackageRealSum;
    @BindView(R.id.tvDetailPackageId)
    TextView mTvDetailPackageId;
    @BindView(R.id.tvDetailPackageDate)
    TextView mTvDetailPackageDate;
    @BindView(R.id.tvOrderDetailPackagePayprovider)
    TextView mTvOrderDetailPackagePayprovider;
    @BindView(R.id.tvOrderDetailPackageStatus)
    TextView mTvOrderDetailPackageStatus;
    @BindView(R.id.view_anchor)
    View mAnchor;
    private String identifier;
    private JijiaHttpSubscriber mSubscriber;
    private NewOrder mOrder;
    private PayMethodPopWindow plusPopWindow;
    private boolean isPaying = false;
    // 商户PID
    public static final String PARTNER = "2088911832538297";
    // 商户收款账号
    public static final String SELLER = "jykj@hzjytech.com";
    // 商户私钥，pkcs8格式
    public static final String RSA_PRIVATE = "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAL"
            + "/aKG8LlTUMAaatZAyN6Ik9J63jBSZYcYic9GLpGSkGCGQy1viNcJDqpQCPirek78c7jJnwbEI+y6" +
            "/lPupaHbVXa5eNM49vkAtcUfcVup38KrTxvuzHjq8/iXH96KaRoy" +
            "/5mxDanHAJtHpRtCvAxy1YXnZQWuSusf1V" + "/QlhpxWNAgMBAAECgYBf/TD5zlvK" +
            "/MTetiY2udG8yKJPuCRdeSbk6VLFIkd6ZRBRwbYZCftmJmCEFU2ccKOflUXVx5sC0hRrX1HJQ2lU8TIsIyGgSmXKefyK" + "+87BvZsww1ezVkZUKZyS0hIUSF2b+IRdJ1U0IoQErWh3o3tbkfIqMVlcLaxuVXxzMOVpwQJBAPOxAqohdTbeGGI6clHaxc8" + "+pqr6LOS2QQ2dtpDV/TkfHNW98uU7DxFAjmDbDN5/6j7mDNNQfDpuwquvM1CMicUCQQDJitpsgvP7OFogztNRq" + "/39sk92f55ophn4hCBeb2C0HIxCyyG1V6u0K1oHEwLXMyxywZih6Pclq4S5vZ2AIkEpAkBFb+CkfZgapDoqcyDz9fR7UwzGlzaHjO8IZDILHw5iYl0jAlbdvnBSqCRpsMD" + "//U7P0VlCNSLQu5K4vNlQUFtJAkEAqGzRfD0zh43gQ+qTznzdFVYd8flUeD" + "/XFEfWhRSuy9XPoUIAOXu6kiZoZeENTqoFSyFeXHBDmcdsc4xyQtJdgQJATVHNq79TAXE8u2C9" + "RqGKH41ZrrCxoEsTwVmeOu9lZmlf7ZLngLsiAVEvezeAdTL6xRUUX7DjQkyjKLnvCpV82g==";
    // 支付宝公钥
    public static final String RSA_PUBLIC = "";
    private static final int SDK_PAY_FLAG = 1;
    private CustomDialogWithTwoDiffButton customDialogWithTwoDiffButton;
    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @SuppressWarnings("unused")
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MES_COFFEE_ME_PAY_SUCCESS:
                  goSuccessOrder();
                    break;
                case SDK_PAY_FLAG: {
                    hideLoading();
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
                        goSuccessOrder();
                        isPaying = false;
                    } else {
                        // 判断resultStatus 为非"9000"则代表可能支付失败
                        // "8000"代表支付结果因为支付渠道原因或者系统原因还在等待支付结果确认，最终交易是否成功以服务端异步通知为准（小概率状态）
                        if (TextUtils.equals(resultStatus, "8000")) {
                            Toast.makeText(DetailPackageOrderActivity.this,
                                    "支付结果确认中",
                                    Toast.LENGTH_SHORT)
                                    .show();

                        } else {
                            // 其他值就可以判断为支付失败，包括用户主动取消支付，或者系统返回的错误
                            // Toast.makeText(NewPaymentActivity.this, "支付失败", Toast
                            // .LENGTH_SHORT).show();

                            customDialogWithTwoDiffButton = CustomDialogWithTwoDiffButton
                                    .newInstance(
                                    "支付失败",
                                    R.drawable.icon_pay_err,
                                    0,
                                    false);
                            customDialogWithTwoDiffButton.setOnTwoButtonClick(new ITwoButtonClick
                                    () {
                                @Override
                                public void onLeftButtonClick() {

                                }

                                @Override
                                public void onRightButtonClick() {
                                    MainActivity.Instance().goOrder = true;
                                    PackageModulationActivity.Instance()
                                            .finish();
                                    NewCartActivity.Instance()
                                            .finish();

                                    OrderItemDetailActivity.Instance()
                                            .finish();
                                    customDialogWithTwoDiffButton.dismiss();
                                    // finish();
                                }
                            });
                            //这里直接调用show方法会报java.lang.IllegalStateException: Can not perform this
                            // action after onSaveInstanceState
                            //因为show方法中是通过commit进行的提交(通过查看源码)
                            //这里为了修复这个问题，使用commitAllowingStateLoss()方法
                            //注意：DialogFragment是继承自android.app.Fragment，这里要注意同v4包中的Fragment区分，别调用串了
                            //DialogFragment有自己的好处，可能也会带来别的问题
                            //dialog.show(getFragmentManager(), "SignDialog");
                            FragmentManager fm = getSupportFragmentManager();
                            FragmentTransaction ft = fm.beginTransaction();
                            ft.add(customDialogWithTwoDiffButton, "customDialog");
                            ft.commitAllowingStateLoss();
                        }
                        isPaying = false;
                    }
                    break;
                }
                case WECHAT_PAY_SUCCSS:
                    hideLoading();
                    goSuccessOrder();
                    isPaying = false;
                    break;

                case WECHAT_PAY_FAILED:
                    hideLoading();
                    customDialogWithTwoDiffButton = CustomDialogWithTwoDiffButton.newInstance
                            ("支付失败",
                            R.drawable.icon_pay_err,
                            0,
                            false);
                    customDialogWithTwoDiffButton.setOnTwoButtonClick(new ITwoButtonClick() {
                        @Override
                        public void onLeftButtonClick() {

                        }

                        @Override
                        public void onRightButtonClick() {
                            MainActivity.Instance().goOrder = true;
                            PackageModulationActivity.Instance()
                                    .finish();
                            NewCartActivity.Instance()
                                    .finish();

                            OrderItemDetailActivity.Instance()
                                    .finish();
                            customDialogWithTwoDiffButton.dismiss();
                            // finish();
                        }
                    });
                    FragmentManager fm = getSupportFragmentManager();
                    FragmentTransaction ft = fm.beginTransaction();
                    ft.add(customDialogWithTwoDiffButton, "customDialog");
                    ft.commitAllowingStateLoss();
                    isPaying = false;

                    break;
                case WECHAT_PAY_CANCEL:
                    hideLoading();
                    customDialogWithTwoDiffButton = CustomDialogWithTwoDiffButton.newInstance
                            ("支付失败",
                            R.drawable.icon_pay_err,
                            "用户中途取消");
                    customDialogWithTwoDiffButton.setOnTwoButtonClick(new ITwoButtonClick() {
                        @Override
                        public void onLeftButtonClick() {

                        }

                        @Override
                        public void onRightButtonClick() {
                            MainActivity.Instance().goOrder = true;
                            PackageModulationActivity.Instance()
                                    .finish();
                            NewCartActivity.Instance()
                                    .finish();

                            OrderItemDetailActivity.Instance()
                                    .finish();
                            customDialogWithTwoDiffButton.dismiss();
                            // finish();
                        }
                    });
                    FragmentManager fm2 = getSupportFragmentManager();
                    FragmentTransaction ft2 = fm2.beginTransaction();
                    ft2.add(customDialogWithTwoDiffButton, "customDialog");
                    ft2.commitAllowingStateLoss();
                    isPaying = false;
                    break;

                default:
                    break;
            }
        }

        ;
    };
    private String subject;
    private String body;
    private String notify_url;
    private String orderid;
    private preWxPayInfo preWxInfo;

    public static DetailPackageOrderActivity Instance() {
        if (null == mInstance)
            mInstance = new DetailPackageOrderActivity();
        return mInstance;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_package_order);
        ButterKnife.bind(this);
        mInstance = this;
        initData();
        initView(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadData();
        MobclickAgent.onPageStart(UmengConfig.ORDERITEMDETAILACTIVITY);
        MobclickAgent.onResume(this);
    }

    private void initView(Bundle savedInstanceState) {
        mTitleBar.setTitle("订单详情");
        mTitleBar.setTitleColor(Color.WHITE);
        mTitleBar.setLeftImageResource(R.drawable.icon_left);
        mTitleBar.setLeftClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mBtnOpeContainer1.setVisibility(View.GONE);

    }

    private void initData() {
        if (getIntent() != null) {
            identifier = getIntent().getStringExtra(IDENTIFIER);
            showLoading();
        }
    }


    private void loadData() {
        Observable<NewOrder> observable = OrderApi.getOrderDetail(this,
                UserUtils.getUserInfo()
                        .getAuth_token(),
                identifier);
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
                         */
                        if (mOrder == null) {
                            return;
                        }
                        setCommonViews();
                        switch (mOrder.getStatus()) {
                            case 0:
                                setUnPayViews();
                                break;
                            case 1:
                                setSuccessPayViews();
                                break;

                        }
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

    /**
     * 设置相同的views
     */
    private void setCommonViews() {
        DecimalFormat fnum = new DecimalFormat("##0.00");
        DisplayImageOptions options = new DisplayImageOptions.Builder().cacheInMemory(true)/*缓存至内存*/
                .cacheOnDisk(true)/*缓存值SDcard*/
                .bitmapConfig(Bitmap.Config.RGB_565)
                .showImageForEmptyUri(R.drawable.package_empty)
                .showImageOnLoading(R.drawable.package_empty)
                .showImageOnFail(R.drawable.package_empty)
                .build();
        String img_url = mOrder.getPackage_info()
                .getImg_url();
        String str = img_url.contains("?") ? img_url.substring(0, img_url.indexOf("?")) : img_url;
        ImageLoader.getInstance()
                .displayImage(str, mIvDetialPackage, options);
        mTvDetailPackageId.setText(mOrder.getIdentifier() + "");

        mTvDetailPackageDate.setText(DateTimeUtil.longToShort9(mOrder.getCreated_at()));
        mTvDetailPackageOriginalsum.setText(StringJointUtil.obj2String(fnum.format(mOrder
                        .getOriginal_sum()),
                "￥",
                true));
        mTvDetailPackageRealSum.setText(StringJointUtil.obj2String(fnum.format(mOrder.getSum()), "￥", true));
        /**
         * 1 : alipay
         * 2 :wechatpay
         * 3 :jijiapay
         */
        switch (mOrder.getPayment_provider()) {
            case 1:
                mTvOrderDetailPackagePayprovider.setText(getResources().getString(R.string
                        .PayViaAlipay));
                break;
            case 2:
                mTvOrderDetailPackagePayprovider.setText(getString(R.string.PayViaWechat));
                break;
            case 3:
                mTvOrderDetailPackagePayprovider.setText(getString(R.string.PayViaBalance));
                break;
            default:
                if (mOrder.getSum() > 0.00f) {
                    mTvOrderDetailPackagePayprovider.setText(getString(R.string.NoPay));
                } else {
                    mTvOrderDetailPackagePayprovider.setText("");
                }
                break;
        }
        switch (mOrder.getStatus()) {
            case 0:
                mTvOrderDetailPackageStatus.setText(R.string.PayCancel);
                break;
            case 1:
                mTvOrderDetailPackageStatus.setText(R.string.string_paysuccess);
                break;
        }
    }

    /**
     * 未支付
     */
    private void setUnPayViews() {
        mBtnOpeContainer2.setVisibility(View.VISIBLE);
        mBtnOpeContainer1.setVisibility(View.GONE);
        mBtnOpe1.setText(getResources().getString(R.string.check_package));
        mBtnOpe2.setText(getResources().getString(R.string.go_pay));
        mBtnOpe1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (AppUtil.isFastClick())
                    return;
                checkPackage(false);

            }
        });
        mBtnOpe2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (AppUtil.isFastClick())
                    return;
                goPay();

            }
        });
    }

    /**
     * 去支付
     */
    private void goPay() {
        if (!SharedPrefUtil.getLoginType()
                .equals(SharedPrefUtil.LOGINING)) {
            goLogin();
            return;
        }

        if (plusPopWindow != null && plusPopWindow.isShowing()) {
            plusPopWindow.dismiss();
            return;
        } else {
            if (plusPopWindow == null) {
                plusPopWindow = new PayMethodPopWindow(this);
                plusPopWindow.setPayMethodListener(new PayMethodPopWindow.IPayMethodListener() {
                    @Override
                    public void payMethod(int method) {
                        pay(method);
                    }
                });
            }

            plusPopWindow.showAsDropDown(mAnchor, 0, 0);

        }
    }

    private void pay(int method) {
        if (AppUtil.isFastClick())
            return;
        if (isPaying) {
            return;
        }
        isPaying = true;
        payOrder(method, mOrder);
    }

    //订单支付
    private void payOrder(final int method, NewOrder order) {

        RequestParams entity = new RequestParams(Configurations.URL_ORDER_PAY);
        entity.addParameter(Configurations.TOKEN,
                UserUtils.getUserInfo()
                        .getAuth_token());
        entity.addParameter(Configurations.IP, NetUtil.getIPAddress(true));
        entity.addParameter(Configurations.IDENTIFIER, order.getIdentifier());

        String device_id = JPushInterface.getRegistrationID(this);
        String timeStamp = TimeUtil.getCurrentTimeString();
        entity.addParameter(Configurations.TIMESTAMP, timeStamp);
        entity.addParameter(Configurations.DEVICE_ID, device_id);

        Map<String, String> map = new TreeMap<>();
        map.put(Configurations.TOKEN,
                UserUtils.getUserInfo()
                        .getAuth_token());
        map.put(Configurations.IP, NetUtil.getIPAddress(true));
        map.put(Configurations.IDENTIFIER, order.getIdentifier());


        switch (method) {
            //jijiapay
            case COFFEEMEPAY:
                entity.addParameter(Configurations.PAYMENT_PROVIDER, "jijiapay");
                map.put(Configurations.PAYMENT_PROVIDER, "jijiapay");
                break;
            //alipay
            case ALIPAY:
                entity.addParameter(Configurations.PAYMENT_PROVIDER, "alipay");
                map.put(Configurations.PAYMENT_PROVIDER, "alipay");
                break;
            //wxpay
            case WXPAY:
                entity.addParameter(Configurations.PAYMENT_PROVIDER, "wxpay");
                map.put(Configurations.PAYMENT_PROVIDER, "wxpay");
                break;
        }

        entity.addParameter(Configurations.SIGN,
                com.hzjytech.coffeeme.utils.SignUtils.createSignString(device_id, timeStamp, map));

        x.http()
                .request(HttpMethod.PUT, entity, new Callback.CommonCallback<String>() {
                    @Override
                    public void onSuccess(String result) {
                        LogUtil.e("result", result);
                        try {
                            JSONObject object = new JSONObject(result);
                            if (object.getInt(Configurations.STATUSCODE) == 200) {

                                if (method == COFFEEMEPAY) {

                                    parsepayOrderResult(result);
                                } else if (method == ALIPAY) {

                                    startAliPay();
                                } else if (method == WXPAY) {

                                    startWxPay(result);
                                }
                            } else {
                                hideLoading();
                                showPayError(object);

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        LogUtil.e("result", result);
                        hideLoading();
                    }

                    @Override
                    public void onError(Throwable ex, boolean isOnCallback) {
                        hideLoading();
                        showNetError();
                        isPaying = false;
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

    private void showPayError(JSONObject object) throws JSONException {
        final CustomDialogWithTwoDiffButton customDialogWithTwoDiffButton =
                CustomDialogWithTwoDiffButton.newInstance(
                "支付失败",
                R.drawable.icon_pay_err,
                object.getString(Configurations.STATUSMSG));
        customDialogWithTwoDiffButton.setOnTwoButtonClick(new ITwoButtonClick() {
            @Override
            public void onLeftButtonClick() {

            }

            @Override
            public void onRightButtonClick() {
                MainActivity.Instance().goOrder = true;
                PackageModulationActivity.Instance()
                        .finish();
                NewCartActivity.Instance()
                        .finish();

                OrderItemDetailActivity.Instance()
                        .finish();

                customDialogWithTwoDiffButton.dismiss();
                isPaying = false;
                // finish();
            }
        });
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.add(customDialogWithTwoDiffButton, "customDialog");
        ft.commitAllowingStateLoss();
    }

    /**
     * 微信支付
     *
     * @param result
     */
    private void startWxPay(String result) {

        showLoading();
        try {
            JSONObject object = new JSONObject(result);
            if (object.getInt(Configurations.STATUSCODE) == 200) {

                preWxInfo = new preWxPayInfo();
                preWxInfo = new Gson().fromJson(object.getJSONObject("results")
                        .getString("prepay"), new TypeToken<preWxPayInfo>() {}.getType());
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

        Intent intent = new Intent(this, WXPayEntryActivity.class);
        intent.putExtra("appid", appid);
        intent.putExtra("noncestr", noncestr);
        intent.putExtra("partnerid", partnerid);
        intent.putExtra("prepayid", prepayid);
        intent.putExtra("timestamp", timestamp);
        intent.putExtra("sign", sign);
        startActivityForResult(intent, NewRechargeActivity.REQUEST_WECHAT_PAY);

    }

    /**
     * 跳转套餐支付成功页面
     */
    private void goSuccessOrder() {
        Intent intent = new Intent(DetailPackageOrderActivity.this, SuccessPackageOrderActivity.class);
        intent.putExtra(ORDER_BUY, mOrder);
        intent.putExtra(PACKAGE_IMAGE,
                mOrder.getPackage_info()
                        .getImg_url());
        DetailPackageOrderActivity.this.startActivity(intent);
        MainActivity.Instance().goOrder = true;
        PackageModulationActivity.Instance()
                .finish();
        NewCartActivity.Instance()
                .finish();
        OrderItemDetailActivity.Instance()
                .finish();
        finish();
    }

    /**
     * COffeeMe支付结果
     *
     * @param result
     */
    //处理支付结果
    private void parsepayOrderResult(String result) {

        try {
            JSONObject jsonObject = new JSONObject(result);

            checkResOld(jsonObject);
            if (jsonObject.getInt(Configurations.STATUSCODE) == 200) {

                Map<String, String> map = new HashMap<>();
                map.put(UmengConfig.PARAM_PAYMETHOD, UmengConfig.PARAM_BALANCEPAY);
                User user = JSON.parseObject(jsonObject.getJSONObject("results")
                        .getString("user"), User.class);
                UserUtils.getUserInfo()
                        .setBalance(user.getBalance());
                mHandler.sendEmptyMessage(MES_COFFEE_ME_PAY_SUCCESS);
                isPaying = false;
                hideLoading();

            } else {
                ToastUtil.showShort(this, jsonObject.getString(Configurations.STATUSMSG));
                checkResOld(jsonObject);
                isPaying = false;
                hideLoading();


            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 支付宝支付
     */
    private void startAliPay() {

        DecimalFormat fnum = new DecimalFormat("##0.00");
        subject = "CoffeeMe支付";
        //total_fee = "" + fnum.format(realPay);
        body = "咖啡订单支付";
        notify_url = Configurations.URL_ALIPAYNOTIFY;
        orderid = mOrder.getIdentifier();

        if (!StringUtil.isNullOrEmpty(subject) && !StringUtil.isNullOrEmpty(String.valueOf(mOrder
                .getSum())) && !StringUtil.isNullOrEmpty(
                body) && !StringUtil.isNullOrEmpty(String.valueOf(orderid)) && !StringUtil
                .isNullOrEmpty(
                notify_url)) {
            payAli();
        }
    }

    /**
     * call alipay sdk pay. 调用SDK支付
     */
    public void payAli() {

        String orderInfo = getOrderInfo(subject, body, String.valueOf(mOrder.getSum()));

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
                PayTask alipay = new PayTask(DetailPackageOrderActivity.this);
                LogUtil.e("ali_version", alipay.getVersion());
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
        Toast.makeText(this, version, Toast.LENGTH_SHORT)
                .show();
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
        return String.valueOf(orderid);
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

    /**
     * 查看套餐
     */
    private void checkPackage(boolean isPayed) {
        Intent intent = new Intent(this, MyPackageCouponsActivity.class);
        intent.putExtra(PAY_STATUS, isPayed);
        intent.putExtra(IDENTIFERID, mOrder.getIdentifier());
        startActivity(intent);
    }

    /**
     * 已支付
     */
    private void setSuccessPayViews() {
        mBtnOpeContainer1.setVisibility(View.VISIBLE);
        mBtnOpeContainer2.setVisibility(View.GONE);
        mBtnOpeContainer1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkPackage(true);
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(UmengConfig.ORDERITEMDETAILACTIVITY);
        MobclickAgent.onPause(this);
        if(plusPopWindow!=null){
            plusPopWindow.dismiss();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == NewRechargeActivity.REQUEST_WECHAT_PAY) {

            hideLoading();
            if (resultCode == RESULT_CANCELED) {
                isPaying = false;
            }
            if (data != null) {

                Bundle b = data.getExtras();
                if (b != null) {
                    int errCode = b.getInt("errCode");
                    switch (errCode) {
                        case 0:// success
                            Map<String, String> map = new HashMap<>();
                            map.put(UmengConfig.PARAM_PAYMETHOD, UmengConfig.PARAM_WECHATPAY);

                            mHandler.sendEmptyMessage(WECHAT_PAY_SUCCSS);


                            break;
                        case -1:// failed
                            //                         //   Toast.makeText(NewPaymentActivity
                            // .this, "支付失败", Toast.LENGTH_SHORT).show();

                            isPaying = false;
                            mHandler.sendEmptyMessage(WECHAT_PAY_FAILED);
                            break;
                        case -2:// cancel
                            //ToastUtil.showShort(NewPaymentActivity.this, "取消支付");
                            mHandler.sendEmptyMessage(WECHAT_PAY_CANCEL);
                            isPaying = false;

                            //finish();
                            break;
                        default:
                            break;
                    }

                }
            }
        }
    }
}
