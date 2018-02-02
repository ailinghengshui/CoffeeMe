package com.hzjytech.coffeeme.home;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hzjytech.coffeeme.BaseActivity;
import com.hzjytech.coffeeme.MainActivity;
import com.hzjytech.coffeeme.R;
import com.hzjytech.coffeeme.entities.Coupon;
import com.hzjytech.coffeeme.entities.NewOrder;
import com.hzjytech.coffeeme.entities.PackageOrder;
import com.hzjytech.coffeeme.http.JijiaHttpSubscriber;
import com.hzjytech.coffeeme.http.SubscriberOnCompletedListener;
import com.hzjytech.coffeeme.http.SubscriberOnErrorListener;
import com.hzjytech.coffeeme.http.SubscriberOnNextListener;
import com.hzjytech.coffeeme.http.api.OrderApi;
import com.hzjytech.coffeeme.me.MyCouponActivity;
import com.hzjytech.coffeeme.me.PointRateActivity;
import com.hzjytech.coffeeme.order.MyPackageCouponsActivity;
import com.hzjytech.coffeeme.utils.DateTimeUtil;
import com.hzjytech.coffeeme.utils.DateUtil;
import com.hzjytech.coffeeme.utils.UserUtils;
import com.hzjytech.coffeeme.widgets.TitleBar;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observable;

import static com.hzjytech.coffeeme.home.PackageModulationActivity.ORDER_FLAG;
import static com.hzjytech.coffeeme.home.PackageModulationActivity.PACKAGE_IMAGE;
import static com.hzjytech.coffeeme.order.DetailPackageOrderActivity.ORDER_BUY;
import static com.hzjytech.coffeeme.order.DetailPackageOrderActivity.PAY_STATUS;
import static com.hzjytech.coffeeme.order.DetailPackageOrderActivity.REDEEM_LIST;

/**
 * Created by hehongcan on 2018/1/8.
 */

public class SuccessPackageOrderActivity extends BaseActivity {
    public static final String IDENTIFERID = "identiferId";
    @BindView(R.id.titleBar)
    TitleBar mTitleBar;
    @BindView(R.id.success_progress)
    ImageView mSuccessProgress;
    @BindView(R.id.iv_success_package)
    ImageView mIvSuccessPackage;
    @BindView(R.id.tv_package_time)
    TextView mTvPackageTime;
    @BindView(R.id.tv_package_cost)
    TextView mTvPackageCost;
    @BindView(R.id.btn_package_to_check)
    Button mBtnPackageToCheck;
    @BindView(R.id.tv_package_point_count)
    TextView mTvPackagePointCount;
    @BindView(R.id.tv_go_point)
    TextView mTvGoPoint;
    @BindView(R.id.tvCustomDialogDescContainer)
    LinearLayout mTvCustomDialogDescContainer;
    private PackageOrder order;
    private String mImage_url;
    private NewOrder mDetailOrder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_success_package_order);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        initTitle();
        initText();
        initDetails();


    }


    private void initDetails() {
        mIvSuccessPackage.post(new Runnable() {
            @Override
            public void run() {
                int width = mIvSuccessPackage.getWidth();
                int height = width / 2;
                ViewGroup.LayoutParams layoutParams = mIvSuccessPackage.getLayoutParams();
                layoutParams.height = height;
                mIvSuccessPackage.setLayoutParams(layoutParams);
                mIvSuccessPackage.setImageResource(0);
                DisplayImageOptions options = new DisplayImageOptions.Builder().cacheInMemory
                        (true)/*缓存至内存*/
                        .cacheOnDisk(true)/*缓存值SDcard*/
                        .bitmapConfig(Bitmap.Config.RGB_565)
                        .showImageForEmptyUri(R.drawable.package_empty)
                        .showImageOnLoading(R.drawable.package_empty)
                        .showImageOnFail(R.drawable.package_empty)
                        .build();
                String str = mImage_url.contains("?") ? mImage_url.substring(0,
                        mImage_url.indexOf("?")) : mImage_url;
                ImageLoader.getInstance()
                        .displayImage(str, mIvSuccessPackage, options);
            }
        });
        mTvPackageTime.setText(DateTimeUtil.longToShort9(order.getCreated_at()));
        DecimalFormat df = new DecimalFormat("0.00");
        mTvPackageCost.setText(df.format(order.getSum()) + "元");
        mBtnPackageToCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SuccessPackageOrderActivity.this,
                        MyPackageCouponsActivity.class);
                intent.putExtra(PAY_STATUS, true);
                intent.putExtra(IDENTIFERID,order.getIdentifier());
                startActivity(intent);
                MainActivity.Instance().goOrder=true;
            }
        });

    }

    private void initText() {
        Intent intent = getIntent();
        order = intent.getParcelableExtra(ORDER_FLAG);
        mImage_url = intent.getStringExtra(PACKAGE_IMAGE);
        if (order == null) {
            mDetailOrder = (NewOrder) intent.getSerializableExtra(ORDER_BUY);
            order = new PackageOrder(mDetailOrder.getIdentifier(),
                    mDetailOrder.getPayment_provider(),
                    mDetailOrder.getCreated_at(),
                    mDetailOrder.getDescription(),
                    mDetailOrder.getOriginal_sum(),
                    mDetailOrder.getSum(),
                    mDetailOrder.getId(),
                    mDetailOrder.getGet_point(),
                    mDetailOrder.getVending_machine_id(),
                    mDetailOrder.getStatus());

        }

        mTvPackagePointCount.setText((int)order.getGet_point() + "");
        mTvGoPoint.getPaint()
                .setFlags(Paint.UNDERLINE_TEXT_FLAG);
        mTvGoPoint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SuccessPackageOrderActivity.this,
                        PointRateActivity.class);
                startActivity(intent);
            }
        });
    }

    private void initTitle() {
        mTitleBar.setTitle(R.string.string_success_order);
        mTitleBar.setTitleColor(Color.WHITE);
        mTitleBar.setLeftClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mTitleBar.setLeftImageResource(R.drawable.icon_left);
    }

}
