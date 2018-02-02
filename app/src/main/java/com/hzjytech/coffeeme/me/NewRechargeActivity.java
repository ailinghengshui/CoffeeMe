package com.hzjytech.coffeeme.me;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.alipay.sdk.app.PayTask;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hzjytech.coffeeme.BaseActivity;
import com.hzjytech.coffeeme.R;
import com.hzjytech.coffeeme.configurations.Configurations;
import com.hzjytech.coffeeme.configurations.UmengConfig;
import com.hzjytech.coffeeme.entities.Couponitem;
import com.hzjytech.coffeeme.entities.User;
import com.hzjytech.coffeeme.entities.prePayInfo;
import com.hzjytech.coffeeme.entities.preWxPayInfo;
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
import com.hzjytech.coffeeme.wxapi.WXPayEntryActivity;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import cn.jpush.android.api.JPushInterface;

@ContentView(R.layout.activity_new_recharge)
public class NewRechargeActivity extends BaseActivity {


    public static final int REQUEST_WECHAT_PAY = 101;

    @ViewInject(R.id.titleBar)
    private TitleBar tbTitle;
    @ViewInject(R.id.etRechargerMoney)
    private EditText etmoney;
    @ViewInject(R.id.rcyViewRechargerList)
    private RecyclerView reclist;

    @ViewInject(R.id.rlRechargeAlipay)
    private RelativeLayout rlRechargeAlipay;

    @ViewInject(R.id.ivRechargeAlipaySelimg)
    private ImageView ivRechargeAlipaySelimg;

    @ViewInject(R.id.ivRechargeWechatPaySelimg)
    private ImageView ivRechargeWechatPaySelimg;

    @ViewInject(R.id.rlRechargeWechatpay)
    private RelativeLayout rlRechargeWechatpay;

    @ViewInject(R.id.recharge_pay)
    private Button btnpay;
    @ViewInject(R.id.recharge_payinfo)
    private TextView payinfo;
    private RechargeRecAdapter adapter;
    private List<Couponitem> couitems = new ArrayList<Couponitem>();
    private prePayInfo preInfo;
    private preWxPayInfo preWxInfo;

    private String selmoney;
    private int paytype = 0;//0是alipay   1是wchatpay


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        InitView();
        InitData();
    }

    private void InitView() {

        tbTitle.setLeftImageResource(R.drawable.icon_me_back);
        tbTitle.setLeftClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        tbTitle.setTitle(getResources().getString(R.string.title_recharge));
        tbTitle.setTitleColor(Color.WHITE);

        btnpay.setText("确认支付:  ￥0.00");
        btnpay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (AppUtil.isFastClick())
                    return;

                String money = etmoney.getText().toString();

                if (TextUtils.isEmpty(money)) {
                    ToastUtil.showShort(NewRechargeActivity.this, "请输入充值金额");
                    return;
                }else if(Float.valueOf(money)>200){
                    ToastUtil.showShort(NewRechargeActivity.this,"充值限额200");
                    return;
                } else {
                    if (NetUtil.isNetworkAvailable(NewRechargeActivity.this, true)) {
                        getOrderInfo();
                    }

                }
            }
        });
        payinfo.setText("");
        initPayType();
        paytype = 0;
        ivRechargeAlipaySelimg.setBackgroundResource(R.drawable.iconselbg);

        rlRechargeAlipay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (AppUtil.isFastClick())
                    return;
                initPayType();
                paytype = 0;
                ivRechargeAlipaySelimg.setBackgroundResource(R.drawable.iconselbg);

            }
        });

        rlRechargeWechatpay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (AppUtil.isFastClick())
                    return;
                initPayType();
                paytype = 1;
                ivRechargeWechatPaySelimg.setBackgroundResource(R.drawable.iconselbg);

            }
        });
        btnpay.setEnabled(false);
    }

    private void initPayType() {
        ivRechargeAlipaySelimg.setBackgroundResource(R.drawable.iconunselbg);
        ivRechargeWechatPaySelimg.setBackgroundResource(R.drawable.iconunselbg);
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(UmengConfig.RECHARGEACTIVITY);
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(UmengConfig.RECHARGEACTIVITY);
        MobclickAgent.onPause(this);
    }

    private void InitData() {

        getCouponInfo();

        etmoney.addTextChangedListener(new TextWatcher() {
            String beforeStr;
            String afterStr;
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                 beforeStr= s.toString();

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                 afterStr = s.toString();
                boolean isValid = true;
                if (!TextUtils.isEmpty(afterStr)) {
                    isValid=(StringUtil.isMoney(afterStr)&&(Float.valueOf(afterStr)<=1000));
                    if (!isValid) {
                        // 用户现在输入的字符数减去之前输入的字符数，等于新增的字符数
                        int differ = afterStr.length() - beforeStr.length();
                        // 如果用户的输入不符合规范，则显示之前输入的文本
                        etmoney.setText(beforeStr);
                        // 光标移动到文本末尾
                        etmoney.setSelection(afterStr.length() - differ+1);
                    }
                }
            }

            // TODO: 2017/2/15 修改button 0.00的显示问题
            @Override
            public void afterTextChanged(Editable s) {

                if (null != btnpay){
                    LogUtil.e("pay",etmoney.getText().toString()+"pay");
                    if(etmoney.getText().toString().equals("")){
                        btnpay.setEnabled(false);
                        btnpay.setText("确认支付:  ￥" +"0.00"+"元");
                    }else{
                        btnpay.setEnabled(true);
                        DecimalFormat decimalFormat = new DecimalFormat("0.00");
                        String format = decimalFormat.format(Float.valueOf(etmoney.getText().toString()));
                        btnpay.setText("确认支付:  ￥" +format+"元");
                    }

                }
                if (null == payinfo || couitems.size() == 0 || etmoney.getText().toString().length() == 0)
                    return;

                if (Double.parseDouble(etmoney.getText().toString()) < couitems.get(0).getSum()) {
                    payinfo.setText("");
                }
                for (int i = 0; i < couitems.size(); i++) {

                    if (i == couitems.size() - 1) {
                        if (Double.parseDouble(etmoney.getText().toString()) >= couitems.get(i).getSum()) {
                            payinfo.setText("符合" + couitems.get(i).getDescription() + "活动");
                            break;
                        }
                    }
                    if ((Double.parseDouble(etmoney.getText().toString()) >= couitems.get(i).getSum())
                            && (Double.parseDouble(etmoney.getText().toString()) < couitems.get(i + 1).getSum())) {
                        payinfo.setText("符合" + couitems.get(i).getDescription() + "活动");
                    }
                }
            }
        });

    }


    private void getCouponInfo() {

        RequestParams entity = new RequestParams(Configurations.URL_COUPON);
        String auth_token = UserUtils.getUserInfo().getAuth_token();
        entity.addParameter(Configurations.AUTH_TOKEN, auth_token);

        String device_id = JPushInterface.getRegistrationID(NewRechargeActivity.this);
        String timeStamp= TimeUtil.getCurrentTimeString();
        entity.addParameter(Configurations.TIMESTAMP,timeStamp);
        entity.addParameter(Configurations.DEVICE_ID, device_id);

        Map<String, String> map = new TreeMap<String, String>();
        map.put(Configurations.AUTH_TOKEN, auth_token);
        entity.addParameter(Configurations.SIGN, com.hzjytech.coffeeme.utils.SignUtils.createSignString(device_id, timeStamp, map));

        x.http().get(entity, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                parseResult(result);
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


    private void parseResult(String result) {
        try {
            JSONObject object = new JSONObject(result);

            checkResOld(object);
            if (object.getInt(Configurations.STATUSCODE) == 200) {
                couitems = new Gson().fromJson(object.getJSONObject("results").getString("coupon_items"), new TypeToken<ArrayList<Couponitem>>() {
                }.getType());

                adapter = new RechargeRecAdapter(NewRechargeActivity.this, couitems);

                GridLayoutManager gridLayoutManager = new GridLayoutManager(NewRechargeActivity.this,2);
                reclist.setLayoutManager(gridLayoutManager);
                reclist.setAdapter(adapter);

            } else {
                ToastUtil.showShort(NewRechargeActivity.this, object.getString(Configurations.STATUSMSG));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    class RechargeRecAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private LayoutInflater mLayoutInflater;
        private Context context;
        private List<Couponitem> couponitems;
        private int pos;


        public RechargeRecAdapter(Context context, List<Couponitem> couponitems) {
            this.couponitems = couponitems;
            this.context = context;
            mLayoutInflater = LayoutInflater.from(context);
        }


        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            return new ItemUpViewHolder(mLayoutInflater.inflate(R.layout.newrecharge_item, parent, false));
        }


        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            ((ItemUpViewHolder) holder).sumltx.setText(couponitems.get(position).getSum() + "元");
            ((ItemUpViewHolder) holder).bonusltx.setText(couponitems.get(position).getDescription());
        }


        @Override
        public int getItemCount() {
            return couponitems == null ? 0 : couponitems.size();
        }


        public class ItemUpViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

            RelativeLayout reupleftlay;
            TextView sumltx;
            TextView bonusltx;

            public ItemUpViewHolder(View itemView) {
                super(itemView);

                reupleftlay = (RelativeLayout) itemView.findViewById(R.id.reupleft);
                sumltx = (TextView) itemView.findViewById(R.id.reupleft_sum);
                bonusltx = (TextView) itemView.findViewById(R.id.reupleft_bonus);
                reupleftlay.setOnClickListener(this);
            }


            @Override
            public void onClick(View v) {
                pos = getLayoutPosition();

                selmoney = couponitems.get(pos).getSum() + "";

                etmoney.setText(selmoney);
                if (null != btnpay)
                    btnpay.setText("确认支付:  ￥" + selmoney);

                for (int i = 0; i < couitems.size(); i++) {

                    if (i == couitems.size() - 1) {
                        if (Double.parseDouble(selmoney) >= couitems.get(i).getSum()) {
                            payinfo.setText("符合" + couitems.get(i).getDescription() + "活动");
                            break;
                        }
                    }
                    if ((Double.parseDouble(selmoney) >= couitems.get(i).getSum()) && (Double.parseDouble(selmoney) < couitems.get(i + 1).getSum())) {
                        payinfo.setText("符合" + couitems.get(i).getDescription() + "活动");
                    }
                }
            }

        }


    }

    private void getOrderInfo() {
        RequestParams entity = new RequestParams(Configurations.URL_PREPAY);
        String auth_token = UserUtils.getUserInfo().getAuth_token();
        Map<String, String> map = new TreeMap<String, String>();
        String ip = NetUtil.getIPAddress(true);
        entity.addParameter(Configurations.AUTH_TOKEN, auth_token);
        map.put(Configurations.AUTH_TOKEN, auth_token);
        entity.addParameter(Configurations.IP, ip);
        map.put(Configurations.IP, ip);
        if (paytype == 0) {
            entity.addParameter(Configurations.PROVIDER, "alipay");
            map.put(Configurations.PROVIDER, "alipay");
        } else {
            entity.addParameter(Configurations.PROVIDER, "wxpay");
            map.put(Configurations.PROVIDER, "wxpay");
        }
        entity.addParameter(Configurations.SUM, etmoney.getText().toString());
        map.put(Configurations.SUM, etmoney.getText().toString());

        showLoading();

        String device_id = JPushInterface.getRegistrationID(NewRechargeActivity.this);
        String timeStamp= TimeUtil.getCurrentTimeString();
        entity.addParameter(Configurations.TIMESTAMP, timeStamp);
        entity.addParameter(Configurations.DEVICE_ID, device_id);


        map.put(Configurations.AUTH_TOKEN, auth_token);
        entity.addParameter(Configurations.SIGN, com.hzjytech.coffeeme.utils.SignUtils.createSignString(device_id, timeStamp, map));


        x.http().post(entity, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {

                hideLoading();
                parsePrePayResult(result);

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


    private void parsePrePayResult(String result) {
        try {
            JSONObject object = new JSONObject(result);

            checkResOld(object);
            if (object.getInt(Configurations.STATUSCODE) == 200) {
                preInfo = new prePayInfo();
                preInfo = new Gson().fromJson(object.getJSONObject("results").getString("balance_record"), new TypeToken<prePayInfo>() {
                }.getType());
                if (paytype == 1) {
                    preWxInfo = new preWxPayInfo();
                    preWxInfo = new Gson().fromJson(object.getJSONObject("results").getString("prepay"), new TypeToken<preWxPayInfo>() {
                    }.getType());

                    startWxPay();

                } else {

                    startAliPay();
                }

            } else {
                ToastUtil.showShort(NewRechargeActivity.this, object.getString(Configurations.STATUSMSG));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private String subject = null;
    private String total_fee = null;
    private String body = null;
    private String notify_url = null;
    private String orderid = null;

    private void startAliPay() {

        subject = "CoffeeMe充值";
        total_fee = etmoney.getText().toString();
        body = "支付宝充值";
        notify_url = Configurations.URL_ALIPAYBALANCENOTIFY;
        orderid = preInfo.getIdentifier();

        if (!StringUtil.isNullOrEmpty(subject) && !StringUtil.isNullOrEmpty(total_fee) && !StringUtil.isNullOrEmpty(body)
                && !StringUtil.isNullOrEmpty(orderid)
                && !StringUtil.isNullOrEmpty(notify_url)) {
            pay();
        }
    }


    private void startWxPay() {

        String appid = preWxInfo.getAppid();
        String noncestr = preWxInfo.getNoncestr();
        String partnerid = preWxInfo.getPartnerid();
        String prepayid = preWxInfo.getPrepayid();
        String timestamp = preWxInfo.getTimestamp();
        String sign = preWxInfo.getSign();

        Intent intent = new Intent(NewRechargeActivity.this, WXPayEntryActivity.class);
        intent.putExtra("appid", appid);
        intent.putExtra("noncestr", noncestr);
        intent.putExtra("partnerid", partnerid);
        intent.putExtra("prepayid", prepayid);
        intent.putExtra("timestamp", timestamp);
        intent.putExtra("sign", sign);
        startActivityForResult(intent, REQUEST_WECHAT_PAY);

    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_WECHAT_PAY) {
            if (data != null) {
                Bundle b = data.getExtras();
                if (b != null) {
                    int errCode = b.getInt("errCode");
                    switch (errCode) {
                        case 0:// success
                            Toast.makeText(NewRechargeActivity.this, "支付成功", Toast.LENGTH_SHORT).show();

                            if (!TextUtils.isEmpty(etmoney.getText().toString())) {
                                HashMap<String, String> map = new HashMap<>();
                                map.put(UmengConfig.PARAM_PAYMETHOD, UmengConfig.PARAM_WECHATPAY);
                                MobclickAgent.onEventValue(NewRechargeActivity.this, UmengConfig.EVENT_RECHARGE_WECHATPAY,
                                        map, (int)(Double.parseDouble(etmoney.getText().toString())));
                            }

                            getOrderRet();
                            break;
                        case -1:// failed
                            Toast.makeText(NewRechargeActivity.this, "支付失败", Toast.LENGTH_SHORT).show();
                            break;
                        case -2:// cancel
                            Toast.makeText(NewRechargeActivity.this, "取消支付", Toast.LENGTH_SHORT).show();
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
            "/XFEfWhRSuy9XPoUIAOXu6kiZoZeENTqoFSyFeXHBDmcdsc4xyQtJdgQJATVHNq79TAXE8u2C9RqGKH41ZrrCxoEsTwVmeOu9lZmlf7ZLngLsiAVEvezeAdTL6xRUUX7DjQkyjKLnvCpV82g==";
    // 支付宝公钥
    public static final String RSA_PUBLIC = "";
    private static final int SDK_PAY_FLAG = 1;


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

                        if (!TextUtils.isEmpty(etmoney.getText().toString())) {
                            HashMap<String, String> map = new HashMap<>();
                            map.put(UmengConfig.PARAM_PAYMETHOD, UmengConfig.PARAM_ALIPAY);
                            MobclickAgent.onEventValue(NewRechargeActivity.this, UmengConfig.EVENT_RECHARGE_ALIPAYPAY, map, (int)Float.parseFloat(etmoney.getText().toString()));
                        }

                        getOrderRet();
                    } else {
                        // 判断resultStatus 为非"9000"则代表可能支付失败
                        // "8000"代表支付结果因为支付渠道原因或者系统原因还在等待支付结果确认，最终交易是否成功以服务端异步通知为准（小概率状态）
                        if (TextUtils.equals(resultStatus, "8000")) {
                            Toast.makeText(NewRechargeActivity.this, "支付结果确认中", Toast.LENGTH_SHORT).show();

                        } else {
                            // 其他值就可以判断为支付失败，包括用户主动取消支付，或者系统返回的错误
                            Toast.makeText(NewRechargeActivity.this, "支付失败", Toast.LENGTH_SHORT).show();

                        }
                    }

                    break;
                }
                default:
                    break;
            }
        }

        ;
    };


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
                PayTask alipay = new PayTask(NewRechargeActivity.this);
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


    private void getOrderRet() {

        String id = preInfo.getIdentifier();

        RequestParams entity = new RequestParams(Configurations.URL_PREPAY + "/" + id);
        String auth_token = UserUtils.getUserInfo().getAuth_token();
        entity.addParameter(Configurations.AUTH_TOKEN, auth_token);

        String device_id = JPushInterface.getRegistrationID(NewRechargeActivity.this);
        String timeStamp= TimeUtil.getCurrentTimeString();
        entity.addParameter(Configurations.TIMESTAMP,timeStamp);
        entity.addParameter(Configurations.DEVICE_ID, device_id);

        Map<String, String> map = new TreeMap<String, String>();
        map.put(Configurations.AUTH_TOKEN, auth_token);
        entity.addParameter(Configurations.SIGN, com.hzjytech.coffeeme.utils.SignUtils.createSignString(device_id, timeStamp, map));

        x.http().get(entity, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {

                parseOrderRetResult(result);
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


    private void parseOrderRetResult(String result) {

        try {
            JSONObject object = new JSONObject(result);

            checkResOld(object);
            if (object.getInt(Configurations.STATUSCODE) == 200) {

                Toast.makeText(NewRechargeActivity.this, "支付成功", Toast.LENGTH_SHORT).show();

//                User user = JSON.parseObject(object.getJSONObject("results").getString("user"), User.class);
//                UserUtils.saveUserInfo(user);



            } else {
                ToastUtil.showShort(NewRechargeActivity.this, object.getString(Configurations.STATUSMSG));
            }
            finish();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
