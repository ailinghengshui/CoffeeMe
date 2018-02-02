package com.hzjytech.coffeeme.entities;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by hehongcan on 2017/10/25.
 */

public class NewOrder implements Serializable{

    /**
     * identifier : 20171025105858590039346
     * coupon_count : 0.0
     * fetch_code : 12750210
     * description : 卡布奇诺×1
     * original_sum : 7.2
     * created_at : 2017-10-25 11:05:25.9
     * goods : [{"item":{"image_url":"http://coffees.qiniu.jijiakafei
     * .com/sI6QJdDF-%E5%8D%A1%E5%B8%83%E5%A5%87%E8%AF%BA
     * .png?e=1508904325&token=2VQYwL7KbVJwSfBnILVDSpaGbE7F4qB_7zV6lltd
     * :vRG5e3EyHVVf0FcgAeoDYo0gH-4=","isIced":false,"description":"咖啡/牛奶/奶泡=
     * 1：1：1\r\n醇正浓郁·口感丰盈","active":true,"buy_enable":true,"nameEn":"Cappuccino",
     * "activity_url_web":"http://coffees.qiniu.jijiakafei
     * .com/gtmEbCBM-D8QEppmB-%E5%8D%A1%E5%B8%83%E5%A5%87%E8%AF%BA
     * .png?e=1508904325&token=2VQYwL7KbVJwSfBnILVDSpaGbE7F4qB_7zV6lltd:g_dn4eZMeopmGf
     * -sPqdRWvIMky4=","volume":260,"price":12,"image_key":"gtmEbCBM-D8QEppmB-卡布奇诺.png",
     * "name":"卡布奇诺","id":1,"current_price":7.2,"sugar":1,"app_image":"sI6QJdDF-卡布奇诺.png"},
     * "be_token":false,"created_at":"2017-10-25 10:58:58","id":136764,"sugar":null}]
     * sum : 7.2
     * vending_machine_id : null
     * coupon_info : 无优惠
     * coupon_id : null
     * payment_provider : 3
     * id : 53192
     * status : 1
     * new_link：//新接口用于兼容url分享的字段
     * package_info
     */

    private String identifier;
    private String share_fetch_code;
    private String wx_share_title;
    private double coupon_count;
    private String fetch_code;
    private String description;
    private double original_sum;
    private String created_at;
    private double sum;
    private double get_point;
    private String wx_share_pic;
    private int vending_machine_id;
    private String wx_share_link;
    private String coupon_info;
    private int point_count;
    private int coupon_id;
    private double point_value;
    private int payment_provider;
    private String wx_share_description;
    private int id;
    private int status;
    private List<NewGood> goods;
    private  String new_link;
    private PackageInfo package_info;


    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getShare_fetch_code() {
        return share_fetch_code;
    }

    public void setShare_fetch_code(String share_fetch_code) {
        this.share_fetch_code = share_fetch_code;
    }

    public String getWx_share_title() {
        return wx_share_title;
    }

    public void setWx_share_title(String wx_share_title) {
        this.wx_share_title = wx_share_title;
    }

    public double getCoupon_count() {
        return coupon_count;
    }

    public void setCoupon_count(double coupon_count) {
        this.coupon_count = coupon_count;
    }

    public String getFetch_code() {
        return fetch_code;
    }

    public void setFetch_code(String fetch_code) {
        this.fetch_code = fetch_code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getOriginal_sum() {
        return original_sum;
    }

    public void setOriginal_sum(double original_sum) {
        this.original_sum = original_sum;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public double getSum() {
        return sum;
    }

    public void setSum(double sum) {
        this.sum = sum;
    }

    public double getGet_point() {
        return get_point;
    }

    public void setGet_point(double get_point) {
        this.get_point = get_point;
    }

    public String getWx_share_pic() {
        return wx_share_pic;
    }

    public void setWx_share_pic(String wx_share_pic) {
        this.wx_share_pic = wx_share_pic;
    }

    public int getVending_machine_id() {
        return vending_machine_id;
    }

    public void setVending_machine_id(int vending_machine_id) {
        this.vending_machine_id = vending_machine_id;
    }

    public String getWx_share_link() {
        return wx_share_link;
    }

    public void setWx_share_link(String wx_share_link) {
        this.wx_share_link = wx_share_link;
    }

    public String getCoupon_info() {
        return coupon_info;
    }

    public void setCoupon_info(String coupon_info) {
        this.coupon_info = coupon_info;
    }

    public int getPoint_count() {
        return point_count;
    }

    public void setPoint_count(int point_count) {
        this.point_count = point_count;
    }

    public int getCoupon_id() {
        return coupon_id;
    }

    public void setCoupon_id(int coupon_id) {
        this.coupon_id = coupon_id;
    }

    public double getPoint_value() {
        return point_value;
    }

    public void setPoint_value(double point_value) {
        this.point_value = point_value;
    }

    public int getPayment_provider() {
        return payment_provider;
    }

    public void setPayment_provider(int payment_provider) {
        this.payment_provider = payment_provider;
    }

    public String getWx_share_description() {
        return wx_share_description;
    }

    public void setWx_share_description(String wx_share_description) {
        this.wx_share_description = wx_share_description;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public List<NewGood> getGoods() {
        return goods;
    }

    public void setGoods(List<NewGood> goods) {
        this.goods = goods;
    }

    public String getNew_link() {
        return new_link;
    }

    public void setNew_link(String new_link) {
        this.new_link = new_link;
    }

    public PackageInfo getPackage_info() {
        return package_info;
    }

    public void setPackage_info(PackageInfo package_info) {
        this.package_info = package_info;
    }

    /**
     * original_price : 21.6
     * img_url : http://coffees.qiniu.jijiakafei.com/package-1514860428911.jpg
     * price : 12.5
     * startTime : 2018-01-02 10:33:15
     * endTime : 2018-01-05 22:33:15
     * title : 套餐3
     */
   public static class PackageInfo implements Serializable{
       private float original_price;
       private String img_url;
       private float price;
       private String startTime;
       private String endTime;
       private String title;
       private ArrayList<Coupon> redeem_list;
       public float getOriginal_price() { return original_price;}

       public void setOriginal_price(float original_price) { this.original_price = original_price;}

       public String getImg_url() { return img_url;}

       public void setImg_url(String img_url) { this.img_url = img_url;}

       public float getPrice() { return price;}

       public void setPrice(float price) { this.price = price;}

       public String getStartTime() { return startTime;}

       public void setStartTime(String startTime) { this.startTime = startTime;}

       public String getEndTime() { return endTime;}

       public void setEndTime(String endTime) { this.endTime = endTime;}

       public String getTitle() { return title;}

       public void setTitle(String title) { this.title = title;}

        public ArrayList<Coupon> getRedeem_list() {
            return redeem_list;
        }

        public void setRedeem_list(ArrayList<Coupon> redeem_list) {
            this.redeem_list = redeem_list;
        }


    }
}
