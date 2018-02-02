package com.hzjytech.coffeeme.collect;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Handler;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.hzjytech.coffeeme.R;
import com.hzjytech.coffeeme.configurations.Configurations;
import com.hzjytech.coffeeme.configurations.UmengConfig;
import com.hzjytech.coffeeme.entities.AppDosage;
import com.hzjytech.coffeeme.entities.AppDosages;
import com.hzjytech.coffeeme.entities.AppItem;
import com.hzjytech.coffeeme.entities.AppItems;
import com.hzjytech.coffeeme.entities.ComputeAppItem;
import com.hzjytech.coffeeme.entities.MyCoffeesItem;
import com.hzjytech.coffeeme.home.NewPaymentActivity;
import com.hzjytech.coffeeme.utils.AppUtil;
import com.hzjytech.coffeeme.utils.DateUtil;
import com.hzjytech.coffeeme.utils.LogUtil;
import com.hzjytech.coffeeme.utils.SignUtils;
import com.hzjytech.coffeeme.utils.TimeUtil;
import com.hzjytech.coffeeme.utils.ToastUtil;
import com.hzjytech.coffeeme.utils.UserUtils;
import com.hzjytech.coffeeme.widgets.Dosageview.DosageGroup;
import com.hzjytech.coffeeme.widgets.Dosageview.DosageRowDesc;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import cn.jpush.android.api.JPushInterface;
import cz.msebera.android.httpclient.Header;

/**
 * Created by Hades on 2016/5/22.
 */
public class NewMyCoffeesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_HEADER = 0;
    private static final int VIEW_TYPE_CONTENT = 1;
    private static final int BOOK =2 ;
    private static final int ADD_CHART = 1;
    private static final float BASIC_PRICE = 2.0f;
    private Context mContext;
    private DelListener delListener;
    private boolean isFinish=true;
    public Handler handler=new Handler();
    private int clickEvent;
    private ComputeAppItem computeAppItems;
    private ArrayList<ComputeAppItem.AppDosagesBean> appDosages;

    public interface DelListener{
        void onDelListener();
    }

    public void setDelListener(DelListener delListener){
        this.delListener=delListener;
    }

    private List<MyCoffeesItem> myCoffeesItems;

    public NewMyCoffeesAdapter(Context context, List<AppItems> appItems) {
        this.mContext = context;
        this.myCoffeesItems = cut(formatDate(appItems));
    }

    private List<MyCoffeesItem> cut(List<AppItems> appItems) {
        List<MyCoffeesItem> tempCoffeesItem = new ArrayList<>();

        String date = "#";
        for (AppItems appItem : appItems) {
            Log.e("date",date);
            Log.e("creat",appItem.getCreated_at()+"");
            if (date.equals("#")||!date.substring(0,date.indexOf("日")).equals(appItem.getCreated_at().substring(0,appItem.getCreated_at().indexOf("日")))) {
                date = appItem.getCreated_at();
                tempCoffeesItem.add(new MyCoffeesItem(true, appItem));
            }
            tempCoffeesItem.add(new MyCoffeesItem(false, appItem));
        }
        return tempCoffeesItem;
    }

    private List<AppItems> formatDate(List<AppItems> appItems) {
        try {
            for (AppItems appItem : appItems) {
                String date = DateUtil.getMonth(DateUtil.ISO8601toCalendar(appItem.getCreated_at()))
                        + "月" + DateUtil.getDay(DateUtil.ISO8601toCalendar(appItem.getCreated_at()))+"日"+" "
                        +DateUtil.getHour(DateUtil.ISO8601toCalendar(appItem.getCreated_at()))+":"
                        +DateUtil.getMinute(DateUtil.ISO8601toCalendar(appItem.getCreated_at()));

                appItem.setCreated_at(date);
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return appItems;
    }

    @Override
    public int getItemViewType(int position) {
        return myCoffeesItems.get(position).isHeader() ? VIEW_TYPE_HEADER : VIEW_TYPE_CONTENT;
    }

    public void addRefreshData(List<AppItems> appItems) {
        if (null != appItems && appItems.size() > 0) {
            myCoffeesItems.clear();
            myCoffeesItems.addAll(cut(formatDate(appItems)));
            notifyDataSetChanged();
        } else {
            LogUtil.d("NewMyCoffeesAdapter", "size: " + appItems.size());
        }
    }

    public void addMoreData(List<AppItems> appItems) {
        if (null != appItems && appItems.size() > 0) {
            List<MyCoffeesItem> tempData = cut(formatDate(appItems));
            if (myCoffeesItems.get(myCoffeesItems.size() - 1).getAppItems().getCreated_at().equals(tempData.get(0).getAppItems().getCreated_at())) {
                tempData.remove(0);
            }
            myCoffeesItems.addAll(tempData);
            notifyDataSetChanged();
        } else {
            LogUtil.d("NewMyCoffeesAdapter", "size: " + appItems.size());
        }
    }

    private void removeItem(int position) {

        if (position != -1 && position < myCoffeesItems.size()) {
            if (position == myCoffeesItems.size() - 1) {
                if (myCoffeesItems.get(position - 1).isHeader()) {
                    myCoffeesItems.remove(position);
                    myCoffeesItems.remove(position - 1);
                    notifyItemRemoved(position);
                    notifyItemRemoved(position - 1);
                } else {
                    myCoffeesItems.remove(position);
                    notifyItemRemoved(position);
                }
            } else if (myCoffeesItems.get(position - 1).isHeader() && myCoffeesItems.get(position + 1).isHeader()) {
                myCoffeesItems.remove(position);
                myCoffeesItems.remove(position - 1);
                notifyItemRemoved(position);
                notifyItemRemoved(position - 1);
            } else {
                myCoffeesItems.remove(position);
                notifyItemRemoved(position);
            }

        } else {
            notifyDataSetChanged();
        }

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;
        if (viewType == VIEW_TYPE_HEADER) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.mycoffees_header, parent, false);
            return new HeaderViewHolder(view);
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.mycoffees_content, parent, false);
            return new ContentViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        final MyCoffeesItem item = myCoffeesItems.get(position);

        if (item.isHeader()) {
            ((HeaderViewHolder) holder).bindItem(item.getAppItems().getCreated_at());
        } else {
            ((ContentViewHolder) holder).bindContent(item.getAppItems());
            ((ContentViewHolder) holder).setListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    genOrder(item.getAppItems());
                }
            });

        }

    }
    /**
     * 根据id实时获取价格，因为时间过长，价格可能会发生变化
     * @param id
     * @param position
     */
    private void getCurrentPriceById(int id, final int position) {
        LogUtil.e("id",id+"");
        String Url = Configurations.URL_APP_ITEMS + "/" + id;
        com.loopj.android.http.RequestParams params = new com.loopj.android.http.RequestParams();
        params.put(Configurations.AUTH_TOKEN, UserUtils.getUserInfo().getAuth_token());

        String device_id= JPushInterface.getRegistrationID(mContext);
        String timeStamp= TimeUtil.getCurrentTimeString();
        params.put(Configurations.TIMESTAMP, timeStamp);
        params.put(Configurations.DEVICE_ID,device_id );
        Map<String ,String > map=new TreeMap<>();
        map.put(Configurations.AUTH_TOKEN, UserUtils.getUserInfo().getAuth_token());

        params.put(Configurations.SIGN, SignUtils.createSignString(device_id,timeStamp,map));

        AsyncHttpClient client = new AsyncHttpClient();
        client.get(Url, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

                try {
                    if (response.getInt(Configurations.STATUSCODE) == 200) {
                        LogUtil.e("result",response.getJSONObject("results").toString());
                        computeAppItems = JSON.parseObject(response.getJSONObject("results").getString("app_item"), ComputeAppItem.class);
                        LogUtil.e("compute_appitems>>>", computeAppItems.toString());
                        appDosages =new ArrayList<ComputeAppItem.AppDosagesBean>();
                        appDosages.addAll(computeAppItems.getApp_dosages());
                        float price=computePrice();
                        LogUtil.e("price",price+"");
                        if(clickEvent==ADD_CHART){
                            addToChart(price,position);
                        }else if(clickEvent==BOOK){
                            book(price,position);
                        }
                    }
                    //ToastUtil.showShort(getActivity(), response.getString(Configurations.STATUSMSG));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 根据获取到的信息重新计算价格，多处涉及到价格的会复用
     */
    private float computePrice() {
        int parent_id = computeAppItems.getParent_id();
        int id = computeAppItems.getId();
        float price;
        if(parent_id==id){
            price= computeAdjustPrice()+BASIC_PRICE;
        }else{
            price= computeSystemPrice();
        }
        return price;
    }

    /**
     * 计算自调咖啡的新价格
     * 根据物料与物料价格直接相乘
     */
    private float computeAdjustPrice() {
        List<ComputeAppItem.AppDosagesBean> app_dosages = computeAppItems.getApp_dosages();
        float adjustPrice=0;
        for (ComputeAppItem.AppDosagesBean app_dosage : app_dosages) {
            float singlePrice = app_dosage.getMaterial_name().equals("咖啡豆") ? (app_dosage.getWeight() * app_dosage.getAdjust_price() / 10f) : (app_dosage.getWeight() * app_dosage.getAdjust_price());
            adjustPrice+=singlePrice;
        }
        return adjustPrice;
    }

    /**
     * 计算系统咖啡的新价格
     * 1、得到基础总价
     * 2、遍历所有物料与系统物料标准重量对比，不同则乘以sys_price
     * 3、得到新价格
     * 4、设置给currentPrice
     */
    private float computeSystemPrice() {
        float basePrice =Float.valueOf(computeAppItems.getSys_app_item().getCurrent_price());
        List<ComputeAppItem.AppDosagesBean> baseAppdosages = computeAppItems.getSys_app_item().getApp_dosages();
        List<ComputeAppItem.AppDosagesBean> app_dosages = computeAppItems.getApp_dosages();
        for (ComputeAppItem.AppDosagesBean baseAppdosage : baseAppdosages) {
            for (ComputeAppItem.AppDosagesBean app_dosage : app_dosages) {
                if(app_dosage.getId()==baseAppdosage.getId()){
                    if(app_dosage.getWeight()>baseAppdosage.getWeight()){
                        float extraPrice = (float) ((app_dosage.getWeight() - baseAppdosage.getWeight()) * baseAppdosage.getSys_price());
                        basePrice+=extraPrice;
                    }
                }
            }
        }
        computeAppItems.getSys_app_item().setCurrent_price(basePrice+"");
        return basePrice;
    }
    /**
     * 先计算价格再加入购物车，此处注意方法调用顺序与区间
     * 加入购物车
     */
    public  void addToChart(float price, int position){
        int number=1;
        AppItem appItem = new AppItem();
        appItem.setId(myCoffeesItems.get(position).getAppItems().getId());
        appItem.setName(myCoffeesItems.get(position).getAppItems().getName());
        appItem.setPrice(price);
        ArrayList<AppDosage> appDosages = new ArrayList<>();
        for (int i = 0; i < myCoffeesItems.get(position).getAppItems().getApp_dosages().size(); i++) {
            AppDosage appDosage = new AppDosage();
            AppDosage.AppMaterialEntity appMaterialEntity = new AppDosage.AppMaterialEntity();
            appMaterialEntity.setId(myCoffeesItems.get(position).getAppItems().getApp_dosages().get(i).getId());
            appMaterialEntity.setName(myCoffeesItems.get(position).getAppItems().getApp_dosages().get(i).getMaterial_name());
            appDosage.setApp_material(appMaterialEntity);
            appDosage.setId(myCoffeesItems.get(position).getAppItems().getApp_dosages().get(i).getId());
            appDosage.setWater(myCoffeesItems.get(position).getAppItems().getApp_dosages().get(i).getWater());
            appDosage.setWeight(myCoffeesItems.get(position).getAppItems().getApp_dosages().get(i).getWeight());
            appDosages.add(appDosage);
        }
        String auth_token = UserUtils.getUserInfo().getAuth_token();
        int item_id = appItem.getId();
        JSONArray array = new JSONArray();
        try {
            for (int i = 0; i < appDosages.size(); i++) {
                JSONObject object = new JSONObject();
                object.put("id", appDosages.get(i).getId());
                object.put("weight", appDosages.get(i).getWeight());
//                object.put("weight", getWeightFromText(tabModulationDosage.getTabAt(i).getText().toString().trim()));
                object.put("water", appDosages.get(i).getWater());
                array.put(object);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        String custom_dosages = array.toString();
        //float price = appItem.getCurrent_price();
        RequestParams entity = new RequestParams(Configurations.URL_GOODS);
        entity.addParameter(Configurations.AUTH_TOKEN, auth_token);
        entity.addParameter(Configurations.APP_ITEM_ID, item_id);
        entity.addParameter("custom_dosages", custom_dosages);
        entity.addParameter(Configurations.PRICE, price);
        entity.addParameter(Configurations.NUMBER, number);

        String device_id= JPushInterface.getRegistrationID(mContext);
        String timeStamp= TimeUtil.getCurrentTimeString();
        entity.addParameter(Configurations.TIMESTAMP, timeStamp);
        entity.addParameter(Configurations.DEVICE_ID,device_id );

        final Map<String, String> map=new TreeMap<String, String>();
        map.put(Configurations.AUTH_TOKEN, auth_token);
        map.put(Configurations.APP_ITEM_ID, String.valueOf(item_id));
        map.put("custom_dosages", custom_dosages);
        map.put(Configurations.PRICE, String.valueOf(price));
        map.put(Configurations.NUMBER, String.valueOf(number));
        entity.addParameter(Configurations.SIGN, SignUtils.createSignString(device_id,timeStamp,map));


        x.http().post(entity, new Callback.CommonCallback<JSONObject>() {

            @Override
            public void onSuccess(JSONObject result) {
                try {
                    ToastUtil.showShort(mContext, result.getString(Configurations.STATUSMSG));
                    if (result.getInt(Configurations.STATUSCODE) == 200) {
                        LogUtil.e("result",result.toString());
                        MobclickAgent.onEvent(mContext, UmengConfig.EVENT_ADD_CART);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });
    }

    /**
     * 点击进入详情页
     * @param position
     */
    private void toDetailRecipe(int position){
        int id = myCoffeesItems.get(position).getAppItems().getId();
        Intent intent = new Intent(mContext, DetailRecipeActivity.class);
        intent.putExtra("id",id);
        intent.putExtra("isFromMyCoffee",true);
        mContext.startActivity(intent);

    }
    /**
     * 立即购买
     */
    private void book(float price, int position) {
        AppItem appItem = new AppItem();
        appItem.setId(myCoffeesItems.get(position).getAppItems().getId());
        appItem.setName(myCoffeesItems.get(position).getAppItems().getName());
        appItem.setCurrent_price(price);
        ArrayList<AppDosage> appDosages = new ArrayList<>();
        for (int i = 0; i < myCoffeesItems.get(position).getAppItems().getApp_dosages().size(); i++) {
            AppDosage appDosage = new AppDosage();
            AppDosage.AppMaterialEntity appMaterialEntity = new AppDosage.AppMaterialEntity();
            appMaterialEntity.setId(myCoffeesItems.get(position).getAppItems().getApp_dosages().get(i).getId());
            appMaterialEntity.setName(myCoffeesItems.get(position).getAppItems().getApp_dosages().get(i).getMaterial_name());
            appDosage.setApp_material(appMaterialEntity);
            appDosage.setId(myCoffeesItems.get(position).getAppItems().getApp_dosages().get(i).getId());
            appDosage.setWater(myCoffeesItems.get(position).getAppItems().getApp_dosages().get(i).getWater());
            appDosage.setWeight(myCoffeesItems.get(position).getAppItems().getApp_dosages().get(i).getWeight());
            appDosages.add(appDosage);
        }

        Intent intent = new Intent(mContext, NewPaymentActivity.class);
        intent.putExtra("type", 0);
        intent.putExtra("count", 1);
        intent.putExtra("appItem", appItem);
        intent.putParcelableArrayListExtra("appDosages", appDosages);
        mContext.startActivity(intent);

    }

    private void delOrder(final int position) {
        isFinish=false;
        String delUrl = Configurations.URL_APP_ITEMS + "/" + myCoffeesItems.get(position).getAppItems().getId();
        com.loopj.android.http.RequestParams params = new com.loopj.android.http.RequestParams();
        params.put(Configurations.AUTH_TOKEN, UserUtils.getUserInfo().getAuth_token());

        String device_id= JPushInterface.getRegistrationID(mContext);
        String timeStamp= TimeUtil.getCurrentTimeString();
        params.put(Configurations.TIMESTAMP, timeStamp);
        params.put(Configurations.DEVICE_ID,device_id );

        Map<String ,String > map=new TreeMap<>();
        map.put(Configurations.AUTH_TOKEN, UserUtils.getUserInfo().getAuth_token());

        params.put(Configurations.SIGN, SignUtils.createSignString(device_id,timeStamp,map));

        AsyncHttpClient client = new AsyncHttpClient();
        client.delete(delUrl, params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {

                try {
                    if (response.getInt(Configurations.STATUSCODE) == 200) {
                        ToastUtil.showShort(mContext, "删除成功");
                        removeItem(position);
                        if(getItemCount()==0){
                            if(delListener!=null){
                                delListener.onDelListener();
                            }
                        }
                    }else{
                        ToastUtil.showShort(mContext, response.getString(Configurations.STATUSMSG));
                    }

                } catch (JSONException e) {

                    e.printStackTrace();
                }
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        isFinish=true;
                    }
                },1000);

                NewMyCoffeesActivity.Instance().hideLoading();
            }
        });
    }

    private void genOrder(AppItems appItems) {
        AppItem appItem = new AppItem();
        appItem.setId(appItems.getId());
        appItem.setName(appItems.getName());
        appItem.setCurrent_price(appItems.getPrice());
        ArrayList<AppDosage> appDosages = new ArrayList<>();
        for (int i = 0; i < appItems.getApp_dosages().size(); i++) {
            AppDosage appDosage = new AppDosage();
            AppDosage.AppMaterialEntity appMaterialEntity = new AppDosage.AppMaterialEntity();
            appMaterialEntity.setId(appItems.getApp_dosages().get(i).getId());
            appMaterialEntity.setName(appItems.getApp_dosages().get(i).getMaterial_name());
            appDosage.setApp_material(appMaterialEntity);
            appDosage.setId(appItems.getApp_dosages().get(i).getId());
            appDosage.setWater(appItems.getApp_dosages().get(i).getWater());
            appDosage.setWeight(appItems.getApp_dosages().get(i).getWeight());
            appDosages.add(appDosage);
        }

        Intent intent = new Intent(mContext, NewPaymentActivity.class);
        intent.putExtra("type", 0);
        intent.putExtra("count", 1);
        intent.putExtra("appItem", appItem);
        intent.putParcelableArrayListExtra("appDosages", appDosages);
        mContext.startActivity(intent);

    }

    @Override
    public int getItemCount() {
        return myCoffeesItems.size();
    }

    public boolean isHeader(int position) {
        return myCoffeesItems.get(position).isHeader();
    }

    private static class HeaderViewHolder extends RecyclerView.ViewHolder {

        private final TextView tvMycoffeesitemHeader;

        public HeaderViewHolder(View itemView) {
            super(itemView);
            tvMycoffeesitemHeader = (TextView) itemView.findViewById(R.id.tvMycoffeesitemHeader);
        }

        public void bindItem(String text) {
            tvMycoffeesitemHeader.setText(text);
        }
    }

    private class ContentViewHolder extends RecyclerView.ViewHolder {

        private  DosageGroup dosggrpMycoffeeitemDosages;
        private  TextView tvMycoffeeitemName;
        private TextView tvMycoffeeitemDate;
        private ImageView ivMycoffeeitemDel;
        private  TextView btnMyCoffeeitem;
        private  TextView tv_add_chart;
        private LinearLayout ll_to_detail;
        private View.OnClickListener listener;
        private View.OnClickListener delListener;

        public ContentViewHolder(View itemView) {
            super(itemView);
            tvMycoffeeitemName = (TextView) itemView.findViewById(R.id.tvMycoffeeitemName);
            tvMycoffeeitemDate = (TextView) itemView.findViewById(R.id.tvMycoffeeitemDate);
            ivMycoffeeitemDel = (ImageView) itemView.findViewById(R.id.ivMycoffeeitemDel);
            dosggrpMycoffeeitemDosages = (DosageGroup) itemView.findViewById(R.id.dosggrpMycoffeeitemDosages);
            btnMyCoffeeitem = (TextView) itemView.findViewById(R.id.btnMyCoffeeitem);
            ll_to_detail= (LinearLayout) itemView.findViewById(R.id.ll_to_detail);
            tv_add_chart = (TextView) itemView.findViewById(R.id.btnMyCoffeeAddChart);
            ivMycoffeeitemDel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(AppUtil.isFastClick()){
                        return;
                    }
                    if(isFinish==false){
                        return;
                    }
                    NewMyCoffeesActivity.Instance().showLoading();
                    delOrder(getLayoutPosition());
                }
            });
            btnMyCoffeeitem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getLayoutPosition();
                    int id = myCoffeesItems.get(position).getAppItems().getId();
                    clickEvent =BOOK;
                    getCurrentPriceById(id,position);

                }

            });
            ll_to_detail.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getLayoutPosition();
                    toDetailRecipe(position);
                }
            });
            tv_add_chart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getLayoutPosition();
                    int id = myCoffeesItems.get(position).getAppItems().getId();
                    clickEvent =ADD_CHART;
                    getCurrentPriceById(id,position);
                }
            });
        }

        public void setListener(View.OnClickListener listener) {
            this.listener = listener;
        }

        public void setDelListener(View.OnClickListener listener) {
            this.delListener = listener;
        }

        public void bindContent(final AppItems appItems) {
            tvMycoffeeitemName.setText(appItems.getName());
            //Typeface face = Typeface.createFromAsset (mContext.getAssets(),"fonts/qk.ttf" );
           // btnMyCoffeeitem.setTypeface (face);
           // tv_add_chart.setTypeface(face);
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(appItems.getCreated_at());
            tvMycoffeeitemDate.setText(stringBuilder.toString());

            dosggrpMycoffeeitemDosages.removeAllViews();
            List<DosageRowDesc> couponRowDescs = new ArrayList<>();
            couponRowDescs.clear();

            if (appItems.getApp_dosages().size() == 1&&"咖啡豆".equals(appItems.getApp_dosages().get(0).getMaterial_name())&&appItems.getApp_dosages().get(0).getWeight()<=2) {
                couponRowDescs.add(new DosageRowDesc(appItems.getApp_dosages().get(0).getMaterial_name(), appItems.getApp_dosages().get(0).getWeight(), "份"));
            } else if (appItems.getApp_dosages().size() > 1) {
                List<AppDosages> app_dosages = appItems.getApp_dosages();
                Collections.sort(app_dosages,new MyComparetor());
                for (int i = 0; i < appItems.getApp_dosages().size(); i++) {
                    if ("咖啡豆".equals(app_dosages.get(i).getMaterial_name())) {
                        couponRowDescs.add(new DosageRowDesc("咖啡浓度", app_dosages.get(i).getWeight(), "%"));
                    } else {
                        couponRowDescs.add(new DosageRowDesc(app_dosages.get(i).getMaterial_name(),app_dosages.get(i).getWeight(), "克"));
                    }
                }
            } else if(appItems.getApp_dosages().size() == 1){
                if ("咖啡豆".equals(appItems.getApp_dosages().get(0).getMaterial_name())) {
                    couponRowDescs.add(new DosageRowDesc("咖啡浓度", appItems.getApp_dosages().get(0).getWeight(), "%"));
                }else{
                    couponRowDescs.add(new DosageRowDesc(appItems.getApp_dosages().get(0).getMaterial_name(), appItems.getApp_dosages().get(0).getWeight(), "克"));
                }
            }else {
                couponRowDescs.add(new DosageRowDesc("数据异常", 0, ""));
            }
            dosggrpMycoffeeitemDosages.setData(couponRowDescs);
        }
    }
    /**
     *自定义排序接口
     */
    private class MyComparetor implements Comparator<AppDosages> {
        @Override
        public int compare(AppDosages b0, AppDosages b1) {
            if(b0.getSequence()>b1.getSequence()){
                return 1;
            }else if(b0.getSequence()<b1.getSequence()){
                return -1;
            }else{
                return 0;
            }
        }
    }
}