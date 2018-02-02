package com.hzjytech.coffeeme.http.api;

import android.content.Context;

import com.hzjytech.coffeeme.configurations.Configurations;
import com.hzjytech.coffeeme.entities.AddCart;
import com.hzjytech.coffeeme.entities.NewGoods;
import com.hzjytech.coffeeme.http.HttpResultFunc;
import com.hzjytech.coffeeme.http.RetrofitSingleton;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by hehongcan on 2017/10/24.
 */

public class GoodApi {
    //获取购物车列表
    public static Observable<NewGoods> getGoodCartList(Context context,String token, int page) {
        return RetrofitSingleton.getApiService(context,Configurations.NEW_URL_DOMAIN)
                .getGoodCartList(token,page)
                .map(new HttpResultFunc<NewGoods>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    //添加购物车
    public static Observable<Object> addCart(Context context,String token,int number,String item ) {
        return RetrofitSingleton.getApiService(context,Configurations.NEW_URL_DOMAIN)
                .addCart(token,number,item)
                .map(new HttpResultFunc<Object>())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
