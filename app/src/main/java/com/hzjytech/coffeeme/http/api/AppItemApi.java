package com.hzjytech.coffeeme.http.api;

import android.content.Context;

import com.hzjytech.coffeeme.configurations.Configurations;
import com.hzjytech.coffeeme.entities.DisplayItems;
import com.hzjytech.coffeeme.entities.Banners;
import com.hzjytech.coffeeme.http.HttpResultFunc;
import com.hzjytech.coffeeme.http.RetrofitSingleton;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by hehongcan on 2017/10/23.
 */

public class AppItemApi {
    //获取商品列表
    public static Observable<DisplayItems> getAppItems(Context context) {
        return RetrofitSingleton.getApiService(context,Configurations.NEW_URL_DOMAIN)
                .getItemList()
                .map(new HttpResultFunc<DisplayItems>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
    //获取首页轮播图
    public static Observable<Banners> getBanners(Context context,int appId) {
        return RetrofitSingleton.getApiService(context,Configurations.NEW_URL_DOMAIN)
                .getBanners(appId)
                .map(new HttpResultFunc<Banners>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
