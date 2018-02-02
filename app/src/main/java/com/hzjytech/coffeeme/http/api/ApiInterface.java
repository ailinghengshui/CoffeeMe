package com.hzjytech.coffeeme.http.api;

import com.hzjytech.coffeeme.configurations.Configurations;
import com.hzjytech.coffeeme.entities.AddCart;
import com.hzjytech.coffeeme.entities.CreatOrderBean;
import com.hzjytech.coffeeme.entities.DisplayItems;
import com.hzjytech.coffeeme.entities.Banners;
import com.hzjytech.coffeeme.entities.NewGood;
import com.hzjytech.coffeeme.entities.NewGoods;
import com.hzjytech.coffeeme.entities.NewOrder;
import com.hzjytech.coffeeme.entities.NewOrders;
import com.hzjytech.coffeeme.entities.PackageOrder;
import com.hzjytech.coffeeme.http.HttpResult;

import java.util.List;

import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Query;
import rx.Observable;

public interface ApiInterface {

    /**
     * RxJava形式
     */
    @GET("appItem/list")
    Observable<HttpResult<DisplayItems>> getItemList();

    @GET("banners")
    Observable<HttpResult<Banners>> getBanners(
            @Query(Configurations.APP_ID) int appId
    );
     @GET("good/list")
    Observable<HttpResult<NewGoods>> getGoodCartList(
             @Query(Configurations.TOKEN)String token,
             @Query(Configurations.PAGE)int page
     );
    @FormUrlEncoded
    @POST("good/add")
    Observable<HttpResult<Object>> addCart(
            @Field(Configurations.TOKEN)String token,
            @Field(Configurations.NUMBER)int number,
            @Field(Configurations.ITEM)String item
     );
      @GET("order/getMyOrders")
     Observable<HttpResult<NewOrders>> getOrderList(
             @Query(Configurations.TOKEN) String token,
             @Query(Configurations.STATUS) String status,
             @Query(Configurations.PAGE) int page);
    @GET("order/getOrderDetail")
    Observable<HttpResult<NewOrder>>getOrderDetail(
      @Query(Configurations.TOKEN)String token,
      @Query(Configurations.IDENTIFIER)String identifier
    );
    @POST("order/createOrder")
    Observable<HttpResult<NewOrder>>creatOrder(
            @Header("token")String token,
            @Body CreatOrderBean bean
            );
    @FormUrlEncoded
    @POST("packages/createPackageOrder")
    Observable<HttpResult<PackageOrder>>creatPackageOrder(
            @Field(Configurations.TOKEN)String token,
            @Field(Configurations.PACKAGE_ID)int package_id
            );
}
