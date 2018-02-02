package com.hzjytech.coffeeme.collect;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ComponentName;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Base64;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hzjytech.coffeeme.BaseActivity;
import com.hzjytech.coffeeme.Dialogs.BottomSelectDialog;
import com.hzjytech.coffeeme.Dialogs.ClipDialogWithTwoButton;
import com.hzjytech.coffeeme.Dialogs.HintDialog;
import com.hzjytech.coffeeme.R;
import com.hzjytech.coffeeme.authorization.login.LoginActivity;
import com.hzjytech.coffeeme.configurations.Configurations;
import com.hzjytech.coffeeme.configurations.UmengConfig;
import com.hzjytech.coffeeme.entities.AppDosage;
import com.hzjytech.coffeeme.entities.AppDosages;
import com.hzjytech.coffeeme.entities.AppItem;
import com.hzjytech.coffeeme.entities.ComputeAppItem;
import com.hzjytech.coffeeme.entities.Material;
import com.hzjytech.coffeeme.entities.User;
import com.hzjytech.coffeeme.home.FineActivity;
import com.hzjytech.coffeeme.home.NewPaymentActivity;
import com.hzjytech.coffeeme.listeners.IMethod1Listener;
import com.hzjytech.coffeeme.me.ChangeRecipeName;
import com.hzjytech.coffeeme.utils.AppUtil;
import com.hzjytech.coffeeme.utils.DateUtil;
import com.hzjytech.coffeeme.utils.LogUtil;
import com.hzjytech.coffeeme.utils.MyMath;
import com.hzjytech.coffeeme.utils.NetUtil;
import com.hzjytech.coffeeme.utils.SharedPrefUtil;
import com.hzjytech.coffeeme.utils.SignUtils;
import com.hzjytech.coffeeme.utils.TimeUtil;
import com.hzjytech.coffeeme.utils.ToastUtil;
import com.hzjytech.coffeeme.utils.UserUtils;
import com.hzjytech.coffeeme.weibomanager.AccessTokenKeeper;
import com.hzjytech.coffeeme.widgets.CoffeeCupView;
import com.hzjytech.coffeeme.widgets.TitleBar;
import com.hzjytech.coffeeme.widgets.row.RowView;
import com.hzjytech.coffeeme.widgets.row.RowViewDesc;
import com.hzjytech.coffeeme.widgets.switchbutton.SwitchButton;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.sina.weibo.sdk.api.TextObject;
import com.sina.weibo.sdk.api.WeiboMultiMessage;
import com.sina.weibo.sdk.api.share.BaseResponse;
import com.sina.weibo.sdk.api.share.IWeiboHandler;
import com.sina.weibo.sdk.api.share.IWeiboShareAPI;
import com.sina.weibo.sdk.api.share.SendMultiMessageToWeiboRequest;
import com.sina.weibo.sdk.api.share.WeiboShareSDK;
import com.sina.weibo.sdk.auth.AuthInfo;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WeiboAuthListener;
import com.sina.weibo.sdk.constant.WBConstants;
import com.sina.weibo.sdk.exception.WeiboException;
import com.tencent.mm.sdk.modelmsg.SendMessageToWX;
import com.tencent.mm.sdk.modelmsg.WXMediaMessage;
import com.tencent.mm.sdk.modelmsg.WXTextObject;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.umeng.analytics.MobclickAgent;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.common.Callback;
import org.xutils.http.HttpMethod;
import org.xutils.http.RequestParams;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.Event;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.text.DecimalFormat;
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
 * Created by hehongcan on 2017/2/16.
 */
@ContentView(R.layout.activity_detail_recipe)
public class DetailRecipeActivity extends BaseActivity implements IWeiboHandler.Response{
    private static final int REQUEST_CHANGE_NAME = 777;
    private static final int REQUEST_CHANGE_RECIPE = 888;
    private static final  int ADJUST =1 ;
    private static final  int SYSTEM = 2;
    private static final  int  BUY =1 ;
    private static final int ADD_CART = 2;
    private static final float BASE_PRICE = 2.0f;
    private static DetailRecipeActivity mInstance;
    @ViewInject(R.id.titleBar)
    private TitleBar titleBar;
    @ViewInject(R.id.tv_recipe_name)
    private TextView tv_recipe_name;
    @ViewInject(R.id.rv_detail_tab)
    private RecyclerView rv_detail_tab;
    @ViewInject(R.id.iv_coffeecup)
    private CoffeeCupView coffeeCupView;
    @ViewInject(R.id.rv_totle_volume)
    private TextView rv_totle_volume;
    @ViewInject(R.id.rv_totle_price)
    private TextView rv_totle_prece;
    @ViewInject(R.id.rv_change_time)
    private TextView rv_change_time;
    @ViewInject(R.id.switchButton)
    private SwitchButton switchButton;
    @ViewInject(R.id.iv_name_right)
    private ImageView iv_name_right;
    @ViewInject(R.id.rl_change_name)
    private RelativeLayout rl_change_name;
    //收藏界面进入不显示是否收藏
    @ViewInject(R.id.ll_is_show_restore)
    private LinearLayout ll_is_show_restore;
    @ViewInject(R.id.rl_change_receipe)
    private RelativeLayout rl_change_receipe;
    @ViewInject(R.id.rl_guide)
    private RelativeLayout rl_guide;
    @ViewInject(R.id.rl_guide2)
    private RelativeLayout rl_guide2;
    @ViewInject(R.id.tv_guide)
    private TextView tv_guide;
    @ViewInject(R.id.tv_guide2)
    private TextView tv_guide2;
    private IWeiboShareAPI mWeiboShareAPI;
    private IWXAPI api;
    private User user;
    private ArrayList<ComputeAppItem.AppDosagesBean> appDosages;
    //默认收藏
    private boolean isRestore=false;
    private ArrayList<AppDosages> appDosagesForIntent;
    private ArrayList<Material> materials;
    private boolean isFromMyCoffee;
    private int id;
    private ComputeAppItem computeAppItems;
    /**
     * 饮品种类：1、自调 2、系统
     */
    
    private int type=-1;
    private float price;
    private float coffeeWaterRate;
    private String encryptId="#aslkdfjlkajfdljaljdflkjfalkjlkadjfjjdsalfkjdla#";
    private int clickEvent=-1;
    private int coffeeId;
    private int milkId;
    private int sugarId;
    private int chocolateId;
    private ClipboardManager c;
    private ReceipeAdapter recipeAdapter;
    private DecimalFormat df;

    /**
     * 更换名称
     * @param view
     */
    @Event(R.id.rl_change_name)
 private void onChangeNameClick(View view){
        if(!isFromMyCoffee){
            return;
        }
    Intent intent = new Intent(this,ChangeRecipeName.class);
        String name;
        if(type==SYSTEM){
            name=computeAppItems.getName();
        }else{
           name=computeAppItems.getName().replace("(自调)","").replace("(分享)","");
        }
    intent.putExtra("name", name);
    startActivityForResult(intent,REQUEST_CHANGE_NAME);
}

    /**
     * 进入微调修改配方
     * @param view
     */
    @Event(R.id.rl_change_receipe)
    private  void onChangeDetailRecipe(View view){
        //intentAppDosages = (ArrayList<AppDosages>) intent.getSerializableExtra("appDosages");
       // materials = intent.getParcelableArrayListExtra("materials");
        if(type==SYSTEM){
            ToastUtil.showShort(this,"系统饮品暂不支持编辑！");
            return;
        }
        appDosagesForIntent = new ArrayList<>();
        float selfWater=0;
        float water=0;
        for (ComputeAppItem.AppDosagesBean appDosage : appDosages) {
            if(appDosage.getMaterial_name().equals("咖啡豆")){
               selfWater=appDosage.getWeight()*coffeeWaterRate;
                water=appDosage.getWater()-selfWater;
            }else{
                selfWater=0;
                water=appDosage.getWater();
            }
            appDosagesForIntent.add(new AppDosages(appDosage.getId(),appDosage.getWeight(),selfWater,water,appDosage.getSequence(),appDosage.getMaterial_name().equals("咖啡豆")?"咖啡浓度":appDosage.getMaterial_name()));
            //根据sequence排序后传入
            Collections.sort(appDosagesForIntent,new MyComparetor());
        }
        LogUtil.e("appDosages",appDosages.toString());
        LogUtil.e("appDosagesForIntent",appDosagesForIntent.toString());
        Intent intent = new Intent(this, FineActivity.class);
        intent.putExtra("appDosages", appDosagesForIntent);
        intent.putParcelableArrayListExtra("materials",materials);
        intent.putExtra("isFromRecipe",true);
        startActivityForResult(intent,REQUEST_CHANGE_RECIPE);
        this.overridePendingTransition(R.anim.slide_in_right_base,
                R.anim.slide_out_left_base);
    }
    @Event(R.id.btnModulationBuy)
    private void onBuyClick(View view){
        if (!SharedPrefUtil.getLoginType().equals(SharedPrefUtil.LOGINING)) {

            goLogin();
            return;
        }

        if (!NetUtil.isNetworkAvailable(this))
            return;
        //先判断是否是分享过来的配方，再设立flag在配方保存后购买或者加入购物车
        if(isFromMyCoffee){
            buy();
        }else{
            clickEvent=BUY;
            if(isRestore){
                //保存配方,再购买
                restoreCoffee();
                buy();

            }else{
                //直接使用appItemId购买
                buy();
            }
        }

    }

    /**
     * 购买
     */
    private void buy() {
        AppItem appItem = new AppItem();
        appItem.setId(computeAppItems.getId());
        if(!isFromMyCoffee){
            String s = computeAppItems.getName().replace("(自调)", "").replace("(分享)", "") + "(分享)";
            appItem.setName(s);
        }else{
            appItem.setName(computeAppItems.getName());
        }

        appItem.setCurrent_price(Float.valueOf(computeAppItems.getPrice()));
        ArrayList<AppDosage> appDosages = new ArrayList<>();
        for (int i = 0; i < computeAppItems.getApp_dosages().size(); i++) {
            AppDosage appDosage = new AppDosage();
            AppDosage.AppMaterialEntity appMaterialEntity = new AppDosage.AppMaterialEntity();
            appMaterialEntity.setId(computeAppItems.getApp_dosages().get(i).getId());
            appMaterialEntity.setName(computeAppItems.getApp_dosages().get(i).getMaterial_name());
            appDosage.setApp_material(appMaterialEntity);
            appDosage.setId(computeAppItems.getApp_dosages().get(i).getId());
            appDosage.setWater(computeAppItems.getApp_dosages().get(i).getWater());
            appDosage.setWeight(computeAppItems.getApp_dosages().get(i).getWeight());
            appDosages.add(appDosage);
        }

        Intent intent = new Intent(this, NewPaymentActivity.class);
        intent.putExtra("type", 0);
        intent.putExtra("count", 1);
        intent.putExtra("appItem", appItem);
        intent.putParcelableArrayListExtra("appDosages", appDosages);
        startActivity(intent);
    }


    @Event(R.id.btnModulationAddCart)
    private void onCancelClick(View view){
        if (!SharedPrefUtil.getLoginType().equals(SharedPrefUtil.LOGINING)) {

            goLogin();
            return;
        }

        if (!NetUtil.isNetworkAvailable(this))
            return;
        if(isFromMyCoffee){
            addCart();
        }else{
            clickEvent=ADD_CART;
            if(isRestore){
                //保存配方
                restoreCoffee();
                addCart();

            }else{
                addCart();
            }
        }


    }

    private void addCart() {
        LogUtil.e("com",computeAppItems.toString());
        int number=1;
        String auth_token = UserUtils.getUserInfo().getAuth_token();
        JSONArray array = new JSONArray();
        try {
            for (int i = 0; i < appDosages.size(); i++) {
                JSONObject object = new JSONObject();
                object.put("id", appDosages.get(i).getId());
                object.put("weight", appDosages.get(i).getWeight());
                object.put("water", appDosages.get(i).getWater());
                array.put(object);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        String custom_dosages = array.toString();
        RequestParams entity = new RequestParams(Configurations.URL_GOODS);
        entity.addParameter(Configurations.AUTH_TOKEN, auth_token);
        entity.addParameter(Configurations.APP_ITEM_ID, computeAppItems.getId());
        entity.addParameter("custom_dosages", custom_dosages);
        entity.addParameter(Configurations.PRICE, df.format(Float.valueOf(computeAppItems.getPrice())));
        entity.addParameter(Configurations.NUMBER, number);
        if(!isFromMyCoffee){
            entity.addParameter("name",computeAppItems.getName().replace("(自调)","").replace("(分享)","")+"(分享)");
        }
        String device_id= JPushInterface.getRegistrationID(this);
        String timeStamp= TimeUtil.getCurrentTimeString();
        entity.addParameter(Configurations.TIMESTAMP, timeStamp);
        entity.addParameter(Configurations.DEVICE_ID,device_id );

        Map<String, String> map=new TreeMap<String, String>();
        map.put(Configurations.AUTH_TOKEN, auth_token);
        map.put(Configurations.APP_ITEM_ID, String.valueOf(computeAppItems.getId()));
        map.put("custom_dosages", custom_dosages);
        map.put(Configurations.PRICE, String.valueOf(df.format(Float.valueOf(computeAppItems.getPrice()))));
        map.put(Configurations.NUMBER, String.valueOf(number));
        if(!isFromMyCoffee){
           map.put("name",computeAppItems.getName().replace("(自调)","").replace("(分享)","")+"(分享)");
        }
        entity.addParameter(Configurations.SIGN, SignUtils.createSignString(device_id,timeStamp,map));


        x.http().post(entity, new Callback.CommonCallback<JSONObject>() {

            @Override
            public void onSuccess(JSONObject result) {

                checkResOld(result);
                try {

                    if (result.getInt(Configurations.STATUSCODE) == 200) {
                        ToastUtil.showShort(DetailRecipeActivity.this, "加入购物车成功");
                        MobclickAgent.onEvent(DetailRecipeActivity.this, UmengConfig.EVENT_ADD_CART);
                    }else{
                        ToastUtil.showShort(DetailRecipeActivity.this, result.getString(Configurations.STATUSMSG));
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        showLoading();
        initView();
        mWeiboShareAPI = WeiboShareSDK.createWeiboAPI(this, LoginActivity.APP_KEY);
        mWeiboShareAPI.registerApp();
        if (savedInstanceState != null) {
            mWeiboShareAPI.handleWeiboResponse(getIntent(), this);
        }
        api = WXAPIFactory.createWXAPI(this, Configurations.WX_APP_ID);
        c = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
    }
    public static DetailRecipeActivity Instance() {
        if (null == mInstance)
            mInstance = new DetailRecipeActivity();
        return mInstance;
    }

    @Override
    protected void onResume() {
        super.onResume();
        //hideLoading();
    }

    @Override
    protected void onPause() {
        super.onPause();
        hideLoading();
    }

    @Override
    protected void onNewIntent(Intent intent) {
       // hideLoading();
        super.onNewIntent(intent);

    }
    private void initView() {
        mInstance=this;
        initTitle();
        getIntentFromMyCoffee();
        getMaterials();
        isShowNavi();
    }
 //控制导航栏的出现
    private void isShowNavi() {
        if(isFromMyCoffee){
            if(SharedPrefUtil.getIsFirstEnterReceipeFromMy()){
                Window window = getWindow();
                WindowManager.LayoutParams params = window.getAttributes();
                params.systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION|View.SYSTEM_UI_FLAG_IMMERSIVE;
                window.setAttributes(params);
            }else{}
        }else{
            if(SharedPrefUtil.getIsFirstEnterReceipeFromShare()){
                Window window = getWindow();
                WindowManager.LayoutParams params = window.getAttributes();
                params.systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION|View.SYSTEM_UI_FLAG_IMMERSIVE;
                window.setAttributes(params);
            }else{}
        }
    }

    private void restoreCoffee() {
        String auth_token = UserUtils.getUserInfo().getAuth_token();
        JSONObject app_item = new JSONObject();
        try {
            int parent_id= computeAppItems.getParent_id();
            app_item.put("parent_id",parent_id);
            //告诉服务器，来源是哪一单
            app_item.put("source_id",computeAppItems.getId());
            //app_item.put("id", computeAppItems.getId());
            //处理名称，避免携带标注上传到服务器
            String handlerName=computeAppItems.getName();
            if(computeAppItems.getName().contains("(分享)")||computeAppItems.getName().contains("(自调)")){
                handlerName = computeAppItems.getName().replace("(分享)", "");
                handlerName = handlerName.replace("(自调)", "");
            }
            app_item.put("name", handlerName);
            app_item.put("price", computeAppItems.getPrice());
            app_item.put("volume", computeAppItems.getVolume());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        LogUtil.e("appDosages",appDosages.toString());
        JSONArray app_dosages = new JSONArray();
        for (int i = 0; i < appDosages.size(); i++) {
            JSONObject dosage = new JSONObject();
            try {
                switch(appDosages.get(i).getMaterial_name()){
                    case "咖啡豆":
                        dosage.put("id",coffeeId);
                        break;
                    case "糖":
                        dosage.put("id",sugarId);
                        break;
                    case "奶粉":
                        dosage.put("id",milkId);
                        break;
                    case "巧克力粉":
                        dosage.put("id",chocolateId);
                        break;
                }
                dosage.put("weight",appDosages.get(i).getWeight());
                dosage.put("water",appDosages.get(i).getWater());
                dosage.put("sequence",appDosages.get(i).getSequence());
                app_dosages.put(dosage);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        LogUtil.e("dosage", app_dosages.toString());

        if (!TextUtils.isEmpty(auth_token)) {
            RequestParams entity = new RequestParams(Configurations.URL_APP_ITEMS);
            entity.addParameter(Configurations.AUTH_TOKEN, auth_token);
            entity.addParameter("app_item", app_item.toString());
            entity.addParameter("app_dosages", app_dosages.toString());

            entity.addParameter("app_item_type",2);
            String device_id = JPushInterface.getRegistrationID(this);
            String timeStamp = TimeUtil.getCurrentTimeString();
            entity.addParameter(Configurations.TIMESTAMP, timeStamp);
            entity.addParameter(Configurations.DEVICE_ID, device_id);
            entity.addParameter(Configurations.APP_ID,AppUtil.getVersionCode(this));
            Map<String, String> map = new TreeMap<>();
            map.put(Configurations.AUTH_TOKEN, auth_token);
            map.put("app_item", app_item.toString());
            map.put("app_dosages", app_dosages.toString());
           /* if(type==ADJUST){
                map.put("app_item_type", String.valueOf(2));
            }*/
            map.put("app_item_type", String.valueOf(2));
            map.put(Configurations.APP_ID, String.valueOf(AppUtil.getVersionCode(this)));
            entity.addParameter(Configurations.SIGN, SignUtils.createSignString(device_id,timeStamp,map));
            org.xutils.x.http().post(entity, new Callback.CommonCallback<JSONObject>(){
                @Override
                public void onSuccess(JSONObject result) {

                    checkResOld(result);
                    try {
                        if( result.getInt(Configurations.STATUSCODE)!=200){
                            ToastUtil.showShort(DetailRecipeActivity.this, result.getString(Configurations.STATUSMSG));
                            return;
                        }
                        switchButton.setChecked(false);
                        LogUtil.e("result",result.toString());
                        ComputeAppItem changedAppItems = JSON.parseObject(result.getJSONObject("results").getString("app_item"), ComputeAppItem.class);
                        //computeAppItems=changedAppItems;
                        setChangeTime();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onError(Throwable ex, boolean isOnCallback) {
                    showNetError();
                }

                @Override
                public void onCancelled(CancelledException cex) {
                }

                @Override
                public void onFinished() {
                }
            });
        }
    }
    /**
     * 根据id联网获取详细信息
     */
    private void getDataById() {
        LogUtil.e("id",id+"");
        String Url = Configurations.URL_APP_ITEMS + "/" + id;
        com.loopj.android.http.RequestParams params = new com.loopj.android.http.RequestParams();
        //params.put(Configurations.AUTH_TOKEN, UserUtils.getUserInfo().getAuth_token());

        String device_id= JPushInterface.getRegistrationID(this);
        String timeStamp= TimeUtil.getCurrentTimeString();
        params.put(Configurations.TIMESTAMP, timeStamp);
        params.put(Configurations.DEVICE_ID,device_id );

        Map<String ,String > map=new TreeMap<>();
        //map.put(Configurations.AUTH_TOKEN, UserUtils.getUserInfo().getAuth_token());

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
                        appDosages=new ArrayList<ComputeAppItem.AppDosagesBean>();
                        appDosages.addAll(computeAppItems.getApp_dosages());
                        Collections.sort(appDosages,new MyComparetor2());
                        computePrice();
                        initData();
                        hideLoading();
                    }
                   // ToastUtil.showShort(DetailRecipeActivity.this, response.getString(Configurations.STATUSMSG));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 根据获取到的信息重新计算价格，多处涉及到价格的会复用
     */
    private void computePrice() {
        int parent_id = computeAppItems.getParent_id();
        int id = computeAppItems.getId();
        if(parent_id==id){
            type=ADJUST;
        }else{
            type=SYSTEM;
        }
        if(type==SYSTEM){
            computeSystemPrice();
        }else if(type==ADJUST){
            computeAdjustPrice();
        }
    }

    /**
     * 计算自调咖啡的新价格
     * 根据物料与物料价格直接相乘
     */
    private void computeAdjustPrice() {
        List<ComputeAppItem.AppDosagesBean> app_dosages = computeAppItems.getApp_dosages();
        double adjustPrice=0;
        for (ComputeAppItem.AppDosagesBean app_dosage : app_dosages) {
            if(app_dosage.getMaterial_name().equals("咖啡豆")){
                adjustPrice=MyMath.add(adjustPrice+"",app_dosage.getWeight()/10 * app_dosage.getAdjust_price()+"");
            }else{
                adjustPrice=MyMath.add(adjustPrice+"",app_dosage.getWeight() * app_dosage.getAdjust_price()+"");
            }
        }
        price= (float) MyMath.round((BASE_PRICE+adjustPrice),2);
        computeAppItems.setPrice(price+"");
    }

    /**
     * 计算系统咖啡的新价格
     * 1、得到基础总价
     * 2、遍历所有物料与系统物料标准重量对比，不同则乘以sys_price
     * 3、得到新价格
     * 4、设置给currentPrice
     */
    private void computeSystemPrice() {
        double basePrice =MyMath.round(Float.valueOf(computeAppItems.getSys_app_item().getCurrent_price()),2);
        List<ComputeAppItem.AppDosagesBean> baseAppdosages = computeAppItems.getSys_app_item().getApp_dosages();
        List<ComputeAppItem.AppDosagesBean> app_dosages = computeAppItems.getApp_dosages();
        if(app_dosages.size()==1&&app_dosages.get(0).getMaterial_name().equals("咖啡豆")&&app_dosages.get(0).getWeight()<=2.0){
            if(app_dosages.get(0).getWeight()<1.5f){
                basePrice= Float.valueOf(computeAppItems.getSys_app_item().getCurrent_price());
            }else{
                basePrice= Float.valueOf(computeAppItems.getSys_app_item().getPrice());
            }
        }else{
            for (ComputeAppItem.AppDosagesBean baseAppdosage : baseAppdosages) {
                for (ComputeAppItem.AppDosagesBean app_dosage : app_dosages) {
                    if(app_dosage.getMaterial_name().equals(baseAppdosage.getMaterial_name())){
                        if(true){
                            double extraPrice=0;
                            if(app_dosage.getMaterial_name().equals("咖啡豆")){
                                extraPrice= MyMath.mul (MyMath.sub(Double.toString(app_dosage.getWeight()),Double.toString(baseAppdosage.getWeight()))/10f+"" , baseAppdosage.getSys_price()+"");
                            }else{
                                extraPrice= MyMath.mul (MyMath.sub(app_dosage.getWeight()+"",baseAppdosage.getWeight()+"")+"", baseAppdosage.getSys_price()+"");
                            }
                            basePrice+=extraPrice;
                        }
                    }
                }
            }

        }
        basePrice= (float) MyMath.round(basePrice,2);
        computeAppItems.getSys_app_item().setCurrent_price(basePrice+"");
        computeAppItems.setPrice(basePrice+"");
        price = (float) basePrice;
    }

    /**
     * 绑定数据
     */
    private void initData() {
        //设置名称
        String name = computeAppItems.getName();
        if(isFromMyCoffee){
            tv_recipe_name.setText(name);
        }else{
            String replace = name.replace("(自调)", "").replace("(分享)","");
            tv_recipe_name.setText(replace+"(分享)");
        }

        //设置物料
        setDetailRecipe();
        //设置总容量
        setTotleVolume();
        //设置总价格
        setTotlePrice();
        //设置修改时间
        setChangeTime();
        //从收藏界面进入，可修改名称和配方
        if(isFromMyCoffee){
            if(SharedPrefUtil.getIsFirstEnterReceipeFromMy()){
                rl_guide.setVisibility(View.GONE);
            }else{
                rl_guide.setVisibility(View.GONE);
            }
            tv_guide.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    rl_guide.setVisibility(View.GONE);
                    SharedPrefUtil.saveIsFirstEnterReceipeFromMy(false);
                }
            });
            rl_change_name.setClickable(true);
            //ll_change_detail_recipe.setClickable(true);
            iv_name_right.setVisibility(View.VISIBLE);
            //iv_dosage_right.setVisibility(View.VISIBLE);
            ll_is_show_restore.setVisibility(View.GONE);
            rl_change_receipe.setVisibility(View.VISIBLE);
            isRestore=false;
        }else{
            if(SharedPrefUtil.getIsFirstEnterReceipeFromShare()){
                rl_guide2.setVisibility(View.GONE);
            }else{
                rl_guide2.setVisibility(View.GONE);
            }
            tv_guide2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    rl_guide2.setVisibility(View.GONE);
                    SharedPrefUtil.saveIsFirstEnterReceipeFromShare(false);
                }
            });
            rl_change_name.setClickable(false);
            //ll_change_detail_recipe.setClickable(false);
            iv_name_right.setVisibility(View.INVISIBLE);
            //iv_dosage_right.setVisibility(View.INVISIBLE);
            ll_is_show_restore.setVisibility(View.VISIBLE);
            rl_change_receipe.setVisibility(View.GONE);
            isRestore=true;
        }
        initSwitchButton();

    }

    private void initSwitchButton() {
        switchButton.setChecked(true);
        switchButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    isRestore=true;
                }else{
                    isRestore=false;
                }
            }
        });
    }

    private void setChangeTime() {
        String created_at = computeAppItems.getUpdated_at();
        try {
            String date =DateUtil.getYear(DateUtil.ISO8601toCalendar(created_at))+"年"+ DateUtil.getMonth(DateUtil.ISO8601toCalendar(created_at))
                    + "月" + DateUtil.getDay(DateUtil.ISO8601toCalendar(created_at))+"日"+" "
                    +DateUtil.getHour(DateUtil.ISO8601toCalendar(created_at))+":"
                    +DateUtil.getMinute(DateUtil.ISO8601toCalendar(created_at));
            rv_change_time.setText(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

    }

    private void setTotlePrice() {
        rv_totle_prece.setText("¥"+df.format(Float.valueOf(computeAppItems.getPrice())));
    }

    private void setTotleVolume() {
        rv_totle_volume.setText(computeAppItems.getVolume()+"");
    }

    /**
     * 设置物料
     */
    private void setDetailRecipe() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        rv_detail_tab.setLayoutManager(linearLayoutManager);
        int spacingInPixels = getResources().getDimensionPixelSize(R.dimen.receipe_item_margin);
        rv_detail_tab.addItemDecoration(new SpaceItemDecoration(spacingInPixels));
        recipeAdapter=new ReceipeAdapter();
        rv_detail_tab.setAdapter(recipeAdapter);
        ArrayList<AppDosages> newAppDosages = getNewAppDosages();
        LogUtil.e("appDosages",appDosages.toString());
        LogUtil.e("new AppDosages",newAppDosages.toString());
        coffeeCupView.setData(newAppDosages,this);
    }
   private ArrayList<AppDosages> getNewAppDosages(){
       float selfWater=0;
       float water=0;
       ArrayList<AppDosages> newAppDosages = new ArrayList<>();
       for (ComputeAppItem.AppDosagesBean appDosage : this.appDosages) {
           if(appDosage.getMaterial_name().equals("咖啡豆")){
               selfWater=appDosage.getWeight()*coffeeWaterRate;
               water=appDosage.getWater()-selfWater;
           }else{
               selfWater=0;
               water=appDosage.getWater();
           }
           newAppDosages.add(new AppDosages(appDosage.getId(),appDosage.getWeight(),selfWater,water,appDosage.getSequence(),appDosage.getMaterial_name().equals("咖啡豆")?"咖啡浓度":appDosage.getMaterial_name()));
           //根据sequence排序后传入
           Collections.sort(newAppDosages,new MyComparetor());
           Collections.reverse(newAppDosages);
       }
       return newAppDosages;
   }
    /**
     * 获取intent数据
     */
    private void getIntentFromMyCoffee() {
        Intent intent = getIntent();
        id = intent.getIntExtra("id", -1);
        isFromMyCoffee = intent.getBooleanExtra("isFromMyCoffee", false);


    }

    private void getMaterials() {
        RequestParams entity = new RequestParams(Configurations.URL_APP_MATERIALS);
        entity.addParameter(Configurations.APP_ID, AppUtil.getVersionCode(DetailRecipeActivity.this));
        String device_id= JPushInterface.getRegistrationID(DetailRecipeActivity.this);
        String timeStamp= TimeUtil.getCurrentTimeString();
        entity.addParameter(Configurations.TIMESTAMP, timeStamp);
        entity.addParameter(Configurations.DEVICE_ID,device_id );
        Map<String,String> map=new TreeMap<>();
        map.put(Configurations.APP_ID, String.valueOf(AppUtil.getVersionCode(DetailRecipeActivity.this)));
        entity.addParameter(Configurations.SIGN, SignUtils.createSignString(device_id,timeStamp,map));
        x.http().get(entity, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                getDataById();
                parseResult(result);

            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {
                showNetError();
            }

            @Override
            public void onCancelled(CancelledException cex) {

            }

            @Override
            public void onFinished() {

            }
        });
    }
    private void parseResult(String result) {
        try {
            JSONObject results = new JSONObject(result);
            checkResOld(results);
            if (results.getInt(Configurations.STATUSCODE) == 200) {
                materials = new Gson().fromJson(results.getJSONObject("results").getString("app_materials"),
                        new TypeToken<ArrayList<Material>>() {
                        }.getType());
                 LogUtil.e("materials",materials.toString());
                for (Material material : materials) {
                    if(material.getName().equals("咖啡豆")){
                        coffeeWaterRate = (float) material.getWater_rate();
                    }
                }
                for (Material material : materials) {
                    switch(material.getName()){
                        case "咖啡豆":
                            coffeeId =material.getId();
                            break;
                        case "奶粉":
                            milkId =material.getId();
                            break;
                        case "糖":
                            sugarId =material.getId();
                            break;
                        case "巧克力粉":
                            chocolateId =material.getId();
                            break;
                    }
                }
            } else {
                ToastUtil.showShort(this, results.getString(Configurations.STATUSMSG));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void onResponse(BaseResponse baseResponse) {
        switch (baseResponse.errCode) {
            case WBConstants.ErrorCode.ERR_OK:
                ToastUtil.showShort(this, "分享成功");
                break;
            case WBConstants.ErrorCode.ERR_CANCEL:
                ToastUtil.showShort(this, "分享取消");
                break;
            case WBConstants.ErrorCode.ERR_FAIL:
                ToastUtil.showShort(this, "分享失败");
                break;
        }
    }
    private void initTitle() {
        df = new DecimalFormat("0.00");
        titleBar.setTitle(getResources().getString(R.string.str_detail_recipe));
        titleBar.setTitleColor(Color.WHITE);
        titleBar.setLeftImageResource(R.drawable.icon_left);
        titleBar.setLeftClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DetailRecipeActivity.this.finish();
            }
        });
        titleBar.addAction(new TitleBar.Action() {
            @Override
            public String getText() {
                return null;
            }

            @Override
            public int getDrawable() {
                return R.drawable.icon_share_button;
            }

            @Override
            public void performAction(View view) {
                onShareClick();
            }
        });
    }

    /**
     * 保存修改后的配方
     */
    private void changeMyCoffee() {
        String auth_token = UserUtils.getUserInfo().getAuth_token();
        JSONObject app_item = new JSONObject();
        try {
            app_item.put("id", computeAppItems.getId());
            app_item.put("name", computeAppItems.getName());
            app_item.put("price", computeAppItems.getPrice());
            app_item.put("volume", computeAppItems.getVolume());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        LogUtil.e("appDosages",appDosages.toString());
        JSONArray app_dosages = new JSONArray();
        for (int i = 0; i < appDosages.size(); i++) {
            JSONObject dosage = new JSONObject();
            try {
                dosage.put("id",appDosages.get(i).getId());
                dosage.put("weight",appDosages.get(i).getWeight());
                dosage.put("water",appDosages.get(i).getWater());
                dosage.put("sequence",appDosages.get(i).getSequence());
                app_dosages.put(dosage);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    LogUtil.e("Restoredosage", app_dosages.toString());

        if (!TextUtils.isEmpty(auth_token)) {
            RequestParams entity = new RequestParams(Configurations.URL_APP_ITEMS);
            entity.addParameter(Configurations.AUTH_TOKEN, auth_token);
            entity.addParameter("app_item", app_item.toString());
            entity.addParameter("app_dosages", app_dosages.toString());

            String device_id = JPushInterface.getRegistrationID(this);
            String timeStamp = TimeUtil.getCurrentTimeString();
            entity.addParameter(Configurations.TIMESTAMP, timeStamp);
            entity.addParameter(Configurations.DEVICE_ID, device_id);
            entity.addParameter(Configurations.APP_ID,AppUtil.getVersionCode(this));
            Map<String, String> map = new TreeMap<>();
            map.put(Configurations.AUTH_TOKEN, auth_token);
            map.put("app_item", app_item.toString());
            map.put("app_dosages", app_dosages.toString());
            map.put(Configurations.APP_ID, String.valueOf(AppUtil.getVersionCode(this)));
            entity.addParameter(Configurations.SIGN, SignUtils.createSignString(device_id,timeStamp,map));
            x.http().request(HttpMethod.PUT, entity, new Callback.CommonCallback<JSONObject>(){
                @Override
                public void onSuccess(JSONObject result) {
                    checkResOld(result);
                    try {
                        if(result.getInt(Configurations.STATUSCODE)!=200){
                            ToastUtil.showShort(DetailRecipeActivity.this, result.getString(Configurations.STATUSMSG));
                        }else{
                            ToastUtil.showShort(DetailRecipeActivity.this,"配方修改成功");
                        }
                        LogUtil.e("result",result.toString());
                        ComputeAppItem changedAppItems = JSON.parseObject(result.getJSONObject("results").getString("app_item"), ComputeAppItem.class);
                        computeAppItems=changedAppItems;
                        setChangeTime();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onError(Throwable ex, boolean isOnCallback) {
                    showNetError();
                }

                @Override
                public void onCancelled(CancelledException cex) {
                }

                @Override
                public void onFinished() {
                }
            });
        }
    }


    /**
     * 点击分享
     */
    private void onShareClick() {
        String encryptString = new String(Base64.encode(("#CM#"+computeAppItems.getId()).getBytes(),Base64.DEFAULT)).trim();
        LogUtil.e("encryptStirng",encryptString);
        String name = computeAppItems.getName();
        String shareName;
        if(type==SYSTEM){
            shareName=name;
        }else{
           shareName=name.replace("(自调)","").replace("(分享)","");
        }
        encryptId=shareName+"（配方）&"+encryptString+"&";
        int[] images = new int[]{R.drawable.icon_share_weibo,  R.drawable.icon_share_wechat,R.drawable.icon_share_friendcircle};
        String[] titles = new String[]{"新浪微博",  "微信好友","微信朋友圈"};

        final BottomSelectDialog bottomSelectDialog=new BottomSelectDialog();

        bottomSelectDialog.setAdapter(this,images,titles,new GridLayoutManager(this,3));
        // TODO: 2017/2/27 设置剪贴板内容
        bottomSelectDialog.setListener(new IMethod1Listener() {
            @Override
            public void OnMethod1Listener(final int param) {
                final ClipDialogWithTwoButton clipDialogWithTwoButton = ClipDialogWithTwoButton.newInstance("复制这条信息，打开#Coffee Me#，即可查看"+ encryptId);
                clipDialogWithTwoButton.setOnTwoButtonClickListener(new ClipDialogWithTwoButton.OnTwoButtonClickListener() {
                    @Override
                    public void onLeftClick() {
                        clipDialogWithTwoButton.dismiss();
                    }

                    @Override
                    public void onRightClick() {
                        clipDialogWithTwoButton.dismiss();
                        showLoading();
                        switch (param){
                            //新浪微博
                            case 0:
                                shareViaWeibo();
                                break;
                            //微信好友
                            case 1:
                                shareViaWeChat(true);
                                break;
                            //微信朋友圈
                            case 2:
                                shareViaWeChat(false);
                                break;
                        }
                    }});
                clipDialogWithTwoButton.show(DetailRecipeActivity.this.getFragmentManager(),"clipDialog");

                bottomSelectDialog.dismiss();
            }
        });
        bottomSelectDialog.show(getSupportFragmentManager(),"share");

    }
    private void shareViaWeibo() {

        MobclickAgent.onEvent(this, UmengConfig.EVENT_SHAREREFERRALCODE_WEIBO);
        TextObject textObject = new TextObject(); //分享文本
        textObject.text ="复制这条信息，打开#Coffee Me#，即可查看"+ encryptId;
        WeiboMultiMessage msg = new WeiboMultiMessage();
        msg.textObject=textObject;
        SendMultiMessageToWeiboRequest request = new SendMultiMessageToWeiboRequest();
        request.transaction = String.valueOf(System.currentTimeMillis());
        request.multiMessage = msg;

        AuthInfo authInfo = new AuthInfo(this, LoginActivity.APP_KEY, LoginActivity.REDIRECT_URL, LoginActivity.SCOPE);
        Oauth2AccessToken accessToken = AccessTokenKeeper.readAccessToken(getApplicationContext());
        String token = "";
        if (accessToken != null) {
            token = accessToken.getToken();
        }
        mWeiboShareAPI.sendRequest(this, request, authInfo, token, new WeiboAuthListener() {
            @Override
            public void onComplete(Bundle bundle) {
                Oauth2AccessToken newToken = Oauth2AccessToken.parseAccessToken(bundle);
                AccessTokenKeeper.writeAccessToken(getApplicationContext(), newToken);
            }

            @Override
            public void onWeiboException(WeiboException e) {
            }
            @Override
            public void onCancel() {
            }
        });

    }

    private void shareViaWeChat(final boolean isWechat) {
        if (!api.isWXAppInstalled()) {
            //hideLoading();
            HintDialog.newInstance("提示", "手机上没有安装微信", "确定").show(getSupportFragmentManager(), "personInfoHint");
        }


        MobclickAgent.onEvent(DetailRecipeActivity.this, UmengConfig.EVENT_SHAREREFERRALCODE_WECHAT);
                WXTextObject textObj = new WXTextObject();
              if(isWechat){
               /*   c.setPrimaryClip(ClipData.newPlainText("s","复制这条信息，打开#Coffee Me#，即可查看"+ encryptId));
                  final String text=" ";
                  textObj.text=text;
                  WXMediaMessage msg = new WXMediaMessage();
                  msg.mediaObject=textObj;
                  msg.description=text;
                  SendMessageToWX.Req req = new SendMessageToWX.Req();
                  req.transaction = String.valueOf(System.currentTimeMillis());
                  req.message = msg;
                  req.scene = SendMessageToWX.Req.WXSceneSession ;
                  api.sendReq(req);*/
                  c.setPrimaryClip(ClipData.newPlainText("s","复制这条信息，打开#Coffee Me#，即可查看"+ encryptId));
                  Intent intent = new Intent(Intent.ACTION_MAIN);
                  ComponentName cmp = new ComponentName("com.tencent.mm","com.tencent.mm.ui.LauncherUI");

                  intent.addCategory(Intent.CATEGORY_LAUNCHER);
                  intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                  intent.setComponent(cmp);
                  startActivity(intent);
              }else{
                  final String text="复制这条信息，打开#Coffee Me#，即可查看"+encryptId;
                  textObj.text=text;
                  WXMediaMessage msg = new WXMediaMessage();
                  msg.mediaObject=textObj;
                  msg.description=text;
                  SendMessageToWX.Req req = new SendMessageToWX.Req();
                  req.transaction = String.valueOf(System.currentTimeMillis());
                  req.message = msg;
                  req.scene =SendMessageToWX.Req.WXSceneTimeline;
                  api.sendReq(req);
              }

    }
    //一位小数四舍五入
    private double getOneDec(double num){
        int i = (int) Math.round((double)num * 10f);
        double v = i / 10.0f;
        return v;
    }
    private ArrayList<AppDosages> combineNextToMaterial(ArrayList<AppDosages> intentAppDosages) {
        String tag=null;
        List<Integer>removeIndex=new ArrayList<>();
        for(int i=0;i<intentAppDosages.size();i++){
            if(tag!=null&&tag.equals(intentAppDosages.get(i).getMaterial_name())){
                AppDosages appDosage =  intentAppDosages.get(i);
                AppDosages lastAppDosage =  intentAppDosages.get(i - 1);
                lastAppDosage.setWeight((float) getOneDec(lastAppDosage.getWeight()+appDosage.getWeight()));
                lastAppDosage.setWater((float) getOneDec(lastAppDosage.getWater()+appDosage.getWater()));
                lastAppDosage.setSelfWater((float) getOneDec(lastAppDosage.getSelfWater()+appDosage.getSelfWater()));
                removeIndex.add(i);
            }
            tag=intentAppDosages.get(i).getMaterial_name();
        }
        for (int i=0;i<removeIndex.size();i++) {
            intentAppDosages.remove(removeIndex.get(i)-i);
        }
        return intentAppDosages;
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode==REQUEST_CHANGE_NAME&&resultCode==778&&data!=null){
            String changedName = data.getStringExtra("changedName");
            if(isFromMyCoffee){
                changeMyCoffee(changedName);
            }
            // TODO: 2017/2/20 修改配方名称需要在推出页面前提交，也就是说取消情况下也要保存
        }else if(requestCode==REQUEST_CHANGE_RECIPE&&resultCode==2&&data!=null){
            ArrayList<AppDosages> intentAppDosages = (ArrayList<AppDosages>) data.getSerializableExtra("appDosages");
            LogUtil.e("intentAppDosages",intentAppDosages.toString());
            ArrayList<AppDosages> removeAppDosages = combineNextToMaterial(intentAppDosages);
            LogUtil.e("removeAppDosages",removeAppDosages.toString());
            //intentAppDosages.clear();
            //intentAppDosages.addAll(removeAppDosages);
            this.appDosages.clear();
            int index=1;
            for (AppDosages intentAppDosage : removeAppDosages) {
                String name = intentAppDosage.getMaterial_name().equals("咖啡浓度") ? "咖啡豆" : intentAppDosage.getMaterial_name();
                this.appDosages.add(new ComputeAppItem.AppDosagesBean(intentAppDosage.getId(),intentAppDosage.getWeight(), (intentAppDosage.getWater()+intentAppDosage.getSelfWater()),index++,intentAppDosage.getAdjust_price(),0,name));
            }
            Collections.sort(this.appDosages,new MyComparetor2());
            computeAppItems.setApp_dosages(this.appDosages);
            LogUtil.e("changedRecipe>>>",computeAppItems.toString());
            int volume = data.getIntExtra("volume",0);
            computeAppItems.setVolume(volume);
            String price = data.getStringExtra("price");
            computeAppItems.setPrice(price);
            //设置tab和杯子数据
            recipeAdapter.notifyDataSetChanged();
            ArrayList<AppDosages> newAppDosages = getNewAppDosages();
            coffeeCupView.setData(newAppDosages,this);
            setTotleVolume();
            setTotlePrice();
            if(isFromMyCoffee){
                changeMyCoffee();
            }

        }
    }

    /**
     * 修改名称
     * @param changedName
     */
    private void changeMyCoffee(String changedName) {
        String auth_token = UserUtils.getUserInfo().getAuth_token();
        JSONObject app_item = new JSONObject();
        try {
            app_item.put("id", computeAppItems.getId());
            app_item.put("name", changedName);
            app_item.put("price", computeAppItems.getPrice());
            app_item.put("volume", computeAppItems.getVolume());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        LogUtil.e("appDosages",appDosages.toString());

        if (!TextUtils.isEmpty(auth_token)) {
            RequestParams entity = new RequestParams(Configurations.URL_APP_ITEMS);
            entity.addParameter(Configurations.AUTH_TOKEN, auth_token);
            entity.addParameter("app_item", app_item.toString());
            String device_id = JPushInterface.getRegistrationID(this);
            String timeStamp = TimeUtil.getCurrentTimeString();
            entity.addParameter(Configurations.TIMESTAMP, timeStamp);
            entity.addParameter(Configurations.DEVICE_ID, device_id);
            entity.addParameter(Configurations.APP_ID,AppUtil.getVersionCode(this));
            Map<String, String> map = new TreeMap<>();
            map.put(Configurations.AUTH_TOKEN, auth_token);
            map.put("app_item", app_item.toString());
            map.put(Configurations.APP_ID, String.valueOf(AppUtil.getVersionCode(this)));
            entity.addParameter(Configurations.SIGN, SignUtils.createSignString(device_id,timeStamp,map));
            x.http().request(HttpMethod.PUT, entity, new Callback.CommonCallback<JSONObject>(){
                @Override
                public void onSuccess(JSONObject result) {
                    checkResOld(result);
                    try {
                        if(result.getInt(Configurations.STATUSCODE)!=200){
                            ToastUtil.showShort(DetailRecipeActivity.this, result.getString(Configurations.STATUSMSG));
                        }else{
                            ToastUtil.showShort(DetailRecipeActivity.this,"配方名称修改成功");
                        }

                        LogUtil.e("result",result.toString());
                        ComputeAppItem changedAppItems = JSON.parseObject(result.getJSONObject("results").getString("app_item"), ComputeAppItem.class);
                        computeAppItems=changedAppItems;
                        tv_recipe_name.setText(computeAppItems.getName());

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onError(Throwable ex, boolean isOnCallback) {
                    showNetError();
                }

                @Override
                public void onCancelled(CancelledException cex) {
                }

                @Override
                public void onFinished() {
                }
            });
        }
    }
    private class ReceipeAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
        class ItemsViewHolder extends RecyclerView.ViewHolder{
            TextView tvModulationTabName;
            TextView tvModulationTabWeight;

            public ItemsViewHolder(View itemView) {
                super(itemView);
                tvModulationTabName = (TextView) itemView.findViewById(R.id.tvModulationTabName);
                tvModulationTabWeight= (TextView) itemView.findViewById(R.id.tvModulationTabWeight);
            }
        }
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = View.inflate(parent.getContext(), R.layout.layout_receipe_detail_tab, null);
            return new ItemsViewHolder(view);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            ItemsViewHolder holder1 = (ItemsViewHolder) holder;
            if (appDosages.size() == 1&&appDosages.get(0).getMaterial_name().equals("咖啡豆")) {
                holder1.tvModulationTabName.setText("咖啡豆");
                holder1.tvModulationTabWeight.setText((int) appDosages.get(0).getWeight() + "份");
            } else {
                if ("咖啡豆".equals(appDosages.get(position).getMaterial_name())) {
                    holder1.tvModulationTabName.setText("咖啡浓度");
                    holder1.tvModulationTabWeight.setText((int) appDosages.get(position).getWeight() + "%");
                } else {
                    holder1.tvModulationTabName.setText(appDosages.get(position).getMaterial_name());
                    holder1.tvModulationTabWeight.setText( appDosages.get(position).getWeight() + "克");
                }
            }
        }

        @Override
        public int getItemCount() {
            return appDosages.size();
        }
    }
    //设置间距装饰类
    private class SpaceItemDecoration extends RecyclerView.ItemDecoration{

        private int space;

        public SpaceItemDecoration(int space) {
            this.space = space;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {

            if(parent.getChildPosition(view) != 0)
                outRect.left = space;
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
    }  /**
     *自定义排序接口,用于ComputeAppItem.AppDosagesBean排序
     */
    private class MyComparetor2 implements Comparator<ComputeAppItem.AppDosagesBean> {
        @Override
        public int compare(ComputeAppItem.AppDosagesBean b0,ComputeAppItem.AppDosagesBean b1) {
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
