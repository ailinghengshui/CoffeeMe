package com.hzjytech.coffeeme.entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Hades on 2016/4/19.
 */
public class Order implements Serializable {

    /**
     * id : 679
     * created_at : 2016-04-11T17:14:30.000+08:00
     * coupon_count : 0
     * coupon_info : 无优惠
     * description : 卡布奇诺×1
     * fetch_code : 97130685
     * identifier : 2016041117143023704507
     * original_sum : 0.10000000149011612
     * payment_provider : null
     * status : 0
     * sum : 0.10000000149011612
     * coupon_id : null
     * vending_machine_id : null
     * goods : [{"id":2636,"be_token":false,"current_price":0.10000000149011612,"name":"卡布奇诺","original_price":10,"refund":"\u0000","item_id":1}]
     */

    private int id;
    private String created_at;
    private int coupon_count;
    private String coupon_info;
    private String description;
    private String fetch_code;
    private String identifier;
    private double original_sum;
    private int payment_provider;
    private int status;
    private double sum;
    private String coupon_id;
    private String vending_machine_id;
    private int point_count;
    private float point_value;
    private int get_point;
    private String wx_share_link;
    private String wx_share_title;
    private String wx_share_description;
    private String wx_share_pic;
    private String share_fetch_code;
    private List<NewGood> goods;

    public Order() {
    }

    public int getGet_point() {
        return get_point;
    }

    public void setGet_point(int get_point) {
        this.get_point = get_point;
    }

    public Order(List<NewGood> goods) {
        this.goods = goods;
    }

    public int getPoint_count() {
        return point_count;
    }

    public void setPoint_count(int point_count) {
        this.point_count = point_count;
    }

    public float getPoint_value() {
        return point_value;
    }

    public void setPoint_value(float point_value) {
        this.point_value = point_value;
    }

    /**
     * id : 2636
     * be_token : false
     * current_price : 0.10000000149011612
     * name : 卡布奇诺
     * original_price : 10
     * refund :  
     * item_id : 1
     */






    public void setId(int id) {
        this.id = id;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public void setCoupon_count(int coupon_count) {
        this.coupon_count = coupon_count;
    }

    public void setCoupon_info(String coupon_info) {
        this.coupon_info = coupon_info;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setFetch_code(String fetch_code) {
        this.fetch_code = fetch_code;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public void setOriginal_sum(double original_sum) {
        this.original_sum = original_sum;
    }

    public void setPayment_provider(int payment_provider) {
        this.payment_provider = payment_provider;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public void setSum(double sum) {
        this.sum = sum;
    }

    public void setCoupon_id(String coupon_id) {
        this.coupon_id = coupon_id;
    }

    public void setVending_machine_id(String vending_machine_id) {
        this.vending_machine_id = vending_machine_id;
    }

    public void setGoods(ArrayList<NewGood> goods) {
        this.goods = goods;
    }

    public int getId() {
        return id;
    }

    public String getCreated_at() {
        return created_at;
    }

    public int getCoupon_count() {
        return coupon_count;
    }

    public String getCoupon_info() {
        return coupon_info;
    }

    public String getDescription() {
        return description;
    }

    public String getFetch_code() {
        return fetch_code;
    }

    public String getIdentifier() {
        return identifier;
    }

    public double getOriginal_sum() {
        return original_sum;
    }

    public int getPayment_provider() {
        return payment_provider;
    }

    public int getStatus() {
        return status;
    }

    public double getSum() {
        return sum;
    }

    public String getCoupon_id() {
        return coupon_id;
    }

    public String getVending_machine_id() {
        return vending_machine_id;
    }

    public String getWx_share_link() {
        return wx_share_link;
    }

    public void setWx_share_link(String wx_share_link) {
        this.wx_share_link = wx_share_link;
    }

    public String getWx_share_title() {
        return wx_share_title;
    }

    public void setWx_share_title(String wx_share_title) {
        this.wx_share_title = wx_share_title;
    }

    public String getWx_share_description() {
        return wx_share_description;
    }

    public void setWx_share_description(String wx_share_description) {
        this.wx_share_description = wx_share_description;
    }

    public String getWx_share_pic() {
        return wx_share_pic;
    }

    public void setWx_share_pic(String wx_share_pic) {
        this.wx_share_pic = wx_share_pic;
    }

    public String getShare_fetch_code() {
        return share_fetch_code;
    }

    public void setShare_fetch_code(String share_fetch_code) {
        this.share_fetch_code = share_fetch_code;
    }

    public List<NewGood> getGoods() {
        return goods;
    }

    @Override
    public String toString() {
        return "Order{" +
                "id=" + id +
                ", created_at='" + created_at + '\'' +
                ", coupon_count=" + coupon_count +
                ", coupon_info='" + coupon_info + '\'' +
                ", description='" + description + '\'' +
                ", fetch_code='" + fetch_code + '\'' +
                ", identifier='" + identifier + '\'' +
                ", original_sum=" + original_sum +
                ", payment_provider=" + payment_provider +
                ", status=" + status +
                ", sum=" + sum +
                ", coupon_id='" + coupon_id + '\'' +
                ", vending_machine_id='" + vending_machine_id + '\'' +
                ", point_count=" + point_count +
                ", point_value=" + point_value +
                ", get_point=" + get_point +
                ", wx_share_link='" + wx_share_link + '\'' +
                ", wx_share_title='" + wx_share_title + '\'' +
                ", wx_share_description='" + wx_share_description + '\'' +
                ", wx_share_pic='" + wx_share_pic + '\'' +
                ", share_fetch_code='" + share_fetch_code + '\'' +
                ", goods=" + goods +
                '}';
    }
}
