package com.hzjytech.coffeeme.http.api;

import android.content.Context;

import com.google.gson.Gson;
import com.hzjytech.coffeeme.configurations.Configurations;
import com.hzjytech.coffeeme.entities.CreatOrderBean;
import com.hzjytech.coffeeme.entities.NewGoods;
import com.hzjytech.coffeeme.entities.NewOrder;
import com.hzjytech.coffeeme.entities.NewOrders;
import com.hzjytech.coffeeme.entities.PackageOrder;
import com.hzjytech.coffeeme.http.HttpResultFunc;
import com.hzjytech.coffeeme.http.RetrofitSingleton;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by hehongcan on 2017/10/25.
 */

public class OrderApi {
    //获取订单列表
    public static Observable<NewOrders> getOrderList(Context context,String token, String status, int page) {
        return RetrofitSingleton.getApiService(context,Configurations.NEW_URL_DOMAIN)
                .getOrderList(token,status,page)
                .map(new HttpResultFunc<NewOrders>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
    //获取订单详情
    public static Observable<NewOrder> getOrderDetail(Context context,String token, String identifier) {
        return RetrofitSingleton.getApiService(context,Configurations.NEW_URL_DOMAIN)
                .getOrderDetail(token,identifier)
                .map(new HttpResultFunc<NewOrder>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
    //生成订单
    public static Observable<NewOrder> creatOrder(Context context,String token,CreatOrderBean bean) {
        return RetrofitSingleton.getApiService(context,Configurations.NEW_URL_DOMAIN)
                .creatOrder(token, bean)
                .map(new HttpResultFunc<NewOrder>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
    //生成订单
    public static Observable<PackageOrder> creatPackageOrder(Context context, String token, int package_id ) {
        return RetrofitSingleton.getApiService(context,Configurations.NEW_URL_DOMAIN)
                .creatPackageOrder(token, package_id)
                .map(new HttpResultFunc<PackageOrder>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
