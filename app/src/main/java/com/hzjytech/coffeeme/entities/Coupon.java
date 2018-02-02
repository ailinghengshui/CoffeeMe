package com.hzjytech.coffeeme.entities;

import android.support.annotation.VisibleForTesting;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by Hades on 2016/3/2.
 */
public class Coupon implements Serializable {

    /**
     * identifier : hkicIhB09j3diCpJ2huxwQ
     * owner_id : 18
     * end_time : 1470249038000
     * total_use_num : 1
     * created_at : 1466120034000
     * source : activity-so9essm3iai4JDd66lpsZg
     * used : false
     * title : 打折-活动优惠券-so9essm3iai4JDd66lpsZg
     * start_time : 1466120034000
     * updated_at : 1466120034000
     * use_range : null
     * use_condition : null
     * used_num : 0
     * id : 383
     * value : 85
     * coupon_type : 1
     * machine_ids : null
     * app_item_id : 10
     * point_activity_id : null
     * original_sum : 12
     * sum : 0
     * attribute_group_ids : null
     * app_item_name : 拿铁
     * redeem_activity_id : 35
     * order_id : null
     * sugar:2
     */

    private String identifier;
    private int owner_id;
    @SerializedName(value = "end_time",alternate = "end_date")
    private String end_date;
    private int total_use_num;
    private String created_at;
    private String source;
    private boolean used;
    private String title;
    @SerializedName(value = "start_time",alternate = "start_date")
    private String start_date;
    private String updated_at;
    private Object use_range;
    private Object use_condition;
    private int used_num;
    private int id;
    private String value;
    private int coupon_type;
    private Object machine_ids;
    private int app_item_id;
    private Object point_activity_id;
    private int original_sum;
    private int sum;
    private Object attribute_group_ids;
    private String app_item_name;
    private int redeem_activity_id;
    private Object order_id;
    private int sugar;
    private int valid_days;
    private boolean be_token;
    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public int getOwner_id() {
        return owner_id;
    }

    public void setOwner_id(int owner_id) {
        this.owner_id = owner_id;
    }

    public String getEnd_date() {
        return end_date;
    }

    public void setEnd_Date(String end_time) {
        this.end_date = end_time;
    }

    public int getTotal_use_num() {
        return total_use_num;
    }

    public void setTotal_use_num(int total_use_num) {
        this.total_use_num = total_use_num;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public boolean isUsed() {
        return used;
    }

    public void setUsed(boolean used) {
        this.used = used;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getStart_date() {
        return start_date;
    }

    public void setStart_date(String start_date) {
        this.start_date = start_date;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }

    public Object getUse_range() {
        return use_range;
    }

    public void setUse_range(Object use_range) {
        this.use_range = use_range;
    }

    public Object getUse_condition() {
        return use_condition;
    }

    public void setUse_condition(Object use_condition) {
        this.use_condition = use_condition;
    }

    public int getUsed_num() {
        return used_num;
    }

    public void setUsed_num(int used_num) {
        this.used_num = used_num;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public int getCoupon_type() {
        return coupon_type;
    }

    public void setCoupon_type(int coupon_type) {
        this.coupon_type = coupon_type;
    }

    public Object getMachine_ids() {
        return machine_ids;
    }

    public void setMachine_ids(Object machine_ids) {
        this.machine_ids = machine_ids;
    }

    public int getApp_item_id() {
        return app_item_id;
    }

    public void setApp_item_id(int app_item_id) {
        this.app_item_id = app_item_id;
    }

    public Object getPoint_activity_id() {
        return point_activity_id;
    }

    public void setPoint_activity_id(Object point_activity_id) {
        this.point_activity_id = point_activity_id;
    }

    public int getOriginal_sum() {
        return original_sum;
    }

    public void setOriginal_sum(int original_sum) {
        this.original_sum = original_sum;
    }

    public int getSum() {
        return sum;
    }

    public void setSum(int sum) {
        this.sum = sum;
    }

    public Object getAttribute_group_ids() {
        return attribute_group_ids;
    }

    public void setAttribute_group_ids(Object attribute_group_ids) {
        this.attribute_group_ids = attribute_group_ids;
    }

    public String getApp_item_name() {
        return app_item_name;
    }

    public void setApp_item_name(String app_item_name) {
        this.app_item_name = app_item_name;
    }

    public int getRedeem_activity_id() {
        return redeem_activity_id;
    }

    public void setRedeem_activity_id(int redeem_activity_id) {
        this.redeem_activity_id = redeem_activity_id;
    }

    public Object getOrder_id() {
        return order_id;
    }

    public void setOrder_id(Object order_id) {
        this.order_id = order_id;
    }

    public void setEnd_date(String end_date) {
        this.end_date = end_date;
    }

    public int getSugar() {
        return sugar;
    }

    public void setSugar(int sugar) {
        this.sugar = sugar;
    }

    public int getValid_days() {
        return valid_days;
    }

    public void setValid_days(int valid_days) {
        this.valid_days = valid_days;
    }

    public boolean isBe_token() {
        return be_token;
    }

    public void setBe_token(boolean be_token) {
        this.be_token = be_token;
    }

    @Override
    public String toString() {
        return "Coupon{" + "identifier='" + identifier + '\'' + ", owner_id=" + owner_id + ", " +
                "end_time='" + end_date + '\'' + ", total_use_num=" + total_use_num + ", " +
                "created_at='" + created_at + '\'' + ", source='" + source + '\'' + ", used=" +
                used + ", title='" + title + '\'' + ", start_date='" + start_date + '\'' + ", " +
                "updated_at='" + updated_at + '\'' + ", use_range=" + use_range + ", " +
                "use_condition=" + use_condition + ", used_num=" + used_num + ", id=" + id + ", " +
                "value='" + value + '\'' + ", coupon_type=" + coupon_type + ", machine_ids=" +
                machine_ids + ", app_item_id=" + app_item_id + ", point_activity_id=" +
                point_activity_id + ", original_sum=" + original_sum + ", sum=" + sum + ", " +
                "attribute_group_ids=" + attribute_group_ids + ", app_item_name='" +
                app_item_name + '\'' + ", redeem_activity_id=" + redeem_activity_id + ", " +
                "order_id=" + order_id + '}';
    }
}
