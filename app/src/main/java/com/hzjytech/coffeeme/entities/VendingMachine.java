package com.hzjytech.coffeeme.entities;

/**
 * Created by Hades on 2016/3/16.
 */
public class VendingMachine {


    /**
     * id : 1
     * created_at : 2016-03-12T00:05:09.000+08:00
     * updated_at : 2016-03-16T22:58:18.000+08:00
     * address : 江干区白杨街道杭州市高科技企业孵化园区
     * attribute_group_id : 6
     * beans_weight : 8.5
     * begin_time : 2016-03-11T08:00:00.000+08:00
     * brand : 极伽
     * cap_caliber : 80
     * city : 杭州市
     * country : 中国
     * latitude : 30.303411
     * link_status : 
     * longitude : 120.371693
     * mac_address : 08:00:27:04:99:cf
     * magazine_num : 4
     * name : test0001
     * note : test
     * password : 47fc046e9e223040fe7a98cc23f1edce
     * phone : 17767181630
     * province : 浙江省
     * region : 江干区
     * server_address : 121.40.70.171
     * status : 
     * group_id : null
     * vm_types_id : 4
     */

    private int id;
    private String created_at;
    private String updated_at;
    private String address;
    private int attribute_group_id;
    private double beans_weight;
    private String begin_time;
    private String brand;
    private int cap_caliber;
    private String city;
    private String country;
    private String latitude;
    private String link_status;
    private String longitude;
    private String mac_address;
    private int magazine_num;
    private String name;
    private String note;
    private String password;
    private String phone;
    private String province;
    private String region;
    private String server_address;
    private String status;
    private Object group_id;
    private int vm_types_id;

    public void setId(int id) {
        this.id = id;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setAttribute_group_id(int attribute_group_id) {
        this.attribute_group_id = attribute_group_id;
    }

    public void setBeans_weight(double beans_weight) {
        this.beans_weight = beans_weight;
    }

    public void setBegin_time(String begin_time) {
        this.begin_time = begin_time;
    }

    public void setBrand(String brand) {
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

    public void setMac_address(String mac_address) {
        this.mac_address = mac_address;
    }

    public void setMagazine_num(int magazine_num) {
        this.magazine_num = magazine_num;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public void setPassword(String password) {
        this.password = password;
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

    public void setServer_address(String server_address) {
        this.server_address = server_address;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setGroup_id(Object group_id) {
        this.group_id = group_id;
    }

    public void setVm_types_id(int vm_types_id) {
        this.vm_types_id = vm_types_id;
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

    public String getAddress() {
        return address;
    }

    public int getAttribute_group_id() {
        return attribute_group_id;
    }

    public double getBeans_weight() {
        return beans_weight;
    }

    public String getBegin_time() {
        return begin_time;
    }

    public String getBrand() {
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

    public String getMac_address() {
        return mac_address;
    }

    public int getMagazine_num() {
        return magazine_num;
    }

    public String getName() {
        return name;
    }

    public String getNote() {
        return note;
    }

    public String getPassword() {
        return password;
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

    public String getServer_address() {
        return server_address;
    }

    public String getStatus() {
        return status;
    }

    public Object getGroup_id() {
        return group_id;
    }

    public int getVm_types_id() {
        return vm_types_id;
    }
}
