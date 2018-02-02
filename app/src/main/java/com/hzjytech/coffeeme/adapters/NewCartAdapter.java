package com.hzjytech.coffeeme.adapters;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.hzjytech.coffeeme.R;
import com.hzjytech.coffeeme.authorization.login.LoginActivity;
import com.hzjytech.coffeeme.configurations.Configurations;
import com.hzjytech.coffeeme.entities.Good;
import com.hzjytech.coffeeme.entities.Ingredient;
import com.hzjytech.coffeeme.entities.NewGood;
import com.hzjytech.coffeeme.home.ModulationActivity;
import com.hzjytech.coffeeme.home.NewCartActivity;
import com.hzjytech.coffeeme.utils.NetUtil;
import com.hzjytech.coffeeme.utils.SharedPrefUtil;
import com.hzjytech.coffeeme.utils.SignUtils;
import com.hzjytech.coffeeme.utils.TimeUtil;
import com.hzjytech.coffeeme.utils.ToastUtil;
import com.hzjytech.coffeeme.utils.UserUtils;
import com.hzjytech.coffeeme.widgets.AddSubView;
import com.hzjytech.coffeeme.widgets.MyAddSubView;
import com.hzjytech.coffeeme.widgets.SwipeItemLayout;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.jpush.android.api.JPushInterface;
import cz.msebera.android.httpclient.Header;

/**
 * Created by Hades on 2016/6/20.
 */
public class NewCartAdapter extends RecyclerView.Adapter<NewCartAdapter.CartViewHolder> {

    private OnNewCartAdapterListener mListener;

    private boolean mIsSelectAll = false;
    private boolean isFinish = true;

    public void setSelectAll(boolean isSelectAll) {
        this.mIsSelectAll = isSelectAll;
    }

    public interface OnNewCartAdapterListener {

        void notifySum(String sum);

        void onSelectAll(boolean isSelectAll);

        void onSelectedCount(int count);
    }

    public void setOnCartAdapterListener(OnNewCartAdapterListener listener) {
        this.mListener = listener;
    }

    private List<SwipeItemLayout> mOpenedSil = new ArrayList<>();
    private List<NewGood> mGoods = new ArrayList<>();
    private NewCartActivity mContext;
    private Map<NewGood, Integer> selectGood = new HashMap<>();
    private Map<NewGood, Integer> allGood = new HashMap<>();

    public NewCartAdapter(NewCartActivity context, List<NewGood> goods) {
        setHasStableIds(true);
        this.mContext = context;
        this.mGoods = goods;
    }

    public Handler handler = new Handler();

    public void add(int index, NewGood good) {
        mGoods.add(index, good);
        notifyDataSetChanged();
    }

    public void addRefreshAll(Collection<NewGood> goods) {
        if (goods != null) {
            mGoods.clear();
            mGoods.addAll(goods);
            initAllGood();
            notifyDataSetChanged();
        }
    }

    private void initAllGood() {
        for (NewGood good : mGoods) {
            allGood.put(good, 1);
        }
    }

    public void addLoadMoreAll(Collection<NewGood> goods) {
        if (goods != null) {
            mGoods.addAll(goods);
            initAllGood();
            notifyDataSetChanged();
        }
    }

    public void clear() {
        mGoods.clear();
        notifyDataSetChanged();
    }

    public void remove(NewGood good) {
        mGoods.remove(good);
        notifyDataSetChanged();
    }

    public NewGood getItem(int position) {
        if (position < 0 || position >= getItemCount()) {
            ToastUtil.showShort(mContext, mContext.getString(R.string.str_outofbound));
            return null;
        } else {
            return mGoods.get(position);
        }
    }

    @Override
    public long getItemId(int position) {
        return getItem(position).hashCode();
    }

    @Override
    public CartViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.new_cart_item, parent, false);
     /*   view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                parent.requestDisallowInterceptTouchEvent(true);
                return true;
            }
        });*/
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final CartViewHolder holder, final int position) {
        if (selectGood.containsKey(mGoods.get(position))) {
            holder.rbCartitemStatus.setSelected(true);
        } else {
            holder.rbCartitemStatus.setSelected(false);
        }
        holder.mGoodCount.setVisibility(View.INVISIBLE);
        holder.tvCartitemName.setText(mGoods.get(position)
                .getItem().getName());

        DisplayImageOptions options=new DisplayImageOptions.Builder()
                .cacheInMemory(true)/*缓存至内存*/
                .cacheOnDisk(true)/*缓存值SDcard*/
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();
        String app_image = mGoods.get(position).getItem().getImage_url();
        //  String str = app_image.substring(0, app_image.indexOf("?"));
        ImageLoader.getInstance().displayImage(app_image,holder.mIvGood,options);
        holder.mTvGoodInfo.setText(mGoods.get(position).getItem().getDescription());
        int sugar = mGoods.get(position)
                .getSugar();
        switch (sugar){
            case 0:
                holder.mTvGoodSugar.setText(R.string.no_sugar);
                break;
            case 1:
                holder.mTvGoodSugar.setText(R.string.three_points_sweet);
                break;
            case 2:
                holder.mTvGoodSugar.setText(R.string.five_points_sweet);
                break;
            case 3:
                holder.mTvGoodSugar.setText(R.string.seven_points_sweet);
                break;
        }
        DecimalFormat fnum = new DecimalFormat("##0.00");
        holder.tvCartitemPrice.setText("¥ " + String.valueOf(fnum.format(mGoods.get(position)
                .getItem().getCurrent_price())) );
        holder.mTvGoodOldPrice.setText("¥ " + String.valueOf(fnum.format(mGoods.get(position)
                .getItem().getPrice())) );
        holder.addSunCartitemCount.setText(allGood.containsKey(mGoods.get(position)) ? allGood.get(
                mGoods.get(position)) : 1);
        holder.addSunCartitemCount.setListener(new AddSubView.AddSubViewable() {
            @Override
            public void onAddSubViewClick(int count) {
                if (holder.rbCartitemStatus.isSelected()) {
                    selectGood.put(mGoods.get(position), count);
                }
                allGood.put(mGoods.get(position), count);
                if (mListener != null) {
                    mListener.notifySum(String.valueOf(sum()));

                    mListener.onSelectedCount(getSelectGoodCount());
                }
            }
        });

    }

    private int getSelectGoodCount() {
        int sum = 0;
        for (int i : selectGood.values()) {
            sum += i;
        }
        return sum;
    }

    @Override
    public int getItemCount() {
        return mGoods == null ? 0 : mGoods.size();
    }

    class CartViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tvCartitemDel)
        TextView tvCartitemDel;
        @BindView(R.id.rbCartitemStatus)
        TextView rbCartitemStatus;
        @BindView(R.id.llCartitemContainer)
        LinearLayout llCartitemContainer;
        @BindView(R.id.iv_good)
        ImageView mIvGood;
        @BindView(R.id.tv_good_name)
        TextView tvCartitemName;
        @BindView(R.id.tv_good_price)
        TextView tvCartitemPrice;
        @BindView(R.id.tv_good_info)
        TextView mTvGoodInfo;
        @BindView(R.id.tv_good_old_price)
        TextView mTvGoodOldPrice;
        @BindView(R.id.tv_good_sugar)
        TextView mTvGoodSugar;
        @BindView(R.id.good_count)
        TextView mGoodCount;
        @BindView(R.id.rlCartitemContainer1)
        RelativeLayout llCartitemContainer1;
        @BindView(R.id.addSunCartitemCount)
        MyAddSubView addSunCartitemCount;
        @BindView(R.id.rlCartItemContainer2)
        RelativeLayout mRlCartItemContainer2;
        @BindView(R.id.swipeItemCartItemRoot)
        SwipeItemLayout  swipeItemCartItemRoot;
        @BindView(R.id.ll_cart_item)
        LinearLayout mLlCartItem;

        public CartViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
           // R.layout.new_cart_item
            swipeItemCartItemRoot.setSwipeAble(true);
            swipeItemCartItemRoot.setDelegate(new SwipeItemLayout.SwipeItemLayoutDelegate() {
                @Override
                public void onSwipeItemLayoutOpened(SwipeItemLayout swipeItemLayout) {
                    closeOpenedSwipeItemLayoutWithAnim();
                    mOpenedSil.add(swipeItemLayout);
                }

                @Override
                public void onSwipeItemLayoutClosed(SwipeItemLayout swipeItemLayout) {
                    mOpenedSil.remove(swipeItemLayout);

                }

                @Override
                public void onSwipeItemLayoutStartOpen(SwipeItemLayout swipeItemLayout) {
                    closeOpenedSwipeItemLayoutWithAnim();

                }
            });

            tvCartitemDel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (NetUtil.isNetworkAvailable(mContext)) {
                        if (isFinish == false) {
                            return;
                        }
                        mContext.showLoading();
                        isFinish = false;
                        String delUrl = Configurations.URL_GOODS + "/" + mGoods.get(
                                getLayoutPosition())
                                .getId();
                        RequestParams params = new RequestParams();
                        params.put(Configurations.AUTH_TOKEN,
                                UserUtils.getUserInfo()
                                        .getAuth_token());

                        String device_id = JPushInterface.getRegistrationID(mContext);
                        String timeStamp = TimeUtil.getCurrentTimeString();
                        params.put(Configurations.TIMESTAMP, timeStamp);
                        params.put(Configurations.DEVICE_ID, device_id);

                        Map<String, String> map = new TreeMap<String, String>();
                        map.put(Configurations.AUTH_TOKEN,
                                UserUtils.getUserInfo()
                                        .getAuth_token());
                        params.put(Configurations.SIGN,
                                SignUtils.createSignString(device_id, timeStamp, map));

                        AsyncHttpClient client = new AsyncHttpClient();
                        client.delete(delUrl, params, new JsonHttpResponseHandler() {
                            @Override
                            public void onSuccess(
                                    int statusCode,
                                    Header[] headers,
                                    JSONObject response) {

                                try {
                                    if (response.getInt(Configurations.STATUSCODE) == 200) {
                                        selectGood.remove(mGoods.get(getLayoutPosition()));
                                        allGood.remove(mGoods.get(getLayoutPosition()));
                                        if (mListener != null) {
                                            mListener.onSelectedCount(getSelectGoodCount());
                                            mListener.notifySum(String.valueOf(sum()));
                                        }
                                        remove(mGoods.get(getLayoutPosition()));

                                    } else if (response.getInt(Configurations.STATUSCODE) == 401
                                            || response.getInt(
                                            Configurations.STATUSCODE) == 403) {
                                        goLogin();
                                    } else {
                                        notifyDataSetChanged();
                                    }
                                    mContext.hideLoading();
                                    ToastUtil.showShort(mContext,
                                            response.getString(Configurations.STATUSMSG));
                                    //动画完成需要一定的时间
                                    handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            isFinish = true;
                                        }
                                    }, 1000);


                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    }
                }
            });

            llCartitemContainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (rbCartitemStatus.isSelected()) {
                        rbCartitemStatus.setSelected(false);
                        selectGood.remove(mGoods.get(getLayoutPosition()));
                        mIsSelectAll = false;

                    } else {
                        rbCartitemStatus.setSelected(true);
                        selectGood.put(mGoods.get(getLayoutPosition()),
                                addSunCartitemCount.getText());
                    }

                    if (null != mListener) {
                        mListener.notifySum(String.valueOf(sum()));

                        mListener.onSelectedCount(getSelectGoodCount());
                    }
                    SetAllSelView();
                }


            });
            mLlCartItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext, ModulationActivity.class);
                    intent.putExtra(Configurations.APP_ITEM, mGoods.get(getLayoutPosition()).getItem());
                    mContext.startActivity(intent);
                }
            });
        }


        private void SetAllSelView() {
            if ((selectGood.size() > 0) && (selectGood.size() == mGoods.size())) {
                mIsSelectAll = true;
            } else {
                mIsSelectAll = false;
            }
            if (null != mListener) {
                mListener.onSelectAll(mIsSelectAll);
            }
        }
    }

    public void closeOpenedSwipeItemLayoutWithAnim() {
        for (SwipeItemLayout sil : mOpenedSil) {
            sil.closeWithAnim();
        }
        mOpenedSil.clear();
    }

    public void goLogin() {
        SharedPrefUtil.loginout();
        SharedPrefUtil.saveAvatorUrl("");
        SharedPrefUtil.saveUri("");
        SharedPrefUtil.saveWeiboId("");
        UserUtils.saveUserInfo(null);

        Intent intent = new Intent(mContext, LoginActivity.class);
        mContext.startActivity(intent);
    }

    public void selectAll() {
        for (int i = 0; i < mGoods.size(); i++) {
            selectGood.put(mGoods.get(i), allGood.get(mGoods.get(i)));
        }

        if (null != mListener) {
            mListener.notifySum(String.valueOf(sum()));
            mListener.onSelectedCount(getSelectGoodCount());
        }
        notifyDataSetChanged();
    }

    public void disSelectAll() {
        for (int i = 0; i < mGoods.size(); i++) {
            selectGood.clear();
        }
        if (null != mListener) {
            mListener.notifySum(String.valueOf(sum()));
            mListener.onSelectedCount(getSelectGoodCount());
        }
        notifyDataSetChanged();
    }

    public Map<NewGood, Integer> getSelectGood() {
        return selectGood;
    }

    private float sum() {
        float sum = 0f;
        if (selectGood.size() > 0) {
            Iterator iter = selectGood.entrySet()
                    .iterator();
            while (iter.hasNext()) {
                Map.Entry entry = (Map.Entry) iter.next();
                sum += ((NewGood) entry.getKey()).getItem().getCurrent_price() * (Integer) entry.getValue();
            }
            sum = (float) (Math.round(sum * 100)) / 100;
        }

        return sum;
    }


}
