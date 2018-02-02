package com.hzjytech.coffeeme.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.hzjytech.coffeeme.R;
import com.hzjytech.coffeeme.configurations.UmengConfig;
import com.hzjytech.coffeeme.entities.Banner;
import com.hzjytech.coffeeme.home.BannerDetailActivity;
import com.hzjytech.coffeeme.widgets.infiniteviewpager.InfinitePagerAdapter;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.umeng.analytics.MobclickAgent;

import java.util.List;

/**
 * Created by Hades on 2016/6/2.
 */
public class MockPagerAdapter extends InfinitePagerAdapter {

    private final LayoutInflater mInflater;
    private final Context mContext;

    private List<Banner> mList;


    public void setDataList(List<Banner> list) {
        if (list == null || list.size() == 0)
            throw new IllegalArgumentException("list can not be null or has an empty size");
        this.mList = list;
        this.notifyDataSetChanged();
    }


    public MockPagerAdapter(Context context) {
        mContext=context;
        mInflater = LayoutInflater.from(mContext);
    }


    @Override
    public View getView(final int position, View view, ViewGroup container) {
        ViewHolder holder;
        if (view != null) {
            holder = (ViewHolder) view.getTag();
        } else {
            view = mInflater.inflate(R.layout.banner_detail, container, false);
            holder = new ViewHolder(view);
            view.setTag(holder);
        }
        String item = mList.get(position).getImage_url();

        DisplayImageOptions options=new DisplayImageOptions.Builder()
                .cacheInMemory(true)/*缓存至内存*/
                .cacheOnDisk(true)/*缓存值SDcard*/
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();
        ImageLoader.getInstance().displayImage(item,holder.image,options);

        holder.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MobclickAgent.onEvent(mContext, UmengConfig.EVENT_BANNER_CLICK);
                Intent intent=new Intent(mContext, BannerDetailActivity.class);
                intent.putExtra("url_article",mList.get(position).getArticle_url());
                mContext.startActivity(intent);
            }
        });

        return view;
    }


    @Override
    public int getItemCount() {
        return mList==null?0:mList.size();
    }


    private static class ViewHolder {
//        public int position;
        ImageView image;
//        Button downloadButton;
        public ViewHolder(View view) {
//            name = (TextView) view.findViewById(R.id.item_name);
//            description = (TextView) view.findViewById(R.id.item_desc);
            image = (ImageView) view.findViewById(R.id.ivCulturePosterShow);
//            downloadButton = (Button) view.findViewById(R.id.item_button);
        }
    }
}
