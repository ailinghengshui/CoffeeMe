package com.hzjytech.coffeeme.order;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.hzjytech.coffeeme.BaseActivity;
import com.hzjytech.coffeeme.R;
import com.hzjytech.coffeeme.configurations.UmengConfig;
import com.hzjytech.coffeeme.entities.Coupon;
import com.hzjytech.coffeeme.entities.NewOrder;
import com.hzjytech.coffeeme.http.JijiaHttpSubscriber;
import com.hzjytech.coffeeme.http.SubscriberOnCompletedListener;
import com.hzjytech.coffeeme.http.SubscriberOnErrorListener;
import com.hzjytech.coffeeme.http.SubscriberOnNextListener;
import com.hzjytech.coffeeme.http.api.OrderApi;
import com.hzjytech.coffeeme.me.CouponInfoActivity;
import com.hzjytech.coffeeme.utils.DateTimeUtil;
import com.hzjytech.coffeeme.utils.UserUtils;
import com.hzjytech.coffeeme.widgets.TitleBar;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observable;

import static com.hzjytech.coffeeme.home.SuccessPackageOrderActivity.IDENTIFERID;
import static com.hzjytech.coffeeme.order.DetailPackageOrderActivity.PAY_STATUS;
import static com.hzjytech.coffeeme.order.DetailPackageOrderActivity.REDEEM_LIST;


/**
 * Created by hehongcan on 2018/1/8.
 */

public class MyPackageCouponsActivity extends BaseActivity {
    @BindView(R.id.titleBar)
    TitleBar mTitleBar;
    @BindView(R.id.rcyViewMyPackageCoupon)
    RecyclerView mRcyViewMyPackageCoupon;
    private MyExgCouponAdapter adapter;
    private ArrayList<Coupon> mRedeem_list;
    private boolean mIsPayed;
    private String mIdentiferID;
    private JijiaHttpSubscriber mSubscriber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_package_coupons);
        ButterKnife.bind(this);
        initTitle();
        proceedIntent();
        initAdapter();
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(UmengConfig.MYPACKAGECOUPONSACTIVITY);
        MobclickAgent.onResume(this);
        loadData();

    }

    private void proceedIntent() {
        Intent intent = getIntent();
        mIsPayed = intent.getBooleanExtra(PAY_STATUS, false);
        mIdentiferID = intent.getStringExtra(IDENTIFERID);
    }

    private void initTitle() {
        mTitleBar.setTitle(R.string.package_content);
        mTitleBar.setTitleColor(Color.WHITE);
        mTitleBar.setLeftImageResource(R.drawable.icon_left);
        mTitleBar.setLeftClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void initAdapter() {
        adapter = new MyExgCouponAdapter();
        mRcyViewMyPackageCoupon.setAdapter(adapter);
        mRcyViewMyPackageCoupon.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(UmengConfig.MYCOUPONACTIVITY);
        MobclickAgent.onPause(this);

    }

    public class MyExgCouponAdapter extends RecyclerView.Adapter<MyExgCouponAdapter.MyExgCouponViewHolder> {
        @Override
        public MyExgCouponViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(MyPackageCouponsActivity.this)
                    .inflate(R.layout.mycoupon_item, parent, false);
            return new MyExgCouponViewHolder(view);
        }


        @Override
        public void onBindViewHolder(MyExgCouponViewHolder holder, final int pos) {
            holder.mTvMycouponitemTitle.setText(mRedeem_list.get(pos).getApp_item_name()+"兑换券");
            holder.mTvUseCount.setText("可使用次数：1次");
            holder.mTvMycouponitemCoupon.setText("1");
            holder.mTvMycouponitemUnit.setText("杯");
           if(mIsPayed){
               if (!TextUtils.isEmpty(mRedeem_list.get(pos).getEnd_date())) {
                   holder.mTvMycouponitemEnddate.setText(DateTimeUtil.createAvalDate(mRedeem_list.get(pos).getStart_date(),mRedeem_list.get(pos).getEnd_date()));
               } else {
                   holder.mTvMycouponitemEnddate.setText(DateTimeUtil.createAvalDate(mRedeem_list.get(pos).getStart_date()));
               }
               holder.mTvMycouponitemCondition.setVisibility(View.VISIBLE);
               holder.mTvMycouponitemCondition.setText("兑换码："+mRedeem_list.get(pos).getIdentifier());
           }else{

               holder.mTvMycouponitemEnddate.setText("有效期"+mRedeem_list.get(pos).getValid_days()+"天");
               holder.mTvMycouponitemCondition.setVisibility(View.INVISIBLE);
           }
            if(mIsPayed&&!mRedeem_list.get(pos).isBe_token()){
                holder.mTvMycouponitemCheck.setBackgroundResource(R.drawable.icon_img_unselected);
                holder.mTvMycouponitemTitle.setTextColor(getResources().getColor(R.color.standard_black));
                holder.mTvMycouponitemCoupon.setTextColor(getResources().getColor(R.color.light_red));
                holder.mTvMycouponitemUnit.setTextColor(getResources().getColor(R.color.light_red));
                holder.itemView.setClickable(true);
                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(MyPackageCouponsActivity.this, CouponInfoActivity.class);

                        Coupon coupon = mRedeem_list.get(pos);
                        coupon.setCoupon_type(4);
                        intent.putExtra("coupon",coupon);
                        MyPackageCouponsActivity.this.startActivity(intent);
                    }
                });
            }else{
                holder.mTvMycouponitemCheck.setBackgroundResource(R.drawable.icon_unuseable);
                holder.mTvMycouponitemTitle.setTextColor(getResources().getColor(R.color.standard_grey));
                holder. mTvMycouponitemCoupon.setTextColor(getResources().getColor(R.color.standard_grey));
                holder.mTvMycouponitemUnit.setTextColor(getResources().getColor(R.color.standard_grey));
                holder. itemView.setClickable(false);
            }
        }

        @Override
        public int getItemCount() {
            return mRedeem_list == null ? 0 : mRedeem_list.size();
        }

        public class MyExgCouponViewHolder extends RecyclerView.ViewHolder {
            @BindView(R.id.tvMycouponitemCheck)
            TextView mTvMycouponitemCheck;
            @BindView(R.id.tvMycouponitemCoupon)
            TextView mTvMycouponitemCoupon;
            @BindView(R.id.tvMycouponitemUnit)
            TextView mTvMycouponitemUnit;
            @BindView(R.id.tvMycouponitemTitle)
            TextView mTvMycouponitemTitle;
            @BindView(R.id.tvMycouponitemCondition)
            TextView mTvMycouponitemCondition;
            @BindView(R.id.tv_use_count)
            TextView mTvUseCount;
            @BindView(R.id.tvMycouponitemEnddate)
            TextView mTvMycouponitemEnddate;
            @BindView(R.id.iv_useable_mark)
            ImageView mIvUseableMark;

            public MyExgCouponViewHolder(
                    View view) {
                super(view);
               // R.layout.mycoupon_item
                ButterKnife.bind(this,view);
            }
        }
    }
    private void loadData() {
        showLoading();
        Observable<NewOrder> observable = OrderApi.getOrderDetail(this,
                UserUtils.getUserInfo()
                        .getAuth_token(),
                mIdentiferID);
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
                        hideLoading();
                        mRedeem_list = order.getPackage_info()
                                .getRedeem_list();
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
}

