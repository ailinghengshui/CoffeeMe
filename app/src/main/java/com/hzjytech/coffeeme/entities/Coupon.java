package com.hzjytech.coffeeme.entities;

import android.support.annotation.VisibleForTesting;

import java.io.Serializable;

/**
 * Created by Hades on 2016/3/2.
 */
public class Coupon implements Serializable {


    /**
     * id : 11
     * created_at : 2016-04-05T19:59:09.000+08:00
     * updated_at : 2016-04-06T14:47:29.000+08:00
     * coupon_type : 2
     * end_date : 2016-12-30T00:00:00.000+08:00
     * identifier : Mh1MhQW4wRBd-O2yfXoufQ
     * owner_id : 23
     * start_date : 2016-01-01T00:00:00.000+08:00
     * title : 测试满减优惠券
     * total_use_num : 1
     * use_condition : null
     * use_range : null
     * used : false
     * used_num : 0
     * value : 5-1
     * source : null
     *
     *
     *
     *
     *exchange condition
     *
     *
     */


    /**
     * coupon_type 1:打折 2：满减 3：立减
     */



    private int id;
    private String created_at;
    private String updated_at;
    private int coupon_type;
    private String end_date;
    private String identifier;
    private int owner_id;
    private String start_date;
    private String title;
    private int total_use_num;
    private Object use_condition;
    private Object use_range;
    private boolean used;
    private int used_num;
    private String value;
    private Object source;

    private int exchangepoints;

    public Coupon(){

    }
    public Coupon(int exchangepoints,String title){
        this.coupon_type=3;
        this.exchangepoints=exchangepoints;
        this.title=title;
    }

    public int getExchangepoints() {
        return exchangepoints;
    }

    public void setExchangepoints(int exchangepoints) {
        this.exchangepoints = exchangepoints;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }

    public void setCoupon_type(int coupon_type) {
        this.coupon_type = coupon_type;
    }

    public void setEnd_date(String end_date) {
        this.end_date = end_date;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public void setOwner_id(int owner_id) {
        this.owner_id = owner_id;
    }

    public void setStart_date(String start_date) {
        this.start_date = start_date;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setTotal_use_num(int total_use_num) {
        this.total_use_num = total_use_num;
    }

    public void setUse_condition(Object use_condition) {
        this.use_condition = use_condition;
    }

    public void setUse_range(Object use_range) {
        this.use_range = use_range;
    }

    public void setUsed(boolean used) {
        this.used = used;
    }

    public void setUsed_num(int used_num) {
        this.used_num = used_num;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public void setSource(Object source) {
        this.source = source;
    }

    public int getId() {
        return id;
    }

    public String getCreated_at() {
        return created_at;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public int getCoupon_type() {
        return coupon_type;
    }

    public String getEnd_date() {
        return end_date;
    }

    public String getIdentifier() {
        return identifier;
    }

    public int getOwner_id() {
        return owner_id;
    }

    public String getStart_date() {
        return start_date;
    }

    public String getTitle() {
        return title;
    }

    public int getTotal_use_num() {
        return total_use_num;
    }

    public Object getUse_condition() {
        return use_condition;
    }

    public Object getUse_range() {
        return use_range;
    }

    public boolean isUsed() {
        return used;
    }

    public int getUsed_num() {
        return used_num;
    }

    public String getValue() {
        return value;
    }

    public Object getSource() {
        return source;
    }

    @Override
    public String toString() {
        return "Coupon{" +
                "id=" + id +
                ", created_at='" + created_at + '\'' +
                ", updated_at='" + updated_at + '\'' +
                ", coupon_type=" + coupon_type +
                ", end_date='" + end_date + '\'' +
                ", identifier='" + identifier + '\'' +
                ", owner_id=" + owner_id +
                ", start_date='" + start_date + '\'' +
                ", title='" + title + '\'' +
                ", total_use_num=" + total_use_num +
                ", use_condition=" + use_condition +
                ", use_range=" + use_range +
                ", used=" + used +
                ", used_num=" + used_num +
                ", value='" + value + '\'' +
                ", source=" + source +
                ", exchangepoints=" + exchangepoints +
                '}';
    }
}
