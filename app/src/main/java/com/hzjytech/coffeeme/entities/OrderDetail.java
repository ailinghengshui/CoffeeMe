package com.hzjytech.coffeeme.entities;

import java.util.List;

/**
 * Created by Hades on 2016/5/14.
 */
public class OrderDetail {


    /**
     * id : 1500
     * created_at : 2016-05-14T16:27:14.000+08:00
     * coupon_count : 0.09
     * coupon_info : 直接优惠0.09元
     * description : 卡布奇诺×1
     * fetch_code : 78246851
     * identifier : 2016051416271423606096
     * original_sum : 0.1
     * payment_provider : null
     * status : 0
     * sum : 0.01
     * coupon_id : 18
     * vending_machine_id : null
     * wx_share_link : http://wx.hzjytech.com/coupon/index/?user_id=23&order_id=1500
     * wx_share_title : 人人抢红包，咖啡便宜喝！
     * wx_share_description : 人人都可以获得立减优惠券，机会不容错过！
     * wx_share_pic : http://banners.qiniu.jijiakafei.com/share_pic.png
     * goods : [{"id":4484,"be_token":false,"current_price":0.10000000149011612,"name":"卡布奇诺","original_price":10,"refund":"\u0000","item_id":1,"ingredients":"[{\"material_id\":3, \"name\":\"糖\", \"material_type\":\"Material\", \"value\":3.0, \"water\":78.0, \"sequence\":2, \"display_name\":\"糖\", \"display_value\":\"3.0克\"}, {\"material_id\":4, \"name\":\"咖啡豆\", \"material_type\":\"Material\", \"value\":7, \"water\":50.0, \"sequence\":3, \"display_name\":\"咖啡浓度\", \"display_value\":\"100%\"}, {\"material_id\":3, \"name\":\"糖\", \"material_type\":\"Material\", \"value\":10.4, \"water\":57.0, \"sequence\":2, \"display_name\":\"糖\", \"display_value\":\"10.4克\"}, {\"material_id\":1, \"material_type\":\"Material\", \"name\":\"水\", \"value\":185}, {\"material_id\":2, \"material_type\":\"Material\", \"name\":\"杯子\", \"value\":1}]"}]
     */

    private int id;
    private String created_at;
    private double coupon_count;
    private String coupon_info;
    private String description;
    private String fetch_code;
    private String identifier;
    private double original_sum;
    private Object payment_provider;
    private int status;
    private double sum;
    private int coupon_id;
    private Object vending_machine_id;
    private String wx_share_link;
    private String wx_share_title;
    private String wx_share_description;
    private String wx_share_pic;
    /**
     * id : 4484
     * be_token : false
     * current_price : 0.10000000149011612
     * name : 卡布奇诺
     * original_price : 10
     * refund :  
     * item_id : 1
     * ingredients : [{"material_id":3, "name":"糖", "material_type":"Material", "value":3.0, "water":78.0, "sequence":2, "display_name":"糖", "display_value":"3.0克"}, {"material_id":4, "name":"咖啡豆", "material_type":"Material", "value":7, "water":50.0, "sequence":3, "display_name":"咖啡浓度", "display_value":"100%"}, {"material_id":3, "name":"糖", "material_type":"Material", "value":10.4, "water":57.0, "sequence":2, "display_name":"糖", "display_value":"10.4克"}, {"material_id":1, "material_type":"Material", "name":"水", "value":185}, {"material_id":2, "material_type":"Material", "name":"杯子", "value":1}]
     */

    private List<GoodWithIngredients> goods;

    public void setId(int id) {
        this.id = id;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public void setCoupon_count(double coupon_count) {
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

    public void setPayment_provider(Object payment_provider) {
        this.payment_provider = payment_provider;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public void setSum(double sum) {
        this.sum = sum;
    }

    public void setCoupon_id(int coupon_id) {
        this.coupon_id = coupon_id;
    }

    public void setVending_machine_id(Object vending_machine_id) {
        this.vending_machine_id = vending_machine_id;
    }

    public void setWx_share_link(String wx_share_link) {
        this.wx_share_link = wx_share_link;
    }

    public void setWx_share_title(String wx_share_title) {
        this.wx_share_title = wx_share_title;
    }

    public void setWx_share_description(String wx_share_description) {
        this.wx_share_description = wx_share_description;
    }

    public void setWx_share_pic(String wx_share_pic) {
        this.wx_share_pic = wx_share_pic;
    }

    public void setGoods(List<GoodWithIngredients> goods) {
        this.goods = goods;
    }

    public int getId() {
        return id;
    }

    public String getCreated_at() {
        return created_at;
    }

    public double getCoupon_count() {
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

    public Object getPayment_provider() {
        return payment_provider;
    }

    public int getStatus() {
        return status;
    }

    public double getSum() {
        return sum;
    }

    public int getCoupon_id() {
        return coupon_id;
    }

    public Object getVending_machine_id() {
        return vending_machine_id;
    }

    public String getWx_share_link() {
        return wx_share_link;
    }

    public String getWx_share_title() {
        return wx_share_title;
    }

    public String getWx_share_description() {
        return wx_share_description;
    }

    public String getWx_share_pic() {
        return wx_share_pic;
    }

    public List<GoodWithIngredients> getGoods() {
        return goods;
    }

}
