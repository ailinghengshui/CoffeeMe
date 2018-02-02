package com.hzjytech.coffeeme.home;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hzjytech.coffeeme.BaseActivity;
import com.hzjytech.coffeeme.R;
import com.hzjytech.coffeeme.adapterutil.VerticalSpaceItemDecoration;
import com.hzjytech.coffeeme.configurations.Configurations;
import com.hzjytech.coffeeme.configurations.UmengConfig;
import com.hzjytech.coffeeme.entities.Coupon;
import com.hzjytech.coffeeme.listeners.OnRecyclerItemClickListener;
import com.hzjytech.coffeeme.utils.DateTimeUtil;
import com.hzjytech.coffeeme.utils.DateUtil;
import com.hzjytech.coffeeme.utils.SignUtils;
import com.hzjytech.coffeeme.utils.TimeUtil;
import com.hzjytech.coffeeme.utils.ToastUtil;
import com.hzjytech.coffeeme.utils.UserUtils;
import com.hzjytech.coffeeme.widgets.TitleBar;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.TreeMap;
import java.util.TreeMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import cn.jpush.android.api.JPushInterface;

@ContentView(R.layout.activity_coupon_choose)
public class CouponChooseActivity extends BaseActivity {

    private static final String TAG = CouponChooseActivity.class.getSimpleName();
    private static final int RESULT_BACK = 2331;

    private int couponId = -1;

    @ViewInject(R.id.titleBar)
    private TitleBar tbCouponchooseTitle;
    private CouponChooseLstAdapter adapter;

    @ViewInject(R.id.tvCouponchooseDesc)
    private TextView tvCouponchooseDesc;

    @ViewInject(R.id.tvCouponchoose)
    private TextView tvCouponchoose;

    @ViewInject(R.id.rcyViewCouponchooseList)
    private RecyclerView rcyViewCouponchooseList;

    private List<Coupon> coupons = new ArrayList<>();
    private float currentPrice;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getIntent() != null) {
            couponId = getIntent().getIntExtra("couponId", -1);
            currentPrice = getIntent().getFloatExtra("currentPrice", 0.0f);
        }

        /*if (couponId == -1) {
            updateCouponChoose("请选择优惠券", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ToastUtil.showShort(CouponChooseActivity.this, "您还未选择任何优惠券");
                }
            });
        } else {
            updateCouponChoose("使用选中优惠券", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        }*/

        initTitle();
        initAdapter();
        loadCoupons();
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(UmengConfig.COUPONCHOOSEACTIVITY);
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(UmengConfig.COUPONCHOOSEACTIVITY);
        MobclickAgent.onPause(this);
    }

    private void loadCoupons() {
        RequestParams entity = new RequestParams(Configurations.URL_COUPONS);
        if (UserUtils.getUserInfo() != null) {
            entity.addParameter(Configurations.TOKEN, UserUtils.getUserInfo().getAuth_token());
        }
        entity.addParameter(Configurations.AVAILABLE, true);

        String device_id = JPushInterface.getRegistrationID(CouponChooseActivity.this);
        String timeStamp = TimeUtil.getCurrentTimeString();
        entity.addParameter(Configurations.TIMESTAMP, timeStamp);
        entity.addParameter(Configurations.DEVICE_ID, device_id);

        Map<String, String> map = new TreeMap<>();
        map.put(Configurations.TOKEN, UserUtils.getUserInfo().getAuth_token());
        map.put(Configurations.AVAILABLE, String.valueOf(true));
        entity.addParameter(Configurations.SIGN, SignUtils.createSignString(device_id, timeStamp, map));

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

    private void initAdapter() {
        adapter = new CouponChooseLstAdapter();
        rcyViewCouponchooseList.setAdapter(adapter);
        rcyViewCouponchooseList.requestFocus();
        rcyViewCouponchooseList.setLayoutManager(new LinearLayoutManager(CouponChooseActivity.this));
        adapter.setOnItemClickListener(new OnRecyclerItemClickListener() {

            @Override
            public void onItemClick(final View itemView, final int position) {

                couponId = coupons.get(position).getId();

                if (itemView.isSelected()) {

                    couponId = -1;
                    itemView.setSelected(false);
                    adapter.notifyDataSetChanged();

                    updateCouponChoose("请选择优惠券", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            couponId = -1;
                            itemView.setSelected(false);
                            setResult(RESULT_CANCELED);
                            finish();
                        }
                    });

                } else {
                    itemView.setSelected(true);
                    couponId=coupons.get(position).getId();
                   // changeBottomState(coupons,position);
                    adapter.notifyDataSetChanged();

//
                }
            }
        });

    }

    private void changeBottomState(final List<Coupon> coupons, final int position) {
        Coupon checkedCoupon = coupons.get(position);
        String des="";
        switch (checkedCoupon.getCoupon_type()){
            case 1:
                DecimalFormat decimalFormat = new DecimalFormat("##0.0");
                des = decimalFormat.format(Float.valueOf(checkedCoupon.getValue()) * 0.1f)+"折优惠券";
                break;
            case 2:
                String[] strings = checkedCoupon.getValue().split("-");
                des=strings[1]+"元优惠券";
                break;
            case 3:
                des= checkedCoupon.getValue()+"元优惠券";
                break;

        }
        tvCouponchooseDesc.setText(des+"(可用)");
        if (checkedCoupon.getCoupon_type() == 2) {
            String[] strings =checkedCoupon.getValue().split("-");
            if (Float.valueOf(strings[0]) > currentPrice) {
                des.replace("(可用)","");
                tvCouponchooseDesc.setText(des+"("+"满￥" + strings[0] + "可用)");
            }
        }

        tvCouponchoose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if(DateTimeUtil.after(coupons.get(position).getStart_date(),System.currentTimeMillis())){
                        int day = DateUtil.getDay(DateUtil.ISO8601toCalendar(CouponChooseActivity.this.coupons.get(position).getStart_date()));
                        int mon = DateUtil.getMonth(DateUtil.ISO8601toCalendar(CouponChooseActivity.this.coupons.get(position).getStart_date()));
                        int year = DateUtil.getYear(DateUtil.ISO8601toCalendar(CouponChooseActivity.this.coupons.get(position).getStart_date()));

                        StringBuilder sb=new StringBuilder();
                        sb.append(year).append(".");
                        sb.append(mon).append(".");
                        sb.append(day).append("以后可用");
                        ToastUtil.showShort(CouponChooseActivity.this, sb.toString());
                        return;
                    }else{
                        if (CouponChooseActivity.this.coupons.get(position).getCoupon_type() == 2) {
                            String[] strings = CouponChooseActivity.this.coupons.get(position).getValue().split("-");
                            if (Float.valueOf(strings[0]) > currentPrice) {
                                ToastUtil.showShort(CouponChooseActivity.this, "满￥" + strings[0] + "可用");
                                return;
                            }
                        }
                        Intent data = new Intent();
                        couponId = CouponChooseActivity.this.coupons.get(position).getId();
                        data.putExtra("coupon", CouponChooseActivity.this.coupons.get(position));
                        setResult(RESULT_OK, data);
                        finish();
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void updateCouponChoose(String desc, View.OnClickListener listener) {
        tvCouponchooseDesc.setText(desc);
        tvCouponchoose.setOnClickListener(listener);
    }

    private void initTitle() {
        tbCouponchooseTitle.setTitle("选择优惠券");
        tbCouponchooseTitle.setTitleColor(Color.WHITE);
        tbCouponchooseTitle.setLeftImageResource(R.drawable.icon_left);
        tbCouponchooseTitle.setLeftClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_BACK);
                finish();
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

                adapter.notifyDataSetChanged();

            } else {
                ToastUtil.showShort(CouponChooseActivity.this, object.getString(Configurations.STATUSMSG));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    class CouponChooseLstAdapter extends RecyclerView.Adapter<CouponChooseLstAdapter.CouponChooseLstHolder> {

        private OnRecyclerItemClickListener listener;

        public void setOnItemClickListener(OnRecyclerItemClickListener listener) {
            this.listener = listener;
        }

        @Override
        public CouponChooseLstAdapter.CouponChooseLstHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.mycoupon_item, parent, false);
            return new CouponChooseLstHolder(view);
        }


        @Override
        public void onBindViewHolder(CouponChooseLstAdapter.CouponChooseLstHolder holder, final int pos) {
            if (couponId == coupons.get(pos).getId()) {
                holder.itemView.setSelected(true);
                changeBottomState(coupons,pos);
               /* updateCouponChoose("使用选中优惠券", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if(DateTimeUtil.after(coupons.get(pos).getStart_date(),System.currentTimeMillis())){

                            StringBuilder sb=new StringBuilder();
                            sb.append(DateTimeUtil.getShort7TimeFromLong(coupons.get(pos).getStart_date()));
                            sb.append("以后可用");
                            ToastUtil.showShort(CouponChooseActivity.this, sb.toString());
                            return;
                        }else{
                            if (coupons.get(pos).getCoupon_type() == 2) {
                                String[] strings = coupons.get(pos).getValue().split("-");
                                if (Float.valueOf(strings[0]) > currentPrice) {
                                    ToastUtil.showShort(CouponChooseActivity.this, "满￥" + strings[0] + "可用");
                                    return;
                                }
                            }
                            Intent data = new Intent();
                            couponId = coupons.get(pos).getId();
                            data.putExtra("coupon", coupons.get(pos));
                            setResult(RESULT_OK, data);
                            finish();
                        }

                    }
                });*/
            } else {
                holder.itemView.setSelected(false);
            }

            switch (coupons.get(pos).getCoupon_type()) {

                //打折优惠劵
                case 1:
                    DecimalFormat decimalFormat = new DecimalFormat("##0.0");
                    holder.tvMycouponitemCoupon.setText(decimalFormat.format(Float.valueOf(coupons.get(pos).getValue()) * 0.1f));
                    holder.tvMycouponitemUnit.setText("折");
                    holder.tvMycouponitemTitle.setText(coupons.get(pos).getTitle());
                    holder.tvMycouponitemCondition.setText("打折优惠券");

                    if (!TextUtils.isEmpty(coupons.get(pos).getEnd_date())) {

                            holder.tvMycouponitemEnddate.setText(DateTimeUtil.createAvalDate(coupons.get(pos).getStart_date(),coupons.get(pos).getEnd_date()));

                    } else {
                        holder.tvMycouponitemEnddate.setText(DateTimeUtil.createAvalDate(coupons.get(pos).getStart_date()));
                    }

                    break;
                //满减优惠券
                case 2:
                    if (!TextUtils.isEmpty(coupons.get(pos).getValue())) {
                    String[] strings = coupons.get(pos).getValue().split("-");
                    holder.tvMycouponitemCoupon.setText(strings[1]);
                    holder.tvMycouponitemUnit.setText("￥");
                    holder.tvMycouponitemTitle.setText(coupons.get(pos).getTitle());
                    holder.tvMycouponitemCondition.setText("满" + strings[0] + "使用");

                }

                    if (!TextUtils.isEmpty(coupons.get(pos).getEnd_date())) {
                            holder.tvMycouponitemEnddate.setText(DateTimeUtil.createAvalDate(coupons.get(pos).getStart_date(),coupons.get(pos).getEnd_date()));
                    } else {
                        holder.tvMycouponitemEnddate.setText(DateTimeUtil.createAvalDate(coupons.get(pos).getStart_date()));
                    }
                    break;
                //立减优惠券
                case 3:
                    holder.tvMycouponitemCoupon.setText(coupons.get(pos).getValue());
                    holder.tvMycouponitemUnit.setText("￥");
                    holder.tvMycouponitemTitle.setText(coupons.get(pos).getTitle());
                    holder.tvMycouponitemCondition.setText("立减优惠券");

                    if (!TextUtils.isEmpty(coupons.get(pos).getEnd_date())) {
                            holder.tvMycouponitemEnddate.setText(DateTimeUtil.createAvalDate(coupons.get(pos).getStart_date(),coupons.get(pos).getEnd_date()));
                    } else {
                        holder.tvMycouponitemEnddate.setText(DateTimeUtil.createAvalDate(coupons.get(pos).getStart_date()));
                    }
                    break;
            }
           //设置使用次数
            int used_num = coupons.get(pos).getUsed_num();
            int total_use_num = coupons.get(pos).getTotal_use_num();
            String useCount;
            if(total_use_num==0){
                useCount="有效期内可重复使用";
            }else{
                useCount="可使用次数："+(total_use_num-used_num)+"次";
            }
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

        @Override
        public int getItemCount() {
            return coupons.size();
        }

        class CouponChooseLstHolder extends RecyclerView.ViewHolder {

            private final TextView tvMycouponitemCoupon;
            private final TextView tvMycouponitemUnit;
            private final TextView tvMycouponitemTitle;
            private final TextView tvMycouponitemCondition;
            private final TextView tvMycouponitemEnddate;
            private final TextView tvMycouponitemCheck;
            private final TextView tvUseCount;

            public CouponChooseLstHolder(final View itemView) {
                super(itemView);

                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (listener != null) {
                            listener.onItemClick(itemView, getLayoutPosition());
                        }
                    }
                });
                tvMycouponitemCheck = (TextView) itemView.findViewById(R.id.tvMycouponitemCheck);
                tvMycouponitemCoupon = (TextView) itemView.findViewById(R.id.tvMycouponitemCoupon);
                tvMycouponitemUnit = (TextView) itemView.findViewById(R.id.tvMycouponitemUnit);
                tvMycouponitemTitle = (TextView) itemView.findViewById(R.id.tvMycouponitemTitle);
                tvMycouponitemCondition = (TextView) itemView.findViewById(R.id.tvMycouponitemCondition);
                tvMycouponitemEnddate = (TextView) itemView.findViewById(R.id.tvMycouponitemEnddate);
                tvUseCount = (TextView) itemView.findViewById(R.id.tv_use_count);
            }

        }
    }
}