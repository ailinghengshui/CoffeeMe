package com.hzjytech.coffeeme.entities;

import java.io.Serializable;

/**
 * Created by Hades on 2016/2/29.
 */
public class Place implements Serializable {


    /**
     * id : 1
     * address : 江干区下沙街道6号大街孵化器
     * beans_weight : 8
     * begin_time : null
     * brand : null
     * cap_caliber : 80
     * city : 杭州市
     * country : 中国
     * latitude : 30.302832
     * link_status :  
     * longitude : 120.3714
     * magazine_num : 5
     * name : jj05710001
     * note : null
     * phone : 17767181630
     * province : 浙江省
     * region : 江干区
     * status :  
     * attribute_group_id : 1
     * vm_types_id : 1
     * group_id : null
     */

    private int id;
    private String address;
    private int beans_weight;
    private Object begin_time;
    private Object brand;
    private int cap_caliber;
    private String city;
    private String country;
    private String latitude;
    private String link_status;
    private String longitude;
    private int magazine_num;
    private String name;
    private Object note;
    private String phone;
    private String province;
    private String region;
    private String status;
    private int attribute_group_id;
    private int vm_types_id;
    private Object group_id;

    public void setId(int id) {
        this.id = id;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setBeans_weight(int beans_weight) {
        this.beans_weight = beans_weight;
    }

    public void setBegin_time(Object begin_time) {
        this.begin_time = begin_time;
    }

    public void setBrand(Object brand) {
        this.brand = brand;
    }

    public void setCap_caliber(int cap_caliber) {
        this.cap_caliber = cap_caliber;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public void setLink_status(String link_status) {
        this.link_status = link_status;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public void setMagazine_num(int magazine_num) {
        this.magazine_num = magazine_num;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setNote(Object note) {
        this.note = note;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setAttribute_group_id(int attribute_group_id) {
        this.attribute_group_id = attribute_group_id;
    }

    public void setVm_types_id(int vm_types_id) {
        this.vm_types_id = vm_types_id;
    }

    public void setGroup_id(Object group_id) {
        this.group_id = group_id;
    }

    public int getId() {
        return id;
    }

    public String getAddress() {
        return address;
    }

    public int getBeans_weight() {
        return beans_weight;
    }

    public Object getBegin_time() {
        return begin_time;
    }

    public Object getBrand() {
        return brand;
    }

    public int getCap_caliber() {
        return cap_caliber;
    }

    public String getCity() {
        return city;
    }

    public String getCountry() {
        return country;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getLink_status() {
        return link_status;
    }

    public String getLongitude() {
        return longitude;
    }

    public int getMagazine_num() {
        return magazine_num;
    }

    public String getName() {
        return name;
    }

    public Object getNote() {
        return note;
    }

    public String getPhone() {
        return phone;
    }

    public String getProvince() {
        return province;
    }

    public String getRegion() {
        return region;
    }

    public String getStatus() {
        return status;
    }

    public int getAttribute_group_id() {
        return attribute_group_id;
    }

    public int getVm_types_id() {
        return vm_types_id;
    }

    public Object getGroup_id() {
        return group_id;
    }
}
