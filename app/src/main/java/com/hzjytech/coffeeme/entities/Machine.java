package com.hzjytech.coffeeme.entities;

import java.io.Serializable;

/**
 * Created by dblr4287 on 2016/6/12.
 */
public class Machine implements Serializable {

    private int id;

    private String address;

    private String city;

    private String country;

    private String latitude;

    private String link_status;

    private String longitude;

    private String name;

    private String province;

    private String region;

    private String status;

    private String operation_status;

    private double linear_distance;

    public void setId(int id){
        this.id = id;
    }
    public int getId(){
        return this.id;
    }
    public void setAddress(String address){
        this.address = address;
    }
    public String getAddress(){
        return this.address;
    }
    public void setCity(String city){
        this.city = city;
    }
    public String getCity(){
        return this.city;
    }
    public void setCountry(String country){
        this.country = country;
    }
    public String getCountry(){
        return this.country;
    }
    public void setLatitude(String latitude){
        this.latitude = latitude;
    }
    public String getLatitude(){
        return this.latitude;
    }
    public void setLink_status(String link_status){
        this.link_status = link_status;
    }
    public String getLink_status(){
        return this.link_status;
    }
    public void setLongitude(String longitude){
        this.longitude = longitude;
    }
    public String getLongitude(){
        return this.longitude;
    }
    public void setName(String name){
        this.name = name;
    }
    public String getName(){
        return this.name;
    }
    public void setProvince(String province){
        this.province = province;
    }
    public String getProvince(){
        return this.province;
    }
    public void setRegion(String region){
        this.region = region;
    }
    public String getRegion(){
        return this.region;
    }
    public void setStatus(String status){
        this.status = status;
    }
    public String getStatus(){
        return this.status;
    }
    public void setOperation_status(String operation_status){
        this.operation_status = operation_status;
    }
    public String getOperation_status(){
        return this.operation_status;
    }
    public void setLinear_distance(double linear_distance){
        this.linear_distance = linear_distance;
    }
    public double getLinear_distance(){
        return this.linear_distance;
    }

}
