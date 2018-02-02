package com.hzjytech.coffeeme.entities;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by dblr4287 on 2016/5/13.
 */
public class preWxPayInfo  implements Serializable {
    private String appid;
    @SerializedName(value = "partnerid",alternate = "mch_id")
    private String partnerid;

    //private String packageValue;

    private String timestamp;
    @SerializedName(value = "prepayid",alternate = "prepay_id")
    private String prepayid;
    @SerializedName(value = "noncestr",alternate = "nonce_str")
    private String noncestr;

    private String sign;

    public void setAppid(String appid){
        this.appid = appid;
    }
    public String getAppid(){
        return this.appid;
    }
    public void setPartnerid(String partnerid){
        this.partnerid = partnerid;
    }
    public String getPartnerid(){
        return this.partnerid;
    }
/*    public void setPackageValue(String packageValue){
        this.packageValue = packageValue;
    }
    public String getPackageValue(){
        return this.packageValue;
    }*/
    public void setTimestamp(String timestamp){
        this.timestamp = timestamp;
    }
    public String getTimestamp(){
        return this.timestamp;
    }
    public void setPrepayid(String prepayid){
        this.prepayid = prepayid;
    }
    public String getPrepayid(){
        return this.prepayid;
    }
    public void setNoncestr(String noncestr){
        this.noncestr = noncestr;
    }
    public String getNoncestr(){
        return this.noncestr;
    }
    public void setSign(String sign){
        this.sign = sign;
    }
    public String getSign(){
        return this.sign;
    }

}
