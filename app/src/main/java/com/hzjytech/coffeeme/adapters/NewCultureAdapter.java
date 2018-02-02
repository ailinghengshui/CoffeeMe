package com.hzjytech.coffeeme.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hzjytech.banner.SmartFragmentStatePagerAdapter;
import com.hzjytech.coffeeme.BaseActivity;
import com.hzjytech.coffeeme.R;
import com.hzjytech.coffeeme.culture.CultureDetailActivity;
import com.hzjytech.coffeeme.culture.CultureFragment;
import com.hzjytech.coffeeme.culture.CultureHeadFragment;
import com.hzjytech.coffeeme.culture.MoreCultureActivity;
import com.hzjytech.coffeeme.culture.ScaleInTransformer;
import com.hzjytech.coffeeme.entities.NewCulture;
import com.hzjytech.coffeeme.utils.DateTimeUtil;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by hehongcan on 2017/11/15.
 */

public class NewCultureAdapter extends RecyclerView.Adapter {
    private static final int TYPE_BANNER = 0;
    private static final int TYPE_PLATE = 1;
    private static final String URL_ARTICLE = "url_article";
    private final CultureFragment fragment;
    private Context context;
    private NewCulture culture;
    private final LayoutInflater mInflater;

    public NewCultureAdapter(Context context, CultureFragment cultureFragment, NewCulture culture) {
        this.context = context;
        this.culture = culture;
        this.fragment=cultureFragment;
        mInflater = LayoutInflater.from(context);
        resolveUseableData(culture);
    }

    public void setCulture(NewCulture culture) {
        this.culture = culture;
        resolveUseableData(culture);
        notifyDataSetChanged();
    }

    /**
     *
     * @param culture
     */
    private void resolveUseableData(NewCulture culture) {
        if(culture==null){
            return;
        }
        List<NewCulture.PlatesBean> plates = culture.getPlates();
        Iterator<NewCulture.PlatesBean> iterator = plates.iterator();
        while (iterator.hasNext()){
            NewCulture.PlatesBean bean = iterator.next();
            if(bean.getPlate_content()==null||bean.getPlate_content().size()==0){
                iterator.remove();
            }
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_BANNER) {
            View view = mInflater.inflate(R.layout.item_culture_head, null);
            return new CultureHeadViewHolder(view);
        } else {
            View view = mInflater.inflate(R.layout.item_culture_plate, null);
            return new CulturePlateViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof CultureHeadViewHolder) {
            ((CultureHeadViewHolder) holder).setData(culture.getBanners());
        } else {
            if(culture.getBanners()==null||culture.getBanners().getBanners_content()==null||culture.getBanners().getBanners_content().size()==0){
                ((CulturePlateViewHolder) holder).setData(culture.getPlates(),position);
            }else{
                ((CulturePlateViewHolder) holder).setData(culture.getPlates(),position-1);
            }

        }
    }

    @Override
    public int getItemViewType(int position) {
        if(culture.getBanners()==null||culture.getBanners().getBanners_content()==null||culture.getBanners().getBanners_content().size()==0){
            return TYPE_PLATE;
        }else{
        if (position == 0) {
            return TYPE_BANNER;
        } else {
            return TYPE_PLATE;
        }
        }
    }

    @Override
    public int getItemCount() {
        if(culture==null){
            return 0;
        }
        if(culture.getBanners()==null||culture.getBanners().getBanners_content()==null||culture.getBanners().getBanners_content().size()==0){
            return culture.getPlates().size();
        }else{
            return culture==null?0:1+culture.getPlates().size();
        }
    }

     class CultureHeadViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.culture_head_title)
        TextView mCultureHeadTitle;
        @BindView(R.id.tv_head_title_check_more)
        TextView mTvHeadTitleCheckMore;
        @BindView(R.id.vPgCultureHeadShow)
        ViewPager mVPgCultureShow;
        private CultureFragmentAdapter adapter;

        public CultureHeadViewHolder(
                View view) {
            super(view);
            //R.layout.item_culture_head
            ButterKnife.bind(this, view);
            iniPager();
        }

        private void iniPager() {
            mVPgCultureShow.setClipToPadding(false);
            mVPgCultureShow.setPageMargin(20);
            mVPgCultureShow.setOffscreenPageLimit(3);
            mVPgCultureShow.setPageTransformer(true,new ScaleInTransformer());

        }

        public void setData(final NewCulture.BannersBean data) {
            if (data == null || data
                    .getBanners_content()==null||!(data
                    .getBanners_content().size() > 0)) {
                return;
            }
            final List<NewCulture.BannersBean.BannersContentBean> banners_content = data
                    .getBanners_content();
            String banners_name = data.getBanners_name();
            mCultureHeadTitle.setText(banners_name);
            adapter=new CultureFragmentAdapter(((BaseActivity)context).getSupportFragmentManager(),banners_content);
            mVPgCultureShow.setAdapter(adapter);
            if(banners_content.size()>2){
                mTvHeadTitleCheckMore.setVisibility(View.VISIBLE);
                mTvHeadTitleCheckMore.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(context,MoreCultureActivity.class);
                        intent.putExtra("banners",
                               data);
                        context.startActivity(intent);
                    }
                });
            }else{
                mTvHeadTitleCheckMore.setVisibility(View.GONE);
            }

        }
        class CultureFragmentAdapter extends SmartFragmentStatePagerAdapter {


            private  List<NewCulture.BannersBean
                    .BannersContentBean> mData;

            public CultureFragmentAdapter(FragmentManager fm, List<NewCulture.BannersBean
                    .BannersContentBean> data) {
                super(fm);
                mData=data;
            }

            @Override
            public Fragment getItem(int position) {
                return CultureHeadFragment.newInstance(mData.get(position));
            }

            @Override
            public int getCount() {
                if(mData==null){
                    return 0;
                }else if(mData.size()<4){
                    return mData.size();
                }else{
                    return 3;
                }
            }

        }
    }

     class CulturePlateViewHolder extends RecyclerView.ViewHolder {
         @BindView(R.id.culture_plate_title)
        TextView mCulturePlateTitle;
        @BindView(R.id.tv_plate_check_more)
        TextView mTvPlateCheckMore;
        @BindView(R.id.iv_content_img_first)
        ImageView mIvContentImgFirst;
        @BindView(R.id.tv_content_title_first)
        TextView mTvContentTitleFirst;
        @BindView(R.id.tv_content_detail_first)
        TextView mTvContentDetailFirst;
        @BindView(R.id.tv_content_time_first)
        TextView mTvContentTimeFirst;
        @BindView(R.id.ll_container_first)
        LinearLayout mLlContainerFirst;
        @BindView(R.id.iv_content_img_second)
        ImageView mIvContentImgSecond;
        @BindView(R.id.tv_content_title_second)
        TextView mTvContentTitleSecond;
        @BindView(R.id.tv_content_detail_second)
        TextView mTvContentDetailSecond;
        @BindView(R.id.tv_content_time_second)
        TextView mTvContentTimeSecond;
        @BindView(R.id.ll_container_second)
        LinearLayout mLlContainerSecond;
         @BindView(R.id.ll_plate_container)
        LinearLayout mLlContainer;
         private  DisplayImageOptions mOptions;


         public CulturePlateViewHolder(
                View view) {
            super(view);
            //R.layout.item_culture_plate
            ButterKnife.bind(this, view);
             mOptions = new DisplayImageOptions.Builder().cacheInMemory(true)/*缓存至内存*/
                     .cacheOnDisk(true)/*缓存值SDcard*/
                     .bitmapConfig(Bitmap.Config.RGB_565)
                     .showImageOnLoading(R.drawable.bg_none)
                     .showImageForEmptyUri(R.drawable.bg_none)
                     .showImageOnFail(R.drawable.bg_none)
                     .build();
        }

         /**
          * 根据设置的数量设置界面
          * @param data
          * @param position plate模块的位置，从0开始
          */
         public void setData(List<NewCulture.PlatesBean> data, int position) {
             final NewCulture.PlatesBean bean = data.get(position);
             mCulturePlateTitle.setText(bean.getPlate_name());
             final List<NewCulture.PlatesBean.PlateContentBean> contentBeen = bean.getPlate_content();
             if(contentBeen==null||contentBeen.size()==0){
                 //板块内容为空
                 mLlContainer.setVisibility(View.GONE);
             } else{
                 final NewCulture.PlatesBean.PlateContentBean firstPlateContent = contentBeen.get(0);
                 mLlContainer.setVisibility(View.VISIBLE);
                 ImageLoader.getInstance()
                         .displayImage(firstPlateContent.getImage_url(), mIvContentImgFirst, mOptions);
                 mTvContentTitleFirst.setText(firstPlateContent.getTitle());
                 mTvContentDetailFirst.setText(firstPlateContent.getContent());
                 mTvContentTimeFirst.setText(DateTimeUtil.getShort8TimeFromLong(firstPlateContent.getCreate_at()));
                 if(contentBeen.size()==1){
                     mLlContainerSecond.setVisibility(View.GONE);
                 }else{
                     NewCulture.PlatesBean.PlateContentBean secondPlateContent = contentBeen.get(1);
                     mLlContainerSecond.setVisibility(View.VISIBLE);
                     ImageLoader.getInstance()
                             .displayImage(secondPlateContent.getImage_url(), mIvContentImgSecond, mOptions);
                     mTvContentTitleSecond.setText(secondPlateContent.getTitle());
                     mTvContentDetailSecond.setText(secondPlateContent.getContent());
                     mTvContentTimeSecond.setText(DateTimeUtil.getShort8TimeFromLong(secondPlateContent.getCreate_at()));
                 }
                 //点击事件
                 if(contentBeen.size()>2){
                     mTvPlateCheckMore.setVisibility(View.VISIBLE);
                     mTvPlateCheckMore.setOnClickListener(new View.OnClickListener() {
                         @Override
                         public void onClick(View v) {
                             Intent intent = new Intent(context, MoreCultureActivity.class);
                             intent.putExtra("plates",bean);
                             context.startActivity(intent);
                         }
                     });
                 }else{
                     mTvPlateCheckMore.setVisibility(View.INVISIBLE);
                 }
                 mLlContainerFirst.setOnClickListener(new View.OnClickListener() {
                     @Override
                     public void onClick(View v) {
                         Intent intent=new Intent(context, CultureDetailActivity.class);
                         intent.putExtra(URL_ARTICLE,firstPlateContent.getArticle_url());
                         context.startActivity(intent);
                     }
                 });
                 mLlContainer.setOnClickListener(new View.OnClickListener() {
                     @Override
                     public void onClick(View v) {
                         Intent intent=new Intent(context, CultureDetailActivity.class);
                         intent.putExtra(URL_ARTICLE,contentBeen.get(1).getArticle_url());
                         context.startActivity(intent);
                     }
                 });
             }
         }
     }
}
