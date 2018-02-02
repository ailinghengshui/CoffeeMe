package com.hzjytech.coffeeme.me;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hzjytech.coffeeme.BaseActivity;
import com.hzjytech.coffeeme.Dialogs.HintDialog;
import com.hzjytech.coffeeme.MainActivity;
import com.hzjytech.coffeeme.R;
import com.hzjytech.coffeeme.configurations.Configurations;
import com.hzjytech.coffeeme.entities.Coupon;
import com.hzjytech.coffeeme.listeners.IMethod1Listener;
import com.hzjytech.coffeeme.utils.CameraUtil;
import com.hzjytech.coffeeme.utils.DateTimeUtil;
import com.hzjytech.coffeeme.utils.SignUtils;
import com.hzjytech.coffeeme.utils.TimeUtil;
import com.hzjytech.coffeeme.utils.ToastUtil;
import com.hzjytech.coffeeme.utils.UserUtils;
import com.hzjytech.coffeeme.widgets.SelectLinearLayout;
import com.hzjytech.coffeeme.widgets.TitleBar;
import com.hzjytech.scan.activity.CaptureActivity;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.HttpMethod;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.text.DecimalFormat;
import java.util.Map;
import java.util.TreeMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.jpush.android.api.JPushInterface;

/**
 * Created by hehongcan on 2017/10/26.
 */

public class CouponInfoActivity extends BaseActivity {
    private static final int REQUEST_CODE_FETCH = 111;
    private static final String ESPRESSO = "意式浓缩";
    @BindView(R.id.titleBar)
    TitleBar mTitleBar;
    @BindView(R.id.tvMycouponitemCoupon)
    TextView mTvCouponItemNum;
    @BindView(R.id.tvMycouponitemUnit)
    TextView mTvCouponItemUnit;
    @BindView(R.id.tvMycouponitemTitle)
    TextView mTvCouponItemTitle;
    @BindView(R.id.tvMycouponitemCondition)
    TextView mTvCouponItemCondition;
    @BindView(R.id.tv_use_count)
    TextView mTvCouponItemUsecount;
    @BindView(R.id.tvMycouponitemEnddate)
    TextView mTvCouponItemEndDate;
    @BindView(R.id.tv_coupon_display_title)
    TextView mTvCouponDisplayTitle;
    @BindView(R.id.tv_coupon_display_limit)
    TextView mTvCouponDisplayLimit;
    @BindView(R.id.tv_coupon_display_count)
    TextView mTvCouponDisplayCount;
    @BindView(R.id.tv_display_coupon_time)
    TextView mTvDisplayCouponTime;
    @BindView(R.id.tv_display_coupon_first_tip)
    TextView mTvFirstTip;
    @BindView(R.id.tv_coupon_bottom)
    TextView mTvCouponBottom;
    @BindView(R.id.ll_sugar_container)
    LinearLayout mLlSugarContainer;
    @BindView(R.id.sl_sugar)
    SelectLinearLayout mSlSugar;
    private Coupon mCoupon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coupon_info);
        ButterKnife.bind(this);
        initIntent();
        initView();
    }

    private void initView() {
        initTitle();
        initCouponView();

    }

    private void initCouponView() {
        //设置有效期
        if (!TextUtils.isEmpty(mCoupon.getEnd_date())) {
            mTvCouponItemEndDate.setText(DateTimeUtil.createAvalDate(mCoupon.getStart_date(),
                    mCoupon.getEnd_date()));
            mTvDisplayCouponTime.setText(DateTimeUtil.createAvalDate(mCoupon.getStart_date(),
                    mCoupon.getEnd_date()));
        } else {
            mTvCouponItemEndDate.setText(DateTimeUtil.createAvalDate(mCoupon.getStart_date()));
            mTvDisplayCouponTime.setText(DateTimeUtil.createAvalDate(mCoupon.getStart_date()));
        }
        //设置可使用次数
        int used_num = mCoupon.getUsed_num();
        int total_use_num = mCoupon.getTotal_use_num();
        String useCount;
        if (total_use_num == 0) {
            useCount = "有效期内可重复使用";
        } else {
            useCount = "可使用次数：" + (total_use_num - used_num) + "次";
        }
        //根据是否过期和是否开始设置控件信息，包括文字颜色、图片颜色,icon是否可见
        String start_date = mCoupon.getStart_date();
        String end_date = mCoupon.getEnd_date();
        //除兑换券外的使用次数固定格式*
        if (mCoupon.getCoupon_type() != 4) {
            if (useCount.contains("可使用次数")) {
                int startIndex = useCount.indexOf("次数") + 3;
                int endIndex = useCount.length() - 1;
                SpannableStringBuilder style = new SpannableStringBuilder(useCount);
                style.setSpan(new ForegroundColorSpan(Color.RED),
                        startIndex,
                        endIndex,
                        Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
                mTvCouponItemUsecount.setText(style);
                mTvCouponDisplayCount.setText(style);
            } else {
                mTvCouponItemUsecount.setText(useCount);
                mTvCouponDisplayCount.setText(useCount);
            }
            mTvCouponBottom.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(CouponInfoActivity.this, MainActivity.class);
                    MainActivity.Instance().goHome = true;
                    CouponInfoActivity.this.startActivity(intent);
                }
            });
        }
        mLlSugarContainer.setVisibility(View.GONE);
        //除兑换券外提示第一条的固定文字
        mTvFirstTip.setText(R.string.coupon_info_discount_first);
        mTvCouponBottom.setText("去使用");
        //根据不同优惠券设置文字信息
        switch (mCoupon.getCoupon_type()) {
            //打折优惠劵
            case 1:
                DecimalFormat decimalFormat = new DecimalFormat("##0.0");
                mTvCouponItemNum.setText(decimalFormat.format(Float.valueOf(mCoupon.getValue()) *
                        0.1f));
                mTvCouponItemUnit.setText("折");
                mTvCouponItemTitle.setText(mCoupon.getTitle());
                mTvCouponItemCondition.setText("打折优惠券");
                mTvCouponDisplayTitle.setText(mCoupon.getTitle() + "，" + decimalFormat.format
                        (Float.valueOf(
                        mCoupon.getValue()) * 0.1f) + "折");
                mTvCouponDisplayLimit.setText("打折优惠券");
                break;
            //满减优惠券
            case 2:
                if (!TextUtils.isEmpty(mCoupon.getValue())) {
                    if (mCoupon.getValue()
                            .contains("-")) {
                        String[] strings = mCoupon.getValue()
                                .split("-");
                        mTvCouponItemNum.setText(strings[1]);
                        mTvCouponItemUnit.setText("￥");
                        mTvCouponItemTitle.setText(mCoupon.getTitle());
                        mTvCouponItemCondition.setText("满" + strings[0] + "使用");
                        mTvCouponDisplayTitle.setText(mCoupon.getTitle() + "，" + strings[1] + "元");
                        mTvCouponDisplayLimit.setText("满" + strings[0] + "使用");
                    } else {
                        mTvCouponItemCondition.setText(getResources().getString(R.string
                                .err_coupon));

                    }

                }
                break;
            //立减优惠券
            case 3:
                mTvCouponItemNum.setText(mCoupon.getValue());
                mTvCouponItemUnit.setText("￥");
                mTvCouponItemTitle.setText(mCoupon.getTitle());
                mTvCouponItemCondition.setText("立减优惠券");
                mTvCouponDisplayTitle.setText(mCoupon.getTitle() + "," + mCoupon.getValue() + "元");
                mTvCouponDisplayLimit.setText("立减优惠券");
                break;
            case 4:
                mTvCouponItemNum.setText(1 + "");
                mTvCouponItemUnit.setText("杯");
                mTvCouponItemTitle.setText(mCoupon.getApp_item_name()+"兑换券");
                mTvCouponItemCondition.setText("兑换码：" + mCoupon.getIdentifier());
                mTvCouponItemUsecount.setText("可使用次数：1次");
                mTvCouponDisplayTitle.setText(mCoupon.getApp_item_name() + "兑换券，1杯");
                mTvCouponDisplayLimit.setText("兑换码：" + mCoupon.getIdentifier());
                mTvCouponDisplayCount.setText("可使用次数：1次");
                mTvFirstTip.setText(R.string.coupon_info_exchange_first);
                mTvCouponBottom.setText("扫一扫取货");
                mTvCouponBottom.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        exchangeCoffee();
                    }
                });
                if(!mCoupon.getApp_item_name().equals(ESPRESSO)){
                    mLlSugarContainer.setVisibility(View.VISIBLE);
                }
                mSlSugar.setSelectIndex(mCoupon.getSugar());
                mSlSugar.setOnSelectListener(new IMethod1Listener() {
                    @Override
                    public void OnMethod1Listener(int param) {
                        mCoupon.setSugar(param);
                    }
                });


        }
    }

    //获取咖啡
    private void exchangeCoffee() {
        if (!CameraUtil.isCameraCanUse()) {
            //如果没有授权，则请求授权
            HintDialog hintDialog = HintDialog.newInstance("提示", "无法获取摄像头数据，请检查是否已经打开摄像头权限。", "确定");
            hintDialog.show(getSupportFragmentManager(), "cameraHint");
        } else {
            //有授权，直接开启摄像头
            Intent intent = new Intent(CouponInfoActivity.this, CaptureActivity.class);
            CouponInfoActivity.this.startActivityForResult(intent, REQUEST_CODE_FETCH);
        }
    }

    private void initTitle() {
        mTitleBar.setTitle(mCoupon.getApp_item_name()+"兑换券");
        mTitleBar.setTitleColor(Color.WHITE);
        mTitleBar.setLeftImageResource(R.drawable.icon_left);
        mTitleBar.setLeftClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void initIntent() {
        Intent intent = getIntent();
        mCoupon = (Coupon) intent.getSerializableExtra("coupon");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE_FETCH) {

            if (null == data)
                return;
            String result = data.getStringExtra(CaptureActivity.SCAN_RESULT_KEY);

            if (null != result) {

                getCoffeeMId(result);

            } else {
                ToastUtil.showShort(CouponInfoActivity.this, "扫描失败，待会再试一试");

            }
        } else if (resultCode == Activity.RESULT_CANCELED) {
            ToastUtil.showShort(CouponInfoActivity.this, getResources().getString(R.string.cancel));
        }
    }

    private void getCoffeeMId(String id) {
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
        entity.addParameter(Configurations.REDEEMPTION_ID, mCoupon.getIdentifier());
        map.put(Configurations.REDEEMPTION_ID, mCoupon.getIdentifier());
        //排除意式浓缩的情况
        if(!mCoupon.getApp_item_name().equals(ESPRESSO)){
            entity.addParameter(Configurations.SUGAR,mCoupon.getSugar());
            map.put(Configurations.SUGAR,mCoupon.getSugar()+"");
        }
        String device_id = JPushInterface.getRegistrationID(CouponInfoActivity.this);
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

                            ToastUtil.showShort(CouponInfoActivity.this,
                                    result.getString(Configurations.STATUSMSG));
                            finish();
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
