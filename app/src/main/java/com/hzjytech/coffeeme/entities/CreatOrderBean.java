package com.hzjytech.coffeeme.entities;

import java.io.Serializable;
import java.util.List;

/**
 * Created by hehongcan on 2017/10/25.
 */

public class CreatOrderBean implements Serializable{
    private String type;
    private DisplayItems.AppItem app_item;
    private int number;
    private String goods;
    private Integer order_id;
    private Integer coupon_id;
    private Integer point;

    public CreatOrderBean(
            String type,
            DisplayItems.AppItem item,
            int number,
            String goods,
            Integer order_id,
            Integer coupon_id,
            Integer point) {
        this.type = type;
        this.app_item = item;
        this.number = number;
        this.goods = goods;
        this.order_id = order_id;
        this.coupon_id = coupon_id;
        this.point = point;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public DisplayItems.AppItem getItem() {
        return app_item;
    }

    public void setItem(DisplayItems.AppItem item) {
        this.app_item = item;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public String getGoods() {
        return goods;
    }

    public void setGoods(String  goods) {
        this.goods = goods;
    }

    public Integer getOrder_id() {
        return order_id;
    }

    public void setOrder_id(Integer order_id) {
        this.order_id = order_id;
    }

    public Integer getCoupon_id() {
        return coupon_id;
    }

    public void setCoupon_id(Integer coupon_id) {
        this.coupon_id = coupon_id;
    }

    public Integer getPoint() {
        return point;
    }

    public void setPoint(Integer point) {
        this.point = point;
    }
}
