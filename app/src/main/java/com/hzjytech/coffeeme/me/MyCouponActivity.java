package com.hzjytech.coffeeme.me;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
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
import com.alipay.android.app.IAlixPay;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hzjytech.coffeeme.BaseActivity;
import com.hzjytech.coffeeme.Dialogs.HintDialog;
import com.hzjytech.coffeeme.R;
import com.hzjytech.coffeeme.configurations.Configurations;
import com.hzjytech.coffeeme.configurations.UmengConfig;
import com.hzjytech.coffeeme.entities.Coupon;
import com.hzjytech.coffeeme.utils.AppUtil;
import com.hzjytech.coffeeme.utils.CameraUtil;
import com.hzjytech.coffeeme.utils.DateTimeUtil;
import com.hzjytech.coffeeme.utils.DateUtil;
import com.hzjytech.coffeeme.utils.DensityUtil;
import com.hzjytech.coffeeme.utils.LogUtil;
import com.hzjytech.coffeeme.utils.SharedPrefUtil;
import com.hzjytech.coffeeme.utils.SignUtils;
import com.hzjytech.coffeeme.utils.TimeUtil;
import com.hzjytech.coffeeme.utils.ToastUtil;
import com.hzjytech.coffeeme.utils.UserUtils;
import com.hzjytech.coffeeme.widgets.TitleBar;
import com.hzjytech.scan.activity.CaptureActivity;
import com.umeng.analytics.MobclickAgent;

import org.joda.time.DateTime;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.HttpMethod;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
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

@ContentView(R.layout.activity_my_coupon)
public class MyCouponActivity extends BaseActivity {

    public static final String SHOW_BOTTOM_BTN = "bottom_btn";
    private static final int MY_PERMISSIONS_REQUEST_CALL_CAMERA = 334;
    private static final String COUPON="coupon";
    private static final String REDEEM="redeem";

    @ViewInject(R.id.titleBar)
    private TitleBar tbMycouponTitle;

    @ViewInject(R.id.mycouponemptyview)
    private TextView partemptyview;

    @ViewInject(R.id.btnMycouponExchange)
    private Button btnMycouponExchange;

    @ViewInject(R.id.rcyViewMycouponList)
    private RecyclerView rcyViewMycouponList;

    @ViewInject(R.id.rlScanForCoupon)
    private RelativeLayout mRlScanForCoupon;
    private MyCouponLstAdapter adapter;
    private List<Coupon> coupons = new ArrayList<>();
    private String type=COUPON;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        proceedExtras();
        initTitle();
    }

    private void proceedExtras() {
        type = getIntent().getStringExtra("type");
    }

    public static MyCouponActivity mInstance;
    @Event(R.id.btnMycouponExchange)
    private void onMycouponExchange(View view) {
        startActivity(new Intent(MyCouponActivity.this, PointExchangeActivity.class));
    }

    public static MyCouponActivity Instance() {
        if (null == mInstance)
            mInstance = new MyCouponActivity();
        return mInstance;
    }
    @Override
    protected void onResume() {
        mInstance=this;
        super.onResume();
        MobclickAgent.onPageStart(UmengConfig.MYCOUPONACTIVITY);
        MobclickAgent.onResume(this);

        if (getIntent().getBooleanExtra(SHOW_BOTTOM_BTN, true)) {
            rcyViewMycouponList.setPadding(DensityUtil.dp2px(MyCouponActivity.this, 22), 0, DensityUtil.dp2px(MyCouponActivity.this, 22), DensityUtil.dp2px(MyCouponActivity.this, 58));
            btnMycouponExchange.setVisibility(View.VISIBLE);
        } else {
            rcyViewMycouponList.setPadding(DensityUtil.dp2px(MyCouponActivity.this, 22), 0, DensityUtil.dp2px(MyCouponActivity.this, 22), DensityUtil.dp2px(MyCouponActivity.this, 10));
            btnMycouponExchange.setVisibility(View.GONE);

        }


        initAdapter();

        if (!SharedPrefUtil.getLoginType().equals(SharedPrefUtil.LOGINING)) {
            partemptyview.setVisibility(View.VISIBLE);
            rcyViewMycouponList.setVisibility(View.GONE);
        }else{
            partemptyview.setVisibility(View.GONE);
            rcyViewMycouponList.setVisibility(View.VISIBLE);
            if(type.equals(COUPON)){
                loadCoupons();
            }else{
                loadExgCoupons();
            }

        }
    }

    private void loadExgCoupons() {
        showLoading();
        RequestParams entity = new RequestParams(Configurations.URL_COUPONS);
        entity.addParameter(Configurations.TOKEN, UserUtils.getUserInfo().getAuth_token());
        String device_id= JPushInterface.getRegistrationID(MyCouponActivity.this);
        String timeStamp= TimeUtil.getCurrentTimeString();
        entity.addParameter(Configurations.TIMESTAMP, timeStamp);
        entity.addParameter(Configurations.DEVICE_ID,device_id );
       entity.addParameter(Configurations.TYPE,REDEEM);
        Map<String, String> map=new TreeMap<String, String>();
        map.put(Configurations.TOKEN, UserUtils.getUserInfo().getAuth_token());
        map.put(Configurations.TYPE,REDEEM);
        //map.put(Configurations.AVAILABLE, String.valueOf(true));
        entity.addParameter(Configurations.SIGN, SignUtils.createSignString(device_id,timeStamp,map));

        x.http().get(entity, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                LogUtil.e("result",result);
                hideLoading();
                parseResult(result);

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
                hideLoading();
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(UmengConfig.MYCOUPONACTIVITY);
        MobclickAgent.onPause(this);

    }

    private void loadCoupons() {
        showLoading();
        RequestParams entity = new RequestParams(Configurations.URL_COUPONS);
        entity.addParameter(Configurations.TOKEN, UserUtils.getUserInfo().getAuth_token());
        entity.addParameter(Configurations.TYPE,COUPON);
        String device_id= JPushInterface.getRegistrationID(MyCouponActivity.this);
        String timeStamp= TimeUtil.getCurrentTimeString();
        entity.addParameter(Configurations.TIMESTAMP, timeStamp);
        entity.addParameter(Configurations.DEVICE_ID,device_id );
        Map<String, String> map=new TreeMap<String, String>();
        map.put(Configurations.TOKEN, UserUtils.getUserInfo().getAuth_token());
        map.put(Configurations.TYPE,COUPON);
        entity.addParameter(Configurations.SIGN, SignUtils.createSignString(device_id,timeStamp,map));

        x.http().get(entity, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                LogUtil.e("result",result);
                hideLoading();
                parseResult(result);

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
             hideLoading();
            }
        });

    }

    private void initAdapter() {

        adapter = new MyCouponLstAdapter();
        rcyViewMycouponList.setAdapter(adapter);
        rcyViewMycouponList.setLayoutManager(new LinearLayoutManager(MyCouponActivity.this,LinearLayoutManager.VERTICAL,false));

    }

    private void initTitle() {
        if(type.equals(COUPON)){
            tbMycouponTitle.setTitle("我的优惠券");
            mRlScanForCoupon.setVisibility(View.VISIBLE);
        }else {
            tbMycouponTitle.setTitle("我的兑换券");
            mRlScanForCoupon.setVisibility(View.GONE);
        }

        tbMycouponTitle.setTitleColor(Color.WHITE);
        tbMycouponTitle.setLeftImageResource(R.drawable.icon_left);
        tbMycouponTitle.setLeftClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Event(R.id.ivMycouponScan)
    private void onMyCouponScanClick(View v) {
        if(AppUtil.isFastClick()){
            return;
        }
        if (!CameraUtil.isCameraCanUse()) {
            //如果没有授权，则请求授权
            HintDialog hintDialog = HintDialog.newInstance("提示", "无法获取摄像头数据，请检查是否已经打开摄像头权限。", "确定");
            hintDialog.show(getSupportFragmentManager(),"cameraHint");
        } else {
            //有授权，直接开启摄像头
            Intent intent = new Intent(MyCouponActivity.this, CaptureActivity.class);
            startActivityForResult(intent, 0);
        }

    }


    @ViewInject(R.id.etMycouponPick)
    private EditText etMycouponPick;

    @Event(R.id.btnMycouponAdd)
    private void onMyCouponAddClick(View v) {
        if (!TextUtils.isEmpty(etMycouponPick.getText().toString())) {
            RequestParams entity = new RequestParams(Configurations.URL_REDEEMED);
            entity.addParameter(Configurations.AUTH_TOKEN, UserUtils.getUserInfo().getAuth_token());
            entity.addParameter("coupon_id", etMycouponPick.getText().toString().trim());

            String device_id = JPushInterface.getRegistrationID(MyCouponActivity.this);
            String timeStamp= TimeUtil.getCurrentTimeString();
            entity.addParameter(Configurations.TIMESTAMP, timeStamp);
            entity.addParameter(Configurations.DEVICE_ID, device_id);

            Map<String, String> map = new TreeMap<String, String>();
            map.put(Configurations.AUTH_TOKEN, UserUtils.getUserInfo().getAuth_token());
            map.put("coupon_id", etMycouponPick.getText().toString().trim());
            entity.addParameter(Configurations.SIGN, SignUtils.createSignString(device_id, timeStamp, map));

            x.http().request(HttpMethod.PUT, entity, new Callback.CommonCallback<JSONObject>() {
                @Override
                public void onSuccess(JSONObject result) {
                    if (result != null) {

                        checkResOld(result);
                        try {
                            if (result.getInt(Configurations.STATUSCODE) == 200) {
                                MobclickAgent.onEvent(MyCouponActivity.this, UmengConfig.EVENT_ADD_COUPON);
                                Coupon coupon = JSON.parseObject(result.getJSONObject("results").getString("coupon"), Coupon.class);

                                try {
                                    coupon.setCreated_at(DateTimeUtil.ISOToString(coupon
                                                .getCreated_at()));
                                    coupon.setEnd_Date(DateTimeUtil.ISOToString(coupon
                                            .getEnd_date()));
                                    coupon.setStart_date(DateTimeUtil.ISOToString(coupon.getStart_date()));
                                    coupon.setUpdated_at(DateTimeUtil.ISOToString(coupon.getUpdated_at()));
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                coupons.add(0, coupon);
                                adapter.notifyDataSetChanged();
                                etMycouponPick.setText("");
                            } else {

                                ToastUtil.showShort(MyCouponActivity.this, result.getString(Configurations.STATUSMSG));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    } else {
                        ToastUtil.showShort(MyCouponActivity.this, getString(R.string.str_err_net));
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
            ToastUtil.showShort(MyCouponActivity.this, getResources().getString(R.string.str_nocoupon));
        }

        etMycouponPick.setText("");

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            String result = data.getStringExtra(CaptureActivity.SCAN_RESULT_KEY);
            etMycouponPick.setText(result);
        } else if (resultCode == RESULT_CANCELED) {
            ToastUtil.showShort(MyCouponActivity.this, getResources().getString(R.string.cancel));
        }
    }


    private void parseResult(String result) {
        try {
            JSONObject object = new JSONObject(result);

            checkResOld(object);
            if (object.getInt(Configurations.STATUSCODE) == 200) {
                coupons = new Gson().fromJson(object.getJSONObject("results").getString("coupons"), new TypeToken<ArrayList<Coupon>>() {
                }.getType());

                for (int i = 0; i < coupons.size(); i++) {
                    LogUtil.d("coupon" + i, coupons.get(i).getValue() + ":" + coupons.get(i).getEnd_date());
                }
                adapter.notifyDataSetChanged();

            } else {
                ToastUtil.showShort(MyCouponActivity.this, object.getString(Configurations.STATUSMSG));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    class MyCouponLstAdapter extends RecyclerView.Adapter<MyCouponLstAdapter.MyCouponLstHolder> {

        class MyCouponLstHolder extends RecyclerView.ViewHolder {

            private final TextView tvMycouponitemCoupon;
            private final TextView tvMycouponitemUnit;
            private final TextView tvMycouponitemTitle;
            private final TextView tvMycouponitemCondition;
            private final TextView tvMycouponitemEnddate;
            private final TextView tvMycouponitemCheck;
            private final TextView tvUseCount;
            private final ImageView ivUseAble;
            public MyCouponLstHolder(View itemView) {
                super(itemView);

                tvMycouponitemCheck = (TextView) itemView.findViewById(R.id.tvMycouponitemCheck);
                tvMycouponitemCoupon = (TextView) itemView.findViewById(R.id.tvMycouponitemCoupon);
                tvMycouponitemUnit = (TextView) itemView.findViewById(R.id.tvMycouponitemUnit);
                tvMycouponitemTitle = (TextView) itemView.findViewById(R.id.tvMycouponitemTitle);
                tvMycouponitemCondition = (TextView) itemView.findViewById(R.id.tvMycouponitemCondition);
                tvMycouponitemEnddate = (TextView) itemView.findViewById(R.id.tvMycouponitemEnddate);
                tvUseCount = (TextView) itemView.findViewById(R.id.tv_use_count);
                ivUseAble = (ImageView) itemView.findViewById(R.id.iv_useable_mark);

            }

        }


        @Override
        public MyCouponLstAdapter.MyCouponLstHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(MyCouponActivity.this).inflate(R.layout.mycoupon_item, parent, false);
            return new MyCouponLstHolder(view);
        }




        @Override
        public void onBindViewHolder(MyCouponLstAdapter.MyCouponLstHolder holder, final int pos) {
            //设置有效期
            if (!TextUtils.isEmpty(coupons.get(pos).getEnd_date())) {
                holder.tvMycouponitemEnddate.setText(DateTimeUtil.createAvalDate(coupons.get(pos).getStart_date(),coupons.get(pos).getEnd_date()));
            } else {
                holder.tvMycouponitemEnddate.setText(DateTimeUtil.createAvalDate(coupons.get(pos).getStart_date()));
            }
            //设置可使用次数
            int used_num = coupons.get(pos).getUsed_num();
            int total_use_num =coupons.get(pos).getTotal_use_num() ;
            String useCount;
            if(total_use_num==0){
                useCount="有效期内可重复使用";
            }else{
                useCount="可使用次数："+(total_use_num-used_num)+"次";
            }
            //根据是否过期和是否开始设置控件信息，包括文字颜色、图片颜色,icon是否可见
            String start_date = coupons.get(pos).getStart_date();
            String end_date = coupons.get(pos).getEnd_date();
            if(start_date!=null&&!start_date.equals("")&&DateTimeUtil.after(start_date, System.currentTimeMillis())){
                //未开始
                holder.tvMycouponitemCheck.setBackgroundResource(R.drawable.icon_unuseable);
                holder.tvMycouponitemTitle.setTextColor(getResources().getColor(R.color.standard_grey));
                holder.tvMycouponitemCoupon.setTextColor(getResources().getColor(R.color.standard_grey));
                holder.tvMycouponitemUnit.setTextColor(getResources().getColor(R.color.standard_grey));
                holder.ivUseAble.setVisibility(View.VISIBLE);
                holder.ivUseAble.setImageResource(R.drawable.icon_unstart);
                holder.tvUseCount.setText(useCount);
                holder.itemView.setClickable(false);
            }else if(end_date!=null&&!end_date.equals("")&&DateTimeUtil.before(end_date,System.currentTimeMillis())){
                //已过期
                holder.tvMycouponitemCheck.setBackgroundResource(R.drawable.icon_unuseable);
                holder.tvMycouponitemTitle.setTextColor(getResources().getColor(R.color.standard_grey));
                holder.tvMycouponitemCoupon.setTextColor(getResources().getColor(R.color.standard_grey));
                holder.tvMycouponitemUnit.setTextColor(getResources().getColor(R.color.standard_grey));
                holder.ivUseAble.setVisibility(View.VISIBLE);
                holder.ivUseAble.setImageResource(R.drawable.icon_outtime);
                holder.tvUseCount.setText(useCount);
                holder.itemView.setClickable(false);
            }else{
                //正常情况
                holder.tvMycouponitemCheck.setBackgroundResource(R.drawable.icon_img_unselected);
                holder.tvMycouponitemTitle.setTextColor(getResources().getColor(R.color.standard_black));
                holder.tvMycouponitemCoupon.setTextColor(getResources().getColor(R.color.light_red));
                holder.tvMycouponitemUnit.setTextColor(getResources().getColor(R.color.light_red));
                holder.ivUseAble.setVisibility(View.GONE);
                if(coupons.get(pos).getCoupon_type()!=4){
                    if(useCount.contains("可使用次数")){
                        int startIndex = useCount.indexOf("次数")+3;
                        int endIndex=useCount.length()-1;
                        SpannableStringBuilder style=new SpannableStringBuilder(useCount);
                        style.setSpan(new ForegroundColorSpan(Color.RED),startIndex,endIndex, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
                        holder.tvUseCount.setText(style);
                    }else{
                        holder.tvUseCount.setText(useCount);
                    }
                }
                holder.itemView.setClickable(true);
                //点击事件
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(MyCouponActivity.this, CouponInfoActivity.class);
                        intent.putExtra("coupon", coupons.get(pos));
                        MyCouponActivity.this.startActivity(intent);
                    }
                });

            }
            //根据不同优惠券设置文字信息
            switch (coupons.get(pos).getCoupon_type()) {
                //打折优惠劵
                case 1:
                    DecimalFormat decimalFormat = new DecimalFormat("##0.0");
                    holder.tvMycouponitemCoupon.setText(decimalFormat.format(Float.valueOf(coupons.get(pos).getValue()) * 0.1f));
                    holder.tvMycouponitemUnit.setText("折");
                    holder.tvMycouponitemTitle.setText(coupons.get(pos).getTitle());
                    holder.tvMycouponitemCondition.setText("打折优惠券");


                    break;
                //满减优惠券
                case 2:
                    if (!TextUtils.isEmpty(coupons.get(pos).getValue())) {
                        if (coupons.get(pos).getValue().contains("-")) {
                            String[] strings = coupons.get(pos).getValue().split("-");
                            holder.tvMycouponitemCoupon.setText(strings[1]);
                            holder.tvMycouponitemUnit.setText("￥");
                            holder.tvMycouponitemTitle.setText(coupons.get(pos).getTitle());
                            holder.tvMycouponitemCondition.setText("满" + strings[0] + "使用");
                        } else {
                            holder.tvMycouponitemCondition.setText(getResources().getString(R.string.err_coupon));

                        }

                    }
                    break;
                //立减优惠券
                case 3:
                    holder.tvMycouponitemCoupon.setText(coupons.get(pos).getValue());
                    holder.tvMycouponitemUnit.setText("￥");
                    holder.tvMycouponitemTitle.setText(coupons.get(pos).getTitle());
                    holder.tvMycouponitemCondition.setText("立减优惠券");
                    break;
                case 4:
                    holder.tvMycouponitemCoupon.setText("1");
                    holder.tvMycouponitemUnit.setText("杯");
                    holder.tvMycouponitemTitle.setText(coupons.get(pos).getTitle());
                    holder.tvMycouponitemCondition.setText("兑换码："+coupons.get(pos).getIdentifier());
                    holder.tvUseCount.setText("可使用次数：1次");

            }

        }

        @Override
        public int getItemCount() {
            return coupons.size();
        }
    }

}