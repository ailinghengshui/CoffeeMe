package com.hzjytech.coffeeme.widgets.orderview;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hzjytech.coffeeme.R;
import com.hzjytech.coffeeme.configurations.Configurations;
import com.hzjytech.coffeeme.entities.DisplayItems;
import com.hzjytech.coffeeme.entities.Good;
import com.hzjytech.coffeeme.entities.NewGood;
import com.hzjytech.coffeeme.home.ModulationActivity;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.text.DecimalFormat;
import java.util.List;

import butterknife.BindView;

/**
 * Created by Hades on 2016/4/29.
 */
public class OrderRow extends LinearLayout {
    private final Context context;
    private  boolean itemClickable=true;

    private ImageView ivgood;
    private TextView tvgoodname;
    private TextView tvgoodprice;
    private TextView tvgoodinfo;
    private TextView tvgoodoldprice;
    private TextView tvgoodsugar;
    private TextView goodcount;
    private LinearLayout llgood;
    private DisplayImageOptions mOptions;
    private DecimalFormat mDf;


    public OrderRow(Context context) {
        super(context);
        this.context = context;
        init();
    }

    public OrderRow(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init();
    }

    public OrderRow(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        init();
    }

    public OrderRow(Context context, boolean itemClickable) {
        super(context);
        this.context=context;
        this.itemClickable=itemClickable;
        init();
    }

    private void init() {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_good, this, true);
        this.goodcount = (TextView) view.findViewById(R.id.good_count);
        this.tvgoodsugar = (TextView) view.findViewById(R.id.tv_good_sugar);
        this.tvgoodoldprice = (TextView) view.findViewById(R.id.tv_good_old_price);
        this.tvgoodinfo = (TextView) view.findViewById(R.id.tv_good_info);
        this.tvgoodprice = (TextView) view.findViewById(R.id.tv_good_price);
        this.tvgoodname = (TextView) view.findViewById(R.id.tv_good_name);
        this.ivgood = (ImageView) view.findViewById(R.id.iv_good);
        this.llgood = (LinearLayout) view.findViewById(R.id.ll_good);
        /*缓存至内存*//*缓存值SDcard*/
        mOptions = new DisplayImageOptions.Builder().cacheInMemory(true)/*缓存至内存*/
                .cacheOnDisk(true)/*缓存值SDcard*/
                .bitmapConfig(Bitmap.Config.RGB_565)
                .showImageOnLoading(R.drawable.bg_none)
                .showImageForEmptyUri(R.drawable.bg_none)
                .showImageOnFail(R.drawable.bg_none)
                .build();
        mDf = new DecimalFormat("0.00");
    }

    public void setText(NewGood good) {
        this.setText(good, 1);
    }

    public void setText(final NewGood good, int count) {
        if (good != null) {
            //  String str = app_image.substring(0, app_image.indexOf("?"));
            ImageLoader.getInstance()
                    .displayImage(good.getItem().getImage_url(), ivgood, mOptions);
            tvgoodprice.setText("¥" + mDf.format(good.getItem().getCurrent_price()));
            tvgoodname.setText(good.getItem().getName());
            switch (good.getSugar()){
                case 0:
                    tvgoodsugar.setText(R.string.no_sugar);
                    break;
                case 1:
                    tvgoodsugar.setText(R.string.three_points_sweet);
                    break;
                case 2:
                     tvgoodsugar.setText(R.string.five_points_sweet);
                    break;
                case 3:
                    tvgoodsugar.setText(R.string.seven_points_sweet);
                    break;
            }
           tvgoodoldprice.setText("¥" + mDf.format(good.getItem().getPrice()));
            tvgoodinfo.setText(good.getItem().getDescription());
            goodcount.setText("x" + count);
            if(itemClickable){
                llgood.setClickable(true);
                llgood.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(context, ModulationActivity.class);
                        intent.putExtra(Configurations.APP_ITEM, good.getItem());
                        context.startActivity(intent);
                    }
                });
            }else{
                llgood.setClickable(false);
            }

        }
        if(good.getItem().isBuy_enable()){
            tvgoodsugar.setVisibility(VISIBLE);
        }else{
            tvgoodsugar.setVisibility(INVISIBLE);
        }
    }

    public void setItemText(DisplayItems.AppItem item) {
        setItemText(item,1);
    }
    public void setItemText(final DisplayItems.AppItem item, int count){
        tvgoodprice.setText("¥" + mDf.format(item.getCurrent_price()));
        tvgoodname.setText(item.getName());
        switch (item.getSugar()){
            case 0:
                tvgoodsugar.setText(R.string.no_sugar);
                break;
            case 1:
                tvgoodsugar.setText(R.string.three_points_sweet);
                break;
            case 2:
                tvgoodsugar.setText(R.string.five_points_sweet);
                break;
            case 3:
                tvgoodsugar.setText(R.string.seven_points_sweet);
                break;
        }
        tvgoodoldprice.setText("¥" + mDf.format(item.getPrice()));
        goodcount.setText("x" + count);
        llgood.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ModulationActivity.class);
                intent.putExtra(Configurations.APP_ITEM, item);
                context.startActivity(intent);
            }
        });
        if(item.isBuy_enable()){
            tvgoodsugar.setVisibility(VISIBLE);
        }else{
            tvgoodsugar.setVisibility(INVISIBLE);
        }
    }
}
