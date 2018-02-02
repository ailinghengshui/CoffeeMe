package com.hzjytech.coffeeme.adapters;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.hzjytech.coffeeme.R;
import com.hzjytech.coffeeme.authorization.login.LoginActivity;
import com.hzjytech.coffeeme.configurations.Configurations;
import com.hzjytech.coffeeme.entities.Good;
import com.hzjytech.coffeeme.entities.Ingredient;
import com.hzjytech.coffeeme.home.NewCartActivity;
import com.hzjytech.coffeeme.utils.NetUtil;
import com.hzjytech.coffeeme.utils.SharedPrefUtil;
import com.hzjytech.coffeeme.utils.SignUtils;
import com.hzjytech.coffeeme.utils.TimeUtil;
import com.hzjytech.coffeeme.utils.ToastUtil;
import com.hzjytech.coffeeme.utils.UserUtils;
import com.hzjytech.coffeeme.widgets.AddSubView;
import com.hzjytech.coffeeme.widgets.SwipeItemLayout;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

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

import cn.jpush.android.api.JPushInterface;
import cz.msebera.android.httpclient.Header;

/**
 * Created by Hades on 2016/6/20.
 */
public class NewCartAdapter extends RecyclerView.Adapter<NewCartAdapter.CartViewHolder> {

    private OnNewCartAdapterListener mListener;

    private boolean mIsSelectAll = false;
    private boolean isFinish=true;

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
    private List<Good> mGoods = new ArrayList<>();
    private NewCartActivity mContext;
    private Map<Good, Integer> selectGood = new HashMap<>();
    private Map<Good, Integer> allGood = new HashMap<>();

    public NewCartAdapter(NewCartActivity context, List<Good> goods) {
        setHasStableIds(true);
        this.mContext = context;
        this.mGoods = goods;
    }
    public Handler handler=new Handler();
    public void add(int index, Good good) {
        mGoods.add(index, good);
        notifyDataSetChanged();
    }

    public void addRefreshAll(Collection<Good> goods) {
        if (goods != null) {
            mGoods.clear();
            mGoods.addAll(goods);
            initAllGood();
            notifyDataSetChanged();
        }
    }

    private void initAllGood() {
        for (Good good : mGoods) {
            allGood.put(good, 1);
        }
    }

    public void addLoadMoreAll(Collection<Good> goods) {
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

    public void remove(Good good) {
        mGoods.remove(good);
        notifyDataSetChanged();
    }

    public Good getItem(int position) {
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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.new_cart_item, parent, false);
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

        holder.tvCartitemName.setText(mGoods.get(position).getName());

        List<Ingredient> ingredients = new ArrayList<>();

        ingredients.clear();
        ingredients = JSON.parseArray(mGoods.get(position).getIngredients(), Ingredient.class);

        boolean doubleBean = false;
        Iterator<Ingredient> iterator = ingredients.iterator();
        while (iterator.hasNext()) {
            String name = iterator.next().getName();
            if ("杯子".equals(name)) {
                iterator.remove();
            }
            if ("水".equals(name)) {
                iterator.remove();
            }

            if ("咖啡豆".equals(name)) {
                if (!doubleBean) {
                    doubleBean = true;
                } else {
                    iterator.remove();
                }
            }

        }
        switch (ingredients.size()) {
            case 5:
                holder.llCartitemContainer5.setVisibility(View.VISIBLE);
                holder.tvCartitemIngredient5.setText(ingredients.get(4).getDisplay_name());
                holder.tvCartitemIngredientValue5.setText(ingredients.get(4).getDisplay_value());
            case 4:
                holder.llCartitemContainer4.setVisibility(View.VISIBLE);
                holder.tvCartitemIngredient4.setText(ingredients.get(3).getDisplay_name());
                holder.tvCartitemIngredientValue4.setText(ingredients.get(3).getDisplay_value());
            case 3:
                holder.llCartitemContainer3.setVisibility(View.VISIBLE);
                holder.tvCartitemIngredient3.setText(ingredients.get(2).getDisplay_name());
                holder.tvCartitemIngredientValue3.setText(ingredients.get(2).getDisplay_value());
            case 2:
                holder.llCartitemContainer2.setVisibility(View.VISIBLE);
                holder.tvCartitemIngredient2.setText(ingredients.get(1).getDisplay_name());
                holder.tvCartitemIngredientValue2.setText(ingredients.get(1).getDisplay_value());
            case 1:
                holder.llCartitemContainer1.setVisibility(View.VISIBLE);
                holder.tvCartitemIngredient1.setText(ingredients.get(0).getDisplay_name());
                holder.tvCartitemIngredientValue1.setText(ingredients.get(0).getDisplay_value());
                break;
            default:
                break;
        }

        DecimalFormat fnum = new DecimalFormat("##0.00");
        holder.tvCartitemPrice.setText("价格: " + String.valueOf(fnum.format(mGoods.get(position).getCurrent_price())) + "元");

        holder.addSunCartitemCount.setText(allGood.containsKey(mGoods.get(position)) ? allGood.get(mGoods.get(position)) : 1);
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

        private final SwipeItemLayout swipeItemCartItemRoot;
        private final TextView rbCartitemStatus;
        private final TextView tvCartitemName;
        private final LinearLayout llCartitemContainer;
        private final LinearLayout llCartitemContainer1;
        private final TextView tvCartitemIngredient1;
        private final TextView tvCartitemIngredientValue1;
        private final LinearLayout llCartitemContainer2;
        private final TextView tvCartitemIngredient2;
        private final TextView tvCartitemIngredientValue2;
        private final LinearLayout llCartitemContainer3;
        private final TextView tvCartitemIngredient3;
        private final TextView tvCartitemIngredientValue3;
        private final LinearLayout llCartitemContainer4;
        private final TextView tvCartitemIngredient4;
        private final TextView tvCartitemIngredientValue4;
        private final LinearLayout llCartitemContainer5;
        private final TextView tvCartitemIngredient5;
        private final TextView tvCartitemIngredientValue5;
        private final TextView tvCartitemPrice;
        private final AddSubView addSunCartitemCount;
        private final TextView tvCartitemDel;

        public CartViewHolder(View itemView) {
            super(itemView);
            swipeItemCartItemRoot = (SwipeItemLayout) itemView.findViewById(R.id.swipeItemCartItemRoot);
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

            rbCartitemStatus = (TextView) itemView.findViewById(R.id.rbCartitemStatus);
            tvCartitemName = (TextView) itemView.findViewById(R.id.tvCartitemName);
            llCartitemContainer = (LinearLayout) itemView.findViewById(R.id.llCartitemContainer);
            llCartitemContainer1 = (LinearLayout) itemView.findViewById(R.id.llCartitemContainer1);
            tvCartitemIngredient1 = (TextView) itemView.findViewById(R.id.tvCartitemIngredient1);
            tvCartitemIngredientValue1 = (TextView) itemView.findViewById(R.id.tvCartitemIngredientValue1);
            llCartitemContainer2 = (LinearLayout) itemView.findViewById(R.id.llCartitemContainer2);
            tvCartitemIngredient2 = (TextView) itemView.findViewById(R.id.tvCartitemIngredient2);
            tvCartitemIngredientValue2 = (TextView) itemView.findViewById(R.id.tvCartitemIngredientValue2);
            llCartitemContainer3 = (LinearLayout) itemView.findViewById(R.id.llCartitemContainer3);
            tvCartitemIngredient3 = (TextView) itemView.findViewById(R.id.tvCartitemIngredient3);
            tvCartitemIngredientValue3 = (TextView) itemView.findViewById(R.id.tvCartitemIngredientValue3);
            llCartitemContainer4 = (LinearLayout) itemView.findViewById(R.id.llCartitemContainer4);
            tvCartitemIngredient4 = (TextView) itemView.findViewById(R.id.tvCartitemIngredient4);
            tvCartitemIngredientValue4 = (TextView) itemView.findViewById(R.id.tvCartitemIngredientValue4);
            llCartitemContainer5 = (LinearLayout) itemView.findViewById(R.id.llCartitemContainer5);
            tvCartitemIngredient5 = (TextView) itemView.findViewById(R.id.tvCartitemIngredient5);
            tvCartitemIngredientValue5 = (TextView) itemView.findViewById(R.id.tvCartitemIngredientValue5);
            tvCartitemPrice = (TextView) itemView.findViewById(R.id.tvCartitemPrice);
            addSunCartitemCount = (AddSubView) itemView.findViewById(R.id.addSunCartitemCount);

            tvCartitemDel = (TextView) itemView.findViewById(R.id.tvCartitemDel);

            tvCartitemDel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (NetUtil.isNetworkAvailable(mContext)) {
                        if(isFinish==false){
                            return;
                        }
                        mContext.showLoading();
                        isFinish=false;
                        String delUrl = Configurations.URL_GOODS + "/" + mGoods.get(getLayoutPosition()).getId();
                        com.loopj.android.http.RequestParams params = new com.loopj.android.http.RequestParams();
                        params.put(Configurations.AUTH_TOKEN, UserUtils.getUserInfo().getAuth_token());

                        String device_id= JPushInterface.getRegistrationID(mContext);
                        String timeStamp= TimeUtil.getCurrentTimeString();
                        params.put(Configurations.TIMESTAMP, timeStamp);
                        params.put(Configurations.DEVICE_ID,device_id );

                        Map<String, String> map=new TreeMap<String, String>();
                        map.put(Configurations.AUTH_TOKEN, UserUtils.getUserInfo().getAuth_token());
                        params.put(Configurations.SIGN, SignUtils.createSignString(device_id,timeStamp,map));

                        AsyncHttpClient client = new AsyncHttpClient();
                        client.delete(delUrl, params, new JsonHttpResponseHandler() {
                            @Override
                            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

                                try {
                                    if (response.getInt(Configurations.STATUSCODE) == 200) {
                                        selectGood.remove(mGoods.get(getLayoutPosition()));
                                        allGood.remove(mGoods.get(getLayoutPosition()));
                                        if (mListener != null) {
                                            mListener.onSelectedCount(getSelectGoodCount());
                                            mListener.notifySum(String.valueOf(sum()));
                                        }
                                        remove(mGoods.get(getLayoutPosition()));

                                    } else if (response.getInt(Configurations.STATUSCODE) == 401 || response.getInt(Configurations.STATUSCODE) == 403) {
                                        goLogin();
                                    } else {
                                        notifyDataSetChanged();
                                    }
                                    mContext.hideLoading();
                                    ToastUtil.showShort(mContext, response.getString(Configurations.STATUSMSG));
                                    //动画完成需要一定的时间
                                    handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            isFinish=true;
                                        }
                                    },1000);


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
                        selectGood.put(mGoods.get(getLayoutPosition()), addSunCartitemCount.getText());
                    }

                    if (null != mListener) {
                        mListener.notifySum(String.valueOf(sum()));

                        mListener.onSelectedCount(getSelectGoodCount());
                    }
                    SetAllSelView();
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

    public Map<Good, Integer> getSelectGood() {
        return selectGood;
    }

    private float sum() {
        float sum = 0f;
        if (selectGood.size() > 0) {
            Iterator iter = selectGood.entrySet().iterator();
            while (iter.hasNext()) {
                Map.Entry entry = (Map.Entry) iter.next();
                sum += ((Good) entry.getKey()).getCurrent_price() * (Integer) entry.getValue();
            }
            sum = (float) (Math.round(sum * 100)) / 100;
        }

        return sum;
    }


}
