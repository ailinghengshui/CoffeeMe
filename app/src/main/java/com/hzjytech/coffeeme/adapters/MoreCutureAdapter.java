package com.hzjytech.coffeeme.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hzjytech.coffeeme.R;
import com.hzjytech.coffeeme.culture.CultureDetailActivity;
import com.hzjytech.coffeeme.entities.NewCulture;
import com.hzjytech.coffeeme.utils.DateTimeUtil;
import com.hzjytech.coffeeme.utils.DensityUtil;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by hehongcan on 2017/11/15.
 */

public class MoreCutureAdapter extends RecyclerView.Adapter {
    private static final int TAG_BANNER = 0;
    private static final int TAG_PLATE = 1;
    private static final String URL_ARTICLE = "url_article";
    private static final int TYPE_NORMAL = 0x00;
    private static final int TYPE_FOOT = 0x01;
    private Context context;
    private int tag;
    private List<NewCulture.BannersBean.BannersContentBean> banners;
    private List<NewCulture.PlatesBean.PlateContentBean> mPlateContens;
    private final LayoutInflater mInflater;
    private DisplayImageOptions mOptions;

    public MoreCutureAdapter(
            Context context,
            int tag,
            NewCulture.PlatesBean plates,
            List<NewCulture.BannersBean.BannersContentBean> banners) {
        this.context = context;
        this.tag = tag;
        if (plates != null) {
            mPlateContens = plates.getPlate_content();
        } else {
            mPlateContens = null;
        }
        this.banners = banners;
        mInflater = LayoutInflater.from(context);
        mOptions = new DisplayImageOptions.Builder().cacheInMemory(true)/*缓存至内存*/
                .cacheOnDisk(true)/*缓存值SDcard*/
                .bitmapConfig(Bitmap.Config.RGB_565)
                .showImageOnLoading(R.drawable.bg_none)
                .showImageForEmptyUri(R.drawable.bg_none)
                .showImageOnFail(R.drawable.bg_none)
                .build();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_NORMAL) {
            if (tag == TAG_BANNER) {
                View view = mInflater.inflate(R.layout.item_more_culture_banner, parent, false);
                return new MoreCultureBannerViewHolder(view);
            } else {
                View view = mInflater.inflate(R.layout.item_more_culture_plate, parent, false);
                return new MoreCulturerPlateViewHolder(view);
            }
        } else {
            View view = mInflater.inflate(R.layout.item_foot, parent, false);
            return new FootViewHolder(view);
        }

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (!(holder instanceof FootViewHolder)) {
            if (tag == TAG_BANNER) {
                ((MoreCultureBannerViewHolder) holder).setData(banners.get(position));
            } else {
                ((MoreCulturerPlateViewHolder) holder).setData(mPlateContens, position);
            }
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (tag == TAG_BANNER) {
            if (position == banners.size()) {
                return TYPE_FOOT;
            }
        } else if (tag == TAG_PLATE) {
            if (position == mPlateContens.size()) {
                return TYPE_FOOT;
            }
        }
        return TYPE_NORMAL;
    }

    @Override
    public int getItemCount() {
        if (tag == TAG_BANNER) {
            return banners == null ? 0 : banners.size() + 1;
        } else {
            return mPlateContens == null ? 0 : mPlateContens.size() + 1;
        }
    }

    class MoreCultureBannerViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.iv_more_culture_banner)
        ImageView mIvMoreCultureBanner;
        @BindView(R.id.tv_more_culture_banner_title)
        TextView mTvMoreCultureBannerTitle;
        @BindView(R.id.tv_more_culture_banner_content)
        TextView mTvMoreCultureBannerContent;
        @BindView(R.id.tv_more_culture_banner_time)
        TextView mTvMoreCultureBannerTime;


        public MoreCultureBannerViewHolder(
                View view) {
            super(view);
            //R.layout.item_more_culture_banner
            ButterKnife.bind(this, view);

        }

        public void setData(final NewCulture.BannersBean.BannersContentBean bannersBean) {
            ImageLoader.getInstance()
                    .displayImage(bannersBean.getImage_url(), mIvMoreCultureBanner, mOptions);
            mTvMoreCultureBannerTitle.setText(bannersBean.getTitle());
            mTvMoreCultureBannerContent.setText(bannersBean.getContent());
            mTvMoreCultureBannerTime.setText(DateTimeUtil.getShort8TimeFromLong(bannersBean
                    .getCreate_at()));
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, CultureDetailActivity.class);
                    intent.putExtra(URL_ARTICLE, bannersBean.getArticle_url());
                    context.startActivity(intent);
                }
            });
        }
    }

    class MoreCulturerPlateViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.iv_content_img)
        ImageView mIvContentImg;
        @BindView(R.id.tv_content_title)
        TextView mTvContentTitle;
        @BindView(R.id.tv_content_detail)
        TextView mTvContentDetail;
        @BindView(R.id.tv_content_time)
        TextView mTvContentTime;
        @BindView(R.id.ll_container)
        LinearLayout mLlContainer;
        @BindView(R.id.view_line)
        View mViewLine;
        @BindView(R.id.view_head_top)
        View greyHead;

        public MoreCulturerPlateViewHolder(
                View view) {
            super(view);
            // R.layout.item_more_culture_plate
            ButterKnife.bind(this, view);
        }

        public void setData(
                List<NewCulture.PlatesBean.PlateContentBean> plateContentBean,
                int position) {

            final NewCulture.PlatesBean.PlateContentBean content = plateContentBean.get(position);
            if (position == 0) {
                greyHead.setVisibility(View.VISIBLE);
            } else {
                greyHead.setVisibility(View.GONE);
            }
            if (position == plateContentBean.size() - 1) {
                mViewLine.setVisibility(View.INVISIBLE);
            } else {
                mViewLine.setVisibility(View.VISIBLE);
            }
            ImageLoader.getInstance()
                    .displayImage(content.getImage_url(), mIvContentImg, mOptions);
            mTvContentTitle.setText(content.getTitle());
            mTvContentDetail.setText(content.getContent());
            mTvContentTime.setText(DateTimeUtil.getShort8TimeFromLong(content.getCreate_at()));
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(context, CultureDetailActivity.class);
                    intent.putExtra(URL_ARTICLE, content.getArticle_url());
                    context.startActivity(intent);
                }
            });
        }
    }

    private class FootViewHolder extends RecyclerView.ViewHolder {
        public FootViewHolder(View view) {super(view);}
    }
}
