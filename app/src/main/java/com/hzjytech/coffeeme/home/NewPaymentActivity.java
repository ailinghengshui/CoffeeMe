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
import android.widget.CompoundButton;
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
import com.hzjytech.coffeeme.entities.CreatOrderBean;
import com.hzjytech.coffeeme.entities.DisplayItems;
import com.hzjytech.coffeeme.entities.Coupon;
import com.hzjytech.coffeeme.entities.NewGood;
import com.hzjytech.coffeeme.entities.NewOrder;
import com.hzjytech.coffeeme.entities.User;
import com.hzjytech.coffeeme.entities.preWxPayInfo;
import com.hzjytech.coffeeme.http.JijiaHttpSubscriber;
import com.hzjytech.coffeeme.http.SubscriberOnErrorListener;
import com.hzjytech.coffeeme.http.SubscriberOnNextListener;
import com.hzjytech.coffeeme.http.api.OrderApi;
import com.hzjytech.coffeeme.me.NewRechargeActivity;
import com.hzjytech.coffeeme.order.OrderItemDetailActivity;
import com.hzjytech.coffeeme.pays.alipay.PayResult;
import com.hzjytech.coffeeme.pays.alipay.SignUtils;
import com.hzjytech.coffeeme.utils.AppUtil;
import com.hzjytech.coffeeme.utils.CommonUtil;
import com.hzjytech.coffeeme.utils.DateTimeUtil;
import com.hzjytech.coffeeme.utils.LogUtil;
import com.hzjytech.coffeeme.utils.MyMath;
import com.hzjytech.coffeeme.utils.NetUtil;
import com.hzjytech.coffeeme.utils.StringUtil;
import com.hzjytech.coffeeme.utils.TimeUtil;
import com.hzjytech.coffeeme.utils.ToastUtil;
import com.hzjytech.coffeeme.utils.UserUtils;
import com.hzjytech.coffeeme.widgets.TitleBar;
import com.hzjytech.coffeeme.widgets.orderview.OrderGroup;
import com.hzjytech.coffeeme.widgets.row.RowView;
import com.hzjytech.coffeeme.widgets.row.RowViewDesc;
import com.hzjytech.coffeeme.widgets.switchbutton.SwitchButton;
import com.hzjytech.coffeeme.wxapi.WXPayEntryActivity;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONArray;
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
import java.util.TreeMap;
import java.util.List;
import java.util.Map;

import cn.jpush.android.api.JPushInterface;
import rx.Observable;

//支付界面
@ContentView(R.layout.activity_new_payment)
public class NewPaymentActivity extends BaseActivity {

    private static final int PAYMENT_REQUEST_CODE = 1;
    private static final String TAG = NewPaymentActivity.class.getSimpleName();
    private static final int WECHAT_PAY_SUCCSS = 0x100001;
    private static final int WECHAT_PAY_FAILED = 0x100002;
    private static final int WECHAT_PAY_CANCEL = 0x100003;
    //优惠券类型
    private static final int COUPON_TYPE_EXCHANGE=4;

    //下单类型
    //   0   'buy_now'为立即购买类型，  1  'cart_buy’ 为购物车结算,    2  'order_again’,为再来一单类型    3   待支付订单
    private static final int BUY_NOW=0;
    private static final int CART_BUY=1;
    private static final int ORDER_AGAIN=2;
    private static final int ORDER_TO_BUY=3;
    private DecimalFormat fnum = new DecimalFormat("##0.00");
    float realPay = 0.00f;
    float pay = 0.00f;
    private RowViewDesc mRowViewDesc = new RowViewDesc("优惠券：", "选择优惠券", R.drawable.icon_right, new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (buytype == ORDER_TO_BUY)
                return;
            Intent intent = new Intent(NewPaymentActivity.this, CouponChooseActivity.class);
            if (buytype == BUY_NOW) {
                intent.putExtra("currentPrice", count * appItem.getCurrent_price());
            } else if (buytype ==CART_BUY || buytype == ORDER_AGAIN) {
                intent.putExtra("currentPrice", pay);
            }
            intent.putExtra("couponId", couponId);
            MobclickAgent.onEvent(NewPaymentActivity.this, UmengConfig.EVENT_COUPON_CHOOSE);
            startActivityForResult(intent, PAYMENT_REQUEST_CODE);
        }
    });

    PaymentDetailAdapter adapter = new PaymentDetailAdapter();

    @ViewInject(R.id.titleBar)
    private TitleBar tbPaymentTitle;

    @ViewInject(R.id.rcyViewPaymentDetail)
    private RecyclerView rcyViewPaymentDetail;

    private float orderRate = 0.0f;
    private boolean isFullPointPay = false;
    private CustomDialogWithTwoDiffButton customDialogWithTwoDiffButton;
    private int get_point;
    private int point;
    private float coffeeMeBalance;



    @ViewInject(R.id.rvPaymentamountAmount)
    private RowView rvPaymentamountAmount;

    @ViewInject(R.id.rvPaymentamountCoupon)
    private RowView rvPaymentamountCoupon;

    @ViewInject(R.id.tvPaymentamountRatepoint)
    private TextView tvPaymentamountRatepoint;

    @ViewInject(R.id.tvPaymentamountRatepointMoney)
    private TextView tvPaymentamountRatepointMoney;

    @ViewInject(R.id.switchButtonPayamount)
    private SwitchButton switchButtonPayamount;

    @ViewInject(R.id.tvPaymentamountRealpay)
    private TextView tvPaymentamountRealpay;

    @ViewInject(R.id.llPaymentPayInit)
    private LinearLayout llPaymentPayInit;

    @ViewInject(R.id.llPaymentPay)
    private LinearLayout llPaymentPay;
    @ViewInject(R.id.ll_ex_point)
    private LinearLayout mLlExPoint;
    @ViewInject(R.id.tv_point_line)
    private View mTvPointLine;
    //购买数量
    private int count;

    /**
     * define pay method
     * 0 for jijia`
     * 1 for apipay
     * 2 for wxpay
     */
    private int payMethod = 0;
    private List<NewGood> goods;
    private Map<NewGood, Integer> selectGood = new HashMap<>();
    //coupon id
    private int couponId = -1;
    private String identifier;
    private int id;

    private NewOrder mOrder;
    //服务器拿到的实际金额
    private String sumFromService;


    private int buytype = -1;
    private preWxPayInfo preWxInfo;
    //是否正在支付过程中，防止重复点击支付
    private boolean isPaying=false;
    private Coupon bestCoupon;
    private float oldPay=0f;
    private JijiaHttpSubscriber mSubscriber;

    //使用优惠券
    private void useCoupon(Coupon coupon) {
        if(coupon==null){
            return;
        }

        switch (coupon.getCoupon_type()) {
            //打折优惠券
            case 1:
                if (buytype == BUY_NOW) {

                    realPay = count * appItem.getCurrent_price() * Float.parseFloat(coupon.getValue()) * 0.01f;
                } else if (buytype == CART_BUY || buytype == ORDER_AGAIN) {
                    realPay = pay * Float.parseFloat(coupon.getValue()) * 0.01f;
                }
                if (realPay <= 0.01)
                    realPay = Float.parseFloat("0.01");
                break;
            //满减优惠券
            case 2:
                if (!TextUtils.isEmpty(coupon.getValue())) {
                    String[] strings = coupon.getValue().split("-");
                    if (buytype == 0) {
                        if (count * appItem.getCurrent_price() >= Float.parseFloat(strings[1]))
                            realPay = count * appItem.getCurrent_price() - Float.parseFloat(strings[1]);
                    } else if (buytype == CART_BUY || buytype == ORDER_AGAIN) {
                        if (pay >= Float.parseFloat(strings[1]))
                            realPay = pay - Float.parseFloat(strings[1]);
                    }
                }


                break;
            //立减优惠券
            case 3:
                if (buytype == BUY_NOW) {
                    realPay = count * appItem.getCurrent_price() - Float.parseFloat(coupon.getValue());
                } else if (buytype == CART_BUY || buytype == ORDER_AGAIN) {
                    realPay = pay - Float.parseFloat(coupon.getValue());
                }

                if (realPay <= 0)
                    realPay = Float.parseFloat("0.01");

                break;
            default:
                if (buytype == 0) {
                    realPay = count * appItem.getCurrent_price();
                } else if (buytype == CART_BUY || buytype == ORDER_AGAIN) {
                    realPay = pay;
                }
                break;
        }

        notifySum();
    }

    private void notifySum() {
        tvPaymentamountRealpay.setText("" + fnum.format(realPay));
    }


    @Event(R.id.btnPaymentPay)
    private void onBalancePayClick(View view) {
        if (AppUtil.isFastClick())
            return;
        isFullPointPay = true;
        generateOrder(buytype);
    }
//生成订单
    private void generateOrder(int type) {
        CreatOrderBean bean=null;
        //type   buy_now 为立即购买类型， cart_buy 为购物车结算,  order_again 为再来一单类型
        if (type == BUY_NOW) {
            //app_item  当type为 buy_now 时
            bean=new CreatOrderBean("buy_now",appItem,count,null,null,null,null);

        } else if (type == CART_BUY) {
            bean=new CreatOrderBean("cart_buy",null,1,null,null,null,null);
            //goods当type为cart_buy时购物车商品的id值
            try {
                JSONArray goodarray = new JSONArray();
                Iterator iter = selectGood.entrySet().iterator();
                while (iter.hasNext()) {
                    Map.Entry entry = (Map.Entry) iter.next();
                    NewGood good = (NewGood) entry.getKey();
                    int num = (Integer) entry.getValue();
                    JSONObject object = new JSONObject();
                    object.put("id", good.getId());
                    object.put("buy_num", num);
                    goodarray.put(object);
                }
                bean.setGoods(goodarray.toString());

            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else if (type == ORDER_AGAIN) {
            bean=new CreatOrderBean("order_again",null,1,null,id,null,null);
            //order_id 该值为再来一单的原订单id，格式为数字
        }
        //couponId=-1时，也不上传。
        if (couponId != 0&&couponId!=-1) {
            bean.setCoupon_id(couponId);
        }

        if (switchButtonPayamount.isChecked()) {
            if (!TextUtils.isEmpty(tvPaymentamountRatepoint.getText().toString())) {
                bean.setPoint(Integer.valueOf(tvPaymentamountRatepoint.getText().toString()));
            }
        }

        Observable<NewOrder> objectObservable = OrderApi.creatOrder(this,UserUtils.getUserInfo()
                .getAuth_token(), bean);
        mSubscriber = JijiaHttpSubscriber.buildSubscriber(this)
                .setOnNextListener(new SubscriberOnNextListener<NewOrder>() {
                    @Override
                    public void onNext(NewOrder order) {
                        identifier = order.getIdentifier();
                        sumFromService = String.valueOf(order.getSum());
                        if (order.getGet_point()==0) {
                            get_point = 0;

                        } else {
                            get_point = (int) order.getGet_point();
                        }


                        if (isFullPointPay) {
                            goSuccessOrder(0);
                        } else {
                            payOrder();
                        }
                    }
                }).setOnErrorListener(new SubscriberOnErrorListener() {
                    @Override
                    public void onError(Throwable e) {
                        hideLoading();
                        isPaying=false;
                    }
                })
                .build();
        objectObservable.subscribe(mSubscriber);
    }
//订单支付
    private void payOrder() {
          showLoading();
        if (!isFullPointPay) {
            RequestParams entity = new RequestParams(Configurations.URL_ORDER_PAY);
            entity.addParameter(Configurations.TOKEN, UserUtils.getUserInfo().getAuth_token());
            entity.addParameter(Configurations.IP, NetUtil.getIPAddress(true));
            entity.addParameter(Configurations.IDENTIFIER, identifier);

            String device_id= JPushInterface.getRegistrationID(NewPaymentActivity.this);
            String timeStamp= TimeUtil.getCurrentTimeString();
            entity.addParameter(Configurations.TIMESTAMP, timeStamp);
            entity.addParameter(Configurations.DEVICE_ID,device_id );

            Map<String, String> map=new TreeMap<>();
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
                            hideLoading();
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
                            FragmentManager fm = getSupportFragmentManager();
                            FragmentTransaction ft = fm.beginTransaction();
                            ft.add(customDialogWithTwoDiffButton,"customDialog");
                            ft.commitAllowingStateLoss();

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    LogUtil.e("result",result);
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
    }


    private String subject = null;
    private String total_fee = null;
    private String body = null;
    private String notify_url = null;
    private String orderid = null;

    private void startAliPay() {

        DecimalFormat fnum = new DecimalFormat("##0.00");
        subject = "CoffeeMe支付";
        //total_fee = "" + fnum.format(realPay);
        //修改为从服务器拿到的金额
         total_fee=""+fnum.format(Double.valueOf(sumFromService));
         realPay = (float) MyMath.round(realPay, 3);
        if(!fnum.format(realPay).equals(fnum.format(Double.valueOf(sumFromService)))){

            ToastUtil.showShort(NewPaymentActivity.this,"优惠券失效或错误，请重新选择优惠券。");
            return;
        }
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

        Intent intent = new Intent(NewPaymentActivity.this, WXPayEntryActivity.class);
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
//优惠券返回结果
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == PAYMENT_REQUEST_CODE) {
            switchButtonPayamount.setChecked(false);
            if (resultCode == RESULT_CANCELED) {
                ToastUtil.showShort(NewPaymentActivity.this, "没有选择优惠券");
                couponId = -1;
                mRowViewDesc.setDesc("选择优惠券");
                rvPaymentamountCoupon.setData(mRowViewDesc);
                useCoupon(new Coupon());
                tvPaymentamountRatepoint.setText("" + getPoints());
                tvPaymentamountRatepointMoney.setText(fnum.format(getPoints() * 0.01f));

            } else if (resultCode == CouponChooseActivity.RESULT_OK) {
                Coupon coupon = (Coupon) data.getSerializableExtra("coupon");
                LogUtil.e("coupon",coupon.toString());
                if (null == coupon)
                    return;
                String des = null;
                switch (coupon.getCoupon_type()){
                    case 1:
                        DecimalFormat decimalFormat = new DecimalFormat("##0.0");
                        des = decimalFormat.format(Float.valueOf(coupon.getValue()) * 0.1f)+"折优惠券";
                        break;
                    case 2:
                        String[] strings = coupon.getValue().split("-");
                        des=strings[1]+"元优惠券";
                        break;
                    case 3:
                       des= coupon.getValue()+"元优惠券";
                        break;

                }
                mRowViewDesc.setDesc(des);
                rvPaymentamountCoupon.setData(mRowViewDesc);
                useCoupon(coupon);

                tvPaymentamountRatepoint.setText("" + getPoints());
                tvPaymentamountRatepointMoney.setText(fnum.format(getPoints() * 0.01f));
                couponId = coupon.getId();

            }
        } else if (requestCode == NewRechargeActivity.REQUEST_WECHAT_PAY) {

            hideLoading();
            if(resultCode==RESULT_CANCELED){
                isPayFailure=true;
                isPaying=false;
            }
            if (data != null) {

                Bundle b = data.getExtras();
                if (b != null) {
                    int errCode = b.getInt("errCode");
                    switch (errCode) {
                        case 0:// success
                            Map<String, String> map = new HashMap<>();
                            map.put(UmengConfig.PARAM_PAYMETHOD, UmengConfig.PARAM_WECHATPAY);
                            MobclickAgent.onEventValue(NewPaymentActivity.this, UmengConfig.EVENT_PAYORDER_WECHATPAY, map, (int) realPay);

                            mHandler.sendEmptyMessage(WECHAT_PAY_SUCCSS);


                            break;
                        case -1:// failed
//                         //   Toast.makeText(NewPaymentActivity.this, "支付失败", Toast.LENGTH_SHORT).show();
                            isPayFailure=true;
                            isPaying=false;
                            mHandler.sendEmptyMessage(WECHAT_PAY_FAILED);
                            break;
                        case -2:// cancel
                            //ToastUtil.showShort(NewPaymentActivity.this, "取消支付");
                            mHandler.sendEmptyMessage(WECHAT_PAY_CANCEL);
                            isPayFailure=true;
                            isPaying=false;

                            //finish();
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


    private boolean isPayFailure=false;
    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @SuppressWarnings("unused")
        public void handleMessage(Message msg) {
            switch (msg.what) {
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
                        MobclickAgent.onEventValue(NewPaymentActivity.this, UmengConfig.EVENT_PAYORDER_ALIPAY, map, (int) realPay);
                        goSuccessOrder(get_point);
                        isPaying=false;
                    } else {
                        // 判断resultStatus 为非"9000"则代表可能支付失败
                        // "8000"代表支付结果因为支付渠道原因或者系统原因还在等待支付结果确认，最终交易是否成功以服务端异步通知为准（小概率状态）
                        if (TextUtils.equals(resultStatus, "8000")) {
                            Toast.makeText(NewPaymentActivity.this, "支付结果确认中", Toast.LENGTH_SHORT).show();

                        } else {
                            // 其他值就可以判断为支付失败，包括用户主动取消支付，或者系统返回的错误
                           // Toast.makeText(NewPaymentActivity.this, "支付失败", Toast.LENGTH_SHORT).show();

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
                                    customDialogWithTwoDiffButton.dismiss();
                                    isPayFailure=true;
                                   // finish();
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
                case WECHAT_PAY_SUCCSS:
                    hideLoading();
                    goSuccessOrder(get_point);
                    isPaying=false;
                    break;

                case WECHAT_PAY_FAILED:
                    hideLoading();
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
                            customDialogWithTwoDiffButton.dismiss();
                           // finish();
                        }
                    });
                    FragmentManager fm = getSupportFragmentManager();
                    FragmentTransaction ft = fm.beginTransaction();
                    ft.add(customDialogWithTwoDiffButton,"customDialog");
                    ft.commitAllowingStateLoss();
                    isPayFailure=true;
                    isPaying=false;

                    break;
                case WECHAT_PAY_CANCEL:
                    hideLoading();
                    customDialogWithTwoDiffButton = CustomDialogWithTwoDiffButton.newInstance("支付失败", R.drawable.icon_pay_err,"用户中途取消");
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
                            // finish();
                        }
                    });
                    FragmentManager fm2 = getSupportFragmentManager();
                    FragmentTransaction ft2 = fm2.beginTransaction();
                    ft2.add(customDialogWithTwoDiffButton,"customDialog");
                    ft2.commitAllowingStateLoss();
                        break;

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
                PayTask alipay = new PayTask(NewPaymentActivity.this);
                LogUtil.e("ali_version",alipay.getVersion());
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
//处理订单生成后的结果，积分抵消直接支付成功
    private void parseGenerateOrderResult(String result) {
        try {
            JSONObject jsonObject = new JSONObject(result);

            checkResOld(jsonObject);
            if (jsonObject.getInt(Configurations.STATUSCODE) == 200) {
                identifier = jsonObject.getJSONObject("results").getJSONObject("order").getString("identifier");
                sumFromService = jsonObject.getJSONObject("results").getJSONObject("order").getString("sum");
                if (TextUtils.isEmpty(jsonObject.getJSONObject("results").getJSONObject("order").getString("get_point"))) {
                    get_point = 0;

                } else {
                    get_point = Integer.parseInt(jsonObject.getJSONObject("results").getJSONObject("order").getString("get_point"));
                }


                if (isFullPointPay) {
                   goSuccessOrder(0);
                } else {
                    payOrder();
                }
            } else {
                ToastUtil.showShort(NewPaymentActivity.this, jsonObject.getString(Configurations.STATUSMSG));
                hideLoading();
            }
        } catch (JSONException e) {

            e.printStackTrace();
        }
    }
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

    //处理支付结果
    private void parsepayOrderResult(String result) {

        try {
            JSONObject jsonObject = new JSONObject(result);

            checkResOld(jsonObject);
            if (jsonObject.getInt(Configurations.STATUSCODE) == 200) {

                Map<String, String> map = new HashMap<>();
                map.put(UmengConfig.PARAM_PAYMETHOD, UmengConfig.PARAM_BALANCEPAY);
                MobclickAgent.onEventValue(NewPaymentActivity.this, UmengConfig.EVENT_PAYORDER_BALANCEPAY, map,(int)realPay);
                User user = JSON.parseObject(jsonObject.getJSONObject("results").getString("user"), User.class);
                UserUtils.getUserInfo().setBalance(user.getBalance());
                goSuccessOrder(get_point);
                isPaying=false;
                hideLoading();

            } else {

                ToastUtil.showShort(NewPaymentActivity.this, jsonObject.getString(Configurations.STATUSMSG));
                checkResOld(jsonObject);
                isPayFailure=true;
                isPaying=false;
                hideLoading();

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    //替换为一直显示的支付方式选择按钮,设置flag(isPaying)防止重复点击
    @Event(R.id.rl_coffeeMePay)
    private void onCoffeeMePayClick(View v){
        payMethod=0;
        if (AppUtil.isFastClick())
            return;
        if(isPaying){
            return;
        }
        if(realPay>coffeeMeBalance){
            ToastUtil.showShort(this,"余额不足");
            return;
        }
        LogUtil.e("pay","realPay="+realPay+"leavePay="+coffeeMeBalance);
        isPaying=true;
        isFullPointPay = false;
        if (buytype == 3 && null != mOrder) {
            identifier = mOrder.getIdentifier();
            payOrder();
        } else{
            if(isPayFailure){
                payOrder();
            }else{
            generateOrder(buytype);}}
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
        isFullPointPay = false;
        if (buytype == 3 && null != mOrder) {
            identifier = mOrder.getIdentifier();
            payOrder();
        } else{if(isPayFailure){
            payOrder();
        }else{
            generateOrder(buytype);}}
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
        isFullPointPay = false;
        if (buytype == 3 && null != mOrder) {
            identifier = mOrder.getIdentifier();
            payOrder();
        } else{
            if(isPayFailure){
                payOrder();
            }else{generateOrder(buytype);}
            }

    }


//获取coffemepocket余额
    private void getCoffeeMePockectBalance() {
        RequestParams params = new RequestParams(Configurations.URL_CHECK_TOKEN);
        params.addParameter(Configurations.AUTH_TOKEN, UserUtils.getUserInfo().getAuth_token());

        String timeStamp= TimeUtil.getCurrentTimeString();
        String device_id= JPushInterface.getRegistrationID(NewPaymentActivity.this);
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
                        //paycoffeeMePocketBalanceMethodDialog = SelectDialog.newInstance(R.layout.dialog_select,result.getJSONObject("results").getJSONObject("user").getString("balance"));
                        coffeeMeBalance =Float.valueOf(result.getJSONObject("results").getJSONObject("user").getString("balance")) ;
                    }else{
                       // payMethodDialog = SelectDialog.newInstance(R.layout.dialog_select,"0.0");
                        coffeeMeBalance=0.0f;
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

    private DisplayItems.AppItem appItem;
    private List<Coupon> coupons = new ArrayList<Coupon>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        showLoading();

        tbPaymentTitle.setTitle("支付");
        tbPaymentTitle.setTitleColor(Color.WHITE);
        tbPaymentTitle.setLeftImageResource(R.drawable.icon_left);
        tbPaymentTitle.setLeftClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NewPaymentActivity.this.finish();
                NewPaymentActivity.this.overridePendingTransition(R.anim.slide_in_left_base,
                        R.anim.slide_out_right_base);
            }
        });
        //获取coffeee余额
        getCoffeeMePockectBalance();
        llPaymentPay.setVisibility(View.GONE);
        llPaymentPayInit.setVisibility(View.VISIBLE);
        //积分抵扣功能取消
        mLlExPoint.setVisibility(View.GONE);
        mTvPointLine.setVisibility(View.GONE);

        RequestParams entity2 = new RequestParams(Configurations.URL_CHECK_TOKEN);
        entity2.addParameter(Configurations.AUTH_TOKEN, UserUtils.getUserInfo().getAuth_token());

        String timeStamp= TimeUtil.getCurrentTimeString();
        String device_id= JPushInterface.getRegistrationID(NewPaymentActivity.this);
        entity2.addParameter(Configurations.TIMESTAMP, timeStamp);
        entity2.addParameter(Configurations.DEVICE_ID,device_id );

        Map<String,String> map=new TreeMap<>();
        map.put(Configurations.AUTH_TOKEN, UserUtils.getUserInfo().getAuth_token());
        entity2.addParameter(Configurations.SIGN, com.hzjytech.coffeeme.utils.SignUtils.createSignString(device_id,timeStamp,map));

        x.http().get(entity2, new Callback.CommonCallback<JSONObject>() {
            @Override
            public void onSuccess(JSONObject result) {

                checkResOld(result);
                try {
                    if (result.getInt(Configurations.STATUSCODE) == 200) {
                        point = result.getJSONObject("results").getJSONObject("user").getInt("point");
                        getOrderRate();

                    } else {
                        ToastUtil.showShort(NewPaymentActivity.this, result.getString(Configurations.STATUSMSG));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

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


        switchButtonPayamount.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {

                    realPay = (float) (Math.round(realPay * 100)) / 100;
                    realPay = realPay - Float.valueOf(tvPaymentamountRatepointMoney.getText().toString());

                    if (realPay > 0.0f) {
                        llPaymentPay.setVisibility(View.GONE);
                        llPaymentPayInit.setVisibility(View.VISIBLE);
                    } else {

                        realPay = 0;
                        llPaymentPay.setVisibility(View.VISIBLE);
                        llPaymentPayInit.setVisibility(View.GONE);
                    }
                    notifySum();

                } else {
                    realPay = realPay + Float.valueOf(tvPaymentamountRatepointMoney.getText().toString());
                    notifySum();
                    llPaymentPay.setVisibility(View.GONE);
                    llPaymentPayInit.setVisibility(View.VISIBLE);

                }
            }
        });



    }

    //获取最优优惠券
    private void getBestCoupon() {
        if(coupons==null){
            return;
        }
        float nowPrice=0;
        if (buytype == ORDER_TO_BUY)
            return;
        if (buytype == BUY_NOW) {
             nowPrice= count * appItem.getCurrent_price();
        } else if (buytype == CART_BUY || buytype ==ORDER_AGAIN) {
            nowPrice=pay;
        }
        float realPay=nowPrice;
        float minPay=nowPrice;

        for(int i=0;i<coupons.size();i++){
            if(DateTimeUtil.after(coupons.get(i).getStart_date(),System.currentTimeMillis())){
                continue;
            }
            switch (coupons.get(i).getCoupon_type()){
                //打折优惠券
                case 1:
                    realPay =  nowPrice * Float.parseFloat(coupons.get(i).getValue()) * 0.01f;
                    if (realPay <= 0.01)
                        realPay = Float.parseFloat("0.01");
                    break;
                //满减优惠券
                case 2:
                    if (!TextUtils.isEmpty(coupons.get(i).getValue())) {
                        String[] strings = coupons.get(i).getValue().split("-");
                        if (nowPrice >= Float.parseFloat(strings[0]))
                            realPay = nowPrice - Float.parseFloat(strings[1]);

                    }


                    break;
                //立减优惠券
                case 3:

                    realPay = nowPrice - Float.parseFloat(coupons.get(i).getValue());


                    if (realPay <= 0)
                        realPay = Float.parseFloat("0.01");

                    break;
                default:

                    realPay = nowPrice;

                    break;
            }
            if(realPay<minPay){
                minPay=realPay;
                bestCoupon=coupons.get(i);
            }
        }
        if(bestCoupon==null){
            mRowViewDesc.setDesc("无可用优惠券");
            rvPaymentamountCoupon.setData(mRowViewDesc);
            hideLoading();
            return;
        }
        String des = null;
        switch (bestCoupon.getCoupon_type()){
            case 1:
                DecimalFormat decimalFormat = new DecimalFormat("##0.0");
                des = decimalFormat.format(Float.valueOf(bestCoupon.getValue()) * 0.1f)+"折优惠券";
                break;
            case 2:
                String[] strings = bestCoupon.getValue().split("-");
                des=strings[1]+"元优惠券";
                break;
            case 3:
                des= bestCoupon.getValue()+"元优惠券";
                break;

        }
        mRowViewDesc.setDesc(des);
        rvPaymentamountCoupon.setData(mRowViewDesc);
        useCoupon(bestCoupon);
        tvPaymentamountRatepoint.setText("" + getPoints());
        tvPaymentamountRatepointMoney.setText(fnum.format(getPoints() * 0.01f));
        couponId = bestCoupon.getId();
        hideLoading();
    }



    private void loadCoupons() {
        RequestParams entity = new RequestParams(Configurations.URL_COUPONS);
        if (UserUtils.getUserInfo() != null) {
            entity.addParameter(Configurations.TOKEN, UserUtils.getUserInfo().getAuth_token());
        }
        entity.addParameter(Configurations.AVAILABLE,String.valueOf(true));
        String device_id = JPushInterface.getRegistrationID(this);
        String timeStamp = TimeUtil.getCurrentTimeString();
        entity.addParameter(Configurations.TIMESTAMP, timeStamp);
        entity.addParameter(Configurations.DEVICE_ID, device_id);

        Map<String, String> map = new TreeMap<>();
        map.put(Configurations.TOKEN, UserUtils.getUserInfo().getAuth_token());
        map.put(Configurations.AVAILABLE, String.valueOf(true));
        entity.addParameter(Configurations.SIGN, com.hzjytech.coffeeme.utils.SignUtils.createSignString(device_id, timeStamp, map));

        x.http().get(entity, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {

                parseResult(result);

            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                //showNetError();
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
                coupons = new Gson().fromJson(object.getJSONObject("results").getString("coupons"), new TypeToken<ArrayList<Coupon>>() {
                }.getType());

            } else {
                ToastUtil.showShort(this, object.getString(Configurations.STATUSMSG));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        getBestCoupon();
    }
    private void getOrderRate() {
        RequestParams entity = new RequestParams(Configurations.URL_ORDER_RATE);
        entity.addParameter(Configurations.AUTH_TOKEN, UserUtils.getUserInfo().getAuth_token());

        String device_id= JPushInterface.getRegistrationID(NewPaymentActivity.this);
        String timeStamp= TimeUtil.getCurrentTimeString();
        entity.addParameter(Configurations.TIMESTAMP, timeStamp);
        entity.addParameter(Configurations.DEVICE_ID,device_id );

        Map<String, String> map=new TreeMap<String, String>();
        map.put(Configurations.AUTH_TOKEN, UserUtils.getUserInfo().getAuth_token());
        entity.addParameter(Configurations.SIGN, com.hzjytech.coffeeme.utils.SignUtils.createSignString(device_id,timeStamp,map));

        x.http().get(entity, new Callback.CommonCallback<JSONObject>() {
            @Override
            public void onSuccess(JSONObject result) {
                Log.d("++++++result",result.toString());
                try {
                    if (result.getInt(Configurations.STATUSCODE) == 200) {

                        if (!TextUtils.isEmpty(result.getJSONObject("results").getString("order_rate"))) {
                            if (Float.valueOf(result.getJSONObject("results").getString("order_rate")) > 100 || Float.valueOf(result.getJSONObject("results").getString("order_rate")) < 0) {
                                orderRate = 0.0f;
                            } else {
                                orderRate = Float.valueOf(result.getJSONObject("results").getString("order_rate"));
                            }
                        } else {
                            orderRate = 0.0f;
                        }

                        if (getIntent() != null) {
                            rcyViewPaymentDetail.setAdapter(adapter);
                            rcyViewPaymentDetail.setLayoutManager(new LinearLayoutManager(NewPaymentActivity.this));

                            buytype = getIntent().getIntExtra("type", 0);

                            if (buytype == BUY_NOW) {

                                appItem = (DisplayItems.AppItem) getIntent().getSerializableExtra("appItem");
                                LogUtil.e("receiveAppItem",appItem.toString());
                                count = getIntent().getIntExtra("count", 1);

                                realPay = count * appItem.getCurrent_price();

                            } else if (buytype == CART_BUY) {
                                selectGood = (Map<NewGood, Integer>) getIntent().getSerializableExtra("selgoods");
                                Iterator iter = selectGood.entrySet().iterator();
                                goods = new ArrayList<NewGood>();
                                while (iter.hasNext()) {
                                    Map.Entry entry = (Map.Entry) iter.next();
                                    NewGood good = (NewGood) entry.getKey();
                                    int num = (Integer) entry.getValue();
                                    count = count + num;
                                    for (int i = 0; i < num; i++) {
                                        goods.add(good);
                                    }
                                }

                                realPay = getIntent().getFloatExtra("sum", 0.0f);
                                pay = realPay;

                                LogUtil.d("TAG", "RealPay" + realPay + ",pay" + pay);

                            } else if (buytype == ORDER_AGAIN) {
                                mOrder = (NewOrder) getIntent().getSerializableExtra("order");
                                LogUtil.e("mOrder",mOrder.toString());
                                goods = mOrder.getGoods();
                                count = mOrder.getGoods().size();
                                id = getIntent().getIntExtra("order_id", 0);
                                oldPay = Float.parseFloat("" + mOrder.getOriginal_sum());
                                for (NewGood good : goods) {
                                     realPay+= good.getItem().getCurrent_price();

                                }
                                pay = realPay;
                                LogUtil.e("realPay",realPay+"");

                            } else if (buytype == ORDER_TO_BUY) {

                                mOrder = (NewOrder) getIntent().getSerializableExtra("order");
                                goods = mOrder.getGoods();
                                count = mOrder.getGoods().size();
                                realPay = Float.parseFloat("" + mOrder.getSum());
                                pay = realPay;
                                switchButtonPayamount.setClickable(false);
                            }
                            if (buytype == BUY_NOW) {
                                rvPaymentamountAmount.setData(new RowViewDesc("总价：", "￥" +
                                        fnum.format(count * appItem.getCurrent_price()), 0));
                                tvPaymentamountRatepoint.setText("" + getPoints());
                                tvPaymentamountRatepointMoney.setText(fnum.format(getPoints() * 0.01f));

                            } else if (buytype == CART_BUY ) {
                                rvPaymentamountAmount.setData(new RowViewDesc("总价：", "￥" + fnum.format(pay), 0));
                                tvPaymentamountRatepoint.setText("" + getPoints());
                                tvPaymentamountRatepointMoney.setText(fnum.format(getPoints() * 0.01f));
                            }else if(buytype==ORDER_AGAIN){
                                rvPaymentamountAmount.setData(new RowViewDesc("总价：", "￥" + fnum.format(pay), 0));
                               /* if(!fnum.format(oldPay).equals(fnum.format(pay))){
                                    rvPaymentamountAmount.setDesc("¥"+fnum.format(oldPay),"¥"+fnum.format(pay));
                                }*/
                                rvPaymentamountAmount.setData(new RowViewDesc("总价：", "￥" + fnum.format(pay), 0));
                                tvPaymentamountRatepoint.setText("" + getPoints());
                                tvPaymentamountRatepointMoney.setText(fnum.format(getPoints() * 0.01f));
                            } else if (buytype ==ORDER_TO_BUY && null != mOrder) {
                                rvPaymentamountAmount.setData(new RowViewDesc("总价：", "￥" + fnum.format(mOrder.getOriginal_sum()), 0));
                                mRowViewDesc.setDesc(mOrder.getCoupon_info());
                            }
                            rvPaymentamountCoupon.setData(mRowViewDesc);

                            if (buytype ==ORDER_TO_BUY && null != mOrder)
                            { tvPaymentamountRealpay.setText(String.valueOf(fnum.format(mOrder.getSum())));}
                            else
                            {tvPaymentamountRealpay.setText(String.valueOf(fnum.format(realPay)));}
                            loadCoupons();
                        } else {
                            finish();
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {

                showNetError();
                isPaying=false;

            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });
    }

    private int getPoints() {


//        return (int) (realPay * 0.1f * 100.0f);

        return Math.min(Math.round(realPay * orderRate), point);
//        return Math.round(realPay * 100f);
//        return (int) (realPay * 100f);

//        if(UserUtils.getUserInfo()!=null){
//
//            if(UserUtils.getUserInfo().getPoint()==0){
//                return 0;
//            }else{
//                if((int)(realPay*orderRate*100.0f)<=UserUtils.getUserInfo().getPoint()){
//                    return (int)(realPay*orderRate*100.0f);
//                }else{
//                    return UserUtils.getUserInfo().getPoint();
//                }
//            }
//        }else{
//            return 0;
//        }
    }


    class PaymentDetailAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private static final int LAST = 1;
        private static final int DETAIL = 0;

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
            mViewHolder1.mOrderGroup.setItemClickable(false);
            if (buytype == BUY_NOW) {
                ArrayList<DisplayItems.AppItem> list = new ArrayList<>();
                list.add(appItem);
                mViewHolder1.mOrderGroup.setItemData(list,count);
            } else if (buytype == CART_BUY || buytype == ORDER_AGAIN|| buytype == ORDER_TO_BUY) {
                mViewHolder1.mOrderGroup.setData(goods);
               /*  mViewHolder1.tvPaymentitemPrice.setText("价格: ¥" + String.valueOf(fnum.format(goods.get(position).getCurrent_price())));
               if(buytype==2&&!fnum.format(goods.get(position).getNewest_price().getNew_current_price()).equals(fnum.format(goods.get(position).getCurrent_price()))){
                    LogUtil.e("price",fnum.format(goods.get(position).getNewest_price().getNew_current_price())+"======="+fnum.format(goods.get(position).getCurrent_price()));
                    mViewHolder1.tvPaymentitemOldPrice.setVisibility(View.VISIBLE);
                    mViewHolder1.tvPaymentitemPrice.setText("价格: ¥"+String.valueOf(fnum.format(goods.get(position).getNewest_price().getNew_current_price())) );
                    mViewHolder1.tvPaymentitemOldPrice.setText("¥"+String.valueOf(fnum.format(goods.get(position).getCurrent_price())) );
                    mViewHolder1.tvPaymentitemOldPrice.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG|Paint.ANTI_ALIAS_FLAG);
                }*/
            }
//            switch (holder.getItemViewType()) {
//                case LAST:
//                    MViewHolder2 mViewHolder2 = (MViewHolder2) holder;
//                    if (buytype == 0) {
//                        mViewHolder2.rvPaymentamountAmount.setData(new RowViewDesc("价格", "￥" +
//                                fnum.format(count * appItem.getCurrent_price()), 0));
//                        mViewHolder2.tvPaymentamountRatepoint.setText("" + getPoints());
//                        mViewHolder2.tvPaymentamountRatepointMoney.setText("￥" + fnum.format(getPoints() * 0.01f));
//
//                    } else if (buytype == 1 || buytype == 2) {
//                        mViewHolder2.rvPaymentamountAmount.setData(new RowViewDesc("价格", "￥" + fnum.format(pay), 0));
//                    } else if (buytype == 3 && null != mOrder) {
//                        mViewHolder2.rvPaymentamountAmount.setData(new RowViewDesc("价格", "￥" + fnum.format(mOrder.getOriginal_sum()), 0));
//                        mRowViewDesc.setDesc(mOrder.getCoupon_info());
//                    }
//                    mViewHolder2.rvPaymentamountCoupon.setData(mRowViewDesc);
//
//                    if (buytype == 3 && null != mOrder)
//                        mViewHolder2.tvPaymentamountRealpay.setText(String.valueOf(fnum.format(mOrder.getSum())));
//                    else
//                        mViewHolder2.tvPaymentamountRealpay.setText(String.valueOf(fnum.format(realPay)));
//                    break;
//                default:
//
//                    break;
//            }

        }

        @Override
        public int getItemCount() {
            return 1;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        CommonUtil.unSubscribeSubs(mSubscriber);
    }
}