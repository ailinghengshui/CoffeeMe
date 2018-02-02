package com.hzjytech.coffeeme.home;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.hzjytech.coffeeme.R;
import com.hzjytech.coffeeme.configurations.Configurations;
import com.hzjytech.coffeeme.configurations.UmengConfig;
import com.hzjytech.coffeeme.entities.DisplayItems;
import com.hzjytech.coffeeme.fragments.BaseFragment;
import com.hzjytech.coffeeme.utils.DensityUtil;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.umeng.analytics.MobclickAgent;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.noties.scrollable.CanScrollVerticallyDelegate;
import ru.noties.scrollable.OnFlingOverListener;

/**
 * Created by hehongcan on 2018/1/4.
 */

public class PackageItemFragment extends BaseFragment implements CanScrollVerticallyDelegate, OnFlingOverListener {
    private static final int FOOTER = 0x00;
    private static final int ITEM = 0x01;
    @BindView(R.id.rv_home_drink)
    RecyclerView mRvHomeDrink;
    private PackageAdapter mAdapter;
    private List<DisplayItems.Packages> mData;

    @Nullable
    @Override
    public View onCreateView(
            LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home_drink, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView();
    }

    private void initView() {
        mRvHomeDrink.setLayoutManager(new LinearLayoutManager(getActivity(),
                LinearLayoutManager.VERTICAL,
                false));
        mAdapter = new PackageAdapter(mData);
        mRvHomeDrink.setAdapter(mAdapter);
    }

    public void setData(List<DisplayItems.Packages> data) {
        mData = data;
        if (mAdapter != null) {
            mAdapter.setData(mData);
        }

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }


    private class PackageAdapter extends RecyclerView.Adapter {
        private  List<DisplayItems.Packages> mData;
        private LayoutInflater inflater = LayoutInflater.from(getActivity());

        public PackageAdapter(
                List<DisplayItems.Packages> appItems) {
            this.mData = appItems;
            notifyDataSetChanged();
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            PackageViewHolder viewHolder;
            if (viewType == FOOTER) {
                View view3 = inflater.inflate(R.layout.appitem_hint, parent, false);
                viewHolder = new PackageViewHolder(view3);
            } else {
                View view2 = inflater.inflate(R.layout.appitem_package, parent, false);
                viewHolder = new PackageViewHolder(view2);
            }

            return viewHolder;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            final PackageViewHolder itemViewHolder =
                    ( PackageViewHolder) holder;
            if (position != mData.size()) {
                final DisplayItems.Packages appItem = (DisplayItems.Packages) mData.get(position);
                itemViewHolder.iv_item.setImageResource(0);
                DisplayImageOptions options = new DisplayImageOptions.Builder().cacheInMemory
                        (true)/*缓存至内存*/
                        .cacheOnDisk(true)/*缓存值SDcard*/
                        .bitmapConfig(Bitmap.Config.RGB_565)
                        .showImageForEmptyUri(R.drawable.package_empty)
                        .showImageOnLoading(R.drawable.package_empty)
                        .showImageOnFail(R.drawable.package_empty)
                        .build();
                String app_image = appItem.getImg_url();
                String str = app_image.contains("?") ? app_image.substring(0,
                        app_image.indexOf("?")) : app_image;
                ImageLoader.getInstance()
                        .displayImage(str, itemViewHolder.iv_item, options);
                itemViewHolder.itemView.setClickable(true);
            } else {
                itemViewHolder.itemView.setClickable(false);
            }
        }

        @Override
        public int getItemViewType(int position) {
            if (position == mData.size()) {
                return FOOTER;
            } else {
                return ITEM;
            }
        }

        @Override
        public int getItemCount() {
            return mData == null ? 0 : mData.size() + 1;
        }

        public void setData(List<DisplayItems.Packages> data) {
            mData = data;
            notifyDataSetChanged();
        }

        class PackageViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

            public ImageView iv_item;
            public PackageViewHolder(View itemView) {
                super(itemView);
                iv_item = (ImageView) itemView.findViewById(R.id.iv_home_package);
                itemView.setOnClickListener(this);

            }

            @Override
            public void onClick(View v) {
                MobclickAgent.onEvent(getContext(), UmengConfig.EVENT_MODULATION_CLICK);
                Intent intent = new Intent(getActivity(), PackageModulationActivity.class);
                // TODO: 2017/10/19 模拟新的接口数据
                DisplayItems.Packages appItem = mData.get(getLayoutPosition());
                intent.putExtra(Configurations.PAKAGE ,appItem);
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

    public int getUnVisiableHeight() {
        return mRvHomeDrink.getMeasuredHeight() - DensityUtil.dp2px(getActivity(), 269);
    }
}
