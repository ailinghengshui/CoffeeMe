package com.hzjytech.coffeeme.home;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.hzjytech.coffeeme.R;
import com.hzjytech.coffeeme.configurations.Configurations;
import com.hzjytech.coffeeme.configurations.UmengConfig;
import com.hzjytech.coffeeme.entities.DisplayItems;
import com.hzjytech.coffeeme.entities.DrinkItem;
import com.hzjytech.coffeeme.fragments.BaseFragment;
import com.hzjytech.coffeeme.utils.DensityUtil;
import com.hzjytech.coffeeme.utils.LogUtil;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.sina.weibo.sdk.call.Position;
import com.umeng.analytics.MobclickAgent;

import java.text.DecimalFormat;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.noties.scrollable.CanScrollVerticallyDelegate;
import ru.noties.scrollable.OnFlingOverListener;

/**
 * Created by hehongcan on 2017/10/17.
 */

public class DrinkItemFragment extends BaseFragment implements CanScrollVerticallyDelegate, OnFlingOverListener {


    private static final int FOOTER = 0;
    private static final int ITEM = 1;
    @BindView(R.id.rv_home_drink)
    RecyclerView mRvHomeDrink;
    private HomeDrinkAdapter mAdapter;
    private List<DisplayItems.AppItem> mData;

    @Nullable
    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home_drink,container,false);
        ButterKnife.bind(this,view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void initView() {
        mRvHomeDrink.setLayoutManager(new LinearLayoutManager(getActivity(),
                LinearLayoutManager.VERTICAL,
                false));
        mAdapter = new HomeDrinkAdapter(mData);
        mRvHomeDrink.setAdapter(mAdapter);
    }

    public void setData(List<DisplayItems.AppItem> data) {
        mData = data;
        if(mAdapter!=null){
            mAdapter.setData(mData);
        }

    }


    private class HomeDrinkAdapter extends RecyclerView.Adapter{
        private List<DisplayItems.AppItem> mData;
        private LayoutInflater inflater=LayoutInflater.from(getActivity());
        DecimalFormat df=new DecimalFormat("0.00");
        public HomeDrinkAdapter(
                List<DisplayItems.AppItem> appItems) {
            this.mData=appItems;
            notifyDataSetChanged();
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            DrinkItemViewHolder viewHolder;
            if(viewType==FOOTER){
                View view3 = inflater.inflate(R.layout.appitem_hint, parent, false);
                viewHolder= new DrinkItemViewHolder(view3);
            }else{
                View view2 = inflater.inflate(R.layout.appitem_item, parent, false);
                viewHolder = new DrinkItemViewHolder(view2);
            }

            return viewHolder;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            DrinkItemViewHolder itemViewHolder = ( DrinkItemViewHolder) holder;
            if(position!=mData.size()){
                DisplayItems.AppItem appItem = (DisplayItems.AppItem)mData.get(position);
                itemViewHolder.tvAppitemName_ch_.setText(appItem.getName());
                itemViewHolder.tvAppitemName_eg.setText(appItem.getNameEn());
                itemViewHolder.tv_desc.setText(appItem.getDescription());
                itemViewHolder.tv_price_original.setText("￥" + df.format(appItem.getPrice()));
                itemViewHolder.tv_price_original.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
                itemViewHolder.tv_price_current.setText("￥" + df.format(appItem.getCurrent_price()));
                //holder2.iv_item.setImageResource(getItemDrawable(appItem.getName()));
                //// TODO: 2017/1/4
                itemViewHolder.iv_item.setImageResource(0);
                DisplayImageOptions options=new DisplayImageOptions.Builder()
                        .cacheInMemory(true)/*缓存至内存*/
                        .cacheOnDisk(true)/*缓存值SDcard*/
                        .bitmapConfig(Bitmap.Config.RGB_565)
                        .build();
                String app_image = appItem.getImage_url();
                String str = app_image.contains("?")?app_image.substring(0, app_image.indexOf("?")):app_image;
                ImageLoader.getInstance().displayImage(str,itemViewHolder.iv_item,options);
                itemViewHolder.itemView.setClickable(true);
            }else{
               itemViewHolder.itemView.setClickable(false);
            }
        }

        @Override
        public int getItemViewType(int position) {
            if(position==mData.size()){
                return FOOTER;
            }else{
                return ITEM;
            }
        }

        @Override
        public int getItemCount() {
            return mData==null?0:mData.size()+1;
        }

        public void setData(List<DisplayItems.AppItem> data) {
            mData = data;
            notifyDataSetChanged();
        }
        class DrinkItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

            public ImageView iv_item;
            public TextView tvAppitemName_ch_;
            public TextView tvAppitemName_eg;
            public TextView tv_desc;
            public TextView tv_price_original;
            public TextView tv_price_current;

            public DrinkItemViewHolder(View itemView) {
                super(itemView);
                iv_item = (ImageView) itemView.findViewById(R.id.iv_item);
                tvAppitemName_ch_ = (TextView) itemView.findViewById(R.id.tvAppitemName_ch);
                tvAppitemName_eg = (TextView) itemView.findViewById(R.id.tvAppitemName_eg);
                tv_desc = (TextView) itemView.findViewById(R.id.tv_desc);
                tv_price_original = (TextView) itemView.findViewById(R.id.tv_price_original);
                tv_price_current = (TextView) itemView.findViewById(R.id.tv_price_current);
                itemView.setOnClickListener(this);

            }

            @Override
            public void onClick(View v) {
                MobclickAgent.onEvent(getContext(), UmengConfig.EVENT_MODULATION_CLICK);
                Intent intent = new Intent(getActivity(), ModulationActivity.class);
                // TODO: 2017/10/19 模拟新的接口数据
                DisplayItems.AppItem appItem = mData.get(getLayoutPosition());
                intent.putExtra(Configurations.APP_ITEM, appItem);
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.slide_in_right_base,
                        R.anim.slide_out_left_base);
            }
        }
    }
    @Override
    public boolean canScrollVertically(int direction) {
        return mRvHomeDrink != null && mRvHomeDrink.canScrollVertically(direction);
    }

    @Override
    public void onFlingOver(int y, long duration) {
        if (mRvHomeDrink != null) {
            mRvHomeDrink.smoothScrollBy(0, y);
        }
    }
    public int getUnVisiableHeight(){
        return mRvHomeDrink.getMeasuredHeight()-DensityUtil.dp2px(getActivity(),269);
    }
}
